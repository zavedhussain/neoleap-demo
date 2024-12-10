package com.ecommerce.demo.services;

import com.ecommerce.demo.entity.ProductOrder;
import com.ecommerce.demo.repository.OrderRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    public List<ProductOrder> getAllOrders() {
        return orderRepository.findAll();
    }

    public ProductOrder getOrderById(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found with id " + id));
    }

    public ProductOrder createOrder(ProductOrder productOrder) {
        return orderRepository.save(productOrder);
    }

    public ProductOrder updateOrder(Long id, ProductOrder updatedProductOrder) {

        log.info("hello");

        ProductOrder existingProductOrder = getOrderById(id);
        existingProductOrder.setCustomerName(updatedProductOrder.getCustomerName());
        existingProductOrder.setProduct(updatedProductOrder.getProduct());
        existingProductOrder.setQuantity(updatedProductOrder.getQuantity());
        existingProductOrder.setPrice(updatedProductOrder.getPrice());
        return orderRepository.save(existingProductOrder);
    }

    public void deleteOrder(Long id) {
        ProductOrder productOrder = getOrderById(id);
        orderRepository.delete(productOrder);
    }
}
