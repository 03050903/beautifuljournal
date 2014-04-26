package com.maple.beautyjournal;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.maple.beautyjournal.base.BaseActivity;
import com.maple.beautyjournal.provider.Beauty;

import java.util.ArrayList;
import java.util.List;

public class PCDListActivity extends BaseActivity {
    int type;
    TextView title;
    ListView list;
    AddressListAdapter adapter;
    private String province = null, city = null, district = null;
    List<String> dataList = new ArrayList<String>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getIntent().getExtras();
        String key = bundle.getString("key");
        if (key.equals("province")) {
            type = 0;
        } else if (key.equals("city")) {
            type = 1;
        } else {
            type = 2;
        }
        if (bundle.keySet().contains("province")) {
            province = bundle.getString("province");
        }
        if (bundle.keySet().contains("city")) {
            city = bundle.getString("city");
        }
        if (isDataInValid()) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    finish();
                }
            }, 1000);
            return;
        }
        setContentView(R.layout.activity_pcd_list);
        ImageView back = (ImageView) findViewById(R.id.btn_back);
        back.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                onBackPressed();
            }

        });
        title = (TextView) findViewById(R.id.title);
        switch (type) {
            case 0:
                title.setText(R.string.province_hint);
                break;
            case 1:
                title.setText(R.string.city_hint);
                break;
            case 2:
                title.setText(R.string.distinct_hint);
                break;
        }
        initList();
    }

    private boolean isDataInValid() {
        if (type == 1 && TextUtils.isEmpty(province)) {
            Toast.makeText(this, getString(R.string.select_province_first), Toast.LENGTH_SHORT).show();
            return true;
        }
        if (type == 2 && (TextUtils.isEmpty(city) || TextUtils.isEmpty(province))) {
            Toast.makeText(this, getString(R.string.select_province_and_city_first), Toast.LENGTH_SHORT).show();
            return true;
        }
        return false;
    }

    private void initList() {
        initQuery();
        list = (ListView) findViewById(R.id.list);
        adapter = new AddressListAdapter();
        list.setAdapter(adapter);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent();
                prepareData(dataList.get(i));
                if (!TextUtils.isEmpty(province)) {
                    intent.putExtra("province", province);
                }
                if (!TextUtils.isEmpty(city)) {
                    intent.putExtra("city", city);
                }
                if (!TextUtils.isEmpty(district)) {
                    intent.putExtra("district", district);
                }
//                if (type == 0) {
//                    intent.putExtra("province", adapter.getItem(i));
//                } else if (type == 1) {
//                    intent.putExtra("city", adapter.getItem(i));
//                } else if (type == 2) {
//                    intent.putExtra("district", adapter.getItem(i));
//                }
                setResult(RESULT_OK, intent);
                finish();
            }
        });
    }

    private void prepareData(String s) {
        if (type == 0) {
            province = s;
            //query distinct city;
            if (queryDistinctCity()) {
                queryDistinctDistrict();
            }
        } else if (type == 1) {
            city = s;
            //query distinct district;
            queryDistinctDistrict();
        } else if (type == 2) {
            district = s;
        }
        return;
    }

    private void queryDistinctDistrict() {
        Cursor cursor = getContentResolver()
                .query(Beauty.Area.CONTENT_URI, new String[]{Beauty.Area.DISTRICT},
                       Beauty.Area.PROVINCE + "=? AND " + Beauty.Area.CITY + "=?", new String[]{province, city}, null);
        if (cursor != null && cursor.getCount() == 1 && cursor.moveToFirst()) {
            district = cursor.getString(0);
        }
    }

    private boolean queryDistinctCity() {
        Cursor cursor = getContentResolver()
                .query(Beauty.Area.DISTINCT_CITY_CONTENT_URI, null, Beauty.Area.PROVINCE + "=?",
                       new String[]{province}, null);
        if (cursor != null && cursor.getCount() == 1 && cursor.moveToFirst()) {
            city = cursor.getString(0);
            return true;
        }
        return false;
    }

    private void initQuery() {
        Cursor cursor = null;
        if (type == 0) {
            cursor = getContentResolver().query(Beauty.Area.DISTINCT_PROVINCE_CONTENT_URI, null, null, null, null);
        } else if (type == 1) {
            cursor = getContentResolver()
                    .query(Beauty.Area.DISTINCT_CITY_CONTENT_URI, null, Beauty.Area.PROVINCE + "=?",
                           new String[]{province}, null);
        } else if (type == 2) {
            cursor = getContentResolver()
                    .query(Beauty.Area.CONTENT_URI, new String[]{Beauty.Area.DISTRICT},
                           Beauty.Area.PROVINCE + "=? AND " + Beauty.Area.CITY + "=?", new String[]{province, city},
                           null);
        }
        if (cursor != null) {
            while (cursor.moveToNext()) {
                dataList.add(cursor.getString(0));
            }
        }
    }


    private class AddressListAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return dataList.size();
        }

        @Override
        public String getItem(int i) {
            return dataList.get(i);
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            View v = view;
            if (v == null) {
                v = getLayoutInflater().inflate(R.layout.address_list_item, viewGroup, false);
            }
            TextView tv = (TextView) v.findViewById(R.id.address);
            tv.setText(getItem(i));
            return v;
        }
    }
}
