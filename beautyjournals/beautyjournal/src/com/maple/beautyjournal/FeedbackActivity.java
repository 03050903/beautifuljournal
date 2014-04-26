package com.maple.beautyjournal;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.i2mobi.net.HttpClientImplUtil;
import com.i2mobi.net.NetUtil;
import com.maple.beautyjournal.base.BaseActivity;
import com.maple.beautyjournal.utils.ServerDataUtils;
import com.maple.beautyjournal.utils.SettingsUtil;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class FeedbackActivity extends BaseActivity {
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);
        mContent = findViewById(R.id.content);
        mEditText = (EditText) this.findViewById(R.id.comment);
    }

    public void onBack(View v) {
        onBackPressed();
    }

    public void onFeedback(View v) {
    	
    	String sug = mEditText.getEditableText().toString().trim();
    	if(sug == null || sug.length() ==0){
            Toast.makeText(FeedbackActivity.this, R.string.no_feedback_msg, Toast.LENGTH_LONG).show();
            return;
    	}else{
    		new FeedbackTask().execute(); 	
    	}
//        Intent intent = new Intent(Intent.ACTION_SEND);
//        intent.setType("message/rfc822");
//        intent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[]{"young@i2mobi.com"});
//        intent.putExtra(android.content.Intent.EXTRA_SUBJECT, getString(R.string.app_name) + "_" + getString(R.string
//
//  .label_feedback));
//        intent.putExtra(android.content.Intent.EXTRA_TEXT, msg.getText());
//        Intent.createChooser(intent, "Choose Email Client");
//        startActivity(intent);
    }

    private String errorMsg;

    private class FeedbackTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            Context context = FeedbackActivity.this;
            NetUtil util = new HttpClientImplUtil(context, NetUtil.FEEDBACK_URL);
            String msg =mEditText.getEditableText().toString().trim();
            Map<String, String> map = new HashMap<String, String>();
            map.put("userid", SettingsUtil.getUserId(context));
            map.put("suggestion", msg);
            util.setMap(map);
            String result = util.doPost();
            Log.d(TAG, "result is " + result);
            try {
                JSONObject object = new JSONObject(result);
                if (ServerDataUtils.isTaskSuccess(object)) {
                    errorMsg = null;
                } else {
                    errorMsg = object.getString("info");
                }
            } catch (Exception e) {
                e.printStackTrace();
                errorMsg = e.getLocalizedMessage();
            }
            return null;
        }

        @Override
        protected void onPreExecute() {
            showProgress();
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            dismissProgress();
            if (!TextUtils.isEmpty(errorMsg)) {
                Toast.makeText(FeedbackActivity.this, errorMsg, Toast.LENGTH_LONG).show();
                errorMsg = null;
            } else {
                Toast.makeText(FeedbackActivity.this, getString(R.string.send_feedback_success), Toast.LENGTH_SHORT)
                        .show();
            }
            super.onPostExecute(aVoid);
            finish();
        }
    }


    private View mContent;
    private EditText mEditText; 

}
