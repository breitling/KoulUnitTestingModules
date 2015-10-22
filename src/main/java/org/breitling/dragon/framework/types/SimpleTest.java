package org.breitling.dragon.framework.types;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.breitling.dragon.framework.base.TestBase;

public abstract class SimpleTest extends TestBase
{
    @BeforeClass
    public static void testSetUp()
    {
        TestBase.testSetup();
    }

    @Before
    public void testCaseSetup()
    {
        super.testCaseSetup();
    }
    
    @After
    public void testCaseTearDown()
    {
        super.testCaseTearDown();
    }
    
    @AfterClass
    public static void testTearDown()
    {
        TestBase.testTearDown();
    }
}
