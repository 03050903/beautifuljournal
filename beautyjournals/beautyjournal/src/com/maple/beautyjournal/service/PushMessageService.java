package com.maple.beautyjournal.service;


import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import com.maple.beautyjournal.ArticleDetailActivity;
import com.maple.beautyjournal.ProductDetailActivity;
import com.maple.beautyjournal.R;
import com.maple.beautyjournal.accessor.AccessorResultWrapper;
import com.maple.beautyjournal.accessor.AppUtilsAccessor;
import com.maple.beautyjournal.accessparser.AppUtilsParser;
import com.maple.beautyjournal.entitiy.BjNotification;
import com.maple.beautyjournal.utils.SettingsUtil;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;
import android.text.TextUtils;
import android.util.Log;

public class PushMessageService extends Service {

	@Override
	public void onCreate() {
		super.onCreate();		
		Log.d("PushMessageService", "onCreate");
	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
	
	private static final int PushMessageNotificationID=1020;
	
	@Override
	public int onStartCommand (Intent intent, int flags, int startId){
		super.onStartCommand(intent, flags, startId);
		Log.d("PushMessageService", "onStartCommand");
		new reqPushMessageTask().execute();
		return START_STICKY;		
	}
	
	class reqPushMessageTask extends AsyncTask<Object, Integer, AccessorResultWrapper>{

		@Override
		protected AccessorResultWrapper doInBackground(Object... arg0) {
			AccessorResultWrapper message = AppUtilsAccessor.getPushMessage(PushMessageService.this);
			return message;
		}
		
		@Override
		protected void onPreExecute(){
		}
		
		
		@Override
		protected void onPostExecute(AccessorResultWrapper result){	
			if(result.isSuccess){
				
				BjNotification notify = AppUtilsParser.parseNotification(result.result);
				Long lastNotifyTime = SettingsUtil.getLastNotificationTime(PushMessageService.this);
				
				Long newTime = 0L;
				try {
					if(!TextUtils.isEmpty(notify.timestamp)){
						DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
						Date newDate = df.parse(notify.timestamp);
						if(newDate != null){
							newTime = newDate.getTime();
						}
					}
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if(notify != null && !TextUtils.isEmpty(notify.timestamp) && newTime > lastNotifyTime ){

					Notification notification = new Notification();
					notification.flags = Notification.FLAG_AUTO_CANCEL;
					notification.icon = R.drawable.ic_launcher;
					notification.tickerText = getResources().getString(R.string.app_name);
					notification.when = System.currentTimeMillis();
					notification.defaults = Notification.DEFAULT_SOUND;

					if(!TextUtils.isEmpty(notify.articleId)){
						Intent intent = new Intent(PushMessageService.this,ArticleDetailActivity.class);
						intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
						intent.putExtra(ArticleDetailActivity.ARTICLE_ID_EXTRA, notify.articleId);
						final PendingIntent pending = PendingIntent.getActivity(PushMessageService.this, 0,
								intent, PendingIntent.FLAG_UPDATE_CURRENT);
						notification.setLatestEventInfo(PushMessageService.this,
								getResources().getString(R.string.app_name), notify.content, pending);
						((NotificationManager) getSystemService("notification")).notify(PushMessageNotificationID, notification);
						
						SettingsUtil.saveLastNotificationTime(PushMessageService.this, newTime);
					}else if(!TextUtils.isEmpty(notify.productId)) {
						Intent intent = new Intent(PushMessageService.this,ProductDetailActivity.class);
						intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
						intent.putExtra(ProductDetailActivity.PRODUCT_ID, notify.productId);
						final PendingIntent pending = PendingIntent.getActivity(PushMessageService.this, 0,
								intent, PendingIntent.FLAG_UPDATE_CURRENT);
						notification.setLatestEventInfo(PushMessageService.this,
								getResources().getString(R.string.app_name), notify.content, pending);
						((NotificationManager) getSystemService("notification")).notify(PushMessageNotificationID, notification);						
						SettingsUtil.saveLastNotificationTime(PushMessageService.this, newTime);
					}
				}
			}else{
				
			}	
		}
	}

	
	@Override
	public void onDestroy() {
		super.onDestroy();
	}

}
