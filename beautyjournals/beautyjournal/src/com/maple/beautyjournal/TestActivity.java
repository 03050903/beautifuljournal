package com.maple.beautyjournal;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.i2mobi.net.HttpClientImplUtil;
import com.i2mobi.net.NetUtil;
import com.maple.beautyjournal.dialog.ArticleCommentDialog;
import com.maple.beautyjournal.entitiy.Recommend;
import com.maple.beautyjournal.utils.ServerDataUtils;
import com.maple.beautyjournal.utils.SettingsUtil;

import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Text;

/**
 * Created by mosl on 14-4-10.
 */
public class TestActivity extends Activity {



        public void onCreate(Bundle savedInstanceState) {

            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_search);
        }

//
//
//    private ArticleCommentDialog articleCommentDialog;
//    private Button openDialog;
//    private ListView commentList;
//    public void onCreate(Bundle savedInstanceState) {
//
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_article_comment);
//        commentList=(ListView)findViewById(R.id.list_article_comment);
//        initListViewData();
//        openDialog=(Button)findViewById(R.id.commit_article_comment);
//        openDialog.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                articleCommentDialog=new ArticleCommentDialog(TestActivity.this);
//                articleCommentDialog.show();
//            }
//        });
//        new GetDataTask().execute();
//
//    }
//    public void initListViewData(){
//        int[] userImage={R.drawable.left_arrow_2};
//        String[] userName={"moshenglei"};
//        String[] commentDate={"2014-4-21"};
//        String[] commentContent={"大家好才是真的好！"};
//        commentList.setAdapter(new CommentAdapter(userImage,userName,commentDate,commentContent));
//        Log.d("XXX","adapter构造");
//    }
//    public class CommentAdapter extends BaseAdapter{
//
//        View[] itemViews;
//
//        public CommentAdapter(int[] userImage,String[] username,String[] commentdate,String[] commentContent){
//
//            itemViews=new View[userImage.length];
//            for(int i=0;i<userImage.length;i++){
//                Log.d("XXX","xxxxxxxxxx");
//                itemViews[i]=makeItemView(userImage[i],username[i],commentdate[i],commentContent[i]);
//            }
//        }
//        @Override
//        public int getCount() {
//
//            return itemViews.length;
//        }
//
//        @Override
//        public Object getItem(int position) {
//            return itemViews[position];
//        }
//
//        @Override
//        public long getItemId(int position) {
//            return position;
//        }
//
//        @Override
//        public View getView(int position, View convertView, ViewGroup parent) {
//            if (convertView == null)
//                return itemViews[position];
//            return convertView;
//        }
//
//        private View makeItemView(int image,String name,String date,String content){
//            LayoutInflater inflater = (LayoutInflater) TestActivity.this
//                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//
//            View itemView=(View)inflater.inflate(R.layout.article_comment_item,null);
//
//            ImageView imageView=(ImageView)itemView.findViewById(R.id.article_comment_item);
//            imageView.setImageResource(image);
//
//            TextView nameText=(TextView)itemView.findViewById(R.id.user_name_text);
//            nameText.setText(name);
//
//            TextView dateText=(TextView)itemView.findViewById(R.id.comment_date);
//            dateText.setText(date);
//
//            TextView contentText=(TextView)itemView.findViewById(R.id.comment_content);
//            contentText.setText(content);
//
//            return itemView;
//        }
//    }
//
//    private class GetDataTask extends AsyncTask<Void, Void, Void> {
//
//        @Override
//        protected Void doInBackground(Void... params) {
//            NetUtil util = new HttpClientImplUtil(TestActivity.this,"http://42.96.185.57:8008/index.php?r=app/comment/getlistbypro/proid/10000060159518/size/1");
//            String result = util.doGet();
//            Log.d("XXX" ,result);
//            try {
//                JSONObject obj = new JSONObject(result);
//                if (ServerDataUtils.isTaskSuccess(obj)) {
//                    Log.d("XXX" ,obj.toString());
//                }
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//            return null;
//        }
//    }

}