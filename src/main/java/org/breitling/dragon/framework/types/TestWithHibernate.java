package org.breitling.dragon.framework.types;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import org.breitling.dragon.framework.util.HibernateUtils;
import org.hibernate.SessionFactory;
import org.junit.AfterClass;

public abstract class TestWithHibernate extends SimpleDataBaseTest 
{
    private static String dataSetName = null;  
    private static EntityManagerFactory emf = null;
    
    private static boolean initialized = false;
        
    public static void testSetup(final String persistenceUnit)
    {
        SimpleDataBaseTest.testSetup();
        
        try
        {
            emf = HibernateUtils.getFactory(persistenceUnit);
        }
        catch (Exception e)
        {
            emf = null;
            e.printStackTrace();
        }
    }
    
    public static void testSetup(final String persistenceUnit, final String name)
    {
        TestWithHibernate.testSetup(persistenceUnit);
        
        if (name != null && name.length() > 0)
            dataSetName = name;
    }
    
    public void testCaseSetup()
    {
        super.testCaseSetup((SessionFactory) getSessionFactory());
        
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
    
    public static EntityManager getEntityManager()
    {
        if (emf != null)
            return emf.createEntityManager();
        else
            throw new RuntimeException("no entity manager factory!");
    }
    
    public static SessionFactory getSessionFactory()
    {
        if (emf != null)
            return emf.unwrap(SessionFactory.class);
        else
            throw new RuntimeException("no entity manager factory!");
    }
    
    public static void initializeDBSchemaForClass(Class<?> klass)
    {
        EntityManager em = getEntityManager();

        try
        {
            em.getTransaction().begin();
            em.persist(klass.newInstance());
            em.getTransaction().commit();
        }
        catch (Exception e)
        {
            throw new RuntimeException("Failed to initialized DB schema: " + e.toString());
        }
    }
    
    public static Boolean installPersistenceUnit(final String src, final String dst)
    {
        boolean rc = true;
        
        try
        {
            Files.copy(new File(src).toPath(), new File(dst).toPath(), StandardCopyOption.REPLACE_EXISTING);
        }
        catch (Exception e)
        {
            rc = false;
        }
        
        return rc;
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

        HibernateUtils.close();
        
        dataSetName = null;
        emf = null;
        initialized = false;
    }
}
