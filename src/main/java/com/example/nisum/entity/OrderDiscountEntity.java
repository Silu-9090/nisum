package com.example.nisum.entity;

import java.util.List;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.JoinColumn;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Builder;
import lombok.Data;

@Entity
@Table(name = "order_discount")
@Data
@Builder
public class OrderDiscountEntity {

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private double amount;

    @ElementCollection
    @CollectionTable(name = "offers_applied", joinColumns = @JoinColumn(name = "order_discount_id"))
    @Column(name = "offer")
    private List<String> offersApplied;
}
