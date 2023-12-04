package com.cb.conf;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.resilience4j.timelimiter.TimeLimiterConfig;
import io.github.resilience4j.timelimiter.TimeLimiterRegistry;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.circuitbreaker.resilience4j.ReactiveResilience4JCircuitBreakerFactory;
import org.springframework.cloud.client.circuitbreaker.ReactiveCircuitBreaker;
import org.springframework.cloud.client.circuitbreaker.ReactiveCircuitBreakerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.time.Duration;

@Configuration
@ConfigurationProperties("inventory-ms.circuit-breaker")
@Slf4j
@Getter @Setter
public class CircuitBreakerConfiguration {

    private String name;
    private Float failureRateThreshold;
    private Float slowCallRateThreshold;
    private Integer slowCallDurationThresholdMilis;
    private Integer waitDurationInOpenStateMilis;
    private Integer permittedNumberOfCallsInHalfOpenState;
    private Integer slidingWindowSize;
    private Integer timeoutDurationMilis;
    private Integer minimumNumberOfCalls;

    @Bean("inventoryCircuitBreaker")
    ReactiveCircuitBreaker inventoryCircuitBreaker(
            ReactiveCircuitBreakerFactory reactiveCircuitBreakerFactory,
            CircuitBreakerRegistry circuitBreakerRegistry) {
        return createCircuitBreaker(name, reactiveCircuitBreakerFactory, circuitBreakerRegistry);

    }

    private ReactiveCircuitBreaker createCircuitBreaker(
            String name, ReactiveCircuitBreakerFactory reactiveCircuitBreakerFactory,
            CircuitBreakerRegistry circuitBreakerRegistry) {
        ReactiveCircuitBreaker reactiveCircuitBreaker = reactiveCircuitBreakerFactory.create(name);
        addEventToCircuitBreaker(name, circuitBreakerRegistry);
        return reactiveCircuitBreaker;
    }

    private void addEventToCircuitBreaker(String name, CircuitBreakerRegistry circuitBreakerRegistry) {
        CircuitBreaker circuitBreaker = circuitBreakerRegistry.circuitBreaker(name);
        circuitBreaker.getEventPublisher().onEvent(e -> log.info(e.toString()));
    }

    @Bean
    @Primary
    ReactiveCircuitBreakerFactory reactiveCircuitBreakerFactory(
            CircuitBreakerRegistry circuitBreakerRegistry, TimeLimiterRegistry timeLimiterRegistry) {
        return new ReactiveResilience4JCircuitBreakerFactory(circuitBreakerRegistry, timeLimiterRegistry);
    }

    @Bean
    CircuitBreakerRegistry circuitBreakerRegistry(CircuitBreakerConfig circuitBreakerConfig) {
        return CircuitBreakerRegistry.of(circuitBreakerConfig);
    }

    @Bean
    CircuitBreakerConfig circuitBreakerConfig() {
        return CircuitBreakerConfig.custom()
                .minimumNumberOfCalls(minimumNumberOfCalls)
                .failureRateThreshold(failureRateThreshold)
                .slowCallRateThreshold(slowCallRateThreshold)
                .slowCallDurationThreshold(Duration.ofMillis(slowCallDurationThresholdMilis))
                .waitDurationInOpenState(Duration.ofMillis(waitDurationInOpenStateMilis))
                .permittedNumberOfCallsInHalfOpenState(permittedNumberOfCallsInHalfOpenState)
                .slidingWindowSize(slidingWindowSize)
                .build();
    }

    @Bean
    @Primary
    TimeLimiterRegistry timeLimiterRegistry(TimeLimiterConfig timeLimiterConfig) {
        return TimeLimiterRegistry.of(timeLimiterConfig);
    }

    @Bean
    TimeLimiterConfig timeLimiterConfig() {
        return TimeLimiterConfig.custom()
                .cancelRunningFuture(true)
                .timeoutDuration(Duration.ofMillis(timeoutDurationMilis))
                .build();
    }

}
