package com.ecommerce.demo.controllers;

import com.ecommerce.demo.dtos.PaymentRequest;
import com.ecommerce.demo.entity.Payment;
import com.ecommerce.demo.services.PaymentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/payments")
@Slf4j
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    @PostMapping
    public ResponseEntity<Payment> makePayment(@RequestBody PaymentRequest paymentRequest){
        return ResponseEntity.ok(paymentService.makePayment(paymentRequest));
    }

    @GetMapping
    public ResponseEntity<List<Payment>> getAllPayments(){
        return ResponseEntity.ok(paymentService.getAllPayments());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Payment> getPaymentById(@PathVariable Long id){
        return ResponseEntity.ok(paymentService.getPaymentById(id));
    }
}
