package com.flightapp.demo.feign;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import com.flightapp.demo.entity.Flight;
import com.flightapp.demo.entity.Seat;

import reactor.core.publisher.Mono;

@FeignClient(name = "flight-service")
public interface FlightClient {

	@GetMapping("/api/flight/get/{flightId}")
	Mono<ResponseEntity<Flight>> getFlight(@PathVariable String flightId, @RequestHeader("X-Roles") String roles);

	@PutMapping("/api/flight/flights/{id}")
	Mono<ResponseEntity<Void>> updateFlight(@PathVariable("id") String id, @RequestBody Flight flight,
			@RequestHeader("X-Roles") String roles);

	@GetMapping("/api/seats/flight/{flightId}")
	Mono<ResponseEntity<List<Seat>>> getSeatsByFlightId(@PathVariable String flightId,
			@RequestHeader("X-Roles") String roles);

	@PutMapping("/api/seats/flights/{id}/seats")
	ResponseEntity<Void> updateSeats(@PathVariable String id, @RequestBody List<Seat> seats,
			@RequestHeader("X-Roles") String roles);
}
