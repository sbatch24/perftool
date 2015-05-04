package com.catalinamarketing.omni.config;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name="dmpConfig")
public class DmpConfig {
	private String userName;
	private String password;
	private String dmpUrl;
	
	public DmpConfig(){
		
	}
	
	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getDmpUrl() {
		return dmpUrl;
	}

	public void setDmpUrl(String dmpUrl) {
		this.dmpUrl = dmpUrl;
	}

}
