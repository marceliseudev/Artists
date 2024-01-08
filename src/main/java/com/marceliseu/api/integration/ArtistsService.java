package com.marceliseu.api.integration;

import com.fasterxml.jackson.databind.JsonNode;

import java.util.List;

public interface ArtistsService {

    List<JsonNode> listArtists() throws Exception;
}
