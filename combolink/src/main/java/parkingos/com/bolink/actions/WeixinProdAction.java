package parkingos.com.bolink.actions;

import com.alibaba.fastjson.JSONObject;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import parkingos.com.bolink.component.CommonComponent;
import parkingos.com.bolink.constant.Constants;
import parkingos.com.bolink.constant.WeixinConstants;
import parkingos.com.bolink.dto.UnionInfo;
import parkingos.com.bolink.service.WeixinProdService;
import parkingos.com.bolink.utlis.*;
import parkingos.com.bolink.vo.ProdPriceView;
import parkingos.com.bolink.vo.ProdView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 微信月卡续费接口
 */
@Controller
public class WeixinProdAction {
    Logger logger = Logger.getLogger(WeixinProdAction.class);
    @Autowired
    WeixinProdService weixinProdService;

    @Autowired
    CommonComponent commonComponent;

    /**
     * 获取月卡列表数据
     * @param request
     * @param response
     * @return
     */
    @RequestMapping("getwxprodlist")
    public String getProdList(HttpServletRequest request, HttpServletResponse response){
        Long uin = RequestUtil.getLong(request,"uin",-1L);
        List<ProdView> prodList = weixinProdService.getProdList(uin);
        AjaxUtil.ajaxOutputWithSnakeCase(response,prodList);
        return null;
    }

    /**
     * 获取月卡价格
     * @param request
     * @param response
     * @return
     */
    @RequestMapping("getprodprice")
    public String getProdPrice(HttpServletRequest request, HttpServletResponse response){
        String cardId = RequestUtil.getString(request, "card_id");
        String startTime = RequestUtil.getString(request, "start_time");
        Long beginTime = TimeTools.getLongMilliSecondFrom_HHMMDD(startTime)/1000;
        Long comId = RequestUtil.getLong(request, "com_id", -1l);
        Long uin = RequestUtil.getLong(request,"uin",-1L);
        Integer months = RequestUtil.getInteger(request, "months", -1);
        logger.info("getprodprice:"+cardId+"~"+startTime+"~"+comId+"~"+uin+"~"+months);
        ProdPriceView prodPrice = weixinProdService.getProdPrice(comId, cardId, beginTime, months);
        logger.info(prodPrice);
        AjaxUtil.ajaxOutputWithSnakeCase(response,prodPrice);
        return null;
    }

    /**
     * 前往选择月卡续费信息的页面
     * @param request
     * @param response
     * @return
     */
    @RequestMapping("tobuyprod")
    public String toBuyProd(HttpServletRequest request, HttpServletResponse response) throws UnsupportedEncodingException {
        Long uin = RequestUtil.getLong(request,"uin",-1L);
        String cardId = RequestUtil.getString(request, "card_id");
        Long comId = RequestUtil.getLong(request, "com_id", -1l);
        Long endTime = RequestUtil.getLong(request, "end_time", -1l);
        Long carOwnerProductId = RequestUtil.getLong(request, "car_owner_product_id", -1l);
        String prodId = RequestUtil.getString(request, "prod_id");
        Integer type = RequestUtil.getInteger(request, "type", 0);//0:购买 1：续费
        String parkName = RequestUtil.getString(request,"park_name");
        parkName = StringUtils.decodeUTF8(parkName);
        parkName = new String(parkName.getBytes("GBK"), "utf-8");
        logger.info("tobuyprod=>"+uin+"~"+cardId+"~"+comId+"~"+carOwnerProductId+"~"+prodId+"~"+type+"~"+endTime+"~"+parkName);

        String bTime = TimeTools.getDate_YY_MM_DD();
        //获取月卡结束时间
        if(endTime > TimeTools.getToDayBeginTime()){
            bTime = TimeTools.getTimeStr_yyyy_MM_dd((endTime+60*60*24)*1000);
        }

        String[] minStr = bTime.split("-");
        request.setAttribute("minyear", Integer.valueOf(minStr[0]));
        request.setAttribute("minmonth", Integer.valueOf(minStr[1])-1);
        request.setAttribute("minday", Integer.valueOf(minStr[2]));
        request.setAttribute("btime", bTime);
        request.setAttribute("exptime", -1);
        request.setAttribute("pname", -1);
        request.setAttribute("maxyear", 2020);
        request.setAttribute("maxmonth", 12);
        request.setAttribute("maxday", 31);
        request.setAttribute("title", "月卡续费");
        request.setAttribute("money", "0.0");
        request.setAttribute("com_id", comId);
        request.setAttribute("card_id", cardId);
        request.setAttribute("prod_id", prodId);
        request.setAttribute("uin", uin);
        request.setAttribute("park_name", parkName);
        return "wxpublic/buyprod";
    }

    /**
     * 前往第三方支付月卡
     * @param request
     * @param response
     * @return
     */
    @RequestMapping("topayprod")
    public String toPayProd(HttpServletRequest request, HttpServletResponse response){
        String cardId = RequestUtil.getString(request, "card_id");
        Long comId = RequestUtil.getLong(request, "com_id", -1L);
        Integer months = RequestUtil.getInteger(request, "months", 1);
        Long uin = RequestUtil.getLong(request,"uin",-1L);
        String tradeNo = RequestUtil.processParams(request, "trade_no");
        Double price = RequestUtil.getDouble(request, "price", -1d);
        String startTime = RequestUtil.processParams(request, "start_time");
        Long sTime = TimeTools.getLongMilliSecondFrom_HHMMDD(startTime)/1000;
        logger.info("topayprod:"+uin+"~"+cardId+"~"+comId+"~"+months+"~"+tradeNo+"~"+price+"~"+startTime);

        boolean isThirdPay = "1".equals(WeixinConstants.IS_TO_THIRD_WXPAY);
        if(price<=0){
            //价格小于0
            return null;
        }

        //根据车场编号找到对应的厂商平台编号和ukey
        UnionInfo unionInfo = commonComponent.getUnionInfo(comId);
        logger.info("unionInfo:"+unionInfo);
        Long unionId = unionInfo.getUnionId();
        String unionKey = unionInfo.getUnionKey();

        //组织公众号基本参数
        Map<String, Object> attachMap = new HashMap<String, Object>();
        attachMap.put("type", 11);//第三方月卡续费
        attachMap.put("params", sTime+"__"+months+"__"+tradeNo+"__"+price+"__"+comId+"__"+unionId+"__"+cardId);

        if(isThirdPay){
            // 组织第三方参数,签名
            String attach = JSONObject.toJSONString(attachMap);
            // 提供支付完成回调页面
            String backUrl = "http://"+ Constants.DOMAIN+"/zld/thirdsuccess";
            Map<String, Object> paramsMap = new HashMap<String, Object>();
            paramsMap.put("attch", attach);
            paramsMap.put("union_id", unionId);
            paramsMap.put("money", price);
            paramsMap.put("park_id", comId);
            paramsMap.put("trade_no", tradeNo);
            paramsMap.put("backurl", backUrl);
            // 获取unionKey
            String sign = CheckUtil.createSign(paramsMap, unionKey);

            String url = Constants.UNIONIP +
                    "/handlemonthpay?money="+price+"&union_id="+unionId+"&sign="+sign+"&backurl="+backUrl+"&park_id="+comId+"&trade_no="+tradeNo+"&attch="+attach;
            logger.info("topayprod:"+url);
            try {
                response.sendRedirect(url);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }else{
            //本平台微信公众号支付

            return null;
        }
    }

    /**
     * 返回支付成功页面
     * @param request
     * @return
     */
    @RequestMapping("thirdsuccess")
    public String getPaySuccessPage(HttpServletRequest request){
        logger.error("thirdsuccess 进入成功回调页面");
        String content = "月卡续费成功!系统处理可能会有延迟</br>请稍后在月卡页面查看续费情况";
        request.setAttribute("content", content);
        return "wxpublic/thirdsuccess";
    }

}
