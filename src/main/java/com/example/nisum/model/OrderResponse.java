package com.example.nisum.model;

import java.util.List;

import lombok.Data;

@Data
public class OrderResponse {
	
	private String orderNumber;
	
	private List<ResponseItem> items;
	
	private double orderTotal;
	
	private String promoCode;

}
