package com.ProjectScheduler.cfg;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.Environment;

public class DataSourceConfig {

	 @Autowired
	  private Environment env;
	  @Primary
	  public DataSource masterDataSource() {
	    return DataSourceBuilder
	            .create()
	            .driverClassName(env.getProperty("spring.Datasource.driver-class-name"))
	            .username(env.getProperty("spring.Datasource.username"))
	            .password(env.getProperty("spring.Datasource.password"))
	            .url(env.getProperty("spring.Datasource.url"))
	            .build();

	  }
}
