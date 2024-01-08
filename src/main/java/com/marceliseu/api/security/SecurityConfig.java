package com.marceliseu.api.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtValidators;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.boot.actuate.autoconfigure.security.servlet.EndpointRequest;

import com.marceliseu.api.component.ApplicationProperties;
import com.nimbusds.jose.proc.SecurityContext;
import com.nimbusds.jwt.proc.ConfigurableJWTProcessor;
import com.nimbusds.jwt.proc.DefaultJWTProcessor;
import com.nimbusds.jwt.proc.JWTClaimsSetAwareJWSKeySelector;
import com.nimbusds.jwt.proc.JWTProcessor;

@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true)
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

	private Logger logger = LoggerFactory.getLogger(SecurityConfig.class);
	
	private ApplicationProperties applicationProperties;
	
	private TenantJwsGrantedAuthoritiesConverter tenantJwsGrantedAuthoritiesConverter;
	
	private static final String WWW_Authenticate = "WWW-Authenticate";

	public SecurityConfig(ApplicationProperties applicationProperties, 
			TenantJwsGrantedAuthoritiesConverter tenantJwsGrantedAuthoritiesConverter)  {
		this.applicationProperties = applicationProperties;
		this.tenantJwsGrantedAuthoritiesConverter = tenantJwsGrantedAuthoritiesConverter;
        // Allow SecurityContextHolder on @Async methods
        SecurityContextHolder.setStrategyName(SecurityContextHolder.MODE_INHERITABLETHREADLOCAL);
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		logger.debug("SecurityConfig");
		http.csrf().disable().sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and().cors()
				.and().oauth2ResourceServer(
						oauth2 -> oauth2.jwt(jwt -> jwt.jwtAuthenticationConverter(grantedAuthoritiesConverter())));
		if (applicationProperties.getJwt() == null || applicationProperties.getJwt().isAuthenticatedOnly()) {
			http.authorizeRequests()
					.requestMatchers(EndpointRequest.to("health")).permitAll()
					.anyRequest().authenticated();
		} else {
			http.authorizeRequests().anyRequest().permitAll();
		}

		http.exceptionHandling().authenticationEntryPoint((request, response, e) -> {
			String wwwAuthenticateMessage = String.format("Bearer error=\"invalid_token\", error_description=\"%s\"", e.getMessage());
			response.addHeader(WWW_Authenticate, wwwAuthenticateMessage);
			response.setStatus(HttpStatus.UNAUTHORIZED.value());
			response.setContentType("application/json");
			response.getWriter().write("{\"status\":401,\"error\":\"Caller not authenticated.\",\"message\":\"\"}");
		}).accessDeniedHandler((request, response, e) -> {
			response.setStatus(HttpStatus.FORBIDDEN.value());
			response.setContentType("application/json");
			response.getWriter().write("{\"status\":403,\"error\":\"Caller not authorized.\",\"message\":\"\"}");
		});
	}

	Converter<Jwt, AbstractAuthenticationToken> grantedAuthoritiesConverter() {
		JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
		jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(tenantJwsGrantedAuthoritiesConverter);
		return jwtAuthenticationConverter;
	}
	
	@Bean
	JWTProcessor jwtProcessor(JWTClaimsSetAwareJWSKeySelector keySelector) {
	    ConfigurableJWTProcessor<SecurityContext> jwtProcessor = new DefaultJWTProcessor();
	    jwtProcessor.setJWTClaimsSetAwareJWSKeySelector(keySelector);
	    return jwtProcessor;
	}
	
	@Bean
	JwtDecoder jwtDecoder(JWTProcessor jwtProcessor, OAuth2TokenValidator<Jwt> jwtValidator) {
	    NimbusJwtDecoder jwtDecoder = new NimbusJwtDecoder(jwtProcessor);
	    OAuth2TokenValidator<Jwt> validator = new DelegatingOAuth2TokenValidator<>
	            (JwtValidators.createDefault(), jwtValidator);
	    jwtDecoder.setJwtValidator(validator);
	    return jwtDecoder;
	}

}