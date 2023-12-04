package com.cb.client;

import com.cb.client.model.Inventory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;

@Component
@ConfigurationProperties(prefix = "inventory-ms.client")
@Slf4j
public class InventoryClient extends Client {

    @Autowired
    private WebClient inventoryWebClient;

    public Mono<Inventory> getInventoryByProductId(int id) {
        String url = host + "/" + uri + "/" + (int) (Math.random() * (100 - 10));
        return inventoryWebClient
                .get()
                .uri(url)
                .retrieve()
                .onStatus(
                        HttpStatusCode::isError,
                        clientResponse -> clientResponse.createException().flatMap(Mono::error))
                .bodyToMono(Inventory.class)
                /*.retryWhen(
                        Retry.backoff(3, Duration.ofMillis(100)))*/
                .doOnError(throwable -> {
                    log.error("Error in getting inventory. Exception: {}", throwable.getMessage());
                })
                .doOnSuccess(inventory -> {
                    log.error("Received inventory.");
                });
    }
}
