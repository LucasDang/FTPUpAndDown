##FTP文件上传及回执文件下载项目优化

###功能简介：
	操作1：将本地的订单xml文件上传到FTP上，然后在本地其他文件夹进行备
	份并删除源文件。
	操作2：将FTP上面其他公司上传的订单回执Xml下载到temp文件夹（限制数
	量为300，超过300则下个循环再下载。此举防止一个周期时间过长，产生中
	断。） ——> 解析并存储到数据库中 ——> 将temp中的文件转移到备份文件
	夹中并根据时间存储到相对应的文件夹中。


###优化部分：

- **优化1**

**上次的项目流程是：为每个任务创建线程 ——> 线程里面创建定时器 ——> 
	完成任务。**	

我觉得我敢我上次的代码上传到github上也是胆子大，幸好github没有fans，不然被人笑掉大牙了。。。

我本来是想继续优化做成可视化，可人工控制的。结果按照我之前的那样压根控制不了，关闭线程，定时器还在继续任务。关闭定时器，线程又不会被关闭。哎，什么都不了解就乱写。。。

**定时器本身开启之后就是创建一个线程的**，在这个线程里面完成任务。所以再开启一个线程里面创建定时器完全是多此一举！在这里我换了个思路来写。

	现在的项目流程：开启第一个任务在主线程中直接run ——> 开启一个分线程
	来完成第二个任务
	
代码如下：
	
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
    
 
 如下图：哈哈，我用oc写的一个简单的控制任务开关   
    
 ![控制任务开关器](http://olgfh4099.bkt.clouddn.com/Snip20170331_5.png)
    
 这里就是直接开启线程执行任务，而在线程里的run方法里执行任务时根据一个标志boolean类型的属性close来控制任务的开始于停止，这里注意需要使用**synchronized**关键字将线程锁住，否则在线程睡眠的那段时间，再执行run就是重新创建一个新的线程了。（你们可以把关键字去掉，然后重复的开关开关）就知道我说的什么意思了。
 
 代码如下
 
 		
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
 
 
 - **优化2**

 之前的回执文件是直接下载至备份，然后解析存入数据库中再删除ftp上的文件，但是缺乏考虑到如果在下载过程，或者解析过程中途断掉之后的处理。如果断掉的话还是重头从ftp上下载，比较坑。
 
 这次加入了一个temp文件，先将ftp上的文件下载到temp文件夹里面，然后进行解析插入数据库，最后将temp文件移到备份。现在每次开启任务的时候是先判断temp文件里面是否有未解析的文件，有的话先解析再下载。
 
 这是项目流程考虑的不清楚！我的我的~
 
 
 - **优化3**

 之前的存储到数据库是解析一个对象存储一个，这样真的太慢了！！！，插入50个就需要一分钟你敢信？
 
 所以我将之前的单个单个存储用foreach改成了批量存储，经测试，插入五百条数据大约一秒钟不到。
 
 用的mybatis代码如下：
	
		<insert id="add" parameterType="java.util.ArrayList">
        INSERT INTO add_receipt VALUES
        <foreach item="item" index="key" collection="list"open="" separator="," close="">
        (#{item.receiptId},#{item.guid},#{item.ebpCode},#{item.ebcCode},#{item.orderNo},#{item.returnStatus},#{item.returnTime}, #{item.returnInfo},#{item.uploadTime})
         </foreach>;
          </insert>
          
          
          
 
 基本优化了以上三点。依旧新代码上传到[github](https://github.com/LucasDang/FTPUpAndDown.git)
 
 现在准备转成b/s项目，接下来还需要展示回执错误信息的一个列表，还需要再写几个接口调用。
 
 写着玩，有不对的地方还请指出来。谢谢。
 
 ###联系方式
 - **github：** [大猫传说中的gitHud地址](https://github.com/LucasDang/FTPUpAndDown.git)
 - **邮箱：** [不经常用，回复慢的话请原谅](1974469025@qq.com)1974469025@qq.com

 
 
 