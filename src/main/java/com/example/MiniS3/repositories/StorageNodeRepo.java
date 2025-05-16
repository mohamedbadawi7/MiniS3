package com.example.MiniS3.repositories;

import com.example.MiniS3.entities.StorageNodeObject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StorageNodeRepo extends JpaRepository<StorageNodeObject, Integer> {


}
