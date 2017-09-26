package org.breitling.dragon.framework.util;

import java.util.Properties;

import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.transaction.annotation.EnableTransactionManagement;


@Configuration
@EnableTransactionManagement
public class DragonSpringJPAConfig 
{
	private DriverManagerDataSource dataSource;
	
	@Bean
	@Profile("test")
	public DataSource dataSource()
	{
		Properties connectionProperties = new Properties();
		
        connectionProperties.put("hibernate.dialect", "org.hibernate.dialect.H2Dialect");
        connectionProperties.put("hibernate.hbm2ddl.auto", "create");        
        connectionProperties.put("hibernate.show_sql", "true");
//      connectionProperties.put("cache.provider", "org.hibernate.cache.internal.NoCacheProvider");
		
        dataSource = new DriverManagerDataSource();		
		dataSource.setDriverClassName("org.h2.Driver");
        dataSource.setUrl("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1");
        dataSource.setUsername("sa");
        dataSource.setPassword("sa");
        dataSource.setConnectionProperties(connectionProperties);
        
        return dataSource;
	}
	
	public DataSource getDataSource() {
		return dataSource;
	}
}
