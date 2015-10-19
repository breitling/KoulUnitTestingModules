package org.breitling.dragon.framework.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import org.breitling.dragon.framework.types.SimpleTest;
import org.breitling.dragon.framework.util.FunctionSupport;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:context.xml"})
@Category(org.breitling.dragon.framework.category.BasicTests.class)
public class TestXmlDataSet extends SimpleTest
{
    @Test
    @SuppressWarnings("deprecation")
    public void testReadXML_BasicDataSet_ResultSet() throws SQLException, Exception
    {
        ResultSet rs = FunctionSupport.getResultSetFromXml("classpath:/xmldataset.xml");
        
        assertNotNull(rs);        
        assertNotNull(rs.next());
        assertEquals(1, rs.getInt(1));
        assertEquals("TST", rs.getString(2));
        assertEquals("This is a test", rs.getString(3));
        assertNotNull(rs.next());
        assertEquals(2, rs.getInt(1));
        assertEquals("BOB", rs.getString(2));
        assertEquals("This is another test", rs.getString(3));
        assertNotNull(rs.next());
        assertEquals(12, rs.getInt(1));
        assertEquals("SDF", rs.getString(2));
        assertEquals("This is a DATE test", rs.getString(3));
        assertEquals(new Timestamp(2015-1900,OCTOBER,27,10,27,30,0), rs.getTimestamp(4));
    }
}
