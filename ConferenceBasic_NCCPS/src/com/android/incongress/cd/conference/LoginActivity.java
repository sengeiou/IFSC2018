package com.android.incongress.cd.conference;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.incongress.cd.conference.api.CHYHttpClientUsage;
import com.android.incongress.cd.conference.base.AppApplication;
import com.android.incongress.cd.conference.base.BaseActivity;
import com.android.incongress.cd.conference.base.Constants;
import com.android.incongress.cd.conference.beans.UserInfoEnBean;
import com.android.incongress.cd.conference.save.ParseUser;
import com.android.incongress.cd.conference.save.SharePreferenceUtils;
import com.android.incongress.cd.conference.services.AdService;
import com.android.incongress.cd.conference.utils.ActivityUtils;
import com.android.incongress.cd.conference.utils.JSONCatch;
import com.android.incongress.cd.conference.utils.MyLogger;
import com.android.incongress.cd.conference.utils.StringUtils;
import com.android.incongress.cd.conference.utils.ToastUtils;
import com.android.incongress.cd.conference.widget.phonecode.CountDownButton;
import com.android.incongress.cd.conference.widget.phonecode.FocusPhoneCode;
import com.google.gson.Gson;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.mobile.incongress.cd.conference.basic.csccm.R;
import com.pedaily.yc.ycdialoglib.customToast.ToastUtil;
import com.tencent.mm.sdk.modelmsg.SendAuth;
import com.umeng.socialize.UMAuthListener;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.UMShareConfig;
import com.umeng.socialize.bean.SHARE_MEDIA;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URLEncoder;
import java.util.Map;

import cz.msebera.android.httpclient.Header;

/**
 * Created by Jacky on 2016/1/29.
 * 登录页面
 */
public class LoginActivity extends BaseActivity implements View.OnClickListener {
    private EditText mEtMobile, mEtRegistCode, mEtFamilyName, mEtGivenName, mEtName,mEtConfirmCode;
    private Button mBtLogin, mBtCode;
    private TextView tv_title, tv_number_code,mTvGoRegister;
    //private CheckBox mCbRegist;
    private ImageView backimg, we_login;
    private String mUserSMS, mUserMobile, mFamilyName, mGivenName;
    private String mCountryCode = "";
    private View view_background;
    private LinearLayout ll_getcode, ll_login, ll_bottom_wx;
    private CountDownButton countDownTextView;

    private int mCurrentType = 1;
    private ProgressDialog mProgressDialog;
    FocusPhoneCode mFpc;
    private String nickName, imgUrl, sex;
    private static int LOGIN_CODE_RESULT = 1000;
    private static int LOGIN_UPDATA_CODE_RESULT = 1001;

    //必须输入注册码类型，不能取消勾选框
    public static final String LOGIN_TYPE = "loginType";
    public static final String LOGIN_USERNAME = "loginUserName";
    public static final String LOGIN_USERMOBILE = "loginUserMobile";
    public static final String LOGIN_FAMILY_NAME = "familyName";
    public static final String LOGIN_GIVEN_NAME = "givenName";

    public static final int TYPE_NORMAL = 1;
    public static final int TYPE_SECRETARY = 2;
    public static final int TYPE_PROFESSOR = 3;
    public static final int REQUEST_COUNTRY_CODE = 4;

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */

    public static final void startLoginActivity(Context ctx, int type, String userName, String mobilePhone, String familyName, String givenName) {
        Intent intent = new Intent();
        intent.setClass(ctx, LoginActivity.class);
        intent.putExtra(LOGIN_TYPE, type);
        intent.putExtra(LOGIN_USERNAME, userName);
        intent.putExtra(LOGIN_USERMOBILE, mobilePhone);
        intent.putExtra(LOGIN_FAMILY_NAME, familyName);
        intent.putExtra(LOGIN_GIVEN_NAME, givenName);
        ctx.startActivity(intent);
    }

    @Override
    protected void setContentView() {
        setContentView(R.layout.activity_getcode);
    }

    @Override
    protected void initViewsAction() {
        try {
            mCurrentType = getIntent().getIntExtra(LOGIN_TYPE, TYPE_NORMAL);
            mUserMobile = getIntent().getStringExtra(LOGIN_USERMOBILE);
            mFamilyName = getIntent().getStringExtra(LOGIN_FAMILY_NAME);
            mGivenName = getIntent().getStringExtra(LOGIN_GIVEN_NAME);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if(AppApplication.systemLanguage == 1){
            initChinese();
        }else {
            initEnglish();
        }
    }

    private void initChinese() {
        //公共部分
        mEtMobile = findViewById(R.id.et_mobile);
        //StringUtils.setViewTextSize(mEtMobile,getResources().getString(R.string.telephone_number));
        //mEtRegistCode = (EditText) findViewById(R.id.et_regist_code);
        mBtLogin = findViewById(R.id.bt_login);
        mBtLogin.setOnClickListener(this);
        mBtCode = findViewById(R.id.bt_getcode);
        mBtLogin.setEnabled(false);
        mBtLogin.setClickable(false);
        mBtCode.setEnabled(false);
        mBtCode.setClickable(false);
        mBtCode.setOnClickListener(this);
        //mCbRegist = (CheckBox) findViewById(R.id.cb_regist);
        backimg = findViewById(R.id.iv_back);
        backimg.setOnClickListener(this);
        tv_title = findViewById(R.id.tv_title);
        tv_title.setText(getString(R.string.incongress_login));
        mBtCode.getBackground().setAlpha(204);
        mBtLogin.getBackground().setAlpha(204);
        view_background = findViewById(R.id.view_background);
        ll_getcode = findViewById(R.id.ll_getcode);
        ll_login = findViewById(R.id.ll_login);
        ll_bottom_wx = findViewById(R.id.ll_bottom_wx);
        tv_number_code = findViewById(R.id.tv_number_code);
        we_login = findViewById(R.id.we_login);
        we_login.setOnClickListener(this);
        mFpc = findViewById(R.id.fpc);
        countDownTextView = findViewById(R.id.countDownTextView);
        countDownTextView.setOnClickListener(this);
        mCountryCode = "86";
        if (!StringUtils.isEmpty(mUserMobile)) {
            mEtMobile.setText(mUserMobile);
        }

        mEtMobile.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                int length = s.toString().length();
                //删除数字
                if (count == 0) {
                    if (length == 4) {
                        mEtMobile.setText(s.subSequence(0, 3));
                    }
                    if (length == 9) {
                        mEtMobile.setText(s.subSequence(0, 8));
                    }
                }
                //添加数字
                if (count == 1) {
                    if (length == 4) {
                        String part1 = s.subSequence(0, 3).toString();
                        String part2 = s.subSequence(3, length).toString();
                        mEtMobile.setText(part1 + " " + part2);
                    }
                    if (length == 9) {
                        String part1 = s.subSequence(0, 8).toString();
                        String part2 = s.subSequence(8, length).toString();
                        mEtMobile.setText(part1 + " " + part2);
                    }
                }
                if (s.toString().length() == 13) {
                    mBtCode.setEnabled(true);
                    mBtCode.setClickable(true);
                    mBtCode.getBackground().setAlpha(255);
                    view_background.setBackgroundColor(getResources().getColor(R.color.theme_color));
                } else if (!TextUtils.isEmpty(s.toString())) {
                    mBtCode.setEnabled(false);
                    mBtCode.setClickable(false);
                    mBtCode.getBackground().setAlpha(204);
                    view_background.setBackgroundColor(getResources().getColor(R.color.theme_color));
                } else {
                    mBtCode.setEnabled(false);
                    mBtCode.setClickable(false);
                    mBtCode.getBackground().setAlpha(204);
                    view_background.setBackgroundColor(getResources().getColor(R.color.login_gray));
                }

            }

            @Override
            public void afterTextChanged(Editable s) {
                //将光标移动到末尾
                mEtMobile.setSelection(mEtMobile.getText().toString().length());
            }
        });



        /*mEtRegistCode.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    mEtRegistCode.setHintTextColor(getResources().getColor(R.color.gray));
                } else {
                    mEtRegistCode.setHintTextColor(getResources().getColor(R.color.white));
                }
            }
        });*/

        //以下代码不用验证了，有些会议可能单独需要
        if (mCurrentType == TYPE_SECRETARY) {
            //mCbRegist.setVisibility(View.GONE);
        }


        mFpc.setFinishListener(new FocusPhoneCode.FinishListener() {
            @Override
            public void isFinish(boolean isFinish) {
                if (isFinish) {
                    mBtLogin.setEnabled(true);
                    mBtLogin.setClickable(true);
                    mBtLogin.getBackground().setAlpha(255);
                    mUserSMS = mFpc.getPhoneCode();
                    mBtLogin.performClick();
                } else {
                    mBtLogin.setEnabled(false);
                    mBtLogin.setClickable(false);
                    mBtLogin.getBackground().setAlpha(204);
                }
            }
        });

    }
    private void initEnglish() {
        mEtMobile = (EditText) findViewById(R.id.et_mobile);
        mEtConfirmCode = (EditText) findViewById(R.id.et_confirm);
        mEtRegistCode = (EditText) findViewById(R.id.et_regist_code);
        mBtLogin = (Button) findViewById(R.id.bt_login);
        mTvGoRegister = (TextView) findViewById(R.id.tv_go_regist);
        backimg = (ImageView) findViewById(R.id.iv_back);
        mCountryCode = "86";

        backimg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        //公共部分
        mEtMobile.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    mEtMobile.setHintTextColor(getResources().getColor(R.color.gray));
                } else {
                    mEtMobile.setHintTextColor(getResources().getColor(R.color.white));
                }
            }
        });

        mEtConfirmCode.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    mEtConfirmCode.setHintTextColor(getResources().getColor(R.color.gray));
                } else {
                    mEtConfirmCode.setHintTextColor(getResources().getColor(R.color.white));
                }
            }
        });


        mEtRegistCode.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    mEtRegistCode.setHintTextColor(getResources().getColor(R.color.gray));
                } else {
                    mEtRegistCode.setHintTextColor(getResources().getColor(R.color.white));
                }
            }
        });

        //以下代码不用验证了，有些会议可能单独需要
        if (mCurrentType == TYPE_SECRETARY) {
            //mCbRegist.setVisibility(View.GONE);
            mTvGoRegister.setVisibility(View.GONE);
        }

        mTvGoRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RegisterActivity.startActivity(LoginActivity.this);
            }
        });

        mBtLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mobile = mEtMobile.getText().toString();
                String confirmCode = mEtConfirmCode.getText().toString();
                String registCode = mEtRegistCode.getText().toString();

                    String familyName = mEtFamilyName.getText().toString();
                    String givenName = mEtGivenName.getText().toString();
                    if (!StringUtils.isAllNotEmpty( mobile, confirmCode)) {
                        ToastUtils.showShorToast(getString(R.string.login_info_empty));
                    } else {
//                      doLogin(givenName, mobile, confirmCode, Constants.LanguageEnglish);
                        doLoginbyEmail(mobile, confirmCode, AppApplication.getSystemLanuageCode());
                    }
                }
        });
        mEtFamilyName = (EditText) findViewById(R.id.et_family_name);
        mEtGivenName = (EditText) findViewById(R.id.et_given_name);
        //mTvCountryCode = (TextView) findViewById(R.id.tv_country_code);

        mEtGivenName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    mEtGivenName.setHintTextColor(getResources().getColor(R.color.gray));
                } else {
                    mEtGivenName.setHintTextColor(getResources().getColor(R.color.white));
                }
            }
        });

        mEtGivenName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    mEtGivenName.setHintTextColor(getResources().getColor(R.color.gray));
                } else {
                    mEtGivenName.setHintTextColor(getResources().getColor(R.color.white));
                }
            }
        });

        if (!StringUtils.isEmpty(mFamilyName)) {
            mEtFamilyName.setText(mFamilyName);
        }

        if (!StringUtils.isEmpty(mGivenName)) {
            mEtGivenName.setText(mGivenName);
        }
        if (!StringUtils.isEmpty(mUserMobile)) {
            mEtMobile.setText(mUserMobile);
        }
    }

    //登录成功标识符
    public static final String LOGIN_ACTION = "login";
    //退出登录标识符
    public static final String LOGOUT_ACTION = "logout";

    private void doLogin___(final String mobile, final String sms, String lan) {
        CHYHttpClientUsage.getInstanse().doLoginByCode(mobile.replaceAll(" ", ""), sms, lan, Constants.getFromWhere(), new JsonHttpResponseHandler(Constants.ENCODING_GBK) {
            public void onStart() {
                super.onStart();
                mProgressDialog = ProgressDialog.show(LoginActivity.this, null, "loading...");
            }

            @Override
            public void onFinish() {
                super.onFinish();
                mProgressDialog.dismiss();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                ToastUtils.showShorToast("服务器开小差了，请稍后重试");
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                try {
                    if (JSONCatch.parseInt("state", response) == 1) {
                        if (TextUtils.isEmpty(JSONCatch.parseString("name", response)) && TextUtils.isEmpty(JSONCatch.parseString("img", response))) {
                            Intent intent = new Intent(LoginActivity.this, LoginForUpdateInfoActivity.class);
                            Bundle bundle = new Bundle();
                            bundle.putString(Constants.USER_MOBILE, mobile.replaceAll(" ", ""));
                            bundle.putString("sms", sms);
                            bundle.putString(Constants.USER_ID, JSONCatch.parseString(Constants.USER_ID, response));
                            bundle.putString(Constants.USER_TYPE, JSONCatch.parseString(Constants.USER_TYPE, response));
                            intent.putExtras(bundle);
                            mFpc.clearData();
                            startActivityForResult(intent, LOGIN_UPDATA_CODE_RESULT);
                            return;
                        }
                        ParseUser.saveUserInfo(response.toString());
                        SharePreferenceUtils.saveUserBoolean(Constants.USER_IS_LOGIN, true);

//                        setResult(RESULT_OK);
                        //发送广播
                        Intent loginIntent = new Intent();
                        loginIntent.setAction(LOGIN_ACTION);
                        sendBroadcast(loginIntent);
                        finish();
                    } else {
                        ToastUtils.showShorToast(response.getString("msg"));
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void doGetSms(String mobile, String lan) {
        CHYHttpClientUsage.getInstanse().doGetSmsMobile(Constants.getConId(), mobile.replaceAll(" ", ""), Constants.ConfirmTypeLogin, lan, new JsonHttpResponseHandler(Constants.ENCODING_GBK) {
            @Override
            public void onStart() {
                super.onStart();
                mProgressDialog = ProgressDialog.show(LoginActivity.this, null, "loading...");
            }

            /*
             返回内容{"state":1,"code":"156114","msg":""}
             */
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);

                MyLogger.jLog().i(response.toString());
                String msg = JSONCatch.parseString("msg", response);
                if (JSONCatch.parseInt("state", response) == 0) {
                    showDialog(msg, null);
                } else {
                    ToastUtils.showShorToast(getString(R.string.success_send_regist_code));
                }
            }

            @Override
            public void onFinish() {
                super.onFinish();
                mProgressDialog.dismiss();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                ToastUtils.showShorToast(getString(R.string.server_error));
            }
        });
    }

    private void showDialog(String msg, DialogInterface.OnClickListener okListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.dialog_tips)).setMessage(msg).setPositiveButton(R.string.positive_button, okListener).setCancelable(false).show();
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if (isShouldHideInput(v, ev)) {

                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
            return super.dispatchTouchEvent(ev);
        }
        // 必不可少，否则所有的组件都不会有TouchEvent了
        if (getWindow().superDispatchTouchEvent(ev)) {
            return true;
        }
        return onTouchEvent(ev);
    }

    public boolean isShouldHideInput(View v, MotionEvent event) {
        if (v != null && (v instanceof EditText)) {
            int[] leftTop = {0, 0};
            //获取输入框当前的location位置
            v.getLocationInWindow(leftTop);
            int left = leftTop[0];
            int top = leftTop[1];
            int bottom = top + v.getHeight();
            int right = left + v.getWidth();
            if (event.getX() > left && event.getX() < right
                    && event.getY() > top && event.getY() < bottom) {
                return false;
            } else {
                return true;
            }
        }
        return false;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        UMShareAPI.get(this).onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_COUNTRY_CODE) {
                String code = data.getStringExtra("code");
                //mTvCountryCode.setText("(+" + code + ")");
                mCountryCode = code;
            } else if (requestCode == LOGIN_CODE_RESULT || requestCode == LOGIN_UPDATA_CODE_RESULT) {
                this.finish();
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_login:
                if (!TextUtils.isEmpty(mUserMobile) && !TextUtils.isEmpty(mUserSMS)) {
                    doLogin___(mUserMobile, mUserSMS, AppApplication.getSystemLanuageCode());
                }
                break;
            case R.id.countDownTextView:
                break;
            case R.id.we_login:
                if (ActivityUtils.isWxInstall(getApplicationContext())) {
                    authorization(SHARE_MEDIA.WEIXIN);
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle(R.string.dialog_tips).setMessage("检测到您手机没有安装微信，请安装后使用该功能").setPositiveButton("去下载", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(Intent.ACTION_VIEW);
                            intent.setData(Uri.parse("https://weixin.qq.com/m"));
                            startActivity(intent);
                        }
                    }).setNegativeButton(R.string.negative_button, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    }).show();
                    break;
                    //直接跳转
                    //ToastUtils.showShorToast("检测到您手机没有安装微信，请安装后使用该功能");

                }

                break;
            case R.id.iv_back:
                if (ll_getcode.getVisibility() == View.VISIBLE) {
                    finish();
                } else {
                    ll_getcode.setVisibility(View.VISIBLE);
                    ll_bottom_wx.setVisibility(View.VISIBLE);
                    we_login.setVisibility(View.VISIBLE);
                    ll_login.setVisibility(View.GONE);
                    mFpc.clearData();
                }

                break;
            case R.id.bt_getcode:
                mUserMobile = mEtMobile.getText().toString().trim();
                if (TextUtils.isEmpty(mUserMobile)) {
                    ToastUtils.showToast("请先输入手机号");
                    return;
                }
                ll_getcode.setVisibility(View.GONE);
                ll_login.setVisibility(View.VISIBLE);
                ll_bottom_wx.setVisibility(View.GONE);
                we_login.setVisibility(View.GONE);
                countDownTextView.setCountDownMillis(30000);
                countDownTextView.start();
                tv_number_code.setText("验证码已发送   " + mUserMobile);
                doGetSms(mUserMobile, Constants.LanguageChinese);
                break;

        }
    }

    //微信登录授权
    private void authorization(SHARE_MEDIA share_media) {
        UMShareAPI.get(AppApplication.getContext()).getPlatformInfo(LoginActivity.this, share_media, new UMAuthListener() {
            @Override
            public void onStart(SHARE_MEDIA share_media) {
                Log.d("myTest", "onStart " + "授权开始");
            }

            @Override
            public void onComplete(SHARE_MEDIA share_media, int i, Map<String, String> map) {
                Log.d("myTest", "onComplete: " + "授权完成");

                //sdk是6.4.4的,但是获取值的时候用的是6.2以前的(access_token)才能获取到值,未知原因
                String uid = map.get("uid");
                String openid = map.get("openid");//微博没有
                String unionid = map.get("unionid");//微博没有
                String access_token = map.get("access_token");
                String refresh_token = map.get("refresh_token");//微信,qq,微博都没有获取到
                String expires_in = map.get("expires_in");
                nickName = map.get("name");
                sex = map.get("gender");
                imgUrl = map.get("iconurl");
                loginWX(openid);
            }

            @Override
            public void onError(SHARE_MEDIA share_media, int i, Throwable throwable) {
                Log.d("myTest", "onError: " + "授权失败");
            }

            @Override
            public void onCancel(SHARE_MEDIA share_media, int i) {
                Log.d("myTest", "onCancel: " + "授权取消");
            }
        });
    }

    private void loginWX(String openId) {
        CHYHttpClientUsage.getInstanse().doLoginWX(openId, new JsonHttpResponseHandler(Constants.ENCODING_GBK) {
            @Override
            public void onStart() {
                super.onStart();
                mProgressDialog = ProgressDialog.show(LoginActivity.this, null, "loading...");
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                if ("1".equals(JSONCatch.parseString("state", response))) {
                    if (TextUtils.isEmpty(JSONCatch.parseString("mobilePhone", response)) && !TextUtils.isEmpty(JSONCatch.parseString("openId", response))) {
                        Intent intent = new Intent(LoginActivity.this, WxForLoginActivity.class);
                        intent.putExtra(Constants.USER_OPENID, JSONCatch.parseString(Constants.USER_OPENID, response));
                        intent.putExtra(Constants.USER_NICK_NAME, nickName);
                        intent.putExtra(Constants.USER_IMG, imgUrl);
                        intent.putExtra(Constants.USER_SEX, sex);
                        startActivityForResult(intent, LOGIN_CODE_RESULT);
                    } else {
                        ParseUser.saveUserInfo(response.toString());
                        SharePreferenceUtils.saveUserBoolean(Constants.USER_IS_LOGIN, true);

                        //发送广播
                        Intent loginIntent = new Intent();
                        loginIntent.setAction(LOGIN_ACTION);
                        sendBroadcast(loginIntent);
                        finish();
                    }
                }
            }

            @Override
            public void onFinish() {
                super.onFinish();
                mProgressDialog.dismiss();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                ToastUtils.showShorToast(getString(R.string.server_error));
            }
        });
    }

    //对英文进行处理
    private void doLoginbyEmail(final String email, final String psw, String lan){
        CHYHttpClientUsage.getInstanse().doEmailLoginV1(email,psw,lan,new JsonHttpResponseHandler(Constants.ENCODING_GBK) {
            public void onStart() {
                super.onStart();
                mProgressDialog = ProgressDialog.show(LoginActivity.this, null, "loading...");
            }

            @Override
            public void onFinish() {
                super.onFinish();
                mProgressDialog.dismiss();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                ToastUtils.showShorToast("服务器开小差了，请稍后重试");
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);

                MyLogger.jLog().i(response.toString());
                try {
                    int state = response.getInt("state");
                    if (state == 1) {
                        ParseUser.saveUserInfo(response.toString());
                        SharePreferenceUtils.saveUserBoolean(Constants.USER_IS_LOGIN, true);
                        /*UserInfoEnBean user = gson.fromJson(response.toString(), UserInfoEnBean.class);

                        AppApplication.setSPBooleanValue(Constants.USER_IS_LOGIN, true);
                        AppApplication.setSPStringValue(Constants.USER_NAME, user.getName());
                        AppApplication.setSPStringValue(Constants.USER_IMG, user.getImg());
                        AppApplication.setSPStringValue(Constants.USER_MOBILE, user.getEmail());
                        AppApplication.setSPIntegerValue(Constants.USER_ID, user.getUserId());
                        AppApplication.setSPIntegerValue(Constants.USER_TYPE, user.getUserType());

                        AppApplication.setSPBooleanValue(Constants.USER_IS_LOGIN, true);
                        AppApplication.userId = user.getUserId();
                        AppApplication.username = user.getName();
                        AppApplication.userType = user.getUserType();*/

                        //发送广播
                        Intent loginIntent = new Intent();
                        loginIntent.setAction(LOGIN_ACTION);
                        sendBroadcast(loginIntent);
                        Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                        startActivity(intent);
                        finish();

                    } else {
                        ToastUtils.showShorToast(response.getString("msg"));
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

}
