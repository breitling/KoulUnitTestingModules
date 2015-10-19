package org.breitling.dragon.framework.utils;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.Types;
import java.util.Calendar;
import java.util.List;

import org.h2.tools.SimpleResultSet;


public class Functions
{
	private static Functions that = new Functions();
	
	private StringUtils utils;
	
	public static ResultSet getTest(int id)
	{
		SimpleResultSet rs = new SimpleResultSet();

		rs.addColumn("col1", Types.INTEGER, 10, 0);
		rs.addColumn("col2", Types.CHAR, 25, 0);
		rs.addColumn("col3", Types.VARCHAR, 25, 0);
		rs.addColumn("col4", Types.DATE, 24, 0);

		rs.addRow(7, "What", that.camelCase("this is a test."), new Date(Calendar.getInstance().getTime().getTime()));

		return rs;
	}
   
	public String changeCase(String src)
	{
		return utils.camelCase(src);
	}
	
	private String camelCase(final String src)
	{
		String[] parts = src.split(" ");
		StringBuilder sb = new StringBuilder();

		for (String p : parts) 
		{
			sb.append(p.substring(0, 1).toUpperCase()).append(p.substring(1)).append(" ");
		}

		return sb.toString().trim();
	}
	
	private Integer add3(int a1, int a2, int a3)
	{
		return a1 + a2 + a3;
	}
	
	private void validate(final String arg) throws RuntimeException
	{
		if (arg == null)
			throw new RuntimeException("arg is null.");
		
		if (arg.length() == 0)
			throw new RuntimeException("arg is empty.");
	}
	
	private Integer getArrayListSize(final List<?> list)
	{
		return list.size();
	}
	
	private Integer getListSize(final List<?> list)
	{
		return list.size();
	}
}
