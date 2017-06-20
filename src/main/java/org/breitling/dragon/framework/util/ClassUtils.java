/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.breitling.dragon.framework.util;

import java.lang.reflect.Method;

import org.mockito.MockingDetails;
import org.mockito.Mockito;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author bbreitling
 */

public class ClassUtils 
{
    private final static Logger LOG = LoggerFactory.getLogger(ClassUtils.class);
    
    private ClassUtils() {    
    }
    
    public static Object testPrivateMethod(Object object, String name, Object... args) throws RuntimeException
    {
        Class klass = object.getClass();
        Class<?> [] arg_types = new Class [args.length];
        Object returning_object = null;
        
        boolean mocked = isMocked(klass);
        
        try
        {
            int n = 0;

            for (Object o : args)
            {
                if (o != null)
                {
                    Class<?> k = o.getClass();
                    String className = k.getName();

                    arg_types[n++] = getType(k, className, o);
                }
                else
                {
                    arg_types[n++] = String.class;
                }
            }
        
            fix_arg_types(arg_types);
            
            if (mocked)
            {
                Method method = findMockitoClass(object).getDeclaredMethod(name, arg_types);
                method.setAccessible(true);
                    
                returning_object = method.invoke(object, args);
            }
            else
            {
                Method method = klass.getDeclaredMethod(name, arg_types);
                method.setAccessible(true);
                
                returning_object = method.invoke(object, args);
            }
        }
        catch (Exception e)
        {
            LOG.error("error invoking private method(" + name + "): " + e.toString());
            
            throw new RuntimeException(e.getCause() !=  null ? e.getCause().getMessage() 
                                                             : "error invoking private method(" + name + "): " + e.toString());
        }

        return returning_object;
    }
    
    public static Object testPrivateMethodWithTypes(Object object, String name, Object... args) throws RuntimeException
    {
        int real_size = args.length/2;
        
        Class klass = object.getClass();
        Class<?> [] arg_types = new Class [real_size];
        Object [] new_arg_list = new Object [real_size];
        
        Object returning_object = null;
        
        boolean mocked = isMocked(klass);
        
        try
        {
            int n = 0;
            
            for (int m = 0; m < args.length; m = m + 2)
            {
                String typeName = (String) args[m];
                Object o = args[m+1];
                
                new_arg_list[n] = o;
                
                if (o != null)
                    arg_types[n++] = getType(o.getClass(), typeName, o);
                else
                    arg_types[n++] = String.class;
            }
        
            fix_arg_types(arg_types);
            
            if (mocked)
            {
                Method method = findMockitoClass(object).getDeclaredMethod(name, arg_types);
                method.setAccessible(true);
                    
                returning_object = method.invoke(object, new_arg_list);
            }
            else
            {
                Method method = klass.getDeclaredMethod(name, arg_types);
                method.setAccessible(true);
                
                returning_object = method.invoke(object, new_arg_list);
            }
        }
        catch (Exception e)
        {
            LOG.error("error invoking private method(" + name + "): " + e.toString());
            
            throw new RuntimeException(e.getCause() !=  null ? e.getCause().getMessage() 
                                                             : "error invoking private method(" + name + "): " + e.toString());
        }

        return returning_object;
    }
        
//  MOCKITO SUPPORT METHODS

     private Class extractTargetObject(Object proxied) 
     {
        try 
        {
            if (isMocked(proxied.getClass()))
                return findMockitoClass(proxied);
            else
                return Class.forName("java.lang.Object");
        }
        catch (Exception e) 
        {
            throw new RuntimeException(e);
        }
    }
     
    private static Class<?> findMockitoClass(Object proxied)
    {
        Class<?> k = null;
        MockingDetails details = Mockito.mockingDetails(proxied);
                
        if (details != null)
            k = details.getMockCreationSettings().getTypeToMock();

        return k;
    }
    
    private static Class<?> getType(Class k, String className, Object o) throws Exception
    {
        if (className.equals("int"))    // "java.lang.Integer"))
            return Integer.TYPE;
        else
        if (className.equals("long"))   // "java.lang.Long"))
            return Long.TYPE;
        else
        if (className.equals("short"))  // "java.lang.Short"))
            return Short.TYPE;
        else
        if (className.equals("float"))  // "java.lang.Float"))
            return Float.TYPE;
        else
        if (className.equals("double")) // "java.lang.Double"))
            return Double.TYPE;
        else
        if (className.equals("byte"))   // "java.lang.Byte"))
            return Byte.TYPE;
        else
        if (className.equals("char"))   // "java.lang.Char"))
            return Character.TYPE;
        else
        if (isMocked(k))
            return findMockitoClass(o);
        else
            return Class.forName(className);
    }
    
    private static boolean isMocked(Class klass)
    {
        String className = klass.getName();
        
        if (className.contains("EnhancerByMockitoWithCGLIB"))
            return true;
        else
        if (className.contains("MockitoMock"))
            return true;

        return false;
    }
    
    private static void fix_arg_types(Class<?> [] types) throws Exception
    {
        int n = 0;
        
        for (Class<?> k : types)
        {
            if (k.getName().equals("java.util.ArrayList"))
                types[n++] = Class.forName("java.util.List");
        }
    }
}
