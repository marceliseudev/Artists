package com.marceliseu.api.security;

import java.util.Collection;
import java.util.Collections;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

import com.marceliseu.api.component.ApplicationProperties.IssuerData;
import com.marceliseu.api.component.JwtIssuers;



@Component
public class TenantJwsGrantedAuthoritiesConverter implements Converter<Jwt, Collection<GrantedAuthority>> {

	private static final Logger logger = LoggerFactory.getLogger(TenantJwsGrantedAuthoritiesConverter.class); 
    
	private JwtIssuers jwtIssuers;
	
	public TenantJwsGrantedAuthoritiesConverter(JwtIssuers jwtIssuers)  {
		this.jwtIssuers = jwtIssuers;
	}
		
	@Override
	public Collection<GrantedAuthority> convert(Jwt jwt) {

        IssuerData issuerData = jwtIssuers.getIssuerData(jwt.getIssuer().toString())
            	.orElseThrow(() -> new SecurityException("Issuer not found.")); 
		Collection<?> authorities = (Collection<?>) jwt.getClaims().getOrDefault(issuerData.getPermissionsScopeName(),
				Collections.emptyList());
		logger.debug("JWT permissionsScopeName:" + issuerData.getPermissionsScopeName());
		logger.debug("JWT scope empty " + authorities.isEmpty());
		logger.debug("JWT scope entries=" + authorities.size());
		authorities.forEach(action -> {
			logger.debug("authority:" + action);
		});
		return authorities.stream().map(Object::toString).map(SimpleGrantedAuthority::new)
				.collect(Collectors.toList());
	}

}
