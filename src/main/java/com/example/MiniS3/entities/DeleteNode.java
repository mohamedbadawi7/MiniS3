package com.example.MiniS3.entities;
import jakarta.persistence.*;

import java.security.Timestamp;
import java.time.LocalDateTime;

@Entity
@Table(name = "delete_node")
public class DeleteNode {

    @Id
    @Column(name = "id")
    private int id;

    @Column(name = "fname")
    private String fileName;

    @Column(name = "size")
    private float size;

    @Column(name = "createdAt")
    private LocalDateTime createdAt;

    @Column(name = "addedAt")
    private LocalDateTime addedAt;


    private int capacity;
    private int load;
    private int uploadRequests;
    private int successfulUploads;
    private int deleteRequests;
    private int successfulDeletes;
    private int retrieveRequests;
    private int successfulRetrieves;


    public DeleteNode(int capacity) {
        this.capacity = capacity;
        this.load = 0;
        this.uploadRequests = 0;
        this.successfulUploads = 0;
        this.deleteRequests = 0;
        this.successfulDeletes = 0;
        this.retrieveRequests = 0;
        this.successfulRetrieves = 0;
    }

    public DeleteNode() {}
}
