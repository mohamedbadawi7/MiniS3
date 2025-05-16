package com.example.MiniS3.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.MiniS3.entities.ObjectLocations;

public interface ObjectLocationsRepo extends JpaRepository<ObjectLocations, Integer> {

    int findIdByTier(String tier);

}
