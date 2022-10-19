package com.codevui.realworldapp.exception;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.codevui.realworldapp.exception.custom.CustomBadRequestException;
import com.codevui.realworldapp.exception.custom.CustomNotFoundException;
import com.codevui.realworldapp.model.user.CustomError;
//khi Controller throw Exception thi se nhay vao day
@RestControllerAdvice
public class APIExceptionHandler {
    @ExceptionHandler(CustomBadRequestException.class)// khi throw ra badrequestex thi se nhay vao day
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)

    public Map<String,CustomError> badRequestException(CustomBadRequestException ex){
        return ex.getErrors();
    }

    @ExceptionHandler(CustomNotFoundException.class)// khi throw ra badrequestex thi se nhay vao day
    @ResponseStatus(value = HttpStatus.NOT_FOUND)

    public Map<String,CustomError> notFoundException(CustomNotFoundException ex){
        return ex.getErrors();
    }
}
