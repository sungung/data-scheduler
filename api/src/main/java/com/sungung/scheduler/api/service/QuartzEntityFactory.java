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
import com.sungung.scheduler.api.entity.JobTrigger;

public class QuartzEntityFactory {
	
	public static Set<Trigger> buildTriggers(Job job){		
		Set<Trigger> triggers = new HashSet<Trigger>();		
		for (JobTrigger jt : job.getSchedules()){
			triggers.add(buildTrigger(job, jt));
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
	
	public static Trigger buildTrigger(Job job, JobTrigger jt){
		JobDataMap jdm = new JobDataMap(job.getData());
		jdm.put("cron", jt.getCronExpression());
		jdm.put("suspended", jt.isSuspended());
		return TriggerBuilder.newTrigger()
				.withIdentity(buildId(job.getName()))
				.startAt(jt.getStart())
				.endAt(jt.getEnd())
				.withSchedule(CronScheduleBuilder.cronSchedule(jt.getCronExpression()))
				.usingJobData(jdm)
				.build();
	}
}
