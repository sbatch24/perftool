package com.catalinamarketing.omni.message;

public class UpdateRequest {
	private String statusCode;
	
	public UpdateRequest() {
	}
	
	public UpdateRequest(String val) {
		this.setStatusCode(val);
	}

	public String getStatusCode() {
		return statusCode;
	}

	public void setStatusCode(String statusCode) {
		this.statusCode = statusCode;
	}

}
