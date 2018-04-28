<%@ page language="java" contentType="text/html; charset=gb2312"
    pageEncoding="gb2312"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta content="width=device-width,initial-scale=1.0,maximum-scale=1.0,user-scalable=no" name="viewport">
<meta content="yes" name="apple-mobile-web-app-capable">
<meta content="black" name="apple-mobile-web-app-status-bar-style">
<meta content="telephone=no" name="format-detection">
<meta content="email=no" name="format-detection">
<title>��ǰ����</title>
<script src="resources/js/jquery.js" type="text/javascript"></script>
<link rel="stylesheet" href="resources/css/prepay.css?v=2">
<style type="text/css">
.error {
	color: red;
	font-size: 15px;
	margin-top:5%;
}
.noorder{
	text-align:center;
	color:red;
	margin-top:55%;
}
.unprepay {
	text-align:center;
	color:red;
	margin-top:55%;
}
.wx_pay{
	border-radius:5px;
	width:96%;
	margin-left:2%;
	height:40px;
	margin-top:5%;
	font-size:15px;
	background-color:#04BE02;
	color:white;
}
</style>
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
			
			//ÿ���Ƴ�һ��class
			function removeClass(currNode, curClass){
				var oldClass,newClass1 = "";
		        oldClass = currNode.getAttribute("class") || currNode.getAttribute("className");
		        if(oldClass !== null) {
				   oldClass = oldClass.split(" ");
				   for(var i=0;i<oldClass.length;i++){
					   if(oldClass[i] != curClass){
						   if(newClass1 == ""){
							   newClass1 += oldClass[i]
						   }else{
							   newClass1 += " " + oldClass[i];
						   }
					   }
				   }
				}
				currNode.className = newClass1; //IE ��FF��֧��
			}
			
			//����Ƿ������ǰclass
			function hasClass(currNode, curClass){
				var oldClass;
				oldClass = currNode.getAttribute("class") || currNode.getAttribute("className");
				if(oldClass !== null){
					oldClass = oldClass.split(" ");
					for(var i=0;i<oldClass.length;i++){
					   if(oldClass[i] == curClass){
						   return true;
					   }
				   }
				}
				return false;
			}
</script>
</head>
<body>
	<!-- �ҵĳ���[[ -->
		<dl class="my-lpn">
			<dt class="title">�ҵĳ��ƺ���</dt>
			<dd class="lpn">${carnumber}<span id ="mycar"><a class="change-btn" href="aliprepay.do?action=toddcar&ticketid=${ticketid}&parkid=${parkid}&openid=${openid}&forward=${forward}">�޸�</a></span></dd>
		</dl>
		<!-- �ҵĳ���]] -->
	<section class="main">
		<form method="post" action="aliprepay.do?action=prepay" role="form" id="prepayform" class="confirm">
			<fieldset>
			<div class="info-area">	
				<dl class="totle" style="border-bottom:0px">
					<dt class="totle-title">Ԥ��ͣ����</dt>
					<dd class="totle-num" style="color:#04BE02;">��<span id="aftertotal">${total}</span></dd>
					<ul class="nfc">
						<li class="list1"></li>
					</ul>
					<div class="sweepcom hide" style="border-bottom: 1px solid #E0E0E0;"></div>
				</dl>
				
				<ul class="info-list" style="padding-top:1px;">
					<li class="list"><span class="list-title">��Ԥ�����</span><span class="list-content">${total}Ԫ</span></li>
					<li class="list"><span class="list-title">�볡ʱ��</span><span class="list-content">${starttime}</span></li>
					<li class="list"><span class="list-title">��ͣʱ��</span><span class="list-content" id="parktime">${parktime}</span></li>
					<li class="list"><span class="list-title carnumber hide">���ƺ���</span><span class="list-content">${carnumber}</span></li>
				</ul>
				
				<ul class="info-list hide">
					<li class="list"><input name="orderid" value="${orderid}"></li>
					<li class="list"><input name="parkid" value="${parkid}"></li>
					<li class="list"><input name="unionid" value="${unionid}"></li>
					<li class="list"><input name="uin" value="${uin}"></li>
				</ul>
			</div>
			<input type="button" id="wx_pay" onclick='payorder();' class="wx_pay" value="ȥ֧��">
			<div class="tips"></div>
			</fieldset>
		</form>
		<div style="text-align:center;" id="error" class="error">����10����֮���볡</div>
	</section>
	<section class="noorder hide" id="noorder">
		<div>��ǰ�޶���</div>
	</section>
</body>
<script type="text/javascript">
	function payorder(){
		$("#prepayform")[0].submit();
	}
	if('${noorder}'==1){
		$(".noorder").removeClass("hide");
		$(".error").addClass("hide");
		$(".main").addClass("hide");
	}else if('${noorder}'=='-1'){
		$(".noorder").removeClass("hide");
		$(".error").addClass("hide");
		$(".main").addClass("hide");
		document.getElementById('noorder').innerHTML="${error}";
	}
	if('${carnumber}'=='')
		document.getElementById('mycar').style.display='none';
</script>
</html>
