package org.breitling.dragon.framework.test;

import static org.junit.Assert.assertNotNull;
import org.junit.Test;
import org.breitling.dragon.framework.types.SimpleTest;

public class TestSimple extends SimpleTest
{
    @Test
    public void testSimpleTest_defineThis_NotNull()
    {
        assertNotNull(this);
    }
}
