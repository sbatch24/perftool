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
import com.catalinamarketing.omni.config.OfferSetup;
import com.catalinamarketing.omni.config.PromotionSetup;
import com.catalinamarketing.omni.dmp.setup.Wallet;
import com.catalinamarketing.omni.message.DataSetupActivityLog;
import com.catalinamarketing.omni.pmr.setup.CampaignOfferSetupMessage;
import com.catalinamarketing.omni.pmr.setup.DynamicControlsMessage;
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
	public void dataSetup() {
		logger.info("Data setup will involve publishing data to PMR and to the DMP");
		PmrDataOrganizer pmrDataOrganizer = new PmrDataOrganizer(config, activityLog);
		pmrDataOrganizer.initializePmrDataSetup();
		publishPmrSetupData(pmrDataOrganizer);
		publishDynamicControlData(pmrDataOrganizer);
		publishStringPrintData(pmrDataOrganizer);
		initializeDmpData();
	}

	/**
	 * Method responsible for publishing String Print data to the PMR queue.
	 * @param pmrDataOrganizer
	 */
	private void publishStringPrintData(PmrDataOrganizer pmrDataOrganizer) {
		logger.info("Publishing String print  data to the PMR in  " + config.getConfiguredEnvironmentName());
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
			channel.queueDeclare(environment.getQueueInfo().getStringPrintQueueName(),
					true, false, false, null);
			Map<String, Object> headers = new HashMap<String, Object>();
			headers.put("__TypeId__", "campaign-offers-setup");
			Gson gson = new Gson();
			CampaignOfferSetupMessage campaignSetupMessage = pmrDataOrganizer.getCampaignMessage();
			String jsonMessage = gson.toJson(campaignSetupMessage);
			channel.basicPublish("", environment.getQueueInfo()
					.getStringPrintQueueName(), new AMQP.BasicProperties.Builder()
					.contentType("application/json").headers(headers)
					.build(), jsonMessage.getBytes());
		} catch (IOException e) {
			logger.error("Problem publish string print data. Error :"+ e.getMessage());
			activityLog.addException("Problem publishing string print data. Error :"+ e.getMessage());
		} finally {
			try {
				if(channel != null && connection != null) {
					channel.close();
					connection.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}		
	}

	/**
	 * Method responsible for publishing dynamic control information to the PMR queue.
	 * @param pmrDataOrganizer
	 */
	private void publishDynamicControlData(PmrDataOrganizer pmrDataOrganizer) {
		logger.info("Publishing dynamic control data to the PMR in  " + config.getConfiguredEnvironmentName());
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
			channel.queueDeclare(environment.getQueueInfo().getDcQueueName(),
					true, false, false, null);
			Map<String, Object> headers = new HashMap<String, Object>();
			headers.put("__TypeId__", "dynamic-controls-setup");
			Gson gson = new Gson();
			DynamicControlsMessage dcMessage = pmrDataOrganizer.getDynamicControlMessage();
			String jsonMessage = gson.toJson(dcMessage);
			channel.basicPublish("", environment.getQueueInfo()
					.getDcQueueName(), new AMQP.BasicProperties.Builder()
					.contentType("application/json").headers(headers)
					.build(), jsonMessage.getBytes());
		} catch (IOException e) {
			logger.error("Problem publishing dynamic controls data. Error :"+ e.getMessage());
			activityLog.addException("Problem publishing dynamic controls. Error :"+ e.getMessage());
		} finally {
			try {
				if(channel != null && connection != null) {
					channel.close();
					connection.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}				
	}

	/**
	 * Publish data to the consumer dmp.
	 * @param entry
	 * @param client
	 * @param feature
	 */
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
			List<PromotionSetup> promotionSetupList = config.getPromotionSetupByCardRangeId(cardSetup.getCardRangeId());
			for(PromotionSetup promoSetup : promotionSetupList) {
				List<Wallet> walletList = prepareWalletForId(promoSetup, config.getNetworkId());
				BigInteger firstId = new BigInteger(cardSetup.cardRange().get(0));
				BigInteger lastId = new BigInteger(cardSetup.cardRange().get(1));
				for (; firstId.compareTo(lastId) <= 0; firstId = firstId.add(BigInteger.ONE)) {
					customerWallet.put("USA-"
							+ config.getNetworkId() + "-" + firstId, walletList);
				}
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
		if(promotionSetup.isHistoricalPrint()) {
			Wallet wallet = new Wallet();
			wallet.setAdgroup_id(promotionSetup.getAwardId().toString());
			wallet.setChannel_type(ChannelTypeTranslator
					.getChannelType(promotionSetup.getChannelType()));
			wallet.setId(promotionSetup.getAwardId().toString());
			wallet.setLimit("" + promotionSetup.getConsumerCap());
			wallet.setSystem_id("MXP");
			wallet.setType(promotionSetup.getPromotionTypeForDmp());
			wallet.setNetwork_id(networkId);
			wallet.setCampaign_id(promotionSetup.getCampaignId());
			walletList.add(wallet);	
			if(promotionSetup.isStringPrint()) {
				OfferSetup offerSetup = config.getOfferSetupByCampaignId(promotionSetup.getCampaignId());
				List<Wallet> offerWalletList = createOfferPromotion(promotionSetup, offerSetup, networkId);
				for(Wallet offerWallet : offerWalletList) {
					walletList.add(offerWallet);
				}
				
			}
		}
		
		return walletList;
	}
	
	/**
	 * 
	 * @param promotionSetup
	 * @param offerSetup
	 * @param networkId 
	 * @return
	 */
	private List<Wallet> createOfferPromotion(PromotionSetup promotionSetup,
			OfferSetup offerSetup, String networkId) {
		List<Wallet> offerList = new ArrayList<Wallet>();
		for(String offerId : offerSetup.getOfferList()) {
			Wallet wallet = new Wallet();
			wallet.setAdgroup_id(promotionSetup.getAwardId().toString());
			wallet.setChannel_type(ChannelTypeTranslator
					.getChannelType(promotionSetup.getChannelType()));
			wallet.setId(offerId);
			wallet.setLimit("" + promotionSetup.getConsumerCap());
			wallet.setType(promotionSetup.getPromotionTypeForDmp());
			wallet.setSystem_id("MXP");
			wallet.setNetwork_id(networkId);
			wallet.setCampaign_id(promotionSetup.getCampaignId());
			offerList.add(wallet);
		}
		return offerList;
	}

	/**
	 * Publish the media data to the pmr.
	 */
	private void publishPmrSetupData(PmrDataOrganizer pmrDataOrganizer) {
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
				channel.queueDeclare(environment.getQueueInfo().getSetupQueueName(),
						true, false, false, null);
				Map<String, Object> headers = new HashMap<String, Object>();
				headers.put("__TypeId__", "offer-setup");
				Gson gson = new Gson();
				List<PmrSetupMessage> pmrMessageList = pmrDataOrganizer.getPmrSetupMessageList();
				for (PmrSetupMessage message : pmrMessageList) {
					String jsonMessage = gson.toJson(message);
					channel.basicPublish("", environment.getQueueInfo()
							.getSetupQueueName(), new AMQP.BasicProperties.Builder()
							.contentType("application/json").headers(headers)
							.build(), jsonMessage.getBytes());
				}
			} catch (IOException e) {
				logger.error("Problem publish pmr data. Error :"+ e.getMessage());
				activityLog.addException("Problem publish pmr data. Error :"+ e.getMessage());
			} finally {
				try {
					if(channel != null && connection != null) {
						channel.close();
						connection.close();
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
