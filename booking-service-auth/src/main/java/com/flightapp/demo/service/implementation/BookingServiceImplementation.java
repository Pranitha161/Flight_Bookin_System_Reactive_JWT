package com.flightapp.demo.service.implementation;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.flightapp.demo.entity.Booking;
import com.flightapp.demo.entity.Flight;
import com.flightapp.demo.entity.Seat;
import com.flightapp.demo.entity.User;
import com.flightapp.demo.event.BookingEventProducer;
import com.flightapp.demo.repository.BookingRepository;
import com.flightapp.demo.service.BookingService;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import reactor.core.publisher.Mono;

@Service

public class BookingServiceImplementation implements BookingService {

	private final BookingRepository bookingRepo;
	private final BookingEventProducer eventProducer;
	private final WebClient webClient;

	public BookingServiceImplementation(BookingRepository bookingRepo, BookingEventProducer eventProducer,
			WebClient.Builder builder) {
		this.bookingRepo = bookingRepo;
		this.eventProducer = eventProducer;
		this.webClient = builder.baseUrl("http://localhost:8765").build();
	}

	@Override
	public Mono<ResponseEntity<Booking>> getTicketsByPnr(String pnr) {
		return bookingRepo.findByPnr(pnr).map(ResponseEntity::ok)
				.switchIfEmpty(Mono.just(ResponseEntity.notFound().build()));
	}

	@Override
	public Mono<ResponseEntity<Booking>> getBookingsByEmail(String email) {
		return bookingRepo.findByEmail(email).map(ResponseEntity::ok)
				.switchIfEmpty(Mono.just(ResponseEntity.notFound().build()));
	}

	@CircuitBreaker(name = "flightServiceCircuitBreaker", fallbackMethod = "fallbackDeleteBooking")
	public Mono<ResponseEntity<String>> deleteBookingByPnr(String pnr, String roles) {
		return bookingRepo.findByPnr(pnr)
				.flatMap(booking -> webClient.get().uri("http://FLIGHT-SERVICE-AUTH/flight/{id}", booking.getFlightId())
						.header("X-Roles", roles).retrieve().bodyToMono(Flight.class).flatMap(flight -> {
							if (flight == null) {
								return Mono.just(ResponseEntity.status(HttpStatus.NOT_FOUND)
										.body("Flight not found for booking PNR: " + pnr));
							}

							ZoneId systemZone = ZoneId.systemDefault();
							ZonedDateTime now = ZonedDateTime.now(systemZone);
							ZonedDateTime departure = flight.getDepartureTime().atZone(systemZone);

							if (departure.isBefore(now.plusHours(24))) {
								return Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST)
										.body("Cannot delete booking within 24 hours of departure for PNR: " + pnr));
							}

							return webClient.get()
									.uri("http://FLIGHT-SERVICE-AUTH/flight/{id}/seats", booking.getFlightId())
									.header("X-Roles", roles).retrieve().bodyToFlux(Seat.class).collectList()
									.flatMap(seats -> {
										if (seats.isEmpty()) {
											return Mono.just(ResponseEntity.status(HttpStatus.NOT_FOUND)
													.body("Seats not found for flight: " + booking.getFlightId()));
										}

										List<String> seatNumbers = booking.getSeatNumbers();
										seats.stream().filter(s -> seatNumbers.contains(s.getSeatNumber()))
												.forEach(s -> s.setAvailable(true));

										flight.setAvailableSeats(flight.getAvailableSeats() + seatNumbers.size());

										return bookingRepo.delete(booking)
												.then(Mono.fromRunnable(() -> eventProducer.bookingDeleted(booking)))
												.thenReturn(ResponseEntity.ok("Booking with PNR " + pnr
														+ " deleted successfully. Seats released."));
									});
						}))
				.switchIfEmpty(Mono.just(ResponseEntity.status(HttpStatus.NOT_FOUND).body("Booking not found")));
	}

	public Mono<ResponseEntity<String>> fallbackDeleteBooking(String pnr, Throwable t) {
		return Mono.just(ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
				.body("Flight service unavailable while deleting booking with PNR: " + pnr));
	}

	@CircuitBreaker(name = "flightServiceCircuitBreaker", fallbackMethod = "fallbackGetFlight")
	public Mono<ResponseEntity<String>> bookTicket(String flightId, Booking booking, String roles) {
		final List<String> seatReq = booking.getSeatNumbers();
		if (seatReq == null || seatReq.isEmpty()) {
			return Mono.just(ResponseEntity.badRequest().body("No seats requested"));
		}

		return webClient.get().uri("http://USER-SERVICE-AUTH/passenger/{email}", booking.getEmail()).retrieve()
				.bodyToMono(User.class).switchIfEmpty(Mono.error(new RuntimeException("Invalid email")))
				.flatMap(user -> webClient.post().uri("http://USER-SERVICE-AUTH/users/byIds")
						.bodyValue(booking.getUserIds()).retrieve().bodyToFlux(User.class).collectList()
						.flatMap(users -> {
							if (users.size() != booking.getUserIds().size()) {
								return Mono.just(ResponseEntity.status(HttpStatus.NOT_FOUND)
										.body("One or more passenger IDs are invalid"));
							}

							return webClient.get().uri("http://FLIGHT-SERVICE-AUTH/flight/{id}", flightId)
									.header("X-Roles", roles).retrieve().bodyToMono(Flight.class).flatMap(flight -> {
										if (flight == null) {
											return Mono.just(ResponseEntity.status(HttpStatus.NOT_FOUND)
													.body("Flight not found with id: " + flightId));
										}

		return webClient.get()
			.uri("http://FLIGHT-SERVICE-AUTH/flight/{id}/seats", flightId)
				.header("X-Roles", roles).retrieve().bodyToFlux(Seat.class)
				.collectList().flatMap(seats -> {
				if (seats.isEmpty()) {
					return Mono.just(ResponseEntity.status(HttpStatus.NOT_FOUND).body("Seats not found for flight: " + flightId));}
				for (String req : seatReq) {
					Seat seat = seats.stream()
					.filter(s -> req.equals(s.getSeatNumber())).findFirst().orElse(null);
					if (seat == null) {
					return Mono.just(ResponseEntity.badRequest()
					.body("Seat " + req + " does not exist"));
														}
			if (!seat.isAvailable()) {
					return Mono.just(ResponseEntity.status(HttpStatus.CONFLICT)
							.body("Seat " + req + " is already booked"));
														}
													}

		booking.setPnr(flight.getId() + "-" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyMMddHHmmss")));
		booking.setFlightId(flightId);
		booking.setTotalAmount(flight.getPrice().getOneWay());
		seats.stream().filter(s -> seatReq.contains(s.getSeatNumber())).forEach(s -> s.setAvailable(false));
		int newAvailable = flight.getAvailableSeats() - seatReq.size();
		if (newAvailable < 0) {
			return Mono.just(ResponseEntity.status(HttpStatus.CONFLICT).body("Insufficient seats available"));}
		flight.setAvailableSeats(newAvailable);
		return bookingRepo.save(booking).then(Mono.fromRunnable(() -> eventProducer.bookingCreated(booking)))
		.thenReturn(ResponseEntity.status(HttpStatus.CREATED)
				.body("Booking created successfully with PNR: "+ booking.getPnr()));
												});
									});
						}));
	}

	public Mono<ResponseEntity<String>> fallbackGetFlight(String flightId, Booking booking, Throwable t) {
		return Mono.just(ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
				.body("Flight service unavailable while booking flightId: " + flightId));
	}
}
