package com.catalinamarketing.omni.server;

import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.glassfish.jersey.client.authentication.HttpAuthenticationFeature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.catalinamarketing.omni.config.CardSetup;
import com.catalinamarketing.omni.config.Config;
import com.catalinamarketing.omni.config.Environment;
import com.catalinamarketing.omni.config.PromotionSetup;
import com.catalinamarketing.omni.dmp.setup.Wallet;
import com.catalinamarketing.omni.message.DataSetupActivityLog;
import com.catalinamarketing.omni.pmr.setup.PmrDataOrganizer;
import com.catalinamarketing.omni.pmr.setup.PmrSetupMessage;
import com.catalinamarketing.omni.util.ChannelTypeTranslator;
import com.google.common.base.Stopwatch;
import com.google.gson.Gson;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

public class DataSetupHandler {
	final static Logger logger = LoggerFactory.getLogger(DataSetupHandler.class);
	private Config config;
	private boolean publish;
	private DataSetupActivityLog activityLog;
	
	public DataSetupHandler(Config config, boolean publish, DataSetupActivityLog activityLog) {
		this.config = config;
		this.publish = publish;
		this.activityLog = activityLog;
	}
	
	/**
	 * Setup capping data via pmr queue. Also setup consumer profiles and be
	 * ready for the test to be started. TODO 1. Create the PMR data hierachy
	 * based on the Promotion/Program setup in config.xml 2. Use the card setup
	 * data to create the consumer profiles.
	 */
	public DataSetupActivityLog dataSetup() {
		logger.info("Data setup will involve publishing data to PMR and to the DMP");
		PmrDataOrganizer pmrDataOrganizer = new PmrDataOrganizer(config, activityLog);
		pmrDataOrganizer.initializePmrDataSetup();
		publishPmrData(pmrDataOrganizer);
		initializeDmpData();
		return activityLog;
	}

	public void updateWalletDataForConsumer(Map.Entry<String, List<Wallet>> entry, Client client, HttpAuthenticationFeature feature) {
		Gson gson = new Gson();
		WebTarget target = client
				.target(dmpWalletUrl(entry.getKey()));
		Response resp = target.request()
				.accept(MediaType.APPLICATION_JSON)
				.header("Content-Type", MediaType.APPLICATION_JSON).post(Entity.entity(gson.toJson(entry.getValue()), MediaType.APPLICATION_JSON));	
		if(resp.getStatusInfo().getStatusCode() != Response.Status.OK.getStatusCode() && resp.getStatusInfo() != Response.Status.ACCEPTED) {
			logger.error("Problem post wallet data for CID " + entry.getKey() + " Error code : "+ resp.getStatus());
			activityLog.addException("Problem post wallet data for CID " + entry.getKey() + " Error code : "+ resp.getStatus());
		}
		resp.close();
	}
	
	/**
	 * Publish wallet information to the dmp
	 * @param customerWallet 
	 */
	private void publishDmpData(Map<String, List<Wallet>> customerWallet) {
		if (publish) {
			logger.info("Publishing data to the consumer DMP");
			Client client  = null;
			HttpAuthenticationFeature feature = HttpAuthenticationFeature.basic(config.getDmpUserName(), config.getDmpPassword());

			Stopwatch watch =  Stopwatch.createStarted();
			client = ClientBuilder.newBuilder().register(feature).build();
			for(Map.Entry<String, List<Wallet>> entry : customerWallet.entrySet()){
				try {
					updateWalletDataForConsumer(entry, client, feature);
					Thread.sleep(10);	
					logger.info("Publishing data for CID - " + entry.getKey());
				}catch(Exception ex) {
					
					logger.error("Problems posting wallet to profile(cid - " + entry.getKey() + " ). Error " + ex.getMessage());
					activityLog.addException("Problems posting wallet to profile(cid - " + entry.getKey() + " ). Error " + ex.getMessage());
					client.close();
					client = ClientBuilder.newBuilder().register(feature).build();
				}
			}
			logger.info("Time taken to publish wallet information for " +  customerWallet.size()+" ids is "+ watch.elapsed(TimeUnit.SECONDS) + " seconds");
			client.close();
		}
	}

	/**
	 * Format the dmp wallet end point
	 * 
	 * @param tokenizedId
	 *            (country code - network id - cid)
	 * @return
	 */
	public String dmpWalletUrl(String tokenizedId) {
		Environment environment = config.getConfiguredEnvironment();
		return String.format(environment.getDmpConfig().getDmpWalletUrl(),
				tokenizedId);
	}
	
	public void clearEventsFromProfile() {
		logger.info("Clearing events from profile data");
		List<CardSetup> cardSetupList = config.getCardSetupList();
		Client client  = null;
		HttpAuthenticationFeature feature = HttpAuthenticationFeature.basic(config.getDmpUserName(), config.getDmpPassword());
		client = ClientBuilder.newBuilder().register(feature).build();
		for (CardSetup cardSetup : cardSetupList) {
			BigInteger firstId = new BigInteger(cardSetup.cardRange().get(0));
			BigInteger lastId = new BigInteger(cardSetup.cardRange().get(1));
			for (; firstId.compareTo(lastId) <= 0; firstId = firstId.add(BigInteger.ONE)) {
				WebTarget target = client
						.target(String.format(config.getConfiguredEnvironment().getDmpConfig().getDmpProfileUrl(),
								"USA-"+ config.getNetworkId() + "-" + firstId)+"/reset");
				Response resp = target.request()
						.accept(MediaType.APPLICATION_JSON)
						.header("Content-Type", MediaType.APPLICATION_JSON).get();
				if(resp.getStatusInfo().getStatusCode() != Response.Status.OK.getStatusCode() && resp.getStatusInfo() != Response.Status.ACCEPTED) {
					logger.error("Problem clearing events from profile " + "USA-"
								+ config.getNetworkId() + "-" + firstId  + " http code : " + resp.getStatus());
					activityLog.addException("Problem clearing events from profile " + "USA-"
								+ config.getNetworkId() + "-" + firstId  + " http code : " + resp.getStatus());
				}
				resp.close();
			}
		}
	}

	/**
	 * Initialize consumer profile data.
	 */
	private void initializeDmpData() {
		logger.info("Initializing profile data");
		Map<String, List<Wallet>> customerWallet = new HashMap<String, List<Wallet>>();
		List<CardSetup> cardSetupList = config.getCardSetupList();
		for (CardSetup cardSetup : cardSetupList) {
			PromotionSetup promotionSetup = config
					.getPromotionSetupByCardRangeId(cardSetup.getCardRangeId());
			List<Wallet> walletList = prepareWalletForId(promotionSetup, config.getNetworkId());
			BigInteger firstId = new BigInteger(cardSetup.cardRange().get(0));
			BigInteger lastId = new BigInteger(cardSetup.cardRange().get(1));
			for (; firstId.compareTo(lastId) <= 0; firstId = firstId.add(BigInteger.ONE)) {
				customerWallet.put("USA-"
						+ config.getNetworkId() + "-" + firstId, walletList);
			}
		}
		
		publishDmpData(customerWallet);
	}

	/**
	 * Prepare wallet based on promotionSetup information provided.
	 * 
	 * @param promotionSetup
	 * @return
	 */
	private List<Wallet> prepareWalletForId(PromotionSetup promotionSetup,
			String networkId) {
		List<Wallet> walletList = new ArrayList<Wallet>();
		Wallet wallet = new Wallet();
		wallet.setAdgroup_id(promotionSetup.getAwardId().toString());
		wallet.setChannel_type(ChannelTypeTranslator
				.getChannelType(promotionSetup.getChannelType()));
		wallet.setId(promotionSetup.getAwardId().toString());
		wallet.setLimit("" + promotionSetup.getConsumerCap());
		wallet.setSystem_id("MXP");
		wallet.setType(promotionSetup.getPromotionTypeForDmp());
		wallet.setNetwork_id(networkId);
		walletList.add(wallet);
		return walletList;
	}

	/**
	 * Publish the media data to the pmr.
	 */
	private void publishPmrData(PmrDataOrganizer pmrDataOrganizer) {
		if (publish) {
			logger.info("Publishing PMR data to the " + config.getConfiguredEnvironmentName());
			Environment environment = config.getConfiguredEnvironment();
			ConnectionFactory factory = new ConnectionFactory();
			factory.setUsername(environment.getQueueInfo().getUserName());
			factory.setPassword(environment.getQueueInfo().getPassword());
			factory.setPort(environment.getQueueInfo().getPort());
			factory.setHost(environment.getQueueInfo().getHostName());
			Connection connection = null;
			Channel channel = null;
			try {
				connection = factory.newConnection();
				channel = connection.createChannel();
				channel.queueDeclare(environment.getQueueInfo().getQueueName(),
						true, false, false, null);
				Map<String, Object> headers = new HashMap<String, Object>();
				headers.put("__TypeId__", "offer-setup");
				Gson gson = new Gson();
				List<PmrSetupMessage> pmrMessageList = pmrDataOrganizer.getPmrSetupMessageList();
				for (PmrSetupMessage message : pmrMessageList) {
					String jsonMessage = gson.toJson(message);
					channel.basicPublish("", environment.getQueueInfo()
							.getQueueName(), new AMQP.BasicProperties.Builder()
							.contentType("application/json").headers(headers)
							.build(), jsonMessage.getBytes());
				}
			} catch (IOException e) {
				logger.error("Problem publish pmr data. Error :"+ e.getMessage());
				activityLog.addException("Problem publish pmr data. Error :"+ e.getMessage());
			} finally {
				try {
					channel.close();
					connection.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
