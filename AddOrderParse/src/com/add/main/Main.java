package com.add.main;

/**
 * Created by Kuajing on 2017/3/20.
 */
public class Main {

    public static void main(String[] args) {

        //开启两个线程，用来分别执行上传和下载的操作。
        ThreadUtils orderThread = new ThreadUtils("orderXmlUpload",false);

        ThreadUtils receiptThread = new ThreadUtils("receiptXmlDownload",false);

        Thread order = new Thread(orderThread);

        Thread receipt = new Thread(receiptThread);

        order.start();

        receipt.start();

    }
}
