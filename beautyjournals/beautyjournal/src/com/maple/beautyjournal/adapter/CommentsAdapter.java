package com.maple.beautyjournal.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.i2mobi.widget.ChineseStyleSpan;
import com.maple.beautyjournal.R;
import com.maple.beautyjournal.entitiy.Comment;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;

import java.util.Date;
import java.util.List;

public class CommentsAdapter extends BaseAdapter {
    private Context context;
    private List<Comment> comments;
    ImageLoader loader;

    public CommentsAdapter(Context context, List<Comment> comments) {
        this.context = context;
        this.comments = comments;
        loader = ImageLoader.getInstance();
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }

    @Override
    public int getCount() {
        return comments.size();
    }

    @Override
    public Comment getItem(int i) {
        return comments.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View convertView, ViewGroup parent) {
        View v = convertView;
        if (v == null) {
            v = LayoutInflater.from(context).inflate(R.layout.comment_list_item, parent, false);
        }
        final Comment comment = getItem(i);
        final ImageView iv = (ImageView) v.findViewById(R.id.image);
        if (TextUtils.isEmpty(comment.image)) {
            iv.setImageResource(R.drawable.ic_launcher);
        } else {
            loader.displayImage(comment.image, iv, null, new ImageLoadingListener() {
                @Override
                public void onLoadingStarted() {

                }

                @Override
                public void onLoadingFailed(FailReason failReason) {
                    iv.setImageResource(R.drawable.ic_launcher);
                }

                @Override
                public void onLoadingComplete(Bitmap bitmap) {

                }

                @Override
                public void onLoadingCancelled() {

                }
            });
        }
        TextView tv1 = (TextView) v.findViewById(R.id.date);
        Date d = new Date(comment.time);
        tv1.setText(DateFormat.format("yy-MM-dd", d));
        TextView tv2 = (TextView) v.findViewById(R.id.content);
        String comments = comment.username + ": " + comment.content;
        SpannableString ss = new SpannableString(comments);
		ss.setSpan(new ChineseStyleSpan(Typeface.BOLD), 0, comment.username.length()+1
				, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        tv2.setText(ss);
        RatingBar rating = (RatingBar) v.findViewById(R.id.rate);
        rating.setRating(comment.star);
        return v;
    }
}