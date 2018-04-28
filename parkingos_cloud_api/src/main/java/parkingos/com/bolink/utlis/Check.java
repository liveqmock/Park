package parkingos.com.bolink.utlis;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 
 * 
 * 项目名称：callback 类名称：Check 类描述： 检测一个号码是手机号或者电话号,uin号等等方法 创建人：shanyz 创建时间：Apr 3,
 * 2010 2:54:36 PM 修改人：shanyz 修改时间：Apr 3, 2010 2:54:36 PM 修改备注：
 *
 * @version
 * 
 */
public class Check {

	public static boolean checkPhone(String phonenumber) {
		if (phonenumber == null || phonenumber.equals(""))
			return false;
		String phone = "0\\d{2,3}\\d{7,8}";//带区号
		String shortPhone="\\d{7,8}";//不带区号
		Pattern p = Pattern.compile(phone);
		Matcher m = p.matcher(phonenumber);
		if(!m.matches()){
			p = Pattern.compile(shortPhone);
			m=p.matcher(phonenumber);
		}
		return m.matches();
	}

	public static boolean checkMobile(String mobilenumber) {
		if (mobilenumber == null || mobilenumber.equals(""))
			return false;
		String mobile = "^((\\+{0,1}0){0,1})1[0-9]{10}";
		Pattern p = Pattern.compile(mobile);
		Matcher m = p.matcher(mobilenumber);
		return m.matches();
	}

	public static boolean checkUin(String uin) {
		if (uin == null || uin.equals(""))
			return false;
		Pattern pattern = Pattern.compile("[0-9]*");
		Matcher m = pattern.matcher(uin);
		  return m.matches();
	}
	/**
	 * 
	* checkMobileHead   
	* 检测 手机是否以0开头，如果开头则去掉0
	* @param  @return    
	* @return String     
	* @Exception   
	* @since  CodingExample　Ver 1.1
	 */
	public static String checkMobileHead(String mobilenumber) {
		if (mobilenumber == null || mobilenumber.equals(""))
			return null;
		if(checkMobile(mobilenumber)&&mobilenumber.startsWith("0")) {
			mobilenumber=mobilenumber.substring(1, mobilenumber.length());
		}
		  return mobilenumber;
	}
	/**
	 * 
	* getKufuName   
	* 通过工单表 talk_content 中 得到客服姓名
	* @param  @return    
	* @return String     
	* @Exception   
	* @since  CodingExample　Ver 1.1
	 */
	public static String  getKufuName(String talk_content) {
		if(talk_content==null){
			return "";
		}
		String pString="<br>+[^<br>]+\\(\\d{4,8}\\)";//匹配结果:<br>望小忆(1086)
		String resultString="";
		Pattern p = Pattern.compile(pString);
		Matcher m = p.matcher(talk_content);
		if(m.find()) {
			resultString=m.group();
			resultString=resultString.substring(resultString.indexOf("<br>")+4, resultString.indexOf("("));
		}		
		return resultString;
	}
	
	/**
	 * 检查URL
	 * @param url
	 * @return
	 */
	public static boolean checkURL(String url){
		Pattern exp= Pattern.compile("^(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]",Pattern.CASE_INSENSITIVE);
		return exp.matcher(url).matches();
	}
	
	public static void main(String[] args) {
		String url ="http://test.server.lembee.com/bolian/inPark/notify?a=1";
		System.err.println(checkURL(url));
	}
/**
 * 
* dealPhone
* @param  @return    
* @return String     
* @Exception   
* @since  CodingExample　Ver 1.1
 */
	public static String dealPhone(String phone) {
		if (phone.length() > 13) {//表明为手机加区号
			String[] quhaoStrings = { "010", "020", "021", "022", "023", "024",
					"025", "027", "028", "029" };
			String result = null;
			for (int i = 0; i < quhaoStrings.length; i++) {
				if (phone.startsWith(quhaoStrings[i])) {
					result = phone.substring(3, phone.length());
					break;
				}
			}
			if (result == null)
				result = phone.substring(4, phone.length());
			return result;
		} else {
			return phone;
		}

	}
	/**验证电话 
	 * 手机是1开头，第二位是3、4、5、8，长度11位数字，或以0开头，其它与前面一样，12位数字
	 * 正确有1340000011或013269710010(长途)
	 * 固定电话是在2到9开头长度是7到8位数字或以0开头第二位是1到9，长度是11到12位数字，
	 * 不能是非数字，不能带分机号，
	 * 如010-88998899或010-88998899-22或(010)88999933
	 * Params:type:"m"手机,"t"固定电话
	*/
    public static boolean checkPhone(String phone,String type){  
    	String teleReg ="^(0[1-9]{1}\\d{9,10})|([2-9]\\d{6,7})$";
		String mobilReg = "^(1[3-8]\\d{9})|(01[3-8]\\d{9})$";
		if(type.equals("m"))
			return phone.matches(mobilReg);
		else if(type.equals("t"))
			return phone.matches(teleReg);
		return false;
    } 
    /**
     * 判断是否是整型
     * @param value
     * @return
     */
    public static boolean isNumber(String value){
    	// int -2147483648－－2147483647
    	if(value==null)
    		return false;
    	if(value.length()>9&&isLong(value)){
    		Long  l = Long.parseLong(value);
    		if(l<=Integer.MAX_VALUE)
    			return true;
    	}
    	return value.matches("^\\d{1,9}");
    }
    /**
     * 判断是否是长整型
     * @param value
     * @return
     */
    public static boolean isLong(String value){
    	if(value==null)
    		return false;
    	if( value.matches("^\\d+")){
    		try {
				if (Long.parseLong(value) <= Long.MAX_VALUE)
					return true;
			} catch (Exception e) {
				return false;
			}
    	}
    	return false;
    }
    
    public static boolean checkEmail(String value) {
    	if(value == null) {
    		return false;
    	} 
    	return value.matches("^\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*$");
    }
    
    /**
     * 判断是否是长整型
     * @param value
     * @return
     */
    public static boolean isDouble(String value){
    	if(value==null)
    		return false;
		try {
			Double d = Double.valueOf(value);
			return true;
		} catch (Exception e) {
			return false;
		}
    }
    /**
	 * 判断字符串是否为空
	 * <ul>
	 * <li>isEmpty(null) = true</li>
	 * <li>isEmpty("") = true</li>
	 * <li>isEmpty("   ") = true</li>
	 * <li>isEmpty("abc") = false</li>
	 * </ul>
	 * 
	 * @param value
	 *            目标字符串
	 * @return true/false
	 */
	public static boolean isEmpty(String value) {
		int strLen;
		if (value == null || (strLen = value.length()) == 0|| "null".equals(value)) {
			return true;
		}
		for (int i = 0; i < strLen; i++) {
			if ((Character.isWhitespace(value.charAt(i)) == false)) {
				return false;
			}
		}
		return true;
	}
}
