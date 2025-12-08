package com.flightapp.demo.service;

import java.util.List;

import com.flightapp.demo.entity.Seat;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface SeatService {
	Mono<Void> initialiszeSeats(String flightId, int rows, int cols);

	Mono<Boolean> updateSeats(String flightId, List<Seat> seats);

	Flux<Seat> getSeatsByFlightId(String flightId);
}
