package com.code4ro.legalconsultation.common.controller;

import com.code4ro.legalconsultation.common.exceptions.DocumentAlreadyPresentException;
import com.code4ro.legalconsultation.common.exceptions.InvalidDocumentException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class DocumentExceptionHandler extends GlobalExceptionHandler {

    @ExceptionHandler(InvalidDocumentException.class)
    public ResponseEntity<String> handleInvalidDocumentExcepion(InvalidDocumentException e){
        return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(DocumentAlreadyPresentException.class)
    public ResponseEntity<String> handleDocumentAlreadyPresentException(DocumentAlreadyPresentException e){
        return new ResponseEntity<>(e.getMessage(), HttpStatus.ALREADY_REPORTED);
    }
}
