package com.turmeric.service;

import org.springframework.http.ResponseEntity;

import com.turmeric.model.CreateDeliveryRequest;
import com.turmeric.model.UpdateDeliveryRequest;

public interface DeliveryService {

	ResponseEntity<?> createDelivery(CreateDeliveryRequest request);

	ResponseEntity<?> getDeliveries();

	ResponseEntity<?> getDelivery(String deliveryId);

	ResponseEntity<?> getDeliveryByOrderId(String orderId);

	ResponseEntity<?> updateDelivery(UpdateDeliveryRequest request);

	ResponseEntity<?> deleteDelivery(String deliveryId);

	ResponseEntity<?> getDeliveriesByCustomerId(String customerId);

}
