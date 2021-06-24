package org.breitling.dragon.framework.test;

import com.hazelcast.core.HazelcastInstance;
import org.junit.Test;
import org.breitling.dragon.framework.types.SimpleTest;
import org.breitling.dragon.framework.util.HazelcastUtils;
import org.junit.After;
import static org.junit.Assert.assertNotNull;
import org.junit.Before;

public class TestHazelcastUtils extends SimpleTest
{
    private HazelcastInstance instance;
    
//  TEST CASES
    
    @Test
    public void testcreateTestInstance_DefaultNames_Instance()
    {
        instance = HazelcastUtils.createTestInstance();
        
        assertNotNull(instance);
    }
    
    @Test
    public void testcreateTestInstance_Names_Instance()
    {
        instance = HazelcastUtils.createTestInstance("MyCluster", "MyCaching");
        
        assertNotNull(instance);
    }
    
    @After
    public void testCaseTearDown()
    {
        instance.shutdown();
        instance = null;
        super.testCaseTearDown();
    }
}
