package org.breitling.dragon.framework.base;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Savepoint;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.dbunit.DataSourceDatabaseTester;
import org.dbunit.IDatabaseTester;
import org.dbunit.IOperationListener;
import org.dbunit.database.DatabaseConfig;
import org.dbunit.database.IDatabaseConnection;
import org.junit.AfterClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.breitling.dragon.framework.jdbc.IntRowMapper;
import org.breitling.dragon.framework.jdbc.RowMapper;
import org.breitling.dragon.framework.jdbc.StringRowMapper;
import org.breitling.dragon.framework.jdbc.TimestampRowMapper;
import org.breitling.dragon.framework.util.DbUtils;


public abstract class DataBaseTestBase extends TestBase
{
    private static Logger LOG = LoggerFactory.getLogger(DataBaseTestBase.class);
    private static String DEFAULT_SCHEMA = "PUBLIC";

    private String schema;    
    private IDatabaseTester databaseTester;
    
    private boolean useQualifiedNames = false;
    
//  TESTING CONTRACTS
    
    public static void testSetup()
    {
        TestBase.testSetup();
    }
    
    public void testCaseSetup(final DataSource dataSource)
    {
        super.testCaseSetup();

        if (dataSource == null)
            throw new RuntimeException("DataSource is null");
        
        try
        {
            DbUtils.setDataSource(dataSource);
            DbUtils.setUp();

            databaseTester = new DataSourceDatabaseTester(DbUtils.getDataSource(), getSchema());            
            databaseTester.setOperationListener(new DataBaseTestBaseOperationalListener());
        }
        catch (Exception e)
        {
            LOG.error("failed to initialize things!", e);
            throw new RuntimeException("error intializing DBUnit: " + e.toString());
        }
    }
    
    public void testCaseTearDown()
    {
        super.testCaseTearDown();
    }
    
    @AfterClass
    public static void testTearDown()
    {
        TestBase.testTearDown();
        execute("drop all objects");
    }
    
//  GETTERS AND SETTERS
    
    public IDatabaseTester getDatabaseTester()
    {
        return databaseTester;
    }
    
    public void setUseQualifiedNames(final Boolean state)
    {
        this.useQualifiedNames = state.booleanValue();
    }
    
//  EXTRA DB METHODS
    
    protected static void execute(final String queryString)
    {
        validateDB();

        Statement ps = null;

        try
        {
            ps = DbUtils.getConnection().createStatement();
            ps.executeUpdate(queryString);
        }
        catch (Exception e)
        {
            throw new RuntimeException("datebase update failed: " + e.toString());
        }
        finally
        {
            DbUtils.closeQuietly(ps);
            DbUtils.commit();
        }
    }
    
    public <T> List<T> executeQuery(final String queryString, final RowMapper<T> mapper)
    {
        validateDB();
        
        List<T> list = new ArrayList<T>();
        PreparedStatement ps = null;
        ResultSet rs = null;
        
        try
        {
            ps = DbUtils.getConnection().prepareStatement(queryString);
            ps.clearParameters();
            
            rs = ps.executeQuery();
            
            while(rs.next())
                list.add(mapper.mapRow(rs, 0));
        }
        catch (Exception e)
        {
            LOG.error("", e);
            throw new RuntimeException("datebase query failed: " + e.toString());
        }
        finally
        {
            DbUtils.closeQuietly(rs);
            DbUtils.closeQuietly(ps);
        }
        
        return list;
    }
    
    public void executeUpdate(final String queryString)
    {
        validateDB();
        
        Statement ps = null;
        Savepoint sp = null;
        
        try
        {
            sp = DbUtils.getConnection().setSavepoint();
            
            DbUtils.setAutoCommit(false);            
            
            ps = DbUtils.getConnection().createStatement();
            ps.executeUpdate(queryString);
            
            DbUtils.commit();
        }
        catch (Exception e)
        {
            LOG.error(e.toString());
            DbUtils.rollback(sp);
            throw new RuntimeException("datebase update failed: " + e.toString());
        }
        finally
        {
            DbUtils.closeQuietly(ps);
            DbUtils.setAutoCommit(true);
        }
    }
    
    public Integer getInt(final String query)
    {
        List<Integer> values = this.executeQuery(query, new IntRowMapper());
        
        if (values == null)
            throw new RuntimeException("query failed");    
        if (values.size() > 1)
            throw new RuntimeException("query returned more than 1 row");
        
        if (values.size() == 0)
            return new Integer(0);
        else
            return values.get(0);
    }
    
    public String getString(final String query)
    {
        List<String> values = this.executeQuery(query, new StringRowMapper());
        
        if (values == null)
            throw new RuntimeException("query failed");    
        if (values.size() > 1)
            throw new RuntimeException("query returned more than 1 row");
        
        if (values.size() == 0)
            return "";
        else
            return values.get(0);
    }
    
    public List<String> getStrings(final String query)
    {
        List<String> values = this.executeQuery(query, new StringRowMapper());
        
        if (values == null)
            throw new RuntimeException("query failed");
        
        return values;
    }
    
    public Timestamp getTimestamp(final String query)
    {
        List<Timestamp> values = this.executeQuery(query, new TimestampRowMapper());

        if (values == null)
            throw new RuntimeException("query failed");    
        if (values.size() > 1)
            throw new RuntimeException("query returned more than 1 row");

        if (values.size() == 0)
            return new Timestamp(0L);
        else
            return values.get(0);
    }
    
    public static void validateDB()
    {
        validateJUnitDB(DbUtils.getConnection());
    }
    
    public static void validateDB(final String schemaName)
    {
        if (schemaName == null)
            throw new IllegalArgumentException("missing schema name.");
        
        validateDB();
        
        boolean found = false;
            
        try
        {
            DatabaseMetaData md = DbUtils.getConnection().getMetaData();
            ResultSet results = md.getSchemas();

            while(results.next())
            {
                String name = results.getString(1);

                if (schemaName.equalsIgnoreCase(name))
                    found = true;
            }
        }
        catch (SQLException e)
        {
            throw new RuntimeException("failed to get database schema names: " + e.toString());
        }
        if (found == false)
        {
            throw new RuntimeException("schema name not found.");
        }   
    }
    
    public static void validateDBTableName(final String tableName)
    {
        validateDBTableName("PUBLIC", tableName);
    }
    
    public static void validateDBTableName(final String schemaName, final String tableName)    
    {
        if (schemaName == null)
            throw new IllegalArgumentException("missing schema name.");
        
        if (tableName == null)
            throw new IllegalArgumentException("missing table name.");
 
        boolean found = false;
        
        try
        {
            DatabaseMetaData md = DbUtils.getConnection().getMetaData();
            ResultSet rs = md.getTables(null, schemaName, "%", null);
        
            while(rs.next())
            {
                String name = rs.getString(3);
                
                LOG.debug(name);
                
                if (tableName.equalsIgnoreCase(name))
                {
                    found = true;
                    break;
                }                
            }
        }
        catch (SQLException e)
        {
            throw new RuntimeException("failed to get database table names: " + e.toString());   
        }
        if (found == false)
        {
            throw new RuntimeException("table name not found.");
        }
    }
    
//  GETTERS AND SETTERS
    
    public String getSchema()
    {
        if (this.schema == null)
            return DEFAULT_SCHEMA;
        else
            return this.schema;
    }
    
    public void setSchema(final String schema)
    {
        this.schema = schema;
    }
    
//  PRIVATE METHODS
    
    private static void validateJUnitDB(final Connection conn)
    {
        if (conn == null)
            throw new RuntimeException("No DB connection!");
    }
    
//  PRIVATE CLASSES
    
    private class DataBaseTestBaseOperationalListener implements IOperationListener
    {
        public void connectionRetrieved(IDatabaseConnection connection) 
        {
             DatabaseConfig config = connection.getConfig();

             config.setProperty(DatabaseConfig.PROPERTY_DATATYPE_FACTORY, new org.dbunit.ext.h2.H2DataTypeFactory());
             config.setProperty(DatabaseConfig.FEATURE_ALLOW_EMPTY_FIELDS, Boolean.TRUE);
             config.setProperty(DatabaseConfig.FEATURE_QUALIFIED_TABLE_NAMES, useQualifiedNames);
        }
    
        public void operationSetUpFinished(IDatabaseConnection connection) 
        {
        }
    
        public void operationTearDownFinished(IDatabaseConnection connection) 
        {
        }
    }
}
