package org.breitling.dragon.framework.test;

import java.io.Serializable;
import java.sql.Date;

public class TestTable implements Serializable
{
    private static final long serialVersionUID = -8125110408552860218L;
    
    public int    col1;
    public String col2;
    public String col3;
    public Date   col4;
}