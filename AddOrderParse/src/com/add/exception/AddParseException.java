package com.add.exception;

/**
 * Created by Kuajing on 2017/4/6.
 */
public class AddParseException extends RuntimeException {

    private String errorCode;

    private String errorMsg;

    public static AddParseException addParseException;

    public AddParseException(String errorCode,String errorMsg){
        super();
        this.errorCode = errorCode;
        this.errorMsg = errorMsg;
    }


    public static AddParseException getAddParseException(String errorCode,String errorMsg){
        if (addParseException == null){
            synchronized (AddParseException.class){
                addParseException = new AddParseException(errorCode,errorMsg);
            }
        }
            return addParseException;

    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }
}
