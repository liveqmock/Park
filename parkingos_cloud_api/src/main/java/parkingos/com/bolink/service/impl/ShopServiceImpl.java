package parkingos.com.bolink.service.impl;


import com.alibaba.fastjson.JSONObject;
import com.zld.common_dao.dao.CommonDao;
import org.logicalcobwebs.concurrent.FJTask;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import parkingos.com.bolink.beans.UserInfoTb;
import parkingos.com.bolink.service.ShopService;
import parkingos.com.bolink.utlis.weixinpay.utils.RequestUtil;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * 查询所有广告商
 */
@Service
public class ShopServiceImpl implements ShopService {

    @Autowired
    private CommonDao commonDao;
    @Override
    public JSONObject selectResultByConditions(Map<String, String> params) {

        String str = "{\"total\":0,\"page\":1,\"rows\":[]}";
        JSONObject result = JSONObject.parseObject(str);

        UserInfoTb userInfoTb = new UserInfoTb();
        userInfoTb.setRoleId( 12345678L );
        userInfoTb.setState( 0 );

        //查询所有广告商数量
        int count = commonDao.selectCountByConditions( userInfoTb );

        if(count==0){
            return result;
        }

        List<UserInfoTb> list = commonDao.selectListByConditions( userInfoTb );
        result.put( "total",count );
        result.put( "rows",list );
        if(params.get("page")!=null){
            result.put("page",Integer.parseInt(params.get("page")));
        }

        return result;
    }

    @Override
    public String create(HttpServletRequest request) {
        //接收前台参数信息
        Long id  = RequestUtil.getLong( request, "id",-1L );
        String nickname = RequestUtil.processParams( request,"nickname" );
        String password = RequestUtil.processParams( request,"password" );

        //基本信息封装
        UserInfoTb userInfoTb = new UserInfoTb();
        userInfoTb.setNickname( nickname );

        int count=0;

        //如果id不为-1  则表示需要修改广告商信息
        if(id==-1){
            //注册加油站操作
            Long sque = commonDao.selectSequence( userInfoTb.getClass() );
            //注册时间
            userInfoTb.setId( sque );
            userInfoTb.setStrid( sque+"" );
            userInfoTb.setPassword( sque+"" );
            userInfoTb.setState( 0 );
            userInfoTb.setRoleId( 12345678L );
            count = commonDao.insert( userInfoTb );
        }else{
            //修改加油站信息
            userInfoTb.setId( id );
            userInfoTb.setPassword( password );
            userInfoTb.setNickname( nickname );
            count = commonDao.updateByPrimaryKey( userInfoTb );
        }

        return "{\"state\":" + count + "}";
    }

    @Override
    public String delete(HttpServletRequest request) {
        Long id = RequestUtil.getLong( request, "id", -1L );
        int delete = 0;
        if (id > 0) {
            UserInfoTb userInfoTb = new UserInfoTb();
            userInfoTb.setId( id );
            userInfoTb.setState( 1 );
            //删除操作将state状态修改为1
            delete = commonDao.updateByPrimaryKey( userInfoTb );
        }
        return "{\"state\":" + delete + "}";
    }
}
