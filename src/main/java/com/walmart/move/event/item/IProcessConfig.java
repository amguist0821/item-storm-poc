package com.walmart.move.event.item;

/**
 * Interface will be utilized in order to provide the ability to reference those
 * configurations which are needed at runtime for a given event processor,
 * 
 * @author amguist
 *
 */
public interface IProcessConfig {

	/**
	 * Function will be utilized in order to provide the ability to retrieve
	 * back to the calling method the end point ( Zookeeper End Point ) which
	 * a Kafka Spout is to be linked to for consuming messages.
	 * 
	 * @return Zookeeper Host
	 */
	String	getZookeeperHost();
	
	/**
	 * Function will be utilized in order to retrieve back to the calling method
	 * the Topic which the Processor is to consume messages from thus allowing for
	 * the ability to receive those messages.
	 * 
	 * @return Listening Topic For Kafka Spout
	 */
	String	getListeningTopic();
	
	
}
