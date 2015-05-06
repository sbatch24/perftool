package com.catalinamarketing.omni.client;

import java.util.List;
import java.util.Map;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.glassfish.jersey.client.authentication.HttpAuthenticationFeature;

import com.catalinamarketing.omni.dmp.setup.Wallet;
import com.google.common.base.Stopwatch;


/**
 * This thread class is reponsible for executing the targeting api.
 * @author achavan
 *
 */
public class TargetingApiExecutor extends ApiExecutor {

	private final int reportEventDelay;
	
	public TargetingApiExecutor(int reportEventDelay) {
		this.reportEventDelay =  reportEventDelay;
	}
	
	@Override
	public void run() {
		HttpAuthenticationFeature feature = HttpAuthenticationFeature.basic(config.getDmpUserName(), config.getDmpPassword());
		Stopwatch watch =  Stopwatch.createStarted();
		Client client = ClientBuilder.newClient();
		WebTarget target = client.target(dmpWalletUrl(entry.getKey()));
			Response resp = target.register(feature).request()
					.accept(MediaType.APPLICATION_JSON)
					.header("Content-Type", MediaType.APPLICATION_JSON).post(Entity.entity(gson.toJson(entry.getValue()), MediaType.APPLICATION_JSON));	
			if(resp.getStatusInfo().getStatusCode() != Response.Status.OK.getStatusCode()) {
				logger.error("Problem post wallet data for CID " + entry.getKey() + " Error code : "+ resp.getStatus());
			}
			resp.close();
			client.close();
	}

	public int getReportEventDelay() {
		return reportEventDelay;
	}
}
