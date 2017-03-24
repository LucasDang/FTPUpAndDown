package com.add.mapper;

import com.add.bean.Receipt;

/**
 * Created by Kuajing on 2017/3/15.
 */
public interface ReceiptMapper {

    Receipt checkIsExist(String orderNo);

    void add(Receipt receipt);

    void update(Receipt receipt);


}
