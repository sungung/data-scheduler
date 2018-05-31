package com.sungung.scheduler.api.service;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.quartz.QuartzJobBean;

/***
 * Produce job event 
 * 
 * @author spark
 *
 */
public class SimpleJob extends QuartzJobBean {

	private final static Logger log = LoggerFactory.getLogger(SimpleJob.class);
	
	private String name;
	
	@Override
	protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
		log.info("Starting job - " + name);
	}

}
