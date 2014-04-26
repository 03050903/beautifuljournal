package com.maple.beautyjournal.service;

import com.maple.beautyjournal.R;
import com.maple.beautyjournal.accessor.AccessorResultWrapper;
import com.maple.beautyjournal.accessor.AppUtilsAccessor;
import com.maple.beautyjournal.accessparser.AppUtilsParser;
import com.maple.beautyjournal.entitiy.BjVersion;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

public class CheckUpdateService extends Service {
	private NotificationManager nm;
	private Notification notification;
	private String versionNum;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onStart(Intent intent, int startId) {
		Log.d("chekUpdateService", "onStart");
		super.onStart(intent, startId);
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {

		Log.d("chekUpdateService", "onStartCommand");
		versionNum = getVersionNum(this);
		new CheckUpdateTask().execute();

		return super.onStartCommand(intent, flags, startId);
	}

	class CheckUpdateTask extends
			AsyncTask<Object, Integer, AccessorResultWrapper> {

		@Override
		protected AccessorResultWrapper doInBackground(Object... params) {
			AccessorResultWrapper message = AppUtilsAccessor
					.getLatestVerstion(CheckUpdateService.this);
			return message;
		}

		@Override
		protected void onPreExecute() {
		}

		@Override
		protected void onPostExecute(AccessorResultWrapper result) {
			if (result.isSuccess) {				
				BjVersion version = AppUtilsParser.parseVersionInfo(result.result);				
				if (!versionNum.equals(version.version_num)) {
					nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
					notification = new Notification();
					notification.icon = R.drawable.ic_launcher;
					notification.tickerText = getResources().getString(
							R.string.app_name);
					notification.when = System.currentTimeMillis();
					notification.defaults = Notification.DEFAULT_SOUND;
					
					Intent intent = new Intent(CheckUpdateService.this,
							AutoUpdateService.class);
					Bundle bundle = new Bundle();
					bundle.putSerializable("version", version);
					intent.putExtras(bundle);

					PendingIntent contentIntent = PendingIntent.getService(
							CheckUpdateService.this, 0, intent,
							PendingIntent.FLAG_UPDATE_CURRENT);
					notification.setLatestEventInfo(CheckUpdateService.this,
							getResources().getString(R.string.app_name),
							version.update_hint+" 点击开始更新", contentIntent);

					// 将下载任务添加到任务栏中
					nm.notify(AutoUpdateService.UpdateNotificationID, notification);
				}
			} else {

			}
			// stop self
			CheckUpdateService.this.stopSelf();
		}
	}

	// 取得当前版本号
	public String getVersionNum(Context context) {

		try {
			PackageManager pm = context.getPackageManager();
			PackageInfo info = pm.getPackageInfo(context.getPackageName(), 0);
			versionNum = info.versionName;

			if (versionNum == null || versionNum.length() <= 0) {
				return "";
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return versionNum;

	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}
}
