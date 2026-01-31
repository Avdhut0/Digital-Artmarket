package com.digitalartmarketplace;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ArtworkRepository extends JpaRepository<Artwork, Integer> {

    // ✅ Required for Profile page (Artist → My Artworks)
    List<Artwork> findByArtist(User artist);
}
