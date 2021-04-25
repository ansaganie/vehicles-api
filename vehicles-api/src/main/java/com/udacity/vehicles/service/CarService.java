package com.udacity.vehicles.service;

import com.netflix.appinfo.InstanceInfo;
import com.netflix.discovery.EurekaClient;
import com.udacity.vehicles.client.maps.MapsClient;
import com.udacity.vehicles.client.prices.Price;
import com.udacity.vehicles.client.prices.PriceClient;
import com.udacity.vehicles.domain.Location;
import com.udacity.vehicles.domain.car.Car;
import com.udacity.vehicles.domain.car.CarRepository;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * Implements the car service create, read, update or delete
 * information about vehicles, as well as gather related
 * location and price data when desired.
 */
@Service
public class CarService {

    private final CarRepository repository;

    private final WebClient maps;
    private final PriceClient priceClient;
    private final ModelMapper modelMapper;

    public CarService(CarRepository repository, WebClient maps, WebClient.Builder pricing,
                      ModelMapper modelMapper, EurekaClient eurekaClient) {
        this.repository = repository;
        this.maps = maps;
        this.modelMapper = modelMapper;

        String PRICING_ENDPOINT = "pricing-service";
        InstanceInfo instanceInfo = eurekaClient.getNextServerFromEureka(PRICING_ENDPOINT, false);
        WebClient webClient = pricing
                .baseUrl(instanceInfo.getHomePageUrl())
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
        this.priceClient = new PriceClient(webClient);
    }

    /**
     * Gathers a list of all vehicles
     * @return a list of all vehicles in the CarRepository
     */
    public List<Car> list() {
        return repository.findAll();
    }

    /**
     * Gets car information by ID (or throws exception if non-existent)
     * @param id the ID number of the car to gather information on
     * @return the requested car's information, including location and price
     */
    public Car findById(Long id) {
        Car car = repository.findById(id).orElseThrow(CarNotFoundException::new);

        String price = priceClient.getPrice(id);
        car.setPrice(price);
        MapsClient mapsClient = new MapsClient(maps, modelMapper);

        Location location = mapsClient.getAddress(car.getLocation());

        car.setLocation(location);
        return car;
    }

    /**
     * Either creates or updates a vehicle, based on prior existence of car
     * @param car A car object, which can be either new or existing
     * @return the new/updated car is stored in the repository
     */
    public Car save(Car car) {
        if (car.getId() != null) {
            return repository.findById(car.getId())
                    .map(carToBeUpdated -> {
                        carToBeUpdated.setDetails(car.getDetails());
                        carToBeUpdated.setLocation(car.getLocation());
                        carToBeUpdated.setCondition(car.getCondition());
                        return repository.save(carToBeUpdated);
                    }).orElseThrow(CarNotFoundException::new);
        }
        Car savedCar = repository.save(car);
        Price price = priceClient.postPrice(savedCar.getId());
        savedCar.setPrice(String.format("%s %s", price.getCurrency(), price.getPrice()));
        return savedCar;
    }

    /**
     * Deletes a given car by ID
     * @param id the ID number of the car to delete
     */
    public void delete(Long id) {
        Car car = repository.findById(id).orElseThrow(CarNotFoundException::new);
        priceClient.deletePrice(id);
        repository.delete(car);
    }
}
