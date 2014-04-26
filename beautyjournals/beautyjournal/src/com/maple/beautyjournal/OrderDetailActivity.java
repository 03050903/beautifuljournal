package com.maple.beautyjournal;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.i2mobi.net.HttpClientImplUtil;
import com.i2mobi.net.NetUtil;
import com.i2mobi.net.URLConstant;
import com.maple.beautyjournal.base.BaseActivity;
import com.maple.beautyjournal.utils.ServerDataUtils;
import com.maple.beautyjournal.widget.ProgressAnimateView;
import com.nostra13.universalimageloader.core.ImageLoader;

public class OrderDetailActivity extends BaseActivity {

    private ListView list;
    private OrderAdapter adapter;
    private List<DetailOrder> orders = new ArrayList<DetailOrder>();
    private String orderId;
    private TextView totalPriceView, orderStatusView, orderIdView, orderProdCountView;
    private String totalPrice, orderStatus;

    private float mTotalPrice;
    private View mScrollView;
    private ProgressAnimateView progressView = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.setContentView(R.layout.fragment_order_detail);
        progressView = (ProgressAnimateView) this.findViewById(R.id.progressAnimateView);

        ImageView back = (ImageView) findViewById(R.id.btn_back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            	OrderDetailActivity.this.onBackPressed();
            }
        });
        mScrollView = this.findViewById(R.id.scrollView1);
        mScrollView.setVerticalScrollBarEnabled(false);
        list = (ListView) findViewById(R.id.list);
        adapter = new OrderAdapter();
        list.setAdapter(adapter);
        totalPriceView = (TextView) findViewById(R.id.total_price);
        orderStatusView = (TextView) findViewById(R.id.order_status);
        orderIdView = (TextView) findViewById(R.id.order_id);
        orderProdCountView = (TextView) findViewById(R.id.order_count);
        Bundle bundle = getIntent().getExtras();
        orderId = bundle.getString("id");
        orderIdView.setText(orderId);
        new GetOrderTask().execute();
    }

    private class OrderAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return orders.size();
        }

        @Override
        public DetailOrder getItem(int position) {
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
                v = LayoutInflater.from(OrderDetailActivity.this).inflate(R.layout.detail_order_list_item, parent, false);
            }
            final DetailOrder order = getItem(position);
            ImageView iv = (ImageView) v.findViewById(R.id.product_image);
            if (!TextUtils.isEmpty(order.image)) {
                if (order.image.startsWith("http")) {
                    ImageLoader.getInstance().displayImage(order.image, iv);
                } else {
                    ImageLoader.getInstance().displayImage(URLConstant.SERVER_ADDRESS + order.image, iv);
                }
            }else{
                iv.setImageResource(R.drawable.default_product);
            }

            View.OnClickListener toProdListener= new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent;
                    intent = new Intent(OrderDetailActivity.this, ProductDetailActivity.class);
                    intent.putExtra(ProductDetailActivity.PRODUCT_ID, order.id);
                    startActivity(intent);
                }
            };
            //iv.setOnClickListener(toProdListener);
            v.setOnClickListener(toProdListener);
            TextView name = (TextView) v.findViewById(R.id.product_name);

            name.setText(order.name);
            TextView count = (TextView) v.findViewById(R.id.count);
            count.setText(TextUtils.isEmpty(order.count) ? "x 1" : "x "+order.count);
            TextView price = (TextView) v.findViewById(R.id.price);
            price.setText(order.price);
            return v;
        }
    }

    private static final String TAG = "OrderDetailActivity";

    private class GetOrderTask extends AsyncTask<Void, Void, Void> {
        private String errorMsg = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressView.start();
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                NetUtil util = new HttpClientImplUtil(OrderDetailActivity.this, NetUtil.getOrderDetailUrl(orderId));
                String result = util.doGet();
                Log.d(TAG, "get order result: " + result);
                JSONObject object = new JSONObject(result);
                if (ServerDataUtils.isTaskSuccess(object)) {
                    JSONObject info = object.getJSONObject("info");
                    totalPrice = info.optString("total_price");
                    orderStatus = info.getString("order_status");
                    orderId =info.optString("order_id");
                    mTotalPrice = 0;
                    JSONArray array = info.getJSONArray("items");
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject orderObject = array.getJSONObject(i);
                        DetailOrder order = new DetailOrder();
                        order.count = orderObject.optString("count");
                        order.image = orderObject.optString("img");
                        order.id = orderObject.getString("id");
                        order.seller = orderObject.optString("seller");
                        order.name = orderObject.getString("name");
                        order.price = orderObject.getString("price");
                        mTotalPrice += Float.parseFloat(order.price);
                        orders.add(order);
                    }
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
        protected void onPostExecute(Void aVoid) {
        	progressView.end();
            if (!TextUtils.isEmpty(errorMsg)) {
                Toast.makeText(OrderDetailActivity.this, errorMsg, Toast.LENGTH_SHORT).show();
            } else {
            	if(TextUtils.isEmpty(totalPrice)){
            		totalPriceView.setText(""+mTotalPrice);
            	}else{
            		totalPriceView.setText(totalPrice);
            	}
                orderStatusView.setText(orderStatus);
                orderIdView.setText(orderId);
                orderProdCountView.setText("x "+adapter.getCount());
                adapter.notifyDataSetChanged();
            }
            mScrollView.postInvalidate();
            super.onPostExecute(aVoid);
        }
    }

    private class DetailOrder {
        String id;
        String name;
        String price;
        String seller;
        String count;
        String image;
    }
 
}
