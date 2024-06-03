package com.example.nisum.service;

import com.example.nisum.model.OrderRequest;
import com.example.nisum.model.OrderResponse;

public interface OrderService {

	OrderResponse saveOrder(OrderRequest request);

}
