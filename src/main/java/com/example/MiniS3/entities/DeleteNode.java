package com.example.MiniS3.entities;

public class DeleteNode {

    private float capacity;
    private float load;
    private int uploadRequests;
    private int successfulUploads;
    private int purgeRequests;
    private int successfulPurges;
    private int retrieveRequests;
    private int successfulRetrieves;


    public DeleteNode(int capacity) {
        this.capacity = capacity;
        this.load = 0;
        this.uploadRequests = 0;
        this.successfulUploads = 0;
        this.purgeRequests = 0;
        this.successfulPurges = 0;
        this.retrieveRequests = 0;
        this.successfulRetrieves = 0;
    }

    public DeleteNode() {}

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

    public int getPurgeRequests() {
        return purgeRequests;
    }

    public int getSuccessfulPurges() {
        return successfulPurges;
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
        this.purgeRequests++;
    }

    public void incrSuccessfulDeletes() {
        this.successfulPurges++;
    }

    public void incrRetrieveRequests() {
        this.retrieveRequests++;
    }

    public void incrSuccessfulRetrieves() {
        this.successfulRetrieves++;
    }

    public void upload() {
        //TODO//
    }

    public String retrieve() {
        //TODO//

        return "name";
    }

    public void purge() {
        //TODO//
    }
}
