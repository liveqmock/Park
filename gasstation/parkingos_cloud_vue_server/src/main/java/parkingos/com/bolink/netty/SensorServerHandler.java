package parkingos.com.bolink.netty;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.parser.Feature;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import org.apache.log4j.Logger;
import parkingos.com.bolink.service.DoUpload;
import parkingos.com.bolink.utils.Check;
import parkingos.com.bolink.utils.ExecutorsUtil;

import java.io.UnsupportedEncodingException;
import java.util.concurrent.ExecutorService;


/**
 * 注意，pipeline中的所有context都是私有的，针对context的所有操作都是线程安全的，但context对象包含的handler不一定是私有的
 * 。 比如添加了Sharable注解的handler，表示该handler自身可以保证线程安全，这种handler只实例化一个就够了。
 * 而对于没有添加Sharable注解的handler， netty就默认该handler是有线程安全问题的，对应实例也不能被多个Context持有。
 * 
 * @author whx
 * 
 */

@Sharable
public class SensorServerHandler extends ChannelHandlerAdapter {

	Logger logger = Logger.getLogger(SensorServerHandler.class);


	 @Override 
	 public void channelActive(ChannelHandlerContext ctx) throws Exception { 
		 logger.info("一个新的连接"); 
		 ctx.fireChannelActive(); 
		 ctx.flush();
	 }
	 
	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg)	throws Exception {
		handleMessage(ctx,msg);
	}

	private void  handleMessage(final ChannelHandlerContext ctx,final Object msg){
		logger.info("tcp 开始处理消息>>>>"+msg.toString());
		ExecutorService es  = ExecutorsUtil.getExecutorPool();
		es.execute(new Runnable() {
			public void run() {
				if (msg != null && !"".equals(msg.toString())) {
					//接收客户端消息
					String mesg = msg.toString();

					if (mesg.length() > 10)
						logger.error("服务器收到消息：" + mesg);
					if (mesg.indexOf("0x11") != -1 && mesg.length() < 6) {// 心跳

						//doBackMessage("0x12", ctx.channel());
						doBackMessage("{\"service_name\":\"heart\"}", ctx.channel());
						return;
					}
					JSONObject jsonMesg = JSON.parseObject(mesg, Feature.OrderedField);
					logger.info(jsonMesg);
					if (jsonMesg != null) {
						String service_name = null;
						if (jsonMesg.containsKey("service_name")) {
							service_name = jsonMesg.getString("service_name");
						} else {
							String mess = "{\"state\":0,\"errmsg\":\"未识别到service_name标记\"}";
							doBackMessage(mess, ctx.channel());
							logger.error("**************缺少必须参数：service_name,请检查上传数据********");
							return;
						}
						String data = null;
						if (jsonMesg.containsKey("data")) {
							data = jsonMesg.getString("data");
						}
						String sign = null;
						if (jsonMesg.containsKey("sign")) {
							sign = jsonMesg.getString("sign");
						}
						if (Check.isEmpty(sign)) {
							logger.error("data error  ,error:未识别到sign标记");
							String mess = "{\"state\":0,\"errmsg\":\"未识别到sign标记\"}";
							doBackMessage(mess, ctx.channel());
							return;
						}


						logger.error("开始执行tcp接口协议的方法调用");
						if (service_name.equals("login")) {// 登录数据
							JSONObject jsonObj = JSONObject.parseObject(data);
							String parkedID = jsonObj.getString("parkedID");
							if (Check.isEmpty(parkedID)) {// 账户为空直接返回
								logger.error("tcp park login ,error:未识别到park_id标记");
								String mess = "{\"state\":0,\"errmsg\":\"未识别到park_id标记\"}";
								doBackMessage(mess, ctx.channel());
								return;
							}
							//检查是否携带加油站登陆key
							//JSONObject json = JSON.parseObject(data, Feature.OrderedField);
							String ukey = jsonObj.getString( "key" );
							if (Check.isEmpty(ukey)) {
								logger.error("data error  ,error:未识别到加油站登陆key标记");
								String mess = "{\"state\":0,\"errmsg\":\"未识别到登陆key标记\"}";
								doBackMessage(mess, ctx.channel());
								return;
							}
							String deviceID = null;
							String key = parkedID;
							if (jsonObj.containsKey("deviceID")) {//收费系统编号
								deviceID = jsonObj.getString("deviceID");
								if (!Check.isEmpty(deviceID)) {
									//key += "_" + deviceID;
								}
							}
							NettyChannelMap.add(key, ctx.channel());
							System.out.println( "key。。。"+key );
							logger.info(data);
							doLogin(data, sign, key);
						} else {
							String token = jsonMesg.getString("token");
							if (Check.isEmpty(token)) {// token值为空直接返回
								logger.error("data error ,error:未识别到token标记");
								String mess = "{\"state\":0,\"errmsg\":\"未识别到token标记\"}";
								doBackMessage(mess, ctx.channel());
								//验证token
								return;
							}
							DoUpload doUpload = NettyChannelMap.doUpload;
							if (doUpload == null) {
								logger.error("server error ,初始化失败....");
								String mess = "{\"state\":0,\"errmsg\":\"服务器初始化失败\"}";
								doBackMessage(mess, ctx.channel());
								return;
							}

							String parkId = doUpload.checkTokenSign(token, sign, data);
							if (parkId.indexOf("error") != -1) {//验证有错误
								logger.error("server error ," + parkId);
								String mess = "{\"state\":0,\"errmsg\":\"" + parkId + "\"}";
								doBackMessage(mess, ctx.channel());
								return;
							}

							String backMesg = "";
							JSONObject jsonData = JSONObject.parseObject(data);
							if(jsonData!=null){
								jsonData.put("comid",parkId);
							}else {
								logger.error("data error:" + data);
								doBackMessage("{\"state\":0,\"errmsg\":\"data error\"}", ctx.channel());
								return ;
							}
							if (service_name.equals("gateResult")) {//开闸成功调用

								String result = doUpload.gateResult( data );

							}else if(service_name.equals( "heart" )){
								//心跳包
								doBackMessage("{\"service_name\":\"heart\",\"msg\":\"a1\"}", ctx.channel());
							}
							logger.info("return message"+backMesg);
							doBackMessage(backMesg, ctx.channel());
						}
					}
				}

			}});
	}

	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		logger.error("连接断开，释放资源" + ctx.channel());
		DoUpload doUpload = NettyChannelMap.doUpload;
		logger.info("tcpHandle:" + doUpload);
		if (doUpload != null) {
			String parkId = NettyChannelMap.getParkByChannel(ctx.channel());
			if (parkId != null)
				doUpload.logout(parkId,ctx.channel().remoteAddress().toString());
			logger.error(parkId + ",退出登录！");
		}
		ctx.close();
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
		throws Exception {
		logger.error(">>>>>>>>>>>>>>>>>"+cause.getMessage()+"--"+ctx.channel());
	}


		/**
		 * 服务端消息返回到客户端
		 *
		 * @param mesg 原始数据
		 */
	private void doBackMessage(String mesg, Channel channel) {
		if (channel != null && channel.isActive()&& channel.isWritable()) {
			try {
				//发送消息给客户端
//				if(mesg.length()>10)
//					logger.error("服务器处理返回:"+mesg+","+channel);
				//byte[] req = ("\n" + mesg + "\r").getBytes("UTF-8");
				byte[] req = ( mesg ).getBytes("UTF-8");
				ByteBuf buf = Unpooled.buffer(req.length);
				buf.writeBytes(req);
				channel.writeAndFlush(buf);
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
				logger.error("返回消息到客户端出现异常");
			}
		}else{
			logger.error("客户端已掉线...");
		}
	}
	/**
	 * 登录处理
	 *
	 * @param data 原始数据
	 * @param key localid
	 */
	private void doLogin(String data, String sign,String key) {
		DoUpload doUpload = NettyChannelMap.doUpload;
		Channel clientChannel = NettyChannelMap.get(key);
		if (doUpload != null) {
			String token = doUpload.doLogin(data, sign,clientChannel.remoteAddress().toString());
			String result = "{\"state\":1,\"token\":\"" + token + "\","+"\"service_name\":\"login\"}";
			if (token != null && token.indexOf("error") != -1) {// 登录失败
				result = "{\"state\":0,\"errmsg\":\"" + token + "\","+"\"service_name\":\"login\"}";
			}
			logger.error("返回登录消息" + result + ",channel:" + clientChannel);
			doBackMessage(result, clientChannel);
		} else {
			logger.error("服务器初始化失败");
			String result = "{\"state\":0,\"errmsg\":\"服务器初始化异常\"}";
			doBackMessage(result, clientChannel);
		}
	}


}
