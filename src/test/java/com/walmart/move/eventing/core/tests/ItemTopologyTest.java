package com.walmart.move.eventing.core.tests;

import java.util.Date;
import java.util.Properties;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.walmart.move.event.item.core.Item;
import com.walmart.move.event.item.core.KafkaEventItemTopology;
import com.walmart.move.eventing.core.EventNotification;

import kafka.javaapi.producer.Producer;
import kafka.producer.KeyedMessage;
import kafka.producer.ProducerConfig;

/**
 * Class will be utilized in order to perform testing against the Item
 * Topology which would provide the ability to test against an Embedded
 * Instance Of Zookeeper, Kafka, and Storm.
 * 
 * @author amguist
 *
 */
public class ItemTopologyTest {

	/*
	 * Class Member Variables
	 */
	private	TestKafkaCluster		kafka;
	private GsonSerializer<EventNotification<Item>>	serializer = new GsonSerializer<EventNotification<Item>>();
	
	/*
	 * Class Static Constants
	 */
	private static final String		mdcCorrelationId	=	"correlationId";
	
	/**
	 * Function will be invoked for each test case in order to start up
	 * an instance of Apache Kafka for a given Topology to start consuming messages
	 * and providing input based on those events.
	 * 
	 * @throws Exception
	 */
	@Before
	public void init() throws Exception {
		kafka = new TestKafkaCluster();
		org.slf4j.MDC.put(mdcCorrelationId, "1");
	}
	
	/**
	 * Function will provide the ability to test against the Storm
	 * Item Topology and a Kafka Instance.
	 * 
	 * @throws Exception
	 */
	@SuppressWarnings("static-access")
	@Test
	public void testItemTopology() throws Exception {
		String	topic = "US.06067.ITEM";
		this.kafka.addTopic(topic);
		
		// setup producer
        Properties producerProperties	=	new Properties();
        producerProperties.put("zookeeper.host", this.kafka.getZkConnectString());
        producerProperties.put("metadata.broker.list", this.kafka.getKafkaBrokerString());
        producerProperties.put("serializer.class", "kafka.serializer.StringEncoder");
		
        ProducerConfig config = new ProducerConfig(producerProperties);
        Producer<String, String> producer = new Producer<String, String>(config);
        
        //send one message to local kafka server:
        for (int i=0; i<10; i++) {
        	KeyedMessage<String,String> message = new KeyedMessage<String,String>(topic, createEventNotification());
        	producer.send(message);
        }
        producer.close();
		
		String[]	args	=	new String[3];
		args[0] = "unitTest";
		args[1] = this.kafka.getZkConnectString();
		args[2] = "US.06067.ITEM";
		
		KafkaEventItemTopology	itemTopology	=	new KafkaEventItemTopology();
		itemTopology.main(args);   
	}
	
	/**
	 * Function will be utilized in order to return back to the calling method an example of an Event Notification Object
	 * based on having some Item information that is present within the Payload.
	 * 
	 * @return
	 */
	private String	createEventNotification() {
		Item	exampleItem	= new Item();
		exampleItem.setWalmartItemNumber(2939299201L);
		
		EventNotification<Item> eventNotification = new EventNotification<Item>(exampleItem, "testUser", "test.event", new Date());
		return(serializer.convert(eventNotification));
	}
	
	/**
	 * Function will be utilized in order to shut down an instance
	 * of Apache Zookeeper and Kafka which have been embedded as
	 * per testing.
	 * 
	 * @throws Exception
	 */
	@After
	public void cleanup() throws Exception {
		if( this.kafka != null ) {
			this.kafka.stop();
		}
		org.slf4j.MDC.remove(mdcCorrelationId);
	}
	
	/**
	 * Class is to handle the conversion of a given Message to that of a Gson 
	 * format so that the Gson would be the message payload string for any test.
	 * 
	 * @author amguist
	 *
	 * @param <T>
	 */
	private static final class GsonSerializer<T> {
		private static final 	String	dateFormat = "dd MMM yyyy HH:mm:ss";			// Format required for JSON Conversion
		private	final Gson	gson	=	new GsonBuilder().setDateFormat(dateFormat).create();
		
		/**
		 * Function will provide the ability to take a given object and convert the
		 * object to a Json String.
		 * 
		 * @param object
		 * @return
		 */
		public String	convert(T object) {
			return(gson.toJson(object));
		}
	}
}
