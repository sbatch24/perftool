package com.catalinamarketing.omni.config;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="config")
public class Config {
	@XmlElementWrapper(name="environments")
	@XmlElement(name="environment")
	private List<Environment> environment;
	private Client client;
	private Server server;

	public Config() {
		
	}
	
	public List<Environment> getEnvironments() {
		return environment;
	}
	
	/**
	 * Returns the server port number.
	 * @return port Number
	 */
	public int getServerPort() {
		return getServer().getPort();
	}

	/**
	 * Returns the qualified environment based on the env identifier passed in.
	 * @param env
	 * @return Environment
	 */
	public Environment getConfiguredEnvironment() {
		for(Environment environment : this.environment) {
			if(environment.getType().equalsIgnoreCase(getServer().getEnvironment())) {
				return environment;
			}
		}
		return null;
	}
	
	public Simulation getConfiguredSimulation() {
		for(Simulation simulation : getServer().getSimulation()) {
			if(simulation.getType().equalsIgnoreCase(getServer().getSimulationType())) {
				return simulation;
			}
		}
		return null;
	}
	
	/**
	 * Return the environment name of the configured environment.
	 * @return Environment type.
	 */
	public String getConfiguredEnvironmentName() {
		for(Environment environment : this.environment) {
			if(environment.getType().equalsIgnoreCase(getServer().getEnvironment())) {
				return environment.getType();
			}
		}
		return null;
	}
	
	public void setEnvironment(List<Environment> environment) {
		this.environment = environment;
	}

	public Config(Client client, Server server) {
		this.client = client;
		this.server = server;
	}
	
	public Client getClient() {
		return client;
	}
	public void setClient(Client client) {
		this.client = client;
	}
	public Server getServer() {
		return server;
	}
	public void setServer(Server server) {
		this.server = server;
	}
	
	/**
	 * 
	 * @return
	 */
	public String getNetworkId() {
		return getServer().getSetup().getRetailerInfo().getNetworkId();
	}
	
	/**
	 * Finds the program setup based on the billNo.
	 * @param billNo
	 * @return ProgramSetup
	 */
	public ProgramSetup getProgramSetup(String billNo) {
		List<ProgramSetup> programSetupList = getServer().getSetup().getProgramSetup();
		for(ProgramSetup setup : programSetupList) {
			if(setup.getId().equalsIgnoreCase(billNo)) {
				return setup;
			}
		}
		return null;
	}
	
	/**
	 * Returns promotion setup based on the cardRangeId
	 * @param cardRangeId
	 * @return PromotionSetup.
	 */
	public PromotionSetup getPromotionSetupByCardRangeId(String cardRangeId) {
		List<PromotionSetup> promotionSetupList = getServer().getSetup().getPromotionSetup();
		for(PromotionSetup setup : promotionSetupList) {
			if(setup.getCardRangeId().equalsIgnoreCase(cardRangeId)) {
				return setup;
			}
		}
		return null;
	}
	
	/**
	 * Dmp url
	 * @return dmpUrl
	 */
	public String getDmpUrl() {
		return getConfiguredEnvironment().getDmpConfig().getDmpUrl();
	}
	
	/**
	 * The dmp api user name for the configured environment.
	 * @return userName.
	 */
	public String getDmpUserName() {
		return getConfiguredEnvironment().getDmpConfig().getUserName();
	}
	
	/**
	 * The dmp api password for the configured environment.
	 * @return userName.
	 */
	public String getDmpPassword() {
		return getConfiguredEnvironment().getDmpConfig().getPassword();
	}
	
	/**
	 * Returns the card setup list configured for this environment.
	 * @return List<CardSetup>
	 */
	public List<CardSetup> getCardSetupList() {
		return getServer().getSetup().getCardSetup();
	}

	public String getTargetingApiUrl() {
		return getConfiguredEnvironment().getOmniConfig().getTargetingUrl();
	}
	
	public String getCappingApiUrl() {
		return getConfiguredEnvironment().getOmniConfig().getCappingUrl();
	}
	
	public String getEventsApiUrl() {
		return getConfiguredEnvironment().getOmniConfig().getEventsUrl();
	}
}
