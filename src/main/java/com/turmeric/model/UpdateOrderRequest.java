package com.turmeric.model;

import java.util.Date;

public class UpdateOrderRequest {
	private String orderId;
	private String customerId;
	private Date orderDate;
	private String productType;
	private Integer packingSize;
	private Integer quantity;

	public String getOrderId() {
		return orderId;
	}

	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}

	public String getCustomerId() {
		return customerId;
	}

	public void setCustomerId(String customerId) {
		this.customerId = customerId;
	}

	public Date getOrderDate() {
		return orderDate;
	}

	public void setOrderDate(Date orderDate) {
		this.orderDate = orderDate;
	}

	public String getProductType() {
		return productType;
	}

	public void setProductType(String productType) {
		this.productType = productType;
	}

	public Integer getPackingSize() {
		return packingSize;
	}

	public void setPackingSize(Integer packingSize) {
		this.packingSize = packingSize;
	}

	public Integer getQuantity() {
		return quantity;
	}

	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}
}
