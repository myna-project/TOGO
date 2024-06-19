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
package it.mynaproject.togo.api.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.slf4j.LoggerFactory;

public abstract class PropertiesFileReader {

	public static Properties loadPropertiesFile() {

		Properties prop = new Properties();

		try (InputStream resourceAsStream = PropertiesFileReader.class.getClassLoader().getResourceAsStream("config.properties")) {
			prop.load(resourceAsStream);
		} catch (IOException e) {
			LoggerFactory.getLogger(PropertiesFileReader.class).error("Read error: can't find config.properties");
		}

		return prop;
	}
}
