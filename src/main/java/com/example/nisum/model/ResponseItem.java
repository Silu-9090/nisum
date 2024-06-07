package com.example.nisum.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ResponseItem {

	private String itemCode;
	
	private String productGroupName;
	
	private int qty;
	
	private double unitPrice;
	
	private OrderDiscount discount;
}
