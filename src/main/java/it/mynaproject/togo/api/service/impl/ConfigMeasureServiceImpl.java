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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import it.mynaproject.togo.api.domain.Client;
import it.mynaproject.togo.api.domain.ClientType;
import it.mynaproject.togo.api.domain.Drain;
import it.mynaproject.togo.api.domain.Feed;
import it.mynaproject.togo.api.domain.FeedDescriptions;
import it.mynaproject.togo.api.domain.Org;
import it.mynaproject.togo.api.model.ConfigMeasureJson;
import it.mynaproject.togo.api.service.ClientService;
import it.mynaproject.togo.api.service.ConfigMeasureService;
import it.mynaproject.togo.api.service.DrainService;
import it.mynaproject.togo.api.service.FeedService;

@Service
public class ConfigMeasureServiceImpl implements ConfigMeasureService {

	final private Logger log = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private ClientService clientService;

	@Autowired
	private FeedService feedService;

	@Autowired
	private DrainService drainService;

	@Override
	@Transactional
	public void updateConfigFromInput(List<ConfigMeasureJson> input, Boolean isAdmin, String username) {

		log.info("Received new config from WOLF");

		// get controller client from first measure configuration
		Client controller = this.clientService.getClient(Integer.parseInt(input.get(0).getClientId()), isAdmin, username);

		Org org = controller.getOrg();

		List<Client> controlledList = this.clientService.getAllControlledClients(controller);
		Map<String, Client> clientFound = new HashMap<String, Client>();

		for (ConfigMeasureJson newConf : input) {

			Client c = new Client();
			Client client = clientFound.containsKey(newConf.getDeviceId()) ? clientFound.get(newConf.getDeviceId()) : this.clientService.getClientFromDeviceId(controller, newConf.getDeviceId());
			if (client == null) {
				c.setOrg(org);
				c.setController(controller);
				c.setDeviceId(newConf.getDeviceId());
				c.setName(newConf.getClientName());
				c.setType(ClientType.WOLF_MANAGED);
				c.setPluginId(newConf.getPluginId());
				c.setComputerClient(true);
				c.setEnergyClient(true);
				c.setActive(true);

				this.clientService.persist(c);

				clientFound.put(newConf.getDeviceId(), c);
			} else {
				client.setName(newConf.getClientName());
				client.setType(ClientType.WOLF_MANAGED);
				client.setPluginId(newConf.getPluginId());
				client.setComputerClient(true);
				client.setActive(true);
				this.clientService.update(client, username);

				c = client;
				controlledList.remove(c);
			}

			Boolean newDrain = true;
			for (Feed feed : c.getFeeds()) {
				for (Drain drain : feed.getDrains()) {
					if (drain.getMeasureId().equals(newConf.getMeasureId())) {
						drain.setName(newConf.getDrainName());
						drain.setUnitOfMeasure(newConf.getUnitOfMeasure());
						drain.setMeasureType(newConf.getMeasureType());
						this.drainService.update(drain);

						newDrain = false;
						break;
					}
				}
				if (!newDrain)
					break;
			}
			if (newDrain) {
				Boolean newFeed = true;
				Feed f = new Feed();
				String description = FeedDescriptions.getDescription(newConf.getUnitOfMeasure().toLowerCase(Locale.ROOT));
				if (description == null)
					description = newConf.getUnitOfMeasure();
				for (Feed feed : c.getFeeds()) {
					if (feed.getDescription().equals(description)) {
						f = this.feedService.getFeed(feed.getId(), isAdmin, username);
						newFeed = false;
						break;
					}
				}
				if (newFeed) {
					f.setDescription(description);
					List<Client> clients = new ArrayList<>();
					clients.add(c);
					f.setClients(clients);

					this.feedService.persist(f);

					List<Feed> updatedFeeds = c.getFeeds();

					updatedFeeds.add(f);

					c.setFeeds(updatedFeeds);
				}

				Drain d = new Drain();
				d.setFeed(f);
				d.setMeasureId(newConf.getMeasureId());
				d.setName(newConf.getDrainName());
				d.setUnitOfMeasure(newConf.getUnitOfMeasure());
				d.setMeasureType(newConf.getMeasureType());
				d.setClientDefaultDrain(false);

				this.drainService.persist(d);

				List<Drain> updatedDrains = f.getDrains();

				updatedDrains.add(d);

				f.setDrains(updatedDrains);
			}
		}

		// set WOLF_MANAGED clients not found in "input" as inactive if were active
		for (Client inactiveClient : controlledList) {
			if (inactiveClient.getType().equals(ClientType.WOLF_MANAGED) && inactiveClient.getActive()) {
				log.info("Set client {} as inactive", inactiveClient.getId());

				Client to_update = this.clientService.getClient(inactiveClient.getId(), isAdmin, username);
				to_update.setActive(false);
				this.clientService.update(to_update, username);
			}
		}

		// set client "inactive" if all controlled clients are inactive
		this.clientService.inactiveClientsFromController(controller, username);
	}
}
