package com.example.MiniS3.repositories;

import com.example.MiniS3.entities.BackupNodeObject;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BackupNodeRepo extends JpaRepository<BackupNodeObject, Integer> {
}
