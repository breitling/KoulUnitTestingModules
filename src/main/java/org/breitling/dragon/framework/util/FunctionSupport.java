package org.breitling.dragon.framework.util;

import java.io.File;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Types;
import java.util.Calendar;

import javax.sql.DataSource;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.h2.tools.SimpleResultSet;
import org.h2.util.JdbcUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class FunctionSupport
{
	private static Logger LOG = LoggerFactory.getLogger(FunctionSupport.class);
	
    public static ResultSet getResultSet(final DataSource dataSource, final String sql)
    {
        ResultSet         rs = null;
        PreparedStatement ps = null;
        Connection        conn = null;        
        SimpleResultSet   localrs = null;
        
        try
        {
            conn = dataSource.getConnection();
            
            ps = conn.prepareStatement(sql);
            ps.execute();
            rs = ps.getResultSet();
            localrs = getSimpleResultSet(rs);
        }
        catch (Exception e)
        {
        	LOG.error("get result set failed: " + e.toString());
        }
        finally
        {
            JdbcUtils.closeSilently(rs);
            JdbcUtils.closeSilently(ps);
            JdbcUtils.closeSilently(conn);
        }
        
        return localrs;
    }
    
    public static ResultSet getResultSetFromXml(final String xmlFileName) throws Exception
    {
    	SimpleResultSet rs = new SimpleResultSet();
    	
//		<dataset>
//			<schema col1="TYPE:SIZE" col2="TYPE:SIZE"...colN="TYPE:SIZE"/>
//          <row col1="VALUE" col2="VALUE"...colN="VALUE"/>
//          <row col1="VALUE" col2="VALUE"...colN="VALUE"/>
//      </dataset>
//
//      TYPE := INTEGER, CHAR, VARCHAR, DATE
//    	
    	try
    	{
	    	DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
	    	DocumentBuilder db = dbf.newDocumentBuilder();
	    	Document document = db.parse(new File(xmlFileName));
	    	
	    	NodeList schemaTag = document.getElementsByTagName("schema");
	    	NodeList rows = document.getElementsByTagName("row");

	    	Node schema = schemaTag.item(0);
	    	NamedNodeMap columns = schema.getAttributes();
	    	
	    	int n = 0;
	    	int [] columnTypes = new int [columns.getLength()];
	    	
	    	for (n = 0; n < columns.getLength(); n++)
	    	{
	    		Node col = columns.item(n);
	    		String value = col.getNodeValue();
	    		
	    		String [] parts = value.split(":");
	    	
	    		columnTypes[n] = convertStringToType(parts[0]);
	    		rs.addColumn("col"+n, columnTypes[n], Integer.valueOf(parts[1]), 0);
	    	}

	    	int columnCount = n;
	    	
	    	for (n = 0; n < rows.getLength(); n++)
	    	{
		    	Object [] rowobjects = new Object [columnCount];
		    	
	    		Node row = rows.item(n);
		    	NamedNodeMap rowattrs = row.getAttributes();

		    	for (int k = 0; k < rowattrs.getLength(); k++)
		    	{
		    		Node node = rowattrs.item(k);
		    		String value = node.getNodeValue();
	    		
		    		if (columnTypes[k] == Types.DATE)
		    			rowobjects[k] = new Date(Calendar.getInstance().getTime().getTime());
		    		else
		    			rowobjects[k] = value;
		    	}
		    	
		    	rs.addRow(rowobjects);
	    	}	    	
    	}
    	catch (Exception e)
    	{    		
    		throw e;
    	}
    	
    	return rs;
    }
    
//  PRIVATE METHODS
    
    private static int convertStringToType(String type) 
    {
		if (type.equalsIgnoreCase("INTEGER"))
			return Types.INTEGER;
		else
		if (type.equalsIgnoreCase("CHAR"))
			return Types.CHAR;
		else
		if (type.equalsIgnoreCase("VARCHAR"))
			return Types.VARCHAR;
		else
		if (type.equalsIgnoreCase("DATE"))
			return Types.DATE;
		else
			return Types.BLOB;
	}

	private static SimpleResultSet getSimpleResultSet(ResultSet rs) throws Exception
    {
        ResultSetMetaData metadata = rs.getMetaData();        
        int columnCount = metadata.getColumnCount();
        
        SimpleResultSet simple = new SimpleResultSet();
        
        for (int i = 1; i <= columnCount; i++)
        {
            String name = metadata.getColumnName(i);
            
            int sqlType = metadata.getColumnType(i);
            int precision = metadata.getPrecision(i);
            int scale = metadata.getScale(i);
            
            simple.addColumn(name, sqlType, precision, scale);
        }
                
        while (rs.next())
        {
            Object[] list = new Object[columnCount];
            
            for (int j = 0; j < columnCount; j++)
                list[j] = rs.getObject(j+1);

            simple.addRow(list);
        }
        
        return simple;
    }    
}
