package com.ecommerce.demo.dtos;

import com.ecommerce.demo.entity.OrderProduct;
import com.ecommerce.demo.entity.OrderStatus;
import com.ecommerce.demo.entity.User;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderResponse implements Serializable {

    //to enable caching by hazelcast
    @Serial
    private static final long serialVersionUID = 1L;

    private Long id;

    private Long userId;

    private List<OrderProductResponse> orderProducts;

    private BigDecimal orderAmount;

    private OrderStatus orderStatus;

    private LocalDateTime createdOn;

    private  LocalDateTime updatedOn;
}
