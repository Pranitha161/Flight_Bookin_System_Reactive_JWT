package com.flightapp.demo.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
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
@RequestMapping("/api/flight")
public class FlightController {
	private final FlightService flightService;
	 @GetMapping("/ping")
	    public String ping() {
	        System.out.println(">>> Flight Service ping hit");
	        return "pong";
	    }
	@PostMapping("/search")
	public Mono<ResponseEntity<List<Flight>>> searchFlight(@Valid @RequestBody SearchRequest searchRequest,
			@RequestHeader("X-Roles") String roles) {
		if (!roles.contains("ROLE_USER")) {
			return Mono.just(ResponseEntity.status(HttpStatus.FORBIDDEN).build());
		}
		return flightService.search(searchRequest);
	}

	@PostMapping("/add")
	public Mono<ResponseEntity<String>> addInventory(@Valid @RequestBody Flight flight,
			@RequestHeader("X-Roles") String roles) {
		if (!roles.contains("ROLE_ADMIN")) {
			return Mono.just(ResponseEntity.status(HttpStatus.FORBIDDEN).build());
		}
		return flightService.addFlight(flight);
	}

	@PutMapping("/flights/{id}")
	public Mono<ResponseEntity<Void>> updateFlight(@PathVariable String id, @Valid @RequestBody Flight flight,
			@RequestHeader("X-Roles") String roles) {
		if (!roles.contains("ROLE_ADMIN")) {
			return Mono.just(ResponseEntity.status(HttpStatus.FORBIDDEN).build());
		}
		return flightService.updateFlight(id, flight);
	}

	@GetMapping("/get/{flightId}")
	public Mono<ResponseEntity<Flight>> getFlight(@PathVariable String flightId,
			@RequestHeader("X-Roles") String roles) {
		if (!roles.contains("ROLE_USER")) {
			return Mono.just(ResponseEntity.status(HttpStatus.FORBIDDEN).build());
		}
		return flightService.getFlightById(flightId);
	}

	@GetMapping("/get/flights")
	public Flux<Flight> getAllFlights(@RequestHeader("X-Roles") String roles) {
		if (!roles.contains("ROLE_USER")) {
			return Flux.error(new RuntimeException("Forbidden"));
		}
		return flightService.getFlights();
	}

	@GetMapping("/debug")
	public String debug(@RequestHeader("X-User-Id") String userId, @RequestHeader("X-Roles") String roles) {
		System.out.println("heelo");
		return "Flight Service User: " + userId + ", Roles: " + roles;
	}

}