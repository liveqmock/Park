package parkingos.com.bolink.actions;

import com.alibaba.fastjson.JSONObject;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import parkingos.com.bolink.service.GasStationService;
import parkingos.com.bolink.service.LogsService;
import parkingos.com.bolink.utils.RequestUtil;
import parkingos.com.bolink.utils.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

/**
 * 日志查询
 */
@Controller
@RequestMapping("/logs")
public class QueryLogsAction {

    Logger logger = Logger.getLogger(GasStationAction.class);


    @Autowired
    private LogsService logsService;

    /**
     * 日志查询
     */
    @RequestMapping(value = "/quickquery")
    public String query(HttpServletRequest req, HttpServletResponse resp) {

        Map<String, String> reqParameterMap = RequestUtil.readBodyFormRequset(req);

        JSONObject result = logsService.selectResultByConditions(reqParameterMap);

        //把结果返回页面
        StringUtils.ajaxOutput(resp,result.toJSONString());
        return null;
    }
}
