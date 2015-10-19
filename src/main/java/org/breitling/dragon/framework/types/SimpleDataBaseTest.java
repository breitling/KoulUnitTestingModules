package org.breitling.dragon.framework.types;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.sql.DataSource;

import org.dbunit.dataset.CompositeDataSet;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.ReplacementDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.junit.AfterClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.breitling.dragon.framework.base.DataBaseTestBase;


public abstract class SimpleDataBaseTest extends DataBaseTestBase
{
    private static Logger LOG = LoggerFactory.getLogger(SimpleDataBaseTest.class);
    
    private static List<String> dataSetAdditions = new ArrayList<String>();
    private static List<String> dataSetSchemaAdditions = new ArrayList<String>();
    
//  ONLY CALLED IT SUPER CLASS CALLS (no @BeforeClass)
	public static void testSetup()
	{
		DataBaseTestBase.testSetup();
	}
	
//  ONLY CALLED IT SUPER CLASS CALLS (no @Before)
	public void testCaseSetup(final DataSource dataSource)
	{
		super.testCaseSetup(dataSource);
		
		try
		{
		    if (dataSetSchemaAdditions.size() > 0)
		    {
		        Iterator<String> iterator = dataSetSchemaAdditions.iterator();
		        
		        while (iterator.hasNext())
		        {
		            addSchemaToDB(iterator.next());
		            iterator.remove();
		        }		        
		    }
		}
		catch (RuntimeException rte)
		{
		    throw new RuntimeException("failed to load schema: " + rte.toString());
		}		
	}
	
//  ONLY CALLED IT SUPER CLASS CALLS (no @After)
	public void testCaseTearDown()
	{
		super.testCaseTearDown();
	}
	
	@AfterClass
	public static void testTearDown()
	{
		DataBaseTestBase.testTearDown();
		
		dataSetAdditions = new ArrayList<String>();
		dataSetSchemaAdditions = new ArrayList<String>();
	}
	
//  STATIC METHODS
	
    public static void addSchema(final String filename)
    {
        if (filename == null || filename.length() == 0)
            throw new IllegalArgumentException("Bad file name");
        
        dataSetSchemaAdditions.add(filename);
    }
    
    public static void addSchemaFromClasspath(final String filename)
    {
        if (filename == null || filename.length() == 0)
            throw new IllegalArgumentException("Bad file name");
        
        dataSetSchemaAdditions.add("classpath:/" + filename);
    }
    
    public static void addAnAdditionalDataset(final String filename)
    {
        if (filename == null || filename.length() == 0)
            throw new IllegalArgumentException("Bad file name");
        
        dataSetAdditions.add(filename);
    }
    
    public static void addDatasetFromClasspath(final String filename)
    {
        if (filename == null || filename.length() == 0)
            throw new IllegalArgumentException("Bad file name");
        
        dataSetAdditions.add("classpath:/" + filename);
    }
    
//  PUBLIC METHODS
    
    public List<Object> resultSetToList(ResultSet rs)
    {
        List<Object> values = new ArrayList<Object>();
        
        try
        {
            int k = rs.getMetaData().getColumnCount();

            while (rs.next())
            {
                for (int n = 1; n <= k; n++)
                    values.add(rs.getObject(n));
            }
        }
        catch (SQLException sqle)
        {
        	LOG.error("error converting result set to a list of objects", sqle);
        	throw new RuntimeException("failed to convert result set.");
        }
        
        return values;
    }
    
    public void addSchemaToDB(final String filename)
    {
        try
        {
            if (filename.startsWith("classpath:"))
                execute(readInputStreamToString(this.getClass().getResourceAsStream(filename.substring(10))));
            else
                execute(readFileToString(filename));
        }
        catch (Exception e)
        {
            LOG.error("error while adding schema: " + e.toString());
            throw new RuntimeException("failed to execute database script(" + filename + "): " + e.toString());
        }
    }
    
//  Protected METHODS

    protected IDataSet getDataSet(final String dataSet) throws Exception
    {
        int n = 0;        
    	IDataSet [] xmlDataSets = new IDataSet [1 + SimpleDataBaseTest.dataSetAdditions.size()];
        FlatXmlDataSetBuilder builder = new FlatXmlDataSetBuilder();

        builder.setColumnSensing(true);

        if (dataSet.startsWith("classpath:"))
        	xmlDataSets[n++] = builder.build(this.getClass().getResourceAsStream(dataSet.substring(10)));
        else
        	xmlDataSets[n++] = builder.build(new FileInputStream(dataSet));
        
        for (String dataSetName : SimpleDataBaseTest.dataSetAdditions)
        {
        	if (dataSet.startsWith("classpath:"))
        		xmlDataSets[n++] = builder.build(this.getClass().getResourceAsStream(dataSetName.substring(10)));
        	else
        		xmlDataSets[n++] = builder.build(new FileInputStream(dataSetName));
        }

        IDataSet composite = new CompositeDataSet(xmlDataSets);        
    	ReplacementDataSet replacementDataSet = new ReplacementDataSet(composite);		
		replacementDataSet.addReplacementObject("[NULL]",null);
		
		return replacementDataSet;
    }

    protected String readFileToString(final String name) throws Exception
    {
        BufferedReader br = new BufferedReader(new FileReader(name));
        return readReaderToString(br);
    }
    
    protected String readInputStreamToString(final InputStream stream) throws Exception
    {
        if (stream != null)
        {
            BufferedReader br = new BufferedReader(new InputStreamReader(stream));        
            return readReaderToString(br);
        }
        else
        {
            throw new RuntimeException("stream is null.");
        }
    }
    
    protected String readReaderToString(BufferedReader br) throws Exception
    {
        StringBuilder sb = new StringBuilder();

        String line = null;

        while ((line = br.readLine()) != null)
        {
            sb.append(line).append("\n");
        }

        br.close();

        return sb.toString();
    }
}
