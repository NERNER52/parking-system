package com.example.parking.exception;

/** 422 — запрос корректный по формату, но нарушает бизнес-правило (например, тип авто не подходит месту). */
public class BusinessRuleException extends RuntimeException {
    public BusinessRuleException(String message) {
        super(message);
    }
}
