package com.digitalartmarketplace;

import jakarta.persistence.*;

@Entity
public class Orders {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private int artworkId;
    private String buyerEmail;

    public int getId() { return id; }
    public int getArtworkId() { return artworkId; }
    public String getBuyerEmail() { return buyerEmail; }

    public void setId(int id) { this.id = id; }
    public void setArtworkId(int artworkId) { this.artworkId = artworkId; }
    public void setBuyerEmail(String buyerEmail) { this.buyerEmail = buyerEmail; }
}
