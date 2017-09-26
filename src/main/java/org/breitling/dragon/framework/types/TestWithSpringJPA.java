package org.breitling.dragon.framework.types;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.breitling.dragon.framework.util.DragonSpringJPAConfig;
import org.hibernate.SessionFactory;
import org.junit.AfterClass;
import org.springframework.beans.factory.annotation.Autowired;


public abstract class TestWithSpringJPA extends SimpleDataBaseTest 
{
	@PersistenceContext
    private EntityManager em;
	
	@Autowired
	private DragonSpringJPAConfig config;
	
	private static boolean initialized = false;

	private static String dataSetName = null;
	
	
    public static void testSetup(final String name)
    {
    	SimpleDataBaseTest.testSetup();
    	
        if (name != null && name.length() > 0)
            dataSetName = name;
    }
    
    public void testCaseSetup()
    {
    	super.testCaseSetup(config.getDataSource());
    	
        try
        {
            if (initialized == false)
            {
                if (dataSetName != null)
                {
                    getDatabaseTester().setDataSet(getDataSet(dataSetName));
                    getDatabaseTester().onSetup();
                    
                    initialized = true;
                }
            }
        }
        catch (Exception e)
        {
            throw new RuntimeException("failed to load dataset(" + dataSetName + "): " + e.toString());
        }
    }    
    
//  @After
    public void testCaseTearDown()
    {
        super.testCaseTearDown();
    }
   
    @AfterClass
    public static void testTearDown()
    {
        SimpleDataBaseTest.testTearDown();

        dataSetName = null;
        initialized = false;
    }
    
//  PUBLIC METHODS
    
    public EntityManager getEntityManager()
    {
    	return em;
    }
    
    public SessionFactory getSessionFactory()
    {
        if (em != null)
            return em.unwrap(SessionFactory.class);
        else
            throw new RuntimeException("no entity manager factory!");
    }
}
