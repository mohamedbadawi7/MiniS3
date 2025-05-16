package com.example.MiniS3.entities;

public class BackupNode {

    private float capacity;
    private float load;
    private int uploadRequests;
    private int successfulUploads;
    private int deleteRequests;
    private int successfulDeletes;
    private int retrieveRequests;
    private int successfulRetrieves;


    public BackupNode(int capacity) {
        this.capacity = capacity;
        this.load = 0;
        this.uploadRequests = 0;
        this.successfulUploads = 0;
        this.deleteRequests = 0;
        this.successfulDeletes = 0;
        this.retrieveRequests = 0;
        this.successfulRetrieves = 0;
    }

    public BackupNode() {}

    public float getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public float getLoad() {
        return load;
    }

    public void setLoad(int load) {
        this.load = load;
    }

    public int getUploadRequests() {
        return uploadRequests;
    }

    public int getSuccessfulUploads() {
        return successfulUploads;
    }

    public int getDeleteRequests() {
        return deleteRequests;
    }

    public int getSuccessfulDeletes() {
        return successfulDeletes;
    }

    public int getRetrieveRequests() {
        return retrieveRequests;
    }

    public int getSuccessfulRetrieves() {
        return successfulRetrieves;
    }

    public void updateLoad(float size) {
        this.load = this.load + size;
    }

    public boolean atCapacity() {
        return this.load >= this.capacity;
    }

    public void incrUploadRequests() {
        this.uploadRequests++;
    }

    public void incrSuccessfulUploads() {
        this.successfulUploads++;
    }

    public void incrDeleteRequests() {
        this.deleteRequests++;
    }

    public void incrSuccessfulDeletes() {
        this.successfulDeletes++;
    }

    public void incrRetrieveRequests() {
        this.retrieveRequests++;
    }

    public void incrSuccessfulRetrieves() {
        this.successfulRetrieves++;
    }

    public int upload(BackupNodeObject bng) {
        //TODO//
        return 200;
    }

    public String retrieve() {
        //TODO//

        return "name";
    }

    public void delete() {
        //TODO//
    }
}
