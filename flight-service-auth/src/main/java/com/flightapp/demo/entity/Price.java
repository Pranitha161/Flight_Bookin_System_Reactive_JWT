package com.flightapp.demo.entity;

import org.springframework.data.mongodb.core.mapping.Document;

import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document
public class Price {
	@Positive(message = "oneWay trip price cannot be negative")
	private float oneWay;
	@Positive(message = "roundTrip trip price cannot be negative")
	private float roundTrip;
}
