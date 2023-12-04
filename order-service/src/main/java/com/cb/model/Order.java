package com.cb.model;

import com.cb.client.model.Inventory;

public record Order(int orderId, Inventory inventory) {
}
