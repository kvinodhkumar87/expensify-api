package com.expensify.repos;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.expensify.entities.ExpenseReport;

public interface ExpenseReportRepo extends MongoRepository<ExpenseReport, String> {

}
