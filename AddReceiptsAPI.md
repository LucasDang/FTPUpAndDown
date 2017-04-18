##澳多多回执相关接口


###规则
1.请求数据全部为JSON格式输出

**由于有的请求参数会参数用标点符号分割的形式，所以这里提醒，所有的分割符都为英文下的标点符号**

 - 请求

 **code**:响应码：固定为200，代表从服务器获取资源成功
 
 **msg**:服务器响应信息：服务器响应成功或者出错等信息
 
 **result**:数据实体：返回请求的相关数据
 
 **/code**:数据响应吗：返回请求数据的相应码,分别有
    - 成功 = 1;
    - 警告 = 2;
    - 无返回 = -1;
    - 验证失败 = -2;
    - 失败 = 9;
    
 **/description**:成功或失败的描述
 
 **/result**:返回数据详情，单个数据返回map，多个数据则返回list

 **/totalRecord**:如果result为list则返回总个数。
 
	    {
		    code:200,
		    msg:
		    result:{ 
		        code:1,
		        description:成功获取回执列表,
		        result:[
		
		        ],
		        totalRecord:0
		    }
		}
 


2.获取方式主要为POST和GET（若为GET方式会在URL处标记）


****

###上传下载控制

####URL：

***http://test.adodoxm.com:8080/AddReceipt/ftpHandle***

这是用来控制ftp上传下载的，可以获取状态，开启和关闭上传下载，获取文件夹的文件名（目前只展示文件名，更多操作这里暂时用不到）。

	参数规则：有action和task两个必传参数，action代表动作名称，task
	为任务名，由于有上传和下载两个任务，上传订单这里的task为upload，
	下载回执则为download,同时查询上传和下载的状态则用,分割为
	upload,download，注意这里的,是英文下的。

- **获取状态**

	参数：

	> action=status
	
	> task=任务名
	
	返回值
	
	> code为0代表关闭，为1代表开启状态
	
	
- **开启和关闭**
	
	参数
	
	> action=handle
	
	> task=任务名，这里的task不可以传两个任务，只能一个一个传。
	
	> isOpen=开启还是关闭：传1代表开启，传0则代表关闭
	
	返回值
	
	> code为1代表操作成功
	
- **获取文件列表**	
 
 	参数
 	
 	> action=fileNames
	
	> task=任务名
	
	返回值
	
		{
	    "code":"200",
	    "msg":"操作成功",
	    "result":{
	        "code":1,
	        "description":"请求成功",
	        "result":{
	            "localOrder":{
	                "code":-1,
	                "description":"这是返回本地订单文件的文件名列表，code为1时代表成功，否则则表示有地方出错",
	                "fileNames":null
	            },
	            "ftpReceipt":{
	                "code":1,
	                "description":"这是返回ftp上的回执文件的文件名列表，code为1时代表成功，否则则表示有地方出错",
	                "fileNames":Array[24]
	            },
	            "ftpOrder":{
	                "code":1,
	                "description":"这是返回ftp上订单文件的文件名列表，code为1时代表成功，否则则表示有地方出错",
	                "fileNames":Array[1]
	            },
	            "localReceipt":{
	                "code":-1,
	                "description":"这是返回本地回执文件的文件名列表，code为1时代表成功，否则则表示有地方出错",
	                "fileNames":null
	            }
	        },
	        "totalRecord":null
	    	}
	    }
	
	
	
	
****	
	
###批次号获取和回执列表展示

####URL：

***http://test.adodoxm.com:8080/AddReceipt/receiptHandle***

- **批次号获取**

	参数
	
	> action=batchNos
	
	> fuzzyBatchNo=模糊查询的字段，可不传，不传则返回最近的批次，传则返回查询所有包含有当前字段的批次号
	
	> pageSize=查询几条记录，按照批次号顺序从大到小返回pageSize条记录，不传则默认为查询8条记录


- **回执列表获取**

	**注意：**这里的参数比较复杂，主要是根据时间以及批次号筛选，参数除了action都可以不传，不传就默认返回当前时间一个小时之前的失败回执列表，传了哪个参数就根据那个参数的规则，其他默认。
	
	  参数：
	  
	  > action=receipts，必传
	  
	  > pageSize=查询几条记录，可不传，不传则默认为20
	  
	  > currentPage=当前页数，可不传，不传默认为第一页
	    
	  > batchNo=批次号：根据批次查询回执，可不传，不传则默认使用时间来筛选
	  
	  > intervalTime=间隔时间：前多久，这里采用 "个数:单位" 来表示间隔前多久 如 5:m 代表5分钟，1:h代表一小时，最多为d，代表前多少天，这里的分割符为英文下的符号
	   
	  > startTime=开始时间：精确到s，传时间戳。
	    
	  > endTime=结束时间：如果传了intervalTime，那么传的startTime和endTime则无效，所以这里需注意！
	
	  > sort=排序字段:按时间排序，按订单号排序，1：按订单号正序,2：按订单号倒叙,不传则默认为1
	  
	  > status=订单状态,1：只显示成功回执，0：成功和失败的全部显示，-1：只显示失败回执不传则默认显示失败的回执，可不传，不传则默认为-1
  
- **订单回执详情**

	参数：
	
	> action=receiptDetail，必传
	
	> orderNo=订单号，必传，根据订单号查询该订单所有的回执状态并按照时间排序   
   
       
          
  
  
	