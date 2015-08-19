package com.catalinamarketing.omni.client;

import com.catalinamarketing.omni.api.MediaEvents;
import com.catalinamarketing.omni.api.TargetedMediaResponse;

public class ThresholdEventCreator implements MediaEventCreator {

	@Override
	public MediaEvents prepareEvent(TargetedMediaResponse targetMediaResponse,
			String customerId, MediaEvents mediaEvent) {
		return mediaEvent;
	}


}
