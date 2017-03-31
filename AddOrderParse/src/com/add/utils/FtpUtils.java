package com.add.utils;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;

import java.io.*;
import java.net.SocketException;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;


/**
 * Created by Kuajing on 2017/3/15.
 */



public class FtpUtils {


    private static ResourceBundle resource = getFtpResource();

    private static final String ftpHost = resource.getString("ftpHost");
    private static final int ftpPort = Integer.parseInt(resource.getString("ftpPort"));
    private static final String ftpUserName = resource.getString("ftpUserName");
    private static final String ftpPassword = resource.getString("ftpPassword");


    private final static Log logger = LogFactory.getLog(FtpUtils.class);

    /**
     * 获取ftp配置文件
     *
     * @return
     */

    public static ResourceBundle getFtpResource(){
        //String proFilePath = System.getProperty("user.dir") +"/ftpConfig/ftp.properties";
        String proFilePath = "/Users/kuajing/Desktop/FTPUpAndDown/ftpConfig/ftp.properties";
        //System.out.println(proFilePath);
        BufferedInputStream inputStream;
        ResourceBundle rs = null;

        try {
            inputStream = new BufferedInputStream(new FileInputStream(proFilePath));
            rs = new PropertyResourceBundle(inputStream);
            inputStream.close();
        } catch (FileNotFoundException e) {
            logger.error("没有找到ftp配置文件，请确认ftp配置文件路径正确。");
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            logger.error("读取ftp配置文件发生错误。");
            e.printStackTrace();
        }

        return rs;
    }



    /**
     * 获取FTPClient对象
     *
     * @return
     */
    public static FTPClient getFTPClient() {
        FTPClient ftpClient = null;
        try {
            //System.out.println("host:"+ftpHost+","+"port:"+ftpPort+","+"name:"+ftpUserName+","+"pwd:"+ftpPassword);
            ftpClient = new FTPClient();
            ftpClient.connect(ftpHost, ftpPort);// 连接FTP服务器
            ftpClient.login(ftpUserName, ftpPassword);// 登陆FTP服务器
            ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
            ftpClient.setControlEncoding("utf-8");


            if (!FTPReply.isPositiveCompletion(ftpClient.getReplyCode())) {
                logger.error("未连接到FTP，用户名或密码错误。");
                ftpClient.disconnect();
            } else {
                //连接成功
                //System.out.println("连接ftp成功1");
            }
        } catch (SocketException e) {
            //System.out.println("222");
            e.printStackTrace();
            logger.error("FTP的IP地址可能错误，请正确配置。");
        } catch (IOException e) {
            e.printStackTrace();
            logger.error("FTP的端口错误,请正确配置。");
        }
        return ftpClient;
    }

    /**
    * 关闭连接
    * */

    public static synchronized void  closeFtpClient(FTPClient ftpClient){

            if (ftpClient!=null && ftpClient.isConnected()) {
                try {
                    ftpClient.logout();
                    ftpClient.disconnect();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
    }

    /**
     * 从FTP服务器下载文件
     * @param ftpPath FTP服务器中文件所在路径 格式： ftptest/aa
     *
     * @param localPath 下载到本地的位置 格式：H:/download
     *
     * @param fileName 文件名称
     */
    public static void downloadFtpFile(String ftpPath, String localPath,
                                       String fileName,FTPClient ftpClient) {
        try {
            ftpClient.setControlEncoding("GB2312");

            //ftpClient.enterLocalPassiveMode();//设置为主动模式
            ftpClient.changeWorkingDirectory(ftpPath);

            File localFile = new File(localPath + "/"+  fileName);

            OutputStream os = new FileOutputStream(localFile);
            fileName = ftpPath + fileName;
            //System.out.println("ftp文件路径："+ fileName);
            ftpClient.retrieveFile(new String(fileName.getBytes("GB2312"),"ISO-8859-1"), os);
            os.flush();
            os.close();

        } catch (FileNotFoundException e) {
            logger.error("没有找到" + localPath + "文件");
        } catch (SocketException e) {
            logger.error("连接FTP失败."+e.toString());
        } catch (IOException e) {
            logger.error("文件读取错误。");
        }

    }


    /**
     * Description: 向FTP服务器上传文件
     * @Version1.0
     * @param path FTP服务器保存目录
     * @param filename 上传到FTP服务器上的文件名
     * @param input 输入流
     * @return 成功返回true，否则返回false
     */
    public static boolean uploadFile(
            String path, //FTP服务器保存目录
            String filename, //上传到FTP服务器上的文件名
            InputStream input, // 输入流
            FTPClient ftpClient
    ) {
        boolean success = false;
        //ftpClient = getFTPClient();
        try {
            int reply;

            reply = ftpClient.getReplyCode();
            if (!FTPReply.isPositiveCompletion(reply)) {
                //ftpClient.disconnect();
                return success;
            }
            ftpClient.changeWorkingDirectory(path);
            ftpClient.storeFile(filename, input);

            input.close();
            //ftpClient.logout();
            success = true;
            System.out.println("成功上传文件："+filename);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return success;
    }


    // 删除文件至FTP通用方法
    public static void deleteFileFtp(String filePath,String fileName,FTPClient ftpClient){
        try {
           ftpClient.deleteFile(filePath+fileName);
        } catch (Exception e) {
            logger.error("删除文件失败！");
        }

    }


    /**
     * 返回FTP目录下的文件列表
     *
     * @param ftpDirectory
     * @return
     */
    public static String[] getFileNameList(String ftpDirectory,FTPClient ftpClient) {
        String[] list = null;
        try {
            list = ftpClient.listNames(ftpDirectory);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return list;
    }


    /***
     * @上传文件夹
     * @param localDirectory
     *   当地文件夹
     * @param remoteDirectoryPath
     *   Ftp 服务器路径 以目录"/"结束
     * */
    public static boolean uploadDirectory(String localDirectory,
                                   String remoteDirectoryPath,FTPClient ftpClient) {
        File src = new File(localDirectory);
        //ftpClient = getFTPClient();
        try {
            remoteDirectoryPath = remoteDirectoryPath + src.getName() + "/";
            boolean makeDirFlag = ftpClient.makeDirectory(remoteDirectoryPath);
            System.out.println("localDirectory : " + localDirectory);
            System.out.println("remoteDirectoryPath : " + remoteDirectoryPath);
            System.out.println("src.getName() : " + src.getName());
            System.out.println("remoteDirectoryPath : " + remoteDirectoryPath);
            System.out.println("makeDirFlag : " + makeDirFlag);

        }catch (IOException e) {
            e.printStackTrace();
            logger.error(remoteDirectoryPath + "目录创建失败");
        }
        File[] allFile = src.listFiles();
        for (int currentFile = 0;currentFile < allFile.length;currentFile++) {
            if (!allFile[currentFile].isDirectory()) {
                String srcName = allFile[currentFile].getPath().toString();
                try {
                    FileInputStream in = new FileInputStream(new File(srcName));
                    uploadFile(srcName,remoteDirectoryPath,in,ftpClient);
                } catch (FileNotFoundException e) {
                    logger.error("创建文件出口有问题");
                    e.printStackTrace();
                }
            }
        }
        for (int currentFile = 0;currentFile < allFile.length;currentFile++) {
            if (allFile[currentFile].isDirectory()) {
                // 递归
                uploadDirectory(allFile[currentFile].getPath().toString(),
                        remoteDirectoryPath,ftpClient);
            }
        }
        return true;
    }
    /***
     * @下载文件夹
     * @param localDirectoryPath 本地地址
     * @param remoteDirectory 远程文件夹
     * */
    public static boolean downLoadDirectory(String localDirectoryPath,String remoteDirectory,FTPClient ftpClient) {

        try {
            String fileName = new File(remoteDirectory).getName();
            localDirectoryPath = localDirectoryPath + fileName + "//";
            new File(localDirectoryPath).mkdirs();
            FTPFile[] allFile = ftpClient.listFiles(remoteDirectory);
            for (int currentFile = 0;currentFile < allFile.length;currentFile++) {
                if (!allFile[currentFile].isDirectory()) {
                    downloadFtpFile(allFile[currentFile].getName(),localDirectoryPath, remoteDirectory,ftpClient);
                }
            }
            for (int currentFile = 0;currentFile < allFile.length;currentFile++) {
                if (allFile[currentFile].isDirectory()) {
                    String strremoteDirectoryPath = remoteDirectory + "/"+ allFile[currentFile].getName();
                    downLoadDirectory(localDirectoryPath,strremoteDirectoryPath,ftpClient);
                }
            }
        }catch (IOException e) {
            e.printStackTrace();
            System.out.println("下载文件夹失败");
            logger.info("下载文件夹失败");
            return false;
        }
        return true;
    }



}