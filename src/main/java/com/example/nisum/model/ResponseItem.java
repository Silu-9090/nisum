package com.example.nisum.model;

import lombok.Data;

@Data
public class ResponseItem {

	private String itemCode;
	
	private String productGroupName;
	
	private int qty;
	
	private double unitPrice;
	
	private OrderDiscount discount;
}
