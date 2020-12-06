package com.expensify;

import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

import com.expensify.config.AppConfig;
import com.expensify.process.ExpenseReportSyncProcess;

@SpringBootApplication
public class ExpensifyApp {
	
	public static void main( String[] args ){
		ConfigurableApplicationContext ctx = new SpringApplicationBuilder(AppConfig.class).web(WebApplicationType.NONE).run(args);
		ExpenseReportSyncProcess bulkProcess = ctx.getBean(ExpenseReportSyncProcess.class);
		bulkProcess.execute();
    }
    
}
