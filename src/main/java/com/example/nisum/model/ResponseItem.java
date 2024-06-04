package com.example.nisum.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResponseItem {

	private String itemCode;
	
	private String productGroupName;
	
	private int qty;
	
	private double unitPrice;
	
	private OrderDiscount discount;
}
