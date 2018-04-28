package parkingos.com.bolink.actions;

import com.alibaba.fastjson.JSONObject;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import parkingos.com.bolink.service.SyncGasStationService;
import parkingos.com.bolink.utils.RequestUtil;
import parkingos.com.bolink.utils.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;
import java.util.Map;


@Controller
@RequestMapping("/gasStation")
public class SyncGasStationAction {

    Logger logger = Logger.getLogger(SyncGasStationAction.class);

    @Autowired
    private SyncGasStationService gasStationService;

    @RequestMapping("/sendMessage")
    public String sendMessage(HttpServletRequest request, HttpServletResponse response) throws UnsupportedEncodingException {

        //设置字符编码
        request.setCharacterEncoding( "UTF-8" );
        response.setContentType("text/html;charset=UTF-8");

        //将request的所有参数封装到map集合
        Map<String, String> reqParameterMap = RequestUtil.readBodyFormRequset( request );
        logger.info( "接收到加油站云平台消息:"+reqParameterMap  );

        //业务层处理
        JSONObject result = gasStationService.sendMessage( reqParameterMap );
        logger.info( "返回加油站云平台消息:"+result );

        //返回信息到加油站云平台
        StringUtils.ajaxOutput( response, result.toJSONString() );
        return null;
    }

}
