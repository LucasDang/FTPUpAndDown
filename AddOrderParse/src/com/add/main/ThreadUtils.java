package com.add.main;

import java.util.Date;
import java.util.Timer;

/**
 * Created by Kuajing on 2017/3/17.
 */
public class ThreadUtils implements Runnable{


    public String task;
    public boolean ifExit;

    public ThreadUtils(String task, boolean ifExit){
        this.task=task;
        this.ifExit = ifExit;
    }

    @Override
    public void run() {
        if (!ifExit){
            //每个线程都开启定时循环
            TimerTaskUtils timerTaskUtils = new TimerTaskUtils(task);
            Timer timer = new Timer();
            timer.schedule(timerTaskUtils, new Date(), 60000);
        }
    }
}
