package org.breitling.dragon.framework.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.junit.FixMethodOrder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import org.breitling.dragon.framework.annotations.RunInThread;
import org.breitling.dragon.framework.rules.RunInThreadRule;
import org.breitling.dragon.framework.types.SimpleTest;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TestRunInThread extends SimpleTest
{
	private static final ThreadLocal<Integer> testVariable = new ThreadLocal<Integer>();
	
//  RULES
	
	@Rule
	public RunInThreadRule runInThread = new RunInThreadRule();
	
//  TEST CASES
	
	@Test
	@RunInThread
	public void testRunInThread1_Thread_Null()
	{
		assertNull(testVariable.get());
	}
	
	@Test
	@RunInThread
	public void testRunInThread2_SetValue_Value()
	{
		testVariable.set(new Integer(12345));
		
		assertEquals(12345, (int) testVariable.get());
	}
	
	@Test
	@RunInThread
	public void testRunInThread3_AnotherThread_Null()
	{
		assertNull(testVariable.get());
	}
	
	@Test
	public void testRunInThread4_NoThread_Null()
	{
		assertNull(testVariable.get());
	}
	
	@Test
	public void testRunInThread5_NoThreadSet_Value()
	{
		testVariable.set(new Integer(12345));
		
		assertEquals(12345, (int) testVariable.get());
	}
	
	@Test
	public void testRunInThread6_NoThread_NotNull()
	{
		assertNotNull(testVariable.get());
	}
}
