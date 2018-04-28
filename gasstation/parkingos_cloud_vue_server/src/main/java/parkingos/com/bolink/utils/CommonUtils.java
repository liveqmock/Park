package parkingos.com.bolink.utils;

import io.netty.channel.Channel;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import parkingos.com.bolink.dao.spring.CommonDao;
import parkingos.com.bolink.models.ParkTokenTb;
import parkingos.com.bolink.qo.PageOrderConfig;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.security.cert.CertificateException;
import javax.security.cert.X509Certificate;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

@Component
public class CommonUtils {
    Logger logger = Logger.getLogger(CommonUtils.class);
    @Autowired
    CommonDao commonDao;

    /**
     * 获取下发数据的TCP通道
     *
     * @param comid
     * @return
     */
    public String getChannel(String comid) {
       /* String channelPass = "";
        ParkTokenTb tokenTb = new ParkTokenTb();
        tokenTb.setParkId(comid);
        tokenTb = commonDao.selectObjectBySelective(tokenTb);
//		Map parkMap = null;// daService.getMap("select * from park_token_tb where park_id=? order by id desc ", new Object[]{comid});
        if (tokenTb != null ) {
            String localId = tokenTb.getLocalId();
            if (!Check.isEmpty(localId)) {
                channelPass = comid + "_" + localId;
            }
        } else {
            channelPass = comid;
        }
        logger.error("sdk comid:" + channelPass);
        return channelPass;*/
        String channelPass = "";
        ParkTokenTb tokenTb = new ParkTokenTb();
        tokenTb.setParkId(comid);
        PageOrderConfig orderConfig = new PageOrderConfig();
        orderConfig.setOrderInfo("id","desc");
        orderConfig.setPageInfo(1,null);
//        tokenTb.setOrderField("id");
//        tokenTb.setOrderType("desc");
//        tokenTb.setPageSize(0);
        List<ParkTokenTb> tokenTbs = commonDao.selectListByConditions(tokenTb,orderConfig);//.selectObjectBySelective(tokenTb);
        //Map parkMap = dataBaseService.getMap("select * from park_token_tb where park_id=?", new Object[]{comid});
        if(tokenTbs!=null&&!tokenTbs.isEmpty())
            tokenTb = tokenTbs.get(0);
        if(tokenTb != null ){
            String localId =tokenTb.getLocalId();// String.valueOf(parkMap.get("local_id"));
            if(!Check.isEmpty(localId)){
                channelPass += comid+"_"+localId;
            }
        }else{
            channelPass = comid;
        }
        return channelPass;
    }

    /**
     * 消息返回
     *
     * @param mesg 需要发送的消息
     * @param channel 通道
     */
    public boolean doBackMessage(String mesg, Channel channel) {
        if (channel != null && channel.isActive()
                && channel.isWritable()) {
            try {
                logger.error("发消息到SDK，channel:"+channel+",mesg:" + mesg);
                byte[] req= ("\n" + mesg + "\r").getBytes("utf-8");
                ByteBuf buf = Unpooled.buffer(req.length);
                buf.writeBytes(req);
                channel.writeAndFlush(buf);
                return true;
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }else{
            logger.error("客户端已断开连接...");
        }
        return false;
    }

    /*private boolean doBackMessage(String mesg, Channel channel) {
		if (channel != null && channel.isActive()
				&& channel.isWritable()) {
			try {
				logger.error("发消息到SDK，channel:" + channel + ",mesg:" + mesg);
				byte[] req = ("\n" + mesg + "\r").getBytes("utf-8");
				ByteBuf buf = Unpooled.buffer(req.length);
				buf.writeBytes(req);
				ChannelFuture future = channel.writeAndFlush(buf);
				return true;
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		} else {
			logger.error("客户端已断开连接...");
		}
		return false;
	}*/

    public static void trustAllHosts() {
        // Create a trust manager that does not validate certificate chains
        TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
            public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                return new java.security.cert.X509Certificate[] {};
            }

            public void checkClientTrusted(X509Certificate[] chain, String authType)
                    throws CertificateException {
            }

            public void checkServerTrusted(X509Certificate[] chain, String authType)
                    throws CertificateException {
            }

            @Override
            public void checkClientTrusted(
                    java.security.cert.X509Certificate[] arg0,
                    String arg1)
                    throws java.security.cert.CertificateException {
            }
            @Override
            public void checkServerTrusted(
                    java.security.cert.X509Certificate[] arg0,
                    String arg1)
                    throws java.security.cert.CertificateException {
            }
        }
        };
        try {
            SSLContext sc = SSLContext.getInstance("TLS");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<String> getChannels(Long comid) {
        String channelPass = "";
        ParkTokenTb tokenTb = new ParkTokenTb();
        tokenTb.setParkId(comid+"");
        PageOrderConfig orderConfig = new PageOrderConfig();
        orderConfig.setOrderInfo("id","desc");
        orderConfig.setPageInfo(1,null);
        List<ParkTokenTb> tokenTbs = commonDao.selectListByConditions(tokenTb,orderConfig);
        List<String> list = null;
        if(tokenTbs!=null&&!tokenTbs.isEmpty()){
            for(ParkTokenTb parkTokenTb:tokenTbs){
                if(parkTokenTb.getLocalId()!=null&&parkTokenTb.getLocalId().startsWith( "camera_" )){
                    list = new ArrayList<String>();
                    list.add( parkTokenTb.getParkId()+"_"+parkTokenTb.getLocalId() );
                }
            }
        }
        return list;
    }
}
