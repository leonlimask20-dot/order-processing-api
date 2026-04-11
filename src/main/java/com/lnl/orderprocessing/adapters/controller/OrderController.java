package com.lnl.orderprocessing.adapters.controller;

import com.lnl.orderprocessing.adapters.presenter.OrderResponse;
import com.lnl.orderprocessing.adapters.presenter.OrderResponseMapper;
import com.lnl.orderprocessing.application.usecase.CancelOrderUseCase;
import com.lnl.orderprocessing.application.usecase.PlaceOrderUseCase;
import com.lnl.orderprocessing.application.usecase.TrackOrderUseCase;
import com.lnl.orderprocessing.domain.entity.Order;
import com.lnl.orderprocessing.domain.entity.OrderItem;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1/orders")
public class OrderController {

    private final PlaceOrderUseCase    placeOrder;
    private final CancelOrderUseCase   cancelOrder;
    private final TrackOrderUseCase    trackOrder;
    private final OrderResponseMapper  mapper;

    public OrderController(PlaceOrderUseCase placeOrder,
                           CancelOrderUseCase cancelOrder,
                           TrackOrderUseCase trackOrder,
                           OrderResponseMapper mapper) {
        this.placeOrder  = placeOrder;
        this.cancelOrder = cancelOrder;
        this.trackOrder  = trackOrder;
        this.mapper      = mapper;
    }

    @PostMapping
    public ResponseEntity<OrderResponse> place(@Valid @RequestBody PlaceOrderRequest request) {
        List<OrderItem> items = request.items().stream()
            .map(i -> new OrderItem(i.productId(), i.productName(), i.quantity(), i.unitPrice()))
            .toList();

        Order order = placeOrder.execute(request.customerId(), items);
        return ResponseEntity.status(HttpStatus.CREATED).body(mapper.toResponse(order));
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderResponse> track(@PathVariable String id) {
        return ResponseEntity.ok(mapper.toResponse(trackOrder.execute(id)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<OrderResponse> cancel(@PathVariable String id) {
        return ResponseEntity.ok(mapper.toResponse(cancelOrder.execute(id)));
    }
}
