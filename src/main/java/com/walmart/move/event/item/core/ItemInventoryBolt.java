package com.walmart.move.event.item.core;

import org.apache.log4j.Logger;

import backtype.storm.topology.BasicOutputCollector;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseBasicBolt;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Tuple;

/**
 * Class will contain the implementation which is required in order to perform the 
 * necessary transformation of the Item Pay Load to that which is needed for the
 * Inventory Product.
 * 
 * @author amguist
 *
 */
public class ItemInventoryBolt extends BaseBasicBolt {

	/*
	 * Class Member Variables
	 */
	private static final long		serialVersionUID	=		1L;
	private static final Logger		logger				=		Logger.getLogger(ItemReceivingBolt.class);
	
	private static final String		MASTER_ORIGIN		=		"Master";
	
	/**
	 * Function is utilized in order to define those fields which are come out
	 * from the processing of the initial event notification that was received 
	 * over Apache Kafka and which have not been generated from a Next Generation
	 * product.
	 */
	public void declareOutputFields(OutputFieldsDeclarer outputDeclarer) { 
		outputDeclarer.declare(new Fields("correlation_identifier", "inventory_item_object"));
	}
	
	/**
	 * Function will examine the origin of the message in order to determine whether or not
	 * the message is okay for processing based on the origin of the message which could 
	 * be from either the Master or some other source.
	 */
	public void execute(Tuple input, BasicOutputCollector collector) {
		String	eventOrigin = input.getString(input.fieldIndex("event_origin"));
		
		logger.info("Item Event Notification Orgin is: " + eventOrigin);
		if( !eventOrigin.equals(MASTER_ORIGIN)) {
			logger.info("Process is needing to update the format of the current pay load in order to meet the needs of the Inventory Product ....");
			
			
		}
	}
}
