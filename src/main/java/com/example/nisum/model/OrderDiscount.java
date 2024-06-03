package com.example.nisum.model;

import java.util.List;

import lombok.Data;

@Data
public class OrderDiscount {

	private double amount;
	
	private List<String> offersApplied;
}
