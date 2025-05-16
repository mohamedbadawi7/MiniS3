package com.example.MiniS3.entities;

import jakarta.persistence.*;

import java.security.Timestamp;

@Entity
@Table(name = "object_locations")
public class ObjectLocations {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "tier")
    private String tier;

    public ObjectLocations(String tier) {
        this.tier = tier;
    }

    public ObjectLocations() {}

    public String getTier() {
        return tier;
    }

    public void setTier(String tier) {
        this.tier = tier;
    }
}
