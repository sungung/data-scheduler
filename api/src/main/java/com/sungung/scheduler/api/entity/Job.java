package com.sungung.scheduler.api.entity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.quartz.JobDetail;
import org.quartz.Trigger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;

@JsonDeserialize(builder = Job.Builder.class)
public class Job implements Cloneable {
	
	private final static Logger log = LoggerFactory.getLogger(Job.class);
	
	@JsonProperty
	private String name;
	@JsonProperty
	private String description;
	@JsonProperty("class-name")
	private Class<? extends org.quartz.Job> jobClassName;
	@JsonProperty("is-scheduled")
	private boolean isScheduled;
	@JsonProperty("job-group")
	private JobGroup jobGroup;
	@JsonProperty
	private List<JobTrigger> schedules = new ArrayList<>();
	@JsonProperty("job-data")
	Map<String, String> data = new HashMap<String, String>();
		
	public String getName() {
		return name;
	}
	public String getDescription() {
		return description;
	}
	public Class<? extends org.quartz.Job> getJobClassName() {
		return jobClassName;
	}
	public boolean isScheduled() {
		return isScheduled;
	}
	public JobGroup getJobGroup() {
		return jobGroup;
	}
	public List<JobTrigger> getSchedules() {
		return schedules;
	}
	public Map<String, String> getData() {
		return data;
	}

	@JsonPOJOBuilder(buildMethodName = "build")
	public static class Builder{
		@JsonProperty
		private String name;
		@JsonProperty
		private String description;
		@JsonProperty("class-name")
		private Class<? extends org.quartz.Job> jobClassName;
		@JsonProperty("is-scheduled")
		private boolean isScheduled;
		@JsonProperty("job-group")
		private JobGroup jobGroup;
		@JsonProperty
		private List<JobTrigger> schedules = new ArrayList<>();
		@JsonProperty("job-data")
		Map<String, String> data = new HashMap<String, String>();
		
		public Builder name(String name){
			this.name = name;
			return this;
		}
		public Builder description(String description){
			this.description = description;
			return this;
		}		
		public Builder jobClassName(Class<? extends org.quartz.Job> jobClassName){
			this.jobClassName = jobClassName;
			return this;
		}
		public Builder isScheduled(boolean isScheduled){
			this.isScheduled = isScheduled;
			return this;
		}
		public Builder jobGroup(JobGroup jobGroup){
			this.jobGroup = jobGroup;
			return this;
		}
		public Builder schedules(List<JobTrigger> schedules){
			this.schedules = schedules;
			return this;
		}
		public Builder data(Map<String, String> data){
			this.data = data;
			return this;
		}
		
		public Job build() throws CloneNotSupportedException{
			Job jobDefinition = new Job();
			jobDefinition.name = this.name;
			jobDefinition.description = this.description;
			jobDefinition.jobClassName = this.jobClassName;
			jobDefinition.isScheduled = this.isScheduled;
			if (this.jobGroup != null) jobDefinition.jobGroup = (JobGroup) this.jobGroup.clone();
			if (this.schedules != null) jobDefinition.schedules = new ArrayList<>(this.schedules);
			if (this.data != null) jobDefinition.data = new HashMap<>(this.data);
			return jobDefinition;
		}
		
		public Builder copyOf(Job jobDefinition) throws CloneNotSupportedException {
			this.name = jobDefinition.name;
			this.description = jobDefinition.description;
			this.jobClassName = jobDefinition.jobClassName;
			this.isScheduled = jobDefinition.isScheduled;
			if (jobDefinition.jobGroup != null) this.jobGroup = (JobGroup) jobDefinition.jobGroup.clone();
			if (jobDefinition.schedules != null) this.schedules = new ArrayList<>(jobDefinition.schedules);
			if (jobDefinition.data != null) this.data = new HashMap<>(jobDefinition.data); 
			return this;
		}
		
		public Builder from(JobDetail jobDetail, List<Trigger> triggers) {
			
			from(jobDetail);
			
			Function<Trigger, JobTrigger> toJobSchedule = new Function<Trigger, JobTrigger>(){
				@Override
				public JobTrigger apply(Trigger t) {
					return new JobTrigger.Builder()
							.id(t.getKey().getName())
							.start(t.getStartTime())
							.end(t.getEndTime())
							.cronExpression(t.getJobDataMap().getString("cron"))
							.lastFired(t.getPreviousFireTime())
							.nextFire(t.getNextFireTime())
							.suspended(t.getJobDataMap().getBoolean("suspended"))
							.build();
				}			
			};			
			this.schedules = triggers.stream().map(toJobSchedule).collect(Collectors.toList());
						
			return this;
		}
		public Builder from(JobDetail jobDetail) {
			this.name = jobDetail.getKey().getName();
			this.description = jobDetail.getDescription();
			this.jobClassName = jobDetail.getJobClass();
			this.isScheduled = (boolean)jobDetail.getJobDataMap().get("is-scheduled");
			this.jobGroup = new JobGroup.Builder()
					.name(jobDetail.getKey().getGroup())
					.description(jobDetail.getJobDataMap().getString("group-description"))
					.node(jobDetail.getJobDataMap().getString("group-node")).build();
			return this;
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Job other = (Job) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}
	
	@Override
	public String toString() {
		return "Job [name=" + name + ", description=" + description + ", jobClassName=" + jobClassName
				+ ", isScheduled=" + isScheduled + ", jobGroup=" + jobGroup + ", schedules=" + schedules + ", data="
				+ data + "]";
	}
	protected Object clone() throws CloneNotSupportedException{
		return super.clone();
	}
	
}

