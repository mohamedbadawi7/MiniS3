package com.example.MiniS3.services;

import org.springframework.stereotype.Service;

@Service
public interface ObjectLocationsService {

    int findIdByTier(String tier);
}
