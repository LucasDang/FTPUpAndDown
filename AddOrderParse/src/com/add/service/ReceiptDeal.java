package com.add.service;

import cn.itcast.commons.CommonUtils;
import com.add.bean.Receipt;
import com.add.mapperImp.ReceiptMapperImp;
import com.add.utils.FileUtils;
import com.add.utils.FtpUtils;
import com.add.utils.XmlUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.net.ftp.FTPClient;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.io.SAXReader;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.ResourceBundle;


/**
 * Created by Kuajing on 2017/3/15.
 */
public class ReceiptDeal {
    private final static Log logger = LogFactory.getLog(ReceiptDeal.class);


    private static ResourceBundle resource = FtpUtils.getFtpResource();
    private static  String ftpReceiptXmlPath = resource.getString("ftpReceiptXmlPath");
    private static  String localReceiptXmlBackUpPath = resource.getString("localReceiptXmlBackUpPath");


    private static int i = 0;

    public void receiptXmlDownload() {
        if (i==0) {
            try {
                ftpReceiptXmlPath = new String(ftpReceiptXmlPath.getBytes("ISO-8859-1"), "UTF-8");
                localReceiptXmlBackUpPath = new String(localReceiptXmlBackUpPath.getBytes("ISO-8859-1"), "UTF-8");
                i++;
            } catch (UnsupportedEncodingException e) {
                logger.error("下载回执所设置的路径错误");
                e.printStackTrace();
            }
        }

        /**
         * 获取文件名列表
         */
        FTPClient ftpClient = FtpUtils.getFTPClient();
        if (!ftpClient.isConnected()){
            logger.error("连接ftp失败");
            return;
        }

        String[] fileNames = FtpUtils.getFileNameList(ftpReceiptXmlPath,ftpClient);
        //System.out.println("第一个文件名："+fileNames[0]);
        if (fileNames != null && fileNames.length > 0) {

            //如果存在文件即下载,下载到本地，并且按照时间创建文件夹来存储，方便定时清理
            Date currentDay = new Date();
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
            String dateString = formatter.format(currentDay);
            System.out.println("当前日期："+dateString);
            FileUtils.newFolder(localReceiptXmlBackUpPath + dateString);

            //
            for (int i = 0; i < (fileNames.length > 20?20:fileNames.length) ; i++) {

                //如果文件不是xml格式的就不下载
                String str=fileNames[i].substring(fileNames[i].lastIndexOf(".")+1);
                if(str.equals("xml")) {
                    /**
                     * 下载
                     **/
                    //System.out.println("回执xml文件名" + fileNames[i]);
                    logger.info("下载回执Xml文件-" + fileNames[i]);

                    FtpUtils.downloadFtpFile(ftpReceiptXmlPath, localReceiptXmlBackUpPath + dateString, fileNames[i], ftpClient);
                }

            }
                //关闭连接
                FtpUtils.closeFtpClient(ftpClient);

            //进行解析
            receiptXmlParse(fileNames);
        }

    }


    public void receiptXmlParse(String[] fileNames){
/*
        try {
            ftpReceiptXmlPath = new String(ftpReceiptXmlPath.getBytes("ISO-8859-1"),"UTF-8");
            localReceiptXmlBackUpPath = new String(localReceiptXmlBackUpPath.getBytes("ISO-8859-1"),"UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        */

        FTPClient ftpClient = FtpUtils.getFTPClient();

        Date currentDay = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        String dateString = formatter.format(currentDay);


        for (int i=0;i<(fileNames.length > 20?20:fileNames.length);i++) {
            //删除ftp上的文件
            FtpUtils.deleteFileFtp(ftpReceiptXmlPath, fileNames[i],ftpClient);

            //如果文件不是xml格式的就不下载不解析
            String str=fileNames[i].substring(fileNames[i].lastIndexOf(".")+1);

            if(str.equals("xml")){
                SAXReader reader = new SAXReader();
                Document doc = null;
                try {
                    doc = reader.read(new File(localReceiptXmlBackUpPath + dateString + '/' +  fileNames[i]));
                } catch (DocumentException e) {
                    e.printStackTrace();
                }

                Map map = XmlUtils.Dom2Map(doc);
                Map orderStatus = (Map) map.get("OrderReturn");

                Receipt receipt = CommonUtils.toBean(orderStatus, Receipt.class);
                receipt.setUploadTime(new Date());

                ReceiptMapperImp receiptMapperImp = new ReceiptMapperImp();
                receiptMapperImp.add(receipt);
            }

        }
        FtpUtils.closeFtpClient(ftpClient);
    }


}
