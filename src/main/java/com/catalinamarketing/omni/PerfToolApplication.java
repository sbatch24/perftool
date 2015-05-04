package com.catalinamarketing.omni;


import java.io.FileReader;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
	
	/**
	 * Entry point for the program.
	 * @param args
	 */
	public static void main(String args[]) {
		//PerftoolApplication tool = new PerftoolApplication();
		//tool.printsetupmessage();
		List<BigInteger> range = new ArrayList<BigInteger>();
		range.add(new BigInteger("7081329999552803"));
		range.add(new BigInteger("7081329999552805"));
		if(args.length == 1 && args[0].contains("server")) {
			try {
				//System.out.println(System.getProperty("user.name"));
				JAXBContext context = JAXBContext.newInstance(Config.class);
				Unmarshaller um = context.createUnmarshaller();
			    Config config = (Config) um.unmarshal(new FileReader("config.xml"));
			    ControlServer cs = new ControlServer(config);
				cs.run();
			}catch(Exception e) {
				e.printStackTrace();
			}
				
		} else {
			// TODO run the client program.
			try {
				JAXBContext context = JAXBContext.newInstance(Config.class);
				Unmarshaller um = context.createUnmarshaller();
			    Config config = (Config) um.unmarshal(new FileReader("configc.xml"));
			    PerfToolClient client = new PerfToolClient();
			    client.launch(config.getClient().getRemoteHost(), config.getClient().getPort());
			}catch(Exception e) {
				e.printStackTrace();
			}
		}
	}
}
