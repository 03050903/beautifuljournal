package com.maple.beautyjournal;

import android.app.Activity;
import android.os.Bundle;
import android.widget.EditText;

/**
 * Created by mosl on 14-4-29.
 */
public class SearchActivity extends Activity {

    private EditText searchEdit;
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        searchEdit=(EditText)findViewById(R.id.search_edit);
        Bundle bundle=getIntent().getBundleExtra("key");
        String search=bundle.get("search").toString();
        searchEdit.setText(bundle.get("search").toString());
    }
}