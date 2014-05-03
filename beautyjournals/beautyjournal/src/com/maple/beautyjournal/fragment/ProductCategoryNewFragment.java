package com.maple.beautyjournal.fragment;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.i2mobi.net.HttpClientImplUtil;
import com.i2mobi.net.NetUtil;
import com.maple.beautyjournal.R;
import com.maple.beautyjournal.base.BaseFragment;
import com.maple.beautyjournal.entitiy.Category;
import com.maple.beautyjournal.utils.ServerDataUtils;

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

public class ProductCategoryNewFragment extends BaseFragment {
	private ExpandableListView list;
	FragmentActivity context;
	private ListAdapter adapter;
	private List<Category> categories = new ArrayList<Category>();
	String errorMsg = "";
	private static final int CATEGORY = 0; // 分类
	private static final int BRAND = 1; // 品牌
	private static final String CATEGORY_DATA = "category_list_data"; // 分类数据
	private static final String BRAND_DATA = "brand_list_data"; // 品牌数据
	private int currentType = BRAND; // 当前类型
	private Cache cache = null;
	private boolean isCache = false;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		context = getActivity();
		View v = inflater.inflate(R.layout.activity_product_category_new,
				container, false);
		cache = new Cache(context);
		initList(v);
		initData();
		return v;
	}

	/**
	 * 初始化数据
	 */
	private void initData() {
		String grandData = cache.getCache(BRAND_DATA);
		String categoryData = cache.getCache(CATEGORY_DATA);
		if (grandData == null || categoryData == null) {
			isCache = false;
			setType(BRAND);
			new GetDataTask().execute();
		} else {
			try {
				isCache = true;
				JSONObject json = new JSONObject(grandData);
				JSONArray infoArray = new JSONArray(json.getString("info"));
				dealGrandData(infoArray);
				json = new JSONObject(categoryData);
				JSONObject info = json.getJSONObject("info");
				dealCategoryData(info);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 根据名称加载图片
	 * 
	 * @param name
	 * @return
	 */
	public int getImageResourceID(String name) {
		Resources res = context.getResources();
		return res.getIdentifier(name, "drawable", context.getPackageName());
	}

	private void initList(View parent) {
		list = (ExpandableListView) parent.findViewById(R.id.cate_list2);
		list.setDividerHeight(0);// 隐藏掉自带的分割线
		list.setSelector(this.getResources().getDrawable(
				R.drawable.menu_selected));
		list.setGroupIndicator(null);
		adapter = new ListAdapter();
		list.setAdapter(adapter);
		list.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
			@Override
			public boolean onChildClick(ExpandableListView expandableListView,
					View view, int i, int i2, long l) {
				Category.SubCategory sub = adapter.getChild(i, i2);
				if (sub != null && sub.enabled) {
					FragmentManager fm = ProductCategoryNewFragment.this
							.getActivity().getSupportFragmentManager();
					FragmentTransaction ft = fm.beginTransaction();
					Fragment product_cate_child = Fragment.instantiate(
							ProductCategoryNewFragment.this.getActivity(),
							ProductListFragment.class.getName(), null);
					Bundle bundle = new Bundle();
					switch (sub.type) {
					case BRAND:
						bundle.putString(ProductListFragment.LIST_BY_BRAND,
								sub.subId);
						break;
					case CATEGORY:
						bundle.putString(ProductListFragment.LIST_BY_CATEGORY,
								sub.subId);
						break;
					default:
						bundle.putString(ProductListFragment.LIST_BY_CATEGORY,
								sub.subId);
						break;
					}
					bundle.putString("name", sub.name);
					product_cate_child.setArguments(bundle);
					ft.replace(R.id.content, product_cate_child);
					ft.addToBackStack(null);
					ft.commit();
					context.getSupportFragmentManager()
							.executePendingTransactions();
					list.collapseGroup(i);
				}
				return true;
			}
		});
	}

	public class ListAdapter extends BaseExpandableListAdapter {

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
					ProductCategoryNewFragment.this.getActivity()).inflate(
					R.layout.product_cate_child_new, null);
			TextView tv = (TextView) layout.findViewById(R.id.ctv);
			tv.setText(getChild(groupPosition, childPosition).name);
			if (getChild(groupPosition, childPosition).enabled
					&& getChild(groupPosition, childPosition).type == BRAND) {
				((TextView) layout.findViewById(R.id.bottomBorder))
						.setVisibility(View.INVISIBLE);
			} else {
				((TextView) layout.findViewById(R.id.bottomBorder))
						.setVisibility(View.VISIBLE);
			}
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
			LinearLayout layout = (LinearLayout) LayoutInflater.from(
					ProductCategoryNewFragment.this.getActivity()).inflate(
					R.layout.product_cate_group_new, null);
			if (isExpanded) {
				layout.setBackgroundColor(Color.argb(0xff, 0xff, 0xff, 0xff));
			}
			TextView tv = (TextView) layout.findViewById(R.id.gtv);
			tv.setText(getGroup(groupPosition).name);
			TextView num = (TextView) layout.findViewById(R.id.num);
			num.setText("共" + getGroup(groupPosition).subs.size() + "个");
			//加载对应的图片
			ImageView pic = (ImageView) layout.findViewById(R.id.pic);
			pic.setBackgroundResource(getImageResourceID("store_"
					+ getGroup(groupPosition).functionId));
			return layout;

		}

		public boolean isChildSelectable(int groupPosition, int childPosition) {
			return true;
		}

		public boolean hasStableIds() {
			return true;
		}

	}

	/**
	 * 设置当前请求数据类型
	 * 
	 * @param type
	 */
	private void setType(int type) {
		currentType = type;
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
					subC.type = CATEGORY;
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
	private void dealGrandData(JSONArray info) {
		try {
			categories.clear();
			Category cate = new Category();
			cate.name = "品牌";
			cate.functionId = "0";
			String start = "0"; // 头拼音字母
			String pinyin = "";
			for (int i = 0; i < info.length(); i++) {
				JSONObject jsonObject = info.optJSONObject(i);
				pinyin = jsonObject.getString("pinyin");
				if (pinyin != ""
						&& start.equals(pinyin.substring(0, 1)) == false) {
					Category.SubCategory fakeCate = new Category.SubCategory();
					start = pinyin.substring(0, 1);
					fakeCate.name = "·" + start.toUpperCase();
					fakeCate.subId = "-1";
					fakeCate.enabled = false;
					cate.subs.add(fakeCate);
				}
				Category.SubCategory subCate = new Category.SubCategory();
				subCate.name = jsonObject.getString("name");
				subCate.subId = jsonObject.getString("id");
				subCate.type = BRAND;
				cate.subs.add(subCate);
			}
			categories.add(cate);
			// 先进行品牌的数据请求，再进行分类的请求，保证品牌在前
			if (isCache == false) {
				setType(CATEGORY);
				new GetDataTask().execute();
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
	private class GetDataTask extends AsyncTask<Void, Void, Void> {

		@Override
		protected Void doInBackground(Void... params) {
			Context context = getActivity();
			String url = ""; // getBrandList();
			switch (currentType) {
			case CATEGORY:
				url = NetUtil.getCategoryList();
				break;
			case BRAND:
				url = NetUtil.getBrandList();
				break;
			}

			Log.d(TAG, "doInBackground, url is " + url);
			NetUtil util = new HttpClientImplUtil(context, url);
			String result = util.doGet();
			Log.d(TAG, "result is " + result);
			try {
				JSONObject json = new JSONObject(result);
				if (ServerDataUtils.isTaskSuccess(json)) {
					switch (currentType) {
					case CATEGORY:
						// 写缓存
						cache.setCache(CATEGORY_DATA, result);
						JSONObject info = json.getJSONObject("info");
						dealCategoryData(info);
						break;
					case BRAND:
						// 写缓存
						cache.setCache(BRAND_DATA, result);
						JSONArray infoArray = new JSONArray(
								json.getString("info"));
						dealGrandData(infoArray);
						break;
					}
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
			adapter.notifyDataSetChanged();
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
