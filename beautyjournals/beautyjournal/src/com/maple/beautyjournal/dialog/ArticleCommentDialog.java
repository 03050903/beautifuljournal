package com.maple.beautyjournal.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;

import com.maple.beautyjournal.R;

/**
 * Created by mosl on 14-4-21.
 */
public class ArticleCommentDialog extends Dialog {

    Context context;
    public ArticleCommentDialog(Context context) {
        super(context);
        this.context=context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_article_comment_dia);
    }

}
