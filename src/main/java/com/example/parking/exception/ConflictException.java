package com.example.parking.exception;

/** 409 — конфликт состояния (место занято, дубликат, есть активная сессия и т.п.). */
public class ConflictException extends RuntimeException {
    public ConflictException(String message) {
        super(message);
    }
}
