package edu.esi.ds.esiusuarios.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("http://localhost:4200") // URL de tu Angular
                .allowCredentials(true) // Permite el paso de la cookie de sesión
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS");
    }
}
