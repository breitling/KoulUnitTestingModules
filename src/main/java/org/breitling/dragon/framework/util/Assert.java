package org.breitling.dragon.framework.util;

public class Assert 
{
    private Assert()
    {
    }
    
    public static void assertContains(final String source, final String partial)
    {
        if (! source.contains(partial))
            throw new AssertionError(String.format("'%s' does not contain '%s'", source, partial));
    }
}
