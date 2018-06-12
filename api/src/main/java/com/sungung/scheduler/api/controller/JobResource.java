package com.sungung.scheduler.api.controller;

import java.net.URI;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.TriggerKey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.sungung.scheduler.api.entity.Job;
import com.sungung.scheduler.api.entity.JobTrigger;
import com.sungung.scheduler.api.service.QuartzEntityFactory;

@RestController
@RequestMapping("/scheduler")
public class JobResource {
	
	@Autowired
	private Scheduler scheduler;
	
	/*

 $ curl --header "Content-Type: application/json" -XPOST localhost:8080/scheduler/groups/report/jobs --data '{"name":"rep1","description":"unit report","schedules":[{"id":null,"start":"2018-05-31T00:00:00.000+0000","end":"2019-06-31T00:00:00.000+0000","suspended":false,"cron-expression":"10 * * * * ?","last-fired":null,"next-fire":null}],"scheduled":true,"class-name":"com.sungung.scheduler.api.service.SimpleJob","is-scheduled":true,"job-group":{"name":"report","description":"job group for reporting jobs","node":null},"job-data":{}}' -i
HTTP/1.1 201
Location: http://localhost:8080/scheduler/groups/report/jobs/rep1
Content-Length: 0
Date: Tue, 29 May 2018 05:03:33 GMT


$ curl http://localhost:8080/scheduler/groups/report/jobs/rep1 -i
HTTP/1.1 200
Content-Type: application/json;charset=UTF-8
Transfer-Encoding: chunked
Date: Tue, 29 May 2018 05:03:54 GMT

{"name":"rep1","description":null,"schedules":[{"id":null,"start":"2018-05-01T00:00:00.000+0000","end":"2019-05-01T00:00:00.000+0000","suspended":true,"cron-expression":"0 0 15 * * ?","last-fired":null,"next-fire":"2018-05-01T21:00:00.000+0000"},{"id":null,"start":"2010-05-01T00:00:00.000+0000","end":"2015-05-01T00:00:00.000+0000","suspended":true,"cron-expression":"0 0 15 * * ?","last-fired":null,"next-fire":"2010-05-01T05:00:00.000+0000"}],"scheduled":true,"class-name":"com.example.demo.job.SimpleJob","is-scheduled":true,"job-group":{"name":"report","description":"job group for reporting jobs","node":null},"job-data":{}}


$ curl -XDELETE localhost:8080/scheduler/groups/report/jobs/rep1
 
	 */
	@PostMapping("/groups/{group}/jobs")
	public ResponseEntity<?> addJob(@PathVariable String group, @RequestBody Job job){
		try {
			Set<Trigger> triggers = QuartzEntityFactory.buildTriggers(job);
			JobDetail jobDetail = QuartzEntityFactory.buildJobDetail(job);
			if (triggers.isEmpty()){
				scheduler.addJob(jobDetail, false);
			}else{
				scheduler.scheduleJob(jobDetail, triggers, false);
			}
			URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{name}").buildAndExpand(job.getName()).toUri();					
			return ResponseEntity.created(location).build();
		} catch (SchedulerException e) {
			throw new SchedulerApiException("Cannot create job " + job, e);
		}
	}
	
	@GetMapping("/groups/{group}/jobs/{name}")
	public Optional<Job> getJob(@PathVariable String group, @PathVariable String name){
		try {
			JobDetail jobDetail = scheduler.getJobDetail(new JobKey(name, group));
			if (jobDetail == null) {
				throw new EntityNotFoundException("Job [" + name + "] not found.");
			}						
			return Optional.of(new Job.Builder().from(jobDetail, (List<Trigger>) scheduler.getTriggersOfJob(jobDetail.getKey())).build());
		} catch (SchedulerException | CloneNotSupportedException e) {
			throw new SchedulerApiException("Cannot get job " + name + " in " + group, e);
		} 
	}

	/*
	 * $ curl -XDELETE localhost:8080/groups/tgroup/jobs/test2
	 */
	@DeleteMapping("/groups/{group}/jobs/{name}")
	public void deleteJob(@PathVariable String group, @PathVariable String name){
		try {
			scheduler.deleteJob(new JobKey(name, group));
		} catch (SchedulerException e) {
			throw new SchedulerApiException("Cannot delete job " + name + " in " + group, e);
		}
	}
	
	@GetMapping("/groups/{group}/jobs/{job}/triggers/{trigger}")
	public Optional<JobTrigger> getTrigger(@PathVariable String group, @PathVariable String job, @PathVariable String trigger){
		try {
			Trigger t = scheduler.getTrigger(new TriggerKey(trigger));
			if (t == null) {
				throw new EntityNotFoundException("Trigger [" + trigger + "] of [" + job +  "] not found.");
			}
			return Optional.of(new JobTrigger.Builder().from(t).build());
		} catch (SchedulerException e) {
			throw new SchedulerApiException("Cannot get trigger " + trigger + " in " + job, e);
		}
		
	}
	
	@DeleteMapping("/groups/{group}/jobs/{job}/triggers/{trigger}")
	public void deleteTrigger(@PathVariable String group, @PathVariable String job, @PathVariable String trigger){
		try {
			scheduler.unscheduleJob(new TriggerKey(trigger, null));
		} catch (SchedulerException e) {
			throw new SchedulerApiException("Cannot suspend job scheduler " + job + "/" + trigger + " in " + group, e);
		}
	}
	
	@PostMapping("/groups/{group}/jobs/{job}/triggers")
	public ResponseEntity<?> addTrigger(@PathVariable String group, @PathVariable String job, @RequestBody JobTrigger trigger) {
		Trigger t;
		try {
			JobDetail jobDetail = scheduler.getJobDetail(new JobKey(job, group));
			if (jobDetail == null){
				throw new EntityNotFoundException("Job [" + job + "] not found.");
			}			
			t = QuartzEntityFactory.buildTrigger(new Job.Builder().from(jobDetail).build(), trigger);
			scheduler.scheduleJob(jobDetail, t);
			URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{trigger}").buildAndExpand(t.getKey().getName()).toUri();					
			return ResponseEntity.created(location).build();			
		} catch (SchedulerException | CloneNotSupportedException e){
			throw new SchedulerApiException("Cannot create trigger for " + job + " in " + group, e);
		}				
	}
	
	@PostMapping("/groups/{group}/jobs/{job}/triggers/{trigger}")
	public ResponseEntity<?> reschedule(@PathVariable String group, @PathVariable String job, @PathVariable String trigger, @RequestBody JobTrigger jt){
		try {
			JobDetail jobDetail = scheduler.getJobDetail(new JobKey(job, group));
			if (jobDetail == null){
				throw new EntityNotFoundException("Job [" + job + "] not found.");
			}			
			Trigger nt = QuartzEntityFactory.buildTrigger(new Job.Builder().from(jobDetail).build(), jt);
			// delete old trigger and create new
			scheduler.rescheduleJob(new TriggerKey(trigger), nt);
			URI location = ServletUriComponentsBuilder.fromCurrentContextPath().path("/scheduler/groups/{group}/jobs/{job}/triggers/{trigger}")
					.buildAndExpand(group, job, nt.getKey().getName()).toUri();
			return ResponseEntity.created(location).build();
		} catch (SchedulerException | CloneNotSupportedException e) {
			throw new SchedulerApiException("Cannot reschedule trigger[" + trigger + "] for " + job + " in " + group, e);
		}		
	}
	
	@PostMapping("/groups/{group}/jobs/{job}/execute")
	public void execute(@PathVariable String group, @PathVariable String job){
		try {
			JobDetail jobDetail = scheduler.getJobDetail(new JobKey(job, group));
			if (jobDetail == null){
				throw new EntityNotFoundException("Job [" + job + "] not found.");
			}
			Trigger trigger = TriggerBuilder.newTrigger()
					.forJob(jobDetail)
				    .withIdentity(job.concat("-once"), "group")
				    .startNow()
				    .build();	
			scheduler.scheduleJob(trigger);
		} catch (SchedulerException e) {
			// TODO Auto-generated catch block
			throw new SchedulerApiException("Cannot execute job [" + job + "] in " + group, e);
		}
		
	}
	
}
