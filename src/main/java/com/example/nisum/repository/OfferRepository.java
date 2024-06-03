package com.example.nisum.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.example.nisum.entity.Offer;

@Repository
public interface OfferRepository extends MongoRepository<Offer, String>{

	List<Offer> findByOfferIdIn(List<String> ids);

}
