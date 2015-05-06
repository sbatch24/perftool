package com.catalinamarketing.omni.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.math.BigInteger;
import java.net.ServerSocket;
import java.net.Socket;
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
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;

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
import com.catalinamarketing.omni.pmr.setup.PmrSetupMessage;
import com.catalinamarketing.omni.pmr.setup.ProgramInfo;
import com.catalinamarketing.omni.protocol.message.HandShakeMsg;
import com.catalinamarketing.omni.protocol.message.StandByPeriodExpired;
import com.catalinamarketing.omni.protocol.message.TestPlanMsg;
import com.catalinamarketing.omni.util.ChannelTypeTranslator;
import com.google.common.base.Stopwatch;
import com.google.gson.Gson;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

/**
 * Control server is a class that manages setup for the performance tool and
 * assigns work to different nodes in the network.
 * 
 * <ul>
 * <li>Configuration for functioning of the control server is read from a
 * configuration file</li>
 * </ul>
 * 
 * @author achavan
 * @version 1.0
 */
public class ControlServer {

	final static Logger logger = LoggerFactory.getLogger(ControlServer.class);
	private final Config config;
	private List<PmrSetupMessage> pmrSetupMessageList;
	private Map<String, List<Wallet>> customerWallet;
	private ChannelFuture serverChannelFuture;
	private List<ClientCommunicationHandler> clientCommunicationHandlerList;
	private TestPlanDispatcherThread testPlanDispatcherThread;
	private ServerSocket serverSocket;
	
	
	
	public ControlServer(Config configuration) {
		this.clientCommunicationHandlerList = new ArrayList<ClientCommunicationHandler>();
		this.config = configuration;
	}
	
	public String standbyStartTime() {
		return testPlanDispatcherThread.getStartPollDateTime();
	}
	
	public List<ClientCommunicationHandler> getClientCommunicationHandlerList() {
		return clientCommunicationHandlerList;
	}
	
	/**
	 * Returns the number of available machines to execute the test plan for this server.
	 * @return int
	 */
	public int availableExecutorCount() {
		return this.clientCommunicationHandlerList.size();
	}
	
	/**
	 * Shutdown socket and exit
	 */
	public void shutdown() {
		try {
			serverSocket.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void runServer() {
		try {
			dataSetup();
			testPlanDispatcherThread = new TestPlanDispatcherThread(config, this); 
			new Thread(testPlanDispatcherThread).start();
			serverSocket = new ServerSocket(config.getServer().getPort());
	        try {
	            while (true) {
	            	System.out.println("Listening on port "+ config.getServer().getPort());
	                Socket socket = serverSocket.accept();
	                System.out.println("Got connection request from " + socket.getRemoteSocketAddress());
	                if(testPlanDispatcherThread.hasStandbyPeriodExpired()) {
	                	rejectPostPollConnection(socket);
	                }else {
	                	ClientCommunicationHandler handler = new ClientCommunicationHandler(socket, this, config);
		                clientCommunicationHandlerList.add(handler);
		                new Thread(handler).start();	
	                }
	            }
	        }
	        finally {
	            serverSocket.close();
	        }
		}catch(Exception ex) {
			logger.error("Problem occured in server. Error: " + ex.getMessage());
		}
	}
	
	/**
	 * 
	 * @param socket
	 */
	private void rejectPostPollConnection(Socket socket) {
		try {
			logger.info("Rejecting client connection from " + socket.getRemoteSocketAddress() + " since standby period has expired");
			StandByPeriodExpired msg = new StandByPeriodExpired();
			msg.setStartPollDateTime(testPlanDispatcherThread.getStartPollDateTime());
			msg.setEndPollDateTime(testPlanDispatcherThread.getEndPollDateTime());
			msg.setUserName(System.getProperty("user.name"));
			StringWriter writer = new StringWriter();
			JAXBContext context = JAXBContext.newInstance(StandByPeriodExpired.class);
			Marshaller marshaller = context.createMarshaller();
			marshaller.marshal(msg, writer);
			PrintWriter pWriter  = new PrintWriter(socket.getOutputStream(), true);
			pWriter.println(writer.toString());
			pWriter.flush();
			Thread.sleep(50);
			socket.close();
		}catch(Exception ex) {
			logger.error("Error while rejecting post poll client connections. Error: "+ ex.getMessage() );
		}
	}
	
	/**
	 * Spin up the server waiting to accept incoming connections.
	 */
	public void run() {
		
		dataSetup();
		new Thread(new TestPlanDispatcherThread(config, this)).run();
		EventLoopGroup bossGroup = new NioEventLoopGroup(1);
		EventLoopGroup workerGroup = new NioEventLoopGroup();
		try {
			ServerBootstrap b = new ServerBootstrap();
			b.group(bossGroup, workerGroup)
					.channel(NioServerSocketChannel.class)
					.childHandler(new ControlServerIntializer());
			//Wait till the bind happens.
			serverChannelFuture = b.bind(config.getServer().getPort()).sync();
			BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
            for (;;) {
                String line = in.readLine();
                if (line == null) {
                    break;
                }
                if ("exit".equals(line.toLowerCase())) {
                	bossGroup.shutdownGracefully();
                	workerGroup.shutdownGracefully();
                	serverChannelFuture.channel().close();
                	break;
                }
            }
			serverChannelFuture.channel().closeFuture().sync();
		} catch (Exception e) {
			logger.error("problem in perf tool server. Error : "
					+ e.getStackTrace());
		} finally {
			bossGroup.shutdownGracefully();
			workerGroup.shutdownGracefully();
		}
	}

	/**
	 * Setup capping data via pmr queue. Also setup consumer profiles and be
	 * ready for the test to be started. TODO 1. Create the PMR data hierachy
	 * based on the Promotion/Program setup in config.xml 2. Use the card setup
	 * data to create the consumer profiles.
	 */
	private void dataSetup() {
		logger.info("Data setup will involve publishing data to PMR and to the DMP");
		initializePmrDataSetup();
		publishPmrData();
		initializeDmpData();
		publishDmpData();
		logger.info("Finished publishing data. Server will go in Standby mode("+ config.getServer().getStandby()+" seconds) for clients to connect.");
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
				for (PmrSetupMessage message : this.pmrSetupMessageList) {
					String jsonMessage = gson.toJson(message);
					channel.basicPublish("", environment.getQueueInfo()
							.getQueueName(), new AMQP.BasicProperties.Builder()
							.contentType("application/json").headers(headers)
							.build(), jsonMessage.getBytes());
				}
				channel.close();
				connection.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				logger.error("Problem publish pmr data. Error :"
						+ e.getMessage());
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
		pmrSetupMessageList = new ArrayList<PmrSetupMessage>();
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
				pmrSetupMessageList.add(pmrSetupMessage);
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

	public Config getConfig() {
		return config;
	}

	public Map<String, List<Wallet>> getCustomerWallet() {
		return customerWallet;
	}

	public void setCustomerWallet(Map<String, List<Wallet>> customerWallet) {
		this.customerWallet = customerWallet;
	}

	public ChannelFuture getServerChannelFuture() {
		return serverChannelFuture;
	}

	public void setServerChannelFuture(ChannelFuture serverChannelFuture) {
		this.serverChannelFuture = serverChannelFuture;
	}
}
