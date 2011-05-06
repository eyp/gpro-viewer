package com.elpaso.android.gpro.exceptions;

/**
 * Exception for parsing errors.
 * 
 * @author eduardo.yanez
 */
public class ParseException extends Exception {
    private static final long serialVersionUID = 8982703759193368838L;

    public ParseException() {
        super();
    }

    public ParseException(String detailMessage, Throwable throwable) {
        super(detailMessage, throwable);
    }

    public ParseException(String detailMessage) {
        super(detailMessage);
    }

    public ParseException(Throwable throwable) {
        super(throwable);
    }
}
