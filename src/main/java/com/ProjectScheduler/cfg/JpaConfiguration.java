package com.ProjectScheduler.cfg;

import java.util.Properties;

import javax.naming.NamingException;
import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
@Configuration
@EnableJpaRepositories(basePackages = "com.ProjectScheduler.*",
        entityManagerFactoryRef = "entityManagerFactory",
        transactionManagerRef = "transactionManager")
@EnableTransactionManagement
public class JpaConfiguration {

	 @Autowired
	 DataSource datasource;
	 
	    @Bean
	    public LocalContainerEntityManagerFactoryBean entityManagerFactory() throws NamingException {
	        LocalContainerEntityManagerFactoryBean factoryBean = new LocalContainerEntityManagerFactoryBean();
	        factoryBean.setDataSource(datasource);
	        factoryBean.setPackagesToScan(new String[] { "com.ProjectScheduler.*" });
	        factoryBean.setJpaVendorAdapter(jpaVendorAdapter());
	        factoryBean.setJpaProperties(jpaProperties());
	        return factoryBean;
	    }
	 
	    /*
	     * Provider specific adapter.
	     */
	    @Bean
	    public JpaVendorAdapter jpaVendorAdapter() {
	        HibernateJpaVendorAdapter hibernateJpaVendorAdapter = new HibernateJpaVendorAdapter();
	        return hibernateJpaVendorAdapter;
	    }
	    
	 
	    /*
	     * Here you can specify any provider specific properties.
	     */
	    private Properties jpaProperties() {
	        Properties properties = new Properties();
	        properties.put("hibernate.dialect", "org.hibernate.dialect.MySQL5Dialect");
	        properties.put("hibernate.hbm2ddl.auto", "update");
	        properties.put("hibernate.show_sql", "true");
	        return properties;
	    }
	 
	    @Bean
	    @Autowired
	    public PlatformTransactionManager transactionManager(EntityManagerFactory emf) {
	        JpaTransactionManager txManager = new JpaTransactionManager();
	        txManager.setEntityManagerFactory(emf);
	        return txManager;
	    }
}
