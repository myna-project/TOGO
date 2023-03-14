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

import it.mynaproject.togo.api.domain.Job;
import it.mynaproject.togo.api.domain.Org;
import it.mynaproject.togo.api.model.JobJson;

public interface JobService {

	public Job getJob(Integer id, Boolean isAdmin, String username);
	public List<Job> getJobs();
	public List<Job> getJobsForUser(String username);
	public void persist(Job job);
	public Job createJobFromInput(JobJson input, Boolean isAdmin, String username);
	public void update(Job job);
	public Job updateJobFromInput(Integer id, JobJson input, Boolean isAdmin, String username);
	public void deleteJobById(Integer id, Boolean isAdmin, String username);
	public void createDefaultJob(Org org);
}
