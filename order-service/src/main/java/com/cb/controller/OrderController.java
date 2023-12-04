package com.cb.controller;

import com.cb.model.Order;
import com.cb.service.InventoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("order")
public class OrderController {

    @Autowired
    private InventoryService inventoryService;

    @GetMapping("/{orderId}")
    public Mono<Order> getOrder(@PathVariable int orderId) {
        return inventoryService.getInventoryByProductId((int) (Math.random() * (100 - 1))).map(
                product -> new Order(orderId, product));
    }
}
