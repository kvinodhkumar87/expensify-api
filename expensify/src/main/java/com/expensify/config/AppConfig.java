package com.expensify.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoDbFactory;
import org.springframework.scheduling.annotation.EnableAsync;

import com.mongodb.MongoClientURI;

@Configuration
@EnableAsync
@PropertySource("classpath:application.properties")
@ComponentScan(basePackages = "com.expensify")
public class AppConfig {
	
		@Autowired
		private Environment env;

		@Bean
		public MongoDbFactory mongoDbFactory() throws Exception {
			MongoClientURI mongodbUri = new MongoClientURI(env.getProperty("spring.data.mongodb.uri"));
			return new SimpleMongoDbFactory(mongodbUri);
		}

		@Bean
		public MongoTemplate mongoTemplate() throws Exception {
			MongoTemplate mongoTemplate = new MongoTemplate(mongoDbFactory());
			return mongoTemplate;
		}
		


}
