package com.maple.beautyjournal.adapter;

import android.content.Context;
import android.text.format.DateFormat;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.maple.beautyjournal.R;
import com.maple.beautyjournal.entitiy.Article;

import java.util.*;

/**
 * Created by 子星 on 2014/4/30.
 */
public class ArticleListAdapter extends BaseAdapter {

    private List<List<Article>> mArticles = new ArrayList<List<Article>>();
    Article article = null ;
    private Context mContext;
    LayoutInflater layoutInflater = null;
    List<Article> articleList = null ;
    private static final long MILLI_SECONDS_IN_HOUR = 3600 * 1000 + 1 ;

    public ArticleListAdapter(Context context, List<Article> articleList) {
        this.layoutInflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        //this.mArticles = articles;
        this.articleList = articleList ;
        //articleList = mArticles.get(0);
        this.mContext = context ;
    }
    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        view = layoutInflater.inflate(R.layout.article_list_view_text_item , null) ;
        TextView articleTitleTextView = (TextView)view.findViewById(R.id.article_title) ;
        TextView articleTimeTextView = (TextView)view.findViewById(R.id.article_time) ;
        article = articleList.get(i);
        articleTitleTextView.setText(article.title);
        String relativeTime = getRelativeTimeString(article.releaseTime);
        articleTimeTextView.setText(relativeTime);
        if (System.currentTimeMillis() - article.releaseTime * 1000 < MILLI_SECONDS_IN_HOUR) {
            //in the same hour, show "new" indicator
            articleTimeTextView.setCompoundDrawablesWithIntrinsicBounds(null, null, mContext.getResources()
                    .getDrawable(R.drawable.new_article_indicator), null);
        } else {
            articleTimeTextView.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
        }
        //articleTimeTextView.setText(article);
        return view;
    }

    @Override
    public int getCount() {
        return articleList.size();
    }

    @Override
    public Object getItem(int i) {
        return mArticles.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
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
}
