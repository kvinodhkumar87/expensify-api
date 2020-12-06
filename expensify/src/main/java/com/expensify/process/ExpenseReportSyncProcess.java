package com.expensify.process;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.expensify.entities.ProcessConfig;
import com.expensify.repos.ProcessConfigRepo;
import com.expensify.service.APIService;
import com.expensify.utils.DateUtils;

@Component
public class ExpenseReportSyncProcess {
	
	private static final Logger logger = LoggerFactory.getLogger(ExpenseReportSyncProcess.class);

	@Autowired
	private ProcessConfigRepo processConfigRepo;
	@Autowired
	private APIService apiService;

	@Value("${partner.user.id}")
	private String partnerUserId;
	@Value("${partner.user.secret}")
	private String partnerUserSecret;
	@Value("${synch.sleep.time}")
	private Long sleepTime;

	public void execute() {
		while (true) {
			ProcessConfig config = processConfigRepo.findById("EXPORT_REPORT_SYNC_PROCESS").orElse(null);
			String startDate = "1990-01-01";
			if (config != null) {
				startDate = DateUtils.formatDate(config.getLastRunDate());
			}
			apiService.syncExpenseReport(partnerUserId, partnerUserSecret, startDate);
			updateProcessConfig(config);
			try {
				Thread.sleep(sleepTime);
			} catch (InterruptedException e) {
				logger.error("Error occured while sleep", e);
			}
		}
	}

	public void updateProcessConfig(ProcessConfig config) {
		if (config == null) {
			config = new ProcessConfig();
			config.setId("EXPORT_REPORT_SYNC_PROCESS");
		}
		config.setLastRunDate(new Date());
		processConfigRepo.save(config);
	}

}
