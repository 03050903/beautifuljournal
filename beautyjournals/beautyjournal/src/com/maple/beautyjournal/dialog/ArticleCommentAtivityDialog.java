package com.maple.beautyjournal.dialog;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.actionbarsherlock.app.ActionBar;
import com.i2mobi.net.HttpClientImplUtil;
import com.i2mobi.net.NetUtil;
import com.maple.beautyjournal.LoginActivity;
import com.maple.beautyjournal.R;
import com.maple.beautyjournal.base.BaseFragment;
import com.maple.beautyjournal.base.BaseFragmentActivity;
import com.maple.beautyjournal.entitiy.ArticleComment;
import com.maple.beautyjournal.utils.ServerDataUtils;
import com.maple.beautyjournal.utils.SettingsUtil;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by mosl on 14-5-2.
 */
public class ArticleCommentAtivityDialog extends BaseFragmentActivity {

    private ImageView commit_comment;
    private Context context;
    private String articleId;
    private String content;
    private String star;
    private EditText comment_content;
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_article_comment_dia);
        context=this;
        ActionBar bar = getSupportActionBar();
        if (bar != null) {
            bar.hide();
        }
        articleId=getIntent().getStringExtra("articleId");
        initImageView();
    }

    public void onBack(View v){
       finish();
    }
    private void initImageView() {
        commit_comment=(ImageView)findViewById(R.id.commit_comment);
        commit_comment.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                content=comment_content.getText().toString();
                star="5";
                if (SettingsUtil.isLoggedIn(context)) {
                    new GetArticleComment().execute(articleId,SettingsUtil.getUserName(ArticleCommentAtivityDialog.this)
                    ,content,star);
                } else {
                    new GetArticleComment().execute(articleId, "游客"
                            , content, star);
                }
                finish();
                return false;
            }
        });

        comment_content=(EditText)findViewById(R.id.comment_content);
    }



    private class GetArticleComment extends AsyncTask<String,Void,String> {

        @Override
        protected String doInBackground(String... params) {
            String url = NetUtil. getArticleToCommentUrl();
            Map<String,String> comment=new HashMap<String,String>();
            comment.put("pid",params[0]);
            comment.put("username",params[1]);
            comment.put("content",params[2]);
            comment.put("star",params[3]);
            Log.d("XXX",params[0]+params[1]+params[2]+params[3]);
            NetUtil util = new HttpClientImplUtil(context,comment,url);
            String result=util.doPost();
            Log.d("XXX", result);
            return "";
        }

        @Override
        protected void onPostExecute(String result) {

        }

    }
}