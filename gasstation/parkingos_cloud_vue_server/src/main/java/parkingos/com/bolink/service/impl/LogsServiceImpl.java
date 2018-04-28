package parkingos.com.bolink.service.impl;

import com.alibaba.fastjson.JSONObject;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import parkingos.com.bolink.dao.spring.CommonDao;
import parkingos.com.bolink.models.GasStationTb;
import parkingos.com.bolink.models.SyncGasStationTb;
import parkingos.com.bolink.service.LogsService;
import parkingos.com.bolink.service.SupperSearchService;

import java.util.Map;

@Service
public class LogsServiceImpl implements LogsService {


    Logger logger = Logger.getLogger(LogsServiceImpl.class);

    @Autowired
    private CommonDao commonDao;
    @Autowired
    private SupperSearchService<SyncGasStationTb> supperSearchService;

    @Override
    public JSONObject selectResultByConditions(Map<String, String> reqmap) {

        SyncGasStationTb syncGasStationTb = new SyncGasStationTb();

        JSONObject result = supperSearchService.supperSearch(syncGasStationTb, reqmap);

        return result;
    }
}
