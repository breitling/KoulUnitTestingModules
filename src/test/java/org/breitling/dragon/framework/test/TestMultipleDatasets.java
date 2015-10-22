package org.breitling.dragon.framework.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

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
import org.breitling.dragon.framework.types.TestWithClassInit;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:context.xml"})
@Category(org.breitling.dragon.framework.category.BasicTests.class)
public class TestMultipleDatasets extends TestWithClassInit
{
    @Autowired
    private DataSource dataSource;
    
    @BeforeClass
    public static void testSetup()
    {
    	TestMultipleDatasets.addSchema("classpath:/test.schema");
    	TestMultipleDatasets.testSetup("classpath:/test.xml");
        TestMultipleDatasets.addAnAdditionalDataset("classpath:/test2.xml");   // for each additional data set     
    }
    
    @Before
    public void testCaseSetup()
    {
        super.testCaseSetup(dataSource);
    }
    
    @Test
    public void testMultipleDatasets_TwoDataset_GoodCountOfRecords() throws Exception
    {
    	Integer t = super.getInt("select count(*) as cnt from site_category_type");

        assertNotNull(t);
        assertEquals(46, t.intValue());
    }
}
