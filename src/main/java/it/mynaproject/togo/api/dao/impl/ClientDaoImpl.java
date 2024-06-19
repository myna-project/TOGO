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
package it.mynaproject.togo.api.dao.impl;

import java.util.List;

import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.hibernate.Hibernate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import it.mynaproject.togo.api.dao.ClientDao;
import it.mynaproject.togo.api.domain.Client;
import it.mynaproject.togo.api.domain.Drain;
import it.mynaproject.togo.api.domain.Feed;

@Repository
public class ClientDaoImpl extends BaseDaoImpl implements ClientDao {

	final private Logger log = LoggerFactory.getLogger(this.getClass());

	@Override
	public void persist(Client client) {

		log.info("Creating new client: {}", client.toString());

		em.persist(client);
		em.flush();
	}

	@Override
	public void update(Client client) {

		log.info("Updating client: {}", client.toString());

		em.persist(em.merge(client));
		em.flush();
	}

	@Override
	public void delete(Client client) {

		log.info("Deleting client: {}", client.toString());

		em.remove(em.merge(client));
		em.flush();
	}

	@Override
	public Client getClient(Integer id) {

		log.debug("Getting client with id: {}", id);

		Query q = em.createQuery("FROM Client WHERE id = :id");
		q.setParameter("id", id);

		try {
			Client client = (Client) q.getSingleResult();
			this.initializeClient(client);
			return client;
		} catch (NoResultException nre) {
			return null;
		}
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<Client> getClients() {

		log.debug("Getting all clients");

		List<Client> clients = em.createQuery("FROM Client").getResultList();
		for (Client c : clients)
			this.initializeClient(c);

		return clients;
	}

	@Override
	public Client getClientByDeviceIdAndParent(String deviceId, Client parent) {

		log.debug("Getting client with device: {} and parent: {}", deviceId, parent.getId());

		Query q = em.createQuery("FROM Client WHERE parent_id = :parent_id AND device_id = :deviceId");
		q.setParameter("parent_id", parent.getId());
		q.setParameter("deviceId", deviceId);

		try {
			Client client = (Client) q.getSingleResult();
			this.initializeClient(client);
			return client;
		} catch (NoResultException nre) {
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Integer> getClientIdByDeviceId(String deviceId) {

		log.debug("Getting clients with device: {}", deviceId);

		Query q = em.createQuery("SELECT id FROM Client WHERE device_id = :deviceId");
		q.setParameter("deviceId", deviceId);

		try {
			return (List<Integer>) q.getResultList();
		} catch (NoResultException nre) {
			return null;
		}
	}

	private void initializeClient(Client c) {
		Hibernate.initialize(c.getChildList());
		Hibernate.initialize(c.getControlledList());
		Hibernate.initialize(c.getFeeds());
		for (Feed f : c.getFeeds()) {
			Hibernate.initialize(f.getDrains());
			for (Drain d : f.getDrains())
				Hibernate.initialize(d.getControls());
			Hibernate.initialize(f.getClients());
		}
		Hibernate.initialize(c.getFormulas());
	}
}
