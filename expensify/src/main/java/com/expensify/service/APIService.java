package com.expensify.service;

import org.apache.commons.lang.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.expensify.entities.ExpenseReport;
import com.expensify.repos.ExpenseReportRepo;
import com.expensify.utils.RestUtils;

@Service
public class APIService {

	private static String serverUrl = "https://integrations.expensify.com";
	
	@Autowired
	private ExpenseReportRepo expenseReportRepo;

	public void syncExpenseReport(String partnerUserId, String partnerUserSecret, String startDate) {
		String apiUrl = serverUrl + "/Integration-Server/ExpensifyIntegrations";
		String type = "file";
		String template = "<#if addHeader == true>\n"
				+ "    Merchant,Original Amount,Category,Report number,Expense number<#lt>\n" + "</#if>\n"
				+ "<#assign reportNumber = 1>\n" + "<#assign expenseNumber = 1>\n" + "<#list reports as report>\n"
				+ "    <#list report.transactionList as expense>\n" + "        ${expense.merchant},<#t>\n"
				+ "        <#-- note: expense.amount prints the original amount only -->\n"
				+ "        ${expense.amount},<#t>\n" + "        ${expense.category},<#t>\n"
				+ "        ${expense.transactionID},<#t>\n" + "        ${report.reportID}<#lt>\n"
				+ "        <#assign expenseNumber = expenseNumber + 1>\n" + "    </#list>\n"
				+ "    <#assign reportNumber = reportNumber + 1>\n" + "</#list>";

		JSONObject inputSettings = new JSONObject();
		inputSettings.put("type", "combinedReportData");

		JSONObject filters = new JSONObject();
		filters.put("startDate", startDate);
		inputSettings.put("filters", filters);
		JSONObject credentials = new JSONObject();
		credentials.put("partnerUserID", partnerUserId);
		credentials.put("partnerUserSecret", partnerUserSecret);

		JSONObject outputSettings = new JSONObject();
		outputSettings.put("fileExtension", "csv");

		JSONObject requestJobDescription = new JSONObject();
		requestJobDescription.put("type", type);
		requestJobDescription.put("credentials", credentials);
		requestJobDescription.put("inputSettings", inputSettings);
		requestJobDescription.put("outputSettings", outputSettings);

		JSONObject params = new JSONObject();
		JSONArray arr = new JSONArray();
		arr.put("returnRandomFileName");
		params.put("immediateResponse", arr);
		requestJobDescription.put("onReceive", params);
		RestUtils restUtils = new RestUtils();
		String response = restUtils.post(apiUrl, "requestJobDescription", requestJobDescription.toString(), template);
		System.out.println(response);
		if (response != null) {
			String t = "download";
			JSONObject obj = new JSONObject();
			obj.put("fileName", response);
			obj.put("fileSystem", "integrationServer");
			obj.put("type", t);
			obj.put("credentials", credentials);
			String res = restUtils.post(apiUrl, "requestJobDescription", obj.toString(), null);
			System.out.println(res);
			if(res != null) {
				String str[] = res.split("\n");
				if(str != null) {
					if(str.length > 1) {
						for(int i = 1; i < str.length; i++) {
							String s = str[i];
							if(s != null) {
								String[] l = s.split(",");
									ExpenseReport report = new ExpenseReport();
									report.setVendorName(l[0]);
									if(StringUtils.isNotEmpty(l[1])) {
										report.setAmount(Double.parseDouble(l[1]));
									}
									report.setCategory(l[2]);
									report.setReportId(l[4]);
									expenseReportRepo.save(report);
							}
						}
					}
				}
			}
		}
	}

}
