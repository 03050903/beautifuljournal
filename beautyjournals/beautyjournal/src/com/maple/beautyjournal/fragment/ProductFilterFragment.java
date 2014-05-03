package com.maple.beautyjournal.fragment;

import android.content.AsyncQueryHandler;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Color;
import android.os.AsyncTask;
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

import com.i2mobi.net.HttpClientImplUtil;
import com.i2mobi.net.NetUtil;
import com.i2mobi.utils.StringMatcher;
import com.i2mobi.widget.IndexableListView;
import com.maple.beautyjournal.R;
import com.maple.beautyjournal.base.BaseFragment;
import com.maple.beautyjournal.entitiy.Brand;
import com.maple.beautyjournal.entitiy.Function;
import com.maple.beautyjournal.entitiy.Category;
import com.maple.beautyjournal.entitiy.Function.SubFunction;
import com.maple.beautyjournal.provider.Beauty;
import com.maple.beautyjournal.utils.ServerDataUtils;
import com.maple.beautyjournal.utils.Utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.http.util.EncodingUtils;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * 筛选功能
 * 
 * @author enterli
 * 
 */
public class ProductFilterFragment extends BaseFragment implements
		OnClickListener {

	private final int TAB_CATE = 0;
	private final int TAB_FUN = 1;
	private final int TAB_BRAND = 2;
	private int currentTab;

	private FrameLayout tab_cate;
	private FrameLayout tab_fun;
	private FrameLayout tab_brand;
	private FrameLayout tab_1;
	private FrameLayout tab_2;
	private ExpandableListView list1;
	private ExpandableListView list2;
	private IndexableListView list3;
	FragmentActivity context;
	private List1Adapter adapter1;
	private List2Adapter adapter2;
	private List3Adapter adapter3;
	private List<Brand> brands = new ArrayList<Brand>();
	private List<Function> functions = new ArrayList<Function>();
	private List<Category> categories = new ArrayList<Category>();
	private static final String CATEGORY_DATA = "category_list_data"; // 分类数据
	private static final String BRAND_DATA = "brand_list_data"; // 品牌数据
	private static final String FUNCTION_DATA = "function_list_data";
	Cache cache = null;
	String errorMsg = "";

	private TextView title;
	private ImageView search;
	private ImageView titleBack;
	private ImageView shopingcar;
	private TextView titleFilter;
	private String passName;
	private String category;
	private String brand;
	private String passType;

	private TextView all;

	public static String fromChild = ""; // 记录从子页面返回的数据

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		context = getActivity();
		title = (TextView) context.findViewById(R.id.title);
		search = (ImageView) context.findViewById(R.id.search);
		titleBack = (ImageView) context.findViewById(R.id.title_back);
		shopingcar = (ImageView) context.findViewById(R.id.shopingcar);
		titleFilter = (TextView) context.findViewById(R.id.title_filter);
		View v = inflater.inflate(R.layout.product_filter, container, false);
		initQueryParameter();
		titleBack.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				recoverTitleBar();
			}

		});
		cache = new Cache(context);
		initTab(v);
		initList1(v);
		initList2(v);
		initList3(v);
		if (passType.equals(ProductListFragment.LIST_BY_BRAND)) {
			setTab(TAB_CATE);
		} else {
			setTab(TAB_BRAND);
		}
		return v;
	}

	@Override
	public void onResume() {
		super.onResume();
		/*
		 * if(fromChild.equals("back_again")){ fromChild = ""; //再退一步
		 * ProductFilterFragment.this.getActivity().onBackPressed(); }
		 */
	}

	/**
	 * 复原标题栏回退
	 */
	private void recoverTitleBar() {
		title.setText(passName);
		search.setVisibility(View.GONE);
		titleBack.setVisibility(View.VISIBLE);
		shopingcar.setVisibility(View.GONE);
		titleFilter.setVisibility(View.VISIBLE);
		ProductFilterFragment.this.getActivity().onBackPressed();
	}

	/**
	 * 设置标题栏
	 * 
	 * @param name
	 */
	private void setTitleBar() {
		title.setText("筛选");
		search.setVisibility(View.GONE);
		titleBack.setVisibility(View.VISIBLE);
		shopingcar.setVisibility(View.GONE);
		titleFilter.setVisibility(View.INVISIBLE);
	}

	private void initQueryParameter() {
		Bundle bundle = getArguments();
		passName = bundle.getString("name");
		setTitleBar();
		if (bundle.containsKey(ProductListFragment.LIST_BY_BRAND)) {
			brand = bundle.getString(ProductListFragment.LIST_BY_BRAND);
			passType = ProductListFragment.LIST_BY_BRAND;
		} else if (bundle.containsKey(ProductListFragment.LIST_BY_CATEGORY)) {
			category = bundle.getString(ProductListFragment.LIST_BY_CATEGORY);
			passType = ProductListFragment.LIST_BY_CATEGORY;
		} else {
			// default, for test
		}
	}

	private void initTab(View parent) {
		all = (TextView) parent.findViewById(R.id.all);
		tab_1 = (FrameLayout) parent.findViewById(R.id.tab_1);
		tab_2 = (FrameLayout) parent.findViewById(R.id.tab_2);
		if (passType.equals(ProductListFragment.LIST_BY_BRAND)) {
			((TextView) tab_1.getChildAt(0)).setText("分类");
		} else if (passType.equals(ProductListFragment.LIST_BY_CATEGORY)) {
			((TextView) tab_1.getChildAt(0)).setText("品牌");
		} else {
			((TextView) tab_1.getChildAt(0)).setText("分类");
		}
		tab_1.setOnClickListener(this);
		tab_2.setOnClickListener(this);
		all.setOnClickListener(this);
	}

	@Override
	public void onClick(View arg0) {
		if (arg0 == tab_1) {
			if (passType.equals(ProductListFragment.LIST_BY_BRAND)) {
				setTab(TAB_CATE);
			} else {
				setTab(TAB_BRAND);
			}
		} else if (arg0 == tab_2) {
			setTab(TAB_FUN);
		} else if (arg0 == all) {
			switch (currentTab) {
			case TAB_CATE:
				ProductListFragment.filterID = "";
				ProductListFragment.filterType = ProductListFragment.LIST_BY_CATEGORY;
				break;
			case TAB_FUN:
				ProductListFragment.filterID = "";
				ProductListFragment.filterType = ProductListFragment.LIST_BY_FUNCTION;
				break;
			case TAB_BRAND:
				ProductListFragment.filterID = "";
				ProductListFragment.filterType = ProductListFragment.LIST_BY_BRAND;
				break;
			}
			recoverTitleBar();
		}

	}

	private void setTab(int tab) {
		currentTab = tab;
		list1.setVisibility(View.GONE);
		list2.setVisibility(View.GONE);
		list3.setVisibility(View.GONE);
		switch (currentTab) {
		case TAB_CATE:
			tab_1.setBackgroundResource(R.drawable.product_filter_left_2);
			tab_2.setBackgroundResource(R.drawable.product_filter_right_1);
			((TextView) tab_1.getChildAt(0)).setTextColor(Color.WHITE);
			((TextView) tab_2.getChildAt(0)).setTextColor(Color.argb(0xff,
					0xfe, 0x7d, 0x9c));
			list1.setVisibility(View.VISIBLE);
			all.setText("全部分类");
			break;
		case TAB_FUN:
			tab_1.setBackgroundResource(R.drawable.product_filter_left_1);
			tab_2.setBackgroundResource(R.drawable.product_filter_right_2);
			((TextView) tab_1.getChildAt(0)).setTextColor(Color.argb(0xff,
					0xfe, 0x7d, 0x9c));
			((TextView) tab_2.getChildAt(0)).setTextColor(Color.WHITE);
			list2.setVisibility(View.VISIBLE);
			all.setText("全部功效");
			break;
		case TAB_BRAND:
			tab_1.setBackgroundResource(R.drawable.product_filter_left_2);
			tab_2.setBackgroundResource(R.drawable.product_filter_right_1);
			((TextView) tab_1.getChildAt(0)).setTextColor(Color.WHITE);
			((TextView) tab_2.getChildAt(0)).setTextColor(Color.argb(0xff,
					0xfe, 0x7d, 0x9c));
			list3.setVisibility(View.VISIBLE);
			all.setText("全部品牌");
			break;
		}
	}

	private void initList1(View parent) {
		list1 = (ExpandableListView) parent.findViewById(R.id.cate_list1);
		list1.setSelector(this.getResources().getDrawable(
				R.drawable.menu_selected));
		list1.setGroupIndicator(null);
		adapter1 = new List1Adapter();
		list1.setAdapter(adapter1);
		list1.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
			@Override
			public boolean onChildClick(ExpandableListView expandableListView,
					View view, int i, int i2, long l) {
				Category.SubCategory sub = adapter1.getChild(i, i2);
				if (sub != null) {
					ProductListFragment.filterID = sub.subId;
					ProductListFragment.filterType = ProductListFragment.LIST_BY_CATEGORY;
					recoverTitleBar();
				}
				return true;
			}
		});

		String cacheStr = cache.getCache(CATEGORY_DATA);
		if (cacheStr != null) {
			try {
				JSONObject json = new JSONObject(cacheStr);
				JSONObject info = json.getJSONObject("info");
				dealCategoryData(info);
				adapter1.notifyDataSetChanged();
			} catch (Exception e) {
				e.printStackTrace();

			}
		} else {
			new GetCategoryDataTask().execute();
		}
	}

	private void initList2(View parent) {
		list2 = (ExpandableListView) parent.findViewById(R.id.cate_list2);
		list2.setSelector(this.getResources().getDrawable(
				R.drawable.menu_selected));
		list2.setGroupIndicator(null);
		adapter2 = new List2Adapter();
		list2.setAdapter(adapter2);
		list2.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
			@Override
			public boolean onChildClick(ExpandableListView expandableListView,
					View view, int i, int i2, long l) {
				Function.SubFunction sub = adapter2.getChild(i, i2);
				if (sub != null) {
					ProductListFragment.filterID = sub.subId;
					ProductListFragment.filterType = ProductListFragment.LIST_BY_FUNCTION;
					recoverTitleBar();
				}
				return true;
			}
		});
		String cacheStr = cache.getCache(FUNCTION_DATA);
		if (cacheStr != null) {
			try {
				JSONObject json = new JSONObject(cacheStr);
				JSONObject info = json.getJSONObject("info");
				dealFunctionData(info);
				adapter2.notifyDataSetChanged();
			} catch (Exception e) {
				e.printStackTrace();

			}
		} else {
			new GetFunctionDataTask().execute();
		}
	}

	private void initList3(View parent) {
		list3 = (IndexableListView) parent.findViewById(R.id.cate_list3);
		list3.setDividerHeight(0);
		adapter3 = new List3Adapter();
		list3.setFastScrollEnabled(true);
		list3.setAdapter(adapter3);
		list3.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> adapterView, View view,
					int i, long l) {
				Brand b = brands.get(i);
				if (b != null && b.brandId.equals("-1") == false) {
					ProductListFragment.filterID = b.brandId;
					ProductListFragment.filterType = ProductListFragment.LIST_BY_BRAND;
					recoverTitleBar();
				}
			}
		});
		String cacheStr = cache.getCache(BRAND_DATA);
		if (cacheStr != null) {
			try {
				JSONObject json = new JSONObject(cacheStr);
				JSONArray info = new JSONArray(json.getString("info"));
				dealBrandData(info);
				adapter3.notifyDataSetChanged();
			} catch (Exception e) {
				e.printStackTrace();

			}
		} else {
			new GetBrandDataTask().execute();
		}
	}

	public class List1Adapter extends BaseExpandableListAdapter {

		public Category.SubCategory getChild(int groupPosition,
				int childPosition) {
			return getGroup(groupPosition).subs.get(childPosition);
		}

		public long getChildId(int groupPosition, int childPosition) {
			return getChild(groupPosition, childPosition).id;
		}

		public int getChildrenCount(int groupPosition) {
			return getGroup(groupPosition).subs.size();
		}

		public View getChildView(int groupPosition, int childPosition,
				boolean isLastChild, View convertView, ViewGroup parent) {
			LinearLayout layout = (LinearLayout) LayoutInflater.from(
					ProductFilterFragment.this.getActivity()).inflate(
					R.layout.product_filter_child, null);
			TextView tv = (TextView) layout.findViewById(R.id.ctv);
			tv.setText(getChild(groupPosition, childPosition).name);
			return layout;

		}

		public Category getGroup(int groupPosition) {
			return categories.get(groupPosition);
		}

		public int getGroupCount() {
			return categories.size();
		}

		public long getGroupId(int groupPosition) {
			return groupPosition;
		}

		public View getGroupView(int groupPosition, boolean isExpanded,
				View convertView, ViewGroup parent) {
			// 实例化布局文件
			RelativeLayout layout = (RelativeLayout) LayoutInflater.from(
					ProductFilterFragment.this.getActivity()).inflate(
					R.layout.product_filter_group, null);
			ImageView iv = (ImageView) layout.findViewById(R.id.giv);
			// 判断分组是否展开，分别传入不同的图片资源
			/*
			 * if (isExpanded) { layout.setBackgroundColor(Color.argb(0xff,
			 * 0xfe, 0x49, 0x80)); iv.setImageResource(R.drawable.arrow_close);
			 * } else { iv.setImageResource(R.drawable.arrow_open);
			 * 
			 * }
			 */
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

	public class List2Adapter extends BaseExpandableListAdapter {

		public Function.SubFunction getChild(int groupPosition,
				int childPosition) {
			return getGroup(groupPosition).subs.get(childPosition);
		}

		public long getChildId(int groupPosition, int childPosition) {
			return getChild(groupPosition, childPosition).id;
		}

		public int getChildrenCount(int groupPosition) {
			return getGroup(groupPosition).subs.size();
		}

		public View getChildView(int groupPosition, int childPosition,
				boolean isLastChild, View convertView, ViewGroup parent) {
			LinearLayout layout = (LinearLayout) LayoutInflater.from(
					ProductFilterFragment.this.getActivity()).inflate(
					R.layout.product_filter_child, null);
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

		public View getGroupView(int groupPosition, boolean isExpanded,
				View convertView, ViewGroup parent) {
			// 实例化布局文件
			RelativeLayout layout = (RelativeLayout) LayoutInflater.from(
					ProductFilterFragment.this.getActivity()).inflate(
					R.layout.product_filter_group, null);
			ImageView iv = (ImageView) layout.findViewById(R.id.giv);
			// 判断分组是否展开，分别传入不同的图片资源
			/*
			 * if (isExpanded) { layout.setBackgroundColor(Color.argb(0xff,
			 * 0xfe, 0x49, 0x80)); iv.setImageResource(R.drawable.arrow_close);
			 * } else { iv.setImageResource(R.drawable.arrow_open);
			 * 
			 * }
			 */
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
				v = LayoutInflater.from(context).inflate(
						R.layout.brand_list_item2, viewGroup, false);
			}
			TextView tv = (TextView) v.findViewById(R.id.brand);
			tv.setText(brands.get(i).name);
			TextView bottomBorder = (TextView) v
					.findViewById(R.id.bottomBorder);
			if (brands.get(i).brandId.equals("-1")) {
				bottomBorder.setVisibility(View.VISIBLE);
			} else {
				bottomBorder.setVisibility(View.INVISIBLE);
			}
			return v;
		}

		// code for sections
		private String mSections = "#ABCDEFGHIJKLMNOPQRSTUVWXYZ";

		@Override
		public Object[] getSections() {
			String[] sections = new String[mSections.length()];
			for (int i = 0; i < mSections.length(); i++) {
				sections[i] = String.valueOf(mSections.charAt(i));
			}
			return sections;
		}

		@Override
		public int getPositionForSection(int section) {
			// If there is no item for current section, previous section will be
			// selected
			for (int i = section; i >= 0; i--) {
				for (int j = 0; j < getCount(); j++) {
					if (i == 0) {
						// For numeric section
						for (int k = 0; k <= 9; k++) {
							if (StringMatcher
									.match(String.valueOf(getItem(j).pinyin
											.charAt(0)), String.valueOf(k))) {
								return j;
							}
						}
					} else {
						if (StringMatcher.match(String
								.valueOf(getItem(j).pinyin.toUpperCase()
										.charAt(0)), String.valueOf(mSections
								.charAt(i)))) {
							return j;
						}
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

	/**
	 * 处理请求分类的数据
	 * 
	 * @param info
	 */
	private void dealCategoryData(JSONObject info) {
		try {
			Iterator keys = info.keys();
			String[] sortKeys = new String[info.length()];
			// 获取key，按从小到大排序
			int i = 0;
			while (keys.hasNext()) {
				String key = keys.next().toString();
				sortKeys[i++] = key;
			}
			Arrays.sort(sortKeys);
			for (i = 0; i < info.length(); i++) {
				String key = sortKeys[i];
				JSONObject firstCate = info.getJSONObject(key);
				Category firstC = new Category();
				firstC.name = firstCate.getString("name");
				firstC.functionId = key;
				// 映射子类
				Map<String, String> subCate = new HashMap<String, String>();
				JSONObject secondCate = firstCate.getJSONObject("sub");
				List<String> subSortKeys = new ArrayList<String>();
				Iterator secondKeys = secondCate.keys();
				while (secondKeys.hasNext()) {
					JSONObject subObject = secondCate.getJSONObject(
							secondKeys.next().toString()).getJSONObject("sub");
					Iterator subKeys = subObject.keys();
					while (subKeys.hasNext()) {
						String subKey = subKeys.next().toString();
						subSortKeys.add(subKey);
						subCate.put(subKey, subObject.getString(subKey));
					}
				}
				// 排序
				Collections.sort(subSortKeys);
				for (String subKey : subSortKeys) {
					Category.SubCategory subC = new Category.SubCategory();
					subC.name = subCate.get(subKey);
					subC.subId = subKey;
					firstC.subs.add(subC);
				}
				categories.add(firstC);
			}
		} catch (Exception e) {
			errorMsg = e.getLocalizedMessage();
		}
	}

	/**
	 * 处理请求品牌数据
	 * 
	 * @param info
	 */
	private void dealBrandData(JSONArray info) {
		try {
			brands.clear();
			String pinyin = "";
			String start = "";
			long id = 0;
			for (int i = 0; i < info.length(); i++) {
				JSONObject jsonObject = info.optJSONObject(i);
				pinyin = jsonObject.getString("pinyin");
				if (pinyin != ""
						&& start.equals(pinyin.substring(0, 1)) == false) {
					start = pinyin.substring(0, 1);
					Brand b = new Brand();
					b.name = "·" + start.toUpperCase();
					b.brandId = "-1";
					b.id = id++;
					b.pinyin = start;
					brands.add(b);
				}
				Brand b = new Brand();
				b.id = id++;
				b.name = jsonObject.getString("name");
				b.brandId = jsonObject.getString("id");
				b.pinyin = jsonObject.getString("pinyin");
				brands.add(b);
			}
		} catch (Exception e) {
			errorMsg = e.getLocalizedMessage();
		}
	}

	/**
	 * 处理请求功效数据
	 * 
	 * @param info
	 */
	private void dealFunctionData(JSONObject info) {
		try {
			functions.clear();
			Iterator keys = info.keys();
			String[] sortKeys = new String[info.length()];
			// 获取key，按从小到大排序
			int i = 0;
			while (keys.hasNext()) {
				String key = keys.next().toString();
				sortKeys[i++] = key;
			}
			Arrays.sort(sortKeys);
			for (i = 0; i < info.length(); i++) {
				String key = sortKeys[i];
				JSONObject parentFuncs = info.getJSONObject(key);
				Function f = new Function();
				f.name = parentFuncs.getString("name");
				f.functionId = key;
				// 获取子节点
				JSONObject subFucs = parentFuncs.getJSONObject("sub");
				String[] subSortKeys = new String[subFucs.length()];
				Iterator subKeys = subFucs.keys();
				int j = 0;
				while (subKeys.hasNext()) {
					String subkey = subKeys.next().toString();
					subSortKeys[j++] = subkey;
				}
				Arrays.sort(subSortKeys);
				for (j = 0; j < subFucs.length(); j++) {
					String subKey = subSortKeys[j];
					Function.SubFunction subf = new Function.SubFunction();
					subf.name = subFucs.getString(subKey);
					subf.subId = subKey;
					f.subs.add(subf);
				}
				functions.add(f);
			}
		} catch (Exception e) {
			errorMsg = e.getLocalizedMessage();
		}
	}

	/**
	 * 请求获取分类数据
	 * 
	 * @author enterli
	 * 
	 */
	private class GetCategoryDataTask extends AsyncTask<Void, Void, Void> {

		@Override
		protected Void doInBackground(Void... params) {
			Context context = getActivity();
			String url = NetUtil.getCategoryList();

			Log.d(TAG, "doInBackground, url is " + url);
			NetUtil util = new HttpClientImplUtil(context, url);
			String result = util.doGet();
			Log.d(TAG, "result is " + result);
			try {
				JSONObject json = new JSONObject(result);
				if (ServerDataUtils.isTaskSuccess(json)) {
					// 写缓存
					cache.setCache(CATEGORY_DATA, result);
					JSONObject info = json.getJSONObject("info");
					dealCategoryData(info);
				} else {
					errorMsg = ServerDataUtils.getErrorMessage(json);
				}
			} catch (Exception e) {
				errorMsg = e.getLocalizedMessage();
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void aVoid) {
			dismissProgress();
			adapter1.notifyDataSetChanged();
			/*
			 * if (!TextUtils.isEmpty(errorMsg)) { Toast.makeText(getActivity(),
			 * errorMsg, Toast.LENGTH_LONG).show(); errorMsg = null; }
			 * list.onRefreshComplete(); if (offset == lastOffset) {
			 * list.setMode(PullToRefreshBase.Mode.DISABLED); }
			 */
			super.onPostExecute(aVoid);
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			dismissProgress();
			if (categories.size() == 0) {
				showProgress();
			}
		}
	}

	private class GetBrandDataTask extends AsyncTask<Void, Void, Void> {

		@Override
		protected Void doInBackground(Void... params) {
			Context context = getActivity();
			String url = NetUtil.getBrandList();

			Log.d(TAG, "doInBackground, url is " + url);
			NetUtil util = new HttpClientImplUtil(context, url);
			String result = util.doGet();
			Log.d(TAG, "result is " + result);
			try {
				JSONObject json = new JSONObject(result);
				if (ServerDataUtils.isTaskSuccess(json)) {
					// 写缓存
					cache.setCache(BRAND_DATA, result);
					JSONArray infoArray = new JSONArray(json.getString("info"));
					dealBrandData(infoArray);
				} else {
					errorMsg = ServerDataUtils.getErrorMessage(json);
				}
			} catch (Exception e) {
				errorMsg = e.getLocalizedMessage();
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void aVoid) {
			dismissProgress();
			adapter3.notifyDataSetChanged();
			super.onPostExecute(aVoid);
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			dismissProgress();
			if (brands.size() == 0) {
				showProgress();
			}
		}
	}

	private class GetFunctionDataTask extends AsyncTask<Void, Void, Void> {

		@Override
		protected Void doInBackground(Void... params) {
			Context context = getActivity();
			String url = NetUtil.getFunctionList();

			Log.d(TAG, "doInBackground, url is " + url);
			NetUtil util = new HttpClientImplUtil(context, url);
			String result = util.doGet();
			Log.d(TAG, "result is " + result);
			try {
				JSONObject json = new JSONObject(result);
				if (ServerDataUtils.isTaskSuccess(json)) {
					// 写缓存
					cache.setCache(FUNCTION_DATA, result);
					JSONObject info = json.getJSONObject("info");
					dealFunctionData(info);
				} else {
					errorMsg = ServerDataUtils.getErrorMessage(json);
				}
			} catch (Exception e) {
				errorMsg = e.getLocalizedMessage();
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void aVoid) {
			dismissProgress();
			adapter2.notifyDataSetChanged();
			super.onPostExecute(aVoid);
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			dismissProgress();
			if (functions.size() == 0) {
				showProgress();
			}
		}
	}

	private class Cache {
		Context mContext = null;
		private static final long CACHE_TIME = 3600000; // 缓存一小时
		private String url = "";

		public Cache(Context context) {
			mContext = context;
			url = context.getExternalCacheDir().toString();
		}

		public String getCache(String fileName) {
			if (fileName == null) {
				return null;
			}
			String result = null;
			File file = new File(url + "/" + fileName);
			if (file.exists() && file.isFile()) {
				long expiredTime = System.currentTimeMillis()
						- file.lastModified();
				if (expiredTime > CACHE_TIME) {
					return null;
				}
				try {
					FileInputStream fis = new FileInputStream(file);
					int buffersize = fis.available();// 取得输入流的字节长度
					byte buffer[] = new byte[buffersize];
					fis.read(buffer);// 将数据读入数组
					fis.close();// 读取完毕后要关闭流。
					result = EncodingUtils.getString(buffer, "UTF-8");// 设置取得的数据编码，防止乱码
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			return result;
		}

		public void setCache(String fileName, String content) {
			try {
				File file = new File(url + "/" + fileName);
				FileOutputStream fos = new FileOutputStream(file);

				fos.write(content.getBytes());// 写入buffer数组。如果想写入一些简单的字符，可以将String.getBytes()再写入文件;

				fos.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

}
