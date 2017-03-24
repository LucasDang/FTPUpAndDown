package com.add.servlet;

import com.add.main.ThreadUtils;

import java.io.IOException;

/**
 * Created by Kuajing on 2017/3/23.
 *
 * 程序入口：
 *  1.启动和停止上传订单文件功能
 *  2.启动和停止下载回执文件工功能
 *  3.查看当前本地未上传订单文件、ftp上已上传订单文件、本地备份的订单文件、需要支持删除操作，预览操作待以后
 *  4.查看当前本地的回执文件、ftp上未下载的回执文件、需要支持删除操作，预览操作待以后
 *  5.订单回执文件的一系列操作
 */
public class RootServlet extends javax.servlet.http.HttpServlet {
    protected void doPost(javax.servlet.http.HttpServletRequest request, javax.servlet.http.HttpServletResponse response) throws javax.servlet.ServletException, IOException {





    }

    protected void doGet(javax.servlet.http.HttpServletRequest request, javax.servlet.http.HttpServletResponse response) throws javax.servlet.ServletException, IOException {
        doPost(request,response);
    }


    private void startUpLoadOrderXml(){
        ThreadUtils orderThread = new ThreadUtils("orderXmlUpload",false);
        Thread order = new Thread(orderThread);
        order.start();
    }

    private void stopUpLoadOrderXml(){
        ThreadUtils orderThread = new ThreadUtils("orderXmlUpload",true);
        Thread order = new Thread(orderThread);
        order.start();
    }

    private void startDownloadReceiptXml(){
        ThreadUtils receiptThread = new ThreadUtils("receiptXmlDownload",false);
        Thread receipt = new Thread(receiptThread);
        receipt.start();
    }

    private void stopDownloadReceiptXml(){
        ThreadUtils receiptThread = new ThreadUtils("receiptXmlDownload",true);
        Thread receipt = new Thread(receiptThread);
        receipt.start();
    }



}
