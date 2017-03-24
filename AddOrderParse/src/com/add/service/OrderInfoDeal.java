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
                System.out.println(localOrderXmlPath + "111");
                i++;
            } catch (UnsupportedEncodingException e) {
                logger.error("上传订单所设置的路径错误");
                e.printStackTrace();
            }
        }


        String[] fileNames = FileUtils.getFileNames(localOrderXmlPath);

        if (fileNames.length>0){
            FTPClient ftpClient = FtpUtils.getFTPClient();
            if (!ftpClient.isConnected()){
                logger.error("连接ftp失败");
                return;
            }

            //存在文件，进行上传
            for (int i=0;i<fileNames.length;i++){

                FileInputStream in = null;
                try {
                    in = new FileInputStream(new File(localOrderXmlPath+fileNames[i]));
                    FtpUtils.uploadFile(ftpOrderXmlPath,fileNames[i],in,ftpClient);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                FileUtils.moveFile(localOrderXmlPath+fileNames[i],localOrderXmlBackUpPath+fileNames[i]);
            }
            FtpUtils.closeFtpClient(ftpClient);

        }
    }

}
