package com.lior.applicaton.rh_test.util;

public class NotAuthorizedException extends IllegalAccessException{

    public NotAuthorizedException (String msg){
        super(msg);
    }
}
