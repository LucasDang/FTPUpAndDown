package com.add.jsonDao;

import java.util.List;

/**
 * Created by Kuajing on 2017/3/6.
 */
public class ListObjects extends AbstractJsonObject{

    private List<?> objectItems;

    private String currentPage;

    private String totalRecords;

    public List<?> getObjectItems() {
        return objectItems;
    }

    public void setObjectItems(List<?> objectItems) {
        this.objectItems = objectItems;
    }
}
