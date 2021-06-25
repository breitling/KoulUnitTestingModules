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
    private HazelcastUtils(){
    }
    
    public static HazelcastInstance createTestInstance()
    {
        return Hazelcast.newHazelcastInstance(hazelCastConfigBean("localhost", 5701, "TestIMDG", "TestCaching"));
    }
    
    public static HazelcastInstance createTestInstance(String cluster, String instance)
    {   
        return Hazelcast.newHazelcastInstance(hazelCastConfigBean("localhost", 5701, cluster, instance));
    }
    
    public static HazelcastInstance createTestInstance(String host, int port, String cluster, String instance)
    {   
        return Hazelcast.newHazelcastInstance(hazelCastConfigBean(host, port, cluster, instance));
    }

//  PRIVATE METHODS
    
    @SuppressWarnings("deprecation")
    private static com.hazelcast.config.Config hazelCastConfigBean(String host, int port, String clusterName, String instanceName)
    {
        com.hazelcast.config.Config config = new com.hazelcast.config.Config();
        
        config.setClusterName(clusterName);
        config.setInstanceName(instanceName);
        
        NetworkConfig network = config.getNetworkConfig();
        network.setPort(port).setPortCount(10);
        network.setPortAutoIncrement(true);
        
        JoinConfig join = network.getJoin();
        join.getMulticastConfig().setEnabled(false);
        join.getAwsConfig().setEnabled(false);
        
        TcpIpConfig tcp = join.getTcpIpConfig();
        tcp.setEnabled(true);
        tcp.addMember(host);
        
        return config; 
    }
}
