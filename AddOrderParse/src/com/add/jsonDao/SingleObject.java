package com.add.jsonDao;

/**
 * Created by Kuajing on 2017/3/6.
 */
public class SingleObject{

    private Object Result;

    private String Code;

    private String Message;



    public String getMessage() {
        return Message;
    }

    public void setMessage(String message) {
        Message = message;
    }


    public String getCode() {
        return Code;
    }

    public void setCode(String code) {
        Code = code;
    }

    public Object getResult() {
        return Result;
    }

    public void setResult(Object result) {
        Result = result;
    }
}
