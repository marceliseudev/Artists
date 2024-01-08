package com.marceliseu.api.component;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Getter
@Setter
@Component
@ConfigurationProperties("app")
public class ApplicationProperties {

	private JwtData jwt;

    private Map<String, BackendData> backends;

	@Getter
	@Setter
    public static class JwtData {

    	private boolean authenticatedOnly;

        private Map<String, IssuerData> issuers;

    }

	@Getter
	@Setter
    public static class IssuerData {

        private String issuer;

        private String jwksUrl;
        
        private String jwksResource;

        private String publicKey;

        private String privateKey;

        private String audience;

        private String permissionsScopeName;

    }

    @Getter
    @Setter
    public static class BackendData {

        private String baseUrl;

        private Integer connectTimeout;

        private Integer connectionRequestTimeout;

        private String authorization;
    }

}
