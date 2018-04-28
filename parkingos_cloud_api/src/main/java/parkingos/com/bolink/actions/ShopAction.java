package parkingos.com.bolink.actions;

import com.alibaba.fastjson.JSONObject;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import parkingos.com.bolink.service.LoginService;
import parkingos.com.bolink.service.ShopService;
import parkingos.com.bolink.utlis.StringUtils;
import parkingos.com.bolink.utlis.weixinpay.utils.RequestUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/shop")
public class ShopAction {

    Logger logger = Logger.getLogger( LoginAction.class);

    @Autowired
    private ShopService shopService;

    /**
     * 广告商快速查询
     * @param req
     * @param resp
     * @return
     * @throws IOException
     */
    @RequestMapping(value = "/quickquery")
    public String dologin(HttpServletRequest req, HttpServletResponse resp) throws IOException {

        Map<String, String> reqParameterMap = RequestUtil.readBodyFormRequset(req);

        JSONObject result = shopService.selectResultByConditions(reqParameterMap);

        //把结果返回页面
        StringUtils.ajaxOutput(resp,result.toJSONString());
        return null;
    }

    /**
     * 广告商添加/修改
     */
    @RequestMapping(value = "/create")
    public String create(HttpServletRequest req, HttpServletResponse resp) {
        String result = shopService.create( req );
        logger.info( result );
        StringUtils.ajaxOutput( resp, result );
        return null;
    }

    /**
     * 删除广告商
     */

    @RequestMapping("/delete")
    public String delete(HttpServletRequest request, HttpServletResponse resp) {
        String result = shopService.delete( request );
        logger.info( result );
        StringUtils.ajaxOutput( resp, result );
        return null;
    }
}
