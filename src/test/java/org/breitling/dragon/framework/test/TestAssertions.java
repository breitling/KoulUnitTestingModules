package org.breitling.dragon.framework.test;

import static org.breitling.dragon.framework.util.Assert.assertContains;
import static org.junit.Assert.assertEquals;

import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import org.breitling.dragon.framework.types.SimpleTest;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:context.xml"})
@Category(org.breitling.dragon.framework.category.BasicTests.class)
public class TestAssertions extends SimpleTest
{
    @Test
    public void testContains_StringContainsString_True()
    {
        assertContains("ThisIsIt", "It");
    }
    
    @Test
    public void testContains_StringDoesNotContainString_Exception()
    {
        try
        {
            assertContains("ThisIsIt", "Not");
        }
        catch (AssertionError ae)
        {
            assertEquals("java.lang.AssertionError: 'ThisIsIt' does not contain 'Not'", ae.toString());
        }
    }
}
