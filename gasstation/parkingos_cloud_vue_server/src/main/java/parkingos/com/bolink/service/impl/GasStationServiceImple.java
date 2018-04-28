package parkingos.com.bolink.service.impl;

import com.alibaba.fastjson.JSONObject;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import parkingos.com.bolink.dao.spring.CommonDao;
import parkingos.com.bolink.models.GasStationTb;
import parkingos.com.bolink.service.GasStationService;
import parkingos.com.bolink.service.SupperSearchService;
import parkingos.com.bolink.utils.RequestUtil;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;
import java.util.UUID;

/**
 * 加油站信息
 */
@Service
public class GasStationServiceImple implements GasStationService {

    Logger logger = Logger.getLogger(GasStationServiceImple.class);

    @Autowired
    private CommonDao commonDao;
    @Autowired
    private SupperSearchService<GasStationTb> supperSearchService;

    @Override
    /**
     * 条件查询加油站信息
     */
    public JSONObject selectResultByConditions(Map<String, String> reqmap) {

        GasStationTb gasStationTb = new GasStationTb();
        gasStationTb.setState( 0 );
        JSONObject result = supperSearchService.supperSearch(gasStationTb, reqmap);

        return result;
    }

    /**
     *  注册/修改加油站信息
     */
    @Override
    public String create(HttpServletRequest request) {

        //接收前台参数信息
        Long id  = RequestUtil.getLong( request, "id",-1L );
        String name = RequestUtil.processParams( request,"name" );
        String address =RequestUtil.processParams( request,"address" );
        Integer device_total = RequestUtil.getInteger( request,"device_total",1 );
        String phone = RequestUtil.processParams( request,"phone" );
        String linkman = RequestUtil.processParams( request,"linkman" );

        //基本信息封装
        GasStationTb gasStationTb = new GasStationTb();
        gasStationTb.setName( name );
        gasStationTb.setState( 0 );
        gasStationTb.setAddress( address );
        gasStationTb.setLinkman( linkman );
        gasStationTb.setPhone( phone );
        gasStationTb.setDeviceTotal( device_total );
        int count=0;

        //如果id不为-1  则表示需要修改加油站信息
        if(id==-1){
            //注册加油站操作
            //生成随机密钥
            String key = UUID.randomUUID().toString().replace( "-","" );
            gasStationTb.setKey( key );
            //注册时间
            gasStationTb.setCreateTime( System.currentTimeMillis()/1000 );
            count = commonDao.insert( gasStationTb );
        }else{
            //修改加油站信息
            gasStationTb.setId( id );
            count = commonDao.updateByPrimaryKey( gasStationTb );
        }

        return "{\"state\":" + count + "}";
    }

    @Override
    public String delete(HttpServletRequest request) {
        Long id = RequestUtil.getLong( request, "id", -1L );
        int delete = 0;
        if (id > 0) {
            GasStationTb gasStationTb = new GasStationTb();
            gasStationTb.setId( id );
            gasStationTb.setState( 1 );
            //删除操作将state状态修改为1
            delete = commonDao.updateByPrimaryKey( gasStationTb );
        }
        return "{\"state\":" + delete + "}";
    }


}
