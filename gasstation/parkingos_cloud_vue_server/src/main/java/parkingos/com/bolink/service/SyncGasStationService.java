package parkingos.com.bolink.service;

import com.alibaba.fastjson.JSONObject;

import java.util.Map;

public interface SyncGasStationService {
    JSONObject sendMessage(Map<String, String> reqParameterMap);
}
