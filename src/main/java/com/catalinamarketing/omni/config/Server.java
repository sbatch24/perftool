package com.catalinamarketing.omni.config;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name="server")
public class Server {
	
	private int port;
	private String environment;
	private String simulationType;
	private String testPlanVersion;

	@XmlElementWrapper(name="simulations")
	@XmlElement(name="simulation")
	private List<Simulation> simulation;
	private Setup setup;
	
	
	public Server() {
		
	}
	
	public Server(int port, List<Simulation> simulations, Setup setup) {
		this.port = port;
		this.simulation = simulations;
		this.setup = setup;
	}
	
	public int getPort() {
		return port;
	}
	public void setPort(int port) {
		this.port = port;
	}
	public List<Simulation> getSimulation() {
		return simulation;
	}
	public void setSimulation(List<Simulation> simulation) {
		this.simulation = simulation;
	}

	public Setup getSetup() {
		return setup;
	}

	public void setSetup(Setup setup) {
		this.setup = setup;
	}

	public String getEnvironment() {
		return environment;
	}

	public void setEnvironment(String environment) {
		this.environment = environment;
	}

	public String getSimulationType() {
		return simulationType;
	}

	public void setSimulationType(String simulationType) {
		this.simulationType = simulationType;
	}

	public String getTestPlanVersion() {
		return testPlanVersion;
	}

	public void setTestPlanVersion(String testPlanVersion) {
		this.testPlanVersion = testPlanVersion;
	}
}
