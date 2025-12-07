package com.flightapp.demo.repository;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

import com.flightapp.demo.entity.AirLine;

import reactor.core.publisher.Mono;

public interface AirLineRepository extends ReactiveMongoRepository<AirLine, String> {
	Mono<AirLine> findByName(String name);
}
