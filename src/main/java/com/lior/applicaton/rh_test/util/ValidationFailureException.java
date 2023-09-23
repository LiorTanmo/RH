package com.lior.applicaton.rh_test.util;

public class ValidationFailureException extends RuntimeException{
    public ValidationFailureException(String msg){
        super(msg);
    }
}
