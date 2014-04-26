package com.maple.beautyjournal;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.i2mobi.net.HttpClientImplUtil;
import com.i2mobi.net.NetUtil;
import com.maple.beautyjournal.adapter.CommentsAdapter;
import com.maple.beautyjournal.base.BaseActivity;
import com.maple.beautyjournal.entitiy.Comment;
import com.maple.beautyjournal.utils.ServerDataUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MoreCommentActivity extends BaseActivity {
    private PullToRefreshListView list;
    private Context context;
    private List<Comment> comments = new ArrayList<Comment>();
    private String errorMsg;
    private int offset;
    private CommentsAdapter commentsAdapter;
    private static final int DEFAULT_PAGE_SIZE = 20;
    private String id;
    public static final String PRODUCT_ID = "product_id";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_more_comment);
        
        context = this;
        list = (PullToRefreshListView) findViewById(R.id.list);
        commentsAdapter = new CommentsAdapter(context, comments);
        list.setAdapter(commentsAdapter);
        id = getIntent().getStringExtra(PRODUCT_ID);
        new GetCommentsTask().execute();
    }
    public void onBack(View v){
        onBackPressed();
    }


    private class GetCommentsTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            String url = NetUtil.getProductCommentsUrl(context, id, DEFAULT_PAGE_SIZE, offset);
            Log.d(TAG, "get comments: " + url);
            NetUtil util = new HttpClientImplUtil(context, url);
            String result = util.doGet();
            Log.d(TAG, "result is " + result);
            try {
                JSONObject jsonObject = new JSONObject(result);
                if(ServerDataUtils.isTaskSuccess(jsonObject)) {
                    JSONObject info = jsonObject.getJSONObject("info");
                    JSONArray array = info.optJSONArray("items");
                    if(array == null || array.length() == 0) {
                        return null;
                    }
                    for(int i = 0; i < array.length(); i++) {
                        JSONObject obj = array.getJSONObject(i);
                        Comment comment = ServerDataUtils.getCommentFromJSONObject(obj);
                        Log.d(TAG, "add comment: " + comment.productName + ", " + comment.username);
                        comments.add(comment);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                errorMsg = e.getLocalizedMessage();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            commentsAdapter.notifyDataSetChanged();
        }
    }


}
