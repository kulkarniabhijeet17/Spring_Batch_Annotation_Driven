package com.springbatch.config;

import java.net.MalformedURLException;
import java.sql.SQLException;
import java.util.Properties;

import javax.sql.DataSource;

import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.support.SimpleJobLauncher;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.repository.support.JobRepositoryFactoryBean;
import org.springframework.batch.support.transaction.ResourcelessTransactionManager;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.datasource.init.DataSourceInitializer;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.transaction.PlatformTransactionManager;

import oracle.jdbc.pool.OracleDataSource;

@Configuration
@PropertySource("file:///D:/Abhijeet/Computer Science/workspace/FrameWorks/Spring/config.properties")
public class DataSourceConfig {
	@Value("/org/springframework/batch/core/schema-drop-oracle10g.sql")
	private Resource dropReopsitoryTables;

	@Value("/org/springframework/batch/core/schema-oracle10g.sql")
	private Resource dataReopsitorySchema;

	@Value("${oracle.url}")
	private String url;

	@Value("${oracle.username}")
	private String username;

	@Value("${oracle.password}")
	private String password;

	@Bean
	DataSource dataSource() throws SQLException {
		Properties cacheProps = new Properties();
		cacheProps.setProperty("MinLimit", "1");
		cacheProps.setProperty("MaxLimit", "10");
		cacheProps.setProperty("InitialLimit", "1");
		cacheProps.setProperty("ConnectionWaitTimeout", "5");
		cacheProps.setProperty("ValidateConnection", "true");

		OracleDataSource dataSource = new OracleDataSource();
		dataSource.setUser(username);
		dataSource.setPassword(password);
		dataSource.setURL(url);
		dataSource.setConnectionCachingEnabled(true);
		dataSource.setConnectionCacheProperties(cacheProps);

		return dataSource;
	}

	@Bean
	public DataSourceInitializer dataSourceInitializer(DataSource dataSource) throws MalformedURLException {
		ResourceDatabasePopulator databasePopulator = new ResourceDatabasePopulator();

		databasePopulator.addScript(dropReopsitoryTables);
		databasePopulator.addScript(dataReopsitorySchema);
		databasePopulator.setIgnoreFailedDrops(true);

		DataSourceInitializer initializer = new DataSourceInitializer();
		initializer.setDataSource(dataSource);
		initializer.setDatabasePopulator(databasePopulator);

		return initializer;
	}

	public JobLauncher getJobLauncher() throws Exception {
		SimpleJobLauncher jobLauncher = new SimpleJobLauncher();
		jobLauncher.setJobRepository(getJobRepository());
		jobLauncher.afterPropertiesSet();
		return jobLauncher;
	}

	private JobRepository getJobRepository() throws Exception {
		JobRepositoryFactoryBean factory = new JobRepositoryFactoryBean();
		factory.setDataSource(dataSource());
		factory.setTransactionManager(getTransactionManager());
		factory.afterPropertiesSet();
		return (JobRepository) factory.getObject();
	}

	private PlatformTransactionManager getTransactionManager() {
		return new ResourcelessTransactionManager();
	}
}