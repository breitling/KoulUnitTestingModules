package org.breitling.dragon.framework.jdbc;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

public class TimestampRowMapper implements RowMapper<Timestamp>
{
	 public Timestamp mapRow(ResultSet rs, int rownumber) throws SQLException
     {
         return rs.getTimestamp(1);
     }
}
