package parkingos.com.bolink.schedule;

import org.apache.log4j.Logger;
import parkingos.com.bolink.dao.spring.CommonDao;
import parkingos.com.bolink.utils.CommonUtils;

import java.util.TimerTask;


public class ParkSchedule extends TimerTask {

	private static Logger logger = Logger.getLogger(ParkSchedule.class);

	//private CommonDao commonDao;
	private CommonDao commonDao;

	private CommonUtils commonUtils;

	public ParkSchedule(CommonDao commonDao, CommonUtils commonUtils) {
		this.commonDao = commonDao;
		this.commonUtils = commonUtils;
	}

	@Override
	public void run() {
		logger.error("数据库操作对象:" + commonDao);
		logger.error("开始下行数据定时任务....");
		sendMessageToSDK();
	}

	private void sendMessageToSDK() {
		/*SyncInfoPoolTb syncInfoPoolTb = new SyncInfoPoolTb();
		syncInfoPoolTb.setState(0);
		//下发数据需要按时间顺序发送，不能倒序
		PageOrderConfig orderConfig = new PageOrderConfig();
		orderConfig.setOrderInfo("id","desc");
		List<SyncInfoPoolTb> dataNeedSyncList =  commonDao.selectListByConditions(syncInfoPoolTb,orderConfig);//.selectAllBySelective(syncInfoPoolTb);//null;// daService.getAllMap("select * from sync_info_pool_tb where state=? ", list);
		logger.info(">>>>需要同步的数据>>>>>>>"+dataNeedSyncList);
		if (dataNeedSyncList != null && dataNeedSyncList.size() > 0) {
			for (SyncInfoPoolTb infoPoolTb : dataNeedSyncList) {
				if (infoPoolTb != null ) {
					String tableName = String.valueOf(infoPoolTb.getTableName());
					Long tableId = Long.valueOf(String.valueOf(infoPoolTb.getTableId()));
					Long comid = Long.valueOf(String.valueOf(infoPoolTb.getComid()));
					Integer operate = Integer.valueOf(String.valueOf(infoPoolTb.getOperate()))+1;
					if (tableName.equals("carower_product")) {
						String result = sendcardMember( tableId, comid, operate);
						logger.error(">>>>>>>>>>>>>>>>>>>>>发送月卡会员信息结果" + result);
					} else if (tableName.equals("price_tb")) {
						logger.info("未处理价格同步");
						//String result = sendPriceInfo( tableId, comid, operate);
						//logger.error(">>>>>>>>>>>>>>>>>>>>>发送价格信息结果" + result);
					} else if (tableName.equals("product_package_tb")) {
						String result = sendProductPackageInfo(tableId, comid, operate);
						logger.error(">>>>>>>>>>>>>>>>>>>>>发送月卡套餐信息结果" + result);
					} else if (tableName.equals("user_info_tb")) {
						String result = sendUserInfo(tableId, comid, operate);
						logger.error(">>>>>>>>>>>>>>>>>>>>>发送收费员信息结果" + result);
					} else if (tableName.equals("order_tb")) {
						logger.info("未处理订单同步");
						//String result = sendOrderInfo(tableId, comid, operate);
						//logger.error(">>>>>>>>>>>>>>>>>>>>>发送结算订单信息结果" + result);
					}else if (tableName.equals("car_type_tb")) {
						String result = sendCarTypeInfo( tableId, comid, operate);
						logger.error(">>>>>>>>>>>>>>>>>>>>>发送车型信息结果" + result);
					} else if (tableName.equals("zld_black_tb")) {
						String result = sendBlackUser(tableId, comid, operate);
						logger.error(">>>>>>>>>>>>>>>>>>>>>发送黑名单信息结果" + result);
					} else if (tableName.equals("card_renew_tb")) {
						String result = sendCardReNewInfo(tableId, comid, operate);
						logger.error(">>>>>>>>>>>>>>>>>>>>>发送月卡续费信息结果" + result);
					}   else {
						logger.error(">>还没有处理当前同步业务:" + tableName);
					}
				}
			}
		}*/
	}



}
