package com.scb.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.scb.model.CustomerRequestData;
import java.lang.String;
import java.util.List;

public interface CustomerDataReposatory extends JpaRepository<CustomerRequestData, Long> {
	List<CustomerRequestData> findByCustomerNameAndCustomerId(String customername, long customerId);

}
