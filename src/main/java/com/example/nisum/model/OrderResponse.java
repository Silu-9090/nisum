package com.example.nisum.model;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderResponse {
	
	private String orderNumber;
	
	private List<ResponseItem> items;
	
	private double orderTotal;
	
	private String promoCode;

}
