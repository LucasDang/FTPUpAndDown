package com.add.service;

import com.add.utils.FileUtils;
import com.add.utils.FtpUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.net.ftp.FTPClient;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ResourceBundle;

/**
 * Created by Kuajing on 2017/3/15.
 */
public class OrderInfoDeal {

    private final static Log logger = LogFactory.getLog(OrderInfoDeal.class);

    private static ResourceBundle resource = FtpUtils.getFtpResource();

    private static  String localOrderXmlPath =  resource.getString("localOrderXmlPath");
    private static  String localOrderXmlBackUpPath = resource.getString("localOrderXmlBackUpPath");
    private static  String ftpOrderXmlPath = resource.getString("ftpOrderXmlPath");

    private static int i = 0;

    public static void orderXmlUpload(){
        if (i==0) {
            try {
                localOrderXmlPath = new String(localOrderXmlPath.getBytes("ISO-8859-1"), "UTF-8");
                localOrderXmlBackUpPath = new String(localOrderXmlBackUpPath.getBytes("ISO-8859-1"), "UTF-8");
                ftpOrderXmlPath = new String(ftpOrderXmlPath.getBytes("ISO-8859-1"), "UTF-8");
                i++;
            } catch (UnsupportedEncodingException e) {
                logger.error("上传订单所设置的路径错误");
                e.printStackTrace();
            }
        }

        Date currentDay = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
        String dateString = formatter.format(currentDay);
        
        boolean isExist = FileUtils.isFolderExist(localOrderXmlPath + dateString);
        if (isExist){
            String[] fileNames = FileUtils.getFileNames(localOrderXmlPath + dateString);

            if (fileNames.length>0){

                FTPClient ftpClient = FtpUtils.getFTPClient();
                if (!ftpClient.isConnected()){
                    logger.error("上传订单连接ftp失败");
                    return;
                }
                //logger.info("开始上传订单文件===" + fileNames.length + "===个");

                //存在文件，进行上传
                for (int i=0;i<fileNames.length;i++){
                    try {
                        Thread.sleep(3000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }


                    FileInputStream in = null;

                    //如果存在文件即下载,下载到本地，并且按照时间创建文件夹来存储，方便定时清理
                    try {
                        in = new FileInputStream(new File(localOrderXmlPath + dateString + "/" + fileNames[i]));
                        FtpUtils.uploadFile(ftpOrderXmlPath,fileNames[i],in,ftpClient);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                    FileUtils.newFolder(localOrderXmlBackUpPath+dateString);
                    FileUtils.moveFile(localOrderXmlPath + dateString + "/" + fileNames[i],localOrderXmlBackUpPath+dateString + "/"+fileNames[i]);
                }

                logger.info("完成上传订单文件===" + fileNames.length + "===个");

                FtpUtils.closeFtpClient(ftpClient);
            }

        }else {
            logger.info("当前日期：" + dateString + "还未创建上传订单的文件夹");
        }
    }

}
