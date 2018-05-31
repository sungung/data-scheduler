package com.sungung.scheduler.api.controller;

import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.http.HttpStatus;

/***
 * 
 * 400 (Bad Request) is the generic client-side error status. Errors can be like malformed request syntax,
 * invalid request message parameters or deceptive request routing etc. The client SHOULD NOT
 * repeat the request without modifications.
 * 
 * 500 (Internal Server Error) is the generic REST API error response. A 500 error is never the client's fault
 * and therefore it is reasonable for the client to retry the exact same request. 
 * 
 * @author spark
 *
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class SchedulerApiException extends RuntimeException {

	public SchedulerApiException(String message) {
		super("Cannot handle request - " + message);
	}
	public SchedulerApiException(String message, Throwable cause) {
		super("Cannot handle request - " + message, cause);
	}


	
}
