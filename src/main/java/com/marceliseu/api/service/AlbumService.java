package com.marceliseu.api.service;

import com.marceliseu.api.model.Album;

import java.util.List;
import java.util.Optional;

public interface AlbumService {

    public Optional<Album> getAlbum(String id);

    public void createAlbum(Album album);

    public void updateAlbum(Album album);

    public List<Album> listAlbums();

    public void deleteAlbum(String id) throws ServiceException;

}
