package com.add.servlet;

import com.add.exceptionHandle.ReturnCode;
import com.add.exceptionHandle.ServiceResult;
import com.add.jsonDao.AbstractJsonObject;
import com.add.main.ThreadUtils;
import com.add.utils.FileUtils;
import com.add.utils.FtpUtils;
import com.add.utils.JackJsonUtil;
import com.add.utils.ResponseUtil;
import org.apache.commons.net.ftp.FTPClient;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by Kuajing on 2017/4/13.
 */

public class FtpHandleServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        String action = request.getParameter("action");

        String task = request.getParameter("task");

        System.out.println("action 为：" + action);

        ServiceResult serviceResult;

        if (task == null){
            serviceResult = new ServiceResult(ReturnCode.VALIDATION_ERROR, "未传task参数");
        }else {
            if (action.equals("status")) {
                //获取任务的状态
                serviceResult = getActionStatus(task);

            } else if (action.equals("handle")) {
                //开关任务
                String isOpen = request.getParameter("isOpen");
                if (isOpen == null) {
                    serviceResult = new ServiceResult(ReturnCode.VALIDATION_ERROR, "未传isOpen参数");
                } else {
                    if (isOpen.equals("1")) {
                        serviceResult = startTimerTask(task);
                    } else if (isOpen.equals("0")) {
                        serviceResult = stopTimerTask(task);
                    } else {
                        serviceResult = new ServiceResult(ReturnCode.VALIDATION_ERROR, "isOpen参数错误");
                    }
                }
            } else if(action.equals("fileNames")){
                serviceResult = getFileNames(task);
            } else {
                serviceResult = new ServiceResult(ReturnCode.VALIDATION_ERROR, "action参数无效");
            }
        }

        AbstractJsonObject base = new AbstractJsonObject("200","操作成功",serviceResult);
        String responseText = JackJsonUtil.toJson(base);
        System.out.println("json数据" + responseText);
        ResponseUtil.renderJson(response, responseText);


    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request,response);
    }

    /**
     * 获取任务对应的状态，返回值code为0代表关闭，为1代表开启状态
     * @param task
     */
    private ServiceResult getActionStatus(String task){
        ServiceResult serviceResult;

        String[] tasks;
        Map<String,String> openStatus = new HashMap<>();
        if (task.contains(",")){
            tasks = task.split(",");
        }else {
            tasks = new String[]{task};
        }


        for (String singleTask:tasks){
            getSingleStatus(singleTask,openStatus);
        }

        serviceResult = new ServiceResult(ReturnCode.SUCCESS,"成功获取状态",openStatus);
        return  serviceResult;
    }

    private void getSingleStatus(String task ,Map openStatus){
        ThreadUtils orderThread = ThreadUtils.getThreadUtils(task);
        System.out.println("当前任务：" + task + "是否处于关闭状态:" + orderThread.close());

        if (orderThread.close()){
            //当前为关闭状态
            openStatus.put(task,"0");
        }else {
            openStatus.put(task,"1");
        }
    }

    /**
     * 开启任务
     * @param task 任务对应标识
     */
    private ServiceResult startTimerTask(String task){

        ServiceResult serviceResult;

        ThreadUtils orderThread = ThreadUtils.getThreadUtils(task);
        /**
         * 如果close为true则说明线程被暂停过，则修改值之后直接运行即可。
         * 这里虽然启动第一个是在主线程运行，但是第二个就会自动在分线程运行的
         */
        orderThread.setClose(false);
        orderThread.start();
        serviceResult = new ServiceResult(ReturnCode.SUCCESS,task + "任务成功开启");
        return serviceResult;
    }

    /**
     * 结束任务
     * @param task 任务对应标识
     */
    private ServiceResult stopTimerTask(String task){
        ServiceResult serviceResult;

        /**
         * 这里直接设置一下中断标识，因为是while循环
         * 那么任务结束当前循环之后就会自动跳出
         */
        ThreadUtils orderThread = ThreadUtils.getThreadUtils(task);
        orderThread.cancelTask();

        serviceResult = new ServiceResult(ReturnCode.SUCCESS,task + "任务成功关闭");
        return serviceResult;
    }

    /**
     * 获取每个任务对应的文件夹里的文件列表名
     * @param task
     * @return
     */
    private ServiceResult getFileNames(String task){
        ServiceResult serviceResult;

        Map<String,Map> resultMap = new HashMap<>();

        FTPClient ftpClient = FtpUtils.getFTPClient();

        ResourceBundle resource = FtpUtils.getFtpResource();
        String localOrderXmlPath;
        String ftpOrderXmlPath;
        String ftpReceiptXmlPath;
        String localReceiptXmlBackUpPath;
        try {
            localOrderXmlPath = new String(resource.getString("localOrderXmlPath").getBytes("ISO-8859-1"), "UTF-8");
            ftpOrderXmlPath = new String(resource.getString("ftpOrderXmlPath").getBytes("ISO-8859-1"), "UTF-8");;

            ftpReceiptXmlPath = new String(resource.getString("ftpReceiptXmlPath").getBytes("ISO-8859-1"), "UTF-8");;
            localReceiptXmlBackUpPath = new String(resource.getString("localReceiptXmlBackUpPath").getBytes("ISO-8859-1"), "UTF-8");;
        } catch (UnsupportedEncodingException e) {
            serviceResult = new ServiceResult(ReturnCode.FAILURE,"对ftp路径进行编码时出错");
            return serviceResult;
        }


        if (task.equals("upload")) {
            resultMap = getUploadFileNames(resultMap,ftpClient,localOrderXmlPath,ftpOrderXmlPath);
        }else if(task.equals("download")){

            resultMap = getDownloadFileNames(resultMap,ftpClient,localReceiptXmlBackUpPath,ftpReceiptXmlPath);

        }else if(task.equals("upload,download") || task.equals("download,upload")){
            resultMap = getUploadFileNames(resultMap,ftpClient,localOrderXmlPath,ftpOrderXmlPath);
            resultMap = getDownloadFileNames(resultMap,ftpClient,localReceiptXmlBackUpPath,ftpReceiptXmlPath);
        }else {
            serviceResult = new ServiceResult(ReturnCode.FAILURE, "task参数错误");
            return serviceResult;
        }

        serviceResult = new ServiceResult(ReturnCode.SUCCESS,"请求成功",resultMap);

        return serviceResult;
    }


    private Map<String,Map> getUploadFileNames(Map<String,Map> resultMap,FTPClient ftpClient,String localOrderXmlPath,String ftpOrderXmlPath){

        Map<String,Object> localOrderMap = new HashMap();
        Map<String,Object> ftpOrderMap = new HashMap();

        Date currentDay = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
        String dateString = formatter.format(currentDay);

        String description;
        int code;
        String[] fileNames = null;

        boolean isExist = FileUtils.isFolderExist(localOrderXmlPath + dateString);
        if (isExist){
            fileNames = FileUtils.getFileNames(localOrderXmlPath + dateString);
            if (fileNames.length > 0) {

                code = ReturnCode.SUCCESS;
                description =  "获取本地订单路径下文件列表成功";

            }else {
                code = ReturnCode.WARNING;
                description =  dateString + "目录下暂无文件";
            }
        }else {
            code = ReturnCode.NOT_FOUND_OBJECT;
            description =  "本地上传订单路径还未创建今天的文件夹";
        }

        localOrderMap.put("code",code);
        localOrderMap.put("description",description);
        localOrderMap.put("fileNames",fileNames);


        fileNames = FtpUtils.getFileNameList(ftpOrderXmlPath,ftpClient);
        if (fileNames.length > 0) {

            code = ReturnCode.SUCCESS;
            description =  "获取ftp订单路径下文件列表成功";
        }else {
            code = ReturnCode.WARNING;
            description =   "ftp订单路径下目录下暂无文件";
        }
        ftpOrderMap.put("code",code);
        ftpOrderMap.put("description",description);
        ftpOrderMap.put("fileNames",fileNames);


        resultMap.put("localOrder",localOrderMap);
        resultMap.put("ftpOrder",ftpOrderMap);

        return resultMap;
    }

    private Map<String,Map> getDownloadFileNames(Map<String,Map> resultMap,FTPClient ftpClient,String localReceiptXmlPath,String ftpReceiptXmlPath){

        Map<String,Object> localReceiptMap = new HashMap();
        Map<String,Object> ftpReceiptMap = new HashMap();

        Date currentDay = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
        String dateString = formatter.format(currentDay);

        String description;
        int code;
        String[] fileNames = null;

        boolean isExist = FileUtils.isFolderExist(localReceiptXmlPath + dateString);
        if (isExist){
            fileNames = FileUtils.getFileNames(localReceiptXmlPath + dateString);
            if (fileNames.length > 0) {

                code = ReturnCode.SUCCESS;
                description =  "获取本地回执路径下文件列表成功";

            }else {
                code = ReturnCode.WARNING;
                description =  dateString + "目录下暂无文件";
            }
        }else {
            code = ReturnCode.NOT_FOUND_OBJECT;
            description =  "本地回执路径还未创建今天的文件夹";
        }

        localReceiptMap.put("code",code);
        localReceiptMap.put("description",description);
        localReceiptMap.put("fileNames",fileNames);


        fileNames = FtpUtils.getFileNameList(ftpReceiptXmlPath,ftpClient);
        if (fileNames.length > 0) {

            code = ReturnCode.SUCCESS;
            description =  "获取ftp订单路径下文件列表成功";
        }else {
            code = ReturnCode.WARNING;
            description =   "ftp订单路径下目录下暂无文件";
        }
        ftpReceiptMap.put("code",code);
        ftpReceiptMap.put("description",description);
        ftpReceiptMap.put("fileNames",fileNames);

        resultMap.put("localReceipt",localReceiptMap);
        resultMap.put("ftpReceipt",ftpReceiptMap);

        return resultMap;

    }


}
