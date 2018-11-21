package utils;

import com.google.gson.*;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author jinzhimin
 * @description: Gson工具类。
*/

public class GsonUtil {
	private static Logger logger = LoggerFactory.getLogger(GsonUtil.class);

	public static Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").setPrettyPrinting().create();

	public static Gson getGson() {
		return gson;
	}

	public static String getElementValue(JsonElement jsonElement, String attribute) {
		String value = null;

		if (jsonElement == null) {
			return null;
		}

		if (StringUtils.isAnyBlank(attribute)) {
			return null;
		}

		if (isValidJson(jsonElement)) {
			JsonObject jsonObject = (JsonObject) jsonElement;
			JsonElement jsonEle = jsonObject.get(attribute);
			if (isValidJson(jsonEle)) {
				value = jsonEle.getAsString();
			}
		}

		return value;
	}

	public static boolean isJsonNull(JsonElement jsonElement) {
		boolean result = false;

		if (jsonElement == null || jsonElement.isJsonNull()) {
			result = true;
		} else {
			JsonObject jsonObject = (JsonObject) jsonElement;
			String value = jsonObject.toString();
			if (value.equals("") || value.equals("{}") || value.equals("[]")) {
				result = true;
			}
		}

		return result;
	}

	public static boolean isValidJson(JsonElement jsonElement) {
		return !isJsonNull(jsonElement);
	}

	public static void main(String[] args) {
		String value = "{\"repetitiveList\":[{\"name\":\"中国移动通信集团公司\","
				+ "\"code\":\"911100007109250324\",\"regNo\":\"100000000032057\","
				+ "\"orgNo\":null}],\"norepetitiveList\":[]}";

		JsonObject jsonObj = gson.fromJson(value, JsonObject.class);

		JsonArray repetArray = (JsonArray) jsonObj.get("repetitiveList");

		for (JsonElement repetEle : repetArray) {
			String orgNo = getElementValue(repetEle, "orgNo");
			logger.info(orgNo);
		}
	}

}
