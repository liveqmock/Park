package parkingos.com.bolink.actions;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.security.MessageDigest;
import java.util.Arrays;

/**
 * 微信服务器认证方法
 */
@Controller
@RequestMapping("/wx")
public class WeixinLogin {

    private static final String token = "ist";


    @RequestMapping(value = "/login",method = RequestMethod.GET)
    public String login(HttpServletRequest req, HttpServletResponse response) throws IOException {
        System.out.println("get...");
        String signature = req.getParameter("signature");
        String timestamp = req.getParameter("timestamp");
        String nonce = req.getParameter("nonce");
        String echostr = req.getParameter("echostr");

        PrintWriter out = response.getWriter();
        if(checkSignature(signature, timestamp, nonce)){
            out.print(echostr);
        }
        return null;
    }

    @RequestMapping(value = "/login",method = RequestMethod.POST)
    public String post(HttpServletRequest req, HttpServletResponse response) throws IOException {
        System.out.println("post...");


        return null;
    }


    public static boolean checkSignature(String signature,String timestamp,String nonce){
        String[] arr = new String[]{token,timestamp,nonce};
        //排序
        Arrays.sort(arr);

        //生成字符串
        StringBuffer content = new StringBuffer();
        for(int i=0;i<arr.length;i++){
            content.append(arr[i]);
        }

        //sha1加密
        String temp = getSha1(content.toString());

        return temp.equals(signature);
    }

    /**
     * Sha1加密方法
     * @param str
     * @return
     */
    public static String getSha1(String str) {
        if (str == null || str.length() == 0) {
            return null;
        }
        char hexDigits[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
                'a', 'b', 'c', 'd', 'e', 'f' };

        try {
            MessageDigest mdTemp = MessageDigest.getInstance("SHA1");
            mdTemp.update(str.getBytes("UTF-8"));

            byte[] md = mdTemp.digest();
            int j = md.length;
            char buf[] = new char[j * 2];
            int k = 0;
            for (int i = 0; i < j; i++) {
                byte byte0 = md[i];
                buf[k++] = hexDigits[byte0 >>> 4 & 0xf];
                buf[k++] = hexDigits[byte0 & 0xf];
            }
            return new String(buf);
        } catch (Exception e) {
            return null;
        }
    }
}
