package com.marceliseu.api.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.OAuth2ErrorCodes;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidatorResult;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimNames;
import org.springframework.stereotype.Component;

import com.marceliseu.api.component.ApplicationProperties.IssuerData;
import com.marceliseu.api.component.JwtIssuers;


@Component
public class TenantJwtIssuerValidator implements OAuth2TokenValidator<Jwt> {
	
	private static final Logger logger = LoggerFactory.getLogger(TenantJwtIssuerValidator.class);
    
	OAuth2Error error = new OAuth2Error(OAuth2ErrorCodes.ACCESS_DENIED, "The required audience is missing", null);
    
	private JwtIssuers jwtIssuers;
	
	public TenantJwtIssuerValidator(JwtIssuers jwtIssuers)  {
		this.jwtIssuers = jwtIssuers;
	}
	 
    @Override
    public OAuth2TokenValidatorResult validate(Jwt jwt) {
   	 	 logger.debug("JWT issuer: {}", jwt.getIssuer());
    	 logger.debug("JWT subject: {}", jwt.getSubject());
    	 logger.debug("JWT audience: {}", jwt.getAudience());
         IssuerData issuerData = jwtIssuers.getIssuerData(jwt.getClaimAsString(JwtClaimNames.ISS))
             	.orElseThrow(() -> new SecurityException("Issuer not found.")); 
         logger.debug("validating JWT audience for: {}",issuerData.getAudience());
    	 if (jwt.getAudience().contains(issuerData.getAudience())) {
    	 	logger.info("JWT audience success" );
    	 	return OAuth2TokenValidatorResult.success();
    	 } else {
    		 logger.info("Invalid JWT audience" );
 			return OAuth2TokenValidatorResult.failure(error);
    	 }
    }

}