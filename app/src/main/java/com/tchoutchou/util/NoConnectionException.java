package com.tchoutchou.util;

public class NoConnectionException extends Exception {

    public NoConnectionException(String s) {
        super(s);
    }

    public NoConnectionException() { super(); }
}
