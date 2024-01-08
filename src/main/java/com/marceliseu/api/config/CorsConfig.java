package com.marceliseu.api.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.marceliseu.api.component.CorsProperties;

/**
 * Global CORS configuration
 *
 * @see <a href="https://spring.io/guides/gs/rest-service-cors/#global-cors-configuration"/>
 */
@Configuration
public class CorsConfig implements WebMvcConfigurer {

    private final CorsProperties corsProps;

    public CorsConfig(CorsProperties corsProps) {
        this.corsProps = corsProps;
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        if (!corsProps.getAllowedOrigins().isEmpty()) {
            String[] allowedOrigins = new String[corsProps.getAllowedOrigins().size()];
            corsProps.getAllowedOrigins().toArray(allowedOrigins);
            registry.addMapping("/**")
              // Allowed origins
              .allowedOrigins(allowedOrigins)
              // Include Access-Control-Allow-Credentials header
              .allowCredentials(Boolean.TRUE)
              .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS", "HEAD", "TRACE");
        }
    }

}
