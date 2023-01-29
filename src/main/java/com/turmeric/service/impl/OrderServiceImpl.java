package com.turmeric.service.impl;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.turmeric.model.AddPaymentRequest;
import com.turmeric.model.AmountMapper;
import com.turmeric.model.ApproveOrderRequest;
import com.turmeric.model.Counter;
import com.turmeric.model.CreateOrderRequest;
import com.turmeric.model.Order;
import com.turmeric.model.OrderRequest;
import com.turmeric.model.PaymentDetails;
import com.turmeric.model.Status;
import com.turmeric.model.UpdateOrderRequest;
import com.turmeric.service.OrderService;
import com.turmeric.service.SmsService;

@Service
public class OrderServiceImpl implements OrderService {
	@Autowired
	private MongoTemplate mongoTemplate;

	@Autowired
	private SmsService smsService;

	@Override
	public ResponseEntity<?> createOrderRequest(CreateOrderRequest request) {
//		this.smsService.triggerSms();
		OrderRequest order = new OrderRequest();
		BeanUtils.copyProperties(request, order);
		Query query = new Query();
		Counter counter = this.mongoTemplate.findOne(query, Counter.class);
		if (counter == null) {
			counter = new Counter();
		}
		int orderRequestCount = counter.getOrderRequestCount() + 1;
		order.setOrderRequestId("WOR-" + orderRequestCount);
		order.setStatus(Status.PENDING.getStatus());
		this.mongoTemplate.save(order);
		Update update = new Update();
		update.set("orderRequestCount", orderRequestCount);
		this.mongoTemplate.updateFirst(query, update, Counter.class);
		return new ResponseEntity<>("Order Request successfully created with Id- " + order.getOrderRequestId(),
				HttpStatus.OK);
	}

	@Override
	public ResponseEntity<?> getOrders(String searchInput) {
		Query query = new Query();
		if (StringUtils.isNotEmpty(searchInput)) {
			query = this.getSearchQuery(searchInput);
		}
		query.with(Sort.by(Sort.Direction.DESC, "createdAt"));

		List<Order> orders = this.mongoTemplate.find(query, Order.class);
		if (!CollectionUtils.isEmpty(orders)) {
			return new ResponseEntity<>(orders, HttpStatus.OK);
		} else {
			return new ResponseEntity<>(new ArrayList<>(), HttpStatus.OK);
		}
	}

	@Override
	public ResponseEntity<?> getRequestedOrders(String searchInput) {
		Query query = new Query();
		if (StringUtils.isNotEmpty(searchInput)) {
			query = this.getSearchQuery(searchInput);
		}
		query.addCriteria(Criteria.where("status").ne(Status.ACCEPTED));
		query.with(Sort.by(Sort.Direction.DESC, "createdAt"));
		List<OrderRequest> orders = this.mongoTemplate.find(query, OrderRequest.class);
		if (!CollectionUtils.isEmpty(orders)) {
			return new ResponseEntity<>(orders, HttpStatus.OK);
		} else {
			return new ResponseEntity<>(new ArrayList<>(), HttpStatus.OK);
		}
	}

	@Override
	public ResponseEntity<?> getOrderByCustomerId(String customerId, String searchInput) {
		Query query = new Query();
		if (StringUtils.isNotEmpty(searchInput)) {
			query = this.getSearchQuery(searchInput);
		}
		query.addCriteria(Criteria.where("customerId").is(customerId));
		query.with(Sort.by(Sort.Direction.DESC, "createdAt"));
		List<Order> orders = this.mongoTemplate.find(query, Order.class);
		if (!CollectionUtils.isEmpty(orders)) {
			return new ResponseEntity<>(orders, HttpStatus.OK);
		} else {
			return new ResponseEntity<>(new ArrayList<>(), HttpStatus.OK);
		}
	}

	@Override
	public ResponseEntity<?> updateOrder(UpdateOrderRequest request) {
		Query query = new Query();
		query.addCriteria(Criteria.where("orderId").is(request.getOrderId()));
		Order order = this.mongoTemplate.findOne(query, Order.class);
		if (order != null) {
			if (!request.getCustomerId().isEmpty()) {
				order.setCustomerId(request.getCustomerId());
			}
			if (request.getQuantity() != null) {
				order.setQuantity(request.getQuantity());
			}
			if (request.getPackingSize() != null) {
				order.setPackingSize(request.getPackingSize());
			}
			if (request.getProductType() != null) {
				order.setProductType(request.getProductType());
			}
			if (request.getOrderDate() != null) {
				order.setOrderDate(request.getOrderDate());
			}

			this.mongoTemplate.save(order);
			return new ResponseEntity<>("Order " + order.getOrderId() + " is successfully updated", HttpStatus.OK);
		} else {
			return new ResponseEntity<>("No order found with Id- " + request.getOrderId(), HttpStatus.NOT_FOUND);
		}
	}

	@Override
	public ResponseEntity<?> deleteOrder(String orderId) {
		Query query = new Query();
		query.addCriteria(Criteria.where("orderId").is(orderId));
		Order order = this.mongoTemplate.findOne(query, Order.class);
		if (order != null) {
			this.mongoTemplate.remove(order);
			return new ResponseEntity<>("Order " + orderId + " is successfully deleted", HttpStatus.OK);
		} else {
			return new ResponseEntity<>("No order found with Id-" + orderId, HttpStatus.NOT_FOUND);
		}
	}

	private Query getSearchQuery(String searchInput) {
		Query query = new Query();
		List<Criteria> criterias = new LinkedList<>();
		Criteria searchCriteria = new Criteria();
		searchCriteria.orOperator(
				Criteria.where("orderId")
						.regex(Pattern.compile(searchInput, Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE)),
				Criteria.where("productType")
						.regex(Pattern.compile(searchInput, Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE)),
				Criteria.where("packingSize")
						.regex(Pattern.compile(searchInput, Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE)),
				Criteria.where("customerId")
						.regex(Pattern.compile(searchInput, Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE)));
		criterias.add(searchCriteria);
		if (!CollectionUtils.isEmpty(criterias)) {
			Criteria criteria = new Criteria();
			criteria.andOperator(criterias.stream().toArray(Criteria[]::new));
			query.addCriteria(criteria);
		}
		return query;
	}

	@Override
	public ResponseEntity<?> getOrder(String orderId) {
		Query query = new Query();
		query.addCriteria(Criteria.where("orderId").is(orderId));
		Order order = this.mongoTemplate.findOne(query, Order.class);
		if (order != null) {
			return new ResponseEntity<>(order, HttpStatus.OK);
		} else {
			return new ResponseEntity<>(new Order(), HttpStatus.OK);
		}
	}

	@Override
	public ResponseEntity<?> approveOrder(ApproveOrderRequest request) {
		Query query = new Query();
		query.addCriteria(Criteria.where("orderRequestId").is(request.getOrderRequestId()));
		Update update = new Update();
		update.set("status", request.getStatus());
		update.set("reason", request.getReason());
		this.mongoTemplate.updateFirst(query, update, OrderRequest.class);
		if (request.getStatus().equals(Status.ACCEPTED.getStatus())) {
			OrderRequest orderRequest = this.mongoTemplate.findOne(query, OrderRequest.class);
			Order order = new Order();
			BeanUtils.copyProperties(orderRequest, order, "orderRequestId");
			Query orderQuery = new Query();
			Counter counter = this.mongoTemplate.findOne(orderQuery, Counter.class);
			if (counter == null) {
				counter = new Counter();
			}
			int orderCount = counter.getOrderCount() + 1;
			int rollCount = counter.getRollCount() + 1;
			order.setOrderId("WO-" + orderCount);
			order.setStatus(Status.ACCEPTED.getStatus());
			order.setAcceptedBy(request.getUserId());
			order.setTotalAmount(this.calculateOrderCost(orderRequest.getProductType(), orderRequest.getPackingSize(),
					orderRequest.getQuantity()));
			order.setPaymentPending(order.getTotalAmount());
			order.setRemainingQuantity(order.getQuantity());
			this.mongoTemplate.save(order);
			Update counterUpdate = new Update();
			counterUpdate.set("orderCount", orderCount);
			counterUpdate.set("rollCount", rollCount);
			this.mongoTemplate.updateFirst(orderQuery, counterUpdate, Counter.class);
			return new ResponseEntity<>("Order successfully created with Id- " + order.getOrderId(), HttpStatus.OK);
		}
		return new ResponseEntity<>("Unable to create order as order request is not accepted", HttpStatus.OK);
	}

	private int calculateOrderCost(String productType, int packingSize, int quantity) {
		Query query = new Query();
		AmountMapper mapper = this.mongoTemplate.findOne(query, AmountMapper.class);
		if (mapper != null) {
			Map<String, Map<Integer, Integer>> valueMap = mapper.getValues();
			if (valueMap.containsKey(String.valueOf(productType))) {
				for (Map.Entry<String, Map<Integer, Integer>> entry : valueMap.entrySet()) {
					if (entry.getKey().equalsIgnoreCase(productType)) {
						Map<Integer, Integer> map = entry.getValue();
						for (Map.Entry<Integer, Integer> mapEntry : map.entrySet()) {
							if (mapEntry.getKey() == packingSize) {
								int cost = mapEntry.getValue().intValue() * quantity;
								return cost;
							}
						}

					}
				}
			}
		}
		return 0;

	}

	@Override
	public ResponseEntity<?> addPaymentDetails(AddPaymentRequest request) {
		if (StringUtils.isEmpty(request.getOrderId())) {
			return new ResponseEntity<>("Order is not found", HttpStatus.NOT_FOUND);
		}
		Query query = new Query();
		Counter counter = this.mongoTemplate.findOne(query, Counter.class);
		query.addCriteria(Criteria.where("orderId").is(request.getOrderId()));
		Order order = this.mongoTemplate.findOne(query, Order.class);
		if (order == null) {
			return new ResponseEntity<>("Order is not found", HttpStatus.NOT_FOUND);
		}
		int paymentCount = counter.getPaymentCount();
		PaymentDetails payment = new PaymentDetails();
		BeanUtils.copyProperties(request, payment);
		payment.setPaymentId("P-" + (counter.getPaymentCount() + 1));
		int amountPaid = order.getAmountPaid();
		amountPaid += payment.getAmount();
		order.setAmountPaid(amountPaid);
		order.setPaymentPending(order.getTotalAmount() - amountPaid);
		counter.setPaymentCount(paymentCount + 1);
		this.mongoTemplate.save(counter);
		this.mongoTemplate.save(order);
		this.mongoTemplate.save(payment);
		return new ResponseEntity<>("Payment details added successfully", HttpStatus.OK);
	}

	@Override
	public ResponseEntity<?> deletePayment(String paymentId) {
		Query query = new Query();
		query.addCriteria(Criteria.where("paymentId").is(paymentId));
		PaymentDetails payment = this.mongoTemplate.findOne(query, PaymentDetails.class);
		if (payment != null) {
			String orderId = payment.getOrderId();
			Query orderQuery = new Query();
			query.addCriteria(Criteria.where("orderId").is(orderId));
			Order order = this.mongoTemplate.findOne(orderQuery, Order.class);
			if (order != null) {
				int amountPaid = order.getAmountPaid();
				amountPaid = amountPaid - payment.getAmount();
				order.setAmountPaid(amountPaid);
				order.setPaymentPending(order.getTotalAmount() - amountPaid);
				this.mongoTemplate.save(order);
			}
			this.mongoTemplate.remove(payment);
			return new ResponseEntity<>("Payment " + paymentId + " is successfully deleted", HttpStatus.OK);
		} else {
			return new ResponseEntity<>("No payment found with Id-" + paymentId, HttpStatus.NOT_FOUND);
		}
	}

	@Override
	public ResponseEntity<?> getPayments() {
		Query query = new Query();
		query.with(Sort.by(Sort.Direction.DESC, "createdAt"));
		List<PaymentDetails> payments = this.mongoTemplate.find(query, PaymentDetails.class);
		if (!CollectionUtils.isEmpty(payments)) {
			return new ResponseEntity<>(payments, HttpStatus.OK);
		} else {
			return new ResponseEntity<>(new ArrayList<>(), HttpStatus.OK);
		}
	}

	@Override
	public ResponseEntity<?> getPaymentByOrderId(String orderId) {
		Query query = new Query();
		query.addCriteria(Criteria.where("orderId").is(orderId));
		query.with(Sort.by(Sort.Direction.DESC, "createdAt"));
		List<PaymentDetails> payments = this.mongoTemplate.find(query, PaymentDetails.class);
		if (!CollectionUtils.isEmpty(payments)) {
			return new ResponseEntity<>(payments, HttpStatus.OK);
		} else {
			return new ResponseEntity<>(new ArrayList<>(), HttpStatus.OK);
		}
	}

}
