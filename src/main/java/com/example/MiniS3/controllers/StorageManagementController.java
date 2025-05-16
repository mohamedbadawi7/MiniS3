package com.example.MiniS3.controllers;
import com.example.MiniS3.entities.*;
import com.example.MiniS3.entities.Object;
import com.example.MiniS3.services.BackupNodeService;
import com.example.MiniS3.services.DeleteNodeService;
import com.example.MiniS3.services.ObjectLocationsService;
import com.example.MiniS3.services.StorageNodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

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

        StorageNodeObject obj = new StorageNodeObject(object.getFileName(), object.getSize());
        storageNode.incrUploadRequests();
        storageNodeService.save(obj);
        int res = storageNode.upload(obj);
        if (res == 200) {storageNode.incrSuccessfulUploads();}
        return res;
    }



    @Scheduled(fixedRate = 600000)
    public void updateTier() {

        List<StorageNodeObject> toBeRemoved = new ArrayList<>();
        storageNode.getPersistentObjects().forEach(o -> {
            Duration duration = Duration.between(o.getAccessed(), LocalDateTime.now());
            if (duration.toMinutes() >= 60) {
                backupNode.incrUploadRequests();
                BackupNodeObject bng = new BackupNodeObject(o.getId(), o.getFileName(), o.getSize());
                backupNodeService.save(bng);
                int res = backupNode.upload(bng);
                if (res == 200) {backupNode.incrSuccessfulUploads();}
                toBeRemoved.add(o);
                storageNodeService.deleteByID(o.getId());
            }
        });
        toBeRemoved.forEach(o -> {
            storageNodeService.deleteByID(o.getId());
        });
    }

}
