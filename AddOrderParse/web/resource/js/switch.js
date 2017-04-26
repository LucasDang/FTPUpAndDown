/*开关js*/
var honeySwitch = {};
honeySwitch.themeColor = "rgb(100, 189, 99)";
honeySwitch.init = function() {
	var s = "<span class='slider'></span>";
    jQuery("[class^=switch]").append(s);

    if (this.themeColor) {

        var c = this.themeColor;
        jQuery(".switch-on").css({
            'border-color' : c,
            'box-shadow' : c + ' 0px 0px 0px 16px inset',
            'background-color' : c
        });
        jQuery(".switch-off").css({
            'border-color' : '#dfdfdf',
            'box-shadow' : 'rgb(223, 223, 223) 0px 0px 0px 0px inset',
            'background-color' : 'rgb(255, 255, 255)'
        });
    }
};


function switchTask(ele) {
	var isOpen;
    if (jQuery(ele).hasClass("switch-on")) {
    	isOpen = '0';
    }else {
        isOpen = '1';
	}
	console.log(jQuery(ele).attr('id') + ":" +isOpen);

    jQuery.ajax2({
        url: '/AddReceipt/ftpHandle',
        type: "post",
        data:{action:'handle',task:jQuery(ele).attr('id'),isOpen:isOpen},
        dataType: "json",
        success: function (data) {
            if (data.code == 200){
                if (data.result.code == 1){
                    if (isOpen == '0'){
                        console.log("1");
                        jQuery(ele).removeClass("switch-on").addClass("switch-off");
                        jQuery(".switch-off").css({
                            'border-color' : '#dfdfdf',
                            'box-shadow' : 'rgb(223, 223, 223) 0px 0px 0px 0px inset',
                            'background-color' : 'rgb(255, 255, 255)'
                        });
                    }else {
                        console.log("2");
                        jQuery(ele).removeClass("switch-off").addClass("switch-on");
                        if (honeySwitch.themeColor) {
                            var c = honeySwitch.themeColor;
                            jQuery(ele).css({
                                'border-color' : c,
                                'box-shadow' : c + ' 0px 0px 0px 16px inset',
                                'background-color' : c
                            });
                        }
                        if (jQuery(ele).attr('themeColor')) {
                            var c2 = jQuery(ele).attr('themeColor');
                            jQuery(ele).css({
                                'border-color' : c2,
                                'box-shadow' : c2 + ' 0px 0px 0px 16px inset',
                                'background-color' : c2
                            });
                        }
                    }

                }else {
                    alert(data.result.description);
                }
            }else {
                alert(data.msg);
            }
            console.log(data);

        }
    });

}




