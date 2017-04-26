//对ajax进行封装，加载进度条
jQuery.ajax2 = function (options) {
    var img = jQuery("#progressImgage");
    var mask = jQuery("#maskOfProgressImage");
    var complete = options.complete;
    options.complete = function (httpRequest, status) {
        img.hide();
        mask.hide();
        if (complete) {
            complete(httpRequest, status);
        }
    };
    options.async = true;
    img.show().css({
        "position": "fixed",
        "top": "50%",
        "left": "50%",
        "margin-top": function () { return -1 * img.height() / 2; },
        "margin-left": function () { return -1 * img.width() / 2; }
    });

    mask.show().css("opacity", "0.1");
    jQuery.ajax(options);
};


// 页数按钮点击事件
//点击第几页的时间，mm为当前的页数
var GG = {
        "kk":function(mm){
            if(requestCount != 0) {
                GetReceipts(mm);
            }
        }
}

// 查看详情

function getDetail(orderNo){
    console.log("订单号为：" + orderNo);

    jQuery.ajax2({
        url:'/AddReceipt/receiptHandle?action=receiptDetail',
        data:{orderNo:orderNo},
        type:"post",
        dataType:"json",
        success:function(data) {
            if(data.code ==200) {
                if (data.result.code == 1) {

                    var receiptObj = data.result;
                    if (receiptObj.result.length > 0) {
                        console.log(receiptObj.totalRecord);

                        jQuery('.theme-popbod').empty();

                        var receipt_detail = "";

                        jQuery.each(receiptObj.result, function (i, item) {
                            var uploadTime = format(item.uploadTime);

                            receipt_detail += '<ul>';
                            receipt_detail += '<li class="t-status"><label>' + item.returnStatus + '</label><i>|</i></li>';
                            receipt_detail += '<li class="t-time"><label>' + item.returnTime + '</label><i>|</i></li>';
                            receipt_detail += '<li class="t-content"><label>' + item.returnInfo + '</label><i>|</i></li>';
                            receipt_detail += '<li class="t-upload"><label>' + uploadTime + '</label></li>';
                            receipt_detail += '</ul>';
                        })
                        jQuery('.theme-popbod').append(receipt_detail);
                    }else {
                        jQuery('.theme-popbod').empty();
                        alert("查询到的数量为0")
                    }

                }
            }

        }
    })

    jQuery('.theme-poptit h3').text('订单号：' + orderNo);
    jQuery('.theme-popover').slideDown(200);
    jQuery('.theme-popover-mask').fadeIn(100);

};
function closeDetail(){
    jQuery('.theme-popover-mask').fadeOut(100);
    jQuery('.theme-popover').slideUp(200);
};

/*模糊查询 */
var $ = function (id) {
    return "string" == typeof id ? document.getElementById(id) : id;
}
var Bind = function (object, fun) {
    return function () {
        return fun.apply(object, arguments);
    }
}
function AutoComplete(obj, autoObj, arr) {
    this.obj = $(obj); //输入框
    this.autoObj = $(autoObj);//DIV的根节点
    this.value_arr = arr; //不要包含重复值
    this.index = -1; //当前选中的DIV的索引
    this.search_value = ""; //保存当前搜索的字符
}

AutoComplete.prototype = {
    //初始化DIV的位置
    init: function () {
        this.autoObj.style.left = this.obj.offsetLeft + "px";
        this.autoObj.style.top = this.obj.offsetTop + this.obj.offsetHeight + "px";
        this.autoObj.style.width = this.obj.offsetWidth - 2 + "px";//减去边框的长度2px
    },
    //删除自动完成需要的所有DIV
    deleteDIV: function () {
        while (this.autoObj.hasChildNodes()) {
            this.autoObj.removeChild(this.autoObj.firstChild);
        }
        this.autoObj.className = "auto_hidden";
    },
    //设置值
    setValue: function (_this) {
        return function () {
            _this.obj.value = this.seq;
            _this.autoObj.className = "auto_hidden";
        }
    },
    //模拟鼠标移动至DIV时，DIV高亮
    autoOnmouseover: function (_this, _div_index) {
        return function () {
            _this.index = _div_index;
            var length = _this.autoObj.children.length;
            for (var j = 0; j < length; j++) {
                if (j != _this.index) {
                    _this.autoObj.childNodes[j].className = 'auto_onmouseout';
                } else {
                    _this.autoObj.childNodes[j].className = 'auto_onmouseover';
                }
            }
        }
    },
    //更改classname
    changeClassname: function (length) {
        for (var i = 0; i < length; i++) {
            if (i != this.index) {
                this.autoObj.childNodes[i].className = 'auto_onmouseout';
            } else {
                this.autoObj.childNodes[i].className = 'auto_onmouseover';
                this.obj.value = this.autoObj.childNodes[i].seq;
            }
        }
    },
    //响应键盘
    pressKey: function (event) {
        var length = this.autoObj.children.length;
        //光标键"↓"
        if (event.keyCode == 40) {
            ++this.index;
            if (this.index > length) {
                this.index = 0;
            } else if (this.index == length) {
                this.obj.value = this.search_value;
            }
            this.changeClassname(length);
        }
        //光标键"↑"
        else if (event.keyCode == 38) {
            this.index--;
            if (this.index < -1) {
                this.index = length - 1;
            } else if (this.index == -1) {
                this.obj.value = this.search_value;
            }
            this.changeClassname(length);
        }
        //回车键
        else if (event.keyCode == 13) {
            this.autoObj.className = "auto_hidden";
            this.index = -1;
        } else {
            this.index = -1;
        }
    },
    //程序入口
    start: function (event) {
        if (event.keyCode != 13 && event.keyCode != 38 && event.keyCode != 40) {
            this.init();
            this.deleteDIV();
            this.search_value = this.obj.value;
            var valueArr = this.value_arr;
            //valueArr.sort();
            if (this.obj.value.replace(/(^\s*)|(\s*$)/g, '') == "") { return; }//值为空，退出
            try { var reg = new RegExp("(" + this.obj.value + ")", "i"); }
            catch (e) { return; }
            var div_index = 0;//记录创建的DIV的索引
            for (var i = 0; i < valueArr.length; i++) {
                if (reg.test(valueArr[i])) {
                    var div = document.createElement("div");
                    div.className = "auto_onmouseout";
                    div.seq = valueArr[i];
                    div.onclick = this.setValue(this);
                    div.onmouseover = this.autoOnmouseover(this, div_index);
                    div.innerHTML = valueArr[i].replace(reg, "<strong>$1</strong>");//搜索到的字符粗体显示
                    this.autoObj.appendChild(div);
                    this.autoObj.className = "auto_show";
                    div_index++;
                }
            }
        }
        this.pressKey(event);
        window.onresize = Bind(this, function () { this.init(); });
    }
};

