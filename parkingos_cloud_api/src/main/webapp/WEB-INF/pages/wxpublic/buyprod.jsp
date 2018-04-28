<%@ page language="java" contentType="text/html; charset=GBK"
    pageEncoding="GBK"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=1">
<meta content="yes" name="apple-mobile-web-app-capable">
<meta name="apple-mobile-web-app-status-bar-style" content="black">
<meta http-equiv="x-ua-compatible" content="IE=edge">
<title>${title}</title>

<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/weui.min.css">
<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/jquery-weui.min.css">
<style type="text/css">
	.hide{
		display: none;
	}


</style>
</head>
<body style="background-color:#EEEEEE;">
<%--<div id="wrapper" style="margin-top:-45px;">--%>
<form method="post" role="form" action="topayprod" id="payform">
	<%--<div id="scroller">--%>
		<%--<ul id="thelist">--%>
			<%--<li id="li2" class="li2">--%>
				<%--<div style="margin-top:15px;color:#6D6D6D;font-weight:bold;margin-left: 10px;">�������ƣ�${cname}</div>--%>
				<%--<div id="pname" style="margin-top:15px;color:#6D6D6D;font-weight:bold;margin-left: 10px;display:none">���²�Ʒ��${pname}</div>--%>
				<%--<div id="exptime" style="margin-top:15px;color:#6D6D6D;font-weight:bold;margin-left: 10px;display:none">�ײ���Ч������${exptime}</div>--%>
			<%--</li>--%>
			<%--<li class="li1">--%>
				<%--<div class="company_name">--%>
					<%--<span>��ʼ����</span>--%>
					<%--<input id="starttime" name="starttime"  readonly= "true"  class="jine hide" value="${btime}" />--%>
					<%--<span style="float:right;">${btime}</span>--%>
				<%--</div>--%>
			<%--</li>--%>
			<%--<li class="li1">--%>
				<%--<div class="company_name"><span>����ʱ��</span>--%>
					<%--<span style="float:right;color:#C3C3C3;">����</span>--%>
					<%--<div class="weui-cell__bd" style="float:right;margin-top:3px;">--%>
					<%--<select id="months" name="months" class="weui_select" style="font-weight:bold;color:#04be02;">--%>
						<%--<option selected="selected">1</option>--%>
						<%--<option>2</option>--%>
						<%--<option>3</option>--%>
						<%--<option>4</option>--%>
						<%--<option>5</option>--%>
						<%--<option>6</option>--%>
						<%--<option>7</option>--%>
						<%--<option>8</option>--%>
						<%--<option>9</option>--%>
						<%--<option>10</option>--%>
						<%--<option>11</option>--%>
						<%--<option>12</option>--%>
					<%--</select>--%>
					<%--</div>--%>
				<%--</div>--%>
			<%--</li>--%>
		<div class="weui-cells">
			<div class="weui-cell">
				<div class="weui-cell__bd">
					<p>�������ƣ�</p>
				</div>
				<div class="weui-cell__ft">${park_name}</div>
			<%--</div><div class="weui-cell">--%>
				<%--<div class="weui-cell__bd">--%>
					<%--<p>�ײ���Ч������</p>--%>
				<%--</div>--%>
				<%--<div class="weui-cell__ft">${exptime}</div>--%>
			<%--</div>--%>
			</div>
		</div>

		<div class="weui-cells weui-cells_form">
			<div class="weui-cell">
				<div class="weui-cell__hd"><label class="weui-label">��ʼ����</label></div>
				<div class="weui-cell__bd">
					<input class="weui-input" id="start_time" name="start_time" readonly= "true" value="${btime}">
				</div>
			</div>

			<div class="weui-cell weui-cell_select weui-cell_select-after">
				<div class="weui-cell__hd">
					<label class="weui-label">����ʱ��</label>
				</div>
				<div class="weui-cell__bd">
					<select id="months" class="weui-select" name="months">
						<option selected="selected">1</option>
						<option>2</option>
						<option>3</option>
						<option>4</option>
						<option>5</option>
						<option>6</option>
						<option>7</option>
						<option>8</option>
						<option>9</option>
						<option>10</option>
						<option>11</option>
						<option>12</option>
					</select>
				</div>
			</div>

		</div>
				<div id="showMoney" style="text-align:center;margin-top:20px;">
					<span style="font-size:25px;font-weight:bold;color:#04be02;">��</span>
					<input id="price" style="display:none" name="price" value=""><span id="money_after" style="font-size:60px;font-weight:bold;color:#04be02;">${money}</span>
					<input id="trade_no" style="display:none" name="trade_no" value="">
					<input id="com_id" style="display:none" name="com_id" value="${com_id}">
					<input id="uin" style="display:none" name="uin" value="${uin}">
					<input id="card_id" style="display:none" name="card_id" value="${card_id}">
				</div>
			<input type="button" id="paysubmit" class="weui-btn weui-btn_primary" style="width:95%;display:block" onclick='check();' value="�����¿�" />
			<div class="wxpay-logo"></div>
			<div style="text-align:center;" id="error" class="error"></div>
		<%--</ul>--%>
	<%--</div>--%>
</form>
<%--</div>--%>

<div id="footer"></div>
<script src="${pageContext.request.contextPath}/resources/js/jquery-2.1.4.js"></script>
<script src="${pageContext.request.contextPath}/resources/js/jquery-weui.min.js"></script>
<script src="${pageContext.request.contextPath}/resources/js/fastclick.js"></script>
<script>
  $(function() {
    FastClick.attach(document.body);
  });
</script>
<script type="text/javascript">
	function getprice(flag) {
		$.showLoading("���ڲ�ѯ�¿����Ѽ۸�...");
		var start_time = document.getElementById("start_time").value;
		var months = document.getElementById("months").value;
		jQuery.ajax({
					type : "post",
					url : "getprodprice",
					data : {
						'card_id' : '${card_id}',
						'start_time' : start_time,
						'months' : months,
						'uin' : '${uin}',
						'com_id' : '${com_id}',
						'r' : Math.random()
					},
					async : false,
					success : function(jsonData) {
						setTimeout('$.hideLoading()',200);
							document.getElementById("money_after").innerHTML = jsonData.price;
							document.getElementById("price").value = jsonData.price;
							console.log(jsonData.total)

							if(jsonData.state==1){
                                //�ɹ�
                                document.getElementById("paysubmit").style.display = "block"
                                document.getElementById("showMoney").style.display = "block"
							}else{
								//ʧ��
								document.getElementById("paysubmit").style.display = "none"
								document.getElementById("showMoney").style.display = "none"
								$.alert(jsonData.errmsg)
							}
							document.getElementById("trade_no").value = jsonData.trade_no;
					}
				});
	}
	getprice();
	
	/* $("#starttime").bind("change", function() {
		getprice();
	}); */
	$("#months").bind("change", function() {
		getprice();
	});

	function check() {
		/* var starttime = document.getElementById("starttime").value;
		var t1 = new Date((starttime+" 00:00:00").replace(/-/g,"/")).getTime();
		var t2 = new Date().getTime();
        if(t1 < t2 - 24*60*60*1000){
        	document.getElementById("error").innerHTML = "��ʼ����Ҫ���ڽ���";
        	return;
        } */
        
        //checkParkStatus()
       $("#payform")[0].submit();
       //getprice("1");
	}
</script>
<script type="text/javascript">
//ÿ�����һ��class
	function addClass(currNode, newClass){
        var oldClass;
        oldClass = currNode.getAttribute("class") || currNode.getAttribute("className");
        if(oldClass !== null) {
		   newClass = oldClass+" "+newClass; 
		}
		currNode.className = newClass; //IE ��FF��֧��
  		}
/* $(function () {  
    $("#starttime").mobiscroll().date({  
        theme: "android-ics light",  
        lang: "zh",  
		minDate: new Date(${minyear}, ${minmonth}, ${minday}),
        maxDate: new Date(${maxyear}, ${maxmonth}, ${maxday}),
		//invalid: { daysOfMonth: ['5/1', '12/24', '12/25'] },
        cancelText: null,  
        dateFormat: 'yy-mm-dd', //���ؽ����ʽ��Ϊ���¸�ʽ  
        // wheels:[], ���ô����Կ���ֻ��ʾ���£��˴���ʾ�����������onBeforeShow����,����Ҳ������treelistȥʵ��  
        onBeforeShow: function (inst) { 
			
		}, //�������ա�����  
        headerText: function (valueText) { //�Զ��嵯����ͷ����ʽ  
            array = valueText.split('-');  
            var time = array[0] + "-" + array[1] + "-" + array[2];
            return time;  
        }  
    });  
})  */
</script>
</body>
</html>
