package com.android.incongress.cd.conference.fragments.meeting_schedule;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.incongress.cd.conference.HomeActivity;
import com.android.incongress.cd.conference.adapters.MeetingScheduleListFragmentAdapter;
import com.android.incongress.cd.conference.base.AppApplication;
import com.android.incongress.cd.conference.base.BaseFragment;
import com.android.incongress.cd.conference.base.Constants;
import com.android.incongress.cd.conference.fragments.search_schedule.SegmentScheduleActionFragment;
import com.android.incongress.cd.conference.model.ConferenceDbUtils;
import com.android.incongress.cd.conference.model.Session;
import com.android.incongress.cd.conference.save.SharePreferenceUtils;
import com.android.incongress.cd.conference.utils.DateUtil;
import com.android.incongress.cd.conference.utils.DensityUtil;
import com.android.incongress.cd.conference.utils.TimeUtils;
import com.android.incongress.cd.conference.widget.MyViewPager;
import com.android.incongress.cd.conference.widget.StatusBarUtil;
import com.android.incongress.cd.conference.widget.popup.ChooseTimePopupWindow;
import com.mobile.incongress.cd.conference.basic.csccm.R;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.List;
import java.util.zip.Inflater;

/**
 * Created by Jacky on 2016/1/31.
 */
public class MeetingScheduleListActionFragment extends BaseFragment {
    private MyViewPager mViewPager;
    private MeetingScheduleListFragmentAdapter mPageAdapter;
    private ArrayList<String> mSessionDaysList = new ArrayList<>();
    private ArrayList<String> newSessionDaysList = new ArrayList<>();
    private TextView mTvTips, mSelectTime;
    private LinearLayout linearLayout, ll_title;
    private RelativeLayout rl_select_time;
    private ImageView title_back, iv_search;
    private int mCurrentPage = 0;
    private ListAdapter listAdapter;
    private ChooseTimePopupWindow popupWindow;
    private static float ScreenHeightLPercent = 0.35f;
    private static float ScreenHeightHPercent = 0.45f;
    private float fixHeight;
    //参数为了在切换到activity返回后，fragment重新设置导航栏字体颜色
    private boolean isBackView = true;
    private View guide_view;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        StatusBarUtil.setStatusBarDarkTheme(getActivity(), true);
        View view = inflater.inflate(R.layout.fragment_meeting_schedule_list, null, false);
        rl_select_time = view.findViewById(R.id.rl_select_time);
        mViewPager = view.findViewById(R.id.viewpager);
        title_back = view.findViewById(R.id.title_back);
        iv_search = view.findViewById(R.id.iv_search);
        mTvTips = view.findViewById(R.id.tv_tips);
        mSelectTime = view.findViewById(R.id.tv_select_time);
        linearLayout = view.findViewById(R.id.ll_show_on_off);
        ll_title = view.findViewById(R.id.ll_title);
        rl_select_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                initPopupWindow(ll_title);
            }
        });
        title_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((HomeActivity) getActivity()).performBackClick();
            }
        });
        iv_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goSearchSchedule(getResources().getString(R.string.home_search_schedule));
            }
        });

        getSessionDays();
        newSessionDaysList = getStringList();
        if (mSessionDaysList.size() != 0) {
            mSelectTime.setText(newSessionDaysList.get(0));
        }
        mPageAdapter = new MeetingScheduleListFragmentAdapter(getChildFragmentManager(), mSessionDaysList);
        mViewPager.setScrollble(true);
        mViewPager.setAdapter(mPageAdapter);
        mViewPager.setOffscreenPageLimit(3);
        for (int i = 0; i < mSessionDaysList.size(); i++) {
            if (TimeUtils.getCurrentTimeMD().equals(mSessionDaysList.get(i))) {
                mCurrentPage = i;
                mSelectTime.setText(newSessionDaysList.get(mCurrentPage));
            }
        }
        mViewPager.setCurrentItem(mCurrentPage, false);
        if (AppApplication.systemLanguage == 1) {
            iv_search.setImageResource(R.drawable.guide_search_ch);
        } else {
            iv_search.setImageResource(R.drawable.guide_search_en);
        }
        if(!SharePreferenceUtils.getAppBoolean(Constants.MEETING_SHAPE_GUIDE,false)){
            addGuideView();
            SharePreferenceUtils.saveAppBoolean(Constants.MEETING_SHAPE_GUIDE,true);
        }
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                mCurrentPage = position;
                mSelectTime.setText(newSessionDaysList.get(mCurrentPage));
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        return view;
    }



    private void getSessionDays() {
        mSessionDaysList.clear();
        List<Session> allSession = ConferenceDbUtils.getAllSession();
        for (int i = 0; i < allSession.size(); i++) {
            Session session = allSession.get(i);
            if (mSessionDaysList.size() > 0) {
                if (!(mSessionDaysList.get(mSessionDaysList.size() - 1).equals(session.getSessionDay()))) {
                    mSessionDaysList.add(session.getSessionDay());
                }
            } else {
                mSessionDaysList.add(session.getSessionDay());
            }
        }
//        if(mSessionDaysList.size()==0){
//            mTvTips.setVisibility(View.VISIBLE);
//        }else {
//            mTvTips.setVisibility(View.GONE);
//        }
    }

    private ArrayList<String> getStringList() {
        ArrayList<String> list = new ArrayList<>();
        for (int i = 0; i < mSessionDaysList.size(); i++) {
            if(AppApplication.systemLanguage == 1){
                list.add(DateUtil.getFormatMonthAndDayChinese(mSessionDaysList.get(i)));
            }else {
                list.add(DateUtil.getFormatMonthAndDayEnglish(mSessionDaysList.get(i)));
            }
        }
        return list;
    }

    public void setRightListener(View view) {
//        view.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if(getActivity()!= null) {
//                    ((HomeActivity)getActivity()).performBackClick();
//                }
//            }
//        });

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*ImageView view = (ImageView) CommonUtils.initView(getActivity(), R.layout.title_right_image);
                if (AppApplication.systemLanguage == 1) {
                    view.setImageResource(R.drawable.schedule_switch_cn);
                } else {
                    view.setImageResource(R.drawable.schedule_switch);
                }
                MeetingScheduleViewPageFragment scheduleFragment = new MeetingScheduleViewPageFragment();
                scheduleFragment.setRightListener(view);
                //getActivity().startActivity(new Intent(getActivity(),MeetingScheduleDetailActivity.class));*/
                MeetingScheduleViewPageFragment scheduleFragment = new MeetingScheduleViewPageFragment();
                HomeActivity activity = (HomeActivity) getActivity();
                activity.addFragment(MeetingScheduleListActionFragment.this, scheduleFragment);
                activity.setTitleEntry(false, false, false, null, null, false, false, false, false);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!isBackView) {
            StatusBarUtil.setStatusBarDarkTheme(getActivity(), true);
        }
        getActivity().findViewById(R.id.title_back_panel).setVisibility(View.VISIBLE);
        MobclickAgent.onPageStart(Constants.FRAGMENT_MEETINGSCHEDULELIST);
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        isBackView = hidden;
        if (!hidden) {
            StatusBarUtil.setStatusBarDarkTheme(getActivity(), true);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        getActivity().findViewById(R.id.title_back_panel).setVisibility(View.VISIBLE);
        MobclickAgent.onPageEnd(Constants.FRAGMENT_MEETINGSCHEDULELIST);
    }

    //创建popupwindow
    private void initPopupWindow(View view) {
        popupWindow = new ChooseTimePopupWindow(getActivity());
        ListView listView = popupWindow.getmListView();
        listAdapter = new ListAdapter(getActivity(), newSessionDaysList);
        listAdapter.setCurrentItem(mCurrentPage);
        listView.setAdapter(listAdapter);
        int totalHeight = 0;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0); //计算子项View 的宽高 //统计所有子项的总高度
            totalHeight += listItem.getMeasuredHeight() + listView.getDividerHeight();
        }
        if (DensityUtil.getScreenSize(getActivity())[1] <= 1920) {
            fixHeight = DensityUtil.getScreenSize(getActivity())[1] * ScreenHeightLPercent;
        } else {
            fixHeight = DensityUtil.getScreenSize(getActivity())[1] * ScreenHeightHPercent;
        }
        if (totalHeight > fixHeight) {
            totalHeight = (int) fixHeight;
        }
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight;
        listView.setLayoutParams(params);
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                lightOn(linearLayout);
            }
        });
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                mCurrentPage = i;
                listAdapter.setCurrentItem(mCurrentPage);
                listAdapter.notifyDataSetChanged();
                mSelectTime.setText(newSessionDaysList.get(mCurrentPage));
                mViewPager.setCurrentItem(mCurrentPage, false);
                popupWindow.dismiss();
            }
        });
        lightOff(linearLayout);
        int offsetX = Math.abs((int) (view.getWidth() * 0.3) - DensityUtil.dip2px(getActivity(), 1.5f));
        //popupWindow.showAsDropDown(view);
        popupWindow.showAsDropDown(view, offsetX, 1, Gravity.CENTER);
    }

    /**
     * 查日程
     */
    private void goSearchSchedule(String title) {
        SegmentScheduleActionFragment fragment = new SegmentScheduleActionFragment();
        action(fragment, null);
    }

    class ListAdapter extends BaseAdapter {
        ArrayList<String> listBeans;
        public Context context;
        public LayoutInflater layoutInflater;

        public ListAdapter(Context context, ArrayList<String> listBeans) {
            this.context = context;
            this.listBeans = listBeans;
            layoutInflater = LayoutInflater.from(context);
        }

        //当前Item被点击的位置
        private int currentItem;

        public void setCurrentItem(int currentItem) {
            this.currentItem = currentItem;
        }

        @Override
        public int getCount() {
            return listBeans.size();
        }

        @Override
        public Object getItem(int position) {
            return listBeans.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            MyTimeHold myHold;
            if (convertView == null) {
                convertView = layoutInflater.inflate(R.layout.my_centertextview, null);
                myHold = new MyTimeHold();
                myHold.tv_time = convertView.findViewById(R.id.tv_time);
                convertView.setTag(myHold);
            } else {
                myHold = (MyTimeHold) convertView.getTag();
            }
            if (currentItem == position) {
                myHold.tv_time.setSelected(true);
            } else {
                myHold.tv_time.setSelected(false);
            }
            myHold.tv_time.setText(listBeans.get(position));
            return convertView;
        }

        class MyTimeHold {
            TextView tv_time;
        }
    }
    //添加蒙层
    private void addGuideView() {
        ImageView iv_guide_search, iv_guide_show;
        guide_view = LayoutInflater.from(getActivity()).inflate(R.layout.activity_guide_view, null);
        iv_guide_search = guide_view.findViewById(R.id.iv_guide_search);
        iv_guide_show = guide_view.findViewById(R.id.iv_guide_show);
        if (AppApplication.systemLanguage == 1) {
            iv_guide_search.setImageResource(R.drawable.guide_search_shape_ch);
            iv_guide_show.setImageResource(R.drawable.guide_search_show_ch);
        } else {
            iv_guide_search.setImageResource(R.drawable.guide_search_shape_en);
            iv_guide_show.setImageResource(R.drawable.guide_search_show_en);
        }
        guide_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.setVisibility(View.GONE);
            }
        });
        addLayer(getActivity(), guide_view);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(guide_view!=null){
            guide_view.setVisibility(View.GONE);
            guide_view = null;
        }
    }

    /**
     * 为rootView添加蒙层
     *
     * @return
     */
    public static void addLayer(Activity mContext, View layerView) {
        if (mContext == null || layerView == null)
            return;
        ViewGroup contentView = (ViewGroup) mContext.getWindow().findViewById(Window.ID_ANDROID_CONTENT);
        contentView.addView(layerView);
    }
}
