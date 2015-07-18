package com.catalinamarketing.omni;


import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;

import com.catalinamarketing.omni.client.PerfToolClient;
import com.catalinamarketing.omni.config.Config;
import com.catalinamarketing.omni.pmr.setup.AwardInfo;
import com.catalinamarketing.omni.pmr.setup.ChannelMediaInfo;
import com.catalinamarketing.omni.pmr.setup.MediaInfo;
import com.catalinamarketing.omni.pmr.setup.PmrSetupMessage;
import com.catalinamarketing.omni.pmr.setup.ProgramInfo;
import com.catalinamarketing.omni.server.ControlServer;
import com.google.gson.Gson;

/**
 * <h1>Performance tool for testing Pin,Targeting functionality in omni.
 * 
 * @author achavan
 *
 */
public class PerfToolApplication  {
	
	final static Logger logger = LoggerFactory.getLogger(PerfToolApplication.class);
	private static  ControlServer controlServer;

	public static ControlServer getControlServer() {
		return controlServer;
	}
	
	public void printsetupmessage() {
		Gson gson = new Gson();
		PmrSetupMessage msg = new PmrSetupMessage();
		List<ProgramInfo> programSetupList = new ArrayList<>();
		ProgramInfo programSetup = new ProgramInfo();
		programSetup.setProgramID("AC-1234");
		programSetup.setContractID("AC-4567");
		programSetup.setCap(2000);
		programSetup.setVariance(2);
		programSetupList.add(programSetup);
		msg.setPrograms(programSetupList);

		MediaInfo mediaSetup = new MediaInfo();
		mediaSetup.setCap(12);
		mediaSetup.setVariance(2);
		mediaSetup.setMediaID("1212121");
		
		ChannelMediaInfo chMediaSetup = new ChannelMediaInfo();
		chMediaSetup.setChannelMediaID("99999");
		chMediaSetup.setCap(20);
		chMediaSetup.setVariance(0);
		chMediaSetup.setChannelType("CATALINA_IN_STORE");
		chMediaSetup.setEndDate("2015-12-28T00:00:00-05:00");
		chMediaSetup.setStartDate("2016-12-28T00:00:00-05:00");
		List<ChannelMediaInfo> chList = new ArrayList<>();
		chList.add(chMediaSetup);
		mediaSetup.setChannels(chList);
		List<MediaInfo> mediaSetupList = new ArrayList<MediaInfo>();
		mediaSetupList.add(mediaSetup);
		AwardInfo awardSetup = new AwardInfo();
		awardSetup.setAwardID("AC-885477");
		awardSetup.setCap(12);
		awardSetup.setVariance(0);
		awardSetup.setMediaList(mediaSetupList);
		List<AwardInfo> awards = new ArrayList<>();
		awards.add(awardSetup);
		programSetup.setAwards(awards);
		String json = gson.toJson(msg);
		System.out.println(json);
	}
	
	public static int isPrime(int A) {
		int upperLimit = (int)(Math.sqrt(A));
		for (int i = 2; i < upperLimit; i++) {
			if (i < A && A % i == 0) return 0;
		}
		return 1;
	}
	
	/**
	 * Entry point for the program.
	 * @param args
	 */
	public static void main(String args[]) {
		
		System.out.println(PerfToolApplication.isPrime(22));
		/*MetricRegistry metrics = new MetricRegistry();
		final ConsoleReporter reporter = ConsoleReporter.forRegistry(metrics)
                .convertRatesTo(TimeUnit.MILLISECONDS)
                .convertDurationsTo(TimeUnit.SECONDS)
                .build();
		
		//reporter.start(1, TimeUnit.MINUTES);
		Timer TEST = metrics.timer("GetTargetedMedia");
		Histogram hist = metrics.histogram("Something");
		Random random = new Random();
		for(int i=0; i < 100; i++) {
			try {
				Timer.Context eventContext = TEST.time();
				int t = random.nextInt(1000);
				//System.out.println(t);
				Thread.sleep(t);
				long tt = eventContext.stop();
				hist.update(tt);
			} catch (InterruptedException e) {
			}
		}
		
		reporter.forRegistry(metrics).outputTo(System.out);
		reporter.report();*/
		
		if(args.length >=1 && args[0].contains("server")) {
			try {
				JAXBContext context = JAXBContext.newInstance(Config.class);
				Unmarshaller um = context.createUnmarshaller();
				SpringApplication.run(HttpServerLauncher.class, args);
			    Config config = (Config) um.unmarshal(new FileReader("config.xml"));
			    controlServer = new ControlServer(config);
				controlServer.runServer(config.getServerPort());
			}catch(Exception e) {
				e.printStackTrace();
			}
				
		} else {
			try {
				JAXBContext context = JAXBContext.newInstance(Config.class);
				Unmarshaller um = context.createUnmarshaller();
			    Config config = (Config) um.unmarshal(new FileReader("configc.xml"));
			    PerfToolClient client = new PerfToolClient();
			    client.launchClient(config.getClient().getRemoteHost(), config.getClient().getPort());
			}catch(Exception e) {
				e.printStackTrace();
			}
		}
	}
}
