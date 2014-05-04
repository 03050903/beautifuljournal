package com.maple.beautyjournal;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.i2mobi.net.HttpClientImplUtil;
import com.i2mobi.net.NetUtil;
import com.i2mobi.net.URLConstant;
import com.maple.beautyjournal.base.BaseActivity;
import com.maple.beautyjournal.entitiy.SearchArticleInfo;
import com.maple.beautyjournal.entitiy.SearchProductInfo;
import com.maple.beautyjournal.utils.ServerDataUtils;
import com.maple.beautyjournal.utils.SettingsUtil;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.sina.weibo.sdk.api.share.Base;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by mosl on 14-4-29.
 */
public class SearchActivity extends BaseActivity {

    private EditText searchEdit;
    private TextView searchArticle,searchGoods,searchCancle_2;
    private ImageView searchCancle_1;
    private ListView searchListView;
    private String Tag_choose;
    private Context context;
    private SearchDataAdapter searchDataAdapter;
    private ListView searchjListView;
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        context=this;
        initCompoment();

    }

    private void initCompoment(){

        searchEdit=(EditText)findViewById(R.id.search_edit);
        searchArticle=(TextView)findViewById(R.id.search_article);
        searchGoods=(TextView)findViewById(R.id.search_goods);
        searchCancle_2=(TextView)findViewById(R.id.search_cancle_2);
        searchCancle_1=(ImageView)findViewById(R.id.search_cancle_1);
        Tag_choose="article";
        searchArticle.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Tag_choose="article";
                searchArticle.setBackgroundResource(R.drawable.left_article_2);
                searchGoods.setBackgroundResource(R.drawable.right_product_2);
                searchDataAdapter.notifyDataSetChanged();
                if(searchArticleInfos.size()==0)
                new GetSearchTittle().execute(searchEdit.getText().toString());
                return false;
            }
        });
        searchGoods.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Tag_choose="product";
                searchGoods.setBackgroundResource(R.drawable.right_product_choose);
                searchArticle.setBackgroundResource(R.drawable.left_article_not);
                searchDataAdapter.notifyDataSetChanged();
                if(searchProductInfos.size()==0)
                new GetSearchTittle().execute(searchEdit.getText().toString());
                return false;
            }
        });
        searchCancle_1.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                searchEdit.setText("");
                return false;
            }
        });
        searchCancle_2.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                searchEdit.setText("");
                return false;
            }
        });
        searchEdit.addTextChangedListener(new SearchTextWatcher());
        searchDataAdapter=new SearchDataAdapter(context);
        searchjListView=(ListView)findViewById(R.id.choose_listview);
        searchjListView.setAdapter(searchDataAdapter);
        searchjListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                switch (scrollState) {
                    // 当不滚动时
                    case AbsListView.OnScrollListener.SCROLL_STATE_IDLE:
                        // 判断滚动到底部
                        if (view.getLastVisiblePosition() == (view.getCount() - 1)) {
                            adapterSize+=10;
                            searchDataAdapter.notifyDataSetChanged();
                        }
                        break;
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

            }
        });
        searchjListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(Tag_choose.equals("article")) {
                    TextView item_id = (TextView) view.findViewById(R.id.item_id);
                    Intent intent = new Intent();
                    intent.putExtra(ArticleDetailActivity.ARTICLE_ID_EXTRA,item_id.getText().toString());
                    intent.setClass(SearchActivity.this, ArticleDetailActivity.class);
                    startActivity(intent);
                }
                else{
                    TextView item_id=(TextView)view.findViewById(R.id.item_id);
                    Intent intent = new Intent();
                    intent.putExtra("product_id",item_id.getText().toString());
                    intent.setClass(SearchActivity.this, ProductDetailActivity.class);
                    startActivity(intent);
                }
            }
        });
        Bundle bundle=getIntent().getBundleExtra("key");
        if(bundle!=null){
            String search=bundle.get("search").toString();
            searchEdit.setText(bundle.get("search").toString());
            new GetSearchTittle().execute(search);
        }


    }

    private class SearchTextWatcher implements TextWatcher{

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {

            searchProductInfos.clear();
            searchArticleInfos.clear();
            searchDataAdapter.notifyDataSetChanged();
            new GetSearchTittle().execute(s.toString());
        }

    }
    private List<SearchArticleInfo> searchArticleInfos=new ArrayList<SearchArticleInfo>();
    private List<SearchProductInfo> searchProductInfos=new ArrayList<SearchProductInfo>();

    private class GetSearchTittle extends AsyncTask<String, Integer, String>{

        @Override
        protected void onPreExecute(){
            showProgress();
            Log.d("XXX","加载的window");
        }
        @Override
        protected String doInBackground(String... params) {
            if(params[0].equals("")||params[0]==null)
                       return "";
            String url = NetUtil.getSearchUrl(context);
            Map<String,String> map=new HashMap<String,String>();
            if(Tag_choose.equals("article")){
                map.put("keyword",params[0]);
                map.put("type","ARTICLE");
                NetUtil util = new HttpClientImplUtil(context,map,url);
                String result = util.doGet();
                try {
                    JSONObject obj = new JSONObject(result);
                    if (ServerDataUtils.isTaskSuccess(obj)) {
                        JSONObject info=obj.getJSONObject("info");
                        JSONArray array=info.getJSONArray("articles");
                        for(int i=0;i<array.length();i++){
                            JSONObject obj_article=array.getJSONObject(i);
                            SearchArticleInfo searchArticleInfo=SearchArticleInfo.fromJson(obj_article);
                            searchArticleInfos.add(searchArticleInfo);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }else{
                map.put("keyword",params[0]);
                map.put("type","PRODUCT");
                NetUtil util = new HttpClientImplUtil(context,map,url);
                String result = util.doGet();
                try {
                    JSONObject obj = new JSONObject(result);
                    if (ServerDataUtils.isTaskSuccess(obj)) {
                        JSONObject info=obj.getJSONObject("info");
                        JSONArray array=info.getJSONArray("products");
                        for(int i=0;i<array.length();i++){
                            JSONObject obj_product=array.getJSONObject(i);
                            SearchProductInfo searchProductInfo=SearchProductInfo.fromJson(obj_product);
                            searchProductInfos.add(searchProductInfo);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return null;
        }
        @Override
        public void onPostExecute(String result){
            Log.d("XXX","加载结束");
            dismissProgress();
            searchDataAdapter.notifyDataSetChanged();
        }
    }

    private int adapterSize=10;
    private class SearchDataAdapter extends BaseAdapter{

        private Context context;
        public SearchDataAdapter(Context context){
            this.context=context;
        }
        @Override
        public int getCount() {
            if(Tag_choose.equals("article")){
             return   adapterSize<searchArticleInfos.size()?adapterSize:searchArticleInfos.size();
            }else
                return adapterSize<searchProductInfos.size()?adapterSize:searchProductInfos.size();
        }

        @Override
        public Object getItem(int position) {
            if(Tag_choose.equals("article")){
                return searchArticleInfos.get(position);
            }else{
                return searchProductInfos.get(position);
            }
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if(Tag_choose.equals("article")){
                convertView = LayoutInflater.from(context).inflate(R.layout.choose_listview_item, null);
                if(searchArticleInfos.size()>0) {
                    TextView tittle = (TextView) convertView.findViewById(R.id.search_tittle);
                    tittle.setText(searchArticleInfos.get(position).item_tittle);
                    TextView item_id=(TextView)convertView.findViewById(R.id.item_id);
                    item_id.setText(searchArticleInfos.get(position).item_id);
                    TextView theme = (TextView) convertView.findViewById(R.id.search_theme);
                    theme.setText(searchArticleInfos.get(position).item_category);
                }
                return convertView;
            }else{
                convertView = LayoutInflater.from(context).inflate(R.layout.search_product_listview_item, null);
                if(searchProductInfos.size()>0) {
                    ImageView productImage=(ImageView)convertView.findViewById(R.id.product_image_search);
                    String imageUrl[]=searchProductInfos.get(position).item_image.split(";");
                   // Log.d("XXX",imageUrl[0]);
                    ImageLoader.getInstance().displayImage(URLConstant.SERVER_ADDRESS +imageUrl[0],productImage);
                    TextView productTittle=(TextView)convertView.findViewById(R.id.tittle_product);
                    ImageView productStar=(ImageView)convertView.findViewById(R.id.starstar);
                    TextView productPrice=(TextView)convertView.findViewById(R.id.product_price);
                    TextView productComments=(TextView)convertView.findViewById(R.id.product_comments);
                    productTittle.setText(searchProductInfos.get(position).item_name);
                    productPrice.setText(searchProductInfos.get(position).item_price);
                    TextView item_id=(TextView)convertView.findViewById(R.id.item_id);
                    item_id.setText(searchProductInfos.get(position).item_id);
                }
                return convertView;
            }
        }
    }

}