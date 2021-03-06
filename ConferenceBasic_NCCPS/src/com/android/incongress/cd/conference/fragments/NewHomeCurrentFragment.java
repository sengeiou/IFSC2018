package com.android.incongress.cd.conference.fragments;

import android.animation.ValueAnimator;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.android.incongress.cd.conference.CollegeActivity;
import com.android.incongress.cd.conference.HomeActivity;
import com.android.incongress.cd.conference.adapters.CourSewareAdapter;
import com.android.incongress.cd.conference.adapters.HomeNormalAdapter;
import com.android.incongress.cd.conference.api.CHYHttpClientUsage;
import com.android.incongress.cd.conference.base.AppApplication;
import com.android.incongress.cd.conference.base.BaseFragment;
import com.android.incongress.cd.conference.base.Constants;
import com.android.incongress.cd.conference.beans.ActivityBean;
import com.android.incongress.cd.conference.beans.CoursewareBean;
import com.android.incongress.cd.conference.beans.NewHomeIconBean;
import com.android.incongress.cd.conference.beans.Row;
import com.android.incongress.cd.conference.beans.SceneShowArrayBean;
import com.android.incongress.cd.conference.fragments.bus_reminder.MeetingBusRemindAllFragment;
import com.android.incongress.cd.conference.fragments.cit_live.CitLiveFragment;
import com.android.incongress.cd.conference.fragments.college.CollegeHomeFragment;
import com.android.incongress.cd.conference.fragments.exhibitor.NewExhibitorsActionFragment;
import com.android.incongress.cd.conference.fragments.interactive.HdSessionActionFragment;
import com.android.incongress.cd.conference.fragments.live.LiveFragment;
import com.android.incongress.cd.conference.fragments.me.PersonCenterFragment;
import com.android.incongress.cd.conference.fragments.meeting_guide.NewMeetingInfoFragment;
import com.android.incongress.cd.conference.fragments.meeting_schedule.MeetingScheduleListActionFragment;
import com.android.incongress.cd.conference.fragments.meeting_schedule.MeetingScheduleViewPageFragment;
import com.android.incongress.cd.conference.fragments.message_station.MessageStationActionFragment;
import com.android.incongress.cd.conference.fragments.my_schedule.MyScheduleActionFragment;
import com.android.incongress.cd.conference.fragments.now_next.NextFragment;
import com.android.incongress.cd.conference.fragments.now_next.NowFragment;
import com.android.incongress.cd.conference.fragments.photo_album.PhotoAlbumFragment;
import com.android.incongress.cd.conference.fragments.professor_secretary.ScretaryProfessorFragment;
import com.android.incongress.cd.conference.fragments.professor_secretary.SecretaryActivity;
import com.android.incongress.cd.conference.fragments.question.QuestionSquarFragment;
import com.android.incongress.cd.conference.fragments.scenic_xiu.ScenicXiuFragment;
import com.android.incongress.cd.conference.fragments.search_schedule.NewSearchScheduleActionFragment;
import com.android.incongress.cd.conference.fragments.search_schedule.SegmentScheduleActionFragment;
import com.android.incongress.cd.conference.fragments.search_speaker.SpeakerSearchFragment;
import com.android.incongress.cd.conference.fragments.wall_poster.PosterFragment;
import com.android.incongress.cd.conference.model.Ad;
import com.android.incongress.cd.conference.model.ConferenceDbUtils;
import com.android.incongress.cd.conference.save.ParseUser;
import com.android.incongress.cd.conference.save.SharePreferenceUtils;
import com.android.incongress.cd.conference.ui.login.view.LoginActivity;
import com.android.incongress.cd.conference.utils.ActivityUtils;
import com.android.incongress.cd.conference.utils.ArrayUtils;
import com.android.incongress.cd.conference.utils.CommonUtils;
import com.android.incongress.cd.conference.utils.DateUtil;
import com.android.incongress.cd.conference.utils.DensityUtil;
import com.android.incongress.cd.conference.utils.JSONCatch;
import com.android.incongress.cd.conference.utils.LanguageUtil;
import com.android.incongress.cd.conference.utils.LogUtils;
import com.android.incongress.cd.conference.utils.MyLogger;
import com.android.incongress.cd.conference.utils.PicUtils;
import com.android.incongress.cd.conference.utils.ToastUtils;
import com.android.incongress.cd.conference.widget.StatusBarUtil;
import com.android.incongress.cd.conference.widget.zxing.activity.CaptureActivity;
import com.android.incongress.cd.conference.widget.zxing.activity.QRCodeCaptureActivity;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.jude.rollviewpager.RollPagerView;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.mobile.incongress.cd.conference.basic.csccm.R;
import com.umeng.analytics.MobclickAgent;
import com.zyp.cardview.YcCardView;

import org.apache.http.util.EncodingUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.litepal.LitePal;
import org.litepal.LitePalDB;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.jpush.android.api.JPushInterface;
import cz.msebera.android.httpclient.Header;
import q.rorbin.badgeview.Badge;
import q.rorbin.badgeview.QBadgeView;

/**
 * Created by GG on 2018/1/3.
 */

public class NewHomeCurrentFragment extends BaseFragment implements View.OnClickListener {
    private LinearLayout mLlConstainer;
    private YcCardView marquee_layout;
    private ViewFlipper mMarqueeView;
    private Row mRow;
    private RecyclerView mRecyclerView;
    //上方广告抽取
    private ImageView mTopADImg, iv_back;
    private String mIconFilePath;

    private ImageView zk, zk_inner;
    //专家秘书模块需要
    private List<ActivityBean> mAllActivitys = new ArrayList<>();
    private List<ActivityBean> mValidActivitys = new ArrayList<>();
    private List<SceneShowArrayBean> mScenceShowBeans = new ArrayList<>();
    private int mTaskNum, mQuestionNum;
    //我的专家秘书
    private TextView mTvSecretaryTime, mTvSecretaryRoom, mTvSecretaryTask, mTvSecretarySessionName;
    private LinearLayout mSecretaryView, courseware_text;
    private Map<String, String> textUrlMap = new HashMap<>();
    //参数为了在切换到activity返回后，fragment重新设置导航栏字体颜色
    private boolean isBackView = true;
    //角标显示
    private int photoNumber, sessionNumber,questionNumber;
    private List<NewHomeIconBean> iconCodeBeans;
    private Badge photoBadge, sessionBadge,questionBadge;

    private static final int PROGRAM = 1; //看日程
    private static final int SEARCH = 2;    //查日程
    private static final int MY_AGENDA = 3; //我的日程
    private static final int INFO_RELEASE = 4;  //现场秀
    private static final int LIVE = 5;          //直播
    private static final int MESSAGE = 6;       //消息站
    private static final int FACULTY = 7;       //专家秘书
    private static final int INFOMATION = 8;    //参会指南
    private static final int INTERACTIVE = 9;   //现场互动
    private static final int LEARNING = 10;     //实习中西
    private static final int POSTER = 11;       //壁报
    private static final int DEMAND = 12;       //学院
    private static final int EXHIBITORS = 13;   //参展商
    private static final int PERSONAL = 14;     //我
    private static final int FACULTY_INDEX = 15;    //讲者检索
    private static final int NEWS_CENTER = 16; //新闻中心
    private static final int PICTURE = 17; //图片模块
    private static final int QUESTION = 18; //提问
    private static final int SCANE = 19; //扫一扫
    private static final int NOW = 20;//正在进行
    private static final int NEXT = 21;//即将进行
    private static final int BUS = 22; //班车提醒
    private static final int PHOTOWALL = 23; //照片墙
    private static final int VENUEPICTURE = 24; //场馆图
    private static final int SCHEDULE_PREVIEW = 25; //日程预览
    private static final int HANDLER_NUMS = 0x0001;
    private int currentSize, priSize, fixedSize = 50;

    private List<CoursewareBean> coursewareBeanList = new ArrayList<>();
    private CourSewareAdapter mCourSewareAdapter;
    private List<String> textList = new ArrayList<>();
    private RollPagerView roll_view;
    private static String TOTALCONID = "totalConId", CONID = "conIDd", FROMWHERE = "fromWhere";
    private int totalConId;

    public static NewHomeCurrentFragment getInstance(int totalConId, int conId, String fromWhere) {
        NewHomeCurrentFragment fragment = new NewHomeCurrentFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(TOTALCONID, totalConId);
        bundle.putInt(CONID, conId);
        bundle.putString(FROMWHERE, fromWhere);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        StatusBarUtil.setStatusBarDarkTheme(getActivity(), false);
        View view = inflater.inflate(R.layout.new_dynamic_home_fragment, null);
        totalConId = getArguments().getInt(TOTALCONID);
        SharePreferenceUtils.saveAppInt(Constants.TOTALCONID, totalConId);
        SharePreferenceUtils.saveAppInt(Constants.CONID, getArguments().getInt(CONID));
        SharePreferenceUtils.saveAppString(Constants.FROMWHERE, getArguments().getString(FROMWHERE));
        doGetUserInfo(SharePreferenceUtils.getUser(Constants.USER_IC_ID));
        LitePalDB litePalDB = LitePalDB.fromDefault("newdb" + totalConId);
        LitePal.use(litePalDB);
        mLlConstainer = view.findViewById(R.id.ll_container);
        roll_view = view.findViewById(R.id.roll_view);
        mTopADImg = view.findViewById(R.id.layout_ad_top);
        zk = Constants.HOME_CLICK_POSITION_INNER ? (ImageView) view.findViewById(R.id.zk_inner_button) : (ImageView) view.findViewById(R.id.zk_button);
        mRecyclerView = view.findViewById(R.id.courseware_recycler);
        mRecyclerView.setFocusable(false);
        marquee_layout = view.findViewById(R.id.marquee_layout);
        courseware_text = view.findViewById(R.id.courseware_layout);
        mMarqueeView = view.findViewById(R.id.viewflipper);

        mSecretaryView = view.findViewById(R.id.ll_secretary);
        mTvSecretaryTime = view.findViewById(R.id.tv_secretary_time);
        mTvSecretaryRoom = view.findViewById(R.id.tv_secretary_room);
        mTvSecretaryTask = view.findViewById(R.id.tv_secretary_task);
        mTvSecretarySessionName = view.findViewById(R.id.tv_secretary_session_name);
        iv_back = view.findViewById(R.id.iv_back);
        iv_back.setVisibility(View.VISIBLE);
        roll_view.setAdapter(new HomeNormalAdapter(null, getTopImage(), getActivity(), totalConId + "/"));
        roll_view.setHintView(null);
        mIconFilePath = AppApplication.instance().getSDPath() + Constants.FILESDIR + totalConId + "/icon.txt";

        ScaleAnimation sa = (ScaleAnimation) AnimationUtils.loadAnimation(getActivity(), R.anim.scale);
        LayoutAnimationController lac = new LayoutAnimationController(sa, 0);
        mLlConstainer.setLayoutAnimation(lac);
        mRecyclerView.setNestedScrollingEnabled(false);
        mRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        CHYHttpClientUsage.getInstanse().doGetContentStream(new JsonHttpResponseHandler("gbk") {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                super.onSuccess(statusCode, headers, response);
                try {
                    if (response.length() > 0) {
                        for (int i = 0; i < response.length(); i++) {
                            String s = response.getJSONObject(i).toString();
                            String text = response.getJSONObject(i).getString("title");
                            if (s.contains("link")) {
                                String textUrl = response.getJSONObject(i).getString("link");
                                textUrlMap.put(text, textUrl);
                            }
                            textList.add(text);
                            mMarqueeView.addView(getTextViewTitle(text));
                        }
                        marquee_layout.setVisibility(View.VISIBLE);
                    } else {
                        marquee_layout.setVisibility(View.GONE);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        CHYHttpClientUsage.getInstanse().doGetCoursewareStream(new JsonHttpResponseHandler("gbk") {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                super.onSuccess(statusCode, headers, response);
                try {
                    for (int i = 0; i < response.length(); i++) {
                        CoursewareBean bean = new CoursewareBean();
                        bean.setAuthor(response.getJSONObject(i).getString("author"));
                        bean.setDataId(response.getJSONObject(i).getInt("dataId"));
                        bean.setFirstPic(response.getJSONObject(i).getString("firstPic"));
                        bean.setTitle(response.getJSONObject(i).getString("title"));
                        bean.setUrl(response.getJSONObject(i).getString("url"));
                        coursewareBeanList.add(bean);
                    }
                    if (coursewareBeanList.size() > 0) {
                        courseware_text.setVisibility(View.VISIBLE);
                        mCourSewareAdapter.notifyDataSetChanged();
                    } else {
                        courseware_text.setVisibility(View.GONE);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });
        getHomeIconInfo();

        getHomeAssistant();

        zk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentSize < priSize) {
                    setHeightAnimator(priSize);
                    zk.setImageResource(R.drawable.sq);
                } else {
                    setHeightAnimator(fixedSize);
                    zk.setImageResource(R.drawable.zk);
                }
            }
        });
        mMarqueeView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = textList.get(mMarqueeView.getDisplayedChild());
                String url = textUrlMap.get(title);
                if (url != null) {
                    CollegeActivity.startCitCollegeActivity(getActivity(), title, url);
                }
            }
        });
        mCourSewareAdapter = new CourSewareAdapter(coursewareBeanList, getActivity(), getActivity().getWindowManager().getDefaultDisplay().getWidth() / 2);
        mRecyclerView.setAdapter(mCourSewareAdapter);

        mCourSewareAdapter.SetOnItemClickListener(new CourSewareAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                CoursewareBean bean = coursewareBeanList.get(position);
                String url = bean.getUrl();
                CollegeActivity.startCitCollegeActivity(getActivity(), bean.getTitle(), url);
            }
        });
        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                performback();
            }
        });
        getUNReadQuestionNumber();
        return view;
    }

    private List<Ad> getTopImage() {
        List<Ad> topList = new ArrayList<>();
        List<Ad> adList = ConferenceDbUtils.getAllAds();
        for(int i = 0;i<adList.size();i++){
            if (adList.get(i).getAdLevel() == 2) {
                topList.add(adList.get(i));
            }
        }
        return topList;
    }

    private TextView getTextViewTitle(String title) {
        TextView textView = (TextView) LayoutInflater.from(getContext()).inflate(R.layout.home_xxview, null);
        textView.setText(title);
        return textView;
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        isBackView = hidden;
        if (hidden) {
            mMarqueeView.stopFlipping();
            mMarqueeView.removeAllViews();
        } else {
            StatusBarUtil.setStatusBarDarkTheme(getActivity(), false);
            for (int i = 0; i < textList.size(); i++) {
                mMarqueeView.addView(getTextViewTitle(textList.get(i)));
            }
            mMarqueeView.startFlipping();
        }
    }

    private void getHomeIconInfo() {
        iconCodeBeans = new ArrayList<>();
        try {
            JSONObject obj = new JSONObject(readFileSdcardFile(mIconFilePath));
            Gson gson = new Gson();
            String iconSort = JSONCatch.parseString("iconSort", obj);
            mRow = gson.fromJson(iconSort, Row.class);
            if (mRow.getRows().size() <= 3) {
                zk.setVisibility(View.GONE);
            }
            fixedSize = 0;
            priSize = 0;
            for (int i = 0; i < mRow.getRows().size(); i++) {
                Row.RowsBean bean = mRow.getRows().get(i);
                //设置首页上方广告
                if (i == 0 && bean.getObj().size() == 1 && bean.getObj().get(0).getIconCode().equals(17 + "")) {
                    final Row.RowsBean.ObjBean picBean = bean.getObj().get(0);
                    PicUtils.loadImageUrl(getActivity(), picBean.getIconUrl(), mTopADImg);
                    mTopADImg.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (!TextUtils.isEmpty(picBean.getNewUrl())) {
                                if (AppApplication.systemLanguage == 1) {
                                    CollegeActivity.startCitCollegeActivity(getActivity(), picBean.getIconName(), picBean.getNewUrl());
                                } else {
                                    CollegeActivity.startCitCollegeActivity(getActivity(), picBean.getIconEnName(), picBean.getNewUrl());
                                }
                            }
                        }
                    });
                    String imgSize = picBean.getImgSize();
                    String[] wh = imgSize.split(",");
                    int width = Integer.parseInt(wh[0]);
                    double height = Double.parseDouble(wh[1]);
                    double bl = width / height;
                    int widthMax = getActivity().getWindowManager().getDefaultDisplay().getWidth();
                    ViewGroup.LayoutParams lp = mTopADImg.getLayoutParams();
                    lp.height = (int) (widthMax / bl);
                    mTopADImg.setLayoutParams(lp);
                } else if (i != 0) {
                    LinearLayout linearLayout;
                    if (i < mRow.getRows().size() - 1) {
                        linearLayout = getHorizontalLinearLayout(true);
                    } else {
                        linearLayout = getHorizontalLinearLayout(false);
                    }
                    for (int j = 0; j < bean.getObj().size(); j++) {
                        Row.RowsBean.ObjBean rowBean = bean.getObj().get(j);
                        LinearLayout innerLinearLayout = getInnerLinearLayout(Float.parseFloat(rowBean.getWidth().replace("%", "")), j);
                        innerLinearLayout.addView(addTextAndImage(rowBean, Float.parseFloat(rowBean.getWidth().replace("%", "")), innerLinearLayout));
                        innerLinearLayout.setTag(rowBean);
                        innerLinearLayout.setOnClickListener(this);
                        linearLayout.addView(innerLinearLayout);
                    }
                    if (i == 1 || i == 2) {
                        fixedSize = fixedSize + ActivityUtils.getViewHeight(linearLayout) + 43;
                    }

                    mLlConstainer.addView(linearLayout);
                }
            }
            priSize = ActivityUtils.getViewHeight(mLlConstainer);
            setHeightAnimator(priSize);
            mHandler.sendEmptyMessageDelayed(1, 2000);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getHomeAssistant() {
        mAllActivitys = ConferenceDbUtils.getSessionAndMeetingBySpeakerId(AppApplication.facultyId);
        // 处理过期时间下的活动
        int size = mAllActivitys.size();
        Date date;
        String current_day = DateUtil.getNowDate(DateUtil.DEFAULT);
        String currentTime = current_day + " 00:00:00";
        Date currentDate = DateUtil.getDate(currentTime, DateUtil.DEFAULT_SECOND);
        long currentSecond = currentDate.getTime();

        for (int j = 0; j < size; j++) {
            String time = mAllActivitys.get(j).getTime() + ":00";
            date = DateUtil.getDate(time, DateUtil.DEFAULT_SECOND);
            long second = date.getTime();
            if (second > currentSecond) {
                mValidActivitys.add(mAllActivitys.get(j));
            }
        }
        //按照时间排序。
        ArrayUtils.quickSort(mValidActivitys);

        if (mValidActivitys != null && mValidActivitys.size() > 0) {
            //获取距离当前时间最近的
            ActivityBean activityBean = null;
            for (int k = 0; k < mValidActivitys.size(); k++) {
                Date date1 = DateUtil.getDate(mValidActivitys.get(k).getTime(), DateUtil.DEFAULT_ENGLISH2);
                if (date1.getTime() > System.currentTimeMillis()) {
                    activityBean = mValidActivitys.get(k);
                    break;
                }
            }

            if (activityBean != null) {
                if (AppApplication.systemLanguage == 1) {
                    mTvSecretaryTime.setText(activityBean.getDate() + " " + activityBean.getStart_time() + "-" + activityBean.getEnd_time());
                    mTvSecretaryRoom.setText(activityBean.getLocation());
                    mTvSecretaryTask.setText("任务：" + activityBean.getTypeName());
                    mTvSecretarySessionName.setText(activityBean.getActivityName());
                } else {
                    mTvSecretaryTime.setText(activityBean.getDate() + " " + activityBean.getStart_time() + "-" + activityBean.getEnd_time());
                    mTvSecretaryRoom.setText(activityBean.getLocationEn());
                    mTvSecretaryTask.setText("Task:" + activityBean.getTypeEnName());
                    mTvSecretarySessionName.setText(activityBean.getActivityNameEN());
                }
            }
        } else {
            //没有任务
            mTvSecretaryTime.setVisibility(View.GONE);
            mTvSecretaryRoom.setVisibility(View.GONE);
            mTvSecretaryTask.setVisibility(View.GONE);
            mTvSecretarySessionName.setText(R.string.secretary_no_task);
        }

        mSecretaryView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goScretary2019();
                ;
            }
        });
        updateSecretaryView();
    }

    //更新专家秘书状态
    public void updateSecretaryView() {
        if (AppApplication.facultyId != -1 && Constants.IS_SECRETARY_SHOW && SharePreferenceUtils.getUserBoolean(Constants.USER_IS_LOGIN, false)) {
            mSecretaryView.setVisibility(View.VISIBLE);
        } else {
            mSecretaryView.setVisibility(View.GONE);
        }
    }

    /**
     * 此方法为了在每个控件上添加边距
     *
     * @param weight
     * @param index  通过这个判断第几个控件
     * @return
     */
    private LinearLayout getInnerLinearLayout(float weight, int index) {
        LinearLayout linearLayout = new LinearLayout(getActivity());
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT);
        if (weight == 50) {
            weight = (float) (weight + 2.4);
        }
        lp.weight = weight;
        int spMarginBottom = DensityUtil.dip2px(getActivity(), 3.5f);
        int spMargin2Bottom = DensityUtil.dip2px(getActivity(), 7f);
        if (index == 0) {
            lp.setMargins(0, 0, spMarginBottom, 0);
        } else if (index == 1) {
            lp.setMargins(spMarginBottom, 0, spMarginBottom, 0);
        } else if (index == 2) {
            lp.setMargins(spMarginBottom, 0, 0, 0);
        } else if (index == 3) {
            lp.setMargins(spMargin2Bottom, 0, 0, 0);
        }
        linearLayout.setLayoutParams(lp);
        linearLayout.setOrientation(LinearLayout.HORIZONTAL);
        linearLayout.setGravity(Gravity.CENTER);

        return linearLayout;
    }

    /**
     * @return
     */
    private LinearLayout getHorizontalLinearLayout(boolean have) {
        LinearLayout linearLayout = new LinearLayout(getActivity());

        int spWidth = DensityUtil.dip2px(getActivity(), 110);
        int spMarginBottom = DensityUtil.dip2px(getActivity(), 6);
        int spPadding = DensityUtil.dip2px(getActivity(), 5);

        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, spWidth);
        if (have) {
            lp.setMargins(0, 0, 0, spMarginBottom);
        }
        linearLayout.setLayoutParams(lp);
        //linearLayout.setPadding(spPadding, 0, spPadding, 0);
        linearLayout.setOrientation(LinearLayout.HORIZONTAL);
        linearLayout.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.transparent));

        return linearLayout;
    }

    private View addTextAndImage(Row.RowsBean.ObjBean bean, float weight, ViewGroup parentView) {
        int iconCode = 0;
        try {
            iconCode = Integer.parseInt(bean.getIconCode());
        } catch (Exception e) {
            e.printStackTrace();
            iconCode = 0;
        }

        int iconDefaultId = getDefaultIconId(iconCode);
        if (iconCode != 0) {
            if (weight >= 50) {
                //横向布局
                View view = LayoutInflater.from(getActivity()).inflate(R.layout.merge_horizontal_text_image, parentView, false);
                view.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                TextView tvName = view.findViewById(R.id.tv_name);
                ImageView ivLogo = view.findViewById(R.id.iv_logo);
                TextView tvMsgNum = view.findViewById(R.id.tv_msg_nums);
                ImageView ivInteractive = view.findViewById(R.id.iv_hd_session_on);
                TextView remind_red = view.findViewById(R.id.remind_red);
                view.setBackgroundColor(Color.parseColor(bean.getIconColor()));
                if (AppApplication.systemLanguage == 1) {
                    tvName.setText(bean.getIconName());
                    int dimen = getResources().getDimensionPixelSize(R.dimen.large_home_text);
                    tvName.setTextSize(TypedValue.COMPLEX_UNIT_PX, dimen);
                } else {
                    tvName.setText(bean.getIconEnName());
                    int dimen = getResources().getDimensionPixelSize(R.dimen.shifting_label);
                    tvName.setTextSize(TypedValue.COMPLEX_UNIT_PX, dimen);
                }
                if (iconCode == PHOTOWALL || iconCode == DEMAND) {
                    NewHomeIconBean bean1 = new NewHomeIconBean();
                    bean1.setIconCode(iconCode);
                    bean1.setTextView(remind_red);
                    iconCodeBeans.add(bean1);
                }
                tvName.setTextColor(Color.parseColor(bean.getIconFontColor()));
                if (TextUtils.isEmpty(bean.getIconUrl())) {
                    ivLogo.setImageResource(iconDefaultId);
                    //ivLogo.setImageDrawable(ImageColorChangeUtils.changeIconColor(getActivity(), iconDefaultId, Color.parseColor(bean.getIconFontColor())));
                } else {
                    PicUtils.loadImageUrl(getActivity(), bean.getIconUrl(), ivLogo);
                }
                return view;
            } else {
                //纵向布局
                View view = LayoutInflater.from(getActivity()).inflate(R.layout.merge_vertical_text_image, parentView, false);
                LinearLayout ll_item = (LinearLayout) view.findViewById(R.id.ll_item);
                TextView tvName = (TextView) view.findViewById(R.id.tv_name);
                ImageView ivLogo = (ImageView) view.findViewById(R.id.iv_logo);
                TextView tvMsgNum = (TextView) view.findViewById(R.id.tv_msg_nums);
                ImageView ivInteractive = (ImageView) view.findViewById(R.id.iv_hd_session_on);
                TextView remind_red = view.findViewById(R.id.remind_red);
                //AppApplication.applyFont(getActivity(), view, "fonts/cg.TTF");
                if (AppApplication.systemLanguage == 1) {
                    tvName.setText(bean.getIconName());
                    int dimen = getResources().getDimensionPixelSize(R.dimen.large_home_text);
                    tvName.setTextSize(TypedValue.COMPLEX_UNIT_PX, dimen);
                } else {
                    tvName.setText(bean.getIconEnName());
                    int dimen = getResources().getDimensionPixelSize(R.dimen.shifting_label);
                    tvName.setTextSize(TypedValue.COMPLEX_UNIT_PX, dimen);
                }
                if (iconCode == PHOTOWALL || iconCode == DEMAND || iconCode == QUESTION) {
                    NewHomeIconBean bean1 = new NewHomeIconBean();
                    bean1.setIconCode(iconCode);
                    bean1.setTextView(remind_red);
                    iconCodeBeans.add(bean1);
                }
                tvName.setTextColor(Color.parseColor(bean.getIconFontColor()));

                view.setBackgroundColor(Color.parseColor(bean.getIconColor()));

                if (TextUtils.isEmpty(bean.getIconUrl())) {
                    ivLogo.setImageResource(iconDefaultId);
                } else {
                    PicUtils.loadImageUrl(getActivity(), bean.getIconUrl(), ivLogo);
                }
                return view;
            }
        } else {
            ToastUtils.showToast("iconCode解析出错...");
            return null;
        }
    }

    /**
     * 根据iconId去查默认的图片
     *
     * @param iconCode
     */
    private int getDefaultIconId(int iconCode) {
        switch (iconCode) {
            case PROGRAM:
                return R.drawable.watch_schedule;
            case SEARCH:
                return R.drawable.search_schedule;
            case MY_AGENDA:
                return R.drawable.my_schedule;
            case INFO_RELEASE:
                return R.drawable.home_icon_scenicxiu;
            case LIVE:
                return R.drawable.home_icon_live;
            case MESSAGE:
                return R.drawable.home_icon_msg;
            case FACULTY:
                return R.drawable.home_icon_zjms;
            case INFOMATION:
                return R.drawable.home_icon_guide;
            case INTERACTIVE:
                return R.drawable.home_icon_interactive;
            case LEARNING:
                return R.drawable.home_icon_handson;
            case POSTER:
                return R.drawable.home_icon_dzbb;
            case DEMAND:
                return R.drawable.home_icon_cit_college;
            case EXHIBITORS:
                return R.drawable.home_icon_exhibitor;
            case PERSONAL:
                return R.drawable.home_icon_me;
            case FACULTY_INDEX:
                return R.drawable.home_icon_jzjs;
            case NEWS_CENTER:
                return R.drawable.home_icon_news;
            case QUESTION:
                return R.drawable.home_icon_question;
            case SCANE:
                return R.drawable.home_icon_scane;
            case NOW:
                return R.drawable.home_icon_now;
            case NEXT:
                return R.drawable.home_icon_next;
            case BUS:
                return R.drawable.home_icon_reminder;
            case PHOTOWALL:
                return R.drawable.home_icon_photowall;
            case VENUEPICTURE:
                return R.drawable.home_icon_venuepic;
            case SCHEDULE_PREVIEW:
                return R.drawable.home_icon_venuepic;

        }
        return 0;
    }

    @Override
    public void onClick(View v) {
        Row.RowsBean.ObjBean rowBean = (Row.RowsBean.ObjBean) v.getTag();
        StatusBarUtil.setStatusBarDarkTheme(getActivity(), true);
        if (rowBean != null) {
            if (!TextUtils.isEmpty(rowBean.getIconName())) {
                uploadClick(rowBean.getIconName());
            }
            if (TextUtils.isEmpty(rowBean.getNewModel())) {//&& Integer.valueOf(rowBean.getIconCode()) != DEMAND
                //根据ID跳转到不同的模块中
                switch (Integer.parseInt(rowBean.getIconCode())) {

                    case PROGRAM:
                        /*Intent intent = new Intent(getActivity(),MainMyActivity.class);
                        startActivity(intent);*/
                        //看日程
                        if (AppApplication.systemLanguage == 1) {
                            goLookSchedule(rowBean.getIconName());
                        } else {
                            goLookSchedule(rowBean.getIconEnName());
                        }
                        break;
                    case SEARCH:
                        //差日程
                        if (AppApplication.systemLanguage == 1) {
                            goSearchSchedule(rowBean.getIconName());
                        } else {
                            goSearchSchedule(rowBean.getIconEnName());
                        }
                        break;
                    case MY_AGENDA:
                        //我的日程
                        if (AppApplication.systemLanguage == 1) {
                            goMySchedule(rowBean.getIconName());
                        } else {
                            goMySchedule(rowBean.getIconEnName());
                        }
                        break;
                    case INFO_RELEASE:
//                        Intent social = new Intent(getActivity(), SocialActivity.class);
//                        startActivity(social);
                        //现场秀
                        if (AppApplication.systemLanguage == 1) {
                            goScenicXiu(rowBean.getIconName());
                        } else {
                            goScenicXiu(rowBean.getIconEnName());
                        }
                        break;
                    case LIVE:
                        //直播
                        if (AppApplication.systemLanguage == 1) {
                            goLive(rowBean.getIconName());
                        } else {
                            goLive(rowBean.getIconEnName());
                        }
                        break;
                    case MESSAGE:
                        //消息站
                        goMessageStation();
                        break;
                    case INFOMATION:
                        //参会指南
                        if (AppApplication.systemLanguage == 1) {
                            goGuide(rowBean.getIconName());
                        } else {
                            goGuide(rowBean.getIconEnName());
                        }
                        break;
                    case LEARNING:
                        //实习中心
                        goHandsOn();
                        break;
                    case DEMAND:
                        //学院
                        if (AppApplication.systemLanguage == 1) {
                            goCollege(rowBean.getIconName());
                        } else {
                            goCollege(rowBean.getIconEnName());
                        }
                        postUNReadQuestionNumber(false,12);
                        sessionBadge.hide(false);
                        break;
                    case EXHIBITORS:
                        //参展商
                        if (AppApplication.systemLanguage == 1) {
                            goExhibitor(rowBean.getIconName());
                        } else {
                            goExhibitor(rowBean.getIconEnName());
                        }
                        break;
                    case PERSONAL:
                        //我
                        goPersonCenter();
                        break;
                    case FACULTY_INDEX:
                        //讲者检索
                        if (AppApplication.systemLanguage == 1) {
                            goSpeakerSearch(rowBean.getIconName());
                        } else {
                            goSpeakerSearch(rowBean.getIconEnName());
                        }
                        break;
                    case INTERACTIVE:
                        if (AppApplication.systemLanguage == 1) {
                            goQuestionAndA(rowBean.getIconName());
                        } else {
                            goQuestionAndA(rowBean.getIconEnName());
                        }
                        break;
                    case POSTER:
                        if (AppApplication.systemLanguage == 1) {
                            goPost(rowBean.getIconName());
                        } else {
                            goPost(rowBean.getIconEnName());
                        }
                        break;
                    case FACULTY:
                        goSecretary();
                        break;
                    case PICTURE:
                        if (!TextUtils.isEmpty(rowBean.getNewUrl())) {
                            if (AppApplication.systemLanguage == 1) {
                                CollegeActivity.startCitCollegeActivity(getActivity(), rowBean.getIconName(), rowBean.getNewUrl());
                            } else {
                                CollegeActivity.startCitCollegeActivity(getActivity(), rowBean.getIconEnName(), rowBean.getNewUrl());
                            }
                        }
                        break;
                    case QUESTION:
                        if (AppApplication.systemLanguage == 1) {
                            goQuestions(rowBean.getIconName());
                        } else {
                            goQuestions(rowBean.getIconEnName());
                        }
                        postUNReadQuestionNumber(false,18);
                        questionBadge.hide(false);
                        break;
                    case SCANE:
                        goScane();
                        break;
                    case NOW:
                        if (AppApplication.systemLanguage == 1) {
                            goNow(rowBean.getIconName());
                        } else {
                            goNow(rowBean.getIconEnName());
                        }
                        break;
                    case NEXT:
                        if (AppApplication.systemLanguage == 1) {
                            goNext(rowBean.getIconName());
                        } else {
                            goNext(rowBean.getIconEnName());
                        }
                        break;
                    case BUS:
                        if (AppApplication.systemLanguage == 1) {
                            goBus(rowBean.getIconName());
                        } else {
                            goBus(rowBean.getIconEnName());
                        }
                        break;
                    //照片墙
                    case PHOTOWALL:
                        if (AppApplication.systemLanguage == 1) {
                            goPhotoAlbum(rowBean.getIconName());
                        } else {
                            goPhotoAlbum(rowBean.getIconEnName());
                        }
                        postUNReadQuestionNumber(false,23);
                        photoBadge.hide(false);
                        break;
                    case VENUEPICTURE:
                        if (AppApplication.systemLanguage == 1) {
                            goVenuepicture(rowBean.getIconName());
                        } else {
                            goVenuepicture(rowBean.getIconEnName());
                        }
                        break;
                    //日程预览图
                    case SCHEDULE_PREVIEW:
                        goSchedulePreview();
                        break;
                    default:
                        ToastUtils.showToast("未找到该模块，请尝试更新数据包");
                        break;
                }
            } else {
                String url;
                if (Integer.valueOf(rowBean.getIconCode()) == DEMAND) {
                    url = "http://app.incongress.cn/webapp/discussion/CIT2017H5/sessionNowList.html?";
                } else {
                    url = rowBean.getNewModel();
                }

                if (url.contains("appOpenCheckLogin")) {
                    if (SharePreferenceUtils.getUserBoolean(Constants.USER_IS_LOGIN, false)) {
                        if (AppApplication.systemLanguage == 1) {
                            CollegeActivity.startCitCollegeActivity(getActivity(), rowBean.getIconName(), url);
                        } else {
                            CollegeActivity.startCitCollegeActivity(getActivity(), rowBean.getIconEnName(), url);
                        }
                    } else {
                        Intent intent = new Intent(getActivity(), LoginActivity.class);
                        getActivity().startActivity(intent);
                    }
                } else {
                    if (AppApplication.systemLanguage == 1) {
                        CollegeActivity.startCitCollegeActivity(getActivity(), rowBean.getIconName(), url);
                    } else {
                        CollegeActivity.startCitCollegeActivity(getActivity(), rowBean.getIconEnName(), url);
                    }
                }

            }
        }

    }

    private void uploadClick(String modelName) {
        CHYHttpClientUsage.getInstanse().doChyModuleTongji(modelName, new JsonHttpResponseHandler(Constants.ENCODING_GBK) {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
            }
        });
    }
    //=======================================================================模块跳转=======================================================================

    /**
     * 看日程
     */
    public void goLookSchedule(String title) {
        MeetingScheduleListActionFragment listFragment = new MeetingScheduleListActionFragment();
        ImageView view = (ImageView) CommonUtils.initView(getActivity(), R.layout.title_right_image);
        if (AppApplication.systemLanguage == 1) {
            view.setImageResource(R.drawable.schedule_switch_cn);
        } else {
            view.setImageResource(R.drawable.schedule_switch);
        }

        listFragment.setRightListener(view);
        action(listFragment, view);
    }

    /**
     * 查日程(包含讲者检索)
     */
    private void goSearchSchedule(String title) {
        ImageView searchView = (ImageView) CommonUtils.initView(getActivity(), R.layout.title_right_image);
        searchView.setImageResource(R.drawable.search);
        NewSearchScheduleActionFragment searchFragment = new NewSearchScheduleActionFragment();
        searchFragment.setRightView(searchView);
        /*View titleView = CommonUtils.initView(getActivity(), R.layout.title_segment);
        searchFragment.setCenterView(titleView);*/
        //goQuestions("提问");
        //只有日程搜索
        SegmentScheduleActionFragment fragment = new SegmentScheduleActionFragment();
        action(fragment, null);
        //action(searchFragment, title, searchView, false, false, false);
    }

    /**
     * 我的日程
     */
    private void goMySchedule(String title) {
        TextView my_schedule = (TextView) CommonUtils.initView(getActivity(), R.layout.title_right_textview);
        my_schedule.setText(R.string.my_schedule_edit);
        MyScheduleActionFragment schedule = new MyScheduleActionFragment();
        schedule.setRightView(my_schedule);
        action(schedule, title, my_schedule, false, false, false);
    }

    /**
     * 现场秀
     */
    private void goScenicXiu(String title) {
        View scenicTitle = CommonUtils.initView(this.getActivity(), R.layout.scenic_xiu_title);
        LinearLayout mlayout = (LinearLayout) scenicTitle.findViewById(R.id.ll_senic_xiu_title);
        ScenicXiuFragment xiu = new ScenicXiuFragment();
        xiu.setScenicXiuTitle(mlayout);
        action(xiu, title, scenicTitle, false, false, false);
    }

    /**
     * 老直播
     */
    private void goOldLive() {
        getActivity().startActivity(new Intent(getActivity(), CitLiveFragment.class));
    }

    /**
     * 新直播
     */
    public void goLive(String titleName) {
        action(new LiveFragment(), titleName, false, false, false);
    }

    /**
     * 消息站
     */
    private void goMessageStation() {
        action(new MessageStationActionFragment(), R.string.home_messagestation, false, false, false);
    }

    /**
     * 正在进行
     */
    private void goNow(String title) {
        NowFragment now = new NowFragment();
        action(now, title, false, false, false);
    }

    /**
     * 即将进行
     */
    private void goNext(String title) {
        NextFragment next = new NextFragment();
        action(next, title, false, false, false);
    }

    /**
     * 专家秘书
     */
    private void goSecretary() {
        CHYHttpClientUsage.getInstanse().doGetSceneShowQuestions(Constants.getConId() + "", AppApplication.facultyId + "", "-1", AppApplication.getSystemLanuageCode(), new JsonHttpResponseHandler("gbk") {
            @Override
            public void onStart() {
                super.onStart();
                mQuestionNum = 0;
                mTaskNum = 0;
            }

            @Override
            public void onFinish() {
                super.onFinish();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                MyLogger.jLog().i(response.toString());
                try {
                    int state = response.getInt("state");
                    if (state == 0) {
                        mQuestionNum = 0;
                    } else {
                        JSONArray jsonArray = response.getJSONArray("sceneShowArray");
                        Gson gson = new Gson();
                        mScenceShowBeans = gson.fromJson(jsonArray.toString(), new TypeToken<ArrayList<SceneShowArrayBean>>() {
                        }.getType());
                        mQuestionNum = response.getInt("questionCount");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });
        new TaskAsyncTask().execute();
    }

    /**
     * 新专家秘书
     */
    private void goScretary2019() {
        action(new ScretaryProfessorFragment(), getString(R.string.secretary_fragment_title), false, false, false);
    }

    /**
     * 参会指南
     */
    private void goGuide(String title) {
        if (AppApplication.instance().NetWorkIsOpen()) {
            ImageView searchView = (ImageView) CommonUtils.initView(getActivity(), R.layout.title_right_image);
            searchView.setImageResource(R.drawable.icon_share);
            PicUtils.setImageViewColor(searchView, R.color.back_color);
            searchView.setVisibility(Constants.PARTY_GUIDE_SHARE ? View.VISIBLE : View.GONE);
            OnlyWebViewActionFragment fragment = OnlyWebViewActionFragment.getInstance(getActivity().getString(Constants.get_MEETING_GUIDE(), Constants.getConId(), AppApplication.getSystemLanuageCode()));
            fragment.setRightView(searchView);
            action(fragment, R.string.home_meetingguide, searchView, false, false, false);
        } else {
            Toast.makeText(getActivity(), R.string.nowifi, Toast.LENGTH_SHORT).show();
        }
    }

    //场馆图
    private void goVenuepicture(String title) {
        action(new NewMeetingInfoFragment(), title, false, false, false);
    }

    //日程预览图
    private void goSchedulePreview() {
        MeetingScheduleViewPageFragment scheduleFragment = new MeetingScheduleViewPageFragment();
        HomeActivity activity = (HomeActivity) getActivity();
        activity.addFragment(NewHomeCurrentFragment.this, scheduleFragment);
        activity.setTitleEntry(false, false, false, null, null, false, false, false, false);
    }

    /**
     * 现场互动
     */
    private void goQuestionAndA(String title) {
        if (SharePreferenceUtils.getUserBoolean(Constants.USER_IS_LOGIN, false)) {
            HdSessionActionFragment hdFragment = new HdSessionActionFragment();
            View hdTtile = CommonUtils.initView(this.getActivity(), R.layout.title_hdsession);
            hdFragment.setRightListener(hdTtile);
            action(hdFragment, title, hdTtile, false, false, false);
        } else {
            LoginActivity.startLoginActivity(getActivity(), LoginActivity.TYPE_NORMAL, "", "", "", "");
        }
    }

    /**
     * 提问模块
     */
    public void goQuestions(String title) {
        if (SharePreferenceUtils.getUserBoolean(Constants.USER_IS_LOGIN, false)) {
            action(new QuestionSquarFragment(), title, false, false, false);
        } else {
            LoginActivity.startLoginActivity(getActivity(), LoginActivity.TYPE_NORMAL, "", "", "", "");
        }
        // action(new QuestionsFragment(), R.string.question, false, false, false);
    }

    /**
     * 实习中心
     */
    private void goHandsOn() {
        String lan2 = "";
        if (AppApplication.systemLanguage == 1) {
            lan2 = "cn";
        } else {
            lan2 = "en";
        }
        CollegeActivity.startCitCollegeActivity(getActivity(), getString(R.string.home_hands_on),
                getString(Constants.get_HANDS_ON_SITE(), Constants.getConId(), lan2, AppApplication.userId, AppApplication.userType));

    }

    /**
     * 壁报
     */
    private void goPost(String title) {
        View scane = CommonUtils.initView(this.getActivity(), R.layout.title_right_image);
        ((ImageView) scane).setImageResource(R.drawable.scane_scane);
        PosterFragment post = new PosterFragment();
        post.setRightView(scane);
        action(post, title, scane, false, false, false);
    }

    /**
     * 参展商
     */
    public void goExhibitor(String title) {
        //action(new ExhibitorsActionFragment(), title, false, false, false);
        action(new NewExhibitorsActionFragment(), title, false, false, false);
    }

    /**
     * 照片墙
     */
    private void goPhotoAlbum(String title) {
        action(new PhotoAlbumFragment(), title, false, false, false);
    }

    /**
     * 班车提醒
     */
    public void goBus(String title) {
        action(new MeetingBusRemindAllFragment(), title, false, false, false);
    }

    /**
     * 我(个人中心)
     */
    private void goPersonCenter() {
        action(new PersonCenterFragment(), R.string.home_me, false, false, true);
    }

    /**
     * 学院
     */
    public void goCollege(String titleName) {
        /*View titleView = CommonUtils.initView(getActivity(), R.layout.title_segment);
        searchFragment.setCenterView(titleView);*/
        //goQuestions("提问");
        //只有日程搜索
        action(CollegeHomeFragment.getInstance(titleName), null);
        //action(searchFragment, title, searchView, false, false, false);
    }

    /**
     * 讲者检索
     */
    private void goSpeakerSearch(String title) {
        action(SpeakerSearchFragment.getInstance(SpeakerSearchFragment.TYPE_FROM_HOME), title, false, false, false);
    }

    /**
     * 扫一扫
     */
    public void goScane() {
        if (!SharePreferenceUtils.getAppBoolean(Constants.QRCODE_SCAN_SWITCH, false)) {
            SharePreferenceUtils.saveAppBoolean(Constants.QRCODE_SCAN_SWITCH, true);
            AlertDialog.Builder builder2 = new AlertDialog.Builder(getActivity());
            builder2.setTitle(R.string.qr_code_title).setMessage(R.string.qr_code_tips).setPositiveButton(R.string.positive_button, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent intent = new Intent(getActivity(), CaptureActivity.class);
                    getActivity().startActivityForResult(intent, HomeActivity.REQUEST_SCANE);
                }
            }).show();
        } else {
            Intent intent = new Intent(getActivity(), CaptureActivity.class);
            getActivity().startActivityForResult(intent, HomeActivity.REQUEST_SCANE);
        }

    }

    /**
     * 扫一扫
     */
    private void goQRScane() {
        StatusBarUtil.setStatusBarDarkTheme(getActivity(), false);
        Intent intent = new Intent(getActivity(), QRCodeCaptureActivity.class);
        getActivity().startActivity(intent);
    }

    //读SD中的文件
    public String readFileSdcardFile(String fileName) throws IOException {
        String res = "";
        try {
            FileInputStream fin = new FileInputStream(fileName);

            int length = fin.available();

            byte[] buffer = new byte[length];
            fin.read(buffer);
            res = EncodingUtils.getString(buffer, "UTF-8");
            fin.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return res;
    }

    class TaskAsyncTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mValidActivitys.clear();
            mAllActivitys.clear();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            if (AppApplication.userType == Constants.TYPE_USER_VISITOR) {
                LoginActivity.startLoginActivity(getActivity(), LoginActivity.TYPE_PROFESSOR, "", "", "", "");
                return;
            } else if (AppApplication.facultyId == -1) {
                ToastUtils.showToast(getString(R.string.secretary_module_not_available));
                StatusBarUtil.setStatusBarDarkTheme(getActivity(), false);
                return;
            }

            SecretaryActivity.startSecretaryActivity(getActivity(), mTaskNum, mQuestionNum, mValidActivitys, mScenceShowBeans);
        }

        @Override
        protected Void doInBackground(Void... params) {
            if (AppApplication.userType == Constants.TYPE_USER_VISITOR) {
                return null;
            } else if (AppApplication.facultyId == -1) {
                return null;
            }

            mAllActivitys = ConferenceDbUtils.getSessionAndMeetingBySpeakerId(AppApplication.facultyId);

            // 处理过期时间下的活动
            int size = mAllActivitys.size();
            Date date = new Date();
            String current_day = DateUtil.getNowDate(DateUtil.DEFAULT);
            String currentTime = current_day + " 00:00:00";
            Date currentDate = DateUtil.getDate(currentTime, DateUtil.DEFAULT_SECOND);
            long currentSecond = currentDate.getTime();

            for (int i = 0; i < size; i++) {
                String time = mAllActivitys.get(i).getTime() + ":00";
                date = DateUtil.getDate(time, DateUtil.DEFAULT_SECOND);
                long second = date.getTime();
                if (second > currentSecond) {
                    mValidActivitys.add(mAllActivitys.get(i));
                }
            }

            //按照时间排序。
            ArrayUtils.quickSort(mValidActivitys);
            mTaskNum = mValidActivitys.size();
            return null;
        }
    }

    private Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message message) {
            switch (message.what) {
                case 1:
                    zk.performClick();
                    break;
            }
            return false;
        }
    });

    private ValueAnimator createDropAnimator(final View v, int start, int end) {
        ValueAnimator animator = ValueAnimator.ofInt(start, end);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

            @Override
            public void onAnimationUpdate(ValueAnimator arg0) {
                int value = (int) arg0.getAnimatedValue();
                ViewGroup.LayoutParams layoutParams = v.getLayoutParams();
                layoutParams.height = value;
                v.setLayoutParams(layoutParams);
            }
        });
        return animator;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!isBackView) {
            StatusBarUtil.setStatusBarDarkTheme(getActivity(), false);
        }
        MobclickAgent.onPageStart(Constants.FRAGMENT_DYNAMICHOME);
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd(Constants.FRAGMENT_DYNAMICHOME);
    }

    //设置收缩高度
    private void setHeightAnimator(int height) {
        int mLayoutHeight = mLlConstainer.getHeight();
        ValueAnimator valueAnimator = createDropAnimator(mLlConstainer, mLayoutHeight, height);
        valueAnimator.setDuration(1500);
        valueAnimator.start();
        currentSize = height;
    }

    //根据icUserID获取用户信息
    private void doGetUserInfo(String icUserId) {
        CHYHttpClientUsage.getInstanse().doGetMobileUserInfoByIcUserId(icUserId, LanguageUtil.getCurrentLan(getActivity()), new JsonHttpResponseHandler(Constants.ENCODING_GBK) {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                if (JSONCatch.parseInt("state", response) == 1) {
                    ParseUser.saveUserInfo(response.toString());
                    initJpush();
                    saveToken();
                }
            }
        });
    }

    /**
     * 将JPush的registerId发送给服务端，方便服务端进行推送
     */
    private void initJpush() {
        final String registrationID = JPushInterface.getRegistrationID(getActivity());
        Log.d("sgqTest", "initJpush: " + registrationID);
        CHYHttpClientUsage.getInstanse().doSendToken(SharePreferenceUtils.getUser(Constants.CONID) + "", Constants.TYPE_ANDROID, registrationID, SharePreferenceUtils.getUser(Constants.USER_ID) + "", new JsonHttpResponseHandler("gbk") {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
            }
        });
    }
    private void saveToken() {
        String token = SharePreferenceUtils.getUser(Constants.USER_RONG_TOKEN);
        if (TextUtils.isEmpty(token)) {
            CHYHttpClientUsage.getInstanse().doGetToken(AppApplication.userId, new JsonHttpResponseHandler(Constants.ENCODING_GBK) {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    super.onSuccess(statusCode, headers, response);
                    try {
                        int state = response.getInt("state");
                        if (state == 1) {
                            String tokenRes = response.getString("token");
                            if (!cz.msebera.android.httpclient.util.TextUtils.isEmpty(tokenRes)) {
                                SharePreferenceUtils.saveUserString(Constants.USER_RONG_TOKEN, tokenRes);
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                    super.onFailure(statusCode, headers, throwable, errorResponse);
                    LogUtils.println("response:" + errorResponse);
                }
            });
        }
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
                photoNumber = JSONCatch.parseInt("photoWallCount", response);
                sessionNumber = JSONCatch.parseInt("coursewareCount", response);
                questionNumber = JSONCatch.parseInt("questionCount", response);
                for (int i = 0; i < iconCodeBeans.size(); i++) {
                    if (iconCodeBeans.get(i).getIconCode() == PHOTOWALL) {
                        photoBadge = new QBadgeView(getContext()).bindTarget(iconCodeBeans.get(i).getTextView()).setBadgeNumber(photoNumber).setBadgeGravity(Gravity.TOP | Gravity.END).setBadgeTextSize(9, true).setBadgePadding(-0.00005f, true).stroke(getResources().getColor(R.color.remind_cycle_color), 2, true).setBadgeBackgroundColor(getResources().getColor(R.color.remind_cycle_color));//.setBadgeNumber()
                    } else if (iconCodeBeans.get(i).getIconCode() == DEMAND) {
                        sessionBadge = new QBadgeView(getContext()).bindTarget(iconCodeBeans.get(i).getTextView()).setBadgeNumber(sessionNumber).setBadgeGravity(Gravity.TOP | Gravity.END).setBadgeTextSize(9, true).setBadgePadding(-0.00005f, true).stroke(getResources().getColor(R.color.remind_cycle_color), 2, true).setBadgeBackgroundColor(getResources().getColor(R.color.remind_cycle_color));//.setBadgeNumber()
                    }else if (iconCodeBeans.get(i).getIconCode() == QUESTION) {
                        questionBadge = new QBadgeView(getContext()).bindTarget(iconCodeBeans.get(i).getTextView()).setBadgeNumber(questionNumber).setBadgeGravity(Gravity.TOP | Gravity.END).setBadgeTextSize(9, true).setBadgePadding(-0.00005f, true).stroke(getResources().getColor(R.color.remind_cycle_color), 2, true).setBadgeBackgroundColor(getResources().getColor(R.color.remind_cycle_color));//.setBadgeNumber()
                    }
                }
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
    public void postUNReadQuestionNumber(boolean isCompass,int moduleNo) {
        CHYHttpClientUsage.getInstanse().doPostUNReadQuestion(isCompass,moduleNo, new JsonHttpResponseHandler(Constants.ENCODING_GBK) {
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
}
