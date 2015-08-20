package com.catalinamarketing.omni.client;

import com.catalinamarketing.omni.api.MediaEvents;
import com.catalinamarketing.omni.api.TargetedMediaResponse;
import com.catalinamarketing.omni.protocol.message.TestPlanMsg;

public interface MediaEventCreator {
	public MediaEvents prepareEvent(TargetedMediaResponse targetMediaResponse, MediaEvents mediaEvent, TestPlanMsg testPlan);
}
