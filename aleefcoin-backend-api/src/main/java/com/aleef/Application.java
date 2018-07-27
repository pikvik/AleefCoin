package com.aleef;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@Configuration
@EnableScheduling
@EnableAutoConfiguration 
@ComponentScan(basePackages = "com.aleef")
@PropertySource({"classpath:application.properties", "classpath:message.properties"})
public class Application extends SpringBootServletInitializer {

	private static final Class<Application> applicationClass = Application.class;
	
	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		return application.sources(applicationClass);
	}
	
}
