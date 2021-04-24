package com.udacity.vehicles.client.prices;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Implements a class to interface with the Pricing Client for price data.
 */
@Component
public class PriceClient {

    private static final Logger log = LoggerFactory.getLogger(PriceClient.class);

    private final WebClient client;

    public PriceClient(WebClient pricing) {
        this.client = pricing;
    }

    // In a real-world application we'll want to add some resilience
    // to this method with retries/CB/failover capabilities
    // We may also want to cache the results so we don't need to
    // do a request every time
    /**
     * Gets a vehicle price from the pricing client, given vehicle ID.
     * @param vehicleId ID number of the vehicle for which to get the price
     * @return Currency and price of the requested vehicle,
     *   error message that the vehicle ID is invalid, or note that the
     *   service is down.
     */
    public String getPrice(Long vehicleId) {
        try {
            Price price = client
                    .get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/prices/" + vehicleId)
                            .build()
                    )
                    .retrieve().bodyToMono(Price.class).block();

            return String.format("%s %s", Objects.requireNonNull(price).getCurrency(), price.getPrice());

        } catch (Exception e) {
            log.error("Unexpected error retrieving price for vehicle {}", vehicleId, e);
        }
        return "(consult price)";
    }

    public Price postPrice(Long vehicleId) {
        Price price = new Price();
        price.setCurrency("USD");
        price.setVehicleId(vehicleId);
        price.setPrice(randomPrice());


        try {
            return client.method(HttpMethod.POST)
                    .uri("/prices")
                    .body(Mono.just(price), Price.class)
                    .retrieve()
                    .bodyToMono(Price.class).block();
        } catch (Exception e) {
            log.error("Unexpected error creating price for vehicle {}", vehicleId, e);
        }
        return  null;
    }

    public void deletePrice(Long vehicleId) {
        try {
            client.method(HttpMethod.DELETE)
                    .uri("/prices/" + vehicleId)
                    .retrieve()
                    .bodyToMono(Price.class).block();

        } catch (Exception e) {
            log.error("Unexpected error creating price for vehicle {}", vehicleId, e);
        }
    }

    /**
     * Gets a random price to fill in for a given vehicle ID.
     * @return random price for a vehicle
     */
    private static BigDecimal randomPrice() {
        return BigDecimal.valueOf(ThreadLocalRandom.current().nextDouble(1, 5))
                .multiply(new BigDecimal("5000")).setScale(2, RoundingMode.HALF_UP);
    }
}
