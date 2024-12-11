package com.ecommerce.demo.services;

import com.ecommerce.demo.config.RabbitMQConfig;
import com.ecommerce.demo.entity.Payment;
import com.ecommerce.demo.entity.Transaction;
import com.ecommerce.demo.repository.TransactionRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class TransactionService {

    @Autowired
    private TransactionRepository transactionRepository;

    @RabbitListener(queues = RabbitMQConfig.QUEUE)
    public void consumePayments(Payment payment){

        log.info("Transaction queue message with id: {} consumed!",payment.getId());
        Transaction transaction = Transaction.builder()
                .amount(payment.getReceivedAmount())
                .user(payment.getUser())
                .customerOrder(payment.getOrder())
                .build();
        transactionRepository.save(transaction);

        log.info("Transaction :{} saved successfully!",transaction.getId());
    }
}
