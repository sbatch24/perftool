package com.catalinamarketing.omni.util;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Class translates channel type between omni and dmp
 * @author achavan
 *
 */
public class ChannelTypeTranslator {
	
	private static final Map<String, String> CHANNEL_MAP;
	static
	{
		Map<String, String>tempMap = new HashMap<String, String>();
		tempMap.put("CATALINA_IN_STORE", "store");
		tempMap.put("LOAD_TO_CARD", "web");
		CHANNEL_MAP = Collections.unmodifiableMap(tempMap);
	}
	
	public static String getChannelType(String key) {
		return CHANNEL_MAP.get(key);
	}

}
