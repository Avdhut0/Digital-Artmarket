package com.digitalartmarketplace;

import java.util.List;

import org.springframework.stereotype.Service;

@Service
public class ArtworkService {

    private final ArtworkRepository repository;

    public ArtworkService(ArtworkRepository repository) {
        this.repository = repository;
    }

    public void save(Artwork artwork) {
        repository.save(artwork);
    }

    public List<Artwork> findAll() {
        return repository.findAll();
    }

    public Artwork findById(int id) {
        return repository.findById(id).orElse(null);
    }

    public void deleteById(int id) {
        repository.deleteById(id);
    }

    // ✅ ADD THIS METHOD (FOR PROFILE PAGE)
    public List<Artwork> findByArtist(User artist) {
        return repository.findByArtist(artist);
    }
}
