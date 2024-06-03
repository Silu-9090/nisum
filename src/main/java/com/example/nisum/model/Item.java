package com.example.nisum.model;

import lombok.Data;

@Data
public class Item {
	
  private String itemCode;
  
  private String productGroupName;
  
  private Integer qty;
  
  private double unitPrice;
  
}
