package com.project.edugov.config;

import static org.springframework.security.config.Customizer.withDefaults;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http
				// 1. Disable CSRF (This is why POST requests usually fail with 401/403)
				.csrf(csrf -> csrf.disable())

				// 2. Allow all requests without a password for now
				.authorizeHttpRequests(auth -> auth.anyRequest().permitAll()).httpBasic(withDefaults());

		return http.build();
	}
}