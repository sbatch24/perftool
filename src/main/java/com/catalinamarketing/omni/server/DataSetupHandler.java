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
import com.catalinamarketing.omni.config.ProgramSetup;
import com.catalinamarketing.omni.config.PromotionSetup;
import com.catalinamarketing.omni.dmp.setup.Wallet;
import com.catalinamarketing.omni.pmr.setup.AwardInfo;
import com.catalinamarketing.omni.pmr.setup.ChannelMediaInfo;
import com.catalinamarketing.omni.pmr.setup.MediaInfo;
import com.catalinamarketing.omni.pmr.setup.PmrDataOrganizer;
import com.catalinamarketing.omni.pmr.setup.PmrSetupMessage;
import com.catalinamarketing.omni.pmr.setup.ProgramInfo;
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
	private PmrDataOrganizer pmrDataOrganizer;
	private Map<String, List<Wallet>> customerWallet;

	
	public DataSetupHandler(Config config) {
		this.config = config;
	}
	
	/**
	 * Setup capping data via pmr queue. Also setup consumer profiles and be
	 * ready for the test to be started. TODO 1. Create the PMR data hierachy
	 * based on the Promotion/Program setup in config.xml 2. Use the card setup
	 * data to create the consumer profiles.
	 */
	public void dataSetup() {
		logger.info("Data setup will involve publishing data to PMR and to the DMP");
		initializePmrDataSetup();
		publishPmrData();
		initializeDmpData();
		publishDmpData();
		logger.info("Finished publishing data");
	}

	/**
	 * Publish wallet information to the dmp
	 */
	private void publishDmpData() {
		if (config.getServer().isSetupData()) {
			logger.info("Publishing data to the consumer DMP");
			Gson gson = new Gson();
			try {
				HttpAuthenticationFeature feature = HttpAuthenticationFeature.basic(config.getDmpUserName(), config.getDmpPassword());
				Stopwatch watch =  Stopwatch.createStarted();
				for(Map.Entry<String, List<Wallet>> entry : customerWallet.entrySet()){
					Client client = ClientBuilder.newClient();
					WebTarget target = client
							.target(dmpWalletUrl(entry.getKey()));
					Response resp = target.register(feature).request()
							.accept(MediaType.APPLICATION_JSON)
							.header("Content-Type", MediaType.APPLICATION_JSON).post(Entity.entity(gson.toJson(entry.getValue()), MediaType.APPLICATION_JSON));	
					if(resp.getStatusInfo().getStatusCode() != Response.Status.OK.getStatusCode()) {
						logger.error("Problem post wallet data for CID " + entry.getKey() + " Error code : "+ resp.getStatus());
					}
					resp.close();
					client.close();
					Thread.sleep(10);
				}
				logger.info("Time taken to publish wallet information for " +  customerWallet.size()+" ids is "+ watch.elapsed(TimeUnit.SECONDS) + " seconds");
				
			}catch(Exception ex) {
				logger.error("Problem post wallet data. Error " + ex.getMessage());
				ex.printStackTrace();
			}
		} else{
			logger.info("Server configured to not publish data to the consumer DMP");
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
		return String.format(environment.getDmpConfig().getDmpUrl(),
				tokenizedId);
	}

	/**
	 * Initialize consumer profile data.
	 */
	private void initializeDmpData() {
		logger.info("Initializing profile data");
		customerWallet = new HashMap<String, List<Wallet>>();
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
	}

	/**
	 * Prepare wallet based on promotionSetup information provided.
	 * 
	 * @param promotionSetup
	 * @return
	 */
	private List<Wallet> prepareWalletForId(PromotionSetup promotionSetup,
			String networkId) {
		List<Integer> awardRange = promotionSetup.awardRange();
		List<Wallet> walletList = new ArrayList<Wallet>();
		for (int awardNumber : awardRange) {
			Wallet wallet = new Wallet();
			wallet.setAdgroup_id("" + awardNumber);
			wallet.setChannel_type(ChannelTypeTranslator
					.getChannelType(promotionSetup.getChannelType()));
			wallet.setId("" + awardNumber);
			wallet.setLimit("" + promotionSetup.getConsumerCap());
			wallet.setSystem_id("MXP");
			wallet.setType("direct");
			wallet.setNetwork_id(networkId);
			walletList.add(wallet);
		}
		return walletList;
	}

	/**
	 * Publish the media data to the pmr.
	 */
	private void publishPmrData() {
		if (config.getServer().isSetupData()) {
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
				channel.close();
				connection.close();
			} catch (IOException e) {
				logger.error("Problem publish pmr data. Error :"+ e.getMessage());
				e.printStackTrace();
			} finally {
				try {
					channel.close();
					connection.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		} else {
			logger.info("Server configured to not publish data to the PMR");
		}
	}

	/**
	 * Initialize PMR data
	 */
	private void initializePmrDataSetup() {
		logger.info("Initializing PMR data");
	//	pmrSetupMessageList = new ArrayList<PmrSetupMessage>();
		pmrDataOrganizer = new PmrDataOrganizer();
		List<PromotionSetup> promotionSetupList = config.getServer().getSetup()
				.getPromotionSetup();
		for (PromotionSetup promoSetup : promotionSetupList) {
			ProgramSetup programSetup = config.getProgramSetup(promoSetup
					.getBillNo());
			if (programSetup != null) {
				PmrSetupMessage pmrSetupMessage = new PmrSetupMessage();
				pmrSetupMessage.setLocale("US");
				pmrSetupMessage.setSetupSystemID("MXP");
				ProgramInfo programSetupMessage = new ProgramInfo();
				programSetupMessage.setProgramID(programSetup.getProgramId());
				programSetupMessage.setContractID(programSetup.getContractId());
				programSetupMessage.setCap(programSetup.getCap());
				programSetupMessage.setVariance(programSetup.getVariance());

				List<AwardInfo> awards = getAwardSetupList(promoSetup);
				programSetupMessage.setAwards(awards);
				pmrSetupMessage.addProgram(programSetupMessage);
				pmrDataOrganizer.addPmrSetupMessage(pmrSetupMessage);
			} else {
				logger.error("PromotionSetup should always contain a bill No. Check config.xml file for promotionSetup - "
						+ promoSetup.getAwardRange());
			}
		}
	}

	public List<AwardInfo> getAwardSetupList(PromotionSetup promotionSetup) {
		List<Integer> awardRange = promotionSetup.awardRange();
		List<AwardInfo> awardSetupList = new ArrayList<AwardInfo>();
		List<Integer> mediaRange = promotionSetup.mediaIdRange();
		int index = 0;
		for (Integer awardId : awardRange) {
			AwardInfo awardSetup = new AwardInfo();
			awardSetup.setAwardID("" + awardId);
			awardSetup.setCap(promotionSetup.getCap());
			awardSetup.setVariance(promotionSetup.getVariance());
			MediaInfo mediaInfo = new MediaInfo();
			mediaInfo.setMediaID(mediaRange.get(index).toString());
			mediaInfo.setCap(promotionSetup.getMediaCap());
			mediaInfo.setVariance(promotionSetup.getMediaVariance());
			ChannelMediaInfo channelMediaInfo = new ChannelMediaInfo();
			// TODO we set the mediaid as channel media Id
			channelMediaInfo.setChannelMediaID(mediaInfo.getMediaID());
			channelMediaInfo.setChannelType(promotionSetup.getChannelType());
			channelMediaInfo.setStartDate(promotionSetup.getStartDate());
			mediaInfo.addChannelMedia(channelMediaInfo);
			awardSetup.addMedia(mediaInfo);
			awardSetupList.add(awardSetup);
			index++;
		}
		return awardSetupList;
	}
	
	public Map<String, List<Wallet>> getCustomerWallet() {
		return customerWallet;
	}

	public void setCustomerWallet(Map<String, List<Wallet>> customerWallet) {
		this.customerWallet = customerWallet;
	}

	
	
	public PmrDataOrganizer getPmrDataOrganizer() {
		return pmrDataOrganizer;
	}

	public void setPmrDataOrganizer(PmrDataOrganizer pmrDataOrganizer) {
		this.pmrDataOrganizer = pmrDataOrganizer;
	}

}
