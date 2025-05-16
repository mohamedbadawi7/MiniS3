package com.example.MiniS3.entities;


import java.util.ArrayList;
import java.util.List;

public class StorageNode {


    private float capacity;
    private float load;
    private int uploadRequests;
    private int successfulUploads;
    private int deleteRequests;
    private int successfulDeletes;
    private int retrieveRequests;
    private int successfulRetrieves;
    private List<StorageNodeObject> persistentObjects;


    public StorageNode(int capacity) {
        this.capacity = capacity;
        this.load = 0;
        this.uploadRequests = 0;
        this.successfulUploads = 0;
        this.deleteRequests = 0;
        this.successfulDeletes = 0;
        this.retrieveRequests = 0;
        this.successfulRetrieves = 0;
        persistentObjects = new ArrayList<>();
    }

    public StorageNode() {}

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

    public List<StorageNodeObject> getPersistentObjects() {
        return persistentObjects;
    }

    public void setPersistentObjects(List<StorageNodeObject> persistentObjects) {
        this.persistentObjects = persistentObjects;
    }

    public int removeObject(StorageNodeObject object) {
        if (persistentObjects.contains(object)) {
            persistentObjects.remove(object);
            return 200;
        } else {
            return 400;
        }
    }

    public void decrementLoad(float size) {
        this.load -= size;
    }

    public int upload(StorageNodeObject obj) {
        updateLoad(obj.getSize());
        if (!atCapacity()) {
            persistentObjects.add(obj);
            return 200;
        } else {
            decrementLoad(obj.getSize());
            return 400;
        }
    }

    public String retrieve() {
        //TODO//

        return "name";
    }

    public void delete(StorageNodeObject obj) {
        decrementLoad(obj.getSize());
    }

}
