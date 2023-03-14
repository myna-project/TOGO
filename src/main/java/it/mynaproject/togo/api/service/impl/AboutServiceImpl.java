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
package it.mynaproject.togo.api.service.impl;

import java.util.Properties;

import org.springframework.stereotype.Service;

import it.mynaproject.togo.api.model.AboutJson;
import it.mynaproject.togo.api.service.AboutService;
import it.mynaproject.togo.api.util.PropertiesFileReader;

@Service
public class AboutServiceImpl implements AboutService {

	public AboutJson getVersionAndBuildtime() {

		AboutJson aboutJson = new AboutJson();

		Properties prop = PropertiesFileReader.loadPropertiesFile();

		aboutJson.setVersion(prop.getProperty("version"));
		aboutJson.setBuildtime(prop.getProperty("buildtimestamp"));

		return aboutJson;
	}
}
