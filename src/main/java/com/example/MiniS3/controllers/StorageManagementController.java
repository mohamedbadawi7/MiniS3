package com.example.MiniS3.controllers;
import com.example.MiniS3.entities.*;
import com.example.MiniS3.entities.Object;
import com.example.MiniS3.services.BackupNodeService;
import com.example.MiniS3.services.DeleteNodeService;
import com.example.MiniS3.services.ObjectLocationsService;
import com.example.MiniS3.services.StorageNodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@RestController
public class StorageManagementController {

    @Autowired
    private ObjectLocationsService objectLocationsService;
    @Autowired
    private StorageNodeService storageNodeService;
    @Autowired
    private BackupNodeService backupNodeService;
    @Autowired
    private DeleteNodeService deleteNodeService;
    StorageNode storageNode;
    BackupNode backupNode;
    DeleteNode deleteNode;


    public StorageManagementController() {
        storageNode = new StorageNode(100);
        backupNode = new BackupNode(500);
        deleteNode = new DeleteNode(100);
    }

    @PostMapping("/upload")
    public int upload(@RequestBody Object object) {

        ObjectLocations objloc = new ObjectLocations("SN");
        ObjectLocations newObjLoc = objectLocationsService.save(objloc);

        StorageNodeObject obj = new StorageNodeObject(newObjLoc.getId(), object.getFileName(), object.getSize());
        storageNode.incrUploadRequests();

        int res = storageNode.upload(obj);
        if (res == 200) {
            storageNode.incrSuccessfulUploads();
            StorageNodeObject savedObj = storageNodeService.save(obj);
            storageNode.removeObject(obj);
            storageNode.upload(savedObj);

        }
        return res;
    }

    @DeleteMapping("/delete/{id}")
    public void delete(@PathVariable int id) {

        System.out.println("Searching for object in node storage...");
        List<StorageNodeObject> toBeRemoved = new ArrayList<>();
        storageNode.getPersistentObjects().forEach(o -> {
            if (o.getId() == id) {
                System.out.println("Object being deleted from storage node...");

                //Object Location Update
                ObjectLocations objloc = objectLocationsService.findById(id);
                objloc.setTier("DN");
                ObjectLocations newobj = objectLocationsService.save(objloc);

                //Add to Delete Node
                deleteNode.incrUploadRequests();
                DeleteNodeObject dno = new DeleteNodeObject(o.getId(), o.getFileName(), o.getSize());
                int res = deleteNode.upload(dno);
                if (res == 200) {

                    System.out.println("Object added to delete node...");
                    deleteNode.incrSuccessfulUploads(); //Increment successful uploads
                    deleteNodeService.save(dno); //Add new record to table

                    //Remove From Storage Node
                    storageNode.incrDeleteRequests();
                    storageNodeService.deleteByID(o.getId()); //Delete given record by id
                    storageNode.decrementLoad(o.getSize()); //Reduce the load of the storage node
                    toBeRemoved.add(o);
                    System.out.println("Object deleted from storage node...");

                } else {
                    System.out.println("Object cannot be deleted... Garbage is full.");
                }

            }
        });
        toBeRemoved.forEach(o -> {
            storageNode.removeObject(o);
        });

    }



    @Scheduled(fixedRate = 600000)
    public void updateTier() {

        System.out.println("Updating storage tiers...");
        List<StorageNodeObject> toBeRemoved = new ArrayList<>();
        storageNode.getPersistentObjects().forEach(o -> {
            Duration duration = Duration.between(o.getAccessed(), LocalDateTime.now());
            if (duration.toMinutes() >= 60) {

                //Object Location Update
                ObjectLocations objloc = objectLocationsService.findById(o.getId());
                objloc.setTier("BN");
                ObjectLocations newobj = objectLocationsService.save(objloc);

                //Backup Node Object Creation
                backupNode.incrUploadRequests();
                BackupNodeObject bng = new BackupNodeObject(o.getId(), o.getFileName(), o.getSize());

                //Ensure capacity is not met after uploading
                int res = backupNode.upload(bng);
                if (res == 200) {
                    backupNode.incrSuccessfulUploads(); //Increment successfull uploads
                    backupNodeService.save(bng); //Add new record to table

                    storageNodeService.deleteByID(o.getId()); //Delete given record by id
                    storageNode.decrementLoad(o.getSize()); //Reduce the load of the storage node
                    toBeRemoved.add(o);
                }
            }
        });
        toBeRemoved.forEach(o -> {
            storageNode.removeObject(o);
        });
        System.out.println("Storage tiers updated...");

        purge();
    }

    //TODO//
    public void purge() {
        System.out.println("Beginning the purge...");


        System.out.println("Purge over.");

    }

}
