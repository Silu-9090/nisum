package com.example.nisum.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.nisum.entity.Offer;
import com.example.nisum.service.OfferService;

@RestController
@RequestMapping("/api/offer")
public class OfferController {
   
	@Autowired
	private OfferService offerService;
	
	@PostMapping("/save")
	public void saveOffer(@RequestBody Offer offer) {
		offerService.saveOffer(offer);
	}
	
	@GetMapping("/by/{ids}")
	public List<Offer> getAllOffer(@PathVariable List<String> ids) {
		return offerService.getOfferByIds(ids);
	}
}
