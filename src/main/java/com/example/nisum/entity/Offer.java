package com.example.nisum.entity;

import java.sql.Date;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.example.nisum.model.Benefit;
import com.example.nisum.model.Conditions;

import lombok.Data;

@Document(collection = "offer")
@Data
public class Offer {

	@Id
    private String offerId;
	
    private String offerStartDate;
    
    private String offerEndDate;
    
    private String promoCode;
    
    private Conditions conditions;
    
    private Benefit benefit;
    
    private List<String> itemCodes;
}
