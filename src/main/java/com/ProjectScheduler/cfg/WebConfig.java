package com.ProjectScheduler.cfg;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@Configuration

@ComponentScan(basePackages = "com.ProjectScheduler.*")
@ConfigurationProperties
@PropertySource(value= "file:///${app.properties.path}")
public class WebConfig extends WebMvcConfigurerAdapter {

//  @Override
//  public void addInterceptors(InterceptorRegistry registry) {
//    registry.addInterceptor(new MultiTenancyInterceptor());
//  }
}
