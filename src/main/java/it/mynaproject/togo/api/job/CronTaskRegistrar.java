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
package it.mynaproject.togo.api.job;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.config.CronTask;
import org.springframework.stereotype.Component;

import it.mynaproject.togo.api.util.ScheduledTask;

@Component
public class CronTaskRegistrar implements DisposableBean {

	final private Logger log = LoggerFactory.getLogger(this.getClass());

	final private Map<Runnable, ScheduledTask> scheduledTasks = new ConcurrentHashMap<>();

	@Autowired
	private TaskScheduler taskScheduler;

	public TaskScheduler getScheduler() {
		return this.taskScheduler;
	}

	public void addCronTask(Runnable task, String cronExpression) {
		addCronTask(new CronTask(task, cronExpression));
	}

	public void addCronTask(CronTask cronTask) {

		if (cronTask != null) {
			Runnable task = cronTask.getRunnable();
			if (this.scheduledTasks.containsKey(task))
				removeCronTask(task);

			log.debug("Start new task {}", task.toString());

			this.scheduledTasks.put(task, scheduleCronTask(cronTask));
		}
	}

	public void removeCronTask(Runnable task) {

		ScheduledTask scheduledTask = this.scheduledTasks.remove(task);
		if (scheduledTask != null)
			scheduledTask.cancel();
	}

	public ScheduledTask scheduleCronTask(CronTask cronTask) {

		ScheduledTask scheduledTask = new ScheduledTask();
		scheduledTask.future = this.taskScheduler.schedule(cronTask.getRunnable(), cronTask.getTrigger());

		return scheduledTask;
	}

	@Override
	public void destroy() {

		for (ScheduledTask task : this.scheduledTasks.values())
			task.cancel();

		this.scheduledTasks.clear();
	}
}
