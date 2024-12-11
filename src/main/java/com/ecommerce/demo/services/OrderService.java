package com.ecommerce.demo.services;

import com.ecommerce.demo.dtos.OrderProductResponse;
import com.ecommerce.demo.dtos.OrderRequest;
import com.ecommerce.demo.dtos.OrderResponse;
import com.ecommerce.demo.entity.*;
import com.ecommerce.demo.repository.CustomerOrderRepository;
import com.ecommerce.demo.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class OrderService {

    @Autowired
    private CustomerOrderRepository customerOrderRepository;

    @Autowired
    private ProductService productService;

    @Autowired
    private UserService userService;

    @Cacheable(value = "orders-cache")
    public List<OrderResponse> getAllOrders() {
        log.info("fetching from db!");
        List<CustomerOrder> orders = customerOrderRepository.findAll();
        List<OrderResponse> orderResponses = orders.stream().map(this::mapToOrderResponse).toList();
        return orderResponses;
    }

    @Cacheable(value = "orders-cache", key = "#id")
    public OrderResponse getOrderResponseById(Long id) {
        log.info("fetching from db!");
        CustomerOrder order = customerOrderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found with id " + id));
        return mapToOrderResponse(order);
    }

    @Transactional
    public OrderResponse placeOrder(OrderRequest orderRequest) {
        User user = userService.getUserById(orderRequest.getUserId());

        CustomerOrder order = new CustomerOrder();
        order.setUser(user);

        // Create a list of OrderProduct entities
        List<OrderProduct> orderProducts = orderRequest.getProducts().stream().map(productQuantity -> {
            Product product = productService.getProductById(productQuantity.getProductId());
            OrderProduct orderProduct = new OrderProduct();
            orderProduct.setOrder(order);
            orderProduct.setProduct(product);
            orderProduct.setQuantity(productQuantity.getQuantity());
            return orderProduct;
        }).toList();
        order.setOrderProducts(orderProducts);

        //Calculate total order amount
        BigDecimal orderAmount = order.getOrderProducts().stream()
                .map( orderProduct -> orderProduct.getProduct().getPrice()
                        .multiply(BigDecimal.valueOf(orderProduct.getQuantity())))
                .reduce(BigDecimal.ZERO,BigDecimal::add);

        order.setOrderAmount(orderAmount);

        log.info("Order placed for userId: {}",user.getId());

        CustomerOrder placedOrder = customerOrderRepository.save(order);

        return mapToOrderResponse(placedOrder);
    }

    @CachePut(value = "orders-cache", key = "#id")
    public OrderResponse updateOrder(Long id, OrderRequest orderRequest) {
        CustomerOrder existingOrder = getOrderById(id);

        //Cannot update order after payment received successfully
        if(existingOrder.getOrderStatus().equals(OrderStatus.Success)){
            throw new RuntimeException("Order update failed as payment has been already received!");
        }

        User user = userService.getUserById(orderRequest.getUserId());
        existingOrder.setUser(user);

        // Create a list of updated OrderProduct entities
        List<OrderProduct> updatedOrderProducts = orderRequest.getProducts().stream().map(productQuantity -> {
            Product product = productService.getProductById(productQuantity.getProductId());

            // Find if the product already exists in the order
            OrderProduct orderProduct = existingOrder.getOrderProducts().stream()
                    .filter(op -> op.getProduct().getId().equals(product.getId()))
                    .findFirst()
                    .orElse(new OrderProduct()); // Create a new one if not found

            // Update or set details
            orderProduct.setOrder(existingOrder);
            orderProduct.setProduct(product);
            orderProduct.setQuantity(productQuantity.getQuantity());
            return orderProduct;
        }).toList();

        // Update the order's product list
        existingOrder.getOrderProducts().clear(); // Clear products not in updated request
        existingOrder.getOrderProducts().addAll(updatedOrderProducts); // Add updated products

        //Update total order amount
        BigDecimal orderAmount = existingOrder.getOrderProducts().stream()
                .map( orderProduct -> orderProduct.getProduct().getPrice()
                        .multiply(BigDecimal.valueOf(orderProduct.getQuantity())))
                .reduce(BigDecimal.ZERO,BigDecimal::add);

        existingOrder.setOrderAmount(orderAmount);

        log.info("Order: {} updated for userId:{}",id,user.getId());

        CustomerOrder updatedOrder = customerOrderRepository.save(existingOrder);

        return mapToOrderResponse(updatedOrder);
    }

    @CacheEvict(value = "orders-cache", key = "#id")
    public void deleteOrder(Long id) {
        CustomerOrder customerOrder = getOrderById(id);
        customerOrderRepository.delete(customerOrder);
    }

    private OrderResponse mapToOrderResponse(CustomerOrder order){
        List<OrderProductResponse> productResponses = order.getOrderProducts().stream()
                .map(orderProduct ->
                    OrderProductResponse.builder()
                            .id(orderProduct.getId())
                            .productId(orderProduct.getProduct().getId())
                            .quantity(orderProduct.getQuantity())
                            .build()
                )
                .toList();

        return OrderResponse.builder()
                .id(order.getId())
                .userId(order.getUser().getId())
                .orderProducts(productResponses)
                .orderAmount(order.getOrderAmount())
                .orderStatus(order.getOrderStatus())
                .createdOn(order.getCreatedOn())
                .updatedOn(order.getUpdatedOn())
                .build();
    }

    public CustomerOrder getOrderById(Long id) {
        return customerOrderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found with id " + id));
    }

    public void updateOrderStatus(CustomerOrder order,OrderStatus orderStatus){
        order.setOrderStatus(orderStatus);
        customerOrderRepository.save(order);
    }
}
