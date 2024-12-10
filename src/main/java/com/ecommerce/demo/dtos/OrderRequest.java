package com.ecommerce.demo.dtos;

import com.ecommerce.demo.entity.ProductOrder;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OrderRequest {

    @NotBlank
    private String customerName;


    private String product;

    @Positive
    private int quantity;

    @Positive
    private BigDecimal price;

    public ProductOrder to(){
        return ProductOrder.builder()
                .customerName(customerName)
                .product(product)
                .quantity(quantity)
                .price(price)
                .build();
    }
}
