package com.walmart.move.event.item;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.apache.commons.configuration.CompositeConfiguration;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;

/**
 * Class is needed in order to add application level properties at runtime, thus
 * allowing for easier configuration of application preferences.
 * 
 * @author amguist
 *
 */
@Singleton
@Named
public class FileBasedWebConfig implements IProcessConfig {

	/*
	 * Class Member Variables
	 */
	private	Configuration	config		=		null;
	private String[]		arguments;
	
	/**
	 * Function is defined as being the default class constructor that is to be 
	 * utilized in order to initialize a given product to it's initial state of
	 * being.
	 * 
	 * @throws ConfigurationException
	 */
	@Inject
	public FileBasedWebConfig(String[] args) throws ConfigurationException {
		String	envStatus	=	System.getProperty("envStatus", "dev");
		String	propFilePath =	envStatus + "/app.properties";
		
		CompositeConfiguration	cfg	= new CompositeConfiguration();
		cfg.addConfiguration(new PropertiesConfiguration(propFilePath));
		
		this.config = cfg;
		this.arguments = args;
	}
	
	/**
	 * Function will be utilized in order to provide the ability to retrieve
	 * back to the calling method the end point ( Zookeeper End Point ) which
	 * a Kafka Spout is to be linked to for consuming messages.
	 * 
	 * @return Zookeeper Host
	 */
	public String	getZookeeperHost() {
		if (this.arguments == null) {
			return(config.getString("zookeeper.host", "localhost:2181"));
		} else {
			return(this.arguments[1]);
		}
	}
	
	/**
	 * Function will be utilized in order to retrieve back to the calling method
	 * the Topic which the Processor is to consume messages from thus allowing for
	 * the ability to receive those messages.
	 * 
	 * @return Listening Topic For Kafka Spout
	 */
	public String	getListeningTopic() {
		if (this.arguments == null) {
			return(config.getString("listening.topic", "US.06067.ITEM"));
		} else {
			return(this.arguments[2]);
		}
	}
}
