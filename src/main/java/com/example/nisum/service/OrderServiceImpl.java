package com.example.nisum.service;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.chrono.ChronoLocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.nisum.entity.Offer;
import com.example.nisum.model.Item;
import com.example.nisum.model.OrderDiscount;
import com.example.nisum.model.OrderRequest;
import com.example.nisum.model.OrderResponse;
import com.example.nisum.model.ResponseItem;
import com.example.nisum.repository.OfferRepository;

@Service
public class OrderServiceImpl implements OrderService{
	
	@Autowired
	private OfferRepository offerRepository;

	@Override
	public OrderResponse saveOrder(OrderRequest request) {
		
		OrderResponse orderResponse = new OrderResponse();
		List<ResponseItem> items = new ArrayList<>();
		
		List<Offer> offers = offerRepository.findByOfferIdIn(request.getOffers());
		
		for(Item item : request.getItems()) {
			
			for(String offerId : request.getOffers()) {
				
				Optional<Offer> offerOpt = offers.stream()
	                    .filter(offer -> offer.getOfferId().equals(offerId))
	                    .findFirst();

	            if (offerOpt.isPresent()) {
	                ResponseItem responseItem = validateAndApplyOffer(item, offerOpt.get());
	                items.add(responseItem);
	            }
			}
		}
		
		double totalOrderAmount = items.stream()
				.mapToDouble(item -> (item.getQty() * item.getUnitPrice()) - item.getDiscount().getAmount())
				.sum();
				
		orderResponse.setOrderNumber(request.getOrderNumber());
		orderResponse.setItems(items);
		orderResponse.setOrderTotal(totalOrderAmount);
		orderResponse.setPromoCode(request.getPromoCode());
		
		return orderResponse; 
	}

	private ResponseItem validateAndApplyOffer(Item item, Offer offer) {
	  if (validateOrder(item, offer)) {
            return applyDiscount(item, offer);
      } else {
    	  
    	ResponseItem responseItem = new ResponseItem();
  		OrderDiscount discount = new OrderDiscount();
  		discount.setAmount(0);
  		discount.setOffersApplied(Collections.EMPTY_LIST);
  		
  		responseItem.setItemCode(item.getItemCode());
  		responseItem.setProductGroupName(item.getProductGroupName());
  		responseItem.setQty(item.getQty());
  		responseItem.setUnitPrice(item.getUnitPrice());
  		responseItem.setDiscount(discount);
  		
  		return responseItem;
      } 
	}

	private ResponseItem applyDiscount(Item item, Offer offer) {
		ResponseItem responseItem = new ResponseItem();
		OrderDiscount discount = new OrderDiscount();
		discount.setAmount(offer.getBenefit().getDiscount().getDiscountProducts().getDiscountValue());
		discount.setOffersApplied(Arrays.asList(offer.getOfferId()));
		
		responseItem.setItemCode(item.getItemCode());
		responseItem.setProductGroupName(item.getProductGroupName());
		responseItem.setQty(item.getQty());
		responseItem.setUnitPrice(item.getUnitPrice());
		responseItem.setDiscount(discount);
		
		return responseItem;
	}

	private boolean validateOrder(Item item, Offer offer) {
		
		LocalDateTime orderDate = LocalDateTime.now(); 
		
        ChronoLocalDateTime<?> offerStartDate = convertStringToChronoLocalDateTime(offer.getOfferStartDate());
        ChronoLocalDateTime<?> offerEndDate = convertStringToChronoLocalDateTime(offer.getOfferEndDate());
        
        if (orderDate.isBefore(offerStartDate) || orderDate.isAfter(offerEndDate)) {
            return false;
        }
        
        if(!item
        		.getProductGroupName()
        		.equals(offer.getConditions()
        		.getProductConditions()
        		.getProductGroupName())
          ) {
        	return false;
        }


        double totalAmount = item.getQty() * item.getUnitPrice();
        
        if (totalAmount < offer.getConditions().getProductConditions().getMinPurchaseAmt()) {
            return false;
        }

        return true;
	}

	private ChronoLocalDateTime<?> convertStringToChronoLocalDateTime(String date) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSX");
        ZonedDateTime zonedDateTime = ZonedDateTime.parse(date, formatter);
        return zonedDateTime.toLocalDateTime();
	}

}
