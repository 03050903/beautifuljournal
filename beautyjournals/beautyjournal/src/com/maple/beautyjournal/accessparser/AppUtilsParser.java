package com.maple.beautyjournal.accessparser;

import org.json.JSONObject;

import com.maple.beautyjournal.entitiy.BjNotification;
import com.maple.beautyjournal.entitiy.BjVersion;

public class AppUtilsParser {

	public static BjVersion parseVersionInfo(JSONObject result){

		BjVersion version = new BjVersion();
		version.version_changelog = result.optString("version_changelog");
		version.download_link = result.optString("download_link");
		version.force_update = result.optInt("force_update")==1;
		version.for_release = result.optInt("for_release")==1;
		version.version_id = result.optInt("version_id");
		version.version_num = result.optString("version_num");
		version.update_hint = result.optString("update_hint");

		return version;
	}
	
	public static BjNotification parseNotification(JSONObject result){
		BjNotification notification = new BjNotification();
		notification.id = result.optInt("id");
		notification.articleId = result.optString("article_id");
		notification.productId = result.optString("product_id");
		notification.content = result.optString("content");
		notification.timestamp = result.optString("time_stamp");
		return notification;

	}
}
