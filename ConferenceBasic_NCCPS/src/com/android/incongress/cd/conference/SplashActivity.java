package com.android.incongress.cd.conference;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.incongress.cd.conference.api.CHYHttpClientUsage;
import com.android.incongress.cd.conference.base.AppApplication;
import com.android.incongress.cd.conference.base.BaseActivity;
import com.android.incongress.cd.conference.base.Constants;
import com.android.incongress.cd.conference.beans.VersionBean;
import com.android.incongress.cd.conference.data.JsonParser;
import com.android.incongress.cd.conference.model.ConferenceDb;
import com.android.incongress.cd.conference.save.SharePreferenceUtils;
import com.android.incongress.cd.conference.services.DownloadService;
import com.android.incongress.cd.conference.utils.BaseAsyncTask;
import com.android.incongress.cd.conference.utils.FileUtils;
import com.android.incongress.cd.conference.utils.JSONCatch;
import com.android.incongress.cd.conference.utils.ToastUtils;
import com.android.incongress.cd.conference.widget.jumpbeans.JumpingBeans;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.FileAsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.mobile.incongress.cd.conference.basic.csccm.R;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.List;

import butterknife.BindView;
import cn.jpush.android.api.JPushInterface;
import cz.msebera.android.httpclient.Header;

/**
 * 启动屏，进行数据更新和数据库建立
 * <p>
 * 开机首页，主要功能，第一次会解压一个文件，
 */
public class SplashActivity extends BaseActivity {
    private static final int MSG_CHECK = 0x0001;
    private static final int MSG_DOWNLOADING = 0x0002;
    private static final int MSG_FINISH = 0x0003;
    private static final int MSG_NEW_FILE = 0x0004;
    private static final int MSG_DOWNLOADED = 0x0005;
    private static final int MSG_LOGIN = 0x0006;
    private static final int MSG_UPDATE_APK_FOUND = 0x0007;
    private static final int MSG_UPDATE_FOUND = 0x0008;
    private static final int MSG_DOWNLOADING_ZIP = 0x0009;
    protected static final int MSG_ERROR = 0x1002;


    private static final int CREATEDB_TRUE = 0;
    private static final int CREATEDB_FALSE = 1;
    private ConferenceDb.OnUpdateInfoListener mUpdateListener;

    private int mDbVersion = 0;

    //通知
    @BindView(R.id.splash_pbh)
    ProgressBar mPbh;
    @BindView(R.id.splash_text)
    TextView mTv;
    @BindView(R.id.tv_dots)
    TextView mTvDots;
    JumpingBeans mJumpBeans;

    private int downloadPercent = 0;// 下载百分比

    private String path = null;// zip包下载到cache的地址
    private String filespath = null;// zip包解析的地址
    private List<VersionBean> zipList = null;// 数据包下载地址列表

    private boolean updateing;

    private String appversion = AppApplication.instance().getVersionName();
    private Handler handler = new Handler(new Handler.Callback() {
        private int total = 0;

        @Override
        public boolean handleMessage(Message message) {
            switch (message.what) {
                case MSG_LOGIN:
                    mTv.setText(R.string.splash_login);
                    break;
                case MSG_CHECK:
                    //检查更新
                    mTv.setText(R.string.splash_checking);
                    break;
                case MSG_NEW_FILE:
                    //发现更新包
                    total = message.arg2;
                    mTv.setText(getString(R.string.splash_downloading, downloadPercent + "") + "%");
                    mPbh.setVisibility(View.VISIBLE);
                    break;
                case MSG_DOWNLOADING_ZIP:
                    int curent = message.arg1;
                    int totalsize = message.arg2;
                    downloadPercent = Math.round(curent * 100.0f / (total - 1) * 1.0f + 0.5f);
                    if (downloadPercent > 100) {
                        downloadPercent = 100;
                    }
                    mTv.setText(getString(R.string.splash_downloading, downloadPercent + "") + "%");
                    mPbh.setProgress(downloadPercent);
                    mPbh.setMax(100);
                    if (curent < totalsize - 1) {
                        UpdateZip(++curent);
                    } else {
                        handler.sendEmptyMessage(MSG_DOWNLOADED);
                        updateing = false;
                        new Thread(new Runnable() {
                            ;

                            @Override
                            public void run() {
                                ConferenceDb.createDB(filespath, CREATEDB_TRUE, mUpdateListener);
                                handler.sendEmptyMessage(MSG_FINISH);
                            }
                        }).start();
                    }
                    break;
                case MSG_DOWNLOADING:
                    if (message.arg1 != downloadPercent) {
                        downloadPercent = message.arg1;
                        mTv.setText(getString(R.string.splash_downloading, downloadPercent + "") + "%");
                        mPbh.setVisibility(View.VISIBLE);
                        mPbh.setProgress(downloadPercent);
                    }
                    break;

                case MSG_DOWNLOADED: {
                    mTv.setText(R.string.splash_downloaded);
                }
                break;
                case MSG_FINISH: {
                    if (updateing) {
                        break;
                    }
                    finishsplash();
                }
                break;
                case MSG_ERROR: {
                    showDialog(R.string.incongress_data_update_fail, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            handler.sendEmptyMessage(MSG_CHECK);
                            firstUpdateAndCheckNewInfo();
                        }
                    }, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            updateing = false;
                            ConferenceDb.createDB(filespath, CREATEDB_FALSE, mUpdateListener);
                            handler.sendEmptyMessage(MSG_FINISH);
                        }
                    }, false);
                }
                break;
                case MSG_UPDATE_APK_FOUND: {
                    String titles[] = AppApplication.conBean.getClientVersion().split(Constants.ENCHINASPLIT);
                    String title = "";
                    if (AppApplication.systemLanguage == 1) {
                        title = titles[0];
                    } else {
                        if (titles.length > 1 && titles[1] != null && !titles[1].equals("")) {
                            title = titles[1];
                        } else {
                            title = titles[0];
                        }
                    }

                    showDialog(title, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ToastUtils.showToast("新包正在后台下载，您可继续使用应用，稍后将自动安装...");
                            String strUrl = AppApplication.conBean.getUrl().replace("\n", "");
                            Intent intent = new Intent(SplashActivity.this, DownloadService.class);
                            intent.putExtra("url", strUrl);
                            startService(intent);

                            finishsplash();
                        }
                    }, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (zipList != null && zipList.size() > 0) {
                                handler.sendEmptyMessage(MSG_UPDATE_FOUND);
                            } else {
                                updateing = false;
                                handler.sendEmptyMessage(MSG_FINISH);
                            }
                        }
                    }, false);
                }
                break;
                case MSG_UPDATE_FOUND: {
                    if (!updateing) {
                        showDialog(R.string.incongress_data_update, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                UpdateZip(0);
                                updateing = true;
                            }
                        }, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                handler.sendEmptyMessage(MSG_FINISH);
                            }
                        }, false);
                        mPbh.setProgress(0);
                    }
                }
                break;

            }
            return false;
        }
    });
    //根据网络情况检测更新
    //private String state = "-1";
    //private String mVersion = "1.0.0";
    private boolean start = false;

    //oncreat 中，开机显示界面后，检测数据库是否有更新
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        path = AppApplication.instance().getSDPath() + Constants.DOWNLOADDIR;
        filespath = AppApplication.instance().getSDPath() + Constants.FILESDIR;
        mJumpBeans = JumpingBeans.with(mTvDots).appendJumpingDots().build();
        mUpdateListener = new ConferenceDb.OnUpdateInfoListener() {
            @Override
            public void onMeetingStart(final int resID) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        data(resID);
                    }
                });
            }
        };

        final boolean firstlocal = SharePreferenceUtils.getAppBoolean(Constants.DB_frist, true);
        if (AppApplication.instance().NetWorkIsOpen()) {
            //最开始先上传会议ID,返回会议状态
            CHYHttpClientUsage.getInstanse().doQueryShenHe(Constants.getConId() + "", new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    super.onSuccess(statusCode, headers, response);
                    start = true;
                    if ("1".equals(JSONCatch.parseString("homeFacultyState", response))) {
                        Constants.IS_SECRETARY_SHOW = true;
                    } else {
                        Constants.IS_SECRETARY_SHOW = false;
                    }
                    mTv.setText(R.string.splash_checking);
                    if ("1".equals(JSONCatch.parseString("state", response))) {
                        if (appversion.equals(JSONCatch.parseString("bbVersion", response)) && firstlocal) {
                            SharePreferenceUtils.saveAppInt(Constants.PREFERENCE_DB_VERSION, 0);
                            SharePreferenceUtils.saveAppBoolean(Constants.DB_frist, false);
                        }
                        firstUpdateAndCheckNewInfo();
                    } else {
                        finishsplash();
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                    super.onFailure(statusCode, headers, responseString, throwable);
                    if (statusCode != 200) {
                        finishsplash();
                    } else {
                        if (!start) {
                            finishsplash();
                        }
                    }
                }
            });
        } else {
            Toast.makeText(SplashActivity.this, R.string.nowifi, Toast.LENGTH_LONG).show();
            Intent intent = new Intent();
            intent.setClass(SplashActivity.this, HomeActivity.class);
            startActivity(intent);
            finish();
        }
    }

    private void data(int resId) {
        mTv.setText(resId);
    }

    @Override
    protected void setContentView() {
        setContentView(R.layout.activity_splash);
    }

    @Override
    protected void initViewsAction() {
    }

    /**
     * 中断splash界面
     */
    private void finishsplash() {
        AppApplication.getHttpClient().cancelRequests(this, true);

        if (handler != null) {
            handler.removeMessages(MSG_ERROR);
            handler.removeMessages(MSG_UPDATE_APK_FOUND);
            handler.removeMessages(MSG_UPDATE_FOUND);
            handler.removeMessages(MSG_DOWNLOADING);
            handler.removeMessages(MSG_DOWNLOADING_ZIP);
            handler.removeMessages(MSG_NEW_FILE);
            handler.removeMessages(MSG_CHECK);
            handler.removeMessages(MSG_LOGIN);
            handler.removeMessages(MSG_FINISH);
        }

        /**
         * 判断是否需要引导页
         */
//        int isNeedGuide = AppApplication.getSPIntegerValue(Constants.NEED_GUIDE);
        int isNeedGuide = -1;
        WindowManager.LayoutParams attr = getWindow().getAttributes();
        attr.flags &= (~WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().setAttributes(attr);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

        if (isNeedGuide == 0 || isNeedGuide == 1) {
            startActivity(new Intent(SplashActivity.this, GuideAcitivity.class));
        } else {
            Intent intent = new Intent();
            intent.setClass(SplashActivity.this, AdvertisesActivity.class);
            startActivity(intent);
        }

        finish();
    }

    private void UpdateZip(final int index) {
        String strUrl;
        final VersionBean response = zipList.get(index);
        handler.sendMessage(Message.obtain(handler, MSG_NEW_FILE, (index + 1), zipList.size()));
        strUrl = Constants.get_NEWSPREFIX() + response.getZipUrl().replace("\n", "");
        String fileName = strUrl.substring(strUrl.lastIndexOf("/") + 1);
        File dir = new File(path);

        if (!dir.exists()) {
            dir.mkdirs();
        }

        File f = new File(path + fileName);
        if (f.exists()) {
            f.delete();
        }

        AsyncHttpClient httpClient = AppApplication.getHttpClient();
        httpClient.get(this, strUrl, new FileAsyncHttpResponseHandler(f) {
            @Override
            public void onProgress(long bytesWritten, long totalSize) {
                super.onProgress(bytesWritten, totalSize);
                if (zipList.size() == 1) {
                    int percent = (int) ((float) bytesWritten / totalSize * 100);
                    handler.sendMessage(Message.obtain(handler, MSG_DOWNLOADING, percent, 0));
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, File file) {
                handler.sendEmptyMessage(MSG_ERROR);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, final File file) {
                new BaseAsyncTask(SplashActivity.this) {
                    @Override
                    protected void backgroundWork() {
                        FileInputStream zis;
                        try {
                            zis = new FileInputStream(file);
                            FileUtils.unZip(zis, filespath);
                            SharePreferenceUtils.saveAppInt(Constants.PREFERENCE_DB_VERSION, response.getVersion());
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    protected void preWork() {
                    }

                    @Override
                    protected void postWork() {
                        if (zipList.size() > 1) {
                            handler.sendMessage(Message.obtain(handler, MSG_DOWNLOADING_ZIP, index, zipList.size()));
                        } else {
                            new BaseAsyncTask(SplashActivity.this) {

                                @Override
                                protected void backgroundWork() {
                                    ConferenceDb.createDB(filespath, CREATEDB_TRUE, mUpdateListener);
                                }

                                @Override
                                protected void preWork() {
                                    mTv.setText(R.string.splash_downloaded);
                                }

                                @Override
                                protected void postWork() {
                                    updateing = false;
                                    handler.sendEmptyMessage(MSG_FINISH);
                                }

                                @Override
                                protected void cancelWork() {

                                }
                            }.execute();
                        }
                    }

                    @Override
                    protected void cancelWork() {

                    }
                }.execute();
            }
        });
    }

    /**
     * 下载数据
     */
    public void firstUpdateAndCheckNewInfo() {

        mDbVersion = SharePreferenceUtils.getAppInt(Constants.PREFERENCE_DB_VERSION, 0);

        new BaseAsyncTask(SplashActivity.this) {
            @Override
            protected void backgroundWork() {
                if (mDbVersion == 0) {
                    //第一次进行数据加载
                    InputStream zipIn = getResources().openRawResource(R.raw.data1);
                    FileUtils.unZip(zipIn, filespath);
                    ConferenceDb.createDB(filespath, 0, mUpdateListener);
                    SharePreferenceUtils.saveAppInt(Constants.PREFERENCE_DB_VERSION, Constants.DATA_VERSION);
                    mDbVersion = Constants.DATA_VERSION;
                    SharePreferenceUtils.saveAppBoolean(Constants.DB_CLEAR,true);
                }else {
                    //这里是对当前版本进行一次数据库的转移，重新创建一个
                    if(!SharePreferenceUtils.getAppBoolean(Constants.DB_CLEAR,false)){
                        ConferenceDb.deleteAllClass();
                        //第一次进行数据加载
                        InputStream zipIn = getResources().openRawResource(R.raw.data1);
                        FileUtils.unZip(zipIn, filespath);
                        ConferenceDb.createDB(filespath, 0, mUpdateListener);
                        SharePreferenceUtils.saveAppInt(Constants.PREFERENCE_DB_VERSION, Constants.DATA_VERSION);
                        mDbVersion = Constants.DATA_VERSION;
                        SharePreferenceUtils.saveAppBoolean(Constants.DB_CLEAR,true);
                    }
                }
            }

            @Override
            protected void preWork() {

            }

            @Override
            protected void postWork() {
                int conId = Constants.getConId();
                int type = AppApplication.conType;
                String token = SharePreferenceUtils.getAppString("incongress_token");

                //检查更新数据
                CHYHttpClientUsage.getInstanse().doGetInitData(conId, mDbVersion, type, appversion, token, Constants.PROJECT_ID, new JsonHttpResponseHandler(Constants.ENCODING_GBK) {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        super.onSuccess(statusCode, headers, response);
                        AppApplication.conBean = JsonParser.parseIncongress(response);

                        zipList = AppApplication.conBean.getVersionList();
                        if (AppApplication.conBean.getClient().equals("1")) {
                            handler.sendEmptyMessage(MSG_UPDATE_APK_FOUND);
                        } else {
                            if (zipList != null && zipList.size() > 0) {
                                //有更新包
                                handler.sendEmptyMessage(MSG_UPDATE_FOUND);
                            } else {
                                int postion = getSharedPreferences("base_app", Context.MODE_PRIVATE).getInt("postion", -1);
                                if (12 != postion) {
                                    //上次数据未解析完成
                                    ConferenceDb.createDB(filespath, postion, mUpdateListener);
                                    handler.sendEmptyMessage(MSG_FINISH);
                                } else {
                                    //既没有新的安装包也没有新的数据包并且上次内容解析完成，则进入首页
                                    handler.sendEmptyMessage(MSG_FINISH);
                                }
                            }
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                        super.onFailure(statusCode, headers, throwable, errorResponse);
                        handler.sendEmptyMessage(MSG_FINISH);
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                        super.onFailure(statusCode, headers, responseString, throwable);
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                        super.onFailure(statusCode, headers, throwable, errorResponse);
                    }
                });
            }

            @Override
            protected void cancelWork() {
            }
        }.execute();
    }

    protected void onDestroy() {
        AppApplication.getHttpClient().cancelRequests(this, true);

        if (handler != null) {
            handler.removeMessages(MSG_ERROR);
            handler.removeMessages(MSG_UPDATE_APK_FOUND);
            handler.removeMessages(MSG_UPDATE_FOUND);
            handler.removeMessages(MSG_DOWNLOADING);
            handler.removeMessages(MSG_DOWNLOADING_ZIP);
            handler.removeMessages(MSG_NEW_FILE);
            handler.removeMessages(MSG_CHECK);
            handler.removeMessages(MSG_LOGIN);
            handler.removeMessages(MSG_FINISH);
        }

        mJumpBeans.stopJumping();
        super.onDestroy();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            System.exit(100);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onResume() {
        super.onResume();
        JPushInterface.onResume(this);
        MobclickAgent.onPageStart(Constants.ACTIVITY_SPLASH);
    }

    @Override
    public void onPause() {
        super.onPause();
        JPushInterface.onPause(this);
        MobclickAgent.onPageEnd(Constants.ACTIVITY_SPLASH);
    }

}

