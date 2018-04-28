package parkingos.com.bolink.service;

import com.alibaba.fastjson.JSONObject;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

public interface ShopService {
    public JSONObject selectResultByConditions(Map<String, String> reqParameterMap);

    String create(HttpServletRequest req);

    String delete(HttpServletRequest request);
}
