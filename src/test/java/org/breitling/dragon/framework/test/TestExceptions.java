package org.breitling.dragon.framework.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import javax.sql.DataSource;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.breitling.dragon.framework.types.TestWithClassInit;
import org.breitling.dragon.framework.types.TestWithMethodInit;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:context.xml"})
@Category(org.breitling.dragon.framework.category.ValidationTest.class)
public class TestExceptions extends TestWithMethodInit
{
    @Autowired
    private DataSource dataSource;
 
    @BeforeClass
    public static void testSetUp()
    {
        TestWithClassInit.addSchema("classpath:/test.schema");
        TestWithClassInit.testSetup("classpath:/test.xml");
    }
    
    @Before
    public void testCaseSetup()
    {
        super.testCaseSetup(dataSource);
    }
    
    @Test
    public void testDUTFramework_throwsNullDataSourceMessage_Exception()
    {
        try
        {
            super.testCaseSetup(null);
            fail("should not get here.");
        }
        catch (RuntimeException rte)
        {
            assertEquals("java.lang.RuntimeException: DataSource is null", rte.toString().substring(0, 46));
        }
    }
    
    @Test
    public void testDUTFramework_throwsExceptionWithMissingSchemaName_Exception()
    {
        try
        {
            super.testCaseSetup(dataSource);           
            validateDB(null);
            fail("should not get here");
        }
        catch (IllegalArgumentException rte)
        {
            assertEquals("java.lang.IllegalArgumentException: missing schema name", rte.toString().substring(0, 55));
        }
    }

    @Test
    public void testDUTFramework_throwsExceptionWithBadSchema_Exception()
    {
        try
        {            
            validateDBTableName(null, null);
            fail("should not get here");
        }
        catch (IllegalArgumentException rte)
        {
            assertEquals("java.lang.IllegalArgumentException: missing schema name", rte.toString().substring(0, 55));
        }
    }

    @Test
    public void testDUTFramework_throwsExceptionWithBadTableName_Exception()
    {
        try
        {            
            validateDBTableName("PUBLIC", null);
            fail("should not get here");
        }
        catch (IllegalArgumentException rte)
        {
            assertEquals("java.lang.IllegalArgumentException: missing table name", rte.toString().substring(0, 54));
        }
    }


    @Test
    public void testDUTFramework_throwsExceptionFromAddingSchemaNullFilename_Exception()
    {
        try
        {
            TestWithMethodInit.addSchema(null);
            fail("should not get here");
        }
        catch (IllegalArgumentException iae)
        {
            assertEquals("java.lang.IllegalArgumentException: Bad file name", iae.toString());
        }        
    }
    
    @Test
    public void testDUTFramework_throwsExceptionFromHavingSchemaFailure_Exception()
    {
        try
        {
            super.testCaseSetup(dataSource);
            super.addSchemaToDB("src/test/datasets/basemissing.schema");            
            fail("should not get here.");
        }
        catch (RuntimeException rte)
        {
            assertEquals("java.lang.RuntimeException: failed to execute database script", rte.toString().substring(0, 61));
        }
    }
}
