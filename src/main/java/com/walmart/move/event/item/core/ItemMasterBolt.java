package com.walmart.move.event.item.core;

import org.apache.log4j.Logger;

import backtype.storm.topology.BasicOutputCollector;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseBasicBolt;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Tuple;

/**
 * Class will receive input from the Item Filter in order to to determine whether or not 
 * information needs to be published back to the Item Master based on where the message
 * originated from.  Since all events are initially being published to a specific topic
 * based on a given Domain, then we need to make sure that things are properly routed
 * correctly.
 * 
 * @author amguist
 *
 */
public class ItemMasterBolt extends BaseBasicBolt {

	/*
	 * Class Member Variables
	 */
	private static final long			serialVersionUID	=		1L;
	private static final Logger			logger				=		Logger.getLogger(ItemMasterBolt.class);
	
	private static final String			MASTER_ORIGIN		=		"Master";
	private static final String			EVENT_OBJECT		=		"event_object";
	private static final String			CORRELATION_ID		=		"correlation_identifier";
	
	/**
	 * Function is utilized in order to define those fields which are come out
	 * from the processing of the initial event notification that was received 
	 * over Apache Kafka and which have not been generated from a Next Generation
	 * product.
	 */
	public void declareOutputFields(OutputFieldsDeclarer outputDeclarer) { 
		outputDeclarer.declare(new Fields("correlation_identifier", "item_object"));
	}
	
	/**
	 * Function will examine the origin of the message in order to determine whether or not
	 * the message needs to be formatted and published to the Item Master Bolt, which would 
	 * send item updates to the Item Master first, else push information to the next Bolt which 
	 * will then process the information and push to multiple bolts which require the item
	 * update for their local copy of item.
	 */
	public void execute(Tuple input, BasicOutputCollector collector) {
		String	eventOrigin = input.getString(input.fieldIndex("event_origin"));
		String  eventObject = input.getString(input.fieldIndex(EVENT_OBJECT));
		String  correlationId = input.getString(input.fieldIndex(CORRELATION_ID));
		
		logger.info("Item Event Notification Orgin is: " + eventOrigin);
		if( !eventOrigin.equals(MASTER_ORIGIN)) {
			logger.info("[" + correlationId + "] - " + "Process is needing to send a notification to Item Master for master update ....");
			logger.info("[" + correlationId + "] - " + "Event Object: " + eventObject);
			
		}
	}
}
