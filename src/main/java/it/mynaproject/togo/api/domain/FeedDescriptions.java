/*******************************************************************************
 * Copyright (c) Myna-Project SRL <info@myna-project.org>.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 * 
 * Contributors:
 * - Myna-Project SRL <info@myna-project.org> - initial API and implementation
 ******************************************************************************/
package it.mynaproject.togo.api.domain;

import java.util.HashMap;
import java.util.Map;

public abstract class FeedDescriptions {

	private static Map<String, String> valueMap = null;

	static {
		valueMap = new HashMap<String, String>();
		valueMap.put("wh", "Energy");
		valueMap.put("kwh", "Energy");
		valueMap.put("mwh", "Energy");
		valueMap.put("varh", "Energy");
		valueMap.put("kvarh", "Energy");
		valueMap.put("mvarh", "Energy");
		valueMap.put("vah", "Energy");
		valueMap.put("kvah", "Energy");
		valueMap.put("mvah", "Energy");
		valueMap.put("v", "Voltage");
		valueMap.put("kv", "Voltage");
		valueMap.put("mv", "Voltage");
		valueMap.put("a", "Current");
		valueMap.put("ka", "Current");
		valueMap.put("ma", "Current");
		valueMap.put("w", "Power");
		valueMap.put("kw", "Power");
		valueMap.put("mw", "Power");
		valueMap.put("var", "Power");
		valueMap.put("kvar", "Power");
		valueMap.put("mvar", "Power");
		valueMap.put("va", "Power");
		valueMap.put("kva", "Power");
		valueMap.put("mva", "Power");
		valueMap.put("hz", "Frequency");
		valueMap.put("khz", "Frequency");
		valueMap.put("mhz", "Frequency");
		valueMap.put("", "Adimensional");
	}

	static public String getDescription(String key) {
		if (valueMap.containsKey(key))
			return valueMap.get(key);
		return null;
	}
}
