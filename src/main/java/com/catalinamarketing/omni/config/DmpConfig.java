package com.catalinamarketing.omni.config;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name="dmpConfig")
public class DmpConfig {
	private String userName;
	private String password;
	private String dmpWalletUrl;
	private String dmpProfileUrl;
	
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

	public String getDmpWalletUrl() {
		return dmpWalletUrl;
	}

	public void setDmpWalletUrl(String dmpUrl) {
		this.dmpWalletUrl = dmpUrl;
	}

	public String getDmpProfileUrl() {
		return dmpProfileUrl;
	}

	public void setDmpProfileUrl(String dmpProfileUrl) {
		this.dmpProfileUrl = dmpProfileUrl;
	}

}
