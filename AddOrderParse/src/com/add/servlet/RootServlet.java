package com.add.servlet;

import com.add.bean.Receipt;
import com.add.exceptionHandle.ReturnCode;
import com.add.exceptionHandle.ServiceResult;
import com.add.jsonDao.AbstractJsonObject;
import com.add.mapper.ReceiptMapper;
import com.add.mapperImp.ReceiptMapperImp;
import com.add.utils.JackJsonUtil;
import com.add.utils.ResponseUtil;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
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

        System.out.println("action 为：" + action);

        ServiceResult serviceResult = null;

         if (action.equals("batchNos")){
            //获取批次列表
            serviceResult = getBatchNos(request);

        }else if (action.equals("receipts")){
            //根据条件获取回执信息列表
            serviceResult = getReceipts(request);
        }else if (action.equals("receiptDetail")){
            //获取回执详细信息
            String orderNo = request.getParameter("orderNo");
            if (orderNo != null) {
                serviceResult = getReceiptDetail(orderNo);
            }else {
                serviceResult = new ServiceResult(ReturnCode.VALIDATION_ERROR,"没有传订单号");
            }
        }else {
            serviceResult = new ServiceResult(ReturnCode.VALIDATION_ERROR,"action参数无效");
        }

        AbstractJsonObject base = new AbstractJsonObject("200","操作成功",serviceResult);
        String responseText = JackJsonUtil.toJson(base);
        System.out.println("json数据" + responseText);
        ResponseUtil.renderJson(response, responseText);

    }

    protected void doGet(javax.servlet.http.HttpServletRequest request, javax.servlet.http.HttpServletResponse response) throws javax.servlet.ServletException, IOException {
        doPost(request,response);
    }


    /**
     * 获取批次号,分为模糊查询和根据需要查询批次号
     */
    private ServiceResult getBatchNos(javax.servlet.http.HttpServletRequest request){
        String fuzzyBatchNo = request.getParameter("fuzzyBatchNo");
        //根据pageSize获取最近的几个批次
        String pageSize =  request.getParameter("pageSize")==null?"8":request.getParameter("pageSize");

        ServiceResult serviceResult;
        String[] batchNos;

        if (fuzzyBatchNo!=null) {
            ReceiptMapper receiptMapper = new ReceiptMapperImp();
            batchNos = receiptMapper.getBatchNosByFuzzy(fuzzyBatchNo);
        }else {
            ReceiptMapper receiptMapper = new ReceiptMapperImp();
            batchNos = receiptMapper.getBatchNos(pageSize);
        }
        serviceResult = new ServiceResult(ReturnCode.SUCCESS, "成功获取批次号", batchNos);
        return serviceResult;
    }


    /**
     * 获取回执信息列表，多个请求参数
     * @param request
     */
    private ServiceResult getReceipts(javax.servlet.http.HttpServletRequest request){
        ServiceResult serviceResult;

        int pageSize;
        int currentPage;
        try {
            //一页展示的个数,不传默认为12
            pageSize = Integer.parseInt(request.getParameter("pageSize")==null?"20":request.getParameter("pageSize"));
            //当前页数，不传则默认为第一页
            currentPage = Integer.parseInt(request.getParameter("currentPage")==null?"1":request.getParameter("currentPage"));
        }catch (NumberFormatException e){
            serviceResult = new ServiceResult(ReturnCode.VALIDATION_ERROR,"传入的页数有误");
            return serviceResult;
        }
        int startRecord = (currentPage)*pageSize;


        /**
         * 批次号
         */
        String batchNo = request.getParameter("batchNo");


        /**
         * 开始时间 - 结束时间
         * 如果也有传递间隔时间的参数，这里也会覆盖掉
         */
        String startTime = request.getParameter("startTime");
        String endTime = request.getParameter("endTime");

        /**
         * 间隔时间：前多久，这里采用 "个数 ： 单位" 来表示间隔前多久
         * 如 5:m 代表5分钟，1:h代表一小时，最多为d，代表多少天
         */
        String intervalTime = request.getParameter("intervalTime");
        String[] intervalTimes=null;
        if (intervalTime != null) {
            intervalTimes = intervalTime.split(":");
        }
        //选择前多久则表明结束时间为当前时间
        Date endDate = null;
        Date startDate = null;
        Calendar calendar = Calendar.getInstance();


        if (intervalTime != null) {
            endDate = new Date();
            calendar.setTime(endDate);

            if (intervalTimes != null && intervalTimes.length == 2 && intervalTimes[1] != null) {
                //有传递间隔时间这个参数，这里本质还是根据间隔参数来计算开始时间和结束时间
                try {
                    if (intervalTimes[1].equals("m")) {
                        calendar.add(Calendar.MINUTE, -Integer.parseInt(intervalTimes[0]));
                    } else if (intervalTimes[1].equals("h")) {
                        calendar.add(Calendar.HOUR_OF_DAY, -Integer.parseInt(intervalTimes[0]));
                    } else if (intervalTimes[1].equals("d")) {
                        calendar.add(Calendar.DAY_OF_MONTH, -Integer.parseInt(intervalTimes[0]));
                    }
                }catch (NumberFormatException e){
                    serviceResult = new ServiceResult(ReturnCode.VALIDATION_ERROR,"传入的页数有误");
                    return serviceResult;
                }
                startDate = calendar.getTime();
            }  else {
                serviceResult = new ServiceResult(ReturnCode.VALIDATION_ERROR, "传入的间隔参数有误");
                return serviceResult;
            }
        }else if(startTime != null && endDate != null){
                if (startTime != null) {
                    long lt = new Long(endTime);
                    startDate = new Date(lt);
                }
                if (endTime != null) {
                    long lt = new Long(endTime);
                    endDate = new Date(lt);
                }

        }else {
            endDate = new Date();
            calendar.setTime(endDate);
            calendar.add(Calendar.HOUR_OF_DAY, -1);
            startDate =  calendar.getTime();
            endDate = new Date();
        }

        /**
         * 排序字段:按时间排序，按订单号排序，默认为1
         * 1- 按订单号正序
         * 2- 按订单号倒叙
         */
        String sort = request.getParameter("sort") == null?"1":request.getParameter("sort");

        /**
         * 获取订单状态,默认显示失败的回执，为-1
         * 1- 只显示成功回执
         * 0- 成功和失败的全部显示
         */
        String status = request.getParameter("status") == null?"-1":request.getParameter("status");

        ReceiptMapper receiptMapper = new ReceiptMapperImp();
        List<Receipt> receipts;
        String totalRecord;
        if (batchNo == null){
            receipts = receiptMapper.getReceiptsByDate(startDate,endDate,status,sort,startRecord,pageSize);
            totalRecord = receiptMapper.getReceiptsCountByDate(startDate,endDate,status);
        }else {
            //System.out.println("status：" + status + ", sort：" + sort + "。");
            int orderCount = receiptMapper.getOrderCountByBatchNo(batchNo);
            if (orderCount == 0){
                serviceResult = new ServiceResult(ReturnCode.NOT_FOUND_OBJECT,"该批次号不存在");
                return serviceResult;
            }else {
                receipts = receiptMapper.getReceiptsByBatchNo(batchNo, startDate, endDate, status, sort, startRecord, pageSize);
                totalRecord = receiptMapper.getReceiptsCountByBatchNo(batchNo, startDate, endDate, status);
            }
        }

        serviceResult = new ServiceResult(ReturnCode.SUCCESS,"成功获取回执列表",receipts,totalRecord);
        return serviceResult;
    }

    /**
     * 根据订单号查询所有的回执信息
     * @param orderNo
     */
    private ServiceResult getReceiptDetail(String orderNo){
        ServiceResult serviceResult;

        ReceiptMapper receiptMapper = new ReceiptMapperImp();
        List<Receipt> receipts = receiptMapper.getReceiptDetail(orderNo);

        serviceResult = new ServiceResult(ReturnCode.SUCCESS,"成功获取详细回执",receipts);
        return  serviceResult;

    }


}
