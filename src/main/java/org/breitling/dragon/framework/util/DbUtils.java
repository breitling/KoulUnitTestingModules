package org.breitling.dragon.framework.util;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Savepoint;
import java.sql.Statement;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * utility class for tests
 * 
 * @author BBreitling
 * 
 */

public class DbUtils
{	
	private static Logger LOG = LoggerFactory.getLogger(DbUtils.class);
	
	private static DataSource 	dataSource;
	private static Connection 	h2Connection;
	private static boolean 		initialized = false;
	
	private DbUtils()
	{    
	}
	
//  public static METHODS
	
	public static void closeQuietly(ResultSet rs)
	{
	    try
	    {
	        rs.close();
	    }
	    catch (Exception e)
	    {
	    	LOG.error("leaked resultset", e);
	    }
	}
	
	public static void closeQuietly(Statement ps)
	{
	    try
	    {
	        ps.close();
	    }
	    catch (Exception e)
	    {	        
	    	LOG.error("leaked statement", e);
	    }
	}
	
	public static void commit()
	{
	    try
	    {
	        h2Connection.commit();
	    }
	    catch (Exception e)
	    {
	        throw new RuntimeException("DB error: commit failed: " + e.toString());
	    }
	}
	
	public static void setUp() throws Exception
	{
		try
		{
			if (dataSource != null)
			{
				h2Connection = dataSource.getConnection();
				initialized = true;
			}
		}
		catch (Exception e)
		{
			LOG.error("Failed to initialize database.", e);
		}
	}
	
	public static Connection getConnection()
	{
		return h2Connection;
	}
	
	public static void setConnection(Connection connection)
	{
	    h2Connection = connection;
	}
	
	public static DataSource getDataSource()
	{
		return dataSource;
	}
	
	public static void setDataSource(DataSource ds)
	{
		dataSource = ds;
	}
	
	public static Boolean isInitialized()
	{
		if (initialized)
			return Boolean.TRUE;
		else
			return Boolean.FALSE;
	}
	
	public static void injectObject(final Object o, final String fieldName, final Object value) throws RuntimeException
	{
		try
		{
			Object unwrapped = AopSupport.unwrapProxy(o);
			Class<?> klass = unwrapped.getClass();
			Field field = null;
			
			if ((field = klass.getDeclaredField(fieldName)) != null)
			{
				field.setAccessible(true);
				field.set(unwrapped, value);
			}
		}
		catch (NoSuchFieldException nsfe)
		{
			LOG.error("Error: missing field: " + fieldName);
			throw new RuntimeException("missing field " + fieldName);
		}
		catch (IllegalAccessException iae)
		{
			LOG.error("Error: can not access " + fieldName);
			throw new RuntimeException("can not access " + fieldName);
		}
		catch (Exception e)
		{
			LOG.error(e.toString());
			throw new RuntimeException("injection failed for " + fieldName);
		}
	}
	
	public static void rollback(Savepoint sp)
	{
	    try
	    {
	        h2Connection.rollback(sp);
	    }
	    catch (Exception e)
	    {
	        throw new RuntimeException("DB error on rollback: " + e.toString());
	    }
	}
	
	public static void setAutoCommit(Boolean b)
	{
	    try
	    {
	        h2Connection.setAutoCommit(b);
	    }
	    catch (Exception e)
	    {
	        throw new RuntimeException("DB error: failed to set auto commit: " + e.toString());
	    }
	}
	
	public static void tearDown() throws Exception
	{
		h2Connection.close();
	}
}
