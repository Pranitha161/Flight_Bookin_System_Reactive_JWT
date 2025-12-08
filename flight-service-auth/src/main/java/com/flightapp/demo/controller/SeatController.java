package com.flightapp.demo.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.flightapp.demo.entity.Seat;
import com.flightapp.demo.service.SeatService;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/seats")
@RequiredArgsConstructor
public class SeatController {

	private final SeatService seatService;

	@GetMapping("/flight/{flightId}")
	public Mono<ResponseEntity<Flux<Seat>>> getSeatsByFlightId(@PathVariable String flightId) {
		Flux<Seat> seatsFlux = seatService.getSeatsByFlightId(flightId);
		return seatsFlux.hasElements().flatMap(hasSeats -> {
			if (hasSeats) {
				return Mono.just(ResponseEntity.ok(seatsFlux));
			} else {
				return Mono.just(ResponseEntity.notFound().build());
			}
		});
	}

	@PutMapping("/flights/{id}/seats")
	public Mono<ResponseEntity<Void>> updateSeats(@PathVariable String id, @RequestBody List<Seat> seats) {
	    return seatService.updateSeats(id, seats)
	        .map(updated -> updated 
	            ? ResponseEntity.ok().<Void>build() 
	            : ResponseEntity.notFound().build()
	        );
	}

}
