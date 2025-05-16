package com.example.MiniS3.services;

import com.example.MiniS3.entities.DeleteNodeObject;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public interface DeleteNodeService {

    DeleteNodeObject findByID(int id);
    String findNameByID(int id);
    float findSizeByID(int id);
    LocalDateTime findCreatedAtByID(int id);
    LocalDateTime findAccessedByID(int id);
    void save(DeleteNodeObject deleteNodeObject);
    void deleteByID(int id);

}
