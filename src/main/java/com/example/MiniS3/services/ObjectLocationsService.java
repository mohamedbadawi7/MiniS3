package com.example.MiniS3.services;

import com.example.MiniS3.entities.ObjectLocations;
import org.springframework.stereotype.Service;

@Service
public interface ObjectLocationsService {

    int findTierById(int id);
    ObjectLocations save(ObjectLocations objectLocations);
    ObjectLocations findById(int id);
}
