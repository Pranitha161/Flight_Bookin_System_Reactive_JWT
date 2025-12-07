package com.flightapp.demo.service;

import java.util.List;

import com.flightapp.demo.entity.Seat;

import reactor.core.publisher.Flux;

public interface SeatService {
	List<Seat> initialiszeSeats(String flightId, int rows, int cols);

	boolean updateSeats(String flightId, List<Seat> seats);

	Flux<Seat> getSeatsByFlightId(String flightId);
}
