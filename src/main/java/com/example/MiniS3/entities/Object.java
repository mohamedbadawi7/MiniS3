package com.example.MiniS3.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Id;

import java.time.LocalDateTime;

public class Object {


    private int id;


    private String fileName;


    private float size;

    private LocalDateTime createdAt;

    public Object(String fileName, float size) {
        this.fileName = fileName;
        this.size = size;
        this.createdAt = LocalDateTime.now();
    }
}
