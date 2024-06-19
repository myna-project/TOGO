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

import it.mynaproject.togo.api.domain.Feed;
import it.mynaproject.togo.api.model.FeedJson;

public interface FeedService {

	public Feed getFeed(Integer id, Boolean isAdmin, String username);
	public List<Feed> getFeedsForUser(String username);
	public void persist(Feed feed);
	public Feed createFeedFromInput(FeedJson input, Boolean isAdmin, String username);
	public void update(Feed feed);
	public Feed updateFeedFromInput(Integer id, FeedJson input, Boolean isAdmin, String username);
	public void deleteFeedById(Integer id, Boolean isAdmin, String username);
}
