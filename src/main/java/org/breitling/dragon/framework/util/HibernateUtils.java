package org.breitling.dragon.framework.util;

import java.util.HashMap;
import java.util.Map;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HibernateUtils
{
    private static Logger LOG = LoggerFactory.getLogger(HibernateUtils.class);
    
    private static EntityManagerFactory factory = null;
    private static boolean isInitialized = false;
    
    private HibernateUtils()
    {        
    }
    
    public static EntityManagerFactory getFactory(String persistenceUnit) throws Exception
    {
        if (factory == null)
        {
            LOG.debug("EntityManager using " + persistenceUnit + " as persistence unit.");

            Map<String, Object> configs = new HashMap<String, Object>();
            configs.put("connection.driver_class", "org.h2.Driver");
            configs.put("hibernate.connection.url" ,"jdbc:h2:mem:test;DB_CLOSE_DELAY=-1");
            configs.put("hibernate.dialect", "org.hibernate.dialect.H2Dialect");
            configs.put("hibernate.hbm2ddl.auto", "create");        
            configs.put("hibernate.show_sql", "true");
            configs.put("cache.provider", "org.hibernate.cache.internal.NoCacheProvider");

            factory = Persistence.createEntityManagerFactory(persistenceUnit, configs);

            isInitialized = true;
        }
        
        return factory;
    }
    
    public static EntityManager getEntityManager(String persistenceUnit) throws Exception
    {
        if (factory == null)
            factory = getFactory(persistenceUnit);
        
        return factory.createEntityManager();
    }
    
    public static EntityManagerFactory getInstance()
    {
        return factory;
    }
    
    public static Map<String,Object> getProperties()
    {
        if (factory != null)
            return factory.getProperties();
        else
            return null;
    }
    
    public static Boolean isInitialized()
    {
        return new Boolean(isInitialized);
    }
    
    public static void close()
    {
        if (factory != null)
        {
            factory.close();
            
            factory = null;
            isInitialized = false;
        }
    }
}
