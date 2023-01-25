package com.turmeric.service;

import org.springframework.http.ResponseEntity;

import com.turmeric.model.CreateCustomerRequest;
import com.turmeric.model.UpdateCustomerRequest;

public interface CustomerService {

	ResponseEntity<?> createCustomer(CreateCustomerRequest request);

	ResponseEntity<?> getCustomers(String searchInput);
	
	ResponseEntity<?> getCustomer(String customerId);

	ResponseEntity<?> updateCustomer(UpdateCustomerRequest request);

	ResponseEntity<?> deleteCustomer(String customerId);
}
