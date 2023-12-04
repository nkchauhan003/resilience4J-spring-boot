package com.cb.conf;

import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.HttpProtocol;
import reactor.netty.http.client.HttpClient;
import reactor.netty.resources.ConnectionProvider;
import reactor.util.retry.Retry;

import java.time.Duration;

@Configuration
@Setter
@ConfigurationProperties(prefix = "client")
public class ClientConfiguration {

    private Integer maxConnections;
    private Integer maxIdleTimeSeconds;
    private Integer maxLifeTimeSeconds;
    private Long responseTimeoutMilis;
    private Integer retries;
    private Integer retryDelayMilis;

    @Bean
    HttpClient httpClient() {
        ConnectionProvider connectionProvider = ConnectionProvider.builder("client").maxConnections(
                maxConnections).maxIdleTime(Duration.ofSeconds(maxIdleTimeSeconds)).maxLifeTime(
                Duration.ofSeconds(maxLifeTimeSeconds)).build();
        return HttpClient.create(connectionProvider).protocol(HttpProtocol.HTTP11);
    }

    private WebClient getWebClient(HttpClient httpClient, Long responseTimeout) {
        return WebClient.builder()
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .filter(responseTimeout(responseTimeout))
                //.filter(retry(retries, retryDelayMilis))
                .build();
    }

    @Bean("inventoryWebClient")
    WebClient inventoryWebClient(HttpClient httpClient) {
        return getWebClient(httpClient, responseTimeoutMilis);
    }

    // Timeout
    private static ExchangeFilterFunction responseTimeout(Long responseTimeout) {
        return (request, next) -> next.exchange(request).timeout(Duration.ofMillis(responseTimeout));
    }

    // Retry
    private static ExchangeFilterFunction retry(Integer retries, Integer retryDelayMilis) {
        /*return (request, next) -> next.exchange(request).retryWhen(
                Retry.backoff(retries, Duration.ofMillis(retryDelayMilis)));*/
        return  (request, next) -> next.exchange(request).retry(3);
    }
}
