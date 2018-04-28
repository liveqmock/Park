package parkingos.com.bolink.actions;

import com.alibaba.fastjson.JSONObject;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import parkingos.com.bolink.beans.AdvertisementTb;
import parkingos.com.bolink.service.AdvertiserService;
import parkingos.com.bolink.utlis.StringUtils;
import parkingos.com.bolink.utlis.weixinpay.utils.RequestUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

/**
 * 广告添加
 */
@Controller
@RequestMapping("/advertiser")
public class AdvertiserAction {

    Logger logger = Logger.getLogger( LoginAction.class);

    @Autowired
    private AdvertiserService advertiserService;

    /**
     * 广告商快速查询
     * @param req
     * @param resp
     * @return
     * @throws IOException
     */
    @RequestMapping(value = "/quickquery")
    public String quickquery(HttpServletRequest req, HttpServletResponse resp) throws IOException {

        Map<String, String> reqParameterMap = RequestUtil.readBodyFormRequset(req);

        JSONObject result = advertiserService.selectResultByConditions(reqParameterMap);

        //把结果返回页面
        StringUtils.ajaxOutput(resp,result.toJSONString());
        return null;
    }

    /**
     * 广告商绑定停车场
     */
    @RequestMapping(value = "/create")
    public String create(HttpServletRequest req, HttpServletResponse resp) {
        String result = advertiserService.create( req );
        logger.info( result );
        StringUtils.ajaxOutput( resp, result );
        return null;
    }

    /**
     * 解除绑定
     */

    @RequestMapping("/delete")
    public String delete(HttpServletRequest request, HttpServletResponse resp) {
        String result = advertiserService.delete( request );
        logger.info( result );
        StringUtils.ajaxOutput( resp, result );
        return null;
    }

    //上传文件会自动绑定到MultipartFile中
    @RequestMapping(value="/upload",method= RequestMethod.POST)
    public String upload(HttpServletRequest request,
                         @RequestParam("file") MultipartFile file,HttpServletResponse resp) throws Exception {

        String result = advertiserService.upload(request,file);
        logger.info( "文件上传结果>>>>>"+result );
        StringUtils.ajaxOutput( resp, result );
        return null;
    }

    @RequestMapping(value = "/advertisementQuery")
    public String advertisementQuery(HttpServletRequest req, HttpServletResponse resp) throws IOException {

        Map<String, String> reqParameterMap = RequestUtil.readBodyFormRequset(req);

        JSONObject result = advertiserService.selectAdvertisementByConditions(reqParameterMap);

        //把结果返回页面
        StringUtils.ajaxOutput(resp,result.toJSONString());
        return null;
    }

    @RequestMapping(value = "/edit")
    public String edit(HttpServletRequest req, HttpServletResponse resp) throws IOException {


        String result = advertiserService.edit(req);

        //把结果返回页面
        StringUtils.ajaxOutput(resp,result);
        return null;
    }

    @RequestMapping(value = "/deleteAdv")
    public String deleteAdv(HttpServletRequest req, HttpServletResponse resp) throws IOException {


        String result = advertiserService.deleteAdv(req);

        //把结果返回页面
        StringUtils.ajaxOutput(resp,result);
        return null;
    }

}
