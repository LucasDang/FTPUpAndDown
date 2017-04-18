package com.add.jsonDao;

/**
 * Created by Kuajing on 2017/3/6.
 */
public class AbstractJsonObject {

    private String code;

    private String msg;

    private Object result;


    public AbstractJsonObject(String code,String msg,Object result){
        super();
        this.code = code;
        this.msg = msg;
        this.result = result;

    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
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
