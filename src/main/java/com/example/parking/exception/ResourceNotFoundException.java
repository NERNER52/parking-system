package com.example.parking.exception;

/** 404 — сущность не найдена. */
public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
}
