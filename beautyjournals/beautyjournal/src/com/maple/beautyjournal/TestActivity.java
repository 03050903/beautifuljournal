package com.maple.beautyjournal;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.actionbarsherlock.app.ActionBar;
import com.i2mobi.net.HttpClientImplUtil;
import com.i2mobi.net.NetUtil;
import com.maple.beautyjournal.base.BaseActivity;
import com.maple.beautyjournal.base.BaseFragment;
import com.maple.beautyjournal.base.BaseFragmentActivity;
import com.maple.beautyjournal.dialog.ArticleCommentDialog;
import com.maple.beautyjournal.entitiy.Recommend;
import com.maple.beautyjournal.fragment.CheckoutFragment;
import com.maple.beautyjournal.fragment.ProductCategoryFragment;
import com.maple.beautyjournal.utils.ServerDataUtils;
import com.maple.beautyjournal.utils.SettingsUtil;

import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Text;

/**
 * Created by mosl on 14-4-10.
 */
public class TestActivity extends BaseFragmentActivity {



        public void onCreate(Bundle savedInstanceState) {

            super.onCreate(savedInstanceState);
            setContentView(R.layout.checkout_gotopro);

            Bundle bundle=getIntent().getExtras();
            String position=(String)bundle.get("key");
            Log.d("XXX","------------key------"+position);
            if(position.equals("CheckoutFragment")){

                FragmentManager fm =getSupportFragmentManager();
                FragmentTransaction ft = fm.beginTransaction();
                String totalPrice=(String)bundle.get("total_price");
                Bundle b=new Bundle();
                b.putString("total_price", totalPrice);
                Fragment checkout = Fragment.instantiate(this, CheckoutFragment.class.getName(), b);
                ft.replace(R.id.chectout_gotopro, checkout);
                ft.addToBackStack(null);
                ft.commit();
                getSupportFragmentManager().executePendingTransactions();
            }

        }


}