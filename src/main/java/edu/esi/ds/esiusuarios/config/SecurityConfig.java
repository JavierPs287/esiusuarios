package edu.esi.ds.esiusuarios.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .cors(Customizer.withDefaults())
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                .requestMatchers(
                    "/users/login",
                    "/users/registrar",
                    "/users/recuperar-password",
                    "/users/reset-password/**",
                    "/users/validate-token",
                    "/users/savesession",
                    "/users/logout",
                    "/users/cancelar-cuenta",
                    "/external/**",
                    "/error"
                ).permitAll()
                .anyRequest().denyAll()
            );
        return http.build();
    }
}