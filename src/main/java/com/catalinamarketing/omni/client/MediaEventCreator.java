package com.catalinamarketing.omni.client;

import com.catalinamarketing.omni.api.MediaEvents;
import com.catalinamarketing.omni.api.TargetedMediaResponse;

public interface MediaEventCreator {

	public MediaEvents prepareEvent(TargetedMediaResponse targetMediaResponse, String customerId, MediaEvents mediaEvent);
	
}
