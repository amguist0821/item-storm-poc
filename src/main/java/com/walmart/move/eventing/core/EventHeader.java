package com.walmart.move.eventing.core;

import java.util.Date;
import java.util.UUID;

import javax.xml.bind.annotation.XmlRootElement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class contains those information variables which are required in order to
 * send messages over the Walmart Network to Event Brokers which are being consumed
 * by those Event Consumers.
 * 
 * @author amguist
 *
 */
@XmlRootElement()
public class EventHeader {

	/*
	 * Class Member Variables
	 */
	private	String	id;
	private String	name;
	private String	type;
	private String	user;
	private Date	timestamp = new Date();
	private String	checksum;
	
	private static final Logger		logger	=	LoggerFactory.getLogger(EventHeader.class);
	
	/*
	 * Class Static Constants
	 */
	private static final String		mdcCorrelationId	=	"correlationId";
	
	/**
	 * Function is defined as being the default class constructor that is to be
	 * utilized in order to initialize the Event Header object to it's initial
	 * state of being.
	 */
	public EventHeader() {
		
	}
	
	/**
	 * Function is to be utilized in order to provide the ability to initialize
	 * all of the required values of a given Event Notification object, thus
	 * providing the ability to make sure that the Header Section is complete.
	 * 
	 * @param user
	 * @param event
	 * @param timestamp
	 * @param checksum
	 */
	public EventHeader(String user, String event, Date timestamp, String checksum) {
		this.id = UUID.randomUUID().toString();
		this.user = user;
		this.name = event;
		this.timestamp = timestamp;
		this.checksum = checksum;
	}
	
	/**
	 * Function is defined as being the overriding class constructor which is being
	 * utilized in order to initialize the Event Header that is required for all
	 * event messages being passed within the Supply Chain.
	 * 
	 * @param user
	 * @param event
	 */
	public EventHeader(String user, String event) {
		this(user, event, new Date(), generateCheckSum());
	}
	

	
	/**
	 * Function will provide the ability to generate a new Check Sum value that
	 * is to be associated with an Event Notification from within the Next Generation
	 * Supply Chain.
	 * 
	 * @return Generated Check Sum 
	 */
	private static String	generateCheckSum() {
		String checkSum = org.slf4j.MDC.get(mdcCorrelationId);
		if(checkSum == null || checkSum.isEmpty()) {
			logger.warn("Checksum is not currently set ... did you forget to issue org.slf4j.MDC.put(\"{}\".. from a Servlet request filter?");
		}
		return(checkSum);
	}
	
	/**
	 * Function will return back to the calling method the unique identifier
	 * which is assigned to an Event Notification object upon being created from
	 * within any type of product within the Next Generation Supply Chain.
	 * 
	 * @return
	 */
	public String	getId() {
		return(this.id);
	}
	
	/**
	 * Function will return back the class name of the object that is 
	 * being transferred to the Event Framework from any of the products
	 * within the Next Generation Supply Chain.
	 * 
	 * @return Name Of Event Object
	 */
	public String	getName() {
		return(this.name);
	}
	
	/**
	 * Function will return back the type of event that is being process
	 * such as business, system, alert, etc. in order to provide the 
	 * direction on event types.
	 *  
	 * @return Event Type
	 */
	public String	getType() {
		return(this.type);
	}
	
	/**
	 * Function will return back to the calling method the time at which
	 * a given Event Message was generated.
	 * 
	 * @return Event Generation Time
	 */
	public Date	getTimestamp() {
		return(this.timestamp);
	}
	
	/**
	 * Function will be utilized in order to return back to the calling method
	 * the check sum identifier that is to be utilized in order to keep all of
	 * the messages within a given checksum together.
	 *  
	 * @return Event Check Sum
	 */
	public String	getChecksum() {
		return(this.checksum);
	}
	
	/**
	 * Function will be utilized in order to return back to the calling method the 
	 * user who has performed some type of action against a domain thus generating
	 * an event.
	 * 
	 * @return Event User Generator
	 */
	public String	getUser() {
		return(this.user);
	}
}
