package parkingos.com.bolink.actions;

import com.alibaba.fastjson.JSONObject;
import io.netty.channel.Channel;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import parkingos.com.bolink.beans.OrderTb;
import parkingos.com.bolink.beans.TicketTb;
import parkingos.com.bolink.component.CommonComponent;
import parkingos.com.bolink.constant.Constants;
import parkingos.com.bolink.constant.WeixinConstants;
import parkingos.com.bolink.dto.UnionInfo;
import parkingos.com.bolink.dto.WXUserView;
import parkingos.com.bolink.netty.NettyChannelMap;
import parkingos.com.bolink.service.AliPrepayService;
import parkingos.com.bolink.utlis.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * 减免劵使用
 */
@Controller
public class AliQrPrepayAction {
    Logger logger = Logger.getLogger(AliQrPrepayAction.class);
    @Autowired
    CommonComponent commonComponent;
    @Autowired
    AliPrepayService aliPrepayService;
    @Autowired
    CommonUtils commonUtils;
    /**
     * 开始用劵
     * @param request
     * @param response
     * @throws Exception
     */
    @RequestMapping("/aliprepay")
    public String startUseTicket(HttpServletRequest request, HttpServletResponse response)
            throws Exception {

        String action = RequestUtil.processParams(request, "action");
        String target = null;

        if(action.equals("useshopticket")){//ali使用商户减免券业务
            target = useShopTicket(request,response);
        }else if(action.equals("wxuseshopticket")){//微信使用商户减免券业务
            target = wxuseShopTicket(request,response);
        }else if(action.equals("toddcar")){
            target = toAddCar(request);
        }
        return target;
    }

    /**
     * 微信i使用商户减免券业务
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public String wxuseShopTicket(HttpServletRequest request,
                                   HttpServletResponse response) throws Exception{
        Long ticketId = RequestUtil.getLong(request, "ticketid", -1L);
        Long comid =  RequestUtil.getLong(request, "parkid", -1L);
        String openid =RequestUtil.processParams(request, "openid");//"oRoekt9RN8LxHDLq43QJqRhoc0t8";// "oRoekt9RN8LxHDLq43QJqRhoc0t8";//
        //上线去掉
        //String openid ="oGDN-04sKcopIa-aC8CT_xft9CSg";
        logger.error("openid"+openid);
        if(openid.equals("")){
            String code = RequestUtil.processParams(request, "code");
            String access_token_url = "https://api.weixin.qq.com/sns/oauth2/access_token?appid="+ WeixinConstants.WXPUBLIC_APPID+"&secret="+WeixinConstants.WXPUBLIC_SECRET+"&code="+code+"&grant_type=authorization_code";
            String result = WexinPublicUtil.httpsRequest(access_token_url, "GET", null);
            JSONObject map = JSONObject.parseObject(result);
            if(map == null || map.get("errcode") != null){
                String redirect_url = "http%3a%2f%2f"+ WeixinConstants.WXPUBLIC_REDIRECTURL+"%2fzld%2faliprepay%3faction%3dwxuseshopticket"
                        +"%26parkid%3d"+comid+"%26ticketid%3d"+ticketId;
                logger.error(">>>>>>>>>>>>获取openid失败....,重新获取>>>>>>>>>>>redirect_url="+redirect_url);
                String url = "https://open.weixin.qq.com/connect/oauth2/authorize?appid="
                        + WeixinConstants.WXPUBLIC_APPID
                        + "&redirect_uri="
                        + redirect_url
                        + "&response_type=code&scope=snsapi_base&state=123#wechat_redirect";
                response.sendRedirect(url);
                return null;
            }
            openid = map.getString("openid");
        }
        request.setAttribute("openid", openid);
        String carNumber =AjaxUtil.decodeUTF8(RequestUtil.getString(request, "licence"));
        WXUserView wxUserView = commonComponent.getUserinfoByOpenid(openid);
        Long uin = wxUserView.getUin();
        Integer result = 0;
        if(!Check.isEmpty(carNumber)){
            result = commonComponent.addCarnumber(uin, carNumber);
            logger.error("add car:"+result);
        }else {
            Integer bindflag = wxUserView.getBindflag();
            //获取车主车牌号
            carNumber = aliPrepayService.getCarNumber(uin,bindflag);
        }
        if(Check.isEmpty(carNumber)){
            request.setAttribute("from", "useticket");
            request.setAttribute("furl", "aliprepay.do");
            request.setAttribute("ticketid", ticketId);
            request.setAttribute("comid", comid);
            request.setAttribute("action", "wxuseshopticket");
            return "aliprepay/carnumber";
        }
        return handelPrepay(request, response, ticketId, carNumber, comid,"wxuseshopticket");
    }

    /**
     * ali使用商户减免券业务
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    private String useShopTicket(HttpServletRequest request,
                                 HttpServletResponse response) throws Exception {
        Long ticketId = RequestUtil.getLong(request, "ticketid", -1L);
        //取车牌查订单
        String carNumber =AjaxUtil.decodeUTF8(RequestUtil.getString(request, "licence"));
        Long comid =  RequestUtil.getLong(request, "parkid", -1L);
        if(Check.isEmpty(carNumber)){
            Cookie[] cookies = request.getCookies();//这样便可以获取一个cookie数组
            if(cookies!=null){
                for(Cookie cookie : cookies){
                    if(cookie!=null&&cookie.getName().equals("lience"))
                        carNumber = AjaxUtil.decodeUTF8(cookie.getValue());
                }
            }
        }else {
            Cookie  cookie = new Cookie("lience",AjaxUtil.encodeUTF8(carNumber));
            cookie.setMaxAge(8640000);//暂定100天
            //设置路径，这个路径即该工程下都可以访问该cookie 如果不设置路径，那么只有设置该cookie路径及其子路径可以访问
            cookie.setPath("/");
            response.addCookie(cookie);
            logger.error("已保存到cookie,lience="+carNumber);
        }
        if(Check.isEmpty(carNumber)){//没有车牌，到添加车牌页面
            request.setAttribute("from", "useticket");
            request.setAttribute("furl", "aliprepay.do");
            request.setAttribute("ticketid", ticketId);
            request.setAttribute("comid", comid);
            request.setAttribute("action", "useshopticket");
            return "aliprepay/carnumber";
        }
        return handelPrepay(request, response, ticketId, carNumber, comid,"useshopticket");
    }
    /**
     * 处理订单减免和预付
     * @param request
     * @param response
     * @param ticketId 减免券编号
     * @param carNumber 车牌号
     * @param comid 车场编号
     * @return
     * @throws Exception
     */
    private String handelPrepay(HttpServletRequest request,HttpServletResponse response,
                                Long ticketId,String carNumber,Long comid,String action) throws Exception{

        //1 查询在场订单
        request.setAttribute("ticketid", ticketId);
        request.setAttribute("parkid", comid);
        request.setAttribute("carnumber", carNumber);
        request.setAttribute("forward",action);
        OrderTb unpayOrder = aliPrepayService.qryUnpayOrder(carNumber,comid);
        if(unpayOrder==null){
            request.setAttribute("noorder", 1);
            request.setAttribute("error", "车辆未入场");
            return "aliprepay/prepay";
        }
        //2 判断减免券是否可用
        Integer count = aliPrepayService.unpayOrderForTicket(unpayOrder.getId());
        logger.error("是否已使用过券："+count);
        //查询券的类型
        Integer ticketType = -1;
        BigDecimal money = new BigDecimal("0");
        Integer duration = 0;
        TicketTb ticketTb =null;
        String error="车场网络异常，请稍候重试";
        if(ticketId!=-1){
            if(count<1){
                ticketTb= aliPrepayService.qryTicket(ticketId);

                if(ticketTb!=null){
                    //判断有效期
                    long limit_day = ticketTb.getLimitDay();
                    if(System.currentTimeMillis()/1000 > limit_day){
                        error="该优惠券已过期";
                        logger.error("该优惠券已过期");
                    }else{
                        Integer state = ticketTb.getState();
                        if(state!=null){
                            if(state==0){
                                Integer type = ticketTb.getType();
                                if(type!=null){
                                    if(type==3){//3减时券
                                        ticketType=1;
                                        duration = ticketTb.getMoney();
                                    }else if(type==5){//减免金额劵
                                        ticketType=0;
                                        money = ticketTb.getUmoney();
                                        logger.error("该优惠劵："+ticketId+",money："+money);
                                    }else {
                                        error="优惠券无效";
                                        logger.error("优惠券无效");
                                    }
                                }else {
                                    error="优惠券无效";
                                    logger.error("优惠券无效");
                                }
                            }else if(state==2){
                                error="该优惠券已过期";
                                logger.error("该优惠券已过期");
                            }else if(state==1){
                                error="该优惠券已使用";
                                logger.error("该优惠券已使用");
                            }
                        }else {
                            error="该优惠券已使用";
                            logger.error("该优惠券已使用");
                        }
                    }
                }else{
                    error="优惠券不存在";
                    logger.error("该优惠券不存在");
                }
            }else {
                error="本次停车已使用过优惠券";
                logger.error("本次停车已使用过优惠券");
            }
        }else {
            error="优惠券不存在";
            logger.error("该优惠券不存在");
        }
        //3 下发减免劵
        boolean isSend = false;//是否已发送的SDK
        if(ticketType!=-1){//取到了券信息并且有通道，发送消息到sdk
            Channel channel = NettyChannelMap.get(commonUtils.getChannel(ticketTb.getComid()+""));
            if(channel!=null){
                Map<String, Object> messageMap = new HashMap<String, Object>();
                messageMap.put("service_name", "deliver_ticket");
                messageMap.put("ticket_id", ticketId+"");
                messageMap.put("create_time", ticketTb.getCreateTime());
                messageMap.put("limit_day", ticketTb.getLimitDay());
                messageMap.put("duration", duration);
                messageMap.put("money", money+"");
                messageMap.put("car_number", carNumber);
                messageMap.put("ticket_type", ticketType);
                messageMap.put("order_id", unpayOrder.getOrderIdLocal());
                messageMap.put("remark", ticketTb.getRemark());
                String mesg = StringUtils.createJson(messageMap);
                isSend=commonUtils.doBackMessage(mesg, channel);
                logger.error("发送减免券数据到SDK："+mesg+",ret:"+isSend);
            }
        }
        // 4 跳转到泊链支付
        if(isSend){
            //根据车场编号找到对应的厂商编号
            UnionInfo unionInfo = commonComponent.getUnionInfo(comid);
            Long unionid = unionInfo.getUnionId();
            String url = Constants.UNIONIP +
                    "thirdpay?comid="+comid+"&unionid="+unionid+"&orderid="+unpayOrder.getOrderIdLocal();
            logger.error("去泊链平台查询使用减免劵的订单信息："+url);
            response.sendRedirect(url);
            return null;
            //return "redirec:/"+url;
        }else {
            request.setAttribute("noorder", -1);
            request.setAttribute("error", error);
            return "aliprepay/prepay";
        }
    }
    private String toAddCar(HttpServletRequest request) {
        Long ticketId = RequestUtil.getLong(request, "ticketid", -1L);
        Long comid =  RequestUtil.getLong(request, "parkid", -1L);
        String forward =RequestUtil.getString(request, "forward");
        String openid =RequestUtil.getString(request, "openid");
        request.setAttribute("from", "useticket");
        request.setAttribute("furl", "aliprepay.do");
        request.setAttribute("ticketid", ticketId);
        request.setAttribute("comid", comid);
        request.setAttribute("openid", openid);
        request.setAttribute("action", forward);
        return "aliprepay/carnumber";
    }
}
