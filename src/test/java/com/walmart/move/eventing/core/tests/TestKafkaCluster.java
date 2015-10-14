package com.walmart.move.eventing.core.tests;

import java.util.Arrays;
import java.util.Properties;

import org.I0Itec.zkclient.ZkClient;
import org.apache.curator.test.TestingServer;

import kafka.admin.AdminUtils;
import kafka.common.AppInfo;
import kafka.server.KafkaConfig;
import kafka.server.KafkaServer;
import kafka.utils.MockTime;
import kafka.utils.TestUtils;
import kafka.utils.ZKStringSerializer$;

/**
 * Class wraps a test kafka-zookeeper and kafka component to provide a simple and common
 * mechanism for starting TEST-ONLY embedded kafka/zookeeper instances on-demand.
 * 
 * @author amguist
 *
 */
public class TestKafkaCluster {

	/*
	 * Class Member Variables
	 */
	private MyKafkaServerStartable	kafkaServer;
	private	TestingServer			zkServer;
	private	ZkClient				zkClient;
	
	/**
	 * Function is defined as being the default class constructor that is to be utilized in order
	 * to create a new Kafka Cluster which is to be utilized in order to test against those
	 * features of Apache Storm.
	 */
	public	TestKafkaCluster() throws Exception {
		zkServer = new TestingServer();
		
		KafkaConfig config = getKafkaConfig(zkServer.getConnectString());
		this.zkClient = new ZkClient(zkServer.getConnectString(), 30000, 30000, ZKStringSerializer$.MODULE$);
		
		this.kafkaServer = new MyKafkaServerStartable(config);
		this.kafkaServer.startup();
	}
	
	/**
	 * Function will utilize the information which is provided within a test 
	 * Zookeeper instance in order to establish the configuration that is needed
	 * for a Kafka instance.
	 * 
	 * @param zkConnectionString
	 * 
	 * @return Initialized Kafka Configuration
	 */
	private static KafkaConfig	getKafkaConfig(final String zkConnectionString) {
		scala.collection.Iterator<Properties> properties = TestUtils.createBrokerConfigs(1, true).iterator();
		assert properties.hasNext();
		
		Properties props = properties.next();
		assert props.containsKey("zookeeper.connect");
		
		props.put("zookeeper.connect", zkConnectionString);
		return(new KafkaConfig(props));
	}
	
	/**
	 * Function will be returning back to the calling method the Kafka Broker
	 * String that is setup in order to establish communication between the test
	 * cases and an embedded instance of Kafka.
	 * 
	 * @return Kafka Broker Connection String
	 */
	public String	getKafkaBrokerString() {
		return(String.format("localhost:%d", kafkaServer.getConfig().port()));
	}
	
	/**
	 * Function will be returning back to the calling method the Zookeeper
	 * Connection String is utilized in order to establish communication 
	 * to Zookeeper.
	 * @return
	 */
	public String	getZkConnectString() {
		return(zkServer.getConnectString());
	}
	
	/**
	 * Function will return back to the calling method the Kafka Port
	 * that is being utilized in order to communicate to Apache 
	 * Kafka.
	 * 
	 * @return Kafka Port
	 */
	public int	getKafkaPort() {
		return(this.kafkaServer.getConfig().port());
	}
	
	/**
	 * Function will be utilized in order to stop both the Embedded instance
	 * of Apache Kafka and Zookeeper.
	 * 
	 * @throws Exception
	 */
	public void stop() throws Exception {
		this.kafkaServer.shutdown();
		this.zkServer.stop();
	}
	
	/**
	 * Adds a topic on-the-fly for testing
	 * 
	 * @param topic
	 * @throws InterruptedException
	 */
	public synchronized void addTopic(String topic) throws InterruptedException {
		if( !AdminUtils.topicExists(zkClient, topic)) {
			AdminUtils.createTopic(zkClient, topic, 1, 1, new Properties());
			TestUtils.waitUntilMetadataIsPropagated(scala.collection.JavaConversions.asScalaBuffer(Arrays.asList(this.kafkaServer.getServer())), topic, 0, 5000);
		}
	}
	
	/**
	 * Class will be utilized by the Kafka Cluster in order to provide the ability 
	 * to create an instance of Apache Kafka which can be utilized for testing
	 * those Publish/Subscribe features.
	 * 
	 * @author amguist
	 *
	 */
	private class	MyKafkaServerStartable {
		
		/*
		 * Class Member Variables
		 */
		private KafkaServer		server;
		private KafkaConfig		serverConfig;
		
		/**
		 * Function is defined as being the default class constructor which can be 
		 * utilized in order to initialize a new instance of Apache Kafka based on
		 * being supplied a Kafka Configuration.
		 * 
		 * @param config
		 */
		MyKafkaServerStartable(KafkaConfig config) {
			this.serverConfig = config;
			this.server = new KafkaServer(config, new MockTime());
		}
		
		/**
		 * Function will return back to the calling method the initialized 
		 * instance of the Kafka Server.
		 * 
		 * @return Kafka Server instance
		 */
		public KafkaServer	getServer() {
			return(this.server);
		}
		
		/**
		 * Function will return back to the calling method the initialized
		 * instance of the Kafka Configuration.
		 * 
		 * @return Kafka Configuration
		 */
		public KafkaConfig	getConfig() {
			return(this.serverConfig);
		}
		
		/**
		 * Function will be utilized in order to start up the initialized instance of
		 * Apache Kafka thus allowing for test cases to run against the instance
		 * and perform various operations.
		 */
		public void startup() {
			try {
				this.server.startup();
				AppInfo.registerInfo();
			} catch( Throwable eX ) {
				System.err.println("Fatal error during MyKafkaStartable startup.  Prepare to shutdown");
				System.exit(1);
			}
		}
		
		public void shutdown() {
			try {
				server.shutdown();
			} catch ( Throwable eX ) {
				System.err.println("Fatal error during MyKafkaStartable shutdown.  Prepare to halt");
				System.exit(1);
			}
		}
	}
}
