package com.cb.service;

import com.cb.client.model.Inventory;
import reactor.core.publisher.Mono;

public interface InventoryService {
    public Mono<Inventory> getInventoryByProductId(int id);
}
