package parkingos.com.bolink.Interceptor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import parkingos.com.bolink.dao.spring.CommonDao;
import parkingos.com.bolink.models.TokenTb;
import parkingos.com.bolink.models.UserInfoTb;
import parkingos.com.bolink.utils.RequestUtil;
import parkingos.com.bolink.utils.TokenUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 简单权限校验
 */
public class SecurityInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o) throws Exception {

        String token = RequestUtil.getString( httpServletRequest, "token" );

        UserInfoTb user = TokenUtil.getUser( token );
        if(user!=null){
            return true;
        }
        return false;
    }

    @Override
    public void postHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, ModelAndView modelAndView) throws Exception {
    }

    @Override
    public void afterCompletion(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, Exception e) throws Exception {
    }
}
