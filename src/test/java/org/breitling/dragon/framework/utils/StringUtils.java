package org.breitling.dragon.framework.utils;

public class StringUtils 
{
    public StringUtils()
    {
    }
    
    public String camelCase(final String src)
    {
        String[] parts = src.split(" ");
        StringBuilder sb = new StringBuilder();

        for (String p : parts) 
        {
            sb.append(p.substring(0, 1).toUpperCase()).append(p.substring(1)).append(" ");
        }

        return sb.toString().trim();
    }
}
