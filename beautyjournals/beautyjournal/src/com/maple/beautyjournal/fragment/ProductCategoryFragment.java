package com.maple.beautyjournal.fragment;

import android.content.AsyncQueryHandler;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.i2mobi.utils.StringMatcher;
import com.i2mobi.widget.IndexableListView;
import com.maple.beautyjournal.R;
import com.maple.beautyjournal.base.BaseFragment;
import com.maple.beautyjournal.entitiy.Brand;
import com.maple.beautyjournal.entitiy.Function;
import com.maple.beautyjournal.provider.Beauty;
import com.maple.beautyjournal.utils.Utils;

import java.util.ArrayList;
import java.util.List;

public class ProductCategoryFragment extends BaseFragment implements OnClickListener {

    private final int TAB_CATE = 0;
    private final int TAB_FUN = 1;
    private final int TAB_BRAND = 2;
    private int currentTab;

    private FrameLayout tab_cate;
    private FrameLayout tab_fun;
    private FrameLayout tab_brand;
    private ListView list1;
    private ExpandableListView list2;
    private IndexableListView list3;
    FragmentActivity context;
    private QueryHandler queryHandler;
    private List1Adapter adapter1;
    private List2Adapter adapter2;
    private List3Adapter adapter3;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        context = getActivity();
        View v = inflater.inflate(R.layout.activity_product_category, container, false);
        ImageView back = (ImageView) v.findViewById(R.id.btn_back);
        back.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {

                context.onBackPressed();
            }

        });
        initTab(v);
        initList1(v);
        initList2(v);
        initList3(v);
        setTab(TAB_CATE);
        return v;
    }

    private void initTab(View parent) {
        tab_cate = (FrameLayout) parent.findViewById(R.id.tab_cate);
        tab_cate.setOnClickListener(this);
        tab_fun = (FrameLayout) parent.findViewById(R.id.tab_fun);
        tab_fun.setOnClickListener(this);
        tab_brand = (FrameLayout) parent.findViewById(R.id.tab_brand);
        tab_brand.setOnClickListener(this);
        queryHandler = new QueryHandler();
    }

    private void setTab(int tab) {
        currentTab = tab;
        tab_cate.getChildAt(1).setVisibility(View.GONE);
        tab_fun.getChildAt(1).setVisibility(View.GONE);
        tab_brand.getChildAt(1).setVisibility(View.GONE);
        list1.setVisibility(View.GONE);
        list2.setVisibility(View.GONE);
        list3.setVisibility(View.GONE);
        switch (currentTab) {
            case TAB_CATE:
                tab_cate.getChildAt(1).setVisibility(View.VISIBLE);
                list1.setVisibility(View.VISIBLE);
                break;
            case TAB_FUN:
                tab_fun.getChildAt(1).setVisibility(View.VISIBLE);
                list2.setVisibility(View.VISIBLE);
                break;
            case TAB_BRAND:
                tab_brand.getChildAt(1).setVisibility(View.VISIBLE);
                list3.setVisibility(View.VISIBLE);
                break;
        }
    }

    private void initList1(View parent) {
        list1 = (ListView) parent.findViewById(R.id.cate_list1);
        adapter1 = new List1Adapter();
        list1.setAdapter(adapter1);
        list1.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                FragmentManager fm = ProductCategoryFragment.this.getActivity().getSupportFragmentManager();
                FragmentTransaction ft = fm.beginTransaction();
                Fragment product_cate_child = Fragment
                        .instantiate(ProductCategoryFragment.this.getActivity(), ProductCategoryChildFragment.class
                                .getName(), null);
                Category category = categories.get(arg2);
                Bundle bundle = new Bundle();
                bundle.putString(ProductCategoryChildFragment.LIST_BY_CATEGORY, category.id);
                product_cate_child.setArguments(bundle);
                ft.replace(R.id.realtabcontent, product_cate_child);
                ft.addToBackStack(null);
                ft.commit();
                context.getSupportFragmentManager().executePendingTransactions();

            }

        });
        queryHandler
                .startQuery(TOKEN_QUERY_CATEGORIES, null, Beauty.Category.CATEGORY_BASE_CONTENT_URI,
                            CATEGORY_COLUMNS, null, null, Beauty.Category.CATEGORY_ID + " ASC ");
    }

    private void initList2(View parent) {
        list2 = (ExpandableListView) parent.findViewById(R.id.cate_list2);
        list2.setSelector(this.getResources().getDrawable(R.drawable.menu_selected));
        list2.setGroupIndicator(null);
        adapter2 = new List2Adapter();
        list2.setAdapter(adapter2);
        list2.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView expandableListView, View view, int i, int i2, long l) {
                Function.SubFunction sub = adapter2.getChild(i, i2);
                if (sub != null) {
                    FragmentManager fm = ProductCategoryFragment.this.getActivity().getSupportFragmentManager();
                    FragmentTransaction ft = fm.beginTransaction();
                    Fragment product_cate_child = Fragment
                            .instantiate(ProductCategoryFragment.this.getActivity(), ProductListFragment.class
                                    .getName(), null);
                    Bundle bundle = new Bundle();
                    bundle.putString(ProductListFragment.LIST_BY_FUNCTION, sub.subId);
                    product_cate_child.setArguments(bundle);
                    ft.replace(R.id.realtabcontent, product_cate_child);
                    ft.addToBackStack(null);
                    ft.commit();
                    context.getSupportFragmentManager().executePendingTransactions();
                    list2.collapseGroup(i);
                }
                return true;
            }
        });
        queryHandler
                .startQuery(TOKEN_QUERY_FUNCTIONS, null, Beauty.Function.CONTENT_URI, FUNCTION_COLUMNS, null, null,
                            Beauty.Function.FUNCTION_ID + " ASC");
    }

    private void initList3(View parent) {
        list3 = (IndexableListView) parent.findViewById(R.id.cate_list3);
        adapter3 = new List3Adapter();
        list3.setFastScrollEnabled(true);
        list3.setAdapter(adapter3);
        list3.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Brand b = brands.get(i);
                if (b != null) {
                    FragmentManager fm = ProductCategoryFragment.this.getActivity().getSupportFragmentManager();
                    FragmentTransaction ft = fm.beginTransaction();
                    Fragment product_cate_child = Fragment
                            .instantiate(ProductCategoryFragment.this.getActivity(), ProductListFragment.class
                                    .getName(), null);
                    Bundle bundle = new Bundle();
                    bundle.putString(ProductListFragment.LIST_BY_BRAND, b.brandId);
                    product_cate_child.setArguments(bundle);
                    ft.replace(R.id.realtabcontent, product_cate_child);
                    ft.addToBackStack(null);
                    ft.commit();
                    context.getSupportFragmentManager().executePendingTransactions();
                }
            }
        });
        queryHandler
                .startQuery(TOKEN_QUERY_BRANDS, null, Beauty.Brand.CONTENT_URI, BRANDS_COLUMNS, null, null,
                            Beauty.Brand.PINYIN + " ASC");
    }

    public class List1Adapter extends BaseAdapter {

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return categories.size();
        }

        @Override
        public Object getItem(int arg0) {
            // TODO Auto-generated method stub
            return categories.get(arg0);
        }

        @Override
        public long getItemId(int arg0) {
            // TODO Auto-generated method stub
            return arg0;
        }

        @Override
        public View getView(int arg0, View arg1, ViewGroup arg2) {

            Resources res = context.getResources();
            TextView v;
            if (arg1 == null) {

                v = new TextView(context);
                v.setLayoutParams(new AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                                                               ViewGroup.LayoutParams.WRAP_CONTENT));
                v.setGravity(Gravity.CENTER_VERTICAL);
                v.setPadding(Utils.dip2px(context, 10), Utils.dip2px(context, 15), Utils.dip2px(context, 10), Utils
                        .dip2px(context, 15));
                v.setCompoundDrawablePadding(Utils.dip2px(context, 10));
                v.setTextSize(18);

                v.setTextColor(Color.BLACK);
            } else {
                v = (TextView) arg1;
            }
            Category category = categories.get(arg0);
            v.setText(category.name);
            v.setCompoundDrawablesWithIntrinsicBounds(category.drawable == -1 ? null : res
                    .getDrawable(category.drawable), null, res.getDrawable(R.drawable.arrow), null);
            return v;
        }

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
            LinearLayout layout = (LinearLayout) LayoutInflater.from(ProductCategoryFragment.this.getActivity())
                    .inflate(R.layout.product_cate_child, null);
            TextView tv = (TextView) layout.findViewById(R.id.ctv);
            tv.setText(getChild(groupPosition, childPosition).name);
            return layout;

        }

        public Function getGroup(int groupPosition) {
            return functions.get(groupPosition);
        }

        public int getGroupCount() {
            return functions.size();
        }

        public long getGroupId(int groupPosition) {
            return groupPosition;
        }

        public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
            // 实例化布局文件
            RelativeLayout layout = (RelativeLayout) LayoutInflater.from(ProductCategoryFragment.this.getActivity())
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

    private List<Brand> brands = new ArrayList<Brand>();
    private List<Function> functions = new ArrayList<Function>();
    private List<Category> categories = new ArrayList<Category>();

    private class List3Adapter extends BaseAdapter implements SectionIndexer {

        @Override
        public int getCount() {
            return brands.size();
        }

        @Override
        public Brand getItem(int i) {
            return brands.get(i);
        }

        @Override
        public long getItemId(int i) {
            return brands.get(i).id;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            View v = view;
            if (v == null) {
                v = LayoutInflater.from(context).inflate(R.layout.brand_list_item, viewGroup, false);
            }
            TextView tv = (TextView) v.findViewById(R.id.brand);
            tv.setText(brands.get(i).name);
            return v;
        }

        //code for sections
        private String mSections = "#ABCDEFGHIJKLMNOPQRSTUVWXYZ";

        @Override
        public Object[] getSections() {
            String[] sections = new String[mSections.length()];
            for (int i = 0; i < mSections.length(); i++) { sections[i] = String.valueOf(mSections.charAt(i)); }
            return sections;
        }

        @Override
        public int getPositionForSection(int section) {
            // If there is no item for current section, previous section will be selected
            for (int i = section; i >= 0; i--) {
                for (int j = 0; j < getCount(); j++) {
                    if (i == 0) {
                        // For numeric section
                        for (int k = 0; k <= 9; k++) {
                            if (StringMatcher.match(String.valueOf(getItem(j).pinyin.charAt(0)), String.valueOf(k))) {
                                return j;
                            }
                        }
                    } else {
                        if (StringMatcher.match(String.valueOf(getItem(j).pinyin.toUpperCase().charAt(0)), String
                                .valueOf(mSections.charAt(i)))) { return j; }
                    }
                }
            }
            return 0;
        }

        @Override
        public int getSectionForPosition(int i) {
            return 0;
        }
    }

    private class Category {
        public String id;
        public String name;
        public int drawable = -1;
    }

    @Override
    public void onClick(View arg0) {
        if (arg0 == tab_cate) {
            setTab(TAB_CATE);
        } else if (arg0 == tab_fun) {
            setTab(TAB_FUN);
        } else if (arg0 == tab_brand) {
            setTab(TAB_BRAND);
        }

    }

    private static final int TOKEN_QUERY_BRANDS = 0;
    private static final int TOKEN_QUERY_CATEGORIES = 1;
    private static final int TOKEN_QUERY_FUNCTIONS = 2;
    private static final String[] BRANDS_COLUMNS = new String[]{Beauty.Brand._ID,                //0
            Beauty.Brand.BRAND_ID,                                                  //1
            Beauty.Brand.NAME,                                                      //2
            Beauty.Brand.PINYIN                                                     //3
    };
    private static final String[] FUNCTION_COLUMNS = new String[]{Beauty.Function._ID,   //0
            Beauty.Function.NAME,                                           //1
            Beauty.Function.FUNCTION_ID,                                    //2
            Beauty.Function.SUB_FUNCTION_ID,                                //3
            Beauty.Function.SUB_FUNCTION                                    //4
    };

    private static final String[] CATEGORY_COLUMNS = new String[]{Beauty.Category._ID,            //0
            Beauty.Category.CATEGORY_ID,    //1
            Beauty.Category.NAME,           //2
    };

    private class QueryHandler extends AsyncQueryHandler {

        public QueryHandler() {
            super(context.getContentResolver());
        }

        @Override
        protected void onQueryComplete(int token, Object cookie, Cursor cursor) {
            super.onQueryComplete(token, cookie, cursor);
            switch (token) {
                case TOKEN_QUERY_BRANDS:
                    parseBrands(cursor);
                    break;
                case TOKEN_QUERY_CATEGORIES:
                    parseCategories(cursor);
                    break;
                case TOKEN_QUERY_FUNCTIONS:
                    parseFunctions(cursor);
                    break;
            }
        }

        private void parseCategories(Cursor cursor) {
            if (cursor != null) {
                categories.clear();
                while (cursor.moveToNext()) {
                    Category c = new Category();
                    c.id = cursor.getString(1);
                    c.name = cursor.getString(2);
                    Log.d(TAG, "add category : " + c.id + ", " + c.name);
                    categories.add(c);
                }
                adapter1.notifyDataSetChanged();
                cursor.close();
            }
        }

        private void parseFunctions(Cursor cursor) {
            if (cursor != null) {
                functions.clear();
                String lastFunctionId = "";
                Function f = null;
                while (cursor.moveToNext()) {
                    String functionId = cursor.getString(2);
                    if (functionId != null && !functionId.contentEquals(lastFunctionId)) {
                        if (f != null) {
                            functions.add(f);
                        }
                        f = new Function();
                        f.functionId = functionId;
                    }
                    lastFunctionId = functionId;
                    f.name = cursor.getString(1);
                    Function.SubFunction subFunction = new Function.SubFunction();
                    subFunction.id = cursor.getLong(0);
                    subFunction.subId = cursor.getString(3);
                    subFunction.name = cursor.getString(4);
                    f.subs.add(subFunction);
                }
                functions.add(f);
                adapter2.notifyDataSetChanged();
                cursor.close();
            }
        }

        private void parseBrands(Cursor cursor) {
            Log.d(TAG, "parseBrands, cursor is null ? " + (cursor == null));
            if (cursor != null) {
                brands.clear();
                while (cursor.moveToNext()) {
                    Brand b = new Brand();
                    b.id = cursor.getLong(0);
                    b.brandId = cursor.getString(1);
                    b.name = cursor.getString(2);
                    b.pinyin = cursor.getString(3);
                    brands.add(b);
                }
                Log.d(TAG, "parseBrands, brands size is " + brands.size());
                adapter3.notifyDataSetChanged();
                cursor.close();
            }
        }
    }


}
