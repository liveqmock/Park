<%@ page language="java" contentType="text/html; charset=gbk"
         pageEncoding="gbk"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
    <meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=1">
    <meta content="yes" name="apple-mobile-web-app-capable">
    <meta name="apple-mobile-web-app-status-bar-style" content="black">
    <meta http-equiv="x-ua-compatible" content="IE=edge">
    <meta name="renderer" content="webkit">
    <title>停车缴费</title>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/resources/css/list.css?v=6" />
    <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/weui.min.css">
    <link href="https://cdn.bootcss.com/jquery-weui/1.0.0-rc.1/css/jquery-weui.min.css" rel="stylesheet">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/swiper.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/style.css">
   <%-- <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/main.css" />--%>
    <style type="text/css">
        /*#scroller li {
            padding:0 10px;
            height:80px;
            line-height:40px;
            background-color:#FFFFFF;
            font-size:14px;
            margin-top:1px;
        }

        .right2{
            float:right;
            margin-right:30px;

        }
        .left{
            float:left;
        //margin-right:10px;
        }

        .hide{
            display:none;
        }

        a{
            text-decoration:none;
            color:#5F5F5F;
        }

        #header {
            position:absolute; z-index:2;
            top:0; left:0;
            width:100%;
            height:45px;
            line-height:45px;
            background-color:#F3F3F3;
            padding:0;
            font-size:20px;
            text-align:center;
        }

        .red{
            color:red;
        }
        .wx_pay{
            border-radius:15px;
        //margin-left:2%;
        //height:20px;
        //margin-top:5%;
            font-size:14px;
            background-color:#04BE02;
            color:white;
        }
        .wx_checkbox{
            background-color: #FFF;
            border: 1px solid #C1CACA;
            box-shadow: 0 1px 2px rgba(0, 0, 0, 0.05), inset 0px -15px 10px -12px rgba(0, 0, 0, 0.05);
            border-radius: 5px;
            vertical-align:text-bottom; margin-bottom:1px;
        }
        .passli {
            background-image: url(${pageContext.request.contextPath}/resources/images/wxpublic/arrow.png);
            background-size: 19px 39px;
            background-repeat: no-repeat;
            background-position: right center;
        }
        .middle1 {
            margin-top: 45%;
            color: gray;
            position: relative;
            z-index: 99;
            text-align: center;
            font-size: 17px;
        }

        .hide1{
            display:none;
        }
        .weui-btn:after {
            content: " ";
            width: 200%;
            height: 200%;
            position: absolute;
            top: 0;
            left: 0;
            !* border: 1px solid rgba(0,0,0,.2); *!
            -webkit-transform: scale(.5);
            transform: scale(.5);
            -webkit-transform-origin: 0 0;
            transform-origin: 0 0;
            box-sizing: border-box;
            border-radius: 10px;
        }
        .weui-btn:active{
            top:1px;
        }*/

        <!--轮播图-->
        /*    body,div,ul,li,a,img{margin: 0;padding: 0;}
        ul,li{list-style: none;}
        a{text-decoration: none;}

        .wrapper{position: relative;width: 100%;height: 266px;}
        .banner{width: 100%;height: 266px;overflow: hidden;}
        .imgList{width:100%;height:266px;z-index: 10;}
        .imgList li{display: none;}
        .imgList .imgOn{display: inline;}
        .bg{position: absolute;bottom: 0;width: 100%;height: 40px;z-index:20;opacity: 0.4;filter:alpha(opacity=40);background: black;}
        .infoList{position: absolute;left: 10px;bottom: 10px;z-index: 30;}
        .infoList li{display: none;}
        .infoList .infoOn{display: inline;color: white;}
        .indexList{position: absolute;right: 10px;bottom: 5px;z-index: 30;}
        .indexList div{float: left;margin-right: 5px;padding: 2px 4px;border: 2px solid black;background: grey;cursor: pointer;}

        .indexList .indexOn{background: red;font-weight: bold;color: white;}*/
        body,div,ul,li,a,img{margin: 0;padding: 0;}
        html, body {font-family: "Microsoft Yahei", Verdana, sans-serif;}
        .middle1 {
            margin-top: 15%;
            color: gray;
            position: relative;
            z-index: 99;
            text-align: center;
            font-size: 17px;
        }

        .hide1{
            display:none;
        }

        ul, li{list-style: none;}
    /*    ul, li{list-style: none;}
        address, cite, dfn, em, var {font-style: normal;}

        .banner{position: relative;overflow: auto;text-align: center;width:200px;margin:0 auto}
        .banner .dots{position:absolute;left:0;right:0;bottom:70px;z-index:3;}
        .banner .dots li{display:inline-block;width:10px;height:10px;margin:0 4px;text-indent:-999em;border:2px solid #fff;border-radius:6px;cursor:pointer;opacity:.4;-webkit-transition:background .5s,opacity .5s;-moz-transition:background .5s,opacity .5s;transition:background .5s,opacity .5s}
        .banner .dots li.active{background:#fff;opacity:1}
        .banner .arrow{position:absolute;width:15px;height:25px;top: 50%; margin-top: -18px; overflow: hidden;z-index: 2; -webkit-transition: all 0.5s; -moz-transition: all 0.5s;  -ms-transition: all 0.5s; -o-transition: all 0.5s; transition: all 0.5s;}
        .banner #al{left:15px}
        .banner #ar{right:15px}
        .banner ul{z-index: 1;width: 600%;overflow: hidden;}
        .banner ul .slider-item {width:100%; float: left;height:200px; position: relative; overflow: hidden;}
        .banner ul .slider-item .slider-title {position: absolute;left: 0;bottom: 0px;width: 100%;max-height: 56px;padding: 15px;text-align:left;line-height: 28px;color: #fff;font-size: 18px;font-weight: bold;background: -webkit-linear-gradient(top, transparent, rgba(0,0,0,0.9));background: url(../images/slider-info-bg.png) \9;}
        .banner .progress{position:absolute;left:0;bottom:0;width:15%;min-height:3px;overflow:hidden;z-index:2;background:#f60}

        .middle1 {
            margin-top: 15%;
            color: gray;
            position: relative;
            z-index: 99;
            text-align: center;
            font-size: 17px;
        }

        .hide1{
            display:none;
        }*/
    </style>

</head>
<body>
<%--<div style="height: 266px;width: 100%;">
    <a href="https://www.baidu.com" style="width: 100%;height: 100%;">
        <img style="width: 100%;height: 100%;" src="${pageContext.request.contextPath}/resources/images/img/1.jpg" alt="">
    </a>
</div>--%>

<div id="wrapper" style="margin-top:-45px;">
    <div id="scroller">
        <div id="pullDown" class="idle">
            <span class="pullDownIcon"></span>
            <span class="pullDownLabel">下拉刷新...</span>
        </div>

        <%--<div class="banner" id="b04" style="width: 100%;">
            &lt;%&ndash;<ul>
                <li class="slider-item"><a href="http://www.sciseetech.com/"><img src="${pageContext.request.contextPath}/resources/images/img/01.jpg" alt="小米进军泡面界，5块一桶还包邮？雷军回应；92女生：360关闭直播不诚恳，需周鸿祎道歉；传阿里即将控股A站|早报" width="100%" height="200px" ><span class="slider-title" ><em>小米进军泡面界1，5块一桶还包邮？雷军回应；92女生：360关闭直播不诚恳，需周鸿祎道歉；传阿里即将控股A站|早报</em></span></a></li>
                <li class="slider-item"><a href="https://www.baidu.com"><img src="${pageContext.request.contextPath}/resources/images/img/02.jpg" alt="如果你是创业者，你或许应该琢磨琢磨投资人是怎么想的" width="100%" height="200px" ><span class="slider-title"><em>如果你是创业者，你或许应该琢磨琢磨投资人是怎么想的</em></span></a></li>
                <li class="slider-item"><a href="#"><img src="${pageContext.request.contextPath}/resources/images/img/03.jpg" alt="傅盛：生物学思维给我的四个启示" width="100%" height="200px" ><span class="slider-title"><em>傅盛：生物学思维给我的四个启示</em></span></a></li>
                <li class="slider-item"><a href="#"><img src="${pageContext.request.contextPath}/resources/images/img/04.jpg" alt="重仓何小鹏" width="100%" height="200px" ><span class="slider-title"><em>重仓何小鹏</em></span></a></li>
                <li class="slider-item"><a href="#"><img src="${pageContext.request.contextPath}/resources/images/img/05.jpg" alt="深潜：王石的创业" width="100%" height="200px" ><span class="slider-title"><em>深潜：王石的创业</em></span></a></li>
            </ul>&ndash;%&gt;
            <ul>
                <li class="slider-item"><a href="${advertisements[0].redirectUrl}"><img src="${advertisements[0].path}" alt="${advertisements[0].description}" width="100%" height="200px" ><span class="slider-title"><em>${advertisements[0].description}</em></span></a></li>
                <li class="slider-item"><a href="${advertisements[1].redirectUrl}"><img src="${advertisements[1].path}" alt="${advertisements[1].description}" width="100%" height="200px" ><span class="slider-title"><em>${advertisements[1].description}</em></span></a></li>
                <li class="slider-item"><a href="${advertisements[2].redirectUrl}"><img src="${advertisements[2].path}" alt="${advertisements[2].description}" width="100%" height="200px" ><span class="slider-title"><em>${advertisements[2].description}</em></span></a></li>
                <li class="slider-item"><a href="${advertisements[3].redirectUrl}"><img src="${advertisements[3].path}" alt="${advertisements[3].description}" width="100%" height="200px" ><span class="slider-title"><em>${advertisements[3].description}</em></span></a></li>
                <li class="slider-item"><a href="${advertisements[4].redirectUrl}"><img src="${advertisements[4].path}" alt="${advertisements[4].description}" width="100%" height="200px" ><span class="slider-title"><em>${advertisements[4].description}</em></span></a></li>
            </ul>
            <div class="progress"></div>
            <a href="javascript:void(0);" class="unslider-arrow04 prev"><img class="arrow" id="al" src="${pageContext.request.contextPath}/resources/images/img/arrowl.png" alt="prev" width="10" height="15"></a>
            <a href="javascript:void(0);" class="unslider-arrow04 next"><img class="arrow" id="ar" src="${pageContext.request.contextPath}/resources/images/img/arrowr.png" alt="next" width="10" height="17"></a>
        </div>--%>

        <!--没有描述的轮播图-->
        <div class="apple-banner">
            <div class="swiper-container">
                <div class="swiper-wrapper">
                    <div class="swiper-slide slide1" >
                        <%--<a href="https://www.baidu.com" style="width: 100%;height: 100%;"><img style="width: 100%;height: 100%;" src="${pageContext.request.contextPath}/resources/images/img/01.jpg" /></a>--%>
                            <a href="${advertisements[0].redirectUrl}"><img src="${advertisements[0].path}" alt="${advertisements[0].description}" width="100%" height="250px" /></a>
                    </div>
                    <div class="swiper-slide slide2" <%--style="background-size: auto 100%;background: #000 url(${pageContext.request.contextPath}/resources/images/img/watch_alt_large.jpg) no-repeat 50% 10%;"--%>>
                        <a href="${advertisements[1].redirectUrl}"><img src="${advertisements[1].path}" alt="${advertisements[1].description}" width="100%" height="250px" /></a>
                    </div>
                    <div class="swiper-slide slide3" >
                        <a href="${advertisements[2].redirectUrl}"><img src="${advertisements[2].path}" alt="${advertisements[2].description}" width="100%" height="250px" /></a>

                    </div>
                </div>
                <div class="swiper-button-prev" ><span style="background:  url(${pageContext.request.contextPath}/resources/images/img/arrows.png) no-repeat center 17px;"></span></div>
                <div class="swiper-button-next"><span style="background:  url(${pageContext.request.contextPath}/resources/images/img/arrows.png) no-repeat center -71px;"></span></div>
                <ul class="swiper-pagination autoplay">
                </ul>
            </div>
        </div>



        <div class="middle1 hide1">
            <div style="margin-bottom: 15px"><i class="weui-icon-info weui-icon_msg"></i></div>
            暂无订单
            <a href="tocarnumbers?uin=${uin}" class="weui-btn weui-btn_default" style="display:none;box-shadow: 2px 2px 4px  rgba(0,0,0,0.5);background: url(${pageContext.request.contextPath}/resources/images/wxpublic/carnumber1.png) no-repeat 10px 3px;width: 45%;margin-top: 20px"><span style="margin-left: 39px;">我的车牌</span></a>
        </div>
        <ul id="thelist">
            <!--
            <div class="weui-form-preview">
                  <div class="weui-form-preview__hd">
                    <label class="weui-form-preview__label"><span style="font-size:20px;color:black">京T12312</span></label>
                    <em class="weui-form-preview__value"><span style="font-size:20px;">￥240.00</span></em>
                  </div>
                  <div class="weui-form-preview__bd">
                    <div class="weui-form-preview__item">
                      <label class="weui-form-preview__label">入场时间</label>
                      <span class="weui-form-preview__value">2017-05-12 09:37</span>
                    </div>
                    <div class="weui-form-preview__item">
                      <label class="weui-form-preview__label">锁定状态</label>
                      <span class="weui-form-preview__value"><span style="color:red">已锁定</span>&nbsp;&nbsp;&nbsp;&nbsp;</span>
                    </div>
                  </div>
                  <div class="weui-form-preview__ft">
                    <button class="weui-form-preview__btn weui-form-preview__btn_primary">锁定车辆</button>
                    <button class="weui-form-preview__btn weui-form-preview__btn_primary">支付订单</button>
                  </div>
                </div>
                <br/>
                 -->
        </ul>

        <div id="pullUp" class="idle">
            <span class="pullUpIcon"></span>
            <span class="pullUpLabel">上拉加载更多...</span>
        </div>
    </div>
    <div class="weui-cells" style="position:absolute;bottom: 0px;width:100%;display: block" id = "carlist">
        <a class="weui-cell weui-cell_access" href="tocarnumbers?uin=${uin}">
            <div class="weui-cell__hd"><img src="${pageContext.request.contextPath}/resources/images/wxpublic/carnumber1.png" alt="" style="width:20px;margin-right:5px;display:block"></div>
            <div class="weui-cell__bd">
                <p>我的车牌</p>
            </div>
            <div class="weui-cell__ft"></div>
        </a>
    </div>

</div>
<input id="mobile" type="text" style="display:none;" value="${mobile}"/>
<input id="openid" type="text" style="display:none;" value="${openid}"/>
<input id="domain" type="text" style="display:none;" value="${domain}"/>
<input id="uin" type="text" style="display:none;" value="${uin}"/>
<script src="${pageContext.request.contextPath}/resources/js/iscroll.js"></script>
<script src="${pageContext.request.contextPath}/resources/js/curorderlist.js?v=5"></script>
<script src="${pageContext.request.contextPath}/resources/js/jquery.js"></script>
<script src="${pageContext.request.contextPath}/resources/js/jquery-weui.min.js"></script>
<script src="${pageContext.request.contextPath}/resources/js/unslider.min.js"></script>
</body>
<script type="text/javascript">
    var pagefrom = "${from}";
    if(pagefrom=='page'){
        document.getElementById("carlist").style.display='none';
    }
    $(document).ready(function(e) {
        var progress = $(".progress"),li_width = $("#b04 li").length;
        var unslider04 = $('#b04').unslider({
                dots: true,
                complete:function(index){//自己添加的，官方没有
                    progress.animate({"width":(100/li_width)*(index+1)+"%"});
                }
            }),

            data04 = unslider04.data('unslider');
        $('.unslider-arrow04').click(function() {
            var fn = this.className.split(' ')[1];
            data04[fn]();
        });
    });

 /*   var curIndex = 0; //当前index
    //  alert(imgLen);
    // 定时器自动变换2.5秒每次
    var autoChange = setInterval(function(){
        if(curIndex < $(".imgList li").length-1){
            curIndex ++;
        }else{
            curIndex = 0;
        }
        //调用变换处理函数
        changeTo(curIndex);
    },2500);

    $(".indexList").find("div").each(function(item){
        $(this).hover(function(){
            clearInterval(autoChange);
            changeTo(item);
            curIndex = item;
        },function(){
            autoChange = setInterval(function(){
                if(curIndex < $(".imgList li").length-1){
                    curIndex ++;
                }else{
                    curIndex = 0;
                }
                //调用变换处理函数
                changeTo(curIndex);
            },2000);
        });
    });
    function changeTo(num){
        $(".imgList").find("li").removeClass("imgOn").hide().eq(num).fadeIn().addClass("imgOn");
        $(".infoList").find("li").removeClass("infoOn").eq(num).addClass("infoOn");
        $(".indexList").find("div").removeClass("indexOn").eq(num).addClass("indexOn");
    }
*/

</script>
<script src="${pageContext.request.contextPath}/resources/js/swiper.min.js"></script>
<script src="${pageContext.request.contextPath}/resources/js/apple.js"></script>
</html>
