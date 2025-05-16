package com.example.MiniS3.entities;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "storage_node")
public class StorageNodeObject {

    @Id
    @Column(name = "id")
    private int id;

    @Column(name = "fname")
    private String fileName;

    @Column(name = "size")
    private float size;

    @Column(name = "createdAt")
    private LocalDateTime createdAt;

    @Column(name = "accessed")
    private LocalDateTime accessed;

    public StorageNodeObject(String fileName, float size) {
        this.fileName = fileName;
        this.size = size;
        this.createdAt = LocalDateTime.now();
        this.accessed = LocalDateTime.now();
    }

    public StorageNodeObject() {}

    public int getId() {
        return id;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public float getSize() {
        return size;
    }

    public void setSize(float size) {
        this.size = size;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getAccessed() {
        return accessed;
    }

    public void setAccessed(LocalDateTime accessed) {
        this.accessed = accessed;
    }
}
