package com.example.nisum.controller;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.nisum.model.OrderRequest;
import com.example.nisum.model.OrderResponse;
import com.example.nisum.service.OrderService;

@RestController
@RequestMapping("/api/order")
@Validated
public class OrderController {
	
	@Autowired
	private OrderService orderService;

	@PostMapping("/save")
	public OrderResponse saveOrder(@RequestBody @Valid OrderRequest request) {
		 
		return orderService.saveOrder(request);
	} 
}
