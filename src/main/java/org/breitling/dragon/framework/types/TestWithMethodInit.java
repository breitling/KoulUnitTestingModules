package org.breitling.dragon.framework.types;

import javax.sql.DataSource;

import org.dbunit.operation.DatabaseOperation;
import org.junit.After;

public abstract class TestWithMethodInit extends SimpleDataBaseTest 
{    
    private static String dataSetName;

    private static boolean initialized;
    
    
    public static void testSetup(final String name)
    {
        SimpleDataBaseTest.testSetup();

        if (name != null && name.length() > 0)
            dataSetName = name;
    }
    
    public void testCaseSetup(final DataSource dataSource)
    {
        super.testCaseSetup(dataSource);

        try
        {
            if (initialized == false)
            {
                if (dataSetName != null)
                {
                    getDatabaseTester().setDataSet(getDataSet(dataSetName));
                    getDatabaseTester().onSetup();
                }
            }
        }
        catch (Exception e)
        {
            throw new RuntimeException("failed to load dataset(" + dataSetName + "): " + e.toString());
        }
    }
    
    @After
    public void testCaseTearDown()
    {
        try
        {
            if (initialized)
            {
                getDatabaseTester().setTearDownOperation(DatabaseOperation.DELETE);
                getDatabaseTester().setDataSet(getDataSet(dataSetName));
                getDatabaseTester().onTearDown();
            
                getDatabaseTester().getConnection().getConnection().commit();
                
                initialized = false;
            }
        }
        catch (Exception e)
        {
            throw new RuntimeException("failed to clean up after method for dataset(" + dataSetName + "): " + e.toString());
        }

        super.testCaseTearDown();
    }
}
