package org.breitling.dragon.framework.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
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

import org.breitling.dragon.framework.jdbc.IntRowMapper;
import org.breitling.dragon.framework.jdbc.RowMapper;
import org.breitling.dragon.framework.types.TestWithClassInit;
import org.breitling.dragon.framework.util.Utility;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:context.xml"})
@Category(org.breitling.dragon.framework.category.BasicTests.class)
public class TestClassInit extends TestWithClassInit
{
    @Autowired
    private DataSource dataSource;
    
    @BeforeClass
    public static void testSetUp()
    {
    	TestClassInit.addSchema("classpath:/test.schema");
    	TestClassInit.testSetup("classpath:/test.xml");
    }

    @Before
    public void testCaseSetup()
    {
        super.testCaseSetup(dataSource);
    }
    
    @Test
    public void testTestWithClassInit_schemaAdditions_Tables() throws Exception
    {
        Connection conn = Utility.getInstance().getConnection();
        DatabaseMetaData md = conn.getMetaData();
        ResultSet rs = md.getTables(null, "PUBLIC", "%", null);
        
        assertNotNull(rs.next());
        assertEquals("PUBLIC", rs.getString(2));
        assertEquals("SITE_CATEGORY_TYPE", rs.getString(3));

        assertNotNull(rs.next());
        assertEquals("PUBLIC", rs.getString(2));
        assertEquals("TEST_TABLE", rs.getString(3));
        
        assertNotNull(rs.next());
        assertEquals("PUBLIC", rs.getString(2));
        assertEquals("TREE_NODE_T", rs.getString(3));
    }

    @Test
    public void testTestWithClassInit_dataSetLoad_Rows()
    {
        List<Integer> values = super.executeQuery("select count(*) as cnt from test_table", new IntRowMapper());
        Integer t = (Integer) values.get(0);

        assertNotNull(t);
        assertEquals(5, t.intValue());
    }

    @Test
    public void testTestWithClassInit_select_RowsFound()
    {
        List<TestTable> values = super.executeQuery("select * from test_table where col1 = 3", new RowMapper<TestTable>() {
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

        TestTable t = (TestTable) values.get(0);

        assertNotNull(t);
        assertEquals(3, t.col1);
        assertEquals("these", t.col2);
        assertEquals("they are it", t.col3);
    }
    
    @Test
    public void testTestWithClassInit_getIntWithTooManyResults_Exception()
    {
        try
        {
            Integer i = super.getInt("SELECT sitetypeid FROM site_category_type WHERE sitecategoryid = 4");
            fail("should not get here - " + i);
        }
        catch (RuntimeException rte)
        {
            assertEquals("java.lang.RuntimeException: query returned more than 1 row", rte.toString());
        }
    }
    
    @Test
    public void testTestWithClassInit_getInt_Integer()
    {
        Integer i = super.getInt("SELECT col1 FROM test_table WHERE col2 = 'this'");
        
        assertEquals(1, (int) i);
    }
    
    @Test
    public void testTestWithClassInit_getIntWithNoResults_Integer()
    {
        Integer i = super.getInt("SELECT col1 FROM test_table WHERE col2 = 'who?'");
        
        assertEquals(0, (int) i);
    }
    
    @Test
    public void testTestWithClassInit_getStringWithTooManyResults_Exception()
    {
        try
        {
            String s = super.getString("SELECT col2 FROM test_table");
            fail("should not get here - " + s);
        }
        catch (RuntimeException rte)
        {
            assertEquals("java.lang.RuntimeException: query returned more than 1 row", rte.toString());
        }
    }
    
    @Test
    public void testTestWithClassInit_getString_String()
    {
        String s = super.getString("SELECT col2 FROM test_table WHERE col1 = 3");
        
        assertEquals("these", s);
    }
    
    @Test
    public void testTestWithClassInit_getStrings_ListOfStrings()
    {
    	List<String> tables = super.getStrings("SELECT CONCAT(TABLE_SCHEMA,CONCAT('.',TABLE_NAME)) FROM INFORMATION_SCHEMA.TABLES");        

    	assertNotNull(tables);
    }
    
    @Test
    public void testTestWithClassInit_getStringWithNoResults_String()
    {
        String s = super.getString("SELECT col2 FROM test_table WHERE col1 = 64");
        
        assertEquals("", s);
    }
    
    @Test
    public void testTestWithClassInit_getTimestampWithTooManyResults_Exception()
    {
        try
        {
            Timestamp s = super.getTimestamp("SELECT createdate FROM site_category_type WHERE sitecategoryid = 3");
            fail("should not get here - " + s);
        }
        catch (RuntimeException rte)
        {
            assertEquals("java.lang.RuntimeException: query returned more than 1 row", rte.toString());
        }
    }
    
    @Test
    public void testTestWithClassInit_getTimestamp_Timestamp()
    {
        Timestamp ts = super.getTimestamp("SELECT createdate FROM site_category_type WHERE sitecategorytypeid = 1");
        
        assertEquals(Timestamp.valueOf("2012-09-18 08:14:52.0"), ts);
    }
    
    @Test
    public void testTestWithClassInit_getTimestampWithNoResults_Timestamp()
    {
        Timestamp ts = super.getTimestamp("SELECT createdate FROM site_category_type WHERE sitecategorytypeid = 9890");
        
        assertEquals(new Timestamp(0L), ts);
    }
    
    @Test
    public void testTestWithClassInit_nullValue_Null()
    {
    	String s = super.getString("SELECT col2 FROM test_table WHERE col1 = 5");
        
        assertNull(s);
    }
    
    @Test
    public void testTestWithClassInit_emptyValue_String()
    {
    	String s = super.getString("SELECT col3 FROM test_table WHERE col1 = 5");
        
    	assertNotNull(s);
    	assertTrue(s.length() == 0);
    }
}
