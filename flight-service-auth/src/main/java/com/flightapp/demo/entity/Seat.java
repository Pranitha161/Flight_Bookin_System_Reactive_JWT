package com.flightapp.demo.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "seats")
public class Seat {
	@Id
	private String id;
	@NotBlank(message = "Seat number cannot be blank")
	private String seatNumber;
	private boolean available;
	@NotBlank(message = "flightId is required")
	private String flightId;
}
