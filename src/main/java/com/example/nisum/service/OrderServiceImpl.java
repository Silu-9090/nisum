package com.example.nisum.service;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.chrono.ChronoLocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.example.nisum.entity.Offer;
import com.example.nisum.entity.OrderDiscountEntity;
import com.example.nisum.entity.OrderResponseEntity;
import com.example.nisum.entity.ResponseItemEntity;
import com.example.nisum.exception.OfferNotFoundException;
import com.example.nisum.model.Item;
import com.example.nisum.model.OrderDiscount;
import com.example.nisum.model.OrderRequest;
import com.example.nisum.model.OrderResponse;
import com.example.nisum.model.ResponseItem;
import com.example.nisum.repository.OfferRepository;
import com.example.nisum.repository.OrderResponseRepository;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class OrderServiceImpl implements OrderService {
    @Autowired
    private OfferRepository offerRepository;
    
    @Autowired
    private OrderResponseRepository orderResponseRepository;
    
    @Autowired
    private KafkaProducerService kafkaProducerService;

    @Override
    public OrderResponse saveOrder(OrderRequest request) {
    	List<Offer> offers = getOffersFromCache(request.getOffers());
    	if(offers.isEmpty()) {
    		throw new OfferNotFoundException(
    				HttpStatus.NOT_FOUND.value(),
    				HttpStatus.NOT_FOUND.toString(),
    				"Offers Not found for requested Ids"
    				);
    	}
    	
        List<ResponseItem> items = request.getItems().stream()
                .flatMap(item -> request.getOffers().stream()
                        .map(offerId -> offers.stream()
                                .filter(offer -> offer.getOfferId().equals(offerId))
                                .findFirst()
                                .map(offer -> validateAndApplyOffer(item, offer))
                                .orElseGet(() -> createResponseItemWithoutDiscount(item))
                        )
                )
                .collect(Collectors.toList());

        double totalOrderAmount = items.stream()
                .mapToDouble(item -> (item.getQty() * item.getUnitPrice()) - item.getDiscount().getAmount())
                .sum();

        OrderResponse orderResponse = OrderResponse.builder()
        		.orderNumber(request.getOrderNumber())
        		.items(items)
        		.orderTotal(totalOrderAmount)
        		.promoCode(request.getPromoCode())
        		.build();
        
        //save Order reponse to postgre
        OrderResponseEntity orderResponseEntity = convertToOrderResponseEntity(orderResponse);
        orderResponseRepository.save(orderResponseEntity);
        
        //publish Order response to kafka topic
        kafkaProducerService.sendMessage(orderResponse);
        log.info("Message Published to Kafka topic: {}", orderResponse);
        
        return orderResponse;
    }
    
    @Cacheable(value = "offerCache", key = "#offerIds")
    private List<Offer> getOffersFromCache(List<String> offerIds) {
    	log.info("Fetching offers from MongoDB for offer IDs: {}", offerIds);
		return offerRepository.findByOfferIdIn(offerIds);
	}

	private OrderResponseEntity convertToOrderResponseEntity(OrderResponse orderResponse) {
		List<ResponseItemEntity> itemEntities = orderResponse.getItems().stream()
                .map(this::convertToResponseItemEntity)
                .collect(Collectors.toList());
		
        OrderResponseEntity entity = OrderResponseEntity.builder()
        		.orderNumber(orderResponse.getOrderNumber())
        		.orderTotal(orderResponse.getOrderTotal())
        		.promoCode(orderResponse.getPromoCode())
        		.items(itemEntities)
        		.build();

        return entity;
    }

    private ResponseItemEntity convertToResponseItemEntity(ResponseItem responseItem) {
    	OrderDiscountEntity discountEntity = OrderDiscountEntity.builder()
        		.amount(responseItem.getDiscount().getAmount())
        		.offersApplied(responseItem.getDiscount().getOffersApplied())
        		.build();
    	
        ResponseItemEntity entity = ResponseItemEntity.builder()
        		.itemCode(responseItem.getItemCode())
        		.productGroupName(responseItem.getProductGroupName())
        		.qty(responseItem.getQty())
        		.unitPrice(responseItem.getUnitPrice())
        		.discount(discountEntity)
        		.build();

        return entity;
    }

    private ResponseItem validateAndApplyOffer(Item item, Offer offer) {
        if (validateOrder(item, offer)) {
            return createResponseItemWithDiscount(item, offer);
        } else {
            return createResponseItemWithoutDiscount(item);
        }
    }

    private ResponseItem createResponseItemWithDiscount(Item item, Offer offer) {
        OrderDiscount discount = OrderDiscount.builder()
        		.amount(offer.getBenefit().getDiscount().getDiscountProducts().getDiscountValue())
        		.offersApplied(Collections.singletonList(offer.getOfferId()))
        		.build();
        
        ResponseItem responseItem = ResponseItem.builder()
        		.itemCode(item.getItemCode())
        		.productGroupName(item.getProductGroupName())
        		.qty(item.getQty())
        		.unitPrice(item.getUnitPrice())
        		.discount(discount)
        		.build();

        return responseItem;
    }

    private boolean validateOrder(Item item, Offer offer) {
        LocalDateTime orderDate = LocalDateTime.now();

        ChronoLocalDateTime<?> offerStartDate = convertStringToChronoLocalDateTime(offer.getOfferStartDate());
        ChronoLocalDateTime<?> offerEndDate = convertStringToChronoLocalDateTime(offer.getOfferEndDate());

        if (orderDate.isBefore(offerStartDate) || orderDate.isAfter(offerEndDate)) {
            return false;
        }

        if (!item.getProductGroupName().equals(offer.getConditions().getProductConditions().getProductGroupName())) {
            return false;
        }

        double totalAmount = item.getQty() * item.getUnitPrice();

        return totalAmount >= offer.getConditions().getProductConditions().getMinPurchaseAmt();
    }

    private ChronoLocalDateTime<?> convertStringToChronoLocalDateTime(String date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSX");
        ZonedDateTime zonedDateTime = ZonedDateTime.parse(date, formatter);
        return zonedDateTime.toLocalDateTime();
    }

    private ResponseItem createResponseItemWithoutDiscount(Item item) {
        OrderDiscount discount = OrderDiscount.builder()
        		.amount(0)
        		.offersApplied(Collections.emptyList())
        		.build();
        
        ResponseItem responseItem = ResponseItem.builder()
        		.itemCode(item.getItemCode())
        		.productGroupName(item.getProductGroupName())
        		.qty(item.getQty())
        		.unitPrice(item.getUnitPrice())
        		.discount(discount)
        		.build();

        return responseItem;
    }
}
