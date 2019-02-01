package org.breitling.dragon.framework.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.Ignore;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import org.breitling.dragon.framework.types.SimpleTest;
import org.breitling.dragon.framework.util.DbUtils;
import org.breitling.dragon.framework.util.ClassUtils;
import org.breitling.dragon.framework.utils.Functions;
import org.breitling.dragon.framework.utils.StringUtils;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:context.xml"})
@Category(org.breitling.dragon.framework.category.BasicTests.class)
public class TestUtilities extends SimpleTest
{
    private static Functions functions;
    
    @Rule
    public ExpectedException expectedException = ExpectedException.none();
    
    @BeforeClass
    public static void setup()
    {
        functions = new Functions();
    }
    
    @Test
    public void testCallingPrivateMethod_OneArg_String() throws Exception
    {
        String results = (String) ClassUtils.testPrivateMethod(functions, "camelCase", "this is a test");
        
        assertEquals("This Is A Test", results);
    }
    
    @Test
    public void testCallingPrivateMethod_ThreeArgs_Value() throws Exception
    {
        int value = (Integer) ClassUtils.testPrivateMethod(functions, "add3", new Integer(1), new Integer(2), new Integer(3));
        
        assertEquals(6, value);
    }
    
    @Test
    public void testCallingPrivateMethod_ThreePrimitiveArgs_Value() throws Exception
    {
        int value = (Integer) ClassUtils.testPrivateMethod(functions, "add3", 1, 2, 3);
        
        assertEquals(6, value);
    }
    
    @Test
    public void testCallingPrivateMethod_Null_Exception() throws Exception
    {
        expectedException.expect(Exception.class);
        expectedException.expectMessage("arg is null.");
        
        ClassUtils.testPrivateMethod(functions, "validate", (String) null);
    }
    
    @Test
    public void testCallingPrivateMethod_EmptyArg_Exception() throws Exception
    {
        expectedException.expect(Exception.class);
        expectedException.expectMessage("arg is empty.");
        
        ClassUtils.testPrivateMethod(functions, "validate", "");
    }
    
    @Test
    public void testCallingPrivateMethod_ArrayListArg_Integer() throws Exception
    {
        List<String> list = buildList();
        
        Integer size = (Integer) ClassUtils.testPrivateMethod(functions, "getArrayListSize", list);
        
        assertNotNull(size);
        assertEquals(4, (int) size);
    }
    
    @Test
    public void testCallingPrivateMethod_InterfacetArg_Integer() throws Exception
    {
        List<String> list = buildList();
        
        Integer size = (Integer) ClassUtils.testPrivateMethod(functions, "getListSize", list);
        
        assertNotNull(size);
        assertEquals(4, (int) size);
    }
    
    @Test
    public void testInjection_InjectFunction_String() throws Exception
    {
        Functions f = new Functions();
        StringUtils utils = new StringUtils();
        
        DbUtils.injectObject(f, "utils", utils);
        
        String value = f.changeCase("this is a test");
        
        assertEquals("This Is A Test", value);
    }
    
    @Test
    public void testInjection_MissingField_Exception() throws Exception
    {
        Functions f = new Functions();
        
        expectedException.expect(Exception.class);
        expectedException.expectMessage("missing field productDAO");
        
        DbUtils.injectObject(f, "productDAO", null);
    }
    
    @Test
    @Ignore
    public void testCallingPrivateMethod_UsingMockitoProxy_MethodInvoked()
    {
        Functions spied = org.mockito.Mockito.spy(new Functions());
        
        int value = (Integer) ClassUtils.testPrivateMethod(spied, "add3", 1, 2, 3);
		
		assertEquals(6, value);
    }
    
    @Test
    @Ignore
    public void testCallingPrivateMethod_UsingProxiedArgs_MethodInvoked()
    {
        List spied = org.mockito.Mockito.spy(buildList());
        
        Integer size = (Integer) ClassUtils.testPrivateMethod(functions, "getArrayListSize", spied);
		
        assertNotNull(size);
		assertEquals(4, (int) size);
        verify(spied, times(1)).size();
    }
    
    @Test
    public void testCallingPrivateMethodWithTypes_OneType_MethodInvoked()
    {
        List list = buildList();
        
        Integer size = (Integer) ClassUtils.testPrivateMethodWithTypes(functions, "getArrayListSize", "java.util.List", list);
		
        assertNotNull(size);
		assertEquals(4, (int) size);
    }
    
    @Test
    public void testCallingPrivateMethodWithTypes_ManyTypes_MethodInvoked()
    {
        short value = (Short) ClassUtils.testPrivateMethodWithTypes(functions, "add3", "short", (short) 1, "short", (short) 2, "short", (short) 3);
		
		assertEquals(6, value);
    }
    
    @Test
    public void testCallPrivateMethod_WithObjects_MethodInvoked()
    {
        int value = (Integer) ClassUtils.testPrivateMethod(functions, "add2", new Integer(20), new Integer(40));
        
        assertEquals(60, value);
    }
    
//  PRIVATE FACTORIES

    private List<String> buildList()
    {
        List<String> list = new ArrayList<String>();
        
		list.add("This");
		list.add("is");
		list.add("A");
		list.add("Test");
        
        return list;
    }
}
