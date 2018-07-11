package com.scb.serviceImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import com.scb.config.CustomerConfig;
import com.scb.dao.CustomerDataReposatory;
import com.scb.model.AuditLog;
import com.scb.model.CustomerRequest;
import com.scb.model.CustomerRequestData;
import com.scb.model.CustomerResponse;
import com.scb.service.CustomerRequestService;
import com.scb.utils.SCBCommonMethods;

import lombok.extern.log4j.Log4j2;

@Service
@Log4j2
public class CustomerRequestServiceImpl implements CustomerRequestService {
	@Autowired
	private SCBCommonMethods commonMethods;
	@Autowired
	private CustomerConfig propertiesConfig;
	@Autowired
	private GcgInternalApiCall gcgInternalApiCall;

	@Override
	public CustomerResponse customerRequestHandleService(CustomerRequest customerRequest) {
		CustomerRequestData customerRequestData = commonMethods.getCustomerDataFromRequest(customerRequest);
		ResponseEntity<AuditLog> msAuditLogApiResponse = null;
		if (propertiesConfig.getIsEnableAuditLog().equalsIgnoreCase("yes")) {
			AuditLog parseAuditLog = commonMethods.getAuditLogDetails(customerRequestData);
			msAuditLogApiResponse = gcgInternalApiCall.msAuditLogApiCall(parseAuditLog);
		}
		ResponseEntity<CustomerResponse> customerResponseFromPersistDb = gcgInternalApiCall
				.msCustomerPersistApiCall(customerRequestData);
		CustomerRequestData responseFromDownStream = null;
		if (customerResponseFromPersistDb.getBody().getResponseCode() == 200) {
			ResponseEntity<CustomerResponse> customerResponseFromDownStream = gcgInternalApiCall
					.msDownStreamCall(customerResponseFromPersistDb.getBody().getCustomerRequestData());
			if (customerResponseFromDownStream.getBody().getResponseCode() != 200) {
				return customerResponseFromDownStream.getBody();
			}
		} else {
			return customerResponseFromPersistDb.getBody();
			// return commonMethods.getErrorResponse("Error While calling
			// persister");
		}
		return commonMethods.getSuccessResponse(responseFromDownStream);
	}

}
