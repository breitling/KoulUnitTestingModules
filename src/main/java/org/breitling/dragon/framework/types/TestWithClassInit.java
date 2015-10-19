package org.breitling.dragon.framework.types;

import javax.sql.DataSource;

import org.junit.AfterClass;

// ABSTRACT BASE CLASS FOR ALL TESTS THAT USE THE DATABASE...

public abstract class TestWithClassInit extends SimpleDataBaseTest
{
	private static String dataSetName = "src/test/datasets/schema.xml";
	
	private static boolean initialized = false;
	
//  BEFORE CLASS METHODS
	
//  ONLY CALLED IT SUPER CLASS CALLS (no @BeforeClass)
	public static void testSetup(final String name)
	{
        SimpleDataBaseTest.testSetup();

        if (name != null && name.length() > 0)
			dataSetName = name;
	}
	
//  ONLY CALLED IT SUPER CLASS CALLS (no @BeforeClass)	
	public static void testSetupUsingClasspath(final String name)
	{
	    SimpleDataBaseTest.testSetup();

        if (name != null && name.length() > 0)
            dataSetName = "classpath:/" + name;
	}
	
//  BEFORE TEST CASE METHODS
	
//  ONLY CALLED IF SUPER CLASS CALLS (no @Before)
	public void testCaseSetup(final DataSource dataSource)
	{
		super.testCaseSetup(dataSource);
		
		if (initialized == false)
		{
			try
			{
				getDatabaseTester().setDataSet(getDataSet(dataSetName));
				getDatabaseTester().onSetup();
				
				initialized = true;
			}
			catch (Exception e)
			{
				throw new RuntimeException("failed to load dataset(" + dataSetName + "): " + e.toString());
			}
		}
	}
	
//  AFTER TEST CASE METHODS
	
//	ONLY CALLED IT SUPER CLASS CALLS (no @After)
	public void testCaseTearDown()
	{
		super.testCaseTearDown();
	}
	
//  AFTER CLASS METHODS
	
	@AfterClass
	public static void testTearDown()
	{
		SimpleDataBaseTest.testTearDown();
		initialized = false;
	}
}
