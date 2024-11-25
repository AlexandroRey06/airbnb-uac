package com.hotel.exception;

public class DuplicateEntityException extends DatabaseException {
    public DuplicateEntityException(String message) {
        super(message);
    }

    public DuplicateEntityException(String entity, String field, String value) {
        super(String.format("%s con %s '%s' ya existe", entity, field, value));
    }
}