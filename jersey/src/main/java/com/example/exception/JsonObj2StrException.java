package com.example.exception;

import java.io.IOException;

public class JsonObj2StrException extends Exception {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    public JsonObj2StrException(IOException e) {
        super(e);
    }
}
