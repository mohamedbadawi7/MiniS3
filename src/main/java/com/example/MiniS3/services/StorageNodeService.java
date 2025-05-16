package com.example.MiniS3.services;

import com.example.MiniS3.entities.StorageNodeObject;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public interface StorageNodeService {

    StorageNodeObject findByID(int id);
    String findNameByID(int id);
    float findSizeByID(int id);
    LocalDateTime findCreatedAtByID(int id);
    LocalDateTime findAccessedByID(int id);
    StorageNodeObject save(StorageNodeObject storageNodeObject);
    void deleteByID(int id);

}
