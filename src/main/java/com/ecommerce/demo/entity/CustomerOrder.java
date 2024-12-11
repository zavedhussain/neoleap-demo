package com.ecommerce.demo.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CustomerOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<OrderProduct> orderProducts;

    private BigDecimal orderAmount;

    @OneToMany(mappedBy = "order", fetch = FetchType.EAGER)
    @JsonIgnore
    private List<Payment> payments;

    @Enumerated(EnumType.STRING)
    private OrderStatus orderStatus = OrderStatus.Pending;

    @CreationTimestamp
    private LocalDateTime createdOn;

    @UpdateTimestamp
    private  LocalDateTime updatedOn;


}
