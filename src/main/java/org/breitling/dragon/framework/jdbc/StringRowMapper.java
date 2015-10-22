package org.breitling.dragon.framework.jdbc;

import java.sql.ResultSet;
import java.sql.SQLException;

public class StringRowMapper implements RowMapper<String>
{
     public String mapRow(ResultSet rs, int rownumber) throws SQLException
     {
         return rs.getString(1);
     }
}
