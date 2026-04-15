package com.project.edugov.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthFilter) {
        this.jwtAuthFilter = jwtAuthFilter;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // Required to expose the AuthenticationManager (standard practice)
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable()) 
            .authorizeHttpRequests(auth -> auth
                
                // 1. PUBLIC ENDPOINTS (Access to everyone)
                .requestMatchers(
                    "/api/users/login",
                    "/api/users/recoverEmail",
                    "/api/users/resetPassword",
                    "/swagger-ui/**", 
                    "/v3/api-docs/**",
                    "/students/register",  
                    "/faculty/register"    
                ).permitAll()
                
                // 2. LOGGED IN USERS: Anyone with a valid token can log out
                .requestMatchers("/api/users/logout").authenticated()

                // 3. STUDENT & FACULTY OWN PROFILE UPDATES
                .requestMatchers(HttpMethod.PUT, "/students/*/update").hasAnyRole("STUDENT","GOVT_AUDITOR")
                .requestMatchers(HttpMethod.PUT, "/faculty/*/update").hasRole("FACULTY")

                // ==========================================
                // NEW: FACULTY PROJECT & GRANT ACTIONS
                // ==========================================
                // Faculty creating and updating projects
                .requestMatchers(HttpMethod.POST, "/api/projects/*").hasRole("FACULTY")
                .requestMatchers(HttpMethod.PUT, "/api/projects/*").hasRole("FACULTY")
                
                // Faculty applying for grants
                .requestMatchers(HttpMethod.POST, "/api/grants/apply/*").hasRole("FACULTY")
                
                // Viewing projects and grants (Faculty can view their own, Admins can view all)
                .requestMatchers(HttpMethod.GET, "/api/projects/faculty/*").hasAnyRole("FACULTY", "PROG_MANAGER", "UNIV_ADMIN", "GOVT_AUDITOR")
                .requestMatchers(HttpMethod.GET, "/api/projects/*").hasAnyRole("FACULTY", "PROG_MANAGER", "UNIV_ADMIN", "GOVT_AUDITOR")
                .requestMatchers(HttpMethod.GET, "/api/grants/project/*").hasAnyRole("FACULTY", "PROG_MANAGER", "UNIV_ADMIN", "GOVT_AUDITOR")

                // ==========================================
                // NEW: PROGRAM MANAGER & ADMIN GRANT ACTIONS
                // ==========================================
                // Viewing pending grants and faculty history
                .requestMatchers(HttpMethod.GET, "/api/grants/pending").hasAnyRole("PROG_MANAGER", "UNIV_ADMIN", "GOVT_AUDITOR")
                .requestMatchers(HttpMethod.GET, "/api/grants/history/*").hasAnyRole("PROG_MANAGER", "UNIV_ADMIN", "GOVT_AUDITOR")
                
                // Making a decision (Approve/Reject) on a grant
                .requestMatchers(HttpMethod.POST, "/api/grants/decision/*").hasAnyRole("PROG_MANAGER", "UNIV_ADMIN", "GOVT_AUDITOR")

                // ==========================================
                // EXISTING: ADMIN USER/STUDENT/FACULTY MANAGEMENT
                // ==========================================
                .requestMatchers(HttpMethod.GET, "/students").hasAnyRole("UNIV_ADMIN", "PROG_MANAGER", "GOVT_AUDITOR")
                .requestMatchers(HttpMethod.PATCH, "/students/*/approve", "/students/*/decline").hasAnyRole("UNIV_ADMIN", "PROG_MANAGER", "GOVT_AUDITOR")
                .requestMatchers(HttpMethod.DELETE, "/students/*/delete").hasAnyRole("UNIV_ADMIN", "PROG_MANAGER", "GOVT_AUDITOR")
                
                
                .requestMatchers(HttpMethod.GET, "/faculty").hasAnyRole("UNIV_ADMIN", "PROG_MANAGER", "GOVT_AUDITOR")
                .requestMatchers(HttpMethod.PATCH, "/faculty/*/approve", "/faculty/*/decline").hasAnyRole("UNIV_ADMIN", "PROG_MANAGER", "GOVT_AUDITOR")
                .requestMatchers(HttpMethod.DELETE, "/faculty/*/delete").hasAnyRole("UNIV_ADMIN", "PROG_MANAGER", "GOVT_AUDITOR")

                .requestMatchers(HttpMethod.GET, "/api/users/**").hasAnyRole("GOVT_AUDITOR","UNIV_ADMIN")
                .requestMatchers(HttpMethod.PATCH, "/api/users/**").hasAnyRole("GOVT_AUDITOR","UNIV_ADMIN")
                .requestMatchers(HttpMethod.POST, "/programs/save").hasRole("UNIV_ADMIN")

                // ==========================================
                // INTEGRATED: AUDIT CONTROLLER API
                // ==========================================
                .requestMatchers(HttpMethod.POST, "/api/audits").hasRole("COMPLIANCE_OFFICER")
                .requestMatchers(HttpMethod.GET, "/api/audits", "/api/audits/*").hasAnyRole("COMPLIANCE_OFFICER", "GOVT_AUDITOR")
                .requestMatchers(HttpMethod.PUT, "/api/audits/update/*").hasRole("COMPLIANCE_OFFICER")
                .requestMatchers(HttpMethod.DELETE, "/api/audits/delete/*").hasRole("COMPLIANCE_OFFICER")
                .requestMatchers(HttpMethod.PATCH, "/api/audits/*/review").hasRole("GOVT_AUDITOR")

                // ==========================================
                // INTEGRATED: COMPLIANCE CONTROLLER API
                // ==========================================
                .requestMatchers(HttpMethod.POST, "/api/compliance/generate/*", "/api/compliance/create").hasRole("COMPLIANCE_OFFICER")
                .requestMatchers(HttpMethod.GET, "/api/compliance/all").hasAnyRole("COMPLIANCE_OFFICER", "GOVT_AUDITOR")
                .requestMatchers(HttpMethod.PUT, "/api/compliance/update/*").hasRole("COMPLIANCE_OFFICER")
                .requestMatchers(HttpMethod.DELETE, "/api/compliance/delete/*").hasRole("COMPLIANCE_OFFICER")

                // ==========================================
                // INTEGRATED: PROGRAM CONTROLLER API
                // ==========================================
                .requestMatchers(HttpMethod.GET, "/programs/search/*").hasRole("STUDENT")
                .requestMatchers(HttpMethod.GET, "/programs/all", "/programs/*").hasAnyRole("UNIV_ADMIN", "STUDENT", "FACULTY")
                .requestMatchers(HttpMethod.PATCH, "/programs/update/*").hasRole("UNIV_ADMIN")

                // ==========================================
                // INTEGRATED: COURSE CONTROLLER API
                // ==========================================
                .requestMatchers(HttpMethod.POST, "/courses/save").hasRole("UNIV_ADMIN")
                .requestMatchers(HttpMethod.GET, "/courses/faculty/*").hasRole("FACULTY")
                .requestMatchers(HttpMethod.GET, "/courses/program/*").hasRole("STUDENT")
                .requestMatchers(HttpMethod.GET, "/courses/all", "/courses/*").hasAnyRole("UNIV_ADMIN", "STUDENT", "FACULTY")
                .requestMatchers(HttpMethod.PATCH, "/courses/update/*").hasRole("UNIV_ADMIN")

                // ==========================================
                // INTEGRATED: ENROLLMENT CONTROLLER API
                // ==========================================
                .requestMatchers(HttpMethod.POST, "/enrollments/apply").hasRole("STUDENT")
                .requestMatchers(HttpMethod.GET, "/enrollments/status/*", "/enrollments/all").hasRole("UNIV_ADMIN")
                .requestMatchers(HttpMethod.PUT, "/enrollments/update/*/admin/*/status/*").hasRole("UNIV_ADMIN")

                // ==========================================
                // INTEGRATED: RESOURCE REQUEST CONTROLLER API
                // ==========================================
                .requestMatchers(HttpMethod.POST, "/api/requests/resource").hasRole("STUDENT")
                .requestMatchers(HttpMethod.POST, "/api/requests/infrastructure").hasRole("FACULTY")
                .requestMatchers(HttpMethod.POST, "/api/requests/*/approve", "/api/requests/*/decline").hasRole("PROG_MANAGER")
                .requestMatchers(HttpMethod.GET, "/api/requests/**").authenticated()

                // ==========================================
                // INTEGRATED: INFRASTRUCTURE CONTROLLER API
                // ==========================================
                .requestMatchers("/api/infrastructure/**").hasRole("PROG_MANAGER")

                // 6. CATCH-ALL: Anything else still requires authentication
                .anyRequest().authenticated()
            )
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}