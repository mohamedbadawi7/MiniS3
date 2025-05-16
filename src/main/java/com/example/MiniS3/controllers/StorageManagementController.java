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

        //Storage to Delete
        System.out.println("Searching for object in node storage...");
        List<StorageNodeObject> toBeRemoved = new ArrayList<>();
        boolean found = false;
        storageNode.getPersistentObjects().forEach(o -> {
            if (o.getId() == id) {
                System.out.println("Object being deleted from storage node...");

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
                    storageNode.delete(o); //Reduce the load of the storage node
                    toBeRemoved.add(o);
                    storageNode.incrSuccessfulDeletes();
                    System.out.println("Object deleted from storage node...");

                    ObjectLocations objloc = objectLocationsService.findById(id);
                    objloc.setTier("DN");
                    ObjectLocations newobj = objectLocationsService.save(objloc);

                } else {
                    System.out.println("Object cannot be deleted... Garbage is full.");
                    return;
                }

            }
        });
        toBeRemoved.forEach(o -> {
            storageNode.removeObject(o);
            return;
        });

        //Backup to Delete
        System.out.println("Searching for object in backup storage...");
        BackupNodeObject backupObj = backupNodeService.findByID(id); //Retrieve intended ID
        if (backupObj != null) { //Ensure that the given id is real

            System.out.println("Object found in backup storage...");
            backupNode.incrDeleteRequests(); //Increment number of delete requests in backup node

            deleteNode.incrUploadRequests(); //Increment number of upload requests in delete node
            DeleteNodeObject deleteObj = new DeleteNodeObject(backupObj.getId(), backupObj.getFileName(), backupObj.getSize()); //create new delete object
            int res = deleteNode.upload(deleteObj); //attempt to upload the object load onto the node

            if (res == 200) { //with success

                //Add to Delete Node
                deleteNodeService.save(deleteObj);
                System.out.println("Object added to delete node...");

                //Remove From Backup Node
                int res1 = backupNode.delete(backupObj);
                if (res1 == 200) {
                    backupNodeService.deleteByID(id);
                    backupNode.incrSuccessfulDeletes();
                }

                //Change Object Location
                ObjectLocations objloc = objectLocationsService.findById(id);
                objloc.setTier("DN");
                ObjectLocations newobj = objectLocationsService.save(objloc);
            } else {
                System.out.println("Object cannot be deleted... Garbage is full.");
            }
        } else {
            System.out.println("Object not found in backup storage...");
        }

    }

    @GetMapping("/retrieve/{id}")
    public String retrieve(@PathVariable int id) {

        //From Storage Node, persistent storage
        for (int i = 0; i < storageNode.getPersistentObjects().size(); i++) {

            StorageNodeObject obj = storageNode.getPersistentObjects().get(i);

            if (obj.getId() == id) {
                System.out.println("Object found in Storage Node...");
                storageNode.incrRetrieveRequests();
                //Update obj and save it to persistent storage and db
                LocalDateTime currentTime = LocalDateTime.now();
                obj.setAccessed(currentTime);

                StorageNodeObject newObj = storageNodeService.save(obj);
                storageNode.incrSuccessfulRetrieves();
                System.out.println("Object name...");
                return obj.getFileName();
            }
        }

        //From Backup Node, db
        List<BackupNodeObject> backupNodeObjects = backupNodeService.findAll();
        for (int i = 0; i < backupNodeObjects.size(); i++) {

            BackupNodeObject obj = backupNodeObjects.get(i);

            if (obj.getId() == id) {
                System.out.println("Object found in Backup Node...");
                backupNode.incrRetrieveRequests();

                //Update obj, save it to storage node, remove from backup node
                LocalDateTime currentTime = LocalDateTime.now();
                obj.setAccessed(currentTime);

                //Remove from backup node
                backupNode.delete(obj);
                backupNodeService.deleteByID(id);

                //Save to storage node (persistent and db)
                StorageNodeObject sno = new StorageNodeObject(obj.getId(), obj.getFileName(), obj.getSize(), obj.getCreatedAt(), obj.getAccessed());
                storageNode.upload(sno);
                StorageNodeObject snoNew = storageNodeService.save(sno);

                //Object Location Update
                ObjectLocations objloc = objectLocationsService.findById(obj.getId());
                objloc.setTier("SN");
                ObjectLocations newobj = objectLocationsService.save(objloc);

                backupNode.incrSuccessfulRetrieves();
                System.out.println("Object name...");
                return obj.getFileName();
            }
        }

        //From Delete Node, db
        List<DeleteNodeObject> deleteNodeObjects = deleteNodeService.findAll();
        for (int i = 0; i < deleteNodeObjects.size(); i++) {
            DeleteNodeObject obj = deleteNodeObjects.get(i);

            if (obj.getId() == id) {
                System.out.println("Object found in Delete Node...");
                deleteNode.incrRetrieveRequests();

                //Update obj, save it to storage node, remove from backup node
                LocalDateTime currentTime = LocalDateTime.now();
                obj.setAccessed(currentTime);

                //Remove from delete node
                deleteNode.decrementLoad(obj.getSize());
                deleteNodeService.deleteByID(id);

                //Save to storage node (persistent and db)
                StorageNodeObject sno = new StorageNodeObject(obj.getId(), obj.getFileName(), obj.getSize(), obj.getCreatedAt(), obj.getAccessed());
                storageNode.upload(sno);
                StorageNodeObject snoNew = storageNodeService.save(sno);

                //Object Location Update
                ObjectLocations objloc = objectLocationsService.findById(obj.getId());
                objloc.setTier("SN");
                ObjectLocations newobj = objectLocationsService.save(objloc);


                deleteNode.incrSuccessfulRetrieves();
                System.out.println("Object name...");
                return obj.getFileName();
            }
        }
        return "0";
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

    public void purge() {
        System.out.println("Beginning the purge...");

        List<DeleteNodeObject> deleteNodeObjects = deleteNodeService.findAll();
        deleteNodeObjects.forEach(o -> {
            LocalDateTime currentTime = LocalDateTime.now();
            Duration duration = Duration.between(o.getAccessed(), currentTime);
            if (duration.toDays() >= 1) {
                deleteNode.incrPurgeRequests();
                System.out.println("Purged " + o.getFileName() + "...");
                deleteNode.decrementLoad(o.getSize());
                deleteNodeService.deleteByID(o.getId());
                deleteNode.incrSuccessfulPurges();
            }
        });


        System.out.println("Purge over.");

    }

    @GetMapping("/report")
    public void reportSystemOverview() {
        System.out.println("\n--- SYSTEM REPORT ---");

        System.out.println("Storage Node:");
        System.out.println("  Upload Requests: " + storageNode.getUploadRequests());
        System.out.println("  Successful Uploads: " + storageNode.getSuccessfulUploads());
        System.out.println("  Retrieve Requests: " + storageNode.getRetrieveRequests());
        System.out.println("  Successful Retrieves: " + storageNode.getSuccessfulRetrieves());
        System.out.println("  Delete Requests: " + storageNode.getDeleteRequests());
        System.out.println("  Successful Deletes: " + storageNode.getSuccessfulDeletes());
        System.out.println("  Current Load: " + storageNode.getLoad());
        System.out.println("  Stored Objects: " + storageNode.getPersistentObjects().size());

        System.out.println("\nBackup Node:");
        System.out.println("  Upload Requests: " + backupNode.getUploadRequests());
        System.out.println("  Successful Uploads: " + backupNode.getSuccessfulUploads());
        System.out.println("  Retrieve Requests: " + backupNode.getRetrieveRequests());
        System.out.println("  Successful Retrieves: " + backupNode.getSuccessfulRetrieves());
        System.out.println("  Delete Requests: " + backupNode.getDeleteRequests());
        System.out.println("  Successful Deletes: " + backupNode.getSuccessfulDeletes());
        System.out.println("  Current Load: " + backupNode.getLoad());

        System.out.println("\nDelete Node:");
        System.out.println("  Upload Requests: " + deleteNode.getUploadRequests());
        System.out.println("  Successful Uploads: " + deleteNode.getSuccessfulUploads());
        System.out.println("  Retrieve Requests: " + deleteNode.getRetrieveRequests());
        System.out.println("  Successful Retrieves: " + deleteNode.getSuccessfulRetrieves());
        System.out.println("  Purge Requests: " + deleteNode.getPurgeRequests());
        System.out.println("  Successful Purges: " + deleteNode.getSuccessfulPurges());
        System.out.println("  Current Load: " + deleteNode.getLoad());

        System.out.println("\n--- END REPORT ---\n");
    }


}
