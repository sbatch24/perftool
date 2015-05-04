package com.catalinamarketing.omni.config;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name="environment")
public class Environment {
	
	private String type;
	private DmpConfig dmpConfig;
	private OmniConfig omniConfig;
	private Queue  queueInfo;
	
	public Environment(){
		
	}
	
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}

	public Queue getQueueInfo() {
		return queueInfo;
	}
	public void setQueueInfo(Queue queueInfo) {
		this.queueInfo = queueInfo;
	}

	public DmpConfig getDmpConfig() {
		return dmpConfig;
	}

	public void setDmpConfig(DmpConfig dmpConfig) {
		this.dmpConfig = dmpConfig;
	}

	public OmniConfig getOmniConfig() {
		return omniConfig;
	}

	public void setOmniConfig(OmniConfig omniConfig) {
		this.omniConfig = omniConfig;
	}
}
