package com.example.nisum.model;

import java.util.List;

import lombok.Data;

@Data
public class OrderRequest {

	private String orderNumber;
	
	private List<Item> items;
	
	private double orderTotal;
	
	private String promoCode;
	
	private List<String> offers;
}
