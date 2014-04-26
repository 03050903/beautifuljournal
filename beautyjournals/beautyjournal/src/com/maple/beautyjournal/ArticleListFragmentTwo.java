package com.maple.beautyjournal;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.maple.beautyjournal.base.BaseFragment;
import com.maple.beautyjournal.entitiy.Article;
import com.maple.beautyjournal.utils.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by mosl on 14-4-20.
 */
public class ArticleListFragmentTwo extends BaseFragment {

    private ArrayList<MenuItem> menu = new ArrayList<MenuItem>();
    private int mMenuItemHeight;
    private int page = 1;
    private int category;
    private Map<String, MenuItem> sCategoryMap = new HashMap<String, MenuItem>();
    private List<List<Article>> articles = new ArrayList<List<Article>>();
    private ViewPager viewPager;
    private TextView mArticelListMessageView;
    private RadioGroup menutwo;
    TextView mCateSwitcher;
    String key;
    TextView pageCount;

    @Override
    public void onCreate(Bundle savedInstanceState){

        super.onCreate(savedInstanceState);
        mMenuItemHeight = Utils.dip2px(this.getActivity(), 40);

        sCategoryMap.put("beauty", new MenuItem(getString(R.string.menu_beauty_item1), 101));
        sCategoryMap.put("skin_protect", new MenuItem(getString(R.string.menu_skin_item1), 201));
        sCategoryMap.put("perfume", new MenuItem(getString(R.string.menu_perfume_item1), 301));
        sCategoryMap.put("news", new MenuItem(getString(R.string.menu_brand_item1), 401));

        Bundle bundle = getArguments();   //获得bundle数据，
        key = bundle.getString("key");
        if (key.equals("beauty")) {
            menu.add(new MenuItem(getString(R.string.menu_beauty_item1), 101));
            menu.add(new MenuItem(getString(R.string.menu_beauty_item2), 102));
            menu.add(new MenuItem(getString(R.string.menu_beauty_item3), 103));
            menu.add(new MenuItem(getString(R.string.menu_beauty_item4), 104));
            menu.add(new MenuItem(getString(R.string.menu_beauty_item5), 105));
        } else if (key.equals("skin_protect")) {
            menu.add(new MenuItem(getString(R.string.menu_skin_item1), 201));
            menu.add(new MenuItem(getString(R.string.menu_skin_item2), 202));
            menu.add(new MenuItem(getString(R.string.menu_skin_item3), 203));
            menu.add(new MenuItem(getString(R.string.menu_skin_item4), 204));

        } else if (key.equals("perfume")) {
            menu.add(new MenuItem(getString(R.string.menu_perfume_item1), 301));
            menu.add(new MenuItem(getString(R.string.menu_perfume_item2), 302));

        } else if (key.equals("news")) {
            menu.add(new MenuItem(getString(R.string.menu_brand_item1), 401));
            menu.add(new MenuItem(getString(R.string.menu_brand_item2), 402));
        }
        category = sCategoryMap.get(key).id;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){

        View v = inflater.inflate(R.layout.activity_arcticle_detail_2, container, false);
        TextView tittle=(TextView)inflater.inflate(R.id.article_list_title,container,false);
        menutwo=(RadioGroup)inflater.inflate(R.id.meizhuan_tab,container,false);
        if(key.endsWith("beauty")){
            tittle.setText("美妆");
            RadioButton radioButton=new RadioButton(this.getActivity());
            //radioButton.
        }else if(key.equals("skin_protect")){
            tittle.setText("护肤");
        }else if(key.equals("perfume")){
            tittle.setText("香水");
        }else{
            tittle.setText("资讯");
        }


        return v;
    }
    public class MenuItem {
        public String title;
        public int id;

        public MenuItem(String title, int id) {
            this.title = title;
            this.id = id;
        }
    }
}
