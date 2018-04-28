package parkingos.com.bolink.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.zld.common_dao.dao.CommonDao;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import parkingos.com.bolink.beans.UserInfoTb;
import parkingos.com.bolink.service.LoginService;

import java.util.UUID;

@Service
public class LoginServiceImpl implements LoginService {

        Logger logger = Logger.getLogger( LoginServiceImpl.class);

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

            userInfoTb.setPassword(passwd);
            userInfoTb.setStrid( username );
            userInfoTb.setState( 0 );

            logger.info( userInfoTb );
            userInfoTb = (UserInfoTb) commonDao.selectObjectByConditions(userInfoTb);

            if(userInfoTb==null){
                //账号或这密码错误
                //登陆失败
                result.put("state", false);
                result.put("msg", "账号或密码错误");
            }else{
                //生成token
                String tokenStr = UUID.randomUUID().toString().replace( "-" ,"");
                //清空之前的登陆信息
                result.put( "token",tokenStr );
                result.put("state", true);
                result.put("user", userInfoTb);
            }

            logger.info( "登陆信息>>>>"+result );
            return result;
    }
}
