package com.ecommerce.demo.services;

import com.ecommerce.demo.config.RabbitMQConfig;
import com.ecommerce.demo.dtos.PaymentRequest;
import com.ecommerce.demo.entity.*;
import com.ecommerce.demo.repository.PaymentRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@Slf4j
public class PaymentService {


    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private OrderService orderService;
    @Autowired
    private UserService userService;

    @Transactional
    public Payment makePayment(PaymentRequest paymentRequest){
        CustomerOrder customerOrder = orderService.getOrderById(paymentRequest.getOrderId());
        User user = userService.getUserById(paymentRequest.getUserId());
        BigDecimal paymentAmount = paymentRequest.getAmount();
        BigDecimal orderAmount = customerOrder.getOrderAmount();

        if(customerOrder.getOrderStatus().equals(OrderStatus.Success)){
            throw new RuntimeException("Order payment has been already received!");
        }

        Payment payment = Payment.builder()
                .order(customerOrder)
                .user(user)
                .receivedAmount(paymentAmount)
                .build();

        //check amount and update order status if payment successful
        if(paymentAmount.compareTo(orderAmount)<0){
            payment.setPaymentStatus(PaymentStatus.Failed);
            payment.setErrorMessage("Insufficient amount!");
            log.info("Payment failed due to insufficient amount!");
            orderService.updateOrderStatus(customerOrder, OrderStatus.Failed);
        } else if (paymentAmount.compareTo(orderAmount)>0) {
            payment.setPaymentStatus(PaymentStatus.Failed);
            payment.setErrorMessage("Payment amount exceeding order amount!");
            log.info("Payment failed due to payment amount exceeding order amount!");
            orderService.updateOrderStatus(customerOrder,OrderStatus.Failed);
        }else {
            payment.setPaymentStatus(PaymentStatus.Success);
            log.info("Payment processed successfully!");
            orderService.updateOrderStatus(customerOrder,OrderStatus.Success);

            try {
                rabbitTemplate.convertAndSend(RabbitMQConfig.EXCHANGE, RabbitMQConfig.ROUTING_KEY, payment);
                log.info("Payment message sent to transaction queue!");
            }catch (Exception e){
                log.error(e.getMessage());
            }
        }
        return paymentRepository.save(payment);
    }

    public List<Payment> getAllPayments() {
        return paymentRepository.findAll();
    }

    public Payment getPaymentById(Long id) {
        return paymentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Payment not found with id " + id));
    }
}
