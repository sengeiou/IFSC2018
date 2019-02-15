package com.android.incongress.cd.conference.fragments.question;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.TextView;

import com.android.incongress.cd.conference.adapters.AskQuestionAdapter;
import com.android.incongress.cd.conference.adapters.MyQuestionsSquarAdapter;
import com.android.incongress.cd.conference.adapters.QuestionsSquarAdapter;
import com.android.incongress.cd.conference.api.CHYHttpClientUsage;
import com.android.incongress.cd.conference.base.BaseFragment;
import com.android.incongress.cd.conference.base.Constants;
import com.android.incongress.cd.conference.beans.AskQuestionListBean;
import com.android.incongress.cd.conference.beans.MyMeetingQuestion;
import com.android.incongress.cd.conference.beans.QuestionReplyBean;
import com.android.incongress.cd.conference.utils.CommonUtils;
import com.android.incongress.cd.conference.utils.DensityUtil;
import com.android.incongress.cd.conference.utils.ToastUtils;
import com.android.incongress.cd.conference.widget.StatusBarUtil;
import com.android.incongress.cd.conference.widget.popup.QuestionPopupWindow;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.jcodecraeer.xrecyclerview.ProgressStyle;
import com.jcodecraeer.xrecyclerview.XRecyclerView;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.mobile.incongress.cd.conference.basic.csccm.R;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;

/**
 * Created by Jacky on 2016/12/22.
 */

public class MyQuestionSquarFragment extends BaseFragment implements MyQuestionsSquarAdapter.QuestionsAnswerClick{
    private XRecyclerView mRVQuestion;
    private View emptyView;
    private QuestionReplyBean mQuestionReply;
    private MyQuestionsSquarAdapter mQuestionAdapter;
    private LinearLayout ll_question;
    private List<QuestionReplyBean.SceneShowArrayBean> myList = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_squar_question, container,false);
        mRVQuestion =  view.findViewById(R.id.rv_question);
        emptyView = view.findViewById(R.id.tv_tips);
        ll_question = view.findViewById(R.id.ll_question);
        ll_question.setVisibility(View.GONE);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRVQuestion.setLayoutManager(layoutManager);

        mRVQuestion.setRefreshProgressStyle(ProgressStyle.BallSpinFadeLoader);
        mRVQuestion.setLoadingMoreProgressStyle(ProgressStyle.BallRotate);
        mRVQuestion.setArrowImageView(R.drawable.iconfont_downgrey);
        mQuestionAdapter = new MyQuestionsSquarAdapter(getActivity(), myList,this);
        mRVQuestion.setAdapter(mQuestionAdapter);
        mRVQuestion.setLoadingMoreEnabled(false);

        mRVQuestion.setLoadingListener(new XRecyclerView.LoadingListener() {
            @Override
            public void onRefresh() {
                refreshQuestionData( );
            }

            @Override
            public void onLoadMore() {
            }
        });
        mRVQuestion.setRefreshing(true);
        return view;
    }

    /**
     * 只有下拉刷新
     */
    private void refreshQuestionData() {
        CHYHttpClientUsage.getInstanse().doGetQuestionsByMySession( new JsonHttpResponseHandler(Constants.ENCODING_GBK){

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                try {
                    myList.clear();
                    mQuestionReply = new Gson().fromJson(response.toString(), new TypeToken<QuestionReplyBean>() {
                    }.getType());
                    myList.addAll(mQuestionReply.getSceneShowArray()) ;
                    mQuestionAdapter.notifyDataSetChanged();
                    if(myList.size()<=0){
                        mRVQuestion.setEmptyView(emptyView);
                    }
                }catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFinish() {
                super.onFinish();
                mRVQuestion.refreshComplete();
            }
        });
    }

    @Override
    public void qustionAnswer(int position) {
        View question = CommonUtils.initView(getActivity(), R.layout.title_right_textview);
        MyQuestionAnswerFragment instance = new MyQuestionAnswerFragment();
        QuestionReplyBean.SceneShowArrayBean bean  = mQuestionReply.getSceneShowArray().get(position);
        instance.setMeetingQuestionInfo(bean.getSpeakerName(), 123, 123, bean.getMeetingName());
        instance.setRightListener(question);
        action(instance, getString(R.string.ask_sb, bean.getSpeakerName()), question, false, false, false);
    }

    @Override
    public void onResume() {
        super.onResume();
        StatusBarUtil.setStatusBarDarkTheme(getActivity(), true);
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if(!hidden){
            StatusBarUtil.setStatusBarDarkTheme(getActivity(), true);
        }
    }
}
