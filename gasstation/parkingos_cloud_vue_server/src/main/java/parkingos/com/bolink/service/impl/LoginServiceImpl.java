package parkingos.com.bolink.service.impl;

import com.alibaba.fastjson.JSONObject;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import parkingos.com.bolink.dao.spring.CommonDao;
import parkingos.com.bolink.models.TokenTb;
import parkingos.com.bolink.models.UserInfoTb;
import parkingos.com.bolink.service.LoginService;
import parkingos.com.bolink.utils.TokenUtil;

import java.util.UUID;

/**
 * 登陆
 */
@Service
public class LoginServiceImpl implements LoginService {

    Logger logger = Logger.getLogger(LoginServiceImpl.class);

    @Autowired
    private CommonDao commonDao;

    @Override
    public JSONObject getResultByUserNameAndPass(String username, String passwd) {

        JSONObject result = JSONObject.parseObject("{}");

        if(username==null||passwd==null){
            result.put("state", false);
            result.put("msg", "账号或密码错误");
            return result;
        }

        UserInfoTb userInfoTb = new UserInfoTb();
        userInfoTb.setStatus(0);
        userInfoTb.setPassword(passwd);
        userInfoTb.setUsername( username );

        logger.info( userInfoTb );

        userInfoTb = (UserInfoTb) commonDao.selectObjectByConditions(userInfoTb);

        /*if (userInfoTb == null) {
            //登陆失败
            result.put("state", false);
            result.put("msg", "账号或密码错误");
        }else {
            //登陆成功
            String tokenStr = UUID.randomUUID().toString().replace( "-" ,"");

            TokenTb tokenTb = new TokenTb();
            tokenTb.setUsername( username );
            System.out.println( "=-------------------------------" );
            System.out.println( username );
            int count = commonDao.selectCountByConditions( tokenTb );
            System.out.println( count );
            if(count>0){
                //已经登陆
                tokenTb = (TokenTb) commonDao.selectObjectByConditions( tokenTb );
                tokenTb.setToken( tokenStr );
                commonDao.updateByPrimaryKey( tokenTb );
            }else {
                //未登陆直接保存登陆信息
                tokenTb.setToken( tokenStr );
                commonDao.insert( tokenTb );
            }
            result.put( "token",tokenStr );
            result.put("state", true);
            result.put("user", userInfoTb);
        }*/
        if(userInfoTb==null){
            //账号或这密码错误
            //登陆失败
            result.put("state", false);
            result.put("msg", "账号或密码错误");
        }else{
            //生成token
            String tokenStr = UUID.randomUUID().toString().replace( "-" ,"");
            //清空之前的登陆信息
            TokenUtil.clear();

            TokenUtil.put( tokenStr,userInfoTb );

            result.put( "token",tokenStr );
            result.put("state", true);
            result.put("user", userInfoTb);
        }

        logger.info( "登陆信息>>>>"+result );
        return result;
    }
}
