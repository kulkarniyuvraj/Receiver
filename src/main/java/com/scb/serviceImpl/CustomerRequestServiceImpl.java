package com.scb.serviceImpl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.scb.dao.CustomerDataReposatory;
import com.scb.model.CustomerRequest;
import com.scb.model.CustomerRequestData;
import com.scb.model.CustomerResponse;
import com.scb.service.CustomerRequestService;
import com.scb.utils.SCBCommonMethods;

import lombok.extern.log4j.Log4j2;

@Service @Log4j2
public class CustomerRequestServiceImpl implements CustomerRequestService {
	@Autowired
	private CustomerDataReposatory customerDataReposatory;
	@Autowired
	private SCBCommonMethods commonMethods;

	@Override
	public CustomerResponse customerRequestHandleService(CustomerRequest customerRequest) {
		List<CustomerRequestData> customerList = customerDataReposatory
				.findByCustomerNameAndCustomerId(customerRequest.getCustomerName(), customerRequest.getCustomerId() );
		if (customerList.isEmpty()) {
			log.info("Unique request");
			return commonMethods.getSuccessResponse(
					customerDataReposatory.save(commonMethods.getCustomerDataFromRequest(customerRequest)));
		} else {
			log.info("Duplicate request ");
			return commonMethods.getErrorResponse("Duplicate User");
		}

	}

}
