package com.add.bean;

/**
 * Created by Kuajing on 2017/3/7.
 */
public class PageManage {

    /**
     * 总记录个数
     */
    private int totalRecords;

    /**
     * 当前页数
     * 默认为第1页
     */
    private int currentPage = 1;

    /**
     * 查询个数
     * 默认为6
     */
    private int pageSize = 20;


    /**
     * 通过计算得出这个起点值，然后即可以用sql语句（limit 起点，个数）来进行查询
     */
    private int startRecord;

    public int getTotalRecords() {
        return totalRecords;
    }

    public void setTotalRecords(int totalRecords) {
        this.totalRecords = totalRecords;
    }


    public int getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public int getStartRecord() {
        return startRecord;
    }

    public void setStartRecord(int startRecords) {
        this.startRecord = startRecords;
    }
}
