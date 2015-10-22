package org.breitling.dragon.framework.test;

import static org.junit.Assert.assertEquals;

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

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:context.xml"})
@Category(org.breitling.dragon.framework.category.BasicTests.class)
public class TestQualifiedNames extends TestWithClassInit
{
    @Autowired
    private DataSource dataSource;
    
    @BeforeClass
    public static void testSetUp()
    {
        TestQualifiedNames.addSchema("classpath:/QualifiedNames.schema");
        TestQualifiedNames.testSetup("classpath:/QualifiedNames.xml");
    }
    
    @Before
    public void testCaseSetup()
    {
        super.setSchema("public");
        super.setUseQualifiedNames(Boolean.TRUE);
        super.testCaseSetup(dataSource);
/*        
**      List<String> tables = super.getStrings("SELECT CONCAT(TABLE_SCHEMA,CONCAT('.',TABLE_NAME)) FROM INFORMATION_SCHEMA.TABLES");        
**      System.out.println(tables);
*/        
    }
    
    @Test
    public void testTwoSchemas_InsertIntoSecondSchema_FindId()
    {
        super.executeUpdate("INSERT INTO DB2.test_table_2 VALUES(100, 'what')");
        
        int id = super.getInt("SELECT col1 FROM db2.test_table_2 WHERE col2 = 'what'");
        
        assertEquals(100, id);
    }
    
    @Test
    public void testUseQualifiedNames_FindOneRecord_Id1() 
    {
        int id = super.getInt("SELECT col1 FROM db1.test_table_1 WHERE col2 = 'this'");        
        assertEquals(1, id);
    }
    
    @Test
    public void testUseQualifiedNames_FindOneRecord_Id9() 
    {
        int id = super.getInt("SELECT col1 FROM db2.test_table_2 WHERE col2 = 'crap'");
        assertEquals(13, id);
    }
}
