package com.marceliseu.api.component;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.marceliseu.api.component.ApplicationProperties.IssuerData;

@Component
public class JwtIssuers {
    
	private Logger logger = LoggerFactory.getLogger(JwtIssuers.class);
    
    private Map<String, IssuerData> issuers = new HashMap<>();
    
    private ApplicationProperties applicationProperties;

	public JwtIssuers(ApplicationProperties applicationProperties)  {
		this.applicationProperties = applicationProperties;
	}
	
	public Optional<IssuerData> getIssuerData(String issuer) {
		if (issuers.isEmpty()) {
			if (applicationProperties.getJwt().getIssuers() == null || applicationProperties.getJwt().getIssuers().isEmpty()) {
				logger.error("JWT Configuration Error - no issuers");
			} else {
				applicationProperties.getJwt().getIssuers().values().forEach(issuerData->{
					issuers.put(issuerData.getIssuer(), issuerData);
					logger.debug("added issuer {}", issuerData.getIssuer());
				});
			}
		}
		return Optional.ofNullable(issuers.get(issuer));
	}

}

