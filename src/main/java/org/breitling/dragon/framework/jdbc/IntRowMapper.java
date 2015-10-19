package org.breitling.dragon.framework.jdbc;

import java.sql.ResultSet;
import java.sql.SQLException;

public class IntRowMapper implements RowMapper<Integer>
{
	public Integer mapRow(ResultSet rs, int rownumber) throws SQLException
    {
        return new Integer(rs.getInt(1));
    }
}
