package com.example.nisum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.nisum.entity.OrderResponseEntity;

@Repository
public interface OrderResponseRepository extends JpaRepository<OrderResponseEntity, Long>{

}
