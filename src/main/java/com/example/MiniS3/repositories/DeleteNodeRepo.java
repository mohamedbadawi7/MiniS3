package com.example.MiniS3.repositories;

import com.example.MiniS3.entities.DeleteNodeObject;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DeleteNodeRepo extends JpaRepository<DeleteNodeObject, Integer> {
}
