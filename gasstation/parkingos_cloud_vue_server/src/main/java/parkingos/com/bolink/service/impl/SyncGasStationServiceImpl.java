package parkingos.com.bolink.service.impl;

import com.alibaba.fastjson.JSONObject;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import parkingos.com.bolink.dao.spring.CommonDao;
import parkingos.com.bolink.models.SyncGasStationTb;
import parkingos.com.bolink.netty.NettyChannelMap;
import parkingos.com.bolink.service.SyncGasStationService;
import parkingos.com.bolink.utils.StringUtils;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;

/**
 * 加油站云平台向ist云平台发送开闸数据
 */
@Service
public class SyncGasStationServiceImpl implements SyncGasStationService {

    Logger logger = Logger.getLogger( SyncGasStationServiceImpl.class);

    @Autowired
    private CommonDao commonDao;

    @Override
    public JSONObject sendMessage(Map<String, String> reqMap) {

        //返回json消息
        String str = "{}";
        JSONObject result = JSONObject.parseObject( str );

        //判断data
        String data = reqMap.get( "data" );

        if(data==null||data.equals( "" )){
            result.put( "status",0 );
            result.put( "ainfo", "没有开闸数据");
            return result;
        }

        //将data字符串转换为json对象
        JSONObject dataJson = JSONObject.parseObject( data );

        //验证签名
        String sign = reqMap.get( "sign" );

        if(sign==null||sign.equals( "" )){
            result.put( "status",0 );
            result.put( "ainfo", "没有携带签名数据");
            return result;
        }

        String signStr = getSign(dataJson);

        if(!signStr.equals(sign)){
            result.put( "status",0 );
            result.put( "ainfo", "签名验证错误");
            return result;
        }

        //获取时间戳
        Long timeStamp = 0L;
        try{
            timeStamp = Long.valueOf( reqMap.get( "timeStamp" ) );
        }catch(Exception e){
            e.printStackTrace();
        }
        dataJson.put( "timeStamp",timeStamp );

        //保存数据
        SyncGasStationTb syncGasStationTb = saveData(dataJson);

        //tcp发送开闸信息
        sendMessageToSDK(syncGasStationTb,result);

        return result;
    }

    private void sendMessageToSDK(SyncGasStationTb syncGasStationTb, JSONObject result) {
        //获取下传数据的通道信息
        Channel channel = NettyChannelMap.get(syncGasStationTb.getParkedId());
        if (channel == null || !channel.isActive() || !channel.isWritable()) {//通道不可用，客户端掉线
            logger.error(syncGasStationTb.getParkedId() + "，客户端掉线.....");
            result.put( "status",0 );
            result.put( "ainfo",syncGasStationTb.getParkedId()+"客户端掉线");
            return ;
        }
        //定义下传数据的json对象
        JSONObject jsonSend = new JSONObject();

        jsonSend.put("service_name", "gateOperater");
        jsonSend.put("parkedID", syncGasStationTb.getParkedId());
        jsonSend.put("deviceID", syncGasStationTb.getDeviceId());
        jsonSend.put("info", syncGasStationTb.getInfo());
        jsonSend.put("status", syncGasStationTb.getStatus());
        jsonSend.put("id",syncGasStationTb.getId() );


        logger.error("jsonSend:"+jsonSend.toString());
        String isSend = doBackMessage(jsonSend.toString(), channel);
        logger.error(">>>>>>>>>>>>>>云端发送数据到tcp客户端系统结果：" + isSend);
        if(isSend.equals("fail")){
            result.put( "status",0 );
            result.put( "ainfo", "云平台向tcp客户端发送消息失败");
        }else{
            syncGasStationTb.setSendState( 1 );
            commonDao.updateByPrimaryKey( syncGasStationTb );
            result.put( "status",1 );
            result.put( "ainfo", "云平台向tcp客户端发送消息成功");
        }
    }

    //下发数据
    private String doBackMessage(String mesg, Channel channel) {
        String isSent = "fail";
        if (channel != null && channel.isActive()&& channel.isWritable()) {
            try {
                byte[] req = ("\n" + mesg + "\r").getBytes("UTF-8");
                ByteBuf buf = Unpooled.buffer(req.length);
                buf.writeBytes(req);
                channel.writeAndFlush(buf);

                isSent="success";
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
                logger.error("返回消息到客户端出现异常");
            }
        }else{
            logger.error("客户端已掉线...");
        }
        return isSent;
    }


    //保存到同步信息表
    private SyncGasStationTb saveData(JSONObject dataJson) {

        SyncGasStationTb syncGasStationTb = new SyncGasStationTb();
        syncGasStationTb.setDeviceId( dataJson.getString( "deviceID" ) );
        syncGasStationTb.setParkedId( dataJson.getString( "parkedID" ) );
        syncGasStationTb.setStatus( dataJson.getInteger( "Status" ) );
        syncGasStationTb.setInfo( dataJson.getString( "Info" ) );
        syncGasStationTb.setTimeStamp( dataJson.getLong( "timeStamp" ) );

        Long sque = commonDao.selectSequence( syncGasStationTb.getClass() );
        syncGasStationTb.setId( sque );

        commonDao.insert( syncGasStationTb );
        return syncGasStationTb;
    }

    //将data数据按照规则进行签名
    private String getSign(JSONObject dataJson) {

        //需要进行签名的key
        ArrayList<String> list = new ArrayList<>();
        list.add("parkedID");
        list.add("deviceID");
        list.add("Info");
        list.add("Status");

        //自然排序
        Collections.sort(list);

        //如果该key对应的提交参数不为null进行字符串的拼接
        StringBuilder sb = new StringBuilder();
        int i=0;
        for(String key:list){
            String value= dataJson.getString( key );
            if(value!=null && !value.equals( "" )){
                //sb.append(key+"="+value+"&");
                sb.append(key+"="+value);
            }
            if(i!=list.size()-1){
                sb.append( "&" );
            }
            i++;
        }

        sb.append("key=digisterGas");

        //md5加密再大写
        System.out.println( "signStringBuilder:   "+sb.toString() );
        String MD5Str = StringUtils.MD5( sb.toString() ).toUpperCase();
        System.out.println( "MD5Str:"+MD5Str );
        return MD5Str;
    }
}
