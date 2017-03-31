package com.add.servlet;

import com.add.main.ThreadUtils;
import com.add.mapperImp.OrderMapperImp;

import java.io.IOException;
import java.util.List;

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
        String action = request.getParameter("action");


        if (action.equals("upload") || action.equals("download")) {
            //开关任务
            String isOpen = request.getParameter("isOpen");
            if (isOpen.equals("1")) {
                startTimerTask(action);
            } else {
                stopTimerTask(action);
            }
        }else if (action.equals("batchNos")){
            //获取列表
            OrderMapperImp orderMapperImp = new OrderMapperImp();
            List<String> batchNos = orderMapperImp.getBatchNos();
            System.out.println(batchNos);

        }




    }

    protected void doGet(javax.servlet.http.HttpServletRequest request, javax.servlet.http.HttpServletResponse response) throws javax.servlet.ServletException, IOException {
        doPost(request,response);
    }


    /**
     * 开启任务
     * @param task 任务对应标识
     */
    private void startTimerTask(String task){

        ThreadUtils orderThread = ThreadUtils.getThreadUtils(task);
        /**
         * 如果close为true则说明线程被暂停过，则修改值之后直接运行即可。
         * 这里虽然启动第一个是在主线程运行，但是第二个就会自动在分线程运行的
         */
        orderThread.setClose(false);
        orderThread.run();


    }

    /**
     * 结束任务
     * @param task 任务对应标识
     */
    private void stopTimerTask(String task){
        /**
         * 这里直接设置一下中断标识，因为是while循环
         * 那么任务结束当前循环之后就会自动跳出
         */
        ThreadUtils orderThread = ThreadUtils.getThreadUtils(task);
        orderThread.cancelTask();
    }




}
