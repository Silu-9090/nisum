package com.example.nisum.model;

import lombok.Data;

@Data
public class ProductConditions {

	private String productGroupName;
	
    private int minProductQuantity;
    
    private double minPurchaseAmt;
}
