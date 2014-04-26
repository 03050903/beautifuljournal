package com.maple.beautyjournal;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Toast;

import com.i2mobi.net.HttpClientImplUtil;
import com.i2mobi.net.NetUtil;
import com.maple.beautyjournal.base.BaseActivity;
import com.maple.beautyjournal.utils.ServerDataUtils;
import com.maple.beautyjournal.utils.SettingsUtil;
import com.maple.beautyjournal.widget.ProgressWindow;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


public class SubmitCommentActivity extends BaseActivity implements View.OnClickListener,
        ProgressWindow.OnDismissListener {
    private EditText commentView;
    private Button submitButton;
    private RatingBar ratingBar;
    private Context context;
    private String productId;
    public static final String PRODUCT_ID_EXTRA = "product_id";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_submit_comment);
        ratingBar = (RatingBar) findViewById(R.id.rate);
        commentView = (EditText) findViewById(R.id.comment);
        submitButton = (Button) findViewById(R.id.submit);
        submitButton.setOnClickListener(this);
        context = this;
        productId = getIntent().getStringExtra(PRODUCT_ID_EXTRA);
        if (!SettingsUtil.isLoggedIn(this)) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivityForResult(intent, 0);
        }
    }

    public void onBack(View v) {
        onBackPressed();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.submit:            	
            	String sug = commentView.getEditableText().toString().trim();
            	if(sug == null || sug.length() ==0){
                    Toast.makeText(SubmitCommentActivity.this, R.string.no_comment_msg, Toast.LENGTH_LONG).show();
                    return;
            	}else{
            		new SubmitCommentTask().execute();
            	}
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RESULT_OK) {
            //do nothing, allow user to submit comment
        } else {
            Toast.makeText(context, getString(R.string.pls_log_in_first), Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private String errorMsg;

    @Override
    public void onDismiss() {
        if (TextUtils.isEmpty(errorMsg)) {
            setResult(RESULT_OK);
            finish();
        } else {
            Toast.makeText(context, errorMsg, Toast.LENGTH_SHORT).show();
        }
    }


    private class SubmitCommentTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            dismissProgress();
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showProgress(SubmitCommentActivity.this);
        }

        @Override
        protected Void doInBackground(Void... params) {
            String url = NetUtil.getSubmitCommentUrl(context);
            Log.d("SubmitCommentActivity", "submit comment url: " + url);
            NetUtil util = new HttpClientImplUtil(context, url);
            Map<String, String> commentMap = new HashMap<String, String>();
            commentMap.put("productid", productId);
            commentMap.put("username", SettingsUtil.getUserName(context));
            commentMap.put("content", commentView.getText().toString());
            commentMap.put("star", Float.toString(ratingBar.getRating()));
            util.setMap(commentMap);
            String result = util.doPost();
            Log.d("SubmitCommentActivity", "submit comment result: " + result);
            try {
                JSONObject json = new JSONObject(result);
                if (ServerDataUtils.isTaskSuccess(json)) {
                    errorMsg = null;
                } else {
                    errorMsg = json.getString("info");
                }
            } catch (Exception e) {
                errorMsg = e.getLocalizedMessage();
                e.printStackTrace();
            }
            return null;
        }
    }

}
