package com.add.main;

import com.add.service.OrderInfoDeal;
import com.add.service.ReceiptDeal;
import com.add.utils.FtpUtils;

import java.util.Date;
import java.util.TimerTask;

/**
 * Created by Kuajing on 2017/3/16.
 */
public class TimerTaskUtils extends TimerTask{


    public String task;

    public TimerTaskUtils(String task){
        this.task=task;
    }

    @Override
    public void run() {
        if (task.equals("orderXmlUpload")){
            //订单xml上传
            System.out.println("查找订单文件" + new Date().toString());
            OrderInfoDeal.orderXmlUpload();
            FtpUtils.closeFtpClient(FtpUtils.getFTPClient());
        }else if(task.equals("receiptXmlDownload")){
            //回执xml下载
            System.out.println("查找回执文件" + new Date().toString());
            ReceiptDeal receiptDeal = new ReceiptDeal();
            receiptDeal.receiptXmlDownload();
            FtpUtils.closeFtpClient(FtpUtils.getFTPClient());
        }
    }
}
