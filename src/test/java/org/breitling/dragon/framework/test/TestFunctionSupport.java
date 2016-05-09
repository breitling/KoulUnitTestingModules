package org.breitling.dragon.framework.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

import java.sql.ResultSet;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import org.breitling.dragon.framework.types.TestWithClassInit;
import org.breitling.dragon.framework.util.StoredProcUtils;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:context.xml"})
@Category(org.breitling.dragon.framework.category.BasicTests.class)
public class TestFunctionSupport extends TestWithClassInit
{
    @Autowired
    private DataSource dataSource;
    
    @BeforeClass
    public static void testSetUp()
    {
    	TestFunctionSupport.addSchema("classpath:/test.schema");
    	TestFunctionSupport.testSetup("classpath:/test.xml");
    }

    @Before
    public void testCaseSetup()
    {
        super.testCaseSetup(dataSource);
    }
    
    @Test
    public void testGetResultSet_GoodSQLStatement_Results() throws SQLException
    {
		ResultSet rs = StoredProcUtils.getResultSet(dataSource,	"SELECT * FROM test_table WHERE col1 = 3");

		assertNotNull(rs);
		assertTrue(rs.next());
		
		TestTable td = new TestTable();

        td.col1 = rs.getInt(1);
        td.col2 = rs.getString(2);
        td.col3 = rs.getString(3);
        td.col4 = rs.getDate(4);
        
		assertEquals(3, td.col1);
		assertEquals("these", td.col2);
		assertEquals("they are it", td.col3);
    }
    
    @Test
    public void testGetResultSet_NullDataSource_Null()
    {
    	ResultSet rs = StoredProcUtils.getResultSet(null, "SELECT * FROM test_table WHERE col1 = 3");

		assertNull(rs);
    }
    
    @Test
    public void testGetResultSet_EmptySQLStatement_NoResults() throws SQLException
    {
		ResultSet rs = StoredProcUtils.getResultSet(dataSource,	"SELECT * FROM test_table WHERE col1 = 99");

		assertNotNull(rs);
		assertFalse(rs.next());
    }
}
