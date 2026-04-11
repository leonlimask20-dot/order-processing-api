package com.lnl.orderprocessing.infrastructure.config;

import com.lnl.orderprocessing.application.usecase.*;
import com.lnl.orderprocessing.domain.repository.OrderRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Aqui o Spring conhece os use cases -- os use cases NAO conhecem o Spring.
 * Dependency Inversion aplicado: Spring injeta, dominio nao depende de framework.
 */
@Configuration
public class BeanConfig {

    @Bean
    public PlaceOrderUseCase placeOrderUseCase(OrderRepository repo) {
        return new PlaceOrderUseCase(repo);
    }

    @Bean
    public CancelOrderUseCase cancelOrderUseCase(OrderRepository repo) {
        return new CancelOrderUseCase(repo);
    }

    @Bean
    public TrackOrderUseCase trackOrderUseCase(OrderRepository repo) {
        return new TrackOrderUseCase(repo);
    }
}
