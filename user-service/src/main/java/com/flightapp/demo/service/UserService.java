package com.flightapp.demo.service;

import org.springframework.http.ResponseEntity;

import com.flightapp.demo.entity.LoginRequest;
import com.flightapp.demo.entity.User;

public interface UserService {
	ResponseEntity<User> addUser(User user);

	ResponseEntity<String> login(LoginRequest loginRequest);

//	Claims validate(String token);

	ResponseEntity<User> getPassengerById(String passengerId);

	ResponseEntity<User> getPassengerByEmail(String email);

	ResponseEntity<User> updateById(String id, User passenger);

	ResponseEntity<String> deleteById(String passengerId);

	ResponseEntity<String> logout(String token);

}
