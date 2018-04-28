package parkingos.com.bolink.service;

import com.alibaba.fastjson.JSONObject;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Map;

public interface AdvertiserService {
    JSONObject selectResultByConditions(Map<String, String> reqParameterMap);

    String create(HttpServletRequest req);

    String delete(HttpServletRequest request);

    String upload(HttpServletRequest request, MultipartFile file) throws IOException;

    JSONObject selectAdvertisementByConditions(Map<String, String> reqParameterMap);

    String edit(HttpServletRequest req);

    String deleteAdv(HttpServletRequest req);
}
