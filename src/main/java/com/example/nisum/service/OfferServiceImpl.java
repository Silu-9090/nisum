package com.example.nisum.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.nisum.entity.Offer;
import com.example.nisum.repository.OfferRepository;

@Service
public class OfferServiceImpl implements OfferService{
	
	@Autowired
	private OfferRepository offerRepository;

	@Override
	public void saveOffer(Offer offer) {
		offerRepository.save(offer);
	}

	@Override
	public Optional<Offer> getOfferById(String id) {
		
		Optional<Offer> offer = offerRepository.findById(id);
		return offer;
	}

	@Override
	public List<Offer> getOfferByIds(List<String> ids) {
		return offerRepository.findByOfferIdIn(ids);
	}

}
