package com.walmart.move.event.item.core;

import com.walmart.move.event.item.FileBasedWebConfig;

import backtype.storm.Config;
import backtype.storm.LocalCluster;
import backtype.storm.StormSubmitter;
import backtype.storm.spout.SchemeAsMultiScheme;
import backtype.storm.topology.TopologyBuilder;
import storm.kafka.KafkaSpout;
import storm.kafka.SpoutConfig;
import storm.kafka.StringScheme;
import storm.kafka.ZkHosts;

/**
 * Class will contain the definition which is to be utilized in order to provide the ability to
 * process item events which are to be streamed through a Kafka based message infrastructure, thus
 * allowing for the ability to perform various actions against those events.
 * 
 * @author amguist
 *
 */
public class KafkaEventItemTopology {

	/**
	 * Function is executed upon the deployment of the Topology thus
	 * establishing the Spout, which in this case is a Kafka Broker
	 * consumer, along with those bolts which will be utilized for 
	 * performing some type of actions against each emitted result
	 * set.
	 * 
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		TopologyBuilder topology = new TopologyBuilder();
		FileBasedWebConfig configuration = new FileBasedWebConfig(args);
		
		// Kafka Spout - Kafka Consumer
		topology.setSpout("itemListener", new KafkaSpout(getConfiguration(configuration)), 4);
		
		// Item Filter Bolt
		topology.setBolt("item_filter", new ItemFilterBolt(), 4).shuffleGrouping("itemListener");
		
		/*
		 * At this point all that we have retained from the original 
		 * event notification object are the correlation identifier, 
		 * event type, and event object pay load which is to be processed
		 * and sent to other products.
		 * 
		 * Please note that there are going to be multiple bolts which
		 * are going to be consuming the the item_filter, however, based on 
		 * the origin of the message then different actions will be 
		 * executed.  For example, if an update on an item was performed
		 * in one of the Next Generation products such as Shipping or 
		 * Receiving then we don't want to just publish those events immediately
		 * to those subscribers, but we need to have the master perform
		 * the necessary update first, and then have the master publish
		 * an item event which would then update everyone else.  
		 */
		topology.setBolt("item_master", new ItemMasterBolt()).shuffleGrouping("item_filter");
		
		//topology.setBolt("receivingItem", new ItemReceivingBolt()).shuffleGrouping("item_filter");
		//topology.setBolt("inventoryItem", new ItemInventoryBolt()).shuffleGrouping("item_filter");
		
		if (args != null && args.length > 0 && args[0] != "unitTest")
        {
            StormSubmitter.submitTopology("item-event-processor", createConfig(false), topology.createTopology());
        }
        else
        {
            LocalCluster cluster = new LocalCluster();
            cluster.submitTopology("item-event-processor", createConfig(true), topology.createTopology());
            Thread.sleep(60000);
            cluster.shutdown();
        }
	}
	
	/**
	 * Function will be utilized in order to create the required configuration which 
	 * is needed in order to setup the streaming process which is going to be
	 * utilized for processing those Item Events.
	 * 
	 * @param local
	 * 
	 * @return Storm Configuration
	 */
	private static Config createConfig(boolean local)
    {
        Config conf = new Config();
        if (local) {
            conf.setMaxTaskParallelism(2);
        }
        else {
            conf.setNumWorkers(2);
        }
        return conf;
    }
	
	/**
	 * Function will be utilized in order to setup the necessary configuration
	 * which is going to be required in order to allow for the Spout to listen
	 * for events which are being published to Apache Kafka from any source
	 * that is publishing Item Events.
	 * 
	 * @return Kafka Spout Configuration
	 */
	private static SpoutConfig	getConfiguration(FileBasedWebConfig configuration) {
		ZkHosts zkHosts = new ZkHosts(configuration.getZookeeperHost());
		SpoutConfig spoutConfig = new SpoutConfig(zkHosts, configuration.getListeningTopic(), "", "NextGenItemGroup");
		spoutConfig.scheme = new SchemeAsMultiScheme(new StringScheme());
		return(spoutConfig);
	}
}
