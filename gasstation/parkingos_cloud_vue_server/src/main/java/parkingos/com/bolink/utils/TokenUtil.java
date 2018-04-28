package parkingos.com.bolink.utils;

import parkingos.com.bolink.models.UserInfoTb;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 存放用户登陆信息
 */
public class TokenUtil {

    private static Map<String, UserInfoTb> tokenMap = new ConcurrentHashMap<String, UserInfoTb>();

    public static UserInfoTb getUser(String token){
        return tokenMap.get( token );
    }

    public static void put(String token,UserInfoTb userInfoTb){
        tokenMap.put( token,userInfoTb );
    }

    public static void clear(){
        tokenMap.clear();
    }

}
