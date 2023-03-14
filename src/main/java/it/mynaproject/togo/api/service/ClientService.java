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
package it.mynaproject.togo.api.service;

import java.util.List;

import it.mynaproject.togo.api.domain.Client;
import it.mynaproject.togo.api.model.ClientJson;

public interface ClientService {

	public Client getClient(Integer id, Boolean isAdmin, String username);
	public List<Client> getClients();
	public List<Client> getClientsForUser(String username);
	public void persist(Client client);
	public Client createClientFromJson(ClientJson input, Boolean isAdmin, String username);
	public void update(Client client, String username);
	public Client updateClientFromJson(Integer id, ClientJson input, Boolean isAdmin, String username);
	public void deleteClientById(Integer clientId, Boolean isAdmin, String username);
	public List<Integer> getClientIdByDeviceId(String deviceId);
	public Client getClientFromDeviceId(Client controller,String deviceId);
	public List<Client> getAllControlledClients(Client controller);
	public Boolean inactiveClientsFromController(Client controller, String username);
}
