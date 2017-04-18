package com.add.jsonDao;

import java.util.List;

/**
 * Created by Kuajing on 2017/3/6.
 */
public class ListObjects{

    private List<?> Result;

    private String currentPage;

    private String totalRecords;

    private String Code;

    private String Message;


    public ListObjects(){
        super();
    }

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

    public String getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(String currentPage) {
        this.currentPage = currentPage;
    }

    public String getTotalRecords() {
        return totalRecords;
    }

    public void setTotalRecords(String totalRecords) {
        this.totalRecords = totalRecords;
    }

    public List<?> getResult() {
        return Result;
    }

    public void setResult(List<?> result) {
        Result = result;
    }
}
