/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.breitling.dragon.framework.util;

import com.hazelcast.config.JoinConfig;
import com.hazelcast.config.NetworkConfig;
import com.hazelcast.config.TcpIpConfig;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;

/**
 *
 * @author Bob Breitling
 */
public class HazelcastUtils
{
    private static String clusterName;
    private static String instanceName;
    
    private HazelcastUtils(){
    }
    
    public static HazelcastInstance createTestInstance()
    {
        clusterName = "TestIMDG";
        instanceName = "TestCaching";
        
        return Hazelcast.newHazelcastInstance(hazelCastConfigBean());
    }
    
    public static HazelcastInstance createTestInstance(String cluster, String instance)
    {
        clusterName = cluster;
        instanceName = instance;
        
        return Hazelcast.newHazelcastInstance(hazelCastConfigBean());
    }

//  PRIVATE METHODS
    
    @SuppressWarnings("deprecation")
    private static com.hazelcast.config.Config hazelCastConfigBean()
    {
        com.hazelcast.config.Config config = new com.hazelcast.config.Config();
        
        config.setClusterName(clusterName);
        config.setInstanceName(instanceName);
        
        NetworkConfig network = config.getNetworkConfig();
        network.setPort(5701).setPortCount(10);
        network.setPortAutoIncrement(true);
        
        JoinConfig join = network.getJoin();
        join.getMulticastConfig().setEnabled(false);
        join.getAwsConfig().setEnabled(false);;
        
        TcpIpConfig tcp = join.getTcpIpConfig();
        tcp.setEnabled(true);
        tcp.addMember("localhost");
        
        return config; 
    }
}
