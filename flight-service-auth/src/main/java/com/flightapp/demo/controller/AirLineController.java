package com.flightapp.demo.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.flightapp.demo.entity.AirLine;
import com.flightapp.demo.service.AirLineService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/flight/airline")
@RequiredArgsConstructor
public class AirLineController {

	private final AirLineService airlineService;

	@GetMapping("/get")
	public Flux<AirLine> getAirlines() {
		return airlineService.getAllAirlines();
	}

	@GetMapping("/get/{id}")
	public Mono<ResponseEntity<AirLine>> getAirlineById(@Valid @PathVariable String id) {
		return airlineService.getById(id);
	}

}
