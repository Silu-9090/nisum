package com.example.nisum.model;

import lombok.Data;

@Data
public class DiscountProducts {

	private String discountType;
	
    private double discountValue;
    
    private boolean allowNegative;

}
