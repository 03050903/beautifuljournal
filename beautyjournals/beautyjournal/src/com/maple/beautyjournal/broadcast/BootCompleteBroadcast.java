package com.maple.beautyjournal.broadcast;

import java.util.Calendar;
import java.util.Random;

import com.maple.beautyjournal.service.CheckUpdateService;
import com.maple.beautyjournal.service.PushMessageService;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;

public class BootCompleteBroadcast extends BroadcastReceiver {

	public static final String ACTION_APPBOOTCOMPLETED="com.maple.beautyjournal.BOOT_COMPLETED";
	private String ACTION_SYSTEM_BOOTCOMPLETED = "android.intent.action.BOOT_COMPLETED";
 
	private static final int PushMessageServiceRequestCode=119;
	
	@Override
	public void onReceive(final Context context, Intent intent) {
		Log.d("BootCompleteBroadcast", "received");
		if(ACTION_SYSTEM_BOOTCOMPLETED.equals(intent.getAction())){			
			setAlarmManagerForPushMessageService(context);
		}else if(ACTION_APPBOOTCOMPLETED.equals(intent.getAction())){
			Log.d("BootCompleteBroadcast", "ACTION_APPBOOTCOMPLETED");
			setAlarmManagerForPushMessageService(context);

			//通知检查
			new Handler().postDelayed(
				new Runnable(){
					@Override
					public void run() {
						Intent pushMessageService = new Intent(context,PushMessageService.class);
						context.startService(pushMessageService);
					}
				}, 4000);
						
			//更新检查
			new Handler().postDelayed(
				new Runnable(){
					@Override
					public void run() {
						Intent checkUpdate = new Intent(context,CheckUpdateService.class);
						context.startService(checkUpdate);
					}
				},6000);
		}
	}
	
	private void setAlarmManagerForPushMessageService(Context context){
		AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		String notifyTime = "8:0";

		String[] timeItems = notifyTime.split(":");
		int hour = Integer.valueOf(timeItems[0]);
		int minute = Integer.valueOf(timeItems[1]);
		
		Random rand = new Random(System.currentTimeMillis());
		minute = minute + rand.nextInt(60);
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.HOUR_OF_DAY, hour);
		calendar.set(Calendar.MINUTE, minute);
		calendar.set(Calendar.SECOND, 0);
		
		Intent msgService = new Intent(context, PushMessageService.class);			
		PendingIntent pendingIntent = PendingIntent.getService(context, PushMessageServiceRequestCode, msgService, PendingIntent.FLAG_UPDATE_CURRENT);
		alarmManager.cancel(pendingIntent);
		
		//Intent checkUpdate = new Intent(context, CheckUpdateService.class);			
		//PendingIntent checkupdatePendingIntent = PendingIntent.getService(context, 0, checkUpdate, PendingIntent.FLAG_UPDATE_CURRENT);
		//alarmManager.cancel(checkupdatePendingIntent);
		
		alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_HALF_DAY, pendingIntent);

	}

}
