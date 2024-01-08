package com.marceliseu.api.service;

import com.marceliseu.api.model.Album;
import lombok.AllArgsConstructor;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@AllArgsConstructor
@Service("albumService")
public class AlbumServiceImpl implements AlbumService {

    private MongoTemplate mongoTemplate;

    @Override
    public Optional<Album> getAlbum(String id) {
        Album album = mongoTemplate.findById(id, Album.class);
        return Optional.ofNullable(album);
    }

    @Override
    public void createAlbum(Album album) {
        if (Objects.isNull(album.getAlbumId())) {
            album.setAlbumId(UUID.randomUUID().toString());
        }
        mongoTemplate.save(album);
    }

    @Override
    public void updateAlbum(Album album) {
        Query query = new Query();
        query.addCriteria(Criteria.where("albumId").is(album.getAlbumId()));
        mongoTemplate.findAndReplace(query, album);
    }

    @Override
    public List<Album> listAlbums() {
        return mongoTemplate.findAll(Album.class, "albums");
    }

    @Override
    public void deleteAlbum(String id) throws ServiceException {
        Album album = mongoTemplate.findById(id, Album.class);
        if (Objects.isNull(album)) throw new ServiceException("Album does not exist", HttpStatus.NOT_FOUND.value());
        mongoTemplate.remove(album);
    }
}


