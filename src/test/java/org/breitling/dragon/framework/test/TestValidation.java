package org.breitling.dragon.framework.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import javax.sql.DataSource;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.breitling.dragon.framework.jdbc.RowMapper;
import org.breitling.dragon.framework.types.SimpleDataBaseTest;
import org.breitling.dragon.framework.util.DbUtils;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:context.xml"})
@Category(org.breitling.dragon.framework.category.ValidationTest.class)
public class TestValidation extends SimpleDataBaseTest
{
    @Autowired
    private DataSource dataSource;
    
    @BeforeClass
    public static void testSetup()
    {
        SimpleDataBaseTest.addSchema("classpath:/base.schema");
        SimpleDataBaseTest.addSchema("classpath:/test.schema");
    }

    @Before
    public void testCaseSetup()
    {
        super.testCaseSetup(dataSource);
    }
    
    @Test
    public void testSimpleDataBaseTest_validateDB_Success()
    {
        try
        {
            super.validateDB();
        }
        catch (Exception e)
        {
            fail(e.toString());
        }
    }

    @Test
    public void testSimpleDataBaseTest_tableExists_FoundTable() throws SQLException
    {
        assertTrue(DbUtils.isInitialized());

        Connection conn = DbUtils.getConnection();

        assertNotNull(conn);

        DatabaseMetaData md = conn.getMetaData();

        assertNotNull(md);

        ResultSet rs = md.getTables(null, "PUBLIC", "%", null);

        assertNotNull(rs);
        
        List<Object> values = super.resultSetToList(rs);
        
        assertTrue(values.contains("PUBLIC"));
        assertTrue(values.contains("TEST_TABLE"));
    }

    @Test
    public void testSimpleDataBaseTest_insertAndSelect_Success()
    {
        try
        {
            executeUpdate("insert into test_table values(1, 'this', 'that is it', today)");
            List<TestTable> values = super.executeQuery("select * from test_table where col1 = 1", new RowMapper<TestTable>() {
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

    @Test
    public void testSimpleDataBaseTest_storedProcedure_Success()
    {
        try
        {
            CallableStatement cStmt = DbUtils.getConnection().prepareCall("{call public.get_test(?)}");

            cStmt.setInt(1, 4);
            cStmt.execute();

            ResultSet rs = cStmt.getResultSet();

            while (rs.next())
            {
                TestTable td = new TestTable();

                td.col1 = rs.getInt(1);
                td.col2 = rs.getString(2);
                td.col3 = rs.getString(3);
                td.col4 = rs.getDate(4);

                assertEquals(7, td.col1);
                assertEquals("What", td.col2);
                assertEquals("This Is A Test.", td.col3);
            }

            rs.close();
            cStmt.close();
        }
        catch (SQLException sqle)
        {
            fail("stored procedure failed: " + sqle.toString());
        }
        catch (Exception e)
        {
            fail("exception thrown: " + e.toString());
        }
    }
    
    @Test
    public void testSimpleDataBaseTest_schemaValidate_Success()
    {
        try
        {
            SimpleDataBaseTest.validateDB("PUBLIC");
        }
        catch(RuntimeException e)
        {
            fail("Did not find schema name.");
        }
        
        try
        {
            SimpleDataBaseTest.validateDB("public");
        }
        catch(RuntimeException e)
        {
            fail("Did not find schema name.");
        }
        
        try
        {
            SimpleDataBaseTest.validateDB("WHATEVER");
            fail("shold not get here.");
        }
        catch (RuntimeException e)
        {
            assertTrue(true);
        }
    }
    
    @Test
    public void testSimpleDataBaseTest_tableNameValidate_TableFound()
    {
        try
        {
            SimpleDataBaseTest.validateDBTableName("test_table");
        }
        catch(RuntimeException e)
        {
            fail("Did not find table name");
        }
    }
}
