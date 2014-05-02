package com.maple.beautyjournal.dialog;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.actionbarsherlock.app.ActionBar;
import com.maple.beautyjournal.LoginActivity;
import com.maple.beautyjournal.R;
import com.maple.beautyjournal.base.BaseFragment;
import com.maple.beautyjournal.base.BaseFragmentActivity;
import com.maple.beautyjournal.utils.SettingsUtil;

/**
 * Created by mosl on 14-5-2.
 */
public class ArticleCommentAtivityDialog extends BaseFragmentActivity {

    private ImageView commit_comment;
    private Context context;
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article_comment_dia);
        context=this;
        ActionBar bar = getSupportActionBar();
        if (bar != null) {
            bar.hide();
        }

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

                if (SettingsUtil.isLoggedIn(context)) {
                    new ArticleCommentTask().execute();
                } else {
                    Intent intent = new Intent(context, LoginActivity.class);
                    Toast.makeText(context, getString(R.string.pls_log_in_first), Toast.LENGTH_SHORT).show();
                    startActivity(intent);
                }
                return false;
            }
        });
    }

    private class ArticleCommentTask extends AsyncTask<Void,Void,Void> {

        @Override
        protected Void doInBackground(Void... params) {

            return null;
        }
    }
}