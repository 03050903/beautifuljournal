package com.i2mobi.net;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author fuwei
 * @description JSON对象转换成Object
 * @create_time 2012-7-19 下午12:30:22
 */
@SuppressWarnings({ "unchecked", "rawtypes" })
public class JSONUtil {

	/**
	 * @param jSONObject
	 *            JSON对象
	 * @param objectClass
	 *            转换的类
	 * @return 转换后的对象
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @create_time 2012-7-19 下午12:30:39
	 * @create_user fuwei
	 * @whattodo
	 * @modify_time like:date1/date2
	 * @modify_user like:user1/user2
	 * @modify_content like:content1/content2
	 */
	public static Object JSON2Object(JSONObject jSONObject, Class objectClass) {
		// 获取对象字段
		Object obj = null;

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try{
		obj = objectClass.newInstance();
		Field[] fields = objectClass.getDeclaredFields();
		for (Field field : fields) {
			try {
				field.setAccessible(true);
				String name = field.getName();
				String value = jSONObject.getString(name);
				if (jSONObject.get(name) == null && "".equals(value))
					continue;

				if (field.getType().equals(Long.class)
						|| field.getType().equals(long.class))
					field.set(obj, Long.parseLong(value));
				else if (field.getType().equals(String.class))
					field.set(obj, value);
				else if (field.getType().equals(Double.class)
						|| field.getType().equals(double.class))
					field.set(obj, Double.parseDouble(value));
				else if (field.getType().equals(Float.class)
						|| field.getType().equals(float.class))
					field.set(obj, Float.parseFloat(value));
				else if (field.getType().equals(Integer.class)
						|| field.getType().equals(int.class))
					field.set(obj, Integer.parseInt(value));
				else if (field.getType().equals(java.util.Date.class)) {
					if (value == null || value.equals("")
							|| value.equals("null"))
						field.set(obj, new Date());
					else
						field.set(obj, sdf.parse(value));
				} else
					field.set(
							obj,
							JSON2Object(jSONObject.getJSONObject(name),
									field.getType()));
			}  catch (Exception e) {
			    e.printStackTrace();
			}
		}
		}catch (Exception e) {
		    e.printStackTrace();
		}
		return obj;
	}

	public static Object JSON2Object(JSONObject jSONObject, Class objectClass,
			String[] exclusionField) {
		// 获取对象字段
		Object obj = null;
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			obj = objectClass.newInstance();
			Field[] fields = objectClass.getDeclaredFields();
			for (Field field : fields) {
				field.setAccessible(true);
				String name = field.getName();
				if (!exclusionField(exclusionField, name))
					continue;
				String value = jSONObject.getString(name);
				if (jSONObject.get(name) == null && "".equals(value))
					continue;

				if (field.getType().equals(Long.class)
						|| field.getType().equals(long.class))
					field.set(obj, Long.parseLong(value));
				else if (field.getType().equals(String.class))
					field.set(obj, value);
				else if (field.getType().equals(Double.class)
						|| field.getType().equals(double.class))
					field.set(obj, Double.parseDouble(value));
				else if (field.getType().equals(Integer.class)
						|| field.getType().equals(int.class))
					field.set(obj, Integer.parseInt(value));
				else if (field.getType().equals(java.util.Date.class))
					if (value == null || value.equals("")
							|| value.equals("null"))
						field.set(obj, new Date());
					else
						field.set(obj, sdf.parse(value));
				else
					field.set(
							obj,
							JSON2Object(jSONObject.getJSONObject(name),
									field.getType()));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return obj;
	}

	/**
	 * 判断name是否在不需要转化的对象中
	 * 
	 * @param exclusionField
	 * @param name
	 * @return
	 */
	private static boolean exclusionField(String[] exclusionField, String name) {
		boolean flag = true;
		for (int i = 0; i < exclusionField.length; i++) {
			if (name.equals(exclusionField[i]))
				return false;
		}
		return flag;
	}

	/**
	 * @param jSONString
	 *            JSON字符串
	 * @param objectClass
	 *            转换的对象
	 * @return Object对象
	 * @throws JSONException
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 * @create_time 2012-7-19 下午1:07:55
	 * @create_user fuwei
	 * @whattodo
	 * @modify_time like:date1/date2
	 * @modify_user like:user1/user2
	 * @modify_content like:content1/content2
	 */

	public static Object JSON2Object(String jSONString, Class objectClass) {
		JSONObject jsonObject = null;
		try {
			jsonObject = new JSONObject(jSONString);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return JSON2Object(jsonObject, objectClass);
	}

	public static List JSON2List(JSONArray jsonArray, Class objectClass,
			String[] exclusionField) {
		int length = jsonArray.length();
		List list = new ArrayList();
		try {
			for (int i = 0; i < length; i++) {
				JSONObject jsonObject = jsonArray.getJSONObject(i);
				Object obj = JSON2Object(jsonObject, objectClass,
						exclusionField);
				list.add(obj);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return list;
	}

	public static List JSON2List(JSONArray jsonArray, Class objectClass) {
		int length = jsonArray.length();
		List list = new ArrayList();
		try {
			for (int i = 0; i < length; i++) {
				JSONObject jsonObject = jsonArray.getJSONObject(i);
				Object obj = JSON2Object(jsonObject, objectClass);
				list.add(obj);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return list;
	}

	public static Map JSON2Map(JSONObject jSONObject, String[] keys,
			Class objectClass) {
		Map map = new HashMap();
		try {
			int length = keys.length;
			for (int i = 0; i < length; i++) {
				JSONArray jsonArray = (JSONArray) jSONObject.get(keys[i]);
				List list = JSON2List(jsonArray, objectClass);
				map.put(keys[i], list);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return map;
	}

}
