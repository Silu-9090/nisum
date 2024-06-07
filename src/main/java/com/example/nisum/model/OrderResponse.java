package com.example.nisum.model;

import java.util.List;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OrderResponse {
	
	private String orderNumber;
	
	private List<ResponseItem> items;
	
	private double orderTotal;
	
	private String promoCode;

}
