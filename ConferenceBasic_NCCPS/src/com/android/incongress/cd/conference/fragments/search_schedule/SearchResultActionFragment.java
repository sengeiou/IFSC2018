package com.android.incongress.cd.conference.fragments.search_schedule;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.android.incongress.cd.conference.adapters.SearchResultAdapter;
import com.android.incongress.cd.conference.base.AppApplication;
import com.android.incongress.cd.conference.base.BaseFragment;
import com.android.incongress.cd.conference.base.Constants;
import com.android.incongress.cd.conference.fragments.meeting_schedule.SessionDetailViewPageFragment;
import com.android.incongress.cd.conference.model.Class;
import com.android.incongress.cd.conference.model.ConferenceDbUtils;
import com.android.incongress.cd.conference.model.Meeting;
import com.android.incongress.cd.conference.model.Session;
import com.mobile.incongress.cd.conference.basic.csccm.R;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jacky on 2016/3/11.
 * 查询结果
 */
public class SearchResultActionFragment extends BaseFragment implements View.OnClickListener{
    private ListView mLvSearchResult;
    private SearchResultAdapter mAdapter;
    private List<Session> mSessionBeans;
    private List<Meeting> mMeetingBeans;
    private List<Class> mClasses;

    private TextView mTitle,mDay,mTime,mRoom;
    private TextView mTvNoDataTips,mResultsTips;

    private List<Session> mSessionList = new ArrayList<>();

    private static String BUNDLE_SEARCH_DAY = "searchDay";
    private static String BUNDLE_SEARCH_ROOM = "searchRoom";
    private static String BUNDLE_SEARCH_ROOM_NAME = "searchRoomName";
    private static String BUNDLE_SEARCH_START_TIME = "searchStartTime";
    private static String BUNDLE_SEARCH_END_TIME = "searchEndTime";

    private String mSearchDay = "";
    private String mSearchRoom = "";
    private String mSearchRoomName = "";
    private String mSearchStartTime = "";
    private String mSearchEndTime = "";

    public static final SearchResultActionFragment getInstance(String searchDay, String searchRoom, String searchRoomName, String searchStartTime, String searchEndTime) {
        SearchResultActionFragment fragment = new SearchResultActionFragment();
        Bundle bundle = new Bundle();
        bundle.putString(BUNDLE_SEARCH_DAY, searchDay);
        bundle.putString(BUNDLE_SEARCH_ROOM, searchRoom);
        bundle.putString(BUNDLE_SEARCH_ROOM_NAME, searchRoomName);
        bundle.putString(BUNDLE_SEARCH_START_TIME, searchStartTime);
        bundle.putString(BUNDLE_SEARCH_END_TIME, searchEndTime);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search_result, null);
        Bundle bundle = getArguments();

        mSearchDay = bundle.getString(BUNDLE_SEARCH_DAY);
        mSearchRoom = bundle.getString(BUNDLE_SEARCH_ROOM);
        mSearchRoomName = bundle.getString(BUNDLE_SEARCH_ROOM_NAME);
        mSearchStartTime = bundle.getString(BUNDLE_SEARCH_START_TIME);
        mSearchEndTime = bundle.getString(BUNDLE_SEARCH_END_TIME);

        mSessionBeans = ConferenceDbUtils.getSessionByTimeAndRoom(mSearchDay,mSearchRoom,mSearchStartTime,mSearchEndTime);
        mMeetingBeans = ConferenceDbUtils.getMeetingBySessions(mSessionBeans);
        mClasses = ConferenceDbUtils.getAllClasses();
        mDay = (TextView) view.findViewById(R.id.session_day);
        mTime = (TextView) view.findViewById(R.id.session_time);
        mRoom = (TextView) view.findViewById(R.id.session_room);
        mTitle = (TextView) getActivity().findViewById(R.id.title_text);
        mTvNoDataTips = (TextView) view.findViewById(R.id.tv_tips);
        mResultsTips = (TextView) view.findViewById(R.id.tips_results);
        mLvSearchResult = (ListView) view.findViewById(R.id.lv_search_result);
        mDay.setText(mSearchDay.subSequence(5, 10));
        mTime.setText(mSearchStartTime+" - "+mSearchEndTime);
        mRoom.setText(mSearchRoomName);
        mDay.setOnClickListener(this);
        mTime.setOnClickListener(this);
        mRoom.setOnClickListener(this);
        mSessionList.addAll(ConferenceDbUtils.getAllSession());
        if(mSessionBeans.size() == 0) {
            mTvNoDataTips.setVisibility(View.VISIBLE);
            mLvSearchResult.setVisibility(View.GONE);
            mResultsTips.setVisibility(View.GONE);
        }else {
            mResultsTips.setVisibility(View.VISIBLE);
            mAdapter = new SearchResultAdapter(getActivity(), mSessionBeans, mMeetingBeans, mClasses);
            mLvSearchResult.setAdapter(mAdapter);
        }
        mLvSearchResult.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Session session = mSessionBeans.get(position);
                SessionDetailViewPageFragment detail = new SessionDetailViewPageFragment();
                detail.setArguments(getSessionPosition(session.getSessionGroupId()), mSessionList);
                action(detail, R.string.meeting_schedule_detail_title, false, false, false);
            }
        });
        return view;
    }



    /**
     * 获取Meeting在session中的位置
     * @param sessionGroupId
     * @return
     */
    private int getSessionPosition(int sessionGroupId) {
        for (int i = 0; i < mSessionList.size(); i++) {
            Session bean = mSessionList.get(i);
            if (bean.getSessionGroupId() == sessionGroupId) {
                return i;
            }
        }
        return 0;
    }

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart(Constants.FRAGMENT_SEARCHRESULT);
    }


    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd(Constants.FRAGMENT_SEARCHRESULT);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.session_day:
                mTvNoDataTips.setVisibility(View.GONE);
                mLvSearchResult.setVisibility(View.VISIBLE);
                mDay.setVisibility(View.GONE);
                mSearchDay = "";
                mSessionBeans = ConferenceDbUtils.getSessionByTimeAndRoom(mSearchDay,mSearchRoom,mSearchStartTime,mSearchEndTime);
                mAdapter = new SearchResultAdapter(getActivity(), mSessionBeans, mMeetingBeans, mClasses);
                mLvSearchResult.setAdapter(mAdapter);
                if(AppApplication.systemLanguage == 1){
                    mTitle.setText("检索结果("+mSessionBeans.size()+")");
                }else{
                    mTitle.setText("Search result("+mSessionBeans.size()+")");
                }

                break;
            case R.id.session_time:
                mTvNoDataTips.setVisibility(View.GONE);
                mLvSearchResult.setVisibility(View.VISIBLE);
                mTime.setVisibility(View.GONE);
                mSearchStartTime = "06:00";
                mSearchEndTime = "24:00";
                mSessionBeans = ConferenceDbUtils.getSessionByTimeAndRoom(mSearchDay,mSearchRoom,mSearchStartTime,mSearchEndTime);
                mAdapter = new SearchResultAdapter(getActivity(), mSessionBeans, mMeetingBeans, mClasses);
                mLvSearchResult.setAdapter(mAdapter);
                if(AppApplication.systemLanguage == 1){
                    mTitle.setText("检索结果("+mSessionBeans.size()+")");
                }else{
                    mTitle.setText("Search result("+mSessionBeans.size()+")");
                }
                break;
            case R.id.session_room:
                mTvNoDataTips.setVisibility(View.GONE);
                mLvSearchResult.setVisibility(View.VISIBLE);
                mRoom.setVisibility(View.GONE);
                mSearchRoom = "";
                mSessionBeans = ConferenceDbUtils.getSessionByTimeAndRoom(mSearchDay,mSearchRoom,mSearchStartTime,mSearchEndTime);
                mAdapter = new SearchResultAdapter(getActivity(), mSessionBeans, mMeetingBeans, mClasses);
                mLvSearchResult.setAdapter(mAdapter);
                if(AppApplication.systemLanguage == 1){
                    mTitle.setText("检索结果("+mSessionBeans.size()+")");
                }else{
                    mTitle.setText("Search result("+mSessionBeans.size()+")");
                }
                break;
        }
    }
}