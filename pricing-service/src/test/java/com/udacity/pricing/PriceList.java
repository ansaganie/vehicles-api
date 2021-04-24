package com.udacity.pricing;

import com.udacity.pricing.domain.price.Price;

import java.util.List;

public class PriceList {
    private List<Price> prices;
    public List<Price> getPrices() {
        return prices;
    }
    public void setPrices(List<Price> prices) {
        this.prices = prices;
    }
}
