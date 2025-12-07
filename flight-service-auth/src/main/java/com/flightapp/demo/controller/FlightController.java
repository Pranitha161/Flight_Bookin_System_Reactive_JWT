package com.flightapp.demo.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.flightapp.demo.entity.Flight;
import com.flightapp.demo.entity.SearchRequest;
import com.flightapp.demo.service.FlightService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/flight/")
public class FlightController {
	private final FlightService flightService;

	@PostMapping("/search")
	public Mono<ResponseEntity<List<Flight>>> searchFlight(@Valid @RequestBody SearchRequest searchRequest) {
		return flightService.search(searchRequest);
	}
	@PostMapping("/add")
	public  Mono<ResponseEntity<String>> addInventory(@Valid @RequestBody Flight flight){
		return flightService.addFlight(flight);
	}
	@PutMapping("flights/{id}")
	public Mono<ResponseEntity<Void>> updateFlight(@PathVariable String id,@Valid @RequestBody Flight flight){
		return flightService.updateFlight(id, flight);
	}

	@GetMapping("/get/{flightId}")
	public Mono<ResponseEntity<Flight>> getFlight(@PathVariable String flightId) {
		return flightService.getFlightById(flightId);
	}

	@GetMapping("/get/flights")
	public Flux<Flight> getAllFlights() {
		return flightService.getFlights();
	}
}