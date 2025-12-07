package com.flightapp.demo.repository;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

import com.flightapp.demo.entity.Flight;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface FlightRepository extends ReactiveMongoRepository<Flight, String> {
	Flux<Flight> getFightByFromPlaceAndToPlace(String fromPlace, String toPlace);

	Flux<Flight> getByAirlineId(String airlineId);

	Mono<Flight> findByFromPlaceAndToPlace(String fromPlace, String toPlace);
}
