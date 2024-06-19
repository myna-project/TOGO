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
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import it.mynaproject.togo.api.dao.JobDao;
import it.mynaproject.togo.api.domain.Job;
import it.mynaproject.togo.api.domain.Org;
import it.mynaproject.togo.api.exception.ConflictException;
import it.mynaproject.togo.api.exception.NotFoundException;
import it.mynaproject.togo.api.model.JobJson;
import it.mynaproject.togo.api.service.JobService;
import it.mynaproject.togo.api.service.OrgService;
import it.mynaproject.togo.api.service.UserService;

@Service
public class JobServiceImpl implements JobService {

	final private Logger log = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private JobDao jobDao;

	@Autowired
	private UserService userService;

	@Autowired 
	private OrgService orgService;

	@Override
	@Transactional
	public Job getJob(Integer id, Boolean isAdmin, String username) {

		Job j = this.jobDao.getJob(id);
		if ((j == null) || (!isAdmin && !this.getJobsForUser(username).contains(j)))
			throw new NotFoundException(404, "Job " + id + " not found");

		return j;
	}

	@Override
	@Transactional
	public List<Job> getJobs() {
		return this.jobDao.getJobs();
	}

	@Override
	@Transactional
	public List<Job> getJobsForUser(String username) {

		List<Org> orgs = this.orgService.getOrgsForUser(username);
		List<Job> jobs = new ArrayList<>();

		for (Org o : orgs)
			jobs.addAll(o.getJobs());

		return jobs;
	}

	@Override
	@Transactional
	public void persist(Job job) {
		this.jobDao.persist(job);
	}

	@Override
	@Transactional
	public Job createJobFromInput(JobJson input, Boolean isAdmin, String username) {

		log.info("Creating new job: {}", input.toString());

		Org org = this.orgService.getOrg(input.getOrgId(), isAdmin, username);

		if (!this.checkJobNameForOrg(input.getName(), org, null))
			throw new ConflictException(3001, "Job name " + input.getName() + " not available for this org");

		Job j = new Job();
		j.populateJobFromInput(input, org);

		this.persist(j);

		return j;
	}

	@Override
	@Transactional
	public void update(Job job) {
		this.jobDao.update(job);
	}

	@Override
	@Transactional
	public Job updateJobFromInput(Integer id, JobJson input, Boolean isAdmin, String username) {

		log.info("Updating job with id {}", id);

		Job j = this.getJob(id, isAdmin, username);

		Org org = this.orgService.getOrg(input.getOrgId(), isAdmin, username);

		if (!this.checkJobNameForOrg(input.getName(), org, id))
			throw new ConflictException(3001, "Job name " + input.getName() + " not available for this org");

		j.populateJobFromInput(input, org);

		this.update(j);

		return j;
	}

	@Override
	@Transactional
	public void deleteJobById(Integer id, Boolean isAdmin, String username) {

		Job j = this.getJob(id, isAdmin, username);
		if (j.getUsers().size() > 0)
			throw new ConflictException(3101, "Cannot delete job " + j.getName() + " because it is assigned to one or more users");

		this.jobDao.delete(j);
	}

	@Override
	@Transactional
	public void createDefaultJob(Org org) {

		Job j = new Job();
		j.setName("default");
		j.setDescription("Proprietario di organizzazione");
		j.setOrg(org);
		this.persist(j);

		org.springframework.security.core.userdetails.User user = (org.springframework.security.core.userdetails.User)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		it.mynaproject.togo.api.domain.User u = this.userService.getUserByUsername(user.getUsername());
		this.userService.addJobToUser(u, j);
	}

	private Boolean checkJobNameForOrg(String name, Org org, Integer jobId) {

		for (Job j : org.getJobs())
			if (j.getName().trim().toLowerCase().equals(name.trim().toLowerCase()))
				if ((jobId == null) || (j.getId() != jobId))
					return false;

		return true;
	}
}
