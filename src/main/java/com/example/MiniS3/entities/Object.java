package com.example.MiniS3.entities;

import jakarta.persistence.*;

import java.security.Timestamp;

@Entity
@Table(name = "object_locations")
public class Object {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "tier")
    private String tier;

    private String fileName;

    private float size;

    private Timestamp createdAt;

    private Timestamp movedAt;




}
