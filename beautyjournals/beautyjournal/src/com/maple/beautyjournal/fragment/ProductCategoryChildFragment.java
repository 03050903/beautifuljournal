package com.maple.beautyjournal.fragment;

import android.content.AsyncQueryHandler;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.maple.beautyjournal.R;
import com.maple.beautyjournal.base.BaseFragment;
import com.maple.beautyjournal.entitiy.Function;
import com.maple.beautyjournal.provider.Beauty;

import java.util.ArrayList;
import java.util.List;

public class ProductCategoryChildFragment extends BaseFragment {
    public static final String LIST_BY_CATEGORY = "com.maple.beautyjournal.product.category";

    private ExpandableListView list2;
    private String category;
    private List2Adapter adapter;
    private List<Function> subCategories = new ArrayList<Function>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.activity_product_category_child, container, false);
        ImageView back = (ImageView)v.findViewById(R.id.btn_back);
        back.setOnClickListener(new OnClickListener(){

            @Override
            public void onClick(View arg0) {
                
                ProductCategoryChildFragment.this.getActivity().onBackPressed();
            }
            
        });
        initList2(v);
        initQuery();
        return v;
    }

    private void initQuery() {
        Bundle bundle = getArguments();
        if (bundle.containsKey(LIST_BY_CATEGORY)) {
            category = bundle.getString(LIST_BY_CATEGORY);
        }
        queryHandler = new QueryHandler();
        queryHandler
                .startQuery(TOKEN_QUERY_SUB_CATEGORIES, null, Beauty.Category.CONTENT_URI, SUB_CATEGORY_COLUMNS,
                            Beauty.Category.CATEGORY_ID + "=?", new String[]{category},
                            Beauty.Category.SUB_SUB_CATEGORY_ID + " ASC");
    }

    private void initList2(View parent) {
        list2 = (ExpandableListView) parent.findViewById(R.id.cate_list2);
        list2.setSelector(this.getResources().getDrawable(R.drawable.menu_selected));
        list2.setGroupIndicator(null);
        adapter = new List2Adapter();
        list2.setAdapter(adapter);
        list2.setOnChildClickListener(new OnChildClickListener() {

            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition,
                                        long id) {

                FragmentManager fm = ProductCategoryChildFragment.this.getActivity().getSupportFragmentManager();
                FragmentTransaction ft = fm.beginTransaction();
                // TODO : get category via child
                Fragment product_cate_child = Fragment
                        .instantiate(ProductCategoryChildFragment.this.getActivity(), ProductListFragment.class
                                .getName(), ProductListFragment
                                             .productParameterBundle(adapter.getChild(groupPosition,
                                                                                      childPosition).subId));
                Function.SubFunction sub = adapter.getChild(groupPosition, childPosition);
                Bundle b = new Bundle();
                b.putString(ProductListFragment.LIST_BY_CATEGORY, sub.subId);
                product_cate_child.setArguments(b);
                ft.replace(R.id.realtabcontent, product_cate_child);
                ft.addToBackStack(null);
                ft.commit();
                ProductCategoryChildFragment.this.getActivity().getSupportFragmentManager()
                        .executePendingTransactions();
                return false;
            }

        });
    }

    public class List2Adapter extends BaseExpandableListAdapter {

        public Function.SubFunction getChild(int groupPosition, int childPosition) {
            return getGroup(groupPosition).subs.get(childPosition);
        }

        public long getChildId(int groupPosition, int childPosition) {
            return getChild(groupPosition, childPosition).id;
        }

        public int getChildrenCount(int groupPosition) {
            return getGroup(groupPosition).subs.size();
        }

        public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView,
                                 ViewGroup parent) {
            LinearLayout layout = (LinearLayout) LayoutInflater.from(getActivity())
                    .inflate(R.layout.product_cate_child, null);
            TextView tv = (TextView) layout.findViewById(R.id.ctv);
            tv.setText(getChild(groupPosition, childPosition).name);
            return layout;

        }

        public Function getGroup(int groupPosition) {
            return subCategories.get(groupPosition);
        }

        public int getGroupCount() {
            return subCategories.size();
        }

        public long getGroupId(int groupPosition) {
            return groupPosition;
        }

        public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
            // 实例化布局文件
            RelativeLayout layout = (RelativeLayout) LayoutInflater.from(getActivity())
                    .inflate(R.layout.product_cate_group, null);
            ImageView iv = (ImageView) layout.findViewById(R.id.giv);
            // 判断分组是否展开，分别传入不同的图片资源
            if (isExpanded) {
                layout.setBackgroundColor(Color.argb(0xff, 0xfe, 0x49, 0x80));
                iv.setImageResource(R.drawable.arrow_close);
            } else {
                iv.setImageResource(R.drawable.arrow_open);

            }
            TextView tv = (TextView) layout.findViewById(R.id.gtv);
            tv.setText(getGroup(groupPosition).name);
            return layout;

        }

        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return true;
        }

        public boolean hasStableIds() {
            return true;
        }

    }

    private QueryHandler queryHandler;
    private String[] SUB_CATEGORY_COLUMNS = new String[]{Beauty.Category._ID,   //0
            Beauty.Category.SUB_CATEGORY_ID,                                    //1
            Beauty.Category.SUB_CATEGORY,                                       //2
            Beauty.Category.SUB_SUB_CATEGORY_ID,                                //3
            Beauty.Category.SUB_SUB_CATEGORY                                    //4
    };
    private static final int TOKEN_QUERY_SUB_CATEGORIES = 0;

    private class QueryHandler extends AsyncQueryHandler {

        public QueryHandler() {
            super(getActivity().getContentResolver());
        }

        @Override
        protected void onQueryComplete(int token, Object cookie, Cursor cursor) {
            super.onQueryComplete(token, cookie, cursor);
            switch (token) {
                case TOKEN_QUERY_SUB_CATEGORIES:
                    parseSubCategories(cursor);
                    break;
            }
        }

        private void parseSubCategories(Cursor cursor) {
            if (cursor != null) {
                String lastFunctionId = "";
                Function f = null;
                subCategories.clear();
                while (cursor.moveToNext()) {
                    String functionId = cursor.getString(1);
                    if (functionId != null && !functionId.contentEquals(lastFunctionId)) {
                        if (f != null) {
                            subCategories.add(f);
                        }
                        f = new Function();
                        f.functionId = functionId;
                    }
                    lastFunctionId = functionId;
                    f.name = cursor.getString(2);
                    Function.SubFunction subFunction = new Function.SubFunction();
                    subFunction.id = cursor.getLong(0);
                    subFunction.subId = cursor.getString(3);
                    subFunction.name = cursor.getString(4);
                    f.subs.add(subFunction);
                }
                subCategories.add(f);
                adapter.notifyDataSetChanged();
                cursor.close();
            }
        }
    }

}
