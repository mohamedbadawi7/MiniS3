package com.example.MiniS3.services;

import com.example.MiniS3.entities.BackupNodeObject;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public interface BackupNodeService {

    BackupNodeObject findByID(int id);
    String findNameByID(int id);
    float findSizeByID(int id);
    LocalDateTime findCreatedAtByID(int id);
    LocalDateTime findAccessedByID(int id);
    void save(BackupNodeObject backupNodeObject);
    void deleteByID(int id);
    List<BackupNodeObject> findAll();


}
