package com.maple.beautyjournal;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;
import android.widget.RadioGroup;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.maple.beautyjournal.fragment.ArticleListFragment;

/**
 * Created by mosl on 14-4-11.
 */
public class ArticleDetailTwoActivity extends SherlockFragmentActivity{

    private FragmentManager fragmentManager;
    private RadioGroup radioGroup;

   @Override
   public void onCreate(Bundle savedInstanceState) {
       super.onCreate(savedInstanceState);
       /*
       setContentView(R.layout.activity_arcticle_detail_2);
       fragmentManager=getSupportFragmentManager();
       radioGroup=(RadioGroup)findViewById(R.id.meizhuan_tab);
       radioGroup.addView();
       radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
           @Override
           public void onCheckedChanged(RadioGroup group, int checkedId) {
               FragmentTransaction fragmentTransaction=fragmentManager.beginTransaction();
               Fragment beautyFragment=new BeautyFragment();
               fragmentTransaction.replace(R.id.content,beautyFragment);
               fragmentTransaction.commit();
               Log.d("XXX",Integer.toString(checkedId));
           }
       });
        */
       setContentView(R.layout.activity_main);
       //TabWidget tabWidget =findViewById(R.id.tab) ;
       Bundle bundle =getIntent().getExtras();
       String categry=(String)bundle.get("key");
       if(categry.endsWith("btn_beauty")){
           FragmentManager fm = getSupportFragmentManager();
           FragmentTransaction ft = fm.beginTransaction();
           bundle.putString("key", "beauty");    //传递key=beauty
           Fragment article_list = Fragment.instantiate(ArticleDetailTwoActivity.this, ArticleListFragment.class.getName(), bundle);

           ft.replace(R.id.realtabcontent, article_list);
           ft.addToBackStack(null);
           ft.commit();
           getSupportFragmentManager().executePendingTransactions();
       }else if(categry.endsWith("skin_protect")){
           FragmentManager fm =getSupportFragmentManager();
           FragmentTransaction ft = fm.beginTransaction();


           bundle.putString("key", "skin_protect");
           Fragment article_list = Fragment.instantiate(ArticleDetailTwoActivity.this, ArticleListFragment.class.getName(), bundle);

           ft.replace(R.id.realtabcontent, article_list);
           ft.addToBackStack(null);
           ft.commit();
           getSupportFragmentManager().executePendingTransactions();
       }else if(categry.endsWith("prefume")){
           FragmentManager fm =getSupportFragmentManager();
           FragmentTransaction ft = fm.beginTransaction();

           bundle.putString("key", "perfume");
           Fragment article_list = Fragment.instantiate(ArticleDetailTwoActivity.this, ArticleListFragment.class.getName(), bundle);

           ft.replace(R.id.realtabcontent, article_list);
           ft.addToBackStack(null);
           ft.commit();
           getSupportFragmentManager().executePendingTransactions();
       }else {
           FragmentManager fm =getSupportFragmentManager();
           FragmentTransaction ft = fm.beginTransaction();

           bundle.putString("key", "news");
           Fragment article_list = Fragment.instantiate(ArticleDetailTwoActivity.this, ArticleListFragment.class.getName(), bundle);

           ft.replace(R.id.realtabcontent, article_list);
           ft.addToBackStack(null);
           ft.commit();
           getSupportFragmentManager().executePendingTransactions();
       }
     }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        super.onKeyDown(keyCode, event);
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            startActivity(new Intent().setClass(ArticleDetailTwoActivity.this,MainActivity.class));
            return false;
        }
        return false;
    }

   }