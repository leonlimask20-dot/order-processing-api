package com.lnl.orderprocessing.adapters.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

// Interface do Spring Data -- apenas o acesso ao banco, sem logica de negocio
interface SpringDataOrderRepository extends JpaRepository<OrderEntity, String> {
    List<OrderEntity> findByCustomerId(String customerId);
}
