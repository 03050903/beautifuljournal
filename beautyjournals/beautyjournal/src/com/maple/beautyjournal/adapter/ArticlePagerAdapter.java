package com.maple.beautyjournal.adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.text.format.DateFormat;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ListView;
import com.maple.beautyjournal.R;
import com.maple.beautyjournal.entitiy.Article;

import java.util.*;

public class ArticlePagerAdapter extends PagerAdapter {
	
    private static final long MILLI_SECONDS_IN_HOUR = 3600 * 1000 + 1;
    private List<List<Article>> mArticles = new ArrayList<List<Article>>();
    private OnClickListener mOnArticleItemClickListener;

    private Context mContext;
    public ArticlePagerAdapter(Context context){
    	this.mContext = context;
    }
    public void setArticles(List<List<Article>> articles){
    	this.mArticles = articles;
    }

    public void setOnArticleItemClickListener(OnClickListener onClickListener){
    	this.mOnArticleItemClickListener = onClickListener;
    }

    
    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

    @Override
    public int getCount() {
        return mArticles.size();
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public boolean isViewFromObject(View view, Object o) {
        return view == o;
    }
        
    private String getRelativeTimeString(long time) {
    	time = time*1000;
        long current = System.currentTimeMillis();
        if (current - time < MILLI_SECONDS_IN_HOUR) {
            return DateUtils.getRelativeTimeSpanString(time, System
                    .currentTimeMillis(), DateUtils.MINUTE_IN_MILLIS, DateUtils.FORMAT_ABBREV_RELATIVE).toString();
        } else if (DateUtils.isToday(time)) {
            return getTodayRelativeTime(time);
        } else if (isYear(time)) {
            new DateFormat();
			return DateFormat.format("MM/dd", new Date(time)).toString();
        } else {
            new DateFormat();
			return DateFormat.format("yy/MM/dd", new Date(time)).toString();
        }
    }

    private String getTodayRelativeTime(long time) {
        int hour = (int) ((System.currentTimeMillis() - time) / (MILLI_SECONDS_IN_HOUR));
        return String.format(mContext.getString(R.string.hours_ago), hour);
    }

    private boolean isYear(long time) {
        GregorianCalendar calendar = new GregorianCalendar();
        Date date = new Date(time);
        GregorianCalendar calendar1 = new GregorianCalendar();
        calendar1.setTime(date);
        return calendar.get(Calendar.YEAR) == calendar1.get(Calendar.YEAR);
    }


    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        LayoutInflater inflater = LayoutInflater.from(this.mContext);
        int id = (position % 2 == 0) ? R.layout.article_list_item1 : R.layout.article_list_item2;
        id = R.layout.article_list ;
        View pager = inflater.inflate(id, container, false);
        List<Article> articleList = mArticles.get(position);
        int imageCount = 0;
        ListView listView =(ListView)pager.findViewById(R.id.lv_article_list) ;
        ArticleListAdapter articleListAdapter = new ArticleListAdapter(this.mContext , articleList) ;
        listView.setAdapter(articleListAdapter);

        /*
        Set<Integer> usedLayout = new HashSet<Integer>();
        for (int i = 0; i < articleList.size(); i++) {
            Article article = articleList.get(i);
            if (article.hasPic()) {
                ImageView iv;
                TextView title;
                if (imageCount == 0) {
                    iv = (ImageView) pager.findViewById(R.id.image1);
                    title = (TextView) pager.findViewById(R.id.text1_in_frame1);
                    title.setText(article.title);
                    pager.findViewById(R.id.linear1).setVisibility(View.GONE);
                    pager.findViewById(R.id.frame1).setVisibility(View.VISIBLE);
                } else {
                    iv = (ImageView) pager.findViewById(R.id.image2);
                    title = (TextView) pager.findViewById(R.id.text2_in_frame2);
                    title.setText(article.title);
                    pager.findViewById(R.id.linear2).setVisibility(View.GONE);
                    pager.findViewById(R.id.frame2).setVisibility(View.VISIBLE);
                }
                ImageLoader.getInstance().displayImage(article.pic, iv);
                iv.setTag(article);
                iv.setOnClickListener(mOnArticleItemClickListener);
                imageCount++;
            } else {
                int textViewId = getTextViewId(id, i, imageCount);
                if (textViewId != 0) {
                    RelativeLayout tv = (RelativeLayout) pager.findViewById(textViewId);
                    usedLayout.add(textViewId);
                    TextView title = (TextView) tv.getChildAt(0);
                    TextView time = (TextView) tv.getChildAt(1);
                    title.setText(article.title);
                    if (article.title.length() >= 15) {
                        title.setTextSize(16);
                    } else {
                        title.setTextSize(18);
                    }
                    String relativeTime = getRelativeTimeString(article.releaseTime);
                    time.setText(relativeTime);
                    if (System.currentTimeMillis() - article.releaseTime * 1000 < MILLI_SECONDS_IN_HOUR) {
                        //in the same hour, show "new" indicator
                        time.setCompoundDrawablesWithIntrinsicBounds(null, null, mContext.getResources()
                                .getDrawable(R.drawable.new_article_indicator), null);
                    } else {
                        time.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
                    }
                    tv.setTag(article);
                    tv.setOnClickListener(mOnArticleItemClickListener);
                }
            }
        }
        for (int i : layoutList) {
            if (!usedLayout.contains(i)) {
                pager.findViewById(i).setVisibility(View.INVISIBLE);
            }
        }
        */
        container.addView(pager);
        return pager;
    }

    private int getTextViewId(int id, int i, int imageCount) {
        if (imageCount == 0) {
            return layoutList.get(i);
        } else if (imageCount == 2) {
            if (id == R.layout.article_list_item2) {
                if (i == 2) {
                    return layoutList.get(0);
                } else if (i <= 6) {
                    return layoutList.get(i);
                } else {
                    return layoutList.get(i + 2);
                }
            } else {
                if (i <= 3) {
                    return layoutList.get(i - 2);
                } else if (i <= 5) {
                    return layoutList.get(i);
                } else {
                    return layoutList.get(i + 2);
                }
            }
        }else if(imageCount ==1){
            if (id == R.layout.article_list_item2) {
                if (i == 1) {
                    return layoutList.get(0);
                } else {
                    return layoutList.get(i + 1);
                }
            } else {
                if (i <= 2) {
                    return layoutList.get(i - 1);
                } else {
                    return layoutList.get(i + 1);
                }
            }        	
        }
        return 0;
    }
    
    private static final List<Integer> layoutList = new ArrayList<Integer>();

    static {
        layoutList.add(R.id.text1);
        layoutList.add(R.id.text2);
        layoutList.add(R.id.text3);
        layoutList.add(R.id.text4);
        layoutList.add(R.id.text5);
        layoutList.add(R.id.text6);
        layoutList.add(R.id.text7);
        layoutList.add(R.id.text8);
        layoutList.add(R.id.text9);
        layoutList.add(R.id.text10);
    }
}