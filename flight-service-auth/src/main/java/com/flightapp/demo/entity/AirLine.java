package com.flightapp.demo.entity;

import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "airlines")
public class AirLine {
	@Id
	private String id;
	@NotBlank(message = "Airline name cannot be blank")
	private String name;
	@NotBlank(message = "logoUrl cannot be blank")
	@Pattern(regexp = "^(http|https)://.*$", message = "Logo URl must be a valid URL starting with http or https")
	private String logoUrl;
	private List<String> flightIds;
}
