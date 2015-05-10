package com.catalinamarketing.omni.pmr.setup;

import java.util.ArrayList;
import java.util.List;



/**
 * @author achavan
 */
public class PmrSetupMessage {
	private String setupSystemID = new String("MXP");
	private String locale = new String("US");
	private List<ProgramInfo> programs;
	
	public String getSetupSystemID() {
		return setupSystemID;
	}
	public void setSetupSystemID(String setupSystemID) {
		this.setupSystemID = setupSystemID;
	}
	public String getLocale() {
		return locale;
	}
	public void setLocale(String locale) {
		this.locale = locale;
	}
	public List<ProgramInfo> getPrograms() {
		return programs;
	}
	public void setPrograms(List<ProgramInfo> programs) {
		this.programs = programs;
	}
	
	public void addProgram(ProgramInfo program) {
		if(this.programs == null) {
			this.programs = new ArrayList<ProgramInfo>();
		}
		this.programs.add(program);
	}
	
	/**
	 * Find the channelMediaId for an Award.
	 * @param awardNumber
	 * @return the channelMediaId. null if cannot find channelId.
	 */
	public String getChannelMediaId(String awardNumber) {
		String channelMedia = null;
		for(ProgramInfo info : programs) {
			channelMedia = info.getChannelMediaId(awardNumber);
			if(channelMedia != null) {
				break;
			}
		}
		return channelMedia;
	}
}
