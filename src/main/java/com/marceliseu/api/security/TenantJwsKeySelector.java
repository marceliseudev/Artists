package com.marceliseu.api.security;

import com.marceliseu.api.component.ApplicationProperties.IssuerData;
import com.marceliseu.api.component.JwtIssuers;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.KeySourceException;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.jwk.source.RemoteJWKSet;
import com.nimbusds.jose.proc.JWSAlgorithmFamilyJWSKeySelector;
import com.nimbusds.jose.proc.SecurityContext;
import com.nimbusds.jose.util.DefaultResourceRetriever;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.proc.JWTClaimsSetAwareJWSKeySelector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class TenantJwsKeySelector implements JWTClaimsSetAwareJWSKeySelector<SecurityContext> {

	private static final Logger logger = LoggerFactory.getLogger(TenantJwsKeySelector.class);
	
	private static final int connectTimeout = 10000;   
	
	private static final int readTimeout = 10000;
    
    private Map<String, List<PublicKey>> jwsKeysCache = new ConcurrentHashMap<>(); 
    
	private JwtIssuers jwtIssuers;
	
	public TenantJwsKeySelector(JwtIssuers jwtIssuers)  {
		this.jwtIssuers = jwtIssuers;
	}
	
	private Resource jwkresource;
	
    @Override
    public List<PublicKey> selectKeys(JWSHeader jwsHeader, JWTClaimsSet jwtClaimsSet, SecurityContext securityContext)
            throws KeySourceException {
    	List<PublicKey> keycandidates = new ArrayList<>();
        logger.debug("JWT issuer: {}", jwtClaimsSet.getIssuer());
        logger.debug("JWT subject: {}", jwtClaimsSet.getSubject());
        logger.debug("JWT audience: {}", jwtClaimsSet.getAudience());
        Optional<IssuerData> issuerDataResult = jwtIssuers.getIssuerData(jwtClaimsSet.getIssuer());
        if (issuerDataResult.isEmpty()) {
        	logger.debug("JWT Issuer not found");
        	throw new SecurityException("JWT Issuer not found"); 
        }
        logger.debug("JWT Issuer found for kid: {}", jwsHeader.getKeyID());
        IssuerData issuerData = issuerDataResult.get();
    	if (jwsKeysCache.containsKey(jwsHeader.getKeyID())) {
    		return jwsKeysCache.get(jwsHeader.getKeyID());
    	}
    	try {
	        if (issuerData.getPublicKey() != null) {
	            logger.debug("JWT using certKey");
	        	keycandidates.add(getPublicKeyfromKey(issuerData.getPublicKey()));
	        } else if (issuerData.getJwksUrl() != null) {
		            logger.debug("JWT using JwksUrl: {}",issuerData.getJwksUrl());
		    	    JWKSource jwkSource = new RemoteJWKSet(new URL(issuerData.getJwksUrl()), 
		    	    		new DefaultResourceRetriever(connectTimeout, readTimeout));
		            keycandidates = (List<PublicKey>) JWSAlgorithmFamilyJWSKeySelector.fromJWKSource(jwkSource)
		            		.selectJWSKeys(jwsHeader, securityContext);	
	        } else if (issuerData.getJwksResource() != null) {
	            logger.debug("JWT using jwksResource: {}",issuerData.getJwksResource());
	            jwkresource = new ClassPathResource(issuerData.getJwksResource());
	            JWKSet jwkSet = JWKSet.load(jwkresource.getFile());
	            JWKSource jwkSource = new ImmutableJWKSet<SecurityContext>(jwkSet);
                keycandidates = (List<PublicKey>) JWSAlgorithmFamilyJWSKeySelector.fromJWKSource(jwkSource)
                        .selectJWSKeys(jwsHeader, securityContext); 
	        }
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new SecurityException(e.getMessage());
		}       
    	logger.debug("JWT keycandidates size: {}", keycandidates.size());
        if (!keycandidates.isEmpty())
        	jwsKeysCache.put(jwsHeader.getKeyID(), keycandidates);
        return keycandidates;
    }
    
    
	private PublicKey getPublicKeyfromKey(String publickeyPem)
			throws InvalidKeySpecException, NoSuchAlgorithmException {
		publickeyPem = publickeyPem.replaceAll("-----BEGIN (.*)-----", "").replaceAll("-----END (.*)----", "")
				.replaceAll("\r\n", "").replaceAll("\n", "").trim();
		byte[] publickeyDer = Base64.getDecoder().decode(publickeyPem);
		X509EncodedKeySpec keyspec = new X509EncodedKeySpec(publickeyDer);
		KeyFactory keyfactory = KeyFactory.getInstance("RSA");
		return keyfactory.generatePublic(keyspec);
	}

}
