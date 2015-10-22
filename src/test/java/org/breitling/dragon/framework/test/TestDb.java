package org.breitling.dragon.framework.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import javax.sql.DataSource;

import org.breitling.dragon.framework.jdbc.RowMapper;
import org.breitling.dragon.framework.types.SimpleDataBaseTest;
import org.h2.jdbcx.JdbcDataSource;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class TestDb extends SimpleDataBaseTest 
{
    private static DataSource dataSource;
    
    @BeforeClass
    public static void testSetup()
    {
       TestDb.addSchema("classpath:/base.schema");
       
       JdbcDataSource ds = new JdbcDataSource();
               
       ds.setURL("jdbc:h2:mem:dragon;MODE=MySQL");
       ds.setUser("sa");
       ds.setPassword("");
              
       dataSource = ds;
    }

    @Before
    public void testCaseSetup() 
    {
        testCaseSetup(dataSource);
    }

    @Test
    public void testDbTest_validateDB_Success()
    {
        try
        {
            validateDB();
        }
        catch (Exception e)
        {
            fail(e.toString());
        }
    }
    
    @Test
    public void testDbTest_insertAndSelect_Success()
    {
        try
        {
            executeUpdate("insert into test_table values(1, 'this', 'that is it', today)");
            List<TestTable> values = executeQuery("select * from test_table where col1 = 1", new RowMapper<TestTable>() {
                public TestTable mapRow(ResultSet rs, int rownumber) throws SQLException
                {
                    TestTable td = new TestTable();

                    td.col1 = rs.getInt(1);
                    td.col2 = rs.getString(2);
                    td.col3 = rs.getString(3);
                    td.col4 = rs.getDate(4);

                    return td;
                }
            });

            TestTable t = values.get(0);

            assertNotNull(t);
            assertEquals(1, t.col1);
            assertEquals("this", t.col2);
            assertEquals("that is it", t.col3);
        }
        catch (RuntimeException rte)
        {
            fail("insert failed: " + rte.toString());
        }
    }
}
