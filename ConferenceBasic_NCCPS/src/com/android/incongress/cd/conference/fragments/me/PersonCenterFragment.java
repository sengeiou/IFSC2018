package com.android.incongress.cd.conference.fragments.me;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.incongress.cd.conference.CollegeActivity;
import com.android.incongress.cd.conference.HomeActivity;
import com.android.incongress.cd.conference.LoginForUpdateInfoActivity;
import com.android.incongress.cd.conference.api.CHYHttpClientUsage;
import com.android.incongress.cd.conference.base.AppApplication;
import com.android.incongress.cd.conference.base.BaseFragment;
import com.android.incongress.cd.conference.base.Constants;
import com.android.incongress.cd.conference.fragments.message_station.MessageStationActionFragment;
import com.android.incongress.cd.conference.fragments.professor_secretary.ReceiveProfessorQuestionActionFragment;
import com.android.incongress.cd.conference.fragments.question.MyQuestionSquarFragment;
import com.android.incongress.cd.conference.model.ConferenceDbUtils;
import com.android.incongress.cd.conference.model.Note;
import com.android.incongress.cd.conference.save.SharePreferenceUtils;
import com.android.incongress.cd.conference.ui.login.view.LoginActivity;
import com.android.incongress.cd.conference.utils.JSONCatch;
import com.android.incongress.cd.conference.utils.MyLogger;
import com.android.incongress.cd.conference.utils.PicUtils;
import com.android.incongress.cd.conference.utils.ShareUtils;
import com.android.incongress.cd.conference.utils.ToastUtils;
import com.android.incongress.cd.conference.utils.mark_star.AppUtils;
import com.android.incongress.cd.conference.utils.mark_star.GoToScoreUtils;
import com.android.incongress.cd.conference.utils.transformer.CircleTransform;
import com.android.incongress.cd.conference.widget.CircleImageView;
import com.android.incongress.cd.conference.widget.IconChoosePopupWindow;
import com.android.incongress.cd.conference.widget.NoScrollGridView;
import com.android.incongress.cd.conference.widget.StatusBarUtil;
import com.android.incongress.cd.conference.widget.zxing.activity.QRCodeCaptureActivity;
import com.bumptech.glide.Glide;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.mobile.incongress.cd.conference.basic.csccm.R;
import com.pedaily.yc.ycdialoglib.selectDialog.CustomSelectDialog;
import com.umeng.analytics.MobclickAgent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;
import q.rorbin.badgeview.Badge;
import q.rorbin.badgeview.QBadgeView;

/**
 * Created by Jacky on 2016/1/28.
 * 模块：我
 * Jacky Chen
 */
public class PersonCenterFragment extends BaseFragment implements View.OnClickListener {

    public static final int REQUEST_LOGIN = 0x0001;
    private RelativeLayout mMeetingAlertPanel, mContackPanel, mSharePanel, mHelpPanel, mRlMyField, mRlMyKeshi, mRlSettingsCache, mSettingsMark, mSettings, mSettingMessage;
    private TextView tv_modify_info, tv_message;
    private TextView username, welcomeInfo;
    private ImageView iv_scan;
    private int mNoteCount, mTieZiCount;
    private CircleImageView mCivHeadIcon;
    private NoScrollGridView mGridView;
    private GridListAdapter gridListAdapter;
    //中间图片和文字
    private Drawable[] imgList;
    private String[] strList;
    //拖动气泡
    private Badge messageBadge;

    private static final int HANDLE_TIEZI_COUNT = 0x0001;
    private static final int HANDLE_NOTE_COUNT = 0x0002;
    //参数为了在切换到activity返回后，fragment重新设置导航栏字体颜色
    private boolean isBackView = true;

    /**
     * 页面是否处于打开状态
     **/
    private boolean mIsOpen = true;
    private int messageNumber, questionNumber;

    private final int REQUEST_CODE_CAMERA = 1000;
    private final int REQUEST_CODE_GALLERY = 1001;

    //头像上传相关
    private static final int UPLOAD_IMGURL_SUCCESS = 3;
    public static final String EXTRA_FROM_ME = "fromMe";

    /**
     * 头像上传后的地址
     **/
    private String mUploadFilePath = "";
    private IconChoosePopupWindow mIconChoosePopupWindow;

    private Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message message) {
            if (mIsOpen == false) {
                return false;
            }

            int target = message.what;
            /*if (target == HANDLE_TIEZI_COUNT) {
                if (mTieZiCount > 0) {
                    mBtTieZi.setText(getString(R.string.mymeeting_tiezi) + " " + mTieZiCount);
                }else {
                    mBtTieZi.setText(getString(R.string.mymeeting_tiezi));
                }
            }else if(target == HANDLE_NOTE_COUNT) {
                if (mNoteCount > 0) {
                    mBtNote.setText(getString(R.string.mymeeting_note) + " " + mNoteCount);
                }else {
                    mBtNote.setText(getString(R.string.mymeeting_note));
                }
            }else*/
            if (target == UPLOAD_IMGURL_SUCCESS) {
                SharePreferenceUtils.saveUserString(Constants.USER_IMG, mUploadFilePath);

                if (mUploadFilePath.contains("https:"))
                    mUploadFilePath = mUploadFilePath.replaceFirst("s", "");
                Glide.with(getActivity()).load(mUploadFilePath).transform(new CircleTransform(getActivity())).into(mCivHeadIcon);
            }
            return false;
        }
    });

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.mycenter_warmning_panel:
                action(new MyMeetingWarmning(), R.string.mymeeting_warning, false, false, false);
                break;
            case R.id.settings_contact_panel:
                SettingContactActionFragment contact = new SettingContactActionFragment();
                action(contact, R.string.settings_contact, false, false, false);
                break;
            case R.id.setting:
                SettingFragment settingFragment = new SettingFragment();
                action(settingFragment, R.string.system_setting, false, false, false);
                break;
            case R.id.settings_share_panel:
                ShareUtils.shareTextWithUrl(getActivity(), Constants.APPNAME, getString(R.string.settings_share_wxtitle), Constants.APP_DOWNLOAD_SITE, null);
                /*SettingsShare share = new SettingsShare();
                action(share, R.string.settings_share_title, false, false, false);*/
                break;
            case R.id.settings_mark:
                goToStar(getActivity());
                break;
            case R.id.settings_help_panel:
                /*SettingsHelper help = new SettingsHelper ();
                View view = CommonUtils.initView(this.getActivity(), R.layout.hysqhome_shuoliangju_nav_rightbtn);
                TextView mText = (TextView) view.findViewById(R.id.hysq_jiangliangju_titlebar_send);
                help.setView(mText);
                action(help, R.string.settings_help_title, view, false, false, false);*/
                CollegeActivity.startCitCollegeActivity(getContext(), getActivity().getResources().getString(R.string.settings_help_title), Constants.FEEDBACK_URI);
                break;
            case R.id.settings_message:
                action(new MessageStationActionFragment(), R.string.home_messagestation, false, false, false);
                postUNReadQuestionNumber(false, 6);
                if(messageBadge!=null){
                    messageBadge.hide(false);
                }
                break;
            case R.id.tv_modify_info:
                CollegeActivity.startCitCollegeActivity(getContext(), getActivity().getResources().getString(R.string.settings_modify_info_title), Constants.MODEFIY_INFO_URI+SharePreferenceUtils.getUser(Constants.USER_IC_ID));
                /*ModifyInfoFragment fragment = new ModifyInfoFragment();
                action(fragment, R.string.my_info, false, false, false);*/
                break;
            /*case R.id.bt_login:
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                getActivity().startActivityForResult(intent, REQUEST_LOGIN);
                break;*/
            /*case R.id.bt_login_out:

                break;*/
            case R.id.civ_me:
            case R.id.tv_name:
                if (SharePreferenceUtils.getUserBoolean(Constants.USER_IS_LOGIN, false)) {
                    /*initPopupWindow();
                    mIconChoosePopupWindow.showAtLocation(mCivHeadIcon, Gravity.BOTTOM, 0, 0);
                    lightOff();*/
                    Intent intent = new Intent(getActivity(), LoginForUpdateInfoActivity.class);
                    intent.putExtra("update", true);
                    getActivity().startActivity(intent);
                } else {
                    LoginActivity.startLoginActivity(getActivity(), LoginActivity.TYPE_NORMAL, "", "", "", "");
//                    ChooseIdentityActivity.startChooseIdentityActivity(getActivity());
                }
                break;
            case R.id.iv_scan:
                if (AppApplication.isUserLogIn()) {
                    if (SharePreferenceUtils.getUser(Constants.USER_MOBILE).startsWith("18000000") || SharePreferenceUtils.getUser(Constants.USER_MOBILE).startsWith("18100000")) {
                        getActivity().startActivity(new Intent(getActivity(), QRCodeCaptureActivity.class));
                    } else {
                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                        builder.setTitle(R.string.dialog_tips).setMessage(R.string.qrcode_permission).setPositiveButton(R.string.positive_button, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        }).setCancelable(true).show();
                    }
                } else {
                    LoginActivity.startLoginActivity(getActivity(), LoginActivity.TYPE_NORMAL, "", "", "", "");
                }
                break;
            /*case R.id.rl_my_field:
                Intent fieldIntent = new Intent(getActivity(), ChooseFieldActivity.class);
                fieldIntent.putExtra(EXTRA_FROM_ME, true);
                startActivity(fieldIntent);
                break;
            case R.id.rl_my_keshi:
                Intent keshiIntent = new Intent(new Intent(getActivity(), ChooseKeShiActivity.class));
                keshiIntent.putExtra(EXTRA_FROM_ME, true);
                startActivity(keshiIntent);
                break;*/
            default:
                break;
        }
    }

    private void initEvents() {
        mMeetingAlertPanel.setOnClickListener(this);
        mContackPanel.setOnClickListener(this);
        mSettingsMark.setOnClickListener(this);
        mSharePanel.setOnClickListener(this);
        mHelpPanel.setOnClickListener(this);
        mCivHeadIcon.setOnClickListener(this);
        mSettings.setOnClickListener(this);
        tv_modify_info.setOnClickListener(this);
        username.setOnClickListener(this);
        mSettingMessage.setOnClickListener(this);
        iv_scan.setOnClickListener(this);
        getNoteCount();
    }

    /**
     * 查询需要显示的笔记
     */
    private void getNoteCount() {
        List<Note> allNotes = ConferenceDbUtils.getAllNotes();
        mNoteCount = allNotes.size();
        mHandler.sendEmptyMessage(HANDLE_NOTE_COUNT);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        StatusBarUtil.setStatusBarDarkTheme(getActivity(), false);
        View view = inflater.inflate(R.layout.fragment_me, container, false);

        // mRlMyField = (RelativeLayout) view.findViewById(R.id.rl_my_field);
        //mRlMyKeshi = (RelativeLayout) view.findViewById(R.id.rl_my_keshi);
        mCivHeadIcon = view.findViewById(R.id.civ_me);
        mMeetingAlertPanel = view.findViewById(R.id.mycenter_warmning_panel);
        mContackPanel = view.findViewById(R.id.settings_contact_panel);
        mSettingsMark = view.findViewById(R.id.settings_mark);
        mSharePanel = view.findViewById(R.id.settings_share_panel);
        mHelpPanel = view.findViewById(R.id.settings_help_panel);
        mSettings = view.findViewById(R.id.setting);
        username = view.findViewById(R.id.tv_name);
        welcomeInfo = view.findViewById(R.id.tv_welcome);
        mGridView = view.findViewById(R.id.gv_list);
        mSettingMessage = view.findViewById(R.id.settings_message);
        tv_modify_info = view.findViewById(R.id.tv_modify_info);
        tv_message = view.findViewById(R.id.tv_message_number);
        iv_scan = view.findViewById(R.id.iv_scan);
        imgList = new Drawable[]{getResources().getDrawable(R.drawable.remind), getResources().getDrawable(R.drawable.quiz), getResources().getDrawable(R.drawable.note)};
        strList = new String[]{getString(R.string.me_remind), getString(R.string.me_quiz), getString(R.string.me_note)};
        gridListAdapter = new GridListAdapter(getActivity());
        mGridView.setAdapter(gridListAdapter);
        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                switch (i) {
                    case 0:
                        MindBookFragment fragment = new MindBookFragment();
                        action(fragment, null);
                        break;
                    case 1:
                        if (AppApplication.facultyId != -1) {
                            action(ReceiveProfessorQuestionActionFragment.getInstance(), R.string.me_quiz, false, false, false);
                        } else {
                            MyQuestionSquarFragment squarFragment = new MyQuestionSquarFragment();
                            action(squarFragment, R.string.me_quiz, false, false, false);
                        }
                        postUNReadQuestionNumber(false, 18);
                        /*if (SharePreferenceUtils.getUserBoolean(Constants.USER_IS_LOGIN,false)) {
                            action(HistoryPostActionFragment.getInstance(), R.string.mymeeting_tiezi, false, false, false);
                        } else {
                            LoginActivity.startLoginActivity(getActivity(), LoginActivity.TYPE_NORMAL, "", "", "", "");
//                    ChooseIdentityActivity.startChooseIdentityActivity(getActivity());
                        }*/
                        break;
                    case 2:
                        NoteManageActionFragment noteManager = new NoteManageActionFragment();
                        action(noteManager, R.string.mymeeting_note, false, false, false);
                        break;
                }
            }
        });
        if (SharePreferenceUtils.getUser(Constants.USER_MOBILE).startsWith("18000000") || SharePreferenceUtils.getUser(Constants.USER_MOBILE).startsWith("18100000")) {
            iv_scan.setVisibility(View.VISIBLE);
        }
        if(!EventBus.getDefault().isRegistered(this)){
            EventBus.getDefault().register(this);
        }
        initEvents();
        refreshInfo();
        getUNReadMessageNumber();
        getUNReadQuestionNumber();
        return view;
    }

    private void queryCount() {
        //查询我的发帖
        CHYHttpClientUsage.getInstanse().doGetSceneShowByUser(Constants.getConId() + "", "-1", AppApplication.userId + "", AppApplication.userType + "", new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                try {
                    mTieZiCount = response.getInt("sceneShowCount");
                    mHandler.sendEmptyMessage(HANDLE_TIEZI_COUNT);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        getNoteCount();
    }

    /**
     * 获取未读消息数
     */
    private void getUNReadMessageNumber() {
        CHYHttpClientUsage.getInstanse().doGetUNReadMessage(new JsonHttpResponseHandler(Constants.ENCODING_GBK) {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                messageNumber = JSONCatch.parseInt("tokenMessageCount", response);
                messageBadge = new QBadgeView(getActivity()).bindTarget(tv_message).setBadgeNumber(messageNumber).setBadgeGravity(Gravity.TOP | Gravity.END).setBadgeTextSize(9, true).setBadgePadding(-0.00005f, true).stroke(getResources().getColor(R.color.remind_cycle_color), 2, true).setBadgeBackgroundColor(getResources().getColor(R.color.remind_cycle_color));//.setBadgeNumber()
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                ToastUtils.showToast("获取信息失败，请联系管理员");
            }

        });
    }

    /**
     * 获取未读问答数
     * 返回结果：photoWallCount、coursewareCount、questionCount
     */
    private void getUNReadQuestionNumber() {
        CHYHttpClientUsage.getInstanse().doGetUNReadQuestion(new JsonHttpResponseHandler(Constants.ENCODING_GBK) {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                questionNumber = JSONCatch.parseInt("questionCount", response);
                gridListAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                ToastUtils.showToast("获取信息失败，请联系管理员");
            }

        });
    }

    /**
     * 上传红点点击记录
     * 返回结果：photoWallCount、coursewareCount、questionCount
     */
    private void postUNReadQuestionNumber(boolean isCompass, int moduleNo) {
        CHYHttpClientUsage.getInstanse().doPostUNReadQuestion(isCompass, moduleNo, new JsonHttpResponseHandler(Constants.ENCODING_GBK) {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
            }
        });
    }

    public void onResume() {
        super.onResume();
        mIsOpen = true;
        queryCount();
        if (!isBackView) {
            StatusBarUtil.setStatusBarDarkTheme(getActivity(), false);
        }
        MobclickAgent.onPageStart(Constants.FRAGMENT_PERSONCENTER);
    }

    @Override
    public void onPause() {
        super.onPause();
        mIsOpen = false;

        MobclickAgent.onPageEnd(Constants.FRAGMENT_PERSONCENTER);
    }

    //刷新用户信息
    public void refreshInfo() {
        if (SharePreferenceUtils.getUserBoolean(Constants.USER_IS_LOGIN, false) && username != null && welcomeInfo != null && mCivHeadIcon != null) {
            username.setText(SharePreferenceUtils.getUser(Constants.USER_NAME));
            welcomeInfo.setText(getString(R.string.mymeeting_welcome_sb, SharePreferenceUtils.getUser(Constants.USER_NAME)));
            PicUtils.loadCircleImage(getActivity(), SharePreferenceUtils.getUser(Constants.USER_IMG), mCivHeadIcon);
        }
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        isBackView = hidden;
        if (!hidden) {
            StatusBarUtil.setStatusBarDarkTheme(getActivity(), false);
        }
    }

    class GridListAdapter extends BaseAdapter {
        public Context context;
        public LayoutInflater layoutInflater;

        public GridListAdapter(Context context) {
            this.context = context;
            layoutInflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return strList.length;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            convertView = layoutInflater.inflate(R.layout.fragment_me_gv_list, null);
            ImageView iv_gv_list = convertView.findViewById(R.id.iv_gv_list);
            TextView tv_gv_list = convertView.findViewById(R.id.tv_gv_list);
            TextView remind_red = convertView.findViewById(R.id.remind_red);
            iv_gv_list.setImageDrawable(imgList[position]);
            if (AppApplication.systemLanguage == 1) {
                tv_gv_list.setText(strList[position]);
                int dimen = getResources().getDimensionPixelSize(R.dimen.middle_text);
                tv_gv_list.setTextSize(TypedValue.COMPLEX_UNIT_PX, dimen);
            } else {
                int dimen;
                if (position == 0) {
                    dimen = getResources().getDimensionPixelSize(R.dimen.small_l_text);
                } else {
                    dimen = getResources().getDimensionPixelSize(R.dimen.small_m_text);
                }
                tv_gv_list.setText(strList[position]);
                tv_gv_list.setTextSize(TypedValue.COMPLEX_UNIT_PX, dimen);
            }
            tv_gv_list.setText(strList[position]);
            return convertView;
        }
    }
    //对app进行评分

    /**
     * 跳转应用市场
     */
    public void goToStar(final Activity context) {
        ArrayList<String> installAppMarkets = GoToScoreUtils.getInstallAppMarkets(context);
        final ArrayList<String> filterInstallMarkets = GoToScoreUtils.getFilterInstallMarkets(context, installAppMarkets);
        final ArrayList<String> markets = new ArrayList<>();
        if (filterInstallMarkets.size() > 0) {
            //过滤
            for (int a = 0; a < filterInstallMarkets.size(); a++) {
                Log.e("应用市场++++", filterInstallMarkets.get(a));
                String pkg = filterInstallMarkets.get(a);
                //if (installAppMarkets.contains(pkg)&&!"com.tencent.android.qqdownloader".equals(pkg)) {
                markets.add(pkg);
                //}
            }
            List<String> names = new ArrayList<>();
            for (int b = 0; b < markets.size(); b++) {
                AppUtils.AppInfo appInfo = AppUtils.getAppInfo(markets.get(b));
                String name = appInfo.getName();
                names.add(name);
            }
            GoToScoreUtils.showDialog(context, new CustomSelectDialog.SelectDialogListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    GoToScoreUtils.launchAppDetail(context, Constants.PACKAGE_NAME, markets.get(position));
                }
            }, names);
        } else {
            //豌豆荚评分链接
            Uri uri = Uri.parse(Constants.MARKAPP_URI);
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent);
        }
    }

    /**
     * 收到更新通知
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onUpdateConferenceEvent(HomeActivity.UpdateConferenceEvent event) {
        if(AppApplication.isUserLogIn()){
            refreshInfo();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(EventBus.getDefault().isRegistered(this)){
            EventBus.getDefault().unregister(this);
        }
    }
}
