package parkingos.com.bolink.actions;

import com.alibaba.fastjson.JSONObject;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import parkingos.com.bolink.service.LoginService;
import parkingos.com.bolink.utils.Encryption;
import parkingos.com.bolink.utils.StringUtils;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 管理员后台登陆
 */
@Controller
@RequestMapping("/user")
public class LoginAction {

	Logger logger = Logger.getLogger(LoginAction.class);

	@Autowired
	private LoginService loginService;

	@RequestMapping(value = "/dologin")
	public String dologin(HttpServletRequest req, HttpServletResponse resp) throws IOException {


		String username = req.getParameter("username");
		String cpasswd = req.getParameter("password");

	    String passwd = Encryption.decryptToAESPKCS5(cpasswd, Encryption.KEY);

		//根据用户名查询用户返回result
		JSONObject result = loginService.getResultByUserNameAndPass(username,passwd);

		logger.info(result);
		StringUtils.ajaxOutput(resp,result.toJSONString());
		return null;
	}
}