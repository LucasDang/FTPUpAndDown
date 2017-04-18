package com.add.exceptionHandle;

/**
 * Created by Kuajing on 2017/4/7.
 */

public class ServiceResult{

    private int code;//代码

    private String description;//信息

    private Object result; // 结果

    private String totalRecord;

    /**
     * 一系列构造方法
     */

    public ServiceResult() {
    }

    public ServiceResult(int code) {
        this(code, null);
    }

    public ServiceResult(int code, String description) {
        this(code, description, null,null);
    }


    public ServiceResult(int code, Object result) {
        this(code, null, result,null);
    }

    public ServiceResult(int code,String description, Object result) {
        this(code, description, result,null);
    }

    public ServiceResult(int code, String description, Object result,String totalRecord) {
        this.code = code;
        this.result = result;
        this.description = description;
        this.totalRecord = totalRecord;
    }


    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Object getResult() {
        return result;
    }

    public void setResult(Object result) {
        this.result = result;
    }

    public String getTotalRecord() {
        return totalRecord;
    }

    public void setTotalRecord(String totalRecord) {
        this.totalRecord = totalRecord;
    }
}
