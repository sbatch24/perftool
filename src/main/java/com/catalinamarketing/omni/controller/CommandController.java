package com.catalinamarketing.omni.controller;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.catalinamarketing.omni.config.Config;
import com.google.gson.Gson;

@RestController
@RequestMapping("/")
public class CommandController {
	final static Logger logger = LoggerFactory.getLogger(CommandController.class);

	@RequestMapping(method = RequestMethod.GET, value="/config")
	public @ResponseBody String getConfig() {
		logger.info("Got call from client for config");
		Config config = null;
		try {
			JAXBContext context = JAXBContext.newInstance(Config.class);
			Unmarshaller um = context.createUnmarshaller();
		    config = (Config) um.unmarshal(new FileReader("config.xml"));
		} catch(Exception ex) {
			
		}
		String response = "{}";
		if(config != null) {
			Gson gson = new Gson();
			response = gson.toJson(config);
		}
		return response;
	}
	
	@RequestMapping(value = "/update",
	        method = RequestMethod.POST)
	public ResponseEntity<String>  updateConfig(@RequestBody Config config) {
		try {
			JAXBContext context = JAXBContext.newInstance(Config.class);
			Marshaller um = context.createMarshaller();
		    um.marshal(config,new FileWriter(new File("configt.xml")));
		    logger.info("Configuration updated");
		}catch(Exception ex) {
			logger.error("Problem occured during update of configuration. Error : " + ex.getMessage());
		}
		return new ResponseEntity<String>("{\"status\":\"Configuration updated\"}", HttpStatus.OK);
	}
}
