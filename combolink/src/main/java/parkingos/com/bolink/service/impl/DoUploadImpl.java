package parkingos.com.bolink.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.zld.common_dao.dao.CommonDao;
import com.zld.common_dao.qo.PageOrderConfig;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import parkingos.com.bolink.beans.*;
import parkingos.com.bolink.service.DoUpload;
import parkingos.com.bolink.utlis.Check;
import parkingos.com.bolink.utlis.StringUtils;
import parkingos.com.bolink.utlis.TempDataUtil;

import java.math.BigDecimal;
import java.net.Inet4Address;
import java.util.List;
import java.util.Map;
import java.util.UUID;

//
@Service
public class DoUploadImpl implements DoUpload{


    private Logger logger = Logger.getLogger(DoUploadImpl.class);
    @Autowired
    public CommonDao commonDao;


    @Override
    public String checkTokenSign(String token,String sign,String data){
        ParkTokenTb tokenTb = new ParkTokenTb();
        tokenTb.setToken(token);

        tokenTb = (ParkTokenTb)commonDao.selectObjectByConditions(tokenTb);
        logger.info(tokenTb.toString());
        String result ="";
        String parkId = "";
        if(tokenTb!=null)
            parkId= tokenTb.getParkId();
        if(!Check.isEmpty(parkId)){
            if(Check.isLong(parkId)){
                result = parkId;
                ComInfoTb comInfoTb = new ComInfoTb();
                comInfoTb.setId(Long.valueOf(parkId));
                comInfoTb = (ComInfoTb)commonDao.selectObjectByConditions(comInfoTb);
                logger.info(comInfoTb);
//                Map<String, Object> comInfoMap = daService.getMap("select * from com_info_tb where id=?",
//                        new Object[] { Long.valueOf(parkId)});
                if (comInfoTb != null) {
                    String ukey = comInfoTb.getUkey();//String.valueOf(comInfoMap.get("ukey"));
                    String strKey = data+ "key="+ ukey;
                    try {
                        String _sign = StringUtils.MD5(strKey,"utf-8").toUpperCase();
                        logger.error(strKey + "," + _sign + ":" + sign
                                + ",ret:" + sign.equals(_sign));
//						if (!sign.equals(preSign)) {
//							result = "error:签名错误";
//						}
                    } catch (Exception e) {
                        e.printStackTrace();
                        result = "error:md5加密出现异常,请联系后台管理员！！！";
                    }
                }else{
                    result = "error:车场编号异常，未成功在云平台注册";
                }
            }else {
                result = "error:车场没有登录";
            }
        }
        return result;
    }

    @Override
    public String doLogin(String orgdata,String preSign,String sourceIP){
        logger.info(orgdata);
        JSONObject data = JSONObject.parseObject(orgdata);
        String logStr = "tcp park login ";
        logger.info(data);
        String token = "";
        String parkId = data.getString("park_id");
        String localId = "";
        if(data.containsKey("local_id"))//多终端电脑登录
            localId = data.getString("local_id");
        Long comid = Long.valueOf(parkId);
        //查询出车场具体信息
        ComInfoTb infoTb = new ComInfoTb();
        infoTb.setId(comid);
        infoTb = (ComInfoTb)commonDao.selectObjectByConditions(infoTb);

        if(infoTb!=null){
            String strKey = orgdata+"key="+infoTb.getUkey();
            String sign=null;
            try {
                sign = StringUtils.MD5(strKey,"utf-8").toUpperCase();
            } catch (Exception e) {
                e.printStackTrace();
                logger.error("md5加密出现异常,请联系后台程序员。");
            }

            logger.error(strKey+","+sign+":"+preSign+",ret:"+sign.equals(preSign));
            sign = preSign;
            if(sign.equals(preSign)){
                token = UUID.randomUUID().toString().replace("-", "");
                ParkTokenTb params= new ParkTokenTb();
                params.setParkId(parkId);
                params.setLocalId(localId);
                int count = commonDao.selectCountByConditions(params);

                int r = 0;
                String serverIP = "";//车场登录服务器IP,集群时，要记录下服务器IP,后台或接口推下行数据时，要到相应的服务器上推送
                try {
                    serverIP = Inet4Address.getLocalHost().getHostAddress().toString();
                } catch (Exception e) {
                    logger.error("取IP错误...");
                }
                if(count==0){
                    //自动生成主键id
                    ParkTokenTb tokenTb = new ParkTokenTb();
                    tokenTb.setParkId(parkId);
                    tokenTb.setToken(token);
                    tokenTb.setLoginTime(System.currentTimeMillis()/1000);
                    tokenTb.setServerIp(serverIP);
                    tokenTb.setSourceIp(sourceIP);
                    tokenTb.setLocalId(localId);
                    r = commonDao.insert(tokenTb);
                    logger.info("tcp login park:"+comid+",token"+token+",login result:"+r);

                }else {

                    ParkTokenTb tokenTb = new ParkTokenTb();
                    tokenTb.setToken(token);
                    tokenTb.setLoginTime(System.currentTimeMillis()/1000);
                    tokenTb.setServerIp(serverIP);
                    tokenTb.setLocalId(localId);
                    tokenTb.setSourceIp(sourceIP);
                    ParkTokenTb conditions = new ParkTokenTb();
                    conditions.setParkId(parkId);
                    conditions.setLocalId(localId);
                    r= commonDao.updateByConditions(tokenTb,conditions);

//                    r = daService.update(sql, params);
                    logger.info("tcp login , relogon,park:"+comid+",token"+token+",result:"+r);
                }
                if(r>=1){
                    logger.error(logStr+"error:登录成功");
                    return token;
                }else {
                    logger.error(logStr+"error:登录失败");
                    return "error:登录失败";
                }
            }else {
                logger.error(logStr+"error:车场签名错误");
                return "error:签名错误";
            }
        }else {
            logger.error(logStr+"error:车场不存在");
            return "error:车场不存在";
        }
    }
    @Override
    public String logout(String parkId,String sourceIp){
        String pid = parkId;
        String localId  = null;
        if(parkId!=null&&parkId.contains("_")){
            pid = parkId.split("_")[0];
            localId = parkId.substring(pid.length()+1);
        }
        ParkTokenTb tokenTb = new ParkTokenTb();
        tokenTb.setParkId(pid);
        tokenTb.setLocalId(localId);
        tokenTb.setSourceIp(sourceIp);
        int r = commonDao.deleteByConditions(tokenTb);//.deleteBySelective(tokenTb);
       /* String sql = "delete from park_token_tb where park_id= ? ";
        Object[] values = new Object[]{parkId};
        if(localId!=null){
            sql = sql +" and local_id=? ";
            values = new Object[]{pid,localId};
        }
        int r =daService.update(sql,values);*/
        logger.error("退出登录,"+parkId+",ret:"+r);
        return ""+r;
    }

    @Override
    public String inPark(JSONObject object) {
//        JSONObject object = JSONObject.parseObject(data);
        logger.info(object);
        /*String errmsg = "";
        Map<String, Object> params = new HashMap<String, Object>();
        logger.info(params);
        for (String key : object.keySet()) {
            Object value = object.get(key);
            if(!Check.isEmpty(value+"")){
                params.put(StringUtils.underline2Camel(key), value);
            }
        }
        String uid =object.getString("uid");
        Long comid = object.getLong("comid");
        Long userid =getUserId(uid,comid);
        if(userid==-1){
            errmsg ="收费员不存在";
        }
        object.put("uid",userid);
        logger.info(params);
        object = (JSONObject) JSON.toJSON(params);
//        object.put("comid",parkId);inChannelId
        String carType = object.getString("carType");
        Long carTypeId = getCarType(carType,comid);
        if(carTypeId!=-1){
            object.put("carType",carTypeId);
        }
        object.put("uin","-1");
        object.put("createTime",object.get("inTime"));
        object.put("orderIdLocal",object.get("orderId"));
        String inChannelId = object.getString("inChannelId");
        Long channelId = getComPass(inChannelId,comid);
        if(channelId!=-1)
            object.put("inPassid",channelId);
        else
            object.put("inPassid",object.get("inChannelId"));
        logger.info(object);*/
        OrderTb orderTb = setOrder(object);// JSON.parseObject(object.toJSONString(),OrderTb.class);
//        logger.info(orderTb);



        OrderTb con = new OrderTb();
        con.setComid(Long.valueOf(object.getString("comid")));
        con.setCarNumber(orderTb.getCarNumber());
        con.setOrderIdLocal(orderTb.getOrderIdLocal());
        con.setState(0);
        //查询是否已入场
        int count = commonDao.selectCountByConditions(con);
        OrderTb fields = new OrderTb();
        fields.setEndTime(System.currentTimeMillis()/1000);
        fields.setTotal(new BigDecimal(0.0));
        fields.setOutUid(orderTb.getOutUid());
        fields.setState(1);
        con.setOrderIdLocal(null);
        //0元结算掉所有车场编号、车牌号一致、未结算的订单
        int update = commonDao.updateByConditions(fields,con);
        logger.error("update order :"+update);
        //写入新订单
        int r=0;
        if(count>0){
            con.setOrderIdLocal(orderTb.getOrderIdLocal());
            orderTb.setState(0);
            con.setState(null);
            r = commonDao.updateByConditions(orderTb,con);
            logger.info("update order:,con:"+con+",order:"+orderTb);
        }else{
            Long newId = commonDao.selectSequence(OrderTb.class);
            orderTb.setId(newId);
            logger.info("insert order :"+orderTb);
            r= commonDao.insert(orderTb);
        }
        String orderId = orderTb.getOrderIdLocal();
        object.clear();
        object.put("service_name","in_park");
        object.put("state",r);
        object.put("errmsg","");
        object.put("order_id",orderId);
        updateParkPlot(object.getInteger("empty_plot"),object.getLong("comid"));
        return object.toJSONString();
    }

    @Override
    public String uploadCollector(JSONObject jsonData) {
        Long comid = jsonData.getLong("comid");
        String userId = jsonData.getString("user_id");
        UserInfoTb userInfoTb = new UserInfoTb();
        userInfoTb.setNickname(jsonData.getString("name"));
        userInfoTb.setUserId(userId);
        userInfoTb.setRegTime(jsonData.getLong("create_time"));
        userInfoTb.setUpdateTime(jsonData.getLong("update_time"));
        userInfoTb.setSex(jsonData.getLong("sex"));
        Integer operate = jsonData.getInteger("operate_type");
        operate=operate==null?0:operate;
        userInfoTb.setComid(comid);
        userInfoTb.setAuthFlag(2L);
        userInfoTb.setStrid(jsonData.getString("user_id"));
        userInfoTb.setPassword(jsonData.getString("user_id"));
        userInfoTb.setPassword("135246");
        userInfoTb.setRoleId(30L);
        //取集团编号and cityid
        ComInfoTb comInfoTb = new ComInfoTb();
        comInfoTb.setId(comid);
        comInfoTb = (ComInfoTb)commonDao.selectObjectByConditions(comInfoTb);
        if(comInfoTb!=null){
            if(comInfoTb.getGroupid()!=null)
                userInfoTb.setGroupid(comInfoTb.getGroupid());
            if(comInfoTb.getCityid()!=null)
                userInfoTb.setCityid(comInfoTb.getCityid());
        }
        logger.info(operate);
        logger.info(userInfoTb.toString());
        //检查是否已存在userid的收费员
        UserInfoTb con = new UserInfoTb();
        con.setComid(userInfoTb.getComid());
        con.setUserId(userId);
        int count = commonDao.selectCountByConditions(con);
        int r;
        if(operate==1&&count<1){//新增收费员
            r = commonDao.insert(userInfoTb);
        }else{
            if(operate==3){//删除
                UserInfoTb fields = new UserInfoTb();
                fields.setState(1);
                r= commonDao.updateByConditions(fields,con);//commonDao.deleteByConditions(con);
            }else{//更新
                UserInfoTb conditions = new UserInfoTb();
                conditions.setUserId(userId);
                conditions.setComid(userInfoTb.getComid());
                userInfoTb.setUserId(null);
                userInfoTb.setPassword(null);
                userInfoTb.setNickname(null);
                r = commonDao.updateByConditions(userInfoTb,conditions);
            }
        }
        jsonData.clear();
        jsonData.put("service_name","upload_collector");
        jsonData.put("state",r);
        jsonData.put("errmsg","");
        jsonData.put("user_id",userId);
        return jsonData.toJSONString();
    }

    @Override
    public String workRecord(JSONObject jsonData) {
        String errmsg="";
        ParkuserWorkRecordTb recordTb = new ParkuserWorkRecordTb();
        String userId = jsonData.getString("user_id");
        Long comId = jsonData.getLong("comid");
        String uuid = jsonData.getString("uuid");
        Long uid =getUserId(userId,comId);
        if(uid==-1){
            errmsg ="收费员不存在";
        }
        recordTb.setUid(uid);
        recordTb.setUuid(uuid);
        recordTb.setParkId(comId);
        //检查是否已存在记录
        int count = commonDao.selectCountByConditions(recordTb);

        Long startTime = jsonData.getLong("start_time");
        Long endTime = jsonData.getLong("end_time");
        Long wordsiteId = jsonData.getLong("worksite_id");
        recordTb.setStartTime(startTime);
        recordTb.setEndTime(endTime);
        recordTb.setWorksiteId(wordsiteId);
        recordTb.setState(jsonData.getInteger("state"));
        int r = 0;
        if(count<1){//不存在时新建
            r = commonDao.insert(recordTb);
        }else{//已存在时，根据uuid更新
            ParkuserWorkRecordTb con = new ParkuserWorkRecordTb();;
            con.setUuid(uuid);
            r = commonDao.updateByConditions(recordTb,con);
        }
        jsonData.clear();
        jsonData.put("service_name","work_record");
        jsonData.put("state",r);
        jsonData.put("uuid",uuid);
        jsonData.put("errmsg",errmsg);
        return jsonData.toJSONString();

    }

    @Override
    public String uploadLiftrod(JSONObject jsonData) {
        String errmsg="";
        String userId = jsonData.getString("user_id");
        Long comId = jsonData.getLong("comid");
        String liftrodId = jsonData.getString("liftrod_id");
        Long uid = getUserId(userId,comId);
        if(uid==-1){
            errmsg="收费员不存在";
        }
        LiftRodTb liftRodTb = new LiftRodTb();
        liftRodTb.setLiftrodId(liftrodId);
        liftRodTb.setComid(comId);
        //查询是否已存在记录
        int count = commonDao.selectCountByConditions(liftRodTb);

        liftRodTb.setCtime(jsonData.getLong("create_time"));
        liftRodTb.setUpdateTime(jsonData.getLong("update_time"));
        liftRodTb.setReason(jsonData.getInteger("reason"));
        liftRodTb.setCarNumber(jsonData.getString("car_number"));
        liftRodTb.setOutChannelId(jsonData.getString("channel_id"));
        liftRodTb.setOrderId(jsonData.getString("order_id"));
        liftRodTb.setResume(jsonData.getString("resume"));
        liftRodTb.setComid(jsonData.getLong("comid"));
        liftRodTb.setUin(uid);
        int r = 0;
        if(count>0){//
            LiftRodTb con = new LiftRodTb();
            con.setLiftrodId(liftrodId);
            con.setComid(comId);
            r = commonDao.updateByConditions(liftRodTb,con);
        }else{
            r = commonDao.insert(liftRodTb);
        }
        jsonData.clear();
        jsonData.put("service_name","upload_liftrod");
        jsonData.put("state",r);
        jsonData.put("errmsg",errmsg);
        jsonData.put("liftrod_id",liftrodId);
        return jsonData.toJSONString();
    }
    @Override
    public String uploadGate(JSONObject jsonData) {
        ComPassTb passTb = new ComPassTb();
        String channelId = jsonData.getString("channel_id");
        passTb.setChannelId(channelId);
        passTb.setComid(jsonData.getLong("comid"));
        Integer operateType = jsonData.getInteger("operate_type");
        String  passPtye = jsonData.getString("passtype");
        if(passPtye!=null){
            passPtye = passPtye.equals("0")?"1":"0";
        }
        //查询是否已上传过
        int count = commonDao.selectCountByConditions(passTb);
        int ret = 0;
        if(count>0){
            ComPassTb fields = new ComPassTb();
            fields.setState(1);
            if(operateType==3) {//删除
                ret = commonDao.updateByConditions(fields,passTb);
            }else{//更新
                fields.setState(0);
                fields.setPassname(jsonData.getString("passname"));
                fields.setPasstype(passPtye);
                fields.setWorksiteId(jsonData.getLong("worksite_id"));
                ret = commonDao.updateByConditions(fields,passTb);
            }
        }else{//插入
            passTb.setPassname(jsonData.getString("passname"));
            passTb.setPasstype(passPtye);
            passTb.setWorksiteId(jsonData.getLong("worksite_id"));

            ret = commonDao.insert(passTb);
        }
        jsonData.clear();
        jsonData.put("service_name","upload_gate");
        jsonData.put("state",ret);
        jsonData.put("errmsg","上传成功");
        jsonData.put("channel_id",channelId);
        return jsonData.toJSONString();
    }

    @Override
    public String uploadMonthMember(JSONObject jsonData) {
        CarowerProduct product = new CarowerProduct();
        String cardId = jsonData.getString("card_id");
        //1添加2修改3删除
        Integer operateType = jsonData.getInteger("operate_type");
        product.setCardId(cardId);
        Long comId = jsonData.getLong("comid");
        product.setComId(comId);
        int count = commonDao.selectCountByConditions(product);
        int ret = 0;
        if(count>0){
            if(operateType==3){//删除
                CarowerProduct fields = new CarowerProduct();
                fields.setIsDelete(1L);//is_delete 0正常1删除
                ret = commonDao.updateByConditions(fields,product);
            }else{//更新
                operateType=2;
            }
        }
        if(operateType!=3){
            product.setCreateTime(jsonData.getLong("create_time"));
            product.setUpdateTime(jsonData.getLong("update_time"));
            product.setbTime(jsonData.getLong("begin_time"));
            product.seteTime(jsonData.getLong("end_time"));

            if(jsonData.getString("pid")!=null){
                ProductPackageTb productPackageTb = new ProductPackageTb();
                productPackageTb.setComid(comId);
                productPackageTb.setCardId(jsonData.getString("pid"));
                productPackageTb = (ProductPackageTb)commonDao.selectObjectByConditions(productPackageTb);
                if(productPackageTb!=null)
                    product.setPid(productPackageTb.getId());
            }
            if(jsonData.getString("car_type")!=null){
                CarTypeTb carTypeTb = new CarTypeTb();
                carTypeTb.setComid(comId);
                carTypeTb.setCartypeId(jsonData.getString("car_type"));
                carTypeTb = (CarTypeTb)commonDao.selectObjectByConditions(carTypeTb);
                if(carTypeTb!=null)
                    product.setCarTypeId(carTypeTb.getId());
            }


            product.setCarNumber(jsonData.getString("car_number"));
            product.setTotal(jsonData.getBigDecimal("price"));
            if(jsonData.containsKey("amount_receivable")) {
                product.setActTotal(jsonData.getBigDecimal("amount_receivable"));
            }else{
                product.setActTotal(jsonData.getBigDecimal("price"));
            }
            product.setName(jsonData.getString("name"));
            product.setRemark(jsonData.getString("remark"));
            product.setAddress(jsonData.getString("address"));
            product.setMobile(jsonData.getString("tel"));
            product.setLimitDayType(jsonData.getInteger("limit_day_type"));
            product.setpLot(jsonData.getString("p_lot"));
            product.setIsDelete(0L);
            if(operateType==1){//添加
                ret = commonDao.insert(product);
            }else if(operateType==2){//更新
                CarowerProduct con = new CarowerProduct();
                con.setCardId(cardId);
                con.setComId(comId);
                ret = commonDao.updateByConditions(product,con);
            }
        }
        jsonData.clear();
        jsonData.put("service_name","upload_month_member");
        jsonData.put("state",ret);
        jsonData.put("errmsg","");
        jsonData.put("card_id",cardId);
        return jsonData.toJSONString();
    }

    @Override
    public String outPark(JSONObject jsonData) {
        String errmsg="";
        logger.info("handel out park>>>>"+jsonData);
       /* Map<String, Object> params = new HashMap<String, Object>();
//        logger.info(params);
        for (String key : jsonData.keySet()) {
            Object value = jsonData.get(key);
            if(!Check.isEmpty(value+"")){
                params.put(StringUtils.underline2Camel(key), value);
            }
        }
        logger.info(params);
        jsonData = (JSONObject) JSON.toJSON(params);
//        object.put("comid",parkId);
        String payType = jsonData.getString("payType");
        if(payType.equals("cash")){
            jsonData.put("payType",1);
        }else if(payType.equals("monthuser")){
            jsonData.put("payType",3);
        }else if(payType.equals("wallet")||payType.equals("sweepcode")){
            jsonData.put("payType",2);
        }else {
            jsonData.put("payType",8);
        }
        String uid =jsonData.getString("uid");
        Long comid = jsonData.getLong("comid");
        Long userid = getUserId(uid,comid);
        if(userid==-1){
            errmsg ="入场收费员不存在";
        }
        jsonData.put("uid",userid);
        String outUid =jsonData.getString("out_uid");
        Long outuserid =getUserId(outUid,comid);
        if(outuserid==-1){
           errmsg+="出场收费员不存在";
        }
        jsonData.put("outUid",outuserid);
        jsonData.put("uin","-1");
        jsonData.put("createTime",jsonData.get("inTime"));
        jsonData.put("orderIdLocal",jsonData.get("orderId"));
        jsonData.put("endTime",jsonData.get("outTime"));

        jsonData.put("state",1);
        String outChannelId = jsonData.getString("outChannelId");
        Long channeId = getComPass(outChannelId,comid);
        if(channeId!=-1){
            jsonData.put("outPassid",channeId);
        }else{
            jsonData.put("outPassid",outChannelId);
        }
        String inChannelId = jsonData.getString("inChannelId");
        Long inchanneId = getComPass(inChannelId,comid);
        if(inchanneId!=-1){
            jsonData.put("inPassid",inchanneId);
        }else{
            jsonData.put("inPassid",inChannelId);
        }
        String carType = jsonData.getString("carType");
        Long carTypeId = getCarType(carType,comid);
        if(carTypeId!=-1){
            jsonData.put("carType",carTypeId);
        }
        logger.info(jsonData);*/
        OrderTb orderTb = setOrder(jsonData);//JSON.parseObject(jsonData.toJSONString(),OrderTb.class);
        orderTb.setState(1);
        // logger.info(orderTb);
        OrderTb con = new OrderTb();
        con.setCarNumber(orderTb.getCarNumber());
        con.setComid(orderTb.getComid());
        con.setCreateTime(orderTb.getCreateTime());
        con.setOrderIdLocal(orderTb.getOrderIdLocal());
        OrderTb order =(OrderTb)commonDao.selectObjectByConditions(con);
        int r =0;
        if(order!=null&&order.getId()!=null){
            r = commonDao.updateByConditions(orderTb,con);
            orderTb.setId(order.getId());
            writeToAccount(orderTb);
        }
//        Long comid = jsonData.getLong("comid");
        Integer emplyPlot = jsonData.getInteger("empty_plot");
        jsonData.clear();
        jsonData.put("service_name","out_park");
        jsonData.put("state",r);
        jsonData.put("order_id",orderTb.getOrderIdLocal());
        jsonData.put("errmsg",errmsg);
        updateParkPlot(emplyPlot,jsonData.getLong("comid"));
        return jsonData.toJSONString();
    }

    @Override
    public String uploadOrder(JSONObject jsonData) {
        String errmsg ="";
        logger.info(jsonData);
        String orderId = jsonData.getString("order_id");
        Long comid = jsonData.getLong("comid");
        OrderTb orderTb = setOrder(jsonData) ;//new OrderTb();
        OrderTb con = new OrderTb();
        con.setOrderIdLocal(orderId);
        con.setComid(comid);
        con.setState(0);
        OrderTb order =(OrderTb)commonDao.selectObjectByConditions(con);
        int ret = 0;
        orderTb.setState(1);
        if(order!=null&&order.getId()!=null){
            con.setOrderIdLocal(orderId);
            con.setComid(comid);
            ret = commonDao.updateByConditions(orderTb,con);
            orderTb.setId(order.getId());
        }else{
            Long newId = commonDao.selectSequence(OrderTb.class);
            orderTb.setId(newId);
            ret = commonDao.insert(orderTb);
        }
        Integer emptyPlot = jsonData.getInteger("empty_plot");
        jsonData.clear();
        jsonData.put("service_name","upload_order");
        jsonData.put("state",ret);
        jsonData.put("order_id",orderId);
        jsonData.put("errmsg",errmsg);
        updateParkPlot(emptyPlot,comid);
        if(ret>0)
            writeToAccount(orderTb);
        return jsonData.toJSONString();
    }

    @Override
    public String carTypeUpload(JSONObject jsonData) {
        CarTypeTb typeTb = new CarTypeTb();
        String cartypeId = jsonData.getString("car_type_id");
        Integer operateType = jsonData.getInteger("operate_type");
        typeTb.setCartypeId(cartypeId);
        Long comid = jsonData.getLong("comid");
        typeTb.setComid(comid);
        typeTb.setCartypeId(cartypeId);
        int count = commonDao.selectCountByConditions(typeTb);
        String name = jsonData.getString("name");
        typeTb.setName(name);
        int ret = 0;
        typeTb.setCreateTime(jsonData.getLong("create_time"));
        typeTb.setUpdateTime(jsonData.getLong("update_time"));
        typeTb.setSort(jsonData.getInteger("sort"));
        typeTb.setIsDelete(0);
        if(count>0){
            if(operateType==3){//删除
                CarTypeTb fields = new CarTypeTb();
                fields.setIsDelete(1);
                CarTypeTb con = new CarTypeTb();
                con.setCartypeId(cartypeId);
                con.setComid(comid);
                ret = commonDao.updateByConditions(fields,con);
            }else{//更新
                CarTypeTb con = new CarTypeTb();
                con.setCartypeId(cartypeId);
                con.setComid(comid);
                ret = commonDao.updateByConditions(typeTb,con);
            }

        }else{//新建
            ret = commonDao.insert(typeTb);
        }
        jsonData.clear();
        jsonData.put("service_name","car_type_upload");
        jsonData.put("state",ret);
        jsonData.put("cartype_id",cartypeId);
        jsonData.put("errmsg","");
        return jsonData.toJSONString();
    }


    @Override
    public String uploadMonthCard(JSONObject jsonData) {
        String packageId = jsonData.getString("package_id");
        ProductPackageTb packageTb = new ProductPackageTb();
        packageTb.setCardId(packageId);
        packageTb.setComid(jsonData.getLong("comid"));
        Integer operateType = jsonData.getInteger("operate_type");
        int count = commonDao.selectCountByConditions(packageTb);
        int ret = 0;
        if(count>0){
            if(operateType==3){
                ProductPackageTb fields = new ProductPackageTb();
                fields.setIsDelete(1L);
                ret =  commonDao.updateByConditions(fields,packageTb);
            }else{
                operateType =2;
            }
        }
        if(operateType!=3){
            packageTb.setCreateTime(jsonData.getLong("create_time"));
            packageTb.setUpdateTime(jsonData.getLong("update_time"));
            packageTb.setDescribe(jsonData.getString("describe"));
            packageTb.setPrice(jsonData.getBigDecimal("price"));
            packageTb.setpName(jsonData.getString("name"));

            String carTypeId = jsonData.getString("car_type");
            CarTypeTb carTypeTb = new CarTypeTb();
            carTypeTb.setCartypeId(carTypeId);
            carTypeTb.setComid(jsonData.getLong("comid"));
            carTypeTb = (CarTypeTb)commonDao.selectObjectByConditions(carTypeTb);
            if(carTypeTb!=null){
                packageTb.setCarTypeId(carTypeTb.getId()+"");
            }

            packageTb.setPeriod(jsonData.getString("period"));
            packageTb.setPrice(jsonData.getBigDecimal("price"));
            packageTb.setIsDelete(0L);
            if(operateType==1){
                ret = commonDao.insert(packageTb);
            }else if(operateType==2){
                ProductPackageTb con = new ProductPackageTb();
                con.setCardId(packageId);
                con.setComid(jsonData.getLong("comid"));
                ret = commonDao.updateByConditions(packageTb,con);
            }
        }
        jsonData.clear();
        jsonData.put("service_name","upload_month_card");
        jsonData.put("state",ret);
        jsonData.put("package_id",packageId);
        jsonData.put("errmsg","");
        return jsonData.toJSONString();
    }

    @Override
    public String monthPayRecord(JSONObject jsonData) {
        String cardId = jsonData.getString("card_id");
        String comId = jsonData.getString("comid");
        CardRenewTb renewTb = new CardRenewTb();
        renewTb.setComid(comId);
        renewTb.setCardId(cardId);
        int count = 0;//commonDao.selectCountByConditions(renewTb);
        renewTb.setPayTime(jsonData.getInteger("pay_time"));
        renewTb.setAmountReceivable(jsonData.getString("amount_receivable"));
        renewTb.setAmountPay(jsonData.getString("amount_pay"));
        renewTb.setPayType(jsonData.getString("pay_type"));
        renewTb.setCollector(jsonData.getString("collector"));
        renewTb.setBuyMonth(jsonData.getInteger("buy_month"));
        renewTb.setCarNumber(jsonData.getString("car_number"));
        renewTb.setUserId(jsonData.getString("user_id"));
        renewTb.setResume(jsonData.getString("resume"));
        renewTb.setLimitTime(jsonData.getLong("limit_time"));
        int ret = 0;
        if(count>0){
            CardRenewTb con = new CardRenewTb();
            con.setComid(comId);
            con.setCardId(cardId);
            ret = commonDao.updateByConditions(renewTb,con);
        }else{
            ret = commonDao.insert(renewTb);
        }
        jsonData.clear();
        jsonData.put("service_name","month_pay_record");
        jsonData.put("state",ret);
        jsonData.put("card_id",cardId);
        jsonData.put("errmsg","");
        return jsonData.toJSONString();
    }

    @Override
    public String blackUpload(JSONObject jsonData) {
        String blackUUID = jsonData.getString("black_uuid");
        Integer operateType = jsonData.getInteger("operate_type");
        ZldBlackTb blackTb = new ZldBlackTb();
        blackTb.setBlackUuid(blackUUID);
        blackTb.setComid(jsonData.getLong("comid"));
        logger.info(jsonData);
        int count = commonDao.selectCountByConditions(blackTb);
        int ret = 0;
        if(count>0){
            if(operateType==3) {//删除
                ZldBlackTb fields = new ZldBlackTb();
                fields.setState(1);
                ret = commonDao.updateByConditions(fields,blackTb);
            }else
                operateType =2;
        }
        if(operateType!=3){
            blackTb.setCarNumber(jsonData.getString("car_number"));
            blackTb.setCtime(jsonData.getLong("create_time"));
            blackTb.setRemark(jsonData.getString("resume"));
            blackTb.setOperator(jsonData.getString("operator"));
            blackTb.setState(0);
            if(operateType==1){
                ret = commonDao.insert(blackTb);
            }else if(operateType==2){
                ZldBlackTb con = new ZldBlackTb();
                con.setBlackUuid(blackUUID);
                ret = commonDao.updateByConditions(blackTb,con);
            }
        }
        jsonData.clear();
        jsonData.put("service_name","upload_blackuser");
        jsonData.put("state",ret);
        jsonData.put("black_uuid",blackUUID);
        jsonData.put("errmsg","");
        return jsonData.toJSONString();
    }


    private String returnNoUser(String serviceName,String key,String value){
        JSONObject jsonData = new JSONObject();
        logger.error("收费员不存在....");
        jsonData.put("service_name",serviceName);
        jsonData.put("state",0);
        jsonData.put(key,value);
        jsonData.put("errmsg","收费员不存在");
        return jsonData.toJSONString();
    }
    //下行消息返回处理
    @Override
    public void monthMemberSync(JSONObject jsonData) {
        Integer  state = jsonData.getInteger("state");
        if(state!=null&&state==1){
            String cardId = jsonData.getString("card_id");
            CarowerProduct product = new CarowerProduct();
            product.setCardId(cardId);
            product.setComId(jsonData.getLong("comid"));
            product= (CarowerProduct)commonDao.selectObjectByConditions(product);
            if(product!=null){
                Long tableId = product.getId();
                String tableName = "carower_product";//product.getClass().getAnnotation(TableName.class).value();
                updateSyncRecord(tableId,tableName);
            }

        }
    }

    @Override
    public void carTypeSync(JSONObject jsonData) {
        Integer  state = jsonData.getInteger("state");
        if(state!=null&&state==1){
            String carTypeId = jsonData.getString("car_type_id");
            CarTypeTb typeTb = new CarTypeTb();
            typeTb.setCartypeId(carTypeId);
            typeTb.setComid(jsonData.getLong("comid"));
            typeTb = (CarTypeTb)commonDao.selectObjectByConditions(typeTb);
            if(typeTb!=null){
                Long tableId = typeTb.getId();
                String tableName ="car_type_tb";//typeTb.getClass().getAnnotation(TableName.class).value();
                updateSyncRecord(tableId,tableName);
            }

        }
    }

    @Override
    public void monthCardSync(JSONObject jsonData) {
        Integer  state = jsonData.getInteger("state");
        if(state!=null&&state==1){
            String packageId = jsonData.getString("package_id");
            ProductPackageTb packageTb = new ProductPackageTb();
            packageTb.setCardId(packageId);
            packageTb.setComid(jsonData.getLong("comid"));
            packageTb=(ProductPackageTb)commonDao.selectObjectByConditions(packageTb);
            if(packageTb!=null){
                Long tableId = packageTb.getId();
                String tableName = "product_package_tb";//packageTb.getClass().getAnnotation(TableName.class).value();
                updateSyncRecord(tableId,tableName);
            }

        }
    }

    @Override
    public void blackuserSync(JSONObject jsonData) {
        Integer  state = jsonData.getInteger("state");
        if(state!=null&&state==1){
            String blackUUID = jsonData.getString("black_uuid");
            ZldBlackTb blackTb = new ZldBlackTb();
            blackTb.setBlackUuid(blackUUID);
            blackTb.setComid(jsonData.getLong("comid"));
            blackTb = (ZldBlackTb)commonDao.selectObjectByConditions(blackTb);
            if(blackTb!=null){
                Long tableId = blackTb.getId();
                String tableName = "zld_black_tb";//blackTb.getClass().getAnnotation(TableName.class).value();
                updateSyncRecord(tableId,tableName);
            }

        }
    }

    @Override
    public void monthPaySync(JSONObject jsonData) {

        Integer  state = jsonData.getInteger("state");
        if(state!=null&&state==1){
            String cardId = jsonData.getString("card_id");
            String tradeNo = jsonData.getString("trade_no");
            CardRenewTb cardRenewTb = new CardRenewTb();
            cardRenewTb.setCardId(cardId);
            cardRenewTb.setTradeNo(tradeNo);
            cardRenewTb.setComid(jsonData.getString("comid"));
            cardRenewTb = (CardRenewTb)commonDao.selectObjectByConditions(cardRenewTb);
            if(cardRenewTb!=null){
                Long tableId = cardRenewTb.getId().longValue();
                String tableName = "card_renew_tb";//cardRenewTb.getClass().getAnnotation(TableName.class).value();
                updateSyncRecord(tableId,tableName);
            }

        }
    }

    @Override
    public void collectSync(JSONObject jsonData) {
//{"service_name":"collector_sync","sign":"6B17988C69E4C60EFF2636717C6CBE72",
// "token":"3f88a651847541c582dc3e9036d4e531","data":{"operate_type":2,
// "service_name":"collector_sync","data_target":"cloud","state":1,"errmsg":"","user_id":"11"}}
        Integer  state = jsonData.getInteger("state");
        if(state!=null&&state==1){
            String userId = jsonData.getString("user_id");
            UserInfoTb userInfoTb = new UserInfoTb();
            userInfoTb.setUserId(userId);
            userInfoTb.setComid(jsonData.getLong("comid"));
            userInfoTb = (UserInfoTb)commonDao.selectObjectByConditions(userInfoTb);
            if(userInfoTb!=null){
                Long tableId = userInfoTb.getId().longValue();
                String tableName = "user_info_tb";//cardRenewTb.getClass().getAnnotation(TableName.class).value();
                updateSyncRecord(tableId,tableName);
            }

        }
    }

    @Override
    public void queryProdprice(JSONObject jsonData) {

        if(!jsonData.containsKey("trade_no")||!jsonData.containsKey("price")||!jsonData.containsKey("state")){
            logger.info("查询月卡续费价格数据不合法");
        }
        String tradeNo = jsonData.getString("trade_no");
        Double price = jsonData.getDouble("price");
        Integer state = jsonData.getInteger("state");
        String errMsg = jsonData.getString("errmsg");
        String priceInfo = "";
        if(state==0){
            priceInfo = state+"_"+errMsg;
        }else{
            priceInfo = state+"_"+price;
        }
        Map<String, Object> monthPrice = TempDataUtil.monthPrice;
        monthPrice.put(tradeNo,priceInfo);
    }

    @Override
    public void lockCar(JSONObject jsonData) {
        if(!jsonData.containsKey("order_id")||!jsonData.containsKey("state")||!jsonData.containsKey("is_locked")){
            logger.info("锁车数据不合法");
        }
        Integer state = jsonData.getInteger("state");
        String orderId = jsonData.getString("order_id");
        Integer isLocked = jsonData.getInteger("is_locked");
        Integer optState = null;
        if(isLocked==0){
            if(state==1){
                optState = 0;//解锁成功
            }else{
                optState = 5;//解锁失败
            }
        }else {
            if(state==1){
                optState = 1;//锁车成功
            }else{
                optState = 3;//锁车失败
            }
        }
        OrderTb orderUpdate = new OrderTb();
        orderUpdate.setIslocked(optState);
        OrderTb orderCondtions = new OrderTb();
        orderCondtions.setOrderIdLocal(orderId);
        commonDao.updateByConditions(orderUpdate,orderCondtions);
    }

    private void updateSyncRecord(Long id,String tableName){
        int ret = 0;
        SyncInfoPoolTb poolTb = new SyncInfoPoolTb();
        poolTb.setTableName(tableName);
        poolTb.setTableId(id);
        SyncInfoPoolTb fields = new SyncInfoPoolTb();
        fields.setState(1);
        ret = commonDao.updateByConditions(fields,poolTb);
        logger.info("月卡会员同步返回成功，修改数据状态："+ret);
    }

    private void updateParkPlot(Integer plot ,Long comId){
        if(plot!=null&&plot>0&&comId!=null&&comId>0){
            ComInfoTb infoTb = new ComInfoTb();
            infoTb.setEmpty(plot);
            infoTb.setId(comId);
            commonDao.updateByPrimaryKey(infoTb);
        }
    }

    private long getComPass(String channelId,Long comId){
        if(Check.isEmpty(channelId))
            return -1L;
        ComPassTb omPassTb = new ComPassTb();
        omPassTb.setChannelId(channelId);
        omPassTb.setComid(comId);
        //查询收费员编号
        omPassTb = (ComPassTb)commonDao.selectObjectByConditions(omPassTb);
        logger.info(omPassTb);
        if(omPassTb!=null&&omPassTb.getId()!=null)
            return omPassTb.getId();
        return  -1L;
    }

    private Long getCarType(String cartypeID,Long comId){
        if(Check.isEmpty(cartypeID))
            return -1L;
        CarTypeTb carTypeTb = new CarTypeTb();
        carTypeTb.setCartypeId(cartypeID);
        carTypeTb.setComid(comId);
        //查询收费员编号
        carTypeTb = (CarTypeTb)commonDao.selectObjectByConditions(carTypeTb);
        logger.info(carTypeTb);
        if(carTypeTb!=null&&carTypeTb.getId()!=null)
            return carTypeTb.getId();
        return  -1L;
    }

    private Long getUserId(String userId,Long comId){
        if(Check.isEmpty(userId))
            return -1L;
        UserInfoTb userInfoTb = new UserInfoTb();
        userInfoTb.setUserId(userId);
        userInfoTb.setComid(comId);
        //查询收费员编号
        userInfoTb = (UserInfoTb)commonDao.selectObjectByConditions(userInfoTb);
        logger.info(userInfoTb);
        if(userInfoTb!=null&&userInfoTb.getId()!=null)
            return userInfoTb.getId();
        return  -1L;
    }

    /**
     * 生成订单数据
     * @param jsonData
     * @return
     */
    private OrderTb setOrder(JSONObject jsonData){
        OrderTb orderTb = new OrderTb();
        String orderId = jsonData.getString("order_id");
        Long comid = jsonData.getLong("comid");
        orderTb.setOrderIdLocal(orderId);
        orderTb.setComid(comid);
        orderTb.setUin(-1L);
        orderTb.setCreateTime(jsonData.getLong("in_time"));
        orderTb.setEndTime(jsonData.getLong("out_time"));
        orderTb.setCarNumber(jsonData.getString("car_number"));
        String uid = jsonData.getString("uid");
        orderTb.setUid(getUserId(uid,comid));
        String outUid = jsonData.getString("out_uid");
        orderTb.setOutUid(getUserId(outUid,comid));
        orderTb.setDuration(jsonData.getLong("duration"));
        String carType = jsonData.getString("car_type");
        Long carTypeId = getCarType(carType,comid);
        if(carTypeId!=-1){
            orderTb.setCarType(carTypeId+"");
        }else{
            orderTb.setCarType(carType);
        }
        orderTb.setcType(jsonData.getString("c_type"));
        String payType = jsonData.getString("pay_type");
        Integer pay_type = null;
        if(payType!=null){
            if(payType.equals("cash")){
                pay_type=1;
            }else if(payType.equals("monthuser")){
                pay_type=3;
            }else if(payType.equals("wallet")||payType.equals("sweepcode")){
                pay_type =2;
            }else {
                pay_type=8;
            }
        }
        orderTb.setPayType(pay_type);
        orderTb.setTotal(jsonData.getBigDecimal("total"));
        orderTb.setFreereasons(jsonData.getString("freereasons"));

        String outChannelId = jsonData.getString("out_channel_id");
        Long channeId = getComPass(outChannelId,comid);
        if(channeId!=-1){
            orderTb.setOutPassid(channeId+"");
        }else{
            orderTb.setOutPassid(outChannelId);
        }
        String inChannelId = jsonData.getString("in_channel_id");
        Long inchanneId = getComPass(inChannelId,comid);
        if(inchanneId!=-1){
            orderTb.setInPassid(inchanneId+"");
        }else{
            orderTb.setInPassid(inChannelId);
        }
        orderTb.setWorkStationUuid(jsonData.getString("work_station_uuid"));
        orderTb.setReduceAmount(jsonData.getBigDecimal("reduce_amount"));
        orderTb.setAmountReceivable(jsonData.getBigDecimal("amount_receivable"));
        orderTb.setReduceAmount(jsonData.getBigDecimal("reduce_amount"));
        orderTb.setElectronicPrepay(jsonData.getBigDecimal("electronic_prepay"));
        orderTb.setElectronicPay(jsonData.getBigDecimal("electronic_pay"));
        orderTb.setCashPrepay(jsonData.getBigDecimal("cash_prepay"));
        orderTb.setCashPay(jsonData.getBigDecimal("cash_pay"));
        orderTb.setIsclick(jsonData.getInteger("islocked"));
        orderTb.setLockKey(jsonData.getString("lock_key"));
        orderTb.setRemark(jsonData.getString("remark"));
        logger.info(orderTb);
        return orderTb;
    }

    //停车费写入账户表
    private void writeToAccount(OrderTb orderTb){
        int r =0;
        Integer payType = orderTb.getPayType();
        Double total = StringUtils.formatDouble(orderTb.getTotal());
        logger.info("paytype:"+payType+",total:"+total);

        if(total>0&&payType!=null){
            ComInfoTb comInfoTb = new ComInfoTb();
            Long comid = orderTb.getComid();
            Long groupId = -1L;
            comInfoTb.setId(comid);
            comInfoTb = (ComInfoTb)commonDao.selectObjectByConditions(comInfoTb);
            if(comInfoTb!=null&&comInfoTb.getGroupid()!=null)
                groupId = comInfoTb.getGroupid();
            logger.info("groupid:"+groupId);
            BigDecimal eprePay = orderTb.getElectronicPrepay();
            BigDecimal ePay = orderTb.getElectronicPay();
            BigDecimal cprePay =  orderTb.getCashPrepay();
            BigDecimal cPay =  orderTb.getCashPay();

            Double elPay = (eprePay==null?0.0:eprePay.doubleValue())+(ePay==null?0.0:ePay.doubleValue());
            Double caPay = (cprePay==null?0.0:cprePay.doubleValue())+(cPay==null?0.0:cPay.doubleValue());
            logger.info("epay:"+elPay+",cachPay:"+caPay);
            if(caPay>0){//现金支付
                ParkuserCashTb cashTb = new ParkuserCashTb();
                cashTb.setOrderid(orderTb.getId());
                int count = commonDao.selectCountByConditions(cashTb);
                logger.info("cash account count:"+count);
                if(count>0)
                    return ;
                cashTb.setAmount(new BigDecimal(caPay));
                cashTb.setComid(comid);
                cashTb.setCreateTime(System.currentTimeMillis()/1000);
                cashTb.setTarget(0);
                cashTb.setUin(orderTb.getUid());
                if(orderTb.getOutUid()!=null)
                    cashTb.setUin(orderTb.getOutUid());
                cashTb.setCtype(0);
                cashTb.setGroupid(groupId);
                r = commonDao.insert(cashTb);
                logger.info("cash account insert:"+r);
            }
            if(elPay>0){//电子支付
                ParkuserAccountTb accountTb = new ParkuserAccountTb();
                accountTb.setOrderid(orderTb.getId());
                int count = commonDao.selectCountByConditions(accountTb);
                logger.info("epay account count:"+count);
                if(count>0)
                    return ;
                accountTb.setAmount(new BigDecimal(elPay));
                accountTb.setComid(comid);
                accountTb.setGroupid(groupId);
                accountTb.setCreateTime(System.currentTimeMillis()/1000);
                accountTb.setTarget(4);//车主付停车费（非预付）或者打赏收费员
                accountTb.setUin(orderTb.getUid());
                accountTb.setRemark("停车费-"+total);
                if(orderTb.getOutUid()!=null)
                    accountTb.setUin(orderTb.getOutUid());
                accountTb.setType(0);
                r = commonDao.insert(accountTb);
                logger.info("epay account insert :"+r);
            }
        }
        logger.info("inset account :"+r);
    }
    @Override
    public void UploadConfirmOrder(String parkId, String data) {
        JSONObject jsonObj = JSONObject.parseObject(data);
        logger.error("手动匹配订单接收到数据------>"+jsonObj);
        String eventId = "";
        if(jsonObj.containsKey("event_id")){
            eventId = jsonObj.getString("event_id");
        }
        String carNumber = "";
        if(jsonObj.containsKey("car_number")){
            carNumber = jsonObj.getString("car_number");
        }
        Long uploadTime = -1L;
        if(jsonObj.containsKey("upload_time")){
            uploadTime = jsonObj.getLong("upload_time");
        }
        String channelId = "";
        if(jsonObj.containsKey("channel_id")){
            channelId = jsonObj.getString("channel_id");
        }
        //获取车场信息
        ComInfoTb comInfoConditions = new ComInfoTb();
        comInfoConditions.setId(Long.parseLong(parkId));
        ComInfoTb comInfo = (ComInfoTb) commonDao.selectObjectByConditions(comInfoConditions);
        String groupid = "-1";
        if(comInfo.getGroupid()!=null && comInfo.getGroupid()!=-1L){
            groupid = comInfo.getGroupid()+"";
        }
        //写入数据库
        ConfirmOrderTb confirmOrder = new ConfirmOrderTb();
        confirmOrder.setEventId(eventId);
        confirmOrder.setCarNumber(carNumber);
        confirmOrder.setUploadTime(uploadTime);
        confirmOrder.setChannelId(channelId);
        confirmOrder.setState(0);
        confirmOrder.setComid(parkId);
        confirmOrder.setGroupid(groupid);
        Integer result = commonDao.insert(confirmOrder);
        logger.error("手动匹配订单入库结果------>"+result);
    }
    @Override
    public void operateLiftrod(String comid,String channelId,Integer state,Integer operate) {
        LiftrodInfoTb liftrodInfoConditions = new LiftrodInfoTb();
        liftrodInfoConditions.setChannelId(channelId);
        liftrodInfoConditions.setOperate(Long.parseLong(operate+""));
        liftrodInfoConditions.setComid(comid);
        LiftrodInfoTb liftrodInfoTb = (LiftrodInfoTb)commonDao.selectObjectByConditions(liftrodInfoConditions);
        if(liftrodInfoTb != null){
            LiftrodInfoTb updateLiftrodInfo = new LiftrodInfoTb();
            updateLiftrodInfo.setState(Long.parseLong(state+""));
            Integer update = commonDao.updateByConditions(updateLiftrodInfo,liftrodInfoConditions);
            logger.error(">>>>>>>>>>>修改通知抬杆信息入库结果："+update);
        }else{
            LiftrodInfoTb insetLifttrodInfo = new LiftrodInfoTb();
            insetLifttrodInfo.setChannelId(channelId);
            insetLifttrodInfo.setState(Long.parseLong(state+""));
            insetLifttrodInfo.setOperate(Long.parseLong(operate+""));
            insetLifttrodInfo.setComid(comid);
            Integer insert = commonDao.insert(insetLifttrodInfo);
            logger.error(">>>>>>>>>>>添加通知抬杆信息入库结果："+insert);
        }
    }

    @Override
    public void deliverTicket(String parkId,String jsonData) {
        JSONObject jsonObject = JSONObject.parseObject(jsonData);
        logger.error("优惠券信息下发异步返回------>"+jsonObject);
        String ticket_id = null;
        if(jsonObject.containsKey("ticket_id")){
            ticket_id = jsonObject.getString("ticket_id");
        }
        Integer state = null;
        if(jsonObject.containsKey("state")){
            state = jsonObject.getInteger("state");
        }
        String orderId  = "";
        if(jsonObject.containsKey("order_id")){
            orderId = jsonObject.getString("order_id");
        }
        if(state!=null&&state==1){
            //更新优惠劵
            OrderTb orderConditions = new OrderTb();
            orderConditions.setComid(Long.valueOf(parkId));
            orderConditions.setOrderIdLocal(orderId);
            OrderTb order = (OrderTb)commonDao.selectObjectByConditions(orderConditions);

            Long oid =-1L;
            if(order!=null)
                oid = order.getId();
            String car_number ="";
            if(order!=null)
                car_number = order.getCarNumber();
            logger.error("优惠券信息下发同步后更新数据库car_number:"+car_number);
            TicketTb ticketTb = new TicketTb();
            ticketTb.setOrderid(oid);
            ticketTb.setState(1);
            ticketTb.setCarNumber(car_number);
            TicketTb ticketConditions = new TicketTb();
            ticketConditions.setId(Long.parseLong(ticket_id));
            Integer result1 = commonDao.updateByConditions(ticketTb,ticketConditions);
            //更新code
            QrCodeTb qrCodeTb = new QrCodeTb();
            qrCodeTb.setState(1);
            qrCodeTb.setIsuse(1);
            QrCodeTb qrCodeConditions = new QrCodeTb();
            qrCodeConditions.setTicketid(Long.parseLong(ticket_id));
            Integer result2 = commonDao.updateByConditions(qrCodeTb,qrCodeConditions);
            logger.error("优惠券信息下发同步后更新数据库："+result1+","+result2);
        }else {
            logger.error("优惠券信息下发同步失败："+jsonData);
        }
    }

    @Override
    public void confirmOrderInform(String parkId,String jsonData) {
        JSONObject jsonObject = JSONObject.parseObject(jsonData);
        logger.error("人工确认订单通知异步返回------>"+jsonObject);
        String eventId = null;
        if(jsonObject.containsKey("event_id")){
            eventId = jsonObject.getString("event_id");
        }
        Integer state = null;
        if(jsonObject.containsKey("state")){
            state = jsonObject.getInteger("state");
        }
        if(state!=null&&state==1){
            ConfirmOrderTb confirmOrder = new ConfirmOrderTb();
            confirmOrder.setState(1);
            ConfirmOrderTb confirmOrderConditions = new ConfirmOrderTb();
            confirmOrderConditions.setEventId(eventId);
            confirmOrderConditions.setComid(parkId);
            Integer result = commonDao.updateByConditions(confirmOrder,confirmOrderConditions);
            logger.error("人工确认订单通知异步返回更新数据------>"+result);
        }else {
            logger.error("人工确认订单通知返回失败："+jsonData);
        }
    }

    @Override
    public List<CarowerProduct> getCarowerProduct(JSONObject jsonData) {
        String park_id = jsonData.getString( "park_id" );
        CarowerProduct carowerProduct = new CarowerProduct();
        if(park_id==null){
            return null;
        }
        carowerProduct.setComId( Long.valueOf( park_id ) );
        carowerProduct.setIsDelete( 0L );

        PageOrderConfig orderConfig = new PageOrderConfig();
        orderConfig.setOrderInfo("id","desc");
        orderConfig.setPageInfo(1,Integer.MAX_VALUE);

        List<CarowerProduct> list = commonDao.selectListByConditions( carowerProduct,orderConfig);
        /*jsonData.clear();
        jsonData.put("service_name","carover_product");
        jsonData.put("state",1);
        JSONObject dataStr = JSONObject.parseObject( "{}" );

        jsonData.put( "data",list );
        return jsonData.toJSONString();*/
        return list;
    }
}
