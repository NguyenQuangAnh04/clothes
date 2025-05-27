package com.example.clothes.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig {
    @Autowired
    private JwtFilter jwtFilter;

    @Autowired
    private UserDetailsService userDetailsService;
    @Value("${jwt.api}")
    private String api;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(
                        request ->
                                request.requestMatchers("/api/login", "/api/register").permitAll()
                                        .requestMatchers(HttpMethod.PUT,"/api/*").hasAnyRole("ADMIN","USER")
                                        .requestMatchers(HttpMethod.GET, "/api/product/**").permitAll()
                                        .requestMatchers(HttpMethod.POST, "/api/product/*").hasAnyRole("ADMIN", "USER")
                                        .requestMatchers(HttpMethod.DELETE, "/api/product/*").hasAnyRole("ADMIN", "USER")
                                        .requestMatchers(HttpMethod.POST, "/api/upload/*").hasAnyRole("ADMIN", "USER")
                                        .requestMatchers(HttpMethod.POST, "/api/order/*").hasAnyRole("ADMIN", "USER")
                                        .requestMatchers(HttpMethod.GET, "/api/order/**").hasAnyRole("ADMIN", "USER")
                                        .requestMatchers(HttpMethod.PUT, "/api/cart/*").hasAnyRole("ADMIN", "USER")
                                        .requestMatchers(HttpMethod.DELETE, "/api/cart/*").hasAnyRole("ADMIN", "USER")
                                        .requestMatchers(HttpMethod.POST, "/api/cart/*").permitAll()
                                        .requestMatchers(HttpMethod.POST, "/api/upload/*").hasAnyRole("ADMIN", "USER")
                                        .requestMatchers(HttpMethod.GET, "/api/product/search").permitAll()
                                        .requestMatchers(HttpMethod.GET, "/api/cart/cart-detail/*").hasAnyRole("ADMIN", "USER")
                                        .requestMatchers(HttpMethod.GET, "/api/category").permitAll()
                                        .requestMatchers(HttpMethod.DELETE, "/api/category/**").hasAnyRole("ADMIN")
                                        .requestMatchers(HttpMethod.PUT, "/api/category/*").hasAnyRole("ADMIN")

                                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }


    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
        daoAuthenticationProvider.setUserDetailsService(userDetailsService);
        daoAuthenticationProvider.setPasswordEncoder(bCryptPasswordEncoder());
        return daoAuthenticationProvider;
    }

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedHeaders(List.of("Authorization", "Content-Type"));
        configuration.setAllowedOrigins(List.of("http://localhost:4200"));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource urlBasedCorsConfigurationSource = new UrlBasedCorsConfigurationSource();
        urlBasedCorsConfigurationSource.registerCorsConfiguration("/**", configuration);
        return urlBasedCorsConfigurationSource;
    }
}
