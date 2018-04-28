package parkingos.com.bolink.actions;

import com.alibaba.fastjson.JSONObject;
import com.sun.org.apache.xpath.internal.operations.Bool;
import io.netty.channel.Channel;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import parkingos.com.bolink.models.GasStationTb;
import parkingos.com.bolink.netty.NettyChannelMap;
import parkingos.com.bolink.service.GasStationService;
import parkingos.com.bolink.utils.RequestUtil;
import parkingos.com.bolink.utils.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

/**
 * 加油站信息
 */
@Controller
@RequestMapping("/gasStation")
public class GasStationAction {

    Logger logger = Logger.getLogger(GasStationAction.class);


    @Autowired
    private GasStationService gasStationService;
    /**
     * 加油站查询
     */
    @RequestMapping(value = "/quickquery")
    public String query(HttpServletRequest req, HttpServletResponse resp) {

        Map<String, String> reqParameterMap = RequestUtil.readBodyFormRequset(req);

        JSONObject result = gasStationService.selectResultByConditions(reqParameterMap);

        //查询是否掉线
        List<Map<String,Object>> list = getConnectionState(req,result);

        result.put( "rows",list );
        //把结果返回页面
        StringUtils.ajaxOutput(resp,result.toJSONString());
        return null;
    }

    /**
     * 加油站添加/修改
     */
    @RequestMapping(value = "/create")
    public String create(HttpServletRequest req, HttpServletResponse resp) {
        String result = gasStationService.create( req );
        logger.info( result );
        StringUtils.ajaxOutput( resp, result );
        return null;
    }

    /**
     * 删除加油站
     */

    @RequestMapping("/delete")
    public String delete(HttpServletRequest request, HttpServletResponse resp) {
        String result = gasStationService.delete( request );
        logger.info( result );
        StringUtils.ajaxOutput( resp, result );
        return null;
    }


    //查询加油站连接服务器状态
    private List<Map<String,Object>> getConnectionState(HttpServletRequest req, JSONObject result) {
        String data = result.getString( "rows" );
        List<Map<String,Object>> list = (List<Map<String, Object>>) JSONObject.parse( data );
        for(Map<String,Object> map:list){
            Object id = map.get( "id" );
            //boolean isSave = NettyChannelMap.isSaveChannel( id+"" );
            Channel channel = NettyChannelMap.get( id + "" );
            if (channel != null && channel.isActive()&& channel.isWritable()) {
                 map.put( "connectionState",true );
            } else{
                map.put( "connectionState",false );
            }

        }
        return list;
    }

}
