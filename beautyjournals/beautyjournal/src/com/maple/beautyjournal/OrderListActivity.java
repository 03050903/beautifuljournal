package com.maple.beautyjournal;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.i2mobi.net.HttpClientImplUtil;
import com.i2mobi.net.NetUtil;
import com.maple.beautyjournal.base.BaseActivity;
import com.maple.beautyjournal.entitiy.Order;
import com.maple.beautyjournal.utils.ServerDataUtils;
import com.maple.beautyjournal.widget.ProgressAnimateView;

public class OrderListActivity extends BaseActivity {

    private PullToRefreshListView listView;
    private OrdersListAdapter adapter;
    private int offset = 0, page = 0;
    private static final int SIZE = 25;
    private String errorMsg = null;
    private List<Order> orders = new ArrayList<Order>();
    private TextView mListFooterView = null;
    private ProgressAnimateView progressView = null;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.fragment_order_list);
        progressView = (ProgressAnimateView) this.findViewById(R.id.progressAnimateView);

        ImageView back = (ImageView) findViewById(R.id.btn_back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OrderListActivity.this.onBackPressed();
            }

        });
        
        mListFooterView = new TextView(this);
        mListFooterView.setGravity(Gravity.CENTER);
        mListFooterView.setHeight(100);

        listView = (PullToRefreshListView) findViewById(R.id.list);
        adapter = new OrdersListAdapter();
        listView.setAdapter(adapter);
        listView.getRefreshableView().setDividerHeight(14);
        listView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {

            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                new GetOrderTask().execute();
            }
        });
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            	if(position ==0 || adapter.getCount()==0){
            		return;
            	}
                Bundle args = new Bundle();
                Order order = adapter.getItem(position-1);
                args.putString("id", order.order_id);
                Intent intent = new Intent(OrderListActivity.this, OrderDetailActivity.class);
                intent.putExtras(args);
                OrderListActivity.this.startActivity(intent);
            }
        });
        page = 0;
        new GetOrderTask().execute();
    }
    private static final String TAG = "OrderListActivity";
    private class GetOrderTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            try {
                Context context = OrderListActivity.this;
                NetUtil util = new HttpClientImplUtil(context, NetUtil
                        .getOrderListUrl(context, SIZE, offset + page * SIZE));
                String result = util.doGet();
                Log.d(TAG, result);
                JSONObject object = new JSONObject(result);
                if (ServerDataUtils.isTaskSuccess(object)) {
                    errorMsg = null;
                    JSONArray array = object.getJSONArray("info");
                    if (page==0) orders.clear();
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject orderObject = array.getJSONObject(i);
                        Order order = new Order();
                        order.username = orderObject.getString("username");
                        order.order_id = orderObject.getString("order_id");
                        order.order_status = orderObject.getString("order_status");
                        order.order_sum_price = orderObject.getString("order_sum_price");
                        order.order_timestamp = orderObject.getString("order_timestamp");
                        order.order_prods_count = orderObject.optString("item_count");
                        order.image = orderObject.optString("image");
                        orders.add(order);
                    }
                    page++;
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
            super.onPreExecute();
            listView.getRefreshableView().removeFooterView(mListFooterView);
            progressView.start();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
        	progressView.end();
        	if (!TextUtils.isEmpty(errorMsg)) {
                mListFooterView.setText(errorMsg);
                listView.getRefreshableView().addFooterView(mListFooterView);
            }
            adapter.notifyDataSetChanged();
            super.onPostExecute(aVoid);
        }
    }

    private class OrdersListAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return orders.size();
        }

        @Override
        public Order getItem(int position) {
            return orders.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View v = convertView;
            if (v == null) {
                v = LayoutInflater.from(OrderListActivity.this).inflate(R.layout.order_list_item, parent, false);
            }
            TextView tvOrderId = (TextView) v.findViewById(R.id.order_id);
            TextView tvStatus = (TextView) v.findViewById(R.id.order_status);
            TextView tvPrice = (TextView) v.findViewById(R.id.total_price);
            TextView tvCount = (TextView) v.findViewById(R.id.order_count);
            
            Order order = getItem(position);
            tvOrderId.setText(order.order_id);
            tvStatus.setText(order.order_status);
            tvPrice.setText(order.order_sum_price);
            String count = TextUtils.isEmpty(order.order_prods_count)?"x 1":"x "+order.order_prods_count;
            tvCount.setText(count);
            return v;
        }
    }
}
