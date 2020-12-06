package com.expensify.repos;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.expensify.entities.ProcessConfig;

public interface ProcessConfigRepo extends MongoRepository<ProcessConfig, String> {

}
