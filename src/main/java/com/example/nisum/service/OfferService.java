package com.example.nisum.service;

import java.util.List;
import java.util.Optional;

import com.example.nisum.entity.Offer;

public interface OfferService {
	
  public void saveOffer(Offer offer);

  public Optional<Offer> getOfferById(String id);

  public List<Offer> getOfferByIds(List<String> ids);
  
}
