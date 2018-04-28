package parkingos.com.bolink.actions;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import parkingos.com.bolink.service.AdvertisemenetService;
import parkingos.com.bolink.utlis.RequestUtil;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.util.UUID;

@Controller
@RequestMapping("/advertisement")
/**
 * 上传广告
 */
public class UploadadvertisementAction {

    Logger logger = Logger.getLogger(UploadadvertisementAction.class);

    @Autowired
    private AdvertisemenetService advertisemenetService;

    //上传文件会自动绑定到MultipartFile中
    @RequestMapping(value="/upload",method= RequestMethod.POST)
    public String upload(HttpServletRequest request,
                         @RequestParam("file") MultipartFile file) throws Exception {

        String result = advertisemenetService.upload(request,file);
        logger.info( "文件上传结果>>>>>"+result );
        return result;
    }


    //上传文件会自动绑定到MultipartFile中
    @RequestMapping(value="/addParkId",method= RequestMethod.POST)
    public String addParkId(HttpServletRequest request) throws Exception {

        String result = advertisemenetService.addParkId(request);

        return result;
    }
}
