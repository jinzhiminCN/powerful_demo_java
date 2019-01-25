package algorithm.filter;

import java.util.List;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * @author jinzhimin
 * @description:
*/
public class FilterUtil {
	private static Logger logger = LogManager.getLogger(FilterUtil.class.getName());
	
	private static int bitSetSize = Integer.MAX_VALUE;
	private static int expectedNumberOElements = Integer.MAX_VALUE - 1;
	private static int hashNumber = 32;
	
	private static BloomFilter filter = null;
	
	static{
		if(null == filter){
			filter = new BloomFilter(bitSetSize, expectedNumberOElements, hashNumber);
		}
	}
	/**
	 * 像过滤器中添加一个元素
	 * @param key
	 */
	public static void add(String key){
		if(key != null){
			filter.add(key.getBytes());
		}
	}
	
	/**
	 * 像过滤器中添加一个列表元素
	 * @param keys
	 */
	public static void add(Set<String> keys){
		for(String key:keys){
			add(key);
		}
		logger.info("向BloomFilter过滤器中添加了：" + keys.size() + "条去重信息！");
	}
	
	/**
	 * 像过滤器中添加一个列表元素
	 * @param keys
	 */
	public static void add(List<String> keys){
		for(String key:keys){
			add(key);
		}
	}
	
	/**
	 * 检查一个值是否存在过滤器中
	 * @param key
	 * @return
	 */
	public static boolean contains(String key){
		return filter.contains(key.getBytes());
	}

}
