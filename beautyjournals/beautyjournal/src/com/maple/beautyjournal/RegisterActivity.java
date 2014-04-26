package com.maple.beautyjournal;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.i2mobi.net.HttpClientImplUtil;
import com.i2mobi.net.NetUtil;
import com.i2mobi.utils.DESEncryption;
import com.i2mobi.utils.ImageUtils;
import com.i2mobi.widget.PhotoChooseView;
import com.maple.beautyjournal.base.BaseActivity;
import com.maple.beautyjournal.entitiy.UserInfo;
import com.maple.beautyjournal.utils.ConstantsHelper;
import com.maple.beautyjournal.utils.ServerDataUtils;
import com.maple.beautyjournal.utils.SettingsUtil;
import com.maple.beautyjournal.utils.Utils;
import com.maple.beautyjournal.widget.ProgressWindow;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


public class RegisterActivity extends BaseActivity implements View.OnClickListener, ProgressWindow.OnDismissListener {
    private EditText usernameEdit, passwordEdit, confirmEdit;
    private Button registerButton;
    private PhotoChooseView mPhotoImageView;
    View mPicturePicker;
    View mButtonCamera;
    View mButtonGallery;
    private int gender = ConstantsHelper.GENDER_FEMALE;
    private TextView maleView, femaleView;

    private String mPhoneNumber = "";
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        usernameEdit = (EditText) findViewById(R.id.username);
        passwordEdit = (EditText) findViewById(R.id.password);
        confirmEdit = (EditText) findViewById(R.id.confirm);
        registerButton = (Button) findViewById(R.id.register);
        registerButton.setOnClickListener(this);
        mPhotoImageView = (PhotoChooseView) findViewById(R.id.avatarView);
        maleView = (TextView) findViewById(R.id.label_male);
        femaleView = (TextView) findViewById(R.id.label_female);
        maleView.setOnClickListener(this);
        femaleView.setOnClickListener(this);
        
        mPhoneNumber = this.getIntent().getExtras().getString(ConstantsHelper.PHONE_NUMBER);
        if(ConstantsHelper.TEST) {
            usernameEdit.setText("ty" + System.currentTimeMillis()/1000);
            passwordEdit.setText("123456");
            confirmEdit.setText("123456");
        }
        initPicturePicker();
    }

    private void showPicturePicker() {
        mPicturePicker.setVisibility(View.VISIBLE);
    }

    private void initPicturePicker() {
        mPicturePicker = findViewById(R.id.picture_picker);
        mPicturePicker.setOnTouchListener(new OnTouchListener() {

            @Override
            public boolean onTouch(View arg0, MotionEvent arg1) {
                mPicturePicker.setVisibility(View.GONE);
                return true;
            }

        });
        mButtonCamera = findViewById(R.id.camera);
        mButtonCamera.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                mPicturePicker.setVisibility(View.GONE);
                mPhotoImageView.doTakePhoto();
            }

        });
        mButtonGallery = findViewById(R.id.gallery);
        mButtonGallery.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                mPicturePicker.setVisibility(View.GONE);
                mPhotoImageView.doPickFromGallery();
            }

        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_OK) { return; }

        switch (requestCode) {
            case PhotoChooseView.PHOTO_PICKED_WITH_DATA: {
                Log.d(TAG, "data is " + data);
                final Bitmap photo = data.getParcelableExtra("data");
                mPhotoImageView.setImageBitmap(photo);
            }
            break;
            case PhotoChooseView.CAMERA_WITH_DATA: {
                mPhotoImageView.doCropPhoto();
            }
            break;
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.register:
                if (validateFields()) {
                    new RegisterTask().execute();
                }
                break;
            case R.id.avatarView:
                showPicturePicker();
                break;
            case R.id.label_female:
                maleView.setBackgroundColor(getResources().getColor(android.R.color.transparent));
                femaleView.setBackgroundColor(getResources().getColor(R.color.default_pink_color));
                gender = ConstantsHelper.GENDER_FEMALE;
                break;
            case R.id.label_male:
                maleView.setBackgroundColor(getResources().getColor(R.color.default_pink_color));
                femaleView.setBackgroundColor(getResources().getColor(android.R.color.transparent));
                gender = ConstantsHelper.GENDER_MALE;
                break;
        }
    }


    private boolean validateFields() {
        if (TextUtils.isEmpty(usernameEdit.getText().toString())) {
            Toast.makeText(this, R.string.error_username_empty, Toast.LENGTH_LONG).show();
            return false;
        }
        if (TextUtils.isEmpty(passwordEdit.getText().toString())) {
            Toast.makeText(this, R.string.error_password_empty, Toast.LENGTH_LONG).show();
        }
        if (TextUtils.isEmpty(confirmEdit.getText().toString()) || !passwordEdit.getText().toString()
                .contentEquals(confirmEdit.getText().toString())) {
            Toast.makeText(this, R.string.error_confirm_error, Toast.LENGTH_LONG).show();
        }
        return true;
    }

    private String errorMsg;

    @Override
    public void onDismiss() {
        if (!TextUtils.isEmpty(errorMsg)) {
            Toast.makeText(RegisterActivity.this, errorMsg, Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(RegisterActivity.this, R.string.register_success, Toast.LENGTH_SHORT).show();
            Intent loginIntent = new Intent(RegisterActivity.this,LoginActivity.class);
            startActivity(loginIntent);
        }
    }


    private class RegisterTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showProgress(RegisterActivity.this);
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            dismissProgress();
            super.onPostExecute(aVoid);
        }

        @Override
        protected Void doInBackground(Void... params) {
            Context context = RegisterActivity.this;
            String url = NetUtil.getRegisterUrl(context);
            NetUtil util = new HttpClientImplUtil(context, url);
            Map<String, String> registerMap = new HashMap<String, String>();
            registerMap.put("username", usernameEdit.getText().toString());
            String passwd = passwordEdit.getText().toString();
            String desEncryPasswd = DESEncryption.DESEncrypt(passwd);
            registerMap.put("passwd", desEncryPasswd);
            registerMap.put("gender", Integer.toString(gender));
            registerMap.put("platform", "Android");
            registerMap.put("imagename", System.currentTimeMillis() + ".png");
            //必须字段
            registerMap.put("phone", mPhoneNumber);
            util.setMap(registerMap);
            if (mPhotoImageView.getPhotoData() != null) {
                ImageUtils.saveUploadImage(mPhotoImageView.getPhotoData(), RegisterActivity.this);
                util.setFileEntitiy("image", Utils.getAvatarFile(context));
            }
            String result = util.uploadMultiPart();
            Log.d(TAG, "register result: " + result);
            try {
                JSONObject json = new JSONObject(result);
                if (ServerDataUtils.isTaskSuccess(json)) {
                    errorMsg = null;
                    String id = json.optJSONObject("info").optString("id");
                    UserInfo userInfo = new UserInfo();
                    userInfo.id = id;
                    userInfo.gender = gender;
                    userInfo.name = usernameEdit.getText().toString();
                    userInfo.phone = mPhoneNumber;
                    userInfo.passwd = passwordEdit.getText().toString();
                    SettingsUtil.saveUser(context, userInfo);
                } else {
                    errorMsg = ServerDataUtils.getErrorMessage(json);
                }
            } catch (Exception e) {
                errorMsg = e.getLocalizedMessage();
            }
            return null;
        }
    }

    public void onBack(View v){
    	if(this.mPicturePicker.getVisibility() == View.VISIBLE){
    		this.mPicturePicker.setVisibility(View.GONE);
    	}else{
    		onBackPressed();
    	}
    }
}
