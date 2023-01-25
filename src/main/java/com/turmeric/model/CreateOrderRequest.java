package com.turmeric.model;

import java.util.Date;

public class CreateOrderRequest {
	private String customerId;
	private Date orderDate;
	private String productType;
	private int packingSize;
	private int quantity;

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

	public int getPackingSize() {
		return packingSize;
	}

	public void setPackingSize(int packingSize) {
		this.packingSize = packingSize;
	}

	public int getQuantity() {
		return quantity;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}

}
