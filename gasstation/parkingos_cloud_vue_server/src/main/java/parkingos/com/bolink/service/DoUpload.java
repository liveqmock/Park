package parkingos.com.bolink.service;


/**
 * @author liuqb
 * @date  2017-3-31
 */
public interface DoUpload {

	String checkTokenSign(String token, String sign, String data);

	String doLogin(String data, String sign, String ip);

	String logout(String parkId, String soureIp);

	String gateResult(String data);

}

