package com.example.nisum.service;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.chrono.ChronoLocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

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
public class OrderServiceImpl implements OrderService {
    
    @Autowired
    private OfferRepository offerRepository;

    @Override
    public OrderResponse saveOrder(OrderRequest request) {

        List<Offer> offers = offerRepository.findByOfferIdIn(request.getOffers());
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

        OrderResponse orderResponse = new OrderResponse();
        orderResponse.setOrderNumber(request.getOrderNumber());
        orderResponse.setItems(items);
        orderResponse.setOrderTotal(totalOrderAmount);
        orderResponse.setPromoCode(request.getPromoCode());

        return orderResponse;
    }

    private ResponseItem validateAndApplyOffer(Item item, Offer offer) {
        if (validateOrder(item, offer)) {
            return createResponseItemWithDiscount(item, offer);
        } else {
            return createResponseItemWithoutDiscount(item);
        }
    }

    private ResponseItem createResponseItemWithDiscount(Item item, Offer offer) {
        ResponseItem responseItem = new ResponseItem();
        OrderDiscount discount = new OrderDiscount();
        discount.setAmount(offer.getBenefit().getDiscount().getDiscountProducts().getDiscountValue());
        discount.setOffersApplied(Collections.singletonList(offer.getOfferId()));

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
        ResponseItem responseItem = new ResponseItem();
        OrderDiscount discount = new OrderDiscount();
        discount.setAmount(0);
        discount.setOffersApplied(Collections.emptyList());

        responseItem.setItemCode(item.getItemCode());
        responseItem.setProductGroupName(item.getProductGroupName());
        responseItem.setQty(item.getQty());
        responseItem.setUnitPrice(item.getUnitPrice());
        responseItem.setDiscount(discount);

        return responseItem;
    }
}
