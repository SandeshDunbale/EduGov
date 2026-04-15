package com.project.edugov.config;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity // This tells Spring "Stop using the default generated password!"
public class AppConfig {

//    @Bean
//    public ModelMapper modelMapper1() {
//        return new ModelMapper();
//    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder1() {
        return new BCryptPasswordEncoder();
    }

//    @Bean
//    public SecurityFilterChain securityFilterChain1(HttpSecurity http) throws Exception {
//        http
//            .csrf(AbstractHttpConfigurer::disable)
//            .authorizeHttpRequests(auth -> auth
//                .requestMatchers("/faculty/**").permitAll()
//                .requestMatchers("/students/**").permitAll()
//                .anyRequest().permitAll()
//            );
//        return http.build();
//    }
}