package com.add.main;

import com.add.service.OrderInfoDeal;
import com.add.service.ReceiptDeal;
import com.add.utils.FtpUtils;

import java.util.Date;

/**
 * Created by Kuajing on 2017/3/17.
 */
public class ThreadUtils extends Thread{


    public String task;

    public boolean close;

    /**
     *
     */
    private static ThreadUtils uploadThread;
    private static ThreadUtils downloadThread;


    /**
     * 构造方法 新建线程，并根据任务命名线程
     * @param task
     */
    public ThreadUtils(String task){
        super(task);
        this.task=task;
    }

    /**
     * 获取线程-根据线程名来获取对象，如果还没构造就先构造并赋值给static对象
     * @param task
     * @return
     */
    public static ThreadUtils getThreadUtils(String task){

        if (task.equals("upload")){
            if (uploadThread == null){
                uploadThread = new ThreadUtils(task);
                uploadThread.setName(task);
            }
            return uploadThread;

        }else {
            if (downloadThread == null) {
                downloadThread = new ThreadUtils(task);
                downloadThread.setName(task);
            }
            return downloadThread;
        }

    }

    /**
     * 停止任务
     */
    public void cancelTask(){
        setClose(true);
    }


    /**
     * 线程方法
     * 根据task来区分两个线程的方法，并且只要没有关闭，就一直执行任务，执行完sleep一段时间再次执行，通过
     * 这种方式达到一个定时器的功能。
     */
    @Override
    public void run() {
        /**
         * 这里使用synchronized关键字来锁住线程，
         * 这样当线程被再次启动的时候就不会一个任务启动多个线程了。
         */

        if (task.equals("upload")){
            synchronized (this) {
                while (!close()) {
                    System.out.println("查找订单文件" + new Date().toString());
                    OrderInfoDeal.orderXmlUpload();
                    FtpUtils.closeFtpClient(FtpUtils.getFTPClient());
                    try {
                        Thread.sleep(60000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        else if (task.equals("download")){
            synchronized (this) {
                while (!close()) {
                    //回执xml下载
                    System.out.println("查找回执文件" + new Date().toString());
                    ReceiptDeal receiptDeal = new ReceiptDeal();
                    receiptDeal.receiptXmlDownload();
                    FtpUtils.closeFtpClient(FtpUtils.getFTPClient());
                    try {
                        Thread.sleep(60000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

    }


    public boolean close() {
        return close;
    }

    public void setClose(boolean close) {
        this.close = close;
    }
}
