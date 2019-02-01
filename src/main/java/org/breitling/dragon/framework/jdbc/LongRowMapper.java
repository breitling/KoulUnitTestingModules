package org.breitling.dragon.framework.jdbc;

import java.sql.ResultSet;
import java.sql.SQLException;

public class LongRowMapper implements RowMapper<Long>
{
    public Long mapRow(ResultSet rs, int rownumber) throws SQLException
    {
        return rs.getLong(1);
    }
}
