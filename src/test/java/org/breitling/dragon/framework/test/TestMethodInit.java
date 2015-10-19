package org.breitling.dragon.framework.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
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
import org.breitling.dragon.framework.types.TestWithMethodInit;
import org.breitling.dragon.framework.util.Utility;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:context.xml"})
@Category(org.breitling.dragon.framework.category.BasicTests.class)
public class TestMethodInit extends TestWithMethodInit
{
    @Autowired
    private DataSource dataSource;
    
    @BeforeClass
    public static void testSetup()
    {
        TestWithMethodInit.addSchema("classpath:/test.schema");
        TestWithMethodInit.testSetup("classpath:/test.xml");
    }
    
    @Before
    public void testCaseSetup()
    {
        super.testCaseSetup(dataSource);
    }
    
    @Test
    public void testTestWithMethodInit_schemaAdditions_Tables() throws Exception
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
    public void testTestWithMethodInit_dataSetLoad_Rows()
    {
        List<Integer> values = super.executeQuery("select count(*) as cnt from test_table", new RowMapper<Integer>() {
            public Integer mapRow(ResultSet rs, int rownumber) throws SQLException
            {
                return new Integer(rs.getInt(1));
            }
        });

        Integer t = values.get(0);

        assertNotNull(t);
        assertEquals(5, t.intValue());
    }
}
