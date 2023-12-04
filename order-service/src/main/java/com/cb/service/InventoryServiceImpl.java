package com.cb.service;

import com.cb.client.InventoryClient;
import com.cb.client.model.Inventory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.circuitbreaker.ReactiveCircuitBreaker;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class InventoryServiceImpl implements InventoryService {

    @Autowired
    private ReactiveCircuitBreaker inventoryCircuitBreaker;

    @Autowired
    private InventoryClient inventoryClient;

    @Override
    public Mono<Inventory> getInventoryByProductId(int id) {
        return inventoryCircuitBreaker.run(inventoryClient.getInventoryByProductId(id), throwable ->
                getFallbackInventoryByProductId(id)
        );
    }

    private Mono<Inventory> getFallbackInventoryByProductId(int id) {
        return Mono.just(new Inventory(id, -1, -1.0));
    }
}
