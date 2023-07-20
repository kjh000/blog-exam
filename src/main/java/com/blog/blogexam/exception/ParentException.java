package com.blog.blogexam.exception;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

@Getter
public abstract class ParentException extends RuntimeException{

    private final Map<String, String> validation = new HashMap<>();

    public ParentException(String message) {
        super(message);
    }

    public ParentException(String message, Throwable cause) {
        super(message, cause);
    }

    public abstract String statusCode();

    public void addValidation(String fieldName,String message) {
        validation.put(fieldName, message);
    }
}
