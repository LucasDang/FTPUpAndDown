package com.add.jsonDao;

/**
 * Created by Kuajing on 2017/3/6.
 */
public class AbstractJsonObject {

    private int code;

    private String msg;

    private Object result;


    public AbstractJsonObject(int code,String msg,Object result){
        super();
        this.code = code;
        this.msg = msg;
        this.result = result;

    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Object getResult() {
        return result;
    }

    public void setResult(Object result) {
        this.result = result;
    }
}
