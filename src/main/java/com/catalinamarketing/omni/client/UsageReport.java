package com.catalinamarketing.omni.client;

/**
 * This class accumulates the usage information for the channelMediaId.
 * @author achavan	
 *
 */
public class UsageReport {
	private String channelMediaID;
	private int used;
	
	public UsageReport() {
	}
	
	public UsageReport(String channelMediaId, int used) {
		this.setChannelMediaID(channelMediaId);
		this.setUsed(used);
	}

	public String getChannelMediaID() {
		return channelMediaID;
	}

	public void setChannelMediaID(String channelMediaID) {
		this.channelMediaID = channelMediaID;
	}

	public int getUsed() {
		return used;
	}

	public void setUsed(int used) {
		this.used = used;
	}
}
