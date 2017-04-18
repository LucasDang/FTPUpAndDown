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
import java.util.*;


/**
 * Created by Kuajing on 2017/3/15.
 */
public class ReceiptDeal {
    private final static Log logger = LogFactory.getLog(ReceiptDeal.class);

    //每次下载的限制数量
    private final static int limitDownloadNum = 200;

    private static ResourceBundle resource = FtpUtils.getFtpResource();
    private static  String ftpReceiptXmlPath = resource.getString("ftpReceiptXmlPath");
    private static  String localReceiptXmlBackUpPath = resource.getString("localReceiptXmlBackUpPath");
    private static  String localReceiptXmlTempPath = resource.getString("localReceiptXmlTempPath");

    //防止地址被编码一次之后又再次编码
    private static int i = 0;

    public void receiptXmlDownload(){
        if (i==0) {
            try {
                ftpReceiptXmlPath = new String(ftpReceiptXmlPath.getBytes("ISO-8859-1"), "UTF-8");
                localReceiptXmlBackUpPath = new String(localReceiptXmlBackUpPath.getBytes("ISO-8859-1"), "UTF-8");
                localReceiptXmlTempPath = new String(localReceiptXmlTempPath.getBytes("ISO-8859-1"), "UTF-8");
                i++;
            } catch (UnsupportedEncodingException e) {
                logger.error("下载回执所设置的路径错误");
                e.printStackTrace();
            }
        }

        /**
         * 检查临时文件夹
         */
        checkTemp();


        /**
         * 获取文件名列表
         */
        FTPClient ftpClient = FtpUtils.getFTPClient();
        if (!ftpClient.isConnected()){
            logger.error("连接ftp失败");
            return ;
        }

        String[] fileNames = FtpUtils.getFileNameList(ftpReceiptXmlPath,ftpClient);
        

        if (fileNames != null && fileNames.length > 0) {
            String firstStr=fileNames[0].substring(fileNames[0].lastIndexOf(".")+1);
            if (fileNames.length == 1){
                if (!firstStr.equals("xml")){
                    logger.info("ftp上面只有一个隐藏文件了" + new Date());

                    return ;
                }
            }

            //将数组倒叙处理
            Collections.reverse(Arrays.asList(fileNames));


            int downloadNum = fileNames.length<limitDownloadNum?fileNames.length:limitDownloadNum;

            logger.info("ftp上有回执文件，个数为===" + fileNames.length + "===下载其中最后的===" + downloadNum + "===个===" + new Date());

            for (int i = 0; i < downloadNum ; i++) {

                //如果文件不是xml格式的就不下载
                String str = fileNames[i].substring(fileNames[i].lastIndexOf(".")+1);

                String realName = fileNames[i].substring(fileNames[i].lastIndexOf("/")+1);

                if(str.equals("xml")) {
                    /**
                     * 下载操作
                     **/
                    FtpUtils.downloadFtpFile(ftpReceiptXmlPath, localReceiptXmlTempPath , realName, ftpClient);
                }

            }
            //关闭连接
            FtpUtils.closeFtpClient(ftpClient);

            logger.info("下载了===" + downloadNum + "===个回执完成===" + new Date());

            //进行解析
            receiptXmlParse(fileNames);
        }


    }


    public void receiptXmlParse(String[] fileNames){

        /**
         * 获取ftp连接对文件进行删除
         */
        FTPClient ftpClient = FtpUtils.getFTPClient();
        if (!ftpClient.isConnected()){
            System.out.println("连接ftp失败");
            logger.error("连接ftp失败");
            return;
        }

        List<Receipt> receipts = new ArrayList<>();


        /**
         * 根据当前时间创建文件夹并且将插入数据库之后的回执文件转移到备份中
         */
        Date currentDay = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
        String dateString = formatter.format(currentDay);
        FileUtils.newFolder(localReceiptXmlBackUpPath+dateString);


        int downloadNum = fileNames.length<limitDownloadNum?fileNames.length:limitDownloadNum;
        logger.info("开始解析===" + downloadNum + "===个回执文件===" + new Date());

        for (int i=0;i<downloadNum;i++) {
            String realName=fileNames[i].substring(fileNames[i].lastIndexOf("/")+1);
            //如果文件不是xml格式的就不下载不解析

            String str=fileNames[i].substring(fileNames[i].lastIndexOf(".")+1);

            //Order.201703210310433.Ciq.1.EB_add.DXPENT0000013698.20170324180836.146.back

            if(str.equals("xml")){
            //删除ftp上已下载的文件
            FtpUtils.deleteFileFtp(ftpReceiptXmlPath,realName,ftpClient);

                SAXReader reader = new SAXReader();
                Document doc = null;
                try {
                    doc = reader.read(new File(localReceiptXmlTempPath  +  realName));
                } catch (DocumentException e) {
                    e.printStackTrace();
                }


                Map map = XmlUtils.Dom2Map(doc);
                Map orderStatus = (Map) map.get("OrderReturn");

                Receipt receipt = CommonUtils.toBean(orderStatus, Receipt.class);
                receipt.setUploadTime(new Date());

                receipts.add(receipt);


            }
        }

        if(receipts.size() > 0) {

            logger.info("完成解析===" + receipts.size() + "===个回执文件===" + new Date());

            ReceiptMapperImp receiptMapperImp = new ReceiptMapperImp();
            receiptMapperImp.add(receipts);

            logger.info("插入完成===" + receipts.size() + "===个回执文件===" + new Date());

        }


        for (int i=0;i<(fileNames.length<limitDownloadNum?fileNames.length:limitDownloadNum);i++) {
            String realName = fileNames[i].substring(fileNames[i].lastIndexOf("/") + 1);
            //如果文件不是xml格式的就不下载不解析
            String str = fileNames[i].substring(fileNames[i].lastIndexOf(".") + 1);
            if (str.equals("xml")) {
                //解析插入数据库之后则将临时存放的回执文件转移到备份里面，并且按照时间来存放到不同的文件夹里面
                FileUtils.moveFile(localReceiptXmlTempPath + realName, localReceiptXmlBackUpPath + dateString + "/" + realName);
            }
        }

        logger.info("移动temp文件完成===" + new Date());

        FtpUtils.closeFtpClient(ftpClient);
    }


    private void checkTemp(){
        /**
         * 先获取临时文件里面的文件，如果存在文件就先解析这些文件存到数据库之中再下载
         */
        String[] beforeFiles = FileUtils.getFileNames(localReceiptXmlTempPath);
        if (beforeFiles != null && beforeFiles.length > 0){
            String firstStr=beforeFiles[0].substring(beforeFiles[0].lastIndexOf(".")+1);
            if (beforeFiles.length == 1 && !firstStr.equals("xml")){
                    System.out.println("该文件下只存在一个隐藏文件");
                    return;
            }else {
                //String str=beforeFiles[1].substring(beforeFiles[1].lastIndexOf(".")+1);
                //if (str.equals("xml")) {
                //进行解析
                    logger.info("temp文件夹有文件，个数为===" + beforeFiles.length + "===解析其中最后的===" + limitDownloadNum + "===个===" + new Date());
                    //进行解析
                    receiptXmlParse(beforeFiles);

                    /**
                     * 由于设置了每次只解析固定的文件，所以如果temp文件里面超出了，剩下的就没法解析，所以需要一直解析
                     * 将temp文件夹里的文件解析完再下载
                     */
                    checkTemp();
                //}
            }
        }

    }

}
