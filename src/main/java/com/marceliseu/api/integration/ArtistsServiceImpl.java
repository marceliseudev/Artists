package com.marceliseu.api.integration;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.marceliseu.api.component.ApplicationProperties;
import com.marceliseu.api.service.ServiceException;
import com.marceliseu.api.service.ServiceRuntimeException;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import kong.unirest.UnirestInstance;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.security.KeyStore;
import java.util.ArrayList;
import java.util.List;

@Service("artistService")
public class ArtistsServiceImpl implements ArtistsService {

    private UnirestInstance unirest;

    private final ObjectMapper objectMapper;

    public static final String ARTISTS_API = "artistsApi";
    private final ApplicationProperties.BackendData backend;

    public ArtistsServiceImpl(ObjectMapper objectMapper, ApplicationProperties props) {
        this.backend = props.getBackends().get(ARTISTS_API);
        this.objectMapper = objectMapper;
    }

    @PreDestroy
    public void destroy() {
        unirest.shutDown();
    }

    @PostConstruct
    public void Initialization() {
        try {
            unirest = Unirest.spawnInstance();
            unirest.config()
                    .defaultBaseUrl(backend.getBaseUrl())
                    .connectTimeout(backend.getConnectTimeout())
                    .socketTimeout(backend.getConnectionRequestTimeout());

        } catch (Exception e){
            throw new ServiceRuntimeException("initialization failed:" + e.getMessage());
        }
    }


    @Override
    @Cacheable(value = "default", unless = "#result == null")
    public List<JsonNode> listArtists() throws ServiceException {

        List<JsonNode> artists = new ArrayList<>();
        try {
            String uri = backend.getBaseUrl() + "/artists-api-controller";

            String authorization = String.format("Basic %s", backend.getAuthorization());
            HttpResponse<String> response = unirest.get(uri)
                    .header("accept", "application/json, text/plain, */*")
                    .header("Authorization", authorization)
                    .asString();

            if (response.getStatus() == 200) {
                JsonNode responseNode = objectMapper.readTree(response.getBody());
                ArrayNode artistsNode = (ArrayNode) responseNode.path("json");
                artistsNode.forEach(artist-> artists.add(artist.get(0)));
                return artists;
            } else {
                throw new ServiceException(String.format("Artists API error: %s", response.getStatus()));
            }
        } catch (Exception e) {
            throw new ServiceException("Artists API error:" + e.getMessage());
        }
    }



}
