package solr;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.tree.TreeNode;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.restlet.security.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 常用工具类
 * @author fbxu
 */
public class Utils {
	
	public final static Pattern  REG_HTML =  Pattern.compile("<[^>]+>");
	
	//标签分隔符
	public final static Pattern TAG_SPLIT = Pattern.compile("[\\s,，]+");
	
	/**
	 * 中文 汉字表达式,字符串必须全部为中文
	 */
	public final static Pattern REG_CHINESE_CHARACTER = Pattern.compile("^[\u2E80-\u9FFF]+$");
	
	/**
	 * 匹配任何空白字符，包括空格、制表符、换页符等等。等价于[\f\n\r\t\v]
	 */
	public final static Pattern REG_BLANK = Pattern.compile("[\\s]+");
	
	/**
	 * 以单词字符开头的字符串
	 */
	public final static Pattern REG_START_WORD = Pattern.compile("^[\\w]+.*");
	
	/**
	 * 以中文字符开头的字符串
	 */
	public final static Pattern REG_START_CHARACTER = Pattern.compile("^[\u2E80-\u9FFF]+.*");
	
	private final static Logger logger = LoggerFactory.getLogger(Utils.class);
	/**
	 * 判断对象是否为空指针
	 * @param obj
	 * @return
	 */
	public static boolean isNULL(Object obj){
		return obj == null;
	}

	/**
	 * 是否为空串 '', null
	 * @param str
	 * @return
	 */
	public static boolean isEmpty(String str) {
		return (str == null || str.length() == 0);
	}
	
	/**
	 * 是否为有内容,不能为空串
	 * @param str
	 * @return
	 */
	public static boolean hasText(String str) {
		return (str != null && str.trim().length() > 0);
	}
	
	public static void P(Object o) {
		boolean debug = true;
		
		if(debug){
			System.out.println(String.valueOf(o));
		}
	}
	
	/**
	 * 判断集合是否为空
	 * @return
	 */
	public static boolean isEmpty(Collection<?> col){
		return (col == null || col.isEmpty()); 
	}
	
	/**
	 * 判断集合是否为空
	 * @return
	 */
	public static boolean notEmpty(Collection<?> col){
		return !isEmpty(col); 
	}
	
	/**
	 * 默认值
	 * @param value
	 * @param def
	 * @return
	 */
	public static <T> T defaultValue(T value,T def) {
		return value != null?value:def;
	}
	
	public static <K,V> Map<K,V> createMap(K key,V value){
		Map<K,V> m = new HashMap<K,V>();
		m.put(key, value);
		return m;
	}
	
	/**
	 * 截取定长字符串,一个汉字占三个字节
	 * @param str
	 * @param size
	 * @return
	 */
	public static String cutstr(String str,int size) {
//		if(str == null || str.length() < size){
//			return str;
//		}
//		return str.substring(0, size);

		if (str == null || "".equals(str)) {  
            return "";  
        }  

        int d = 0; // byte length  
        int n = 0; // char length  
        for (; n < str.length(); n++) {  
            d = (int) str.charAt(n) > 256 ? d + 3 : d + 1;  
            if (d > size) {  
                break;  
            }  
        }  
  
        if (d > size) {
            return str.substring(0, n > 0 ? n : 0);  
        }  
  
        return str = str.substring(0, n);
	
	}
	
	/**
	 * 截取字符串,缩写形式;一个汉字算两个长度
	 * @param str
	 * @param width
	 * @param ellipsis
	 * @return
	 */
	public static String abbreviate(String str, int width, String ellipsis){
		if (str == null || "".equals(str)) {  
            return "";  
        }
		
		if(Utils.isNULL(ellipsis)){
			ellipsis = "...";
		}
		
        int d = 0; // byte length  
        int n = 0; // char length  
        for (; n < str.length(); n++) {  
            d = (int) str.charAt(n) > 256 ? d + 2 : d + 1;  
            if (d > width) {  
                break;  
            }  
        }  
  
        if (d > width) {  
            n = n - ellipsis.length() / 2;  
            return str.substring(0, n > 0 ? n : 0) + ellipsis;  
        }  
  
        return str = str.substring(0, n);
	}
	
	/**
	 * 提取html中的文本内容
	 * @param str
	 * @return
	 */
	public static String html2Text(String str){
		if(Utils.hasText(str)){
			 Matcher m = REG_HTML.matcher(str);
			 return m.replaceAll("");
		}
		return str;
	}
	
	/**
	 * html中特殊字符，如&lt; 转换为'<' &nbsp;转换为' '
	 * @param str
	 * @return
	 */
	public static String unescapeHtml(String str){
		if(Utils.hasText(str)){
			 return StringEscapeUtils.unescapeHtml(str);
		}
		return str;
	}
	
	/**
	 * 判断字符串是否都是数字
	 * @param str
	 * @return
	 */
	public static boolean isNumeric(String str){
		if(!Utils.hasText(str)){
			return false;
		}
		return StringUtils.isNumeric(str);
	}
	
	/**
	 * 将字符串转换为int类型
	 * @param s
	 * @param def
	 * @return
	 */
	public static Integer parseInt(String s,Integer def){
		if(Utils.isNumeric(s)){
			return Integer.parseInt(s);
		}
		return def;
	}
	
	/**
	 * 去除字符串前后空格
	 * @param str
	 * @return
	 */
	public static String trim(String str){
		if(str == null){
			return "";
		}
		return str.trim();
	}
	
	/**
	 * 将标签词转换为 空格 相隔的字符串
	 * @param tags
	 * @return
	 */
	public static String normalizeTags(String tags){
		if(Utils.isEmpty(tags)){
			return "";
		}
		Matcher m = TAG_SPLIT.matcher(tags);
		StringBuffer sb = new StringBuffer();
		while(m.find()){
			m.appendReplacement(sb, " ");
		}
		m.appendTail(sb);
		return sb.toString();
	}
	
	/**
	 * 转化为string类型 if(obj==null)return null;
	 * @param obj
	 * @return
	 */
	public static String toString(Object obj) {
		if(obj == null){
			return null;
		}
		return obj.toString();
	}
	public static Map<String,String> transformStringMap(Map<String,Object> m){
		Map<String,String> r = new HashMap<String,String>();
		Iterator<Entry<String, Object>> it = m.entrySet().iterator();
		while(it.hasNext()){
			Entry<String, Object> e = it.next();
			r.put(e.getKey(), Utils.toString(e.getValue()));
		}
		return r;
	}
	
	public static <T> T instantiate(Class<T> clazz) {
		try {
			return clazz.newInstance();
		} catch (Exception e) {
			logger.error("can't new Instance class{}", new Object[] { clazz.getName(), e });
		}
		return null;

	}
	
	/**
	 * 获取集合中第一个元素
	 * @param col
	 * @return
	 */
	public static <T> T getFirstElement(Collection<T> col){
		if(Utils.notEmpty(col)){
			return col.iterator().next();
		}
		throw new RuntimeException();
	}
	
	public static void main(String[] args) {
		Date date = new Date(1372665004000L);
		
		System.out.println(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date));
		
		Utils.P(Utils.normalizeTags(" ,,,,,共和 , 换行，         奋斗过的 ,"));// 共和 换行 奋斗过的 
		Utils.P(Utils.normalizeTags("qq ,,,,,共和 , 换行，         奋斗过的 ,"));//qq 共和 换行 奋斗过的
		
		Matcher m = REG_BLANK.matcher("共和  换行");
		
		
		boolean  matches = "_共和换行".matches("^[\\w]+.*");
		
		Utils.instantiate(Serializable.class);
				
	}
 	
	/**
	 * 将空格隔开的字符串转换为list
	 * @param text
	 * @return
	 */
	public static List<String> transferSpaceStringToList(String text){
		if(text == null || text.trim().equals("")){
			return new ArrayList<String>();
		}
		else{
			String[] strs = text.split(" ");
			List<String> list = new ArrayList<String>();
			for(int i=0;i<strs.length;i++){
				list.add(strs[i]);
			}
			return list;
		}
	}

	/**
	 * 获取请求参数中的中文参数
	 * 
	 * @param str
	 * @return
	 */
	public static String getRequestStrCHN(String str) {
		try {
			if (str != null && !"".equals(str) && !"undefined".equals(str)) {
				return new String(str.getBytes("ISO-8859-1"), "UTF-8");
			}
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}
		return "";
	}
	
	/**
	 * 数组转换成list通用方法
	 * @author xshen
	 * @param array
	 * @return
	 */
	public static <T> List<T> transferArrayToList(T[] array){
		List<T> list = new ArrayList<T>();
		if(array != null){
			for(int i=0;i<array.length;i++){
				list.add(array[i]);
			}
		}
		return list;
	}

	public static String decodeURL(String string) {
		if (string == null) {
			return null;
		}

		try {
			return URLDecoder.decode(string, "UTF-8");
		}
		catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return null;
	}
	
}
