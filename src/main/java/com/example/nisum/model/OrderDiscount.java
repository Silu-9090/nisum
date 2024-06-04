package com.example.nisum.model;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderDiscount {

	private double amount;
	
	private List<String> offersApplied;
}
