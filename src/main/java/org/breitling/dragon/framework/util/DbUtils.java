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
	
	public static Object testPrivateMethod(Object object, String name, Object... args) throws RuntimeException
	{
		Class<?> [] arg_types = new Class [args.length];
		Object returning_object = null;
		
		try
		{
			int n = 0;

			for (Object o : args)
			{
				if (o != null)
				{
					String klass = o.getClass().getName();
					
					if (klass.equals("java.lang.Integer"))
						arg_types[n++] = Integer.TYPE;
					else
					if (klass.equals("java.lang.Long"))
						arg_types[n++] = Long.TYPE;
					else
					if (klass.equals("java.lang.Short"))
						arg_types[n++] = Short.TYPE;
					else
					if (klass.equals("java.lang.Float"))
						arg_types[n++] = Float.TYPE;
					else
					if (klass.equals("java.lang.Double"))
						arg_types[n++] = Double.TYPE;
					else
					if (klass.equals("java.lang.Byte"))
						arg_types[n++] = Byte.TYPE;
					else
					if (klass.equals("java.lang.Char"))
						arg_types[n++] = Character.TYPE;
					else
					if (klass.equals("java.util.ArrayList"))		// HACK!! WHY SHOULD THIS BE NECESSARY?
						arg_types[n++] = Class.forName("java.util.List");
					else
						arg_types[n++] = Class.forName(klass);
				}
				else
				{
					arg_types[n++] = String.class;
				}
			}
		
			Method method = object.getClass().getDeclaredMethod(name, arg_types);
			method.setAccessible(true);
	    
			returning_object = method.invoke(object, args);
		}
		catch (Exception e)
		{
			LOG.error("error invoking private method(" + name + "): " + e.toString());
			throw new RuntimeException(e.getCause() !=  null ? e.getCause().getMessage() 
				                                             : "error invoking private method(" + name + "): " + e.toString());
		}

		return returning_object;
	}
}
