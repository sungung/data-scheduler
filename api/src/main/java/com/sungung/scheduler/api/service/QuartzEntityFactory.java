package com.sungung.scheduler.api.service;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import org.quartz.CronScheduleBuilder;
import org.quartz.JobBuilder;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;

import com.sungung.scheduler.api.entity.Job;
import com.sungung.scheduler.api.entity.JobSchedule;

public class QuartzEntityFactory {
	public static Set<Trigger> buildTriggers(Job job){
		
		Set<Trigger> triggers = new HashSet<Trigger>();
		
		for (JobSchedule schd : job.getSchedules()){
			
			JobDataMap jdm = new JobDataMap(job.getData());
			
			schd.setId(buildId(job.getName()));
			// literal cron expression in trigger job data as storage
			// because it is not handy to retrieve expression from Quartz entity
			jdm.put("cron", schd.getCronExpression());
			jdm.put("suspended", schd.isSuspended());
			triggers.add(TriggerBuilder.newTrigger()
					.withIdentity(schd.getId())
					.startAt(schd.getStart())
					.endAt(schd.getEnd())
					.withSchedule(CronScheduleBuilder.cronSchedule(schd.getCronExpression()))
					.usingJobData(jdm)
					.build());
		}
		return triggers;
	}

	private static String buildId(String name) {
		return name.concat("-") + UUID.randomUUID();
	}

	public static JobDetail buildJobDetail(Job job) {
		
		JobDataMap jdm = new JobDataMap(job.getData());
		jdm.put("is-scheduled", job.isScheduled());
		jdm.put("group-node", job.getJobGroup().getNode());
		jdm.put("group-description", job.getJobGroup().getDescription());
		
		return JobBuilder.newJob(job.getJobClassName())
				.withIdentity(job.getName(), job.getJobGroup().getName())
				.withDescription(job.getDescription())
				.usingJobData(jdm)
				.build();
	}
}
