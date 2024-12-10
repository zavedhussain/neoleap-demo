package com.ecommerce.demo.repository;

import com.ecommerce.demo.entity.ProductOrder;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<ProductOrder,Long> {
}
