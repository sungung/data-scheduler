package com.sungung.scheduler.api.entity;

import java.util.Date;

import org.quartz.Trigger;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;

@JsonDeserialize(builder = JobTrigger.Builder.class)
public class JobTrigger {
	@JsonProperty
	private String id;
	@JsonProperty
	private Date start;
	@JsonProperty
	private Date end;
	@JsonProperty("cron-expression")
	private String cronExpression;
	@JsonProperty
	private boolean suspended;
	@JsonProperty("last-fired")
	private Date lastFired;
	@JsonProperty("next-fire")
	private Date nextFire;

	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public Date getStart() {
		return start;
	}
	public Date getEnd() {
		return end;
	}
	public String getCronExpression() {
		return cronExpression;
	}
	public boolean isSuspended() {
		return suspended;
	}
	public Date getLastFired() {
		return lastFired;
	}
	public Date getNextFire() {
		return nextFire;
	}

	@JsonPOJOBuilder(buildMethodName = "build")
	public static class Builder{
		@JsonProperty
		private String id;
		@JsonProperty
		private Date start;
		@JsonProperty
		private Date end;
		@JsonProperty("cron-expression")
		private String cronExpression;
		@JsonProperty
		private boolean suspended;
		@JsonProperty("last-fired")
		private Date lastFired;
		@JsonProperty("next-fire")
		private Date nextFire;
		
		public Builder id(String id){
			this.id = id;
			return this;
		}
		public Builder start(Date start){
			this.start = start;
			return this;
		}
		public Builder end(Date end){
			this.end = end;
			return this;
		}
		public Builder cronExpression(String cronExpression){
			this.cronExpression = cronExpression;
			return this;
		}
		public Builder suspended(boolean suspended){
			this.suspended = suspended;
			return this;
		}
		public Builder lastFired(Date lastFired){
			this.lastFired = lastFired;
			return this;
		}
		public Builder nextFire(Date nextFire){
			this.nextFire =  nextFire;
			return this;
		}
		public JobTrigger build(){
			JobTrigger schedule = new JobTrigger();
			schedule.id = this.id;
			schedule.start = this.start;
			schedule.end = this.end;
			schedule.cronExpression = this.cronExpression;
			schedule.suspended = this.suspended;
			schedule.lastFired = this.lastFired;
			schedule.nextFire = this.nextFire;
			return schedule;
		}
		public Builder from(Trigger trigger){
			return this.id(trigger.getKey().getName())
					.start(trigger.getStartTime())
					.end(trigger.getEndTime())
					.cronExpression(trigger.getJobDataMap().getString("cron"))
					.lastFired(trigger.getPreviousFireTime())
					.nextFire(trigger.getNextFireTime());
		}
	}

	@Override
	public String toString() {
		return "JobSchedule [id=" + id + ", start=" + start + ", end=" + end + ", cronExpression=" + cronExpression
				+ ", suspended=" + suspended + ", lastFired=" + lastFired + ", nextFire=" + nextFire + "]";
	}
	
}

