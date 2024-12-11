package com.ecommerce.demo.dtos;

import lombok.*;

import java.io.Serial;
import java.io.Serializable;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderProductResponse implements Serializable {

    //to enable caching by hazelcast
    @Serial
    private static final long serialVersionUID = 1L;

    private Long id;
    private Long productId;
    private int quantity;
}
