package com.turmeric.service;

import org.springframework.http.ResponseEntity;

import com.turmeric.model.ApproveRequest;
import com.turmeric.model.ReportFilter;

public interface TurmericService {

	ResponseEntity<?> getUniqueValues();

	ResponseEntity<?> getCounts();

	ResponseEntity<?> loginAuthentication(String userName, String password);

	ResponseEntity<?> getPendingAccounts();

	ResponseEntity<?> accountApproval(ApproveRequest request);

	ResponseEntity<?> generateReport(ReportFilter request);
}
