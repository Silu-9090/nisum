package com.example.nisum.model;

import java.util.List;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OrderDiscount {

	private double amount;
	
	private List<String> offersApplied;
}
