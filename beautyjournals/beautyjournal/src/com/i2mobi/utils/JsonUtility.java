package com.i2mobi.utils;


import java.util.Date;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

public class JsonUtility {

	public static String getString(JSONObject json, String name) throws JSONException{
		
		return optString(json, name);
	}
	
	public static String optString(JSONObject json, String name) throws JSONException{
		if (json.isNull(name)){
			return null;
		}
		return json.optString(name);
	}
	
	public static Date getDate(JSONObject json, String name) throws IllegalArgumentException, JSONException {
		String valueText = getString(json, name);
		if (valueText == null){
			return null;
		}
		
		return getDate(valueText);
	}
	
	public static void put(JSONObject json, String name, Date date) throws JSONException {
		if (date != null){
			String value = "/Date(" + date.getTime() + ")/";
			json.put(name, value);
		} else {
			json.remove(name);
		}
	}
	
	public static void put(JSONObject json, String name, double d) throws JSONException {
		if (!Double.isNaN(d)){
			json.put(name, d);
		} else {
			json.remove(name);
		}
	}
	
	public static Date getDate(String text) throws IllegalArgumentException {
		final String prefix = "/Date(";
		final String suffix = ")/";
		if (!text.startsWith(prefix) || !text.endsWith(suffix)){
			throw new IllegalArgumentException("expect pattern \\/Date\\(\\d+\\)\\/: " + text);
		}
		
		int start = prefix.length();
		int end = text.length() - suffix.length();
		String longStr = text.substring(start, end);
		Long miliseconds = Long.parseLong(longStr);
		if (miliseconds == null){
			throw new IllegalArgumentException("cannot convert to long: " + longStr);
		}
		
		return new Date(miliseconds);
	}
	
	public static String[] optStringArray(JSONObject json, String name){
		JSONArray jsonArray = json.optJSONArray(name);
		if (jsonArray == null){
			return null;
		}
		String[] stringArray = new String[jsonArray.length()];
		for (int i = 0; i < stringArray.length; ++i){
			try {
				stringArray[i] = jsonArray.getString(i);
			} catch (JSONException e) {
				Log.e("JsonUtility", "optStringArray: fail to get string element at: " + i);
				e.printStackTrace();
				return null;
			}
		}
		return stringArray;
	}
	
	public static void put(JSONObject json, String name, String[] stringArray){
		if (stringArray == null){
			json.remove(name);
			return;
		}
		JSONArray jsonArray = new JSONArray();
		for (String s : stringArray){
			jsonArray.put(s);
		}
		
		try {
			json.put(name, jsonArray);
		} catch (JSONException e) {
			Log.e("JsonUtility", "putStringArray: " + e.getMessage());
			e.printStackTrace();
		}
	}
	
	public static JSONObject fromBytes(byte[] raw){

		if (raw == null){
			return null;
		}
		
		String jsonText = new String(raw);
		try {
			return new JSONObject(jsonText);
		} catch (JSONException e) {
			e.printStackTrace();
			Log.e("JsonUtility", "fromBytes: new JSONObject: " + e.getMessage());
			return null;
		}
	}
}
