package com.flightapp.demo.entity;

import java.time.LocalDate;

import org.springframework.data.mongodb.core.mapping.Document;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document
public class SearchRequest {
	@NotBlank(message = "Source place is required")
	private String fromPlace;
	@NotBlank(message = "Destination place is required")
	private String toPlace;
	@FutureOrPresent(message = "Travel date must to today or future")
	private LocalDate date;
}
