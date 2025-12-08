package com.flightapp.demo.filter;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import com.flightapp.demo.util.JwtUtil;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class JwtAuthFilter implements GlobalFilter {

	private final JwtUtil jwtUtil;

	@Override
	public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
		String path = exchange.getRequest().getURI().getPath();
		System.out.println("Im reaching here");
	

		if (path.equals("/auth/login") || 
		    path.equals("/auth/signup") || 
		    path.startsWith("/actuator") )
//		    path.startsWith("/user-service/auth")) 
		{
		    return chain.filter(exchange);
		}

		System.out.println("Im reaching here ttoo");
		String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
		if (authHeader == null || !authHeader.startsWith("Bearer ")) {
			exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
			return exchange.getResponse().setComplete();
		}

		String token = authHeader.substring(7);
		if (!jwtUtil.validateToken(token)) {
			exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
			return exchange.getResponse().setComplete();
		}
		System.out.println("Here too");
		String roles = jwtUtil.extractRoles(token);
		String userId = jwtUtil.extractUserId(token);
		String email = jwtUtil.extractEmail(token);
		

		ServerHttpRequest mutated = exchange.getRequest().mutate().header("X-User-Id", userId).header("X-Roles", roles)
				.header("X-User-Id", userId != null ? userId : "").header("X-Email", email != null ? email : "")
				.build();
		System.out.println(mutated+"  hellllllll");
		return chain.filter(exchange.mutate().request(mutated).build());
	}
}
