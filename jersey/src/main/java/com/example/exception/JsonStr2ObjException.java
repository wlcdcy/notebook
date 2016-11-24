package com.example.exception;

import java.io.IOException;

public class JsonStr2ObjException extends Exception {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    public JsonStr2ObjException(IOException e) {
        super(e);
    }
}
