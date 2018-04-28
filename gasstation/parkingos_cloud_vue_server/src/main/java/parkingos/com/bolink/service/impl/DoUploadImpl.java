package parkingos.com.bolink.service.impl;

import com.alibaba.fastjson.JSONObject;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import parkingos.com.bolink.dao.spring.CommonDao;
import parkingos.com.bolink.models.ComInfoTb;
import parkingos.com.bolink.models.GasStationTb;
import parkingos.com.bolink.models.ParkTokenTb;
import parkingos.com.bolink.models.SyncGasStationTb;
import parkingos.com.bolink.service.DoUpload;
import parkingos.com.bolink.utils.Check;
import parkingos.com.bolink.utils.StringUtils;

import java.net.Inet4Address;
import java.util.UUID;

//
@Service
public class DoUploadImpl implements DoUpload {


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
                GasStationTb gasStationTb = new GasStationTb();
                gasStationTb.setId(Long.valueOf(parkId));
                gasStationTb = (GasStationTb)commonDao.selectObjectByConditions(gasStationTb);
                logger.info(gasStationTb);
//                Map<String, Object> comInfoMap = daService.getMap("select * from com_info_tb where id=?",
//                        new Object[] { Long.valueOf(parkId)});
                if (gasStationTb != null) {
                    String ukey = gasStationTb.getKey();//String.valueOf(comInfoMap.get("ukey"));
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
        String parkId = data.getString("parkedID");
        String localId = "";
        if(data.containsKey("deviceID"))//多终端电脑登录
            localId = data.getString("deviceID");
        Long gasStationId = Long.valueOf(parkId);
        //查询出车场具体信息
        GasStationTb gasStationTb = new GasStationTb();
        gasStationTb.setId(gasStationId);
        gasStationTb.setKey( data.getString( "key" ) );
        gasStationTb = (GasStationTb)commonDao.selectObjectByConditions(gasStationTb);
        if(gasStationTb!=null){
            String strKey = orgdata+"key="+gasStationTb.getKey();
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
                    logger.info("tcp login park:"+gasStationId+",token"+token+",login result:"+r);

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
                    logger.info("tcp login , relogon,park:"+gasStationId+",token"+token+",result:"+r);
                }
                if(r>=1){
                    logger.error(logStr+"error:登录成功");
                    return token;
                }else {
                    logger.error(logStr+"error:登录失败");
                    return "error:登录失败";
                }
            }else {
                logger.error(logStr+"error:加油站签名错误");
                return "error:签名错误";
            }
        }else {
            logger.error(logStr+"error:加油站不存在");
            return "error:加油站不存在";
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
    //tcp开闸返回数据处理
    public String gateResult(String data) {

        logger.info( "tcp客户端处理成功返回数据>>>>>>"+data );
        JSONObject jsonObject = JSONObject.parseObject( data );
        Long id=jsonObject.getLong( "id" );
        //tcp客户端返回处理结果  处理成功将state修改为1
        if(id!=null){
            SyncGasStationTb syncGasStationTb = new SyncGasStationTb();
            syncGasStationTb.setId( id );
            syncGasStationTb.setSdkState( 1 );
            int i = commonDao.updateByPrimaryKey( syncGasStationTb );
            if(i!=0){
                return "success";
            }
        }

        return "fail";
    }
}
