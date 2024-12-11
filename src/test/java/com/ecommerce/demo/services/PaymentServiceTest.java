package com.ecommerce.demo.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.ecommerce.demo.dtos.PaymentRequest;
import com.ecommerce.demo.entity.*;
import com.ecommerce.demo.repository.PaymentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import java.math.BigDecimal;
import java.util.Optional;

public class PaymentServiceTest {

    @InjectMocks
    private PaymentService paymentService;

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private OrderService orderService;

    @Mock
    private UserService userService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testMakePayment_Success() {
        // Arrange
        PaymentRequest paymentRequest = new PaymentRequest(1L, 2L, new BigDecimal("100.00"));
        CustomerOrder customerOrder = new CustomerOrder();
        customerOrder.setId(1L);
        customerOrder.setOrderAmount(new BigDecimal("100.00"));
        customerOrder.setOrderStatus(OrderStatus.Pending);

        User user = new User();
        user.setId(2L);

        Payment expectedPayment = Payment.builder()
                .order(customerOrder)
                .user(user)
                .receivedAmount(paymentRequest.getAmount())
                .paymentStatus(PaymentStatus.Success)
                .build();

        when(orderService.getOrderById(1L)).thenReturn(customerOrder);
        when(userService.getUserById(2L)).thenReturn(user);
        when(paymentRepository.save(any(Payment.class))).thenReturn(expectedPayment);

        // Act
        Payment actualPayment = paymentService.makePayment(paymentRequest);

        // Assert
        assertEquals(PaymentStatus.Success, actualPayment.getPaymentStatus());
        assertEquals("100.00", actualPayment.getReceivedAmount().toPlainString());
        verify(orderService).updateOrderStatus(customerOrder, OrderStatus.Success);
    }

    @Test
    public void testMakePayment_InsufficientAmount() {
        // Arrange
        PaymentRequest paymentRequest = new PaymentRequest(1L, 2L, new BigDecimal("50.00"));
        CustomerOrder customerOrder = new CustomerOrder();
        customerOrder.setId(1L);
        customerOrder.setOrderAmount(new BigDecimal("100.00"));
        customerOrder.setOrderStatus(OrderStatus.Pending);

        User user = new User();
        user.setId(2L);

        Payment expectedPayment = Payment.builder()
                .order(customerOrder)
                .user(user)
                .receivedAmount(paymentRequest.getAmount())
                .paymentStatus(PaymentStatus.Failed)
                .errorMessage("Insufficient amount!")
                .build();

        when(orderService.getOrderById(1L)).thenReturn(customerOrder);
        when(userService.getUserById(2L)).thenReturn(user);
        when(paymentRepository.save(any(Payment.class))).thenReturn(expectedPayment);  // Ensure save is mocked

        // Act
        Payment actualPayment = paymentService.makePayment(paymentRequest);

        // Assert
        assertEquals(PaymentStatus.Failed, actualPayment.getPaymentStatus());
        assertEquals("Insufficient amount!", actualPayment.getErrorMessage());
        verify(orderService).updateOrderStatus(customerOrder, OrderStatus.Failed);
    }

    @Test
    public void testMakePayment_ExceedingAmount() {
        // Arrange
        PaymentRequest paymentRequest = new PaymentRequest(1L, 2L, new BigDecimal("150.00"));
        CustomerOrder customerOrder = new CustomerOrder();
        customerOrder.setId(1L);
        customerOrder.setOrderAmount(new BigDecimal("100.00"));
        customerOrder.setOrderStatus(OrderStatus.Pending);

        User user = new User();
        user.setId(2L);

        Payment expectedPayment = Payment.builder()
                .order(customerOrder)
                .user(user)
                .receivedAmount(paymentRequest.getAmount())
                .paymentStatus(PaymentStatus.Failed)
                .errorMessage("Payment amount exceeding order amount!")
                .build();

        when(orderService.getOrderById(1L)).thenReturn(customerOrder);
        when(userService.getUserById(2L)).thenReturn(user);
        when(paymentRepository.save(any(Payment.class))).thenReturn(expectedPayment);  // Ensure save is mocked

        // Act
        Payment actualPayment = paymentService.makePayment(paymentRequest);

        // Assert
        assertEquals(PaymentStatus.Failed, actualPayment.getPaymentStatus());
        assertEquals("Payment amount exceeding order amount!", actualPayment.getErrorMessage());
        verify(orderService).updateOrderStatus(customerOrder, OrderStatus.Failed);
    }


    @Test
    public void testMakePayment_OrderAlreadyPaid() {
        // Arrange
        PaymentRequest paymentRequest = new PaymentRequest(1L, 2L, new BigDecimal("100.00"));
        CustomerOrder customerOrder = new CustomerOrder();
        customerOrder.setId(1L);
        customerOrder.setOrderAmount(new BigDecimal("100.00"));
        customerOrder.setOrderStatus(OrderStatus.Success);  // Already paid order

        when(orderService.getOrderById(1L)).thenReturn(customerOrder);

        // Act & Assert
        RuntimeException thrown = assertThrows(RuntimeException.class, () -> {
            paymentService.makePayment(paymentRequest);
        });
        assertEquals("Order payment has been already received!", thrown.getMessage());
    }
}
