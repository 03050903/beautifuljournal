package com.maple.beautyjournal.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.FileObserver;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.i2mobi.utils.ImageUtils;
import com.i2mobi.widget.PhotoChooseView;
import com.maple.beautyjournal.AboutActivity;
import com.maple.beautyjournal.ChangePasswordActivity;
import com.maple.beautyjournal.FeedbackActivity;
import com.maple.beautyjournal.LoginActivity;
import com.maple.beautyjournal.OrderListActivity;
import com.maple.beautyjournal.R;
import com.maple.beautyjournal.accessor.AccessorResultWrapper;
import com.maple.beautyjournal.accessor.UserAccessor;
import com.maple.beautyjournal.base.BaseFragment;
import com.maple.beautyjournal.base.BaseFragmentActivity;
import com.maple.beautyjournal.base.OnBackPressedListener;
import com.maple.beautyjournal.entitiy.UserInfo;
import com.maple.beautyjournal.utils.ConstantsHelper;
import com.maple.beautyjournal.utils.SettingsUtil;
import com.maple.beautyjournal.utils.Utils;

import java.io.File;

/**
 * Created by mosl on 14-4-30.
 */
public class PersonCenterFragment extends BaseFragment implements View.OnClickListener  {
    private TextView nicknameLabel, genderLabel, phoneNumber;
    private PhotoChooseView avatarView;
    private Button logOutButton;
    private Context context;
    private Drawable avatar;
    private static final int REQUEST_LOG_IN = 110, REQUEST_SEND_FEEDBACK = 111;
    private static final int REQUEST_ORDER_LIST=112;

    private UserInfo mUser;
    private View userInfo;
    View mPicturePicker;
    View mButtonCamera;
    View mButtonGallery;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.activity_settings, container, false);
        context = getActivity();
        initViews(v);
        initObservers();
        initPicturePicker(v);
        return v;
    }

    private void initPicturePicker(View v) {
        mPicturePicker = v.findViewById(R.id.picture_picker);
        mPicturePicker.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View arg0, MotionEvent arg1) {
                mPicturePicker.setVisibility(View.GONE);
                setOnBackPressedListener(null);
                return true;
            }

        });
        mButtonCamera = v.findViewById(R.id.camera);
        mButtonCamera.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                mPicturePicker.setVisibility(View.GONE);
                setOnBackPressedListener(null);
                avatarView.doTakePhoto();
            }

        });
        mButtonGallery = v.findViewById(R.id.gallery);
        mButtonGallery.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                mPicturePicker.setVisibility(View.GONE);
                setOnBackPressedListener(null);
                try {
                    avatarView.doPickFromGallery();
                } catch (Exception e) {
                    Toast.makeText(getActivity(), getString(R.string.cannot_take_photo), Toast.LENGTH_LONG).show();
                }
            }

        });
    }

    FileObserver fileObserver = null;
    SharedPreferences.OnSharedPreferenceChangeListener listener = null;

    private void initObservers() {
        fileObserver = new FileObserver(Utils.getAvatarFile(context).getAbsolutePath()) {
            @Override
            public void onEvent(int event, String path) {
                if (event == FileObserver.CLOSE_WRITE || event == FileObserver.DELETE || event == FileObserver
                        .DELETE_SELF) {
                    refreshHandler.sendEmptyMessage(0);
                }
            }
        };
        fileObserver.startWatching();
        listener = new SharedPreferences.OnSharedPreferenceChangeListener() {

            @Override
            public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
                if (key.contentEquals(SettingsUtil.ADDRESS) || key.contentEquals(SettingsUtil.GENDER) || key
                        .contentEquals(SettingsUtil.ADDRESS)) {
                    refreshHandler.sendEmptyMessage(0);
                }
            }
        };
        SharedPreferences pref = context.getSharedPreferences(SettingsUtil.PREF_NAME, Context.MODE_PRIVATE);
        pref.registerOnSharedPreferenceChangeListener(listener);
    }

    private void showPicturePicker() {
        mPicturePicker.setVisibility(View.VISIBLE);

        OnBackPressedListener listener =  new OnBackPressedListener(){
            @Override
            public void doBack() {
                mPicturePicker.setVisibility(View.GONE);
            }};
        this.setOnBackPressedListener(listener);
    }

    private void setOnBackPressedListener(OnBackPressedListener listener){
        if(getActivity() instanceof BaseFragmentActivity){
            BaseFragmentActivity fragmentActivity = (BaseFragmentActivity) this.getActivity();
            fragmentActivity.setOnBackPressedListener(listener);
        }
    }

    private Handler refreshHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            refreshViews();
        }
    };

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.e(TAG, "onActivityResult, " + requestCode + ", " + resultCode + ", " + data);
        Log.d(TAG, "data is " + data);
        if (data != null && data.getExtras() != null) {
            for (String key : data.getExtras().keySet()) {
                Log.d(TAG, "extra: " + key);
            }
        }
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case PhotoChooseView.PHOTO_PICKED_WITH_DATA: {
                    final Bitmap photo = data.getParcelableExtra("data");
                    avatarView.setImageBitmap(photo);
                }
                ImageUtils.saveUploadImage(avatarView.getPhotoData(), getActivity());
                new UpdateInfoTask().execute();
                break;
                case PhotoChooseView.CAMERA_WITH_DATA: {
                    avatarView.doCropPhoto();
                }
                break;
                case REQUEST_SEND_FEEDBACK: {
                    Intent intent = new Intent(context, FeedbackActivity.class);
                    context.startActivity(intent);
                }
                break;
                case REQUEST_ORDER_LIST: {
                    Intent intent = new Intent(context, OrderListActivity.class);
                    context.startActivity(intent);
                }
                break;
            }
            Log.e(TAG, "refresh");
            refreshHandler.sendEmptyMessage(0);
        }else{
            Log.e(TAG, "not refreshed");
        }
    }

    private void refreshViews() {
        if (SettingsUtil.isLoggedIn(context)) {
            mUser = SettingsUtil.getUser(context);
            userInfo.setVisibility(View.VISIBLE);
            nicknameLabel.setText(SettingsUtil.getUserName(context));
            genderLabel.setText(mUser.gender == ConstantsHelper.GENDER_FEMALE ? R.string.gender_female : R
                    .string.gender_male);
            this.phoneNumber.setText(mUser.phone);
            File f = Utils.getAvatarFile(context);
            if (f.exists()) {
                avatarView.setImageDrawable(Utils.getAvatarDrawableFromFile(context));
            } else {
                avatarView.setImageResource((mUser.gender == ConstantsHelper.GENDER_FEMALE) ?
                        R.drawable.female_default : R.drawable.male_default);
            }
            logOutButton.setText(R.string.log_out);
        } else {
            userInfo.setVisibility(View.GONE);
            logOutButton.setText(R.string.login_title);
        }

    }

    private int currentGenderPosition = -1;

    private void initViews(View v) {

        View address = v.findViewById(R.id.address_manager);
        address.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                FragmentManager fm = PersonCenterFragment.this.getActivity().getSupportFragmentManager();
                FragmentTransaction ft = fm.beginTransaction();

                Fragment address_manager = Fragment
                        .instantiate(PersonCenterFragment.this.getActivity(), AddressListFragment.class.getName(), null);

                ft.replace(R.id.content, address_manager);
                ft.addToBackStack(null);
                ft.commit();
                PersonCenterFragment.this.getActivity().getSupportFragmentManager().executePendingTransactions();

            }
        });

        View genderView = v.findViewById(R.id.gender_view);
        genderView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(getActivity()).setSingleChoiceItems(getResources()
                                .getStringArray(R.array.gender),
                        currentGenderPosition,
                        new DialogInterface.OnClickListener() {


                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                currentGenderPosition = which;
                                genderLabel.setText(currentGenderPosition == 0 ? R.string.gender_female : R.string.gender_male);
                                dialog.dismiss();
                                new UpdateInfoTask().execute();
                            }
                        }).setNegativeButton(getString(android.R.string.cancel), null).show();
            }
        });

        View changePasswdView = v.findViewById(R.id.changepasswd_view);
        changePasswdView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if(context instanceof BaseFragmentActivity){
                    Intent intent = new Intent(context, ChangePasswordActivity.class);
                    context.startActivity(intent);

                }else{
                    //impossible
                    Log.e(TAG, "change passwd onClick impossible");
                }
            }
        });

        View avatarLayout = v.findViewById(R.id.avatar_layout);
        avatarLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPicturePicker();
            }
        });

        View feedback = v.findViewById(R.id.feedback);
        feedback.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                if (SettingsUtil.isLoggedIn(context)) {
                    Intent intent = new Intent(context, FeedbackActivity.class);
                    context.startActivity(intent);
                } else {
                    Intent intent = new Intent(context, LoginActivity.class);
                    startActivityForResult(intent, REQUEST_SEND_FEEDBACK);
                }
            }

        });

        View about = v.findViewById(R.id.about);
        about.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {

                Intent intent = new Intent(context, AboutActivity.class);
                context.startActivity(intent);
            }

        });

        View order = v.findViewById(R.id.order);
        order.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                if (SettingsUtil.isLoggedIn(getActivity())) {
                    Intent intent = new Intent(context, OrderListActivity.class);
                    context.startActivity(intent);
                    return;
                }else{
                    Intent intent = new Intent(context, LoginActivity.class);
                    startActivityForResult(intent, REQUEST_ORDER_LIST);
                }
            }
        });

        mUser = SettingsUtil.getUser(getActivity());
        nicknameLabel = (TextView) v.findViewById(R.id.nickname_label);
        nicknameLabel.setText(mUser.name);

        phoneNumber = (TextView) v.findViewById(R.id.phoneNumber);
        phoneNumber.setText(mUser.phone);

        genderLabel = (TextView) v.findViewById(R.id.gender_label);
        int gender = mUser.gender;
        genderLabel.setText((gender == ConstantsHelper.GENDER_FEMALE) ? R.string.gender_female : R.string.gender_male);
        currentGenderPosition = (gender == ConstantsHelper.GENDER_FEMALE) ? 0 : 1;
        avatarView = (PhotoChooseView) v.findViewById(R.id.image);
        avatarView.initFragmentContext(this);
        avatarView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPicturePicker();
            }
        });
        logOutButton = (Button) v.findViewById(R.id.logout_button);
        logOutButton.setOnClickListener(this);
        userInfo = v.findViewById(R.id.user_info_group);
        if (SettingsUtil.isLoggedIn(context)) {
            userInfo.setVisibility(View.VISIBLE);
            logOutButton.setText(R.string.log_out);
        } else {
            userInfo.setVisibility(View.GONE);
            logOutButton.setText(R.string.login_title);
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                avatar = Utils.getAvatarDrawableFromFile(context);
                if (avatar == null) {
                    avatar = (mUser.gender == ConstantsHelper.GENDER_FEMALE) ? (context.getResources()
                            .getDrawable(R.drawable.female_default)) : (context.getResources()
                            .getDrawable(R.drawable.male_default));
                }
                handler.sendEmptyMessage(MESSAGE_UPDATE_AVATAR);
            }
        }).start();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.logout_button:
                if (SettingsUtil.isLoggedIn(context)) {
                    logOut();
                } else {
                    Intent intent = new Intent(context, LoginActivity.class);
                    startActivityForResult(intent, REQUEST_LOG_IN);
                }
                return;
        }
    }

    private void logOut() {
        Log.d(TAG, "logOut");
        SettingsUtil.logOut(context);
        refreshViews();
    }

    private static final int MESSAGE_UPDATE_NICKNAME = 0;
    private static final int MESSAGE_UPDATE_GENDER = 1;
    private static final int MESSAGE_UPDATE_AVATAR = 2;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message message) {
            switch (message.what) {
                case MESSAGE_UPDATE_NICKNAME:
                    break;
                case MESSAGE_UPDATE_GENDER:
                    break;
                case MESSAGE_UPDATE_AVATAR:
                    avatarView.setImageDrawable(avatar);
                    break;
            }
        }
    };

    @Override
    public void onDestroy() {
        if (fileObserver != null) {
            fileObserver.stopWatching();
        }
        if (listener != null) {
            getActivity().getSharedPreferences(SettingsUtil.PREF_NAME, Context.MODE_PRIVATE)
                    .unregisterOnSharedPreferenceChangeListener(listener);
        }
        super.onDestroy();
    }

    private String errorMsg;

    private class UpdateInfoTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPostExecute(Void aVoid) {
            dismissProgress();
            if (!TextUtils.isEmpty(errorMsg)) {
                Toast.makeText(getActivity(), errorMsg, Toast.LENGTH_LONG).show();
                errorMsg = null;
            } else {
                Toast.makeText(context, getString(R.string.update_info_success), Toast.LENGTH_SHORT).show();
            }

            super.onPostExecute(aVoid);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showProgress();
        }

        @Override
        protected Void doInBackground(Void... params) {

            UserInfo newUser = SettingsUtil.getUser(context);
            newUser.gender =currentGenderPosition == 0?ConstantsHelper.GENDER_FEMALE: ConstantsHelper.GENDER_MALE;
            AccessorResultWrapper result = UserAccessor.doUpdateUser(context, newUser);

            if (result.isSuccess) {
                SettingsUtil.saveUser(context, newUser);
                mUser = newUser;
            } else {
                errorMsg = result.errorMsg;
            }
            return null;
        }
    }

    @Override
    public void onResume(){
        super.onResume();
        Log.e(TAG, "onResume");
        this.refreshViews();
    }
}