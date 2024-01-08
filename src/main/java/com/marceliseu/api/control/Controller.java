package com.marceliseu.api.control;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserRecord;
import com.marceliseu.api.integration.ArtistsService;
import com.marceliseu.api.integration.FirebaseService;
import com.marceliseu.api.model.Album;
import com.marceliseu.api.service.AlbumService;
import com.marceliseu.api.service.ServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.*;

@CrossOrigin(allowCredentials = "true")
@RestController
@RequestMapping(produces = "application/json; charset=UTF-8")
public class Controller {

	private static final Logger logger = LoggerFactory.getLogger(Controller.class);

	@Autowired
	public AlbumService albumService;

    @Autowired
    public ArtistsService artistsService;

    @Autowired
    public FirebaseService firebaseService;

    @GetMapping("/albums")
    public List<Album> listAlbums() throws ServiceException {
        return albumService.listAlbums();
    }
    
    @GetMapping("/album/{id}")
    public ResponseEntity<Album> getAlbum(@PathVariable String id) throws ServiceException {
        Optional<Album> album = albumService.getAlbum(id);
        return album.map(value -> new ResponseEntity<>(value, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity("Album not found",HttpStatus.NOT_FOUND));
    }

    @DeleteMapping("/album/{id}")
    public void deleteAlbum(@PathVariable String id) throws ServiceException {
        albumService.deleteAlbum(id);
    }
    
    @PostMapping("/album")
    public void createAlbum(@RequestBody Album album) throws ServiceException {
        albumService.createAlbum(album);
    }

    @PutMapping("/album")
    public void updateAlbum(@RequestBody Album album) throws ServiceException {
        albumService.updateAlbum(album);
    }

    @PutMapping("/addRole")
    public ResponseEntity<Void> addRole(@AuthenticationPrincipal Jwt jwt, @RequestParam String role) throws ServiceException {
        firebaseService.addRole(jwt, role);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/artists")
    public List<JsonNode> listArtists() throws Exception {
        return artistsService.listArtists();
    }
    
}
