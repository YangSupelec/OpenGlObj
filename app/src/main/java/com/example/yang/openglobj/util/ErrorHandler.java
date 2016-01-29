package com.example.yang.openglobj.util;

/**
 * Created by yang on 29/01/16.
 */
public interface ErrorHandler {
    enum ErrorType {
        BUFFER_CREATION_ERROR
    }

    void handleError(ErrorType errorType, String cause);
}
