package com.flightapp.demo.service.implementation;

import org.springframework.stereotype.Service;
import com.flightapp.demo.entity.Seat;
import com.flightapp.demo.repository.SeatRepository;
import com.flightapp.demo.service.SeatService;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SeatServiceImplementation implements SeatService {
	private final SeatRepository seatRepo;

	public Flux<Seat> getSeatsByFlightId(String flightId) {
		return seatRepo.findByFlightId(flightId);
	}

	public Mono<Void> initialiszeSeats(String flightId, int rows, int cols) {
		char[] seatLetters = { 'A', 'B', 'C', 'D', 'E', 'F' };
		List<Seat> seats = new ArrayList<>();
		for (int row = 1; row <= rows; row++) {
			for (int col = 0; col < cols; col++) {
				Seat seat = new Seat();
				seat.setSeatNumber(row + "" + seatLetters[col]);
				seat.setAvailable(true);
				seat.setFlightId(flightId);
				seats.add(seat);
			}
		}
		return seatRepo.saveAll(seats).then();
	}

	public Mono<Boolean> updateSeats(String flightId, List<Seat> seats) {
		return seatRepo.findByFlightId(flightId).hasElements() 
				.flatMap(exists -> {
					if (!exists) {
						return Mono.just(false);
					}
					seats.forEach(seat -> seat.setFlightId(flightId));
					return seatRepo.saveAll(seats).then(Mono.just(true));
				});
	}
}
