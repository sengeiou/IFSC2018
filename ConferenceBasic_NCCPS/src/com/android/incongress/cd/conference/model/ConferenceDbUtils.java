package com.android.incongress.cd.conference.model;

import android.util.Log;

import com.android.incongress.cd.conference.beans.ActivityBean;
import com.android.incongress.cd.conference.beans.AlertBean;
import com.android.incongress.cd.conference.beans.BusRemindBean;
import com.android.incongress.cd.conference.beans.EsmosBean;
import com.android.incongress.cd.conference.beans.LiveInfoBean;
import com.android.incongress.cd.conference.beans.MyFieldBean;
import com.android.incongress.cd.conference.data.ConferenceTableField;
import com.android.incongress.cd.conference.utils.StringUtils;

import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jacky on 2016/7/28.
 * 数据库查询接口
 */
public class ConferenceDbUtils {

    public static Conferences getConference() {
        return LitePal.findAll(Conferences.class).get(0);
    }

    /**
     * 更新状态
     *
     * @param fieldName
     * @param fieldState
     */
    public static void changeMyFieldState(String fieldName, int fieldState) {
        List<MyFieldBean> myFieldBeens = LitePal.where("fieldName = ?", fieldName).find(MyFieldBean.class);
        if (myFieldBeens != null && myFieldBeens.size() > 0) {
            //存在这条数据
            MyFieldBean myFieldBean = myFieldBeens.get(0);
            myFieldBean.setFieldState(fieldState);
            myFieldBean.save();
        } else {
            MyFieldBean bean = new MyFieldBean();
            bean.setFieldName(fieldName);
            bean.setFieldState(fieldState);
            bean.save();
        }
    }

    /**
     * 查询我是否有领域
     *
     * @return
     */
    public static boolean isChooseMyField() {
        List<MyFieldBean> myFieldBeens = LitePal.findAll(MyFieldBean.class);
        if (myFieldBeens == null || myFieldBeens.size() == 0) {
            return false;
        } else {
            boolean isChooseMyField = false;
            for (int i = 0; i < myFieldBeens.size(); i++) {
                if (myFieldBeens.get(i).isFieldState() == 1) {
                    isChooseMyField = true;
                    break;
                }
            }
            return isChooseMyField;
        }


    }

    /**
     * 获取病症状态
     *
     * @param fieldName
     */
    public static int getMyFieldState(String fieldName) {
        int fieldState = 0;
        List<MyFieldBean> myFieldBeens = LitePal.where("fieldName = ?", fieldName).find(MyFieldBean.class);
        if (myFieldBeens != null && myFieldBeens.size() > 0) {
            //存在这条数据
            fieldState = myFieldBeens.get(0).isFieldState();
        } else {
            fieldState = 0;
        }

        return fieldState;
    }

    /**
     * 添加一条大会信息
     *
     * @param conferenceId
     * @param dataVersion
     */
    public static boolean addConferenceDataVersion(int conferenceId, int dataVersion) {
        ConferenceData data = new ConferenceData();
        data.setConferenceId(conferenceId);
        data.setDataVersion(dataVersion);
        return data.save();
    }

    /**
     * 更新大会的本地存储状态
     *
     * @param conferenceId
     * @param isExist
     */
    public static boolean updateConferenceExistStatus(int conferenceId, int isExist) {
        EsmosBean data = null;
        List<EsmosBean> datas = LitePal.where("dataConferencesId = ?", conferenceId + "").find(EsmosBean.class);
        if (datas != null && datas.size() > 0) {
            data = datas.get(0);
            data.setIsExist(isExist);
            return data.save();
        }

        return false;
    }

    /**
     * 更新大会的数据版本
     *
     * @param conferenceId
     * @param dataVersion
     */
    public static boolean updateConferenceDataVersion(int conferenceId, int dataVersion) {
        ConferenceData data = null;
        List<ConferenceData> datas = LitePal.where("conferenceId = ?", conferenceId + "").find(ConferenceData.class);
        if (datas != null && datas.size() > 0) {
            data = datas.get(0);

            data.setDataVersion(dataVersion);
            return data.save();
        }

        return false;
    }

    /**
     * 获取大会数据版本
     *
     * @param conferenceId
     * @return
     */
    public static int getConferenceDataVersion(int conferenceId) {
        ConferenceData data = null;
        List<ConferenceData> datas = LitePal.where("conferenceId = ?", conferenceId + "").find(ConferenceData.class);
        if (datas != null && datas.size() > 0) {
            data = datas.get(0);
            return data.getDataVersion();
        }

        return -1;
    }

    /**
     * 从数据库中获取compas信息
     *
     * @return
     */
    public static CompasBean getCompasInfo() {
        return LitePal.findFirst(CompasBean.class);
    }

    /**
     * 获取所有的Conpas广告
     *
     * @return
     */
    public static List<ConpassAd> getAllConpassAds() {
        return LitePal.findAll(ConpassAd.class);
    }

    /**
     * 删除所有compas
     *
     * @return
     */
    public static int deleteCompasInfo() {
        return LitePal.deleteAll(CompasBean.class);
    }

    /**
     * 获取Compas首页的所有的会议或其他模块
     *
     * @return
     */
    public static List<EsmosBean> getEsmoBeans() {
        return LitePal.findAll(EsmosBean.class);
    }

    /**
     * 根据conferenceId获取到一个esmo详情页的数据
     *
     * @param conferenceId
     * @return
     */
    public static EsmosBean getEsmoById(int conferenceId) {
        EsmosBean bean = null;
        List<EsmosBean> esmos = LitePal.where("dataConferencesId = ?", conferenceId + "").find(EsmosBean.class);
        if (esmos != null && esmos.size() > 0) {
            bean = esmos.get(0);
        }
        return bean;
    }

    /**
     * 根据会议id返回一个优先级
     *
     * @param classId
     * @return
     */
    public static int getClassOrder(String classId) {
        int priority = 0;
        List<Class> mList = LitePal.where("classesId = ?", classId + "").find(Class.class);
        if (mList != null && mList.size() > 0) {
            priority = mList.get(0).getLevel();
        }
        return priority;
    }

    /**
     * 获取所有的会议室
     */
    public static List<Class> getAllClasses() {
        List<Class> classes;
        try {
            classes = LitePal.findAll(Class.class);
        } catch (Exception e) {
            classes = null;
        }
        return classes;
    }


    /**
     * 根据sesion查找对应的class
     *
     * @return
     */
    public static Class findClassByClassId(int classId) {
        Class classTemp = null;
        List<Class> classes = LitePal.where("classesid = ?", classId + "").find(Class.class);
        if (classes != null && classes.size() > 0) {
            classTemp = classes.get(0);
        }
        return classTemp;
    }

    /**
     * 根据facultyId查找所有的session
     *
     * @return
     */
    public static List<Session> getSessionBySpeakerId(String speakerId) {
        List<Session> sessions = null;
        try {
            sessions = LitePal.where("facultyId like '" + speakerId + ",%' or facultyId like '%," + speakerId + ",%' or facultyId like '%," + speakerId + "' or facultyId = " + speakerId).find(Session.class);
        } catch (Exception e) {
            e.printStackTrace();
            sessions = new ArrayList<>();
        }
        return sessions;
    }

    //根据标题查找对应的Session和meeting 并返回Alert对象
    public static void getAlertByTitle(Alert alertBean) {
        Log.d("sgqTest", "getAlertByTitle: 更新会议闹钟");
        List<Session> sessions;
        List<Meeting> meetings;
        sessions = LitePal.where("sessionName = ?and startTime = ?", alertBean.getTitle(),alertBean.getStart()).find(Session.class);
        meetings = LitePal.where("topic = ? and meetingDay = ?and startTime = ? and endTime = ?", StringUtils.getChinaString(alertBean.getTitle()),alertBean.getDate(),alertBean.getStart(),alertBean.getEnd()).find(Meeting.class);
        if (sessions != null && sessions.size() > 0) {
            Session session = sessions.get(0);
            alertBean.setDate(session.getSessionDay());
            alertBean.setEnd(session.getEndTime());
            alertBean.setRelativeid(String.valueOf(session.getSessionGroupId()));
            alertBean.setIdenId(session.getClassesId());
            alertBean.setStart(session.getStartTime());
            alertBean.setTitle(session.getSessionName() + "#@#" + session.getSessionNameEN());
            alertBean.setType(AlertBean.TYPE_SESSTION);
            alertBean.save();
            session.save();
        } else if (meetings != null && meetings.size() > 0) {
            Meeting meeting = meetings.get(0);
            alertBean.setDate(meeting.getMeetingDay());
            alertBean.setEnd(meeting.getEndTime());
            alertBean.setRelativeid(String.valueOf(meeting.getSessionGroupId()));
            alertBean.setIdenId(meeting.getMeetingId());
            alertBean.setStart(meeting.getStartTime());
            alertBean.setTitle(meeting.getTopic() + "#@#" + meeting.getTopicEn());
            alertBean.setType(AlertBean.TYPE_MEETING);
            alertBean.save();
            meeting.setAttention(1);
            meeting.save();
        } else {
            //如果更新去掉了，就删除这个对象
            alertBean.delete();
        }
    }

    /**
     * 获取所有的session数据
     *
     * @return
     */
    public static List<Session> getAllSession() {
        List<Session> sessions = null;
        try {
            sessions = LitePal.findAll(Session.class);
        } catch (Exception e) {
            e.printStackTrace();
            sessions = new ArrayList<>();
        }
        return sessions;
    }

    /**
     * 根据sessionGroupId来查到所需要的session
     *
     * @param sessionGroupId
     * @return
     */
    public static Session getSessionBySessionId(String sessionGroupId) {
        Session session = null;
        try {
            List<Session> sessions = LitePal.where("sessiongroupid = ? ", sessionGroupId).find(Session.class);

            if (sessions != null && sessions.size() > 0) {
                session = sessions.get(0);
            }
        } catch (Exception e) {
            e.printStackTrace();
            session = new Session();
        }
        return session;
    }

    /**
     * 根据faculty来查找某一个session
     *
     * @return
     */
    public static List<Session> getSessionByFaculty(String faculty) {
        List<Session> sessions = null;
        try {
            sessions = LitePal.where("facultyId like '" + faculty + ",%' or facultyId like '%," + faculty + ",%' or facultyId like '%," + faculty + "' or facultyId = " + faculty).find(Session.class);
        } catch (Exception e) {
            e.printStackTrace();
            sessions = new ArrayList<>();
        }

        return sessions;
    }

    /**
     * @param sessionName
     * @return
     */
    public static List<Session> getSessionByName(String sessionName, boolean isEnglish) {
        List<Session> sessions = null;
        try {
            if (isEnglish)
                sessions = LitePal.where("sessionNameEN like '%" + sessionName + "%'").find(Session.class);
            else
                sessions = LitePal.where("sessionName like '%" + sessionName + "%'").find(Session.class);
        } catch (Exception e) {
            e.printStackTrace();
            sessions = new ArrayList<>();
        }

        return sessions;
    }

    public static List<Meeting> getMeetingByName(String meetingName, boolean isEnglish) {
        List<Meeting> sessions = null;
        try {
            if (isEnglish)
                sessions = LitePal.where("topic like '%" + meetingName + "%'").find(Meeting.class);
            else
                sessions = LitePal.where("topicEn like '%" + meetingName + "%'").find(Meeting.class);
        } catch (Exception e) {
            e.printStackTrace();
            sessions = new ArrayList<>();
        }

        return sessions;
    }

    /**
     * 根据facultyId查找所有的meeting
     *
     * @param faculty
     * @return
     */
    public static List<Meeting> getMeetingBySpeakerId(String faculty) {
        List<Meeting> meetings = null;
        try {
            meetings = LitePal.where("facultyId like '" + faculty + ",%' or facultyId like '%," + faculty + ",%' or facultyId like '%," + faculty + "' or facultyId = " + faculty).find(Meeting.class);
        } catch (Exception e) {
            e.printStackTrace();
            meetings = new ArrayList<>();
        }
        return meetings;
    }

    public static List<Session> getSessionByTimeAndRoom(String searchDay, String searchRoom, String searchStartTime, String searchEndTime) {
        List<Session> sessions;
        String sql = "1=1";

        if (!StringUtils.isEmpty(searchDay)) {
            sql = sql + " and " + ConferenceTableField.SESSION_SESSIONDAY + " = '" + searchDay + "'";
        }
        if (!StringUtils.isEmpty(searchRoom)) {
            sql = sql + " and " + ConferenceTableField.SESSION_CLASSESID + " = '" + searchRoom + "'";
        }
        if (!StringUtils.isEmpty(searchStartTime)) {
            sql = sql + " and " + ConferenceTableField.SESSION_STARTTIME + " >= '" + searchStartTime + "'";
            sql = sql + " and " + ConferenceTableField.SESSION_STARTTIME + " <= '" + searchEndTime + "'";
        }
        //去除结束时间段的限制
        /*if (!StringUtils.isEmpty(searchEndTime)) {
            sql = sql + " and " + ConferenceTableField.SESSION_ENDTIME + " <= '" + searchEndTime + "'";
        }*/

        try {
            sessions = LitePal.where(sql).find(Session.class);
        } catch (Exception e) {
            e.printStackTrace();
            sessions = new ArrayList<>();
        }

        return sessions;
    }

    /**
     * 根据日期还有会议室获取session
     *
     * @return
     */
    public static List<Session> getDayClassSession(String searchRoom, String searchDay) {
        List<Session> sessions = null;

        String sql = "1=1";
        if (!StringUtils.isEmpty(searchDay)) {
            sql = sql + " and " + ConferenceTableField.SESSION_SESSIONDAY + " = '" + searchDay + "'";
        }
        if (!StringUtils.isEmpty(searchRoom)) {
            sql = sql + " and " + ConferenceTableField.SESSION_CLASSESID + " = '" + searchRoom + "'";
        }

        try {
            sessions = LitePal.where(sql).find(Session.class);
        } catch (Exception e) {
            e.printStackTrace();
            sessions = new ArrayList<>();
        }

        return sessions;
    }

    /**
     * 根据会议室获取session
     *
     * @return
     */
    public static List<Session> getSessionByRoom(String searchRoom) {
        List<Session> sessions = null;

        String sql = "1=1";

        if (!StringUtils.isEmpty(searchRoom)) {
            sql = sql + " and " + ConferenceTableField.SESSION_CLASSESID + " = '" + searchRoom + "'";
        }

        try {
            sessions = LitePal.where(sql).find(Session.class);
        } catch (Exception e) {
            e.printStackTrace();
            sessions = new ArrayList<>();
        }

        return sessions;
    }

    /**
     * 根据关键字获取session
     *
     * @return
     */
    public static List<Session> getSessionBySearch(String searchString) {
        List<Session> sessions = null;

        try {
            sessions = LitePal.where("sessionName like ?", "%" + searchString + "%").find(Session.class);
        } catch (Exception e) {
            e.printStackTrace();
            sessions = new ArrayList<>();
        }

        return sessions;
    }

    public static List<Meeting> getMeetingBySessions(List<Session> sessions) {
        List<Meeting> meetings = new ArrayList<>();
        if (sessions.size() > 0) {
            for (int i = 0; i < sessions.size(); i++) {
                Session bean = sessions.get(i);
                meetings.addAll(ConferenceDbUtils.getMeetingBySessionGroupId(bean.getSessionGroupId() + ""));
            }
        }
        return meetings;
    }

    /**
     * 根据sessionGroupId查找对应的meeting
     *
     * @param sessionGroupId
     * @return
     */
    public static List<Meeting> getMeetingBySessionGroupId(String sessionGroupId) {
        List<Meeting> meetings = null;
        try {
            meetings = LitePal.where("sessiongroupid = ?", sessionGroupId).find(Meeting.class);
        } catch (Exception e) {
            e.printStackTrace();
            meetings = new ArrayList<>();
        }

        return meetings;
    }

    /**
     * 获取所有的role
     *
     * @return
     */
    public static List<Role> getAllRoles() {
        List<Role> roles = null;
        try {
            roles = LitePal.findAll(Role.class);
        } catch (Exception e) {
            e.printStackTrace();
            roles = new ArrayList<>();
        }
        return roles;
    }

    /**
     * 根据roleId获得对应的role对象
     *
     * @param roleId
     * @return
     */
    public static Role getRoleById(String roleId) {
        Role role = null;
        try {
            List<Role> roles = LitePal.where("roleid = " + roleId).find(Role.class);
            if (roles != null && roles.size() > 0) {
                role = roles.get(0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return role;
    }

    /**
     * String sqlSession = "select * from " + ConferenceTables.TABLE_INCONGRESS_SESSION + " where sessionDay = '" + mSessionDaysList.get(i) + "'"
     * + " order by " + ConferenceTableField.SESSION_CLASSESID + " ASC, " + ConferenceTableField.SESSION_STARTTIME + " ASC";
     */
    public static List<Session> getSessionsBySessionDayOrderByClassIdAndStartTime(String sessionDay) {
        List<Session> sessions = null;
        try {
            sessions = LitePal.where("sessionDay = '" + sessionDay + "' order by classesId ASC, startTime ASC").find(Session.class);
        } catch (Exception e) {
            e.printStackTrace();
            sessions = new ArrayList<>();
        }
        return sessions;
    }

    /**
     * 获取所有讲者对象
     *
     * @return
     */
    public static List<Speaker> getAllSpeaker() {
        List<Speaker> speakers = null;
        try {
            speakers = LitePal.findAll(Speaker.class);
        } catch (Exception e) {
            e.printStackTrace();
            speakers = new ArrayList<>();
        }

        return speakers;
    }

    /**
     * 根据speakerId获得对应的speaker对象
     *
     * @param speakerId
     * @return
     */
    public static Speaker getSpeakerById(String speakerId) {

        Speaker speaker = null;
        try {
            List<Speaker> speakers = LitePal.where("speakerid = " + speakerId).find(Speaker.class);
            if (speakers != null && speakers.size() > 0) {
                speaker = speakers.get(0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return speaker;
    }

    /**
     * 通过speakerName查询speaker
     *
     * @param speakerName
     * @return
     */
    public static List<Speaker> getSpeakerBySpeakerName(String speakerName, boolean isEnglish) {
        List<Speaker> speakers = null;
        try {
            if (isEnglish) {
                speakers = LitePal.where("enName like '%" + speakerName + "%'").find(Speaker.class);
            } else {
                speakers = LitePal.where("speakerName like '%" + speakerName + "%'").find(Speaker.class);
            }
        } catch (Exception e) {
            e.printStackTrace();
            speakers = new ArrayList<>();
        }
        return speakers;
    }

    /**
     * 获取所有讲者对象
     *
     * @return
     */
    public static List<Map> getAllMaps() {
        List<Map> maps = null;
        try {
            maps = LitePal.findAll(Map.class);
        } catch (Exception e) {
            e.printStackTrace();
            maps = new ArrayList<>();
        }

        return maps;
    }

    /**
     * 获取所有的广告
     *
     * @return
     */
    public static List<Ad> getAllAds() {
        List<Ad> ads = null;
        try {
            ads = LitePal.findAll(Ad.class);
        } catch (Exception e) {
            e.printStackTrace();
            ads = new ArrayList<>();
        }

        return ads;
    }

    /**
     * 获取酥油的参展商信息
     *
     * @return
     */
    public static List<Exhibitor> getAllExhibitors() {
        List<Exhibitor> exhibitors = null;
        try {
            exhibitors = LitePal.findAll(Exhibitor.class);
        } catch (Exception e) {
            e.printStackTrace();
            exhibitors = new ArrayList<>();
        }
        return exhibitors;
    }

    /**
     * 按照首字母顺序排序
     *
     * @return
     */
    public static List<Speaker> getAllSpeakerWithOrderFL() {
        List<Speaker> speakers = null;
        try {
            speakers = LitePal.order("firstLetter asc").find(Speaker.class);
            if(speakers==null||speakers.size()<=0){
                return getAllSpeakerWithOrderSZM();
            }
        } catch (Exception e) {
            e.printStackTrace();
            speakers = new ArrayList<>();
        }
        return speakers;
    }
    /**
     * 按照拼音顺序排序
     * @return
     */
    public static List<Speaker> getAllSpeakerWithOrderSZM() {
        List<Speaker> speakers = null;
        try {
            speakers = LitePal.order("speakernamepingyin asc").find(Speaker.class);
        }catch (Exception e) {
            e.printStackTrace();
            speakers = new ArrayList<>();
        }
        return speakers;
    }

    /**
     * 获取所有的meeting
     *
     * @return
     */
    public static List<Meeting> getAllMeeting() {
        List<Meeting> meetings = null;
        try {
            meetings = LitePal.findAll(Meeting.class);
        } catch (Exception e) {
            e.printStackTrace();
            meetings = new ArrayList<>();
        }
        return meetings;
    }

    /**
     * 根据day获取所有的meeting
     *
     * @return
     */
    public static List<Meeting> getMeetingsByTimeAndAttention(String time, String isAttention) {
        List<Meeting> meetings = null;
        try {
            meetings = LitePal.where("meetingDay = ? and attention = ?", time, isAttention).find(Meeting.class);
        } catch (Exception e) {
            e.printStackTrace();
            meetings = new ArrayList<>();
        }

        return meetings;
    }

    /**
     * 根据关注获取所有的meeting
     *
     * @return
     */
    public static List<Meeting> getMeetingsByAttention(String isAttention) {
        List<Meeting> meetings = null;
        try {
            meetings = LitePal.where(" attention = ?", isAttention).find(Meeting.class);
        } catch (Exception e) {
            e.printStackTrace();
            meetings = new ArrayList<>();
        }

        return meetings;
    }
    /**
     * 根据MeetingId获取对应的Meeting
     *
     * @return
     */
    public static Meeting getMeetingsByMeetingId(int meetingId) {
        List<Meeting> meetingList = null;
        try {
            meetingList = LitePal.where(" meetingId = ?", String.valueOf(meetingId)).find(Meeting.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if(meetingList!=null&&meetingList.size()>0){
            return meetingList.get(0);
        }else {
            return null;
        }
    }
    /**
     * 根据session名称和时间查询Session是否已经预约
     *
     * @return
     */
    public static boolean getSessionStateByNameAndTime(String sessionName ,String sessionTime) {
        List<Alert> alertList = null;
        try {
            alertList = LitePal.where(" title = ?and date = ?", sessionName,sessionTime).find(Alert.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if(alertList!=null&&alertList.size()>0){
            return true;
        }else {
            return false;
        }
    }

    /**
     * 获取所有预约的直播
     *
     * @return
     */
    public static List<LiveForOrderInfo> getLivesByOrder() {
        List<LiveForOrderInfo> lives;
        try {
            lives = LitePal.findAll(LiveForOrderInfo.class);
        } catch (Exception e) {
            e.printStackTrace();
            lives = new ArrayList<>();
        }

        return lives;
    }
    /**
     * 获取所有预约的班车
     *
     * @return
     */
    public static List<BusRemindBean> getBusByOrder() {
        List<BusRemindBean> busList;
        try {
            busList = LitePal.findAll(BusRemindBean.class);
        } catch (Exception e) {
            e.printStackTrace();
            busList = new ArrayList<>();
        }

        return busList;
    }

    /**
     * 通过sessionGroupId获取对应的预约的直播
     *
     * @return
     */
    public static LiveForOrderInfo getLiveBean(int sessionGroupId) {
        List<LiveForOrderInfo> lives;
        List<LiveForOrderInfo> list = LitePal.where("sessionGroupId = ?", String.valueOf(sessionGroupId)).find(LiveForOrderInfo.class);
        if (list != null && list.size() > 0) {
            return list.get(0);
        }
        return null;
    }

    /**
     * 通过时间和关注与否获取的session列表
     *
     * @param time
     * @param isAttention
     * @return
     */
    public static List<Session> getSessionByTimeAndAttention(String time, String isAttention) {
        List<Session> sessions = null;
        try {
            sessions = LitePal.where("sessionday = ? and attention = ?", time, isAttention).find(Session.class);
        } catch (Exception e) {
            e.printStackTrace();
            sessions = new ArrayList<>();
        }

        return sessions;
    }

    /**
     * 通过关注与否获取的session列表
     *
     * @param isAttention
     * @return
     */
    public static List<Session> getSessionByAttention(String isAttention) {
        List<Session> sessions = null;
        try {
            sessions = LitePal.where("attention = ?", isAttention).find(Session.class);
        } catch (Exception e) {
            e.printStackTrace();
            sessions = new ArrayList<>();
        }

        return sessions;
    }

    /**
     * 根据faculty来查找某一个会议
     *
     * @return
     */
    public static List<Meeting> getMeetingByFaculty(String faculty) {
        List<Meeting> meetings = null;
        try {
            meetings = LitePal.where("facultyId like '" + faculty + ",%' or facultyId like '%," + faculty + ",%' or facultyId like '%," + faculty + "' or facultyId = " + faculty).find(Meeting.class);
        } catch (Exception e) {
            e.printStackTrace();
            meetings = new ArrayList<>();
        }

        return meetings;
    }

    /**
     * 根据meetingId来查找某一个会议
     *
     * @return
     */
    public static Meeting getMeetingByMeetingId(int meetingId) {
        List<Meeting> meetings = null;
        try {
            meetings = LitePal.where("meetingid = ?", meetingId + "").find(Meeting.class);
        } catch (Exception e) {
            e.printStackTrace();
            meetings = new ArrayList<>();
        }

        return meetings.get(0);
    }

    /**
     * 给session添加关注attention
     *
     * @return 是否修改成功
     */
    public static boolean addAttentionToSession(int sessionGroupId, int attention) {
        Session session = getSessionBySessionId(sessionGroupId + "");
        if (session != null) {
            session.setAttention(attention);
            return session.save();
        }
        return false;
    }

    /**
     * 给meeting添加关注attention
     *
     * @return
     */
    public static boolean addAttentionToMeeting(int meetingId, int attention) {
        Meeting meeting = getMeetingByMeetingId(meetingId);
        if (meeting != null) {
            meeting.setAttention(attention);
            return meeting.save();
        }
        return false;
    }

    /**
     * 添加一个闹铃
     *
     * @param alert
     * @return
     */
    public static boolean addAlert(Alert alert) {
        if (alert != null)
            return alert.save();
        return false;
    }

    /**
     * 获取所有的闹铃 alert
     *
     * @return
     */
    public static List<Alert> getAllAlert() {
        List<Alert> alertList;
        try {
            alertList = LitePal.findAll(Alert.class);
        } catch (Exception e) {
            e.printStackTrace();
            alertList = new ArrayList<>();
        }
        return alertList;
    }

    /**
     * 删除所有和session有关的meeting的闹铃 alert
     *
     * @return
     */
    public static void deleteAllMeetAlert(int relativeid) {
        List<Alert> alerts = LitePal.where("relativeid = ?", relativeid + "").find(Alert.class);
        if (alerts != null) {
            for (Alert alert : alerts) {
                alert.delete();
            }
        }
    }

    /**
     * 根据sessionId获取闹铃  这里统一定义Alert
     *
     * @return
     */
    public static Alert getAlertByAlertId(int alertId) {
        List<Alert> alerts = null;
        try {
            alerts = LitePal.where("idenId = ?", alertId + "").find(Alert.class);
        } catch (Exception e) {
            e.printStackTrace();
            alerts = new ArrayList<>();
        }

        if (alerts == null || alerts.size() == 0) {
            return null;
        }
        return alerts.get(0);
    }

    /**
     * 删除某个闹铃
     *
     * @param alert
     * @return
     */
    public static int deleteAlert(Alert alert) {
        return alert.delete();
    }

    /**
     * 添加点赞壁报
     *
     * @param posterId
     */
    public static void addPraisePoster(String posterId) {
        PosterPraise posterPraise = new PosterPraise();
        posterPraise.setPosterId(posterId);
        posterPraise.save();
    }

    /**
     * 查询是否已经点赞壁报
     *
     * @param posterId
     * @return
     */
    public static boolean isPraisePosterExist(String posterId) {
        List<PosterPraise> posters = null;
        try {
            posters = LitePal.where("posterId = ?", posterId).find(PosterPraise.class);
        } catch (Exception e) {
            e.printStackTrace();
            posters = new ArrayList<>();
        }

        if (posters == null || posters.size() == 0) {
            return false;
        }
        return true;
    }

    /**
     * 获取所有的笔记
     *
     * @return
     */
    public static List<Note> getAllNotes() {
        List<Note> notes = null;
        try {
            notes = LitePal.order(ConferenceTableField.NOTES_UPDATETIME + " desc").find(Note.class);
        } catch (Exception e) {
            e.printStackTrace();
            notes = new ArrayList<>();
        }
        return notes;
    }

    /**
     * 添加一条笔记
     *
     * @return
     */
    public static boolean addOneNote(Note note) {
        if (note == null) {
            return false;
        }
        return note.save();
    }

    /**
     * 删除一条笔记
     *
     * @return
     */
    public static int deleteOneNote(Note note) {
        if (note == null) {
            return -1;
        }
        return note.delete();
    }

    /**
     * 获取时间表
     *
     * @return
     */
    public static TimeBean getTime() {
        TimeBean timeBean = LitePal.findFirst(TimeBean.class);
        return timeBean;
    }

    /**
     * 更新某一条笔记()
     *
     * @param note
     * @return
     */
    public static boolean updateOneNote(Note note) {
        Note searchNote = getNoteBySessionId(note.getSessionid());

        if (searchNote != null) {
            searchNote.setUpdatetime(String.valueOf(System.currentTimeMillis()));
            searchNote.setContents(note.getContents());
            return searchNote.save();
        }

        return false;
    }

    /**
     * 根据sessionId查找某一条笔记
     *
     * @param sessionId
     * @return
     */
    public static Note getNoteBySessionId(String sessionId) {
        return LitePal.where("sessionid = " + sessionId).findFirst(Note.class);
    }

    /**
     * 根据speaker查找所有的meeting和session
     *
     * @param speakerId
     * @return
     */
    public static List<ActivityBean> getSessionAndMeetingBySpeakerId(int speakerId) {
        List<ActivityBean> beans = new ArrayList<>();

        List<Session> sessions = ConferenceDbUtils.getSessionByFaculty(speakerId + "");

        for (int i = 0; i < sessions.size(); i++) {
            Session tempSession = sessions.get(i);
            ActivityBean activity = new ActivityBean();
            activity.setActivityName(tempSession.getSessionName());

            String facultyId = tempSession.getFacultyId();
            String roleId = tempSession.getRoleId();

            if (StringUtils.isEmpty(facultyId)) {
                activity.setType(-1);
            } else {
                String[] facultyIds = facultyId.split(",");
                String[] roleIds = roleId.split(",");

                activity.setType(getType(speakerId + "", facultyIds, roleIds));
            }

            activity.setTime(tempSession.getSessionDay() + " " + tempSession.getStartTime());
            activity.setMeetingId(tempSession.getSessionGroupId());
            activity.setStart_time(tempSession.getStartTime());
            activity.setEnd_time(tempSession.getEndTime());
            activity.setDate(tempSession.getSessionDay());
            activity.setActivityNameEN(tempSession.getSessionNameEN());
            activity.setIsSessionOrMeeting(0); //0代表session

            {
                //设置身份名称
                Role role = ConferenceDbUtils.getRoleById(activity.getType() + "");
                if (role != null) {
                    activity.setTypeName(role.getName());
                    activity.setTypeEnName(role.getEnName());
                }
            }

            int classId = tempSession.getClassesId();
            Class tempClass = findClassByClassId(classId);
            if (tempClass != null) {
                activity.setLocation(tempClass.getClassesCode());
                activity.setLocationEn(tempClass.getClassCodeEn());
            } else {
                activity.setLocation("");
                activity.setLocationEn("");
            }
            beans.add(activity);
        }

        List<Meeting> meetings = getMeetingByFaculty(speakerId + "");
        for (int i = 0; i < meetings.size(); i++) {
            Meeting tempMeeting = meetings.get(i);
            ActivityBean tempActivity = new ActivityBean();

            String facultyId = tempMeeting.getFacultyId();
            String roleId = tempMeeting.getRoleId();

            if (StringUtils.isEmpty(facultyId)) {
                continue;
            } else {
                String[] facultyIds = facultyId.split(",");
                String[] roleIds = roleId.split(",");
                tempActivity.setType(getType(speakerId + "", facultyIds, roleIds));
            }

            tempActivity.setActivityName(tempMeeting.getTopic());
            tempActivity.setActivityNameEN(tempMeeting.getTopicEn());

            tempActivity.setTime(tempMeeting.getMeetingDay() + " " + tempMeeting.getStartTime());
            tempActivity.setMeetingId(tempMeeting.getMeetingId());
            tempActivity.setStart_time(tempMeeting.getStartTime());
            tempActivity.setEnd_time(tempMeeting.getEndTime());
            tempActivity.setDate(tempMeeting.getMeetingDay());
            tempActivity.setIsSessionOrMeeting(1); // 1代表meeting

            {
                //设置身份名称
                Role tempRole = getRoleById(tempActivity.getType() + "");
                if (tempRole != null) {
                    tempActivity.setTypeName(tempRole.getName());
                    tempActivity.setTypeEnName(tempRole.getEnName());
                }
            }

            int classId = tempMeeting.getClassesId();

            //根据classId获取到会议地点的中文名
            try {
                Class tempClass = findClassByClassId(classId);

                if (tempClass != null) {
                    tempActivity.setLocation(tempClass.getClassesCode());
                    tempActivity.setLocationEn(tempClass.getClassCodeEn());
                } else {
                    tempActivity.setLocation("");
                    tempActivity.setLocationEn("");
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

            beans.add(tempActivity);
        }

        return beans;
    }

    //根据sessionGroupId查找是否有当前预约的直播
    public static boolean liveIsOrdered(int sessionGroupId) {
        List<LiveForOrderInfo> list = LitePal.where("sessionGroupId = ?", String.valueOf(sessionGroupId)).find(LiveForOrderInfo.class);
        if (list != null && list.size() > 0) {
            return true;
        } else {
            return false;
        }
    }


    /**
     * 根据facultyId得到相应的数组位置
     *
     * @param facultyId
     * @param facultyIds
     * @param roleIds    若返回-1表示没有找到该id
     */
    private static int getType(String facultyId, String[] facultyIds, String[] roleIds) {
        int position = -1;
        for (int i = 0; i < facultyIds.length; i++) {
            if (StringUtils.isEquals(facultyId, facultyIds[i])) {
                position = i;
            }
        }

        if (position == -1) {
            return 1;
        } else {
            return Integer.parseInt(roleIds[position]);
        }
    }

    private static String[] getLocationChinesAndEnglish(String location) {
        return location.split("#@#");
    }


}
