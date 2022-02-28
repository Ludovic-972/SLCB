package com.tchoutchou.util;

public class InexistantUserException extends Exception{

    public InexistantUserException(String s) {
        super(s);
    }

    public InexistantUserException() {
        super();
    }
}
