package com.udacity.pricing.service;

import com.udacity.pricing.domain.price.Price;
import com.udacity.pricing.domain.price.PriceRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

/**
 * Implements the pricing service to get prices for each vehicle.
 */
@Service
public class PricingService {

    private final PriceRepository priceRepository;

    public PricingService(PriceRepository priceRepository) {
        this.priceRepository = priceRepository;
    }

    /**
     * Holds {ID: Price} pairings (current implementation allows for 20 vehicles)
     */
    private final Map<Long, Price> PRICES = LongStream
            .range(1, 20)
            .mapToObj(i -> new Price("USD", randomPrice(), i))
            .collect(Collectors.toMap(Price::getVehicleId, p -> p));

    /**
     * If a valid vehicle ID, gets the price of the vehicle from the stored array.
     * @param vehicleId ID number of the vehicle the price is requested for.
     * @return price of the requested vehicle
     * @throws PriceException vehicleID was not found
     */
    public Price getPrice(Long vehicleId) throws PriceException {

        if (!PRICES.containsKey(vehicleId)) {
            throw new PriceException("Cannot find price for Vehicle " + vehicleId);
        }

        return PRICES.get(vehicleId);
    }

    /**
     * Gets a random price to fill in for a given vehicle ID.
     * @return random price for a vehicle
     */
    private BigDecimal randomPrice() {
        return BigDecimal.valueOf(ThreadLocalRandom.current().nextDouble(1, 5))
                .multiply(new BigDecimal("5000")).setScale(2, RoundingMode.HALF_UP);
    }

    public List<Price> getPrices() {
        return new ArrayList<>(PRICES.values());
    }
}
