package com.maple.beautyjournal;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.i2mobi.net.HttpClientImplUtil;
import com.i2mobi.net.NetUtil;
import com.i2mobi.net.URLConstant;
import com.maple.beautyjournal.base.BaseActivity;
import com.maple.beautyjournal.dialog.ArticleCommentAtivityDialog;
import com.maple.beautyjournal.entitiy.ArticleComment;
import com.maple.beautyjournal.utils.ServerDataUtils;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mosl on 14-5-2.
 */
public class ArticleCommentActivity extends BaseActivity {
    private Context context;
    private String articleId;
    private int commentCount=0;
    private ListView commentListView;
    private LinearLayout nocomment;
    private LinearLayout layout_comment;
    private ArticleCommentAdapter articleCommentAdapter;
    private ImageView article_reflesh;
    private Button article_comment;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context=this;
        Intent intent=getIntent();
        Bundle bundle=intent.getExtras();
        articleId=bundle.getString("articleId");
        setContentView(R.layout.activity_article_comment);
        initCompoent();
        new GetArticleComment().execute();
    }

    public void initCompoent(){
        commentListView=(ListView)findViewById(R.id.list_article_comment);
        nocomment=(LinearLayout)findViewById(R.id.nocomment);
        layout_comment=(LinearLayout)findViewById(R.id.layout_comment);
        article_reflesh=(ImageView)findViewById(R.id.article_reflesh);
        article_reflesh.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                articleCommentList.clear();
                if(articleCommentAdapter!=null)
                articleCommentAdapter.notifyDataSetChanged();
                new GetArticleComment().execute();
                return false;
            }
        });
        article_comment=(Button)findViewById(R.id.commit_article_comment);
        article_comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(ArticleCommentActivity.this,ArticleCommentAtivityDialog.class);
                intent.putExtra("articleId",articleId);
                startActivity(intent);
            }
        });
    }
    public void onBack(View v) {

        onBackPressed();
    }

    public void editComment(View v){
        Intent intent=new Intent(ArticleCommentActivity.this,ArticleCommentAtivityDialog.class);
        intent.putExtra("articleId",articleId);
        startActivity(intent);
    }

    private class ArticleCommentAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            return articleCommentList.size();
        }

        @Override
        public Object getItem(int position) {
            return articleCommentList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if(convertView==null){
                convertView= LayoutInflater.from(ArticleCommentActivity.this).inflate(R.layout.article_comment_item, parent, false);
            }
            ArticleComment articleComment=articleCommentList.get(position);
            ImageView userImage=(ImageView)convertView.findViewById(R.id.userImage_comment);
            if(!articleComment.userimage.equals(""))
            ImageLoader.getInstance().displayImage(URLConstant.SERVER_ADDRESS + articleComment.userimage, userImage);
            TextView username=(TextView)convertView.findViewById(R.id.username_comment);
            TextView time=(TextView)convertView.findViewById(R.id.time_comment);
            TextView content=(TextView)convertView.findViewById(R.id.content_comment);
            username.setText(articleComment.username);
            Long timestamp = Long.parseLong(articleComment.time)*1000;
            String formats="yyyy-MM-dd";
            String date = new java.text.SimpleDateFormat(formats).format(new java.util.Date(timestamp));
            time.setText(date);
            content.setText(articleComment.content);
            return convertView;
        }
    }
    List<ArticleComment> articleCommentList=new ArrayList<ArticleComment>();

    private class GetArticleComment extends AsyncTask<String,Void,String> {

        @Override
        protected void onPreExecute(){
            showProgress();
            Log.d("XXX","加载的window");
        }
        @Override
        protected String doInBackground(String... params) {
            String url = NetUtil.getArticleCommentUrl(context,articleId,10);
            NetUtil util = new HttpClientImplUtil(context,url);
            String result = util.doGet();
            try {
                JSONObject obj = new JSONObject(result);
                if (ServerDataUtils.isTaskSuccess(obj)) {
                    JSONObject comment=obj.getJSONObject("info");
                    String count=comment.optString("count");
                    commentCount=Integer.parseInt(count);
                     if( commentCount!=0){
                        JSONArray array=comment.getJSONArray("items");
                        for(int i=0;i<array.length();i++){
                            JSONObject commentObj=array.getJSONObject(i);
                            ArticleComment articleComment=ArticleComment.fromJson(commentObj);
                            articleCommentList.add(articleComment);

                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return "";
        }

        @Override
        protected void onPostExecute(String result) {
            if(commentCount!=0){
                layout_comment.setVisibility(View.VISIBLE);
                nocomment.setVisibility(View.GONE);
                articleCommentAdapter=new ArticleCommentAdapter();
                commentListView.setAdapter(articleCommentAdapter);

            }
            dismissProgress();
        }

    }
}