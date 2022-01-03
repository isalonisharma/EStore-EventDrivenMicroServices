package com.estore.payment.data.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.estore.payment.data.entity.PaymentEntity;

public interface PaymentRepository extends JpaRepository <PaymentEntity, String>{
    
}