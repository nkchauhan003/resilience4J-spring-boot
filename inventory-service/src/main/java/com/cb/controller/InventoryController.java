package com.cb.controller;

import com.cb.model.Inventory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("inventory")
@Slf4j
public class InventoryController {

    @GetMapping("/{productId}")
    public Inventory getInventory(@PathVariable int productId) {
        log.info("Request received for productId: " + productId);
        if (true)
            throw new RuntimeException();
        /*try {
            Thread.sleep(400);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }*/
        return new Inventory(productId, (int) (Math.random() * (100 - 1)), Math.random() * (500 - 300));

    }
}
