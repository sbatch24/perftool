package com.catalinamarketing.omni.config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
	 * Calculates the amount of time in  seconds it would take for the performance simulator to run.
	 * Currently the performance simulator simulates targeting and capping calls. The simulation will take the longest of the two times.
	 * @return
	 */
	public long getSimulationTimeInSeconds() {
		Simulation simulation = getConfiguredSimulation();
		return (simulation.getCappingCallCount() * simulation.getCapReportFrequency()) > (simulation.getTargetingCallCount() * simulation.getEventReportFrequency())
				? (simulation.getCappingCallCount() * simulation.getCapReportFrequency()) : (simulation.getTargetingCallCount() * simulation.getEventReportFrequency());
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
	 * @param programSetupId
	 * @return ProgramSetup
	 */
	public ProgramSetup getProgramSetup(String programSetupId) {
		List<ProgramSetup> programSetupList = getServer().getSetup().getProgramSetup();
		for(ProgramSetup setup : programSetupList) {
			if(setup.getProgramId().equalsIgnoreCase(programSetupId)) {
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
	
	public List<ProgramSetup> getProgramSetupList() {
		return getServer().getSetup().getProgramSetup();
	}
	
	/**
	 * Return a list of promotion setup that fall under same programId
	 * @param programId
	 * @return
	 */
	public Map<Integer,List<PromotionSetup>> getPromotionsByProgramId(String programId) {
		Map<Integer,List<PromotionSetup>> promotionSetupMap = new HashMap<Integer,List<PromotionSetup>>();
		for(PromotionSetup promotionSetup : getServer().getSetup().getPromotionSetup()) {
			if(promotionSetup.getProgramSetupId().equalsIgnoreCase(programId)) {
				List<PromotionSetup> promotionSetupList = null;
				if(!promotionSetupMap.containsKey(promotionSetup.getAwardId())) {
					promotionSetupList = new ArrayList<PromotionSetup>();
					promotionSetupMap.put(promotionSetup.getAwardId(), promotionSetupList);
				}
				promotionSetupList = promotionSetupMap.get(promotionSetup.getAwardId());
				promotionSetupList.add(promotionSetup);
			}
		}
		return promotionSetupMap;
	}
}
