package com.android.incongress.cd.conference.widget.popup;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.PopupWindow;

import com.android.incongress.cd.conference.beans.AskQuestionListBean;
import com.mobile.incongress.cd.conference.basic.csccm.R;

import java.util.List;

/**
 * Created by Admin on 2017/9/12.
 */

public class QuestionPopupWindow extends PopupWindow {
    private int mWidth;
    private View mConvertView;

    public QuestionPopupWindow(Context context) {
        super(context);
        calWidthAndHeight(context);

        mConvertView = LayoutInflater.from(context).inflate(R.layout.ask_question_selector, null);
        setContentView(mConvertView);
        setWidth(mWidth);
        setFocusable(true);
        setTouchable(true);
        setOutsideTouchable(true); //外部可以点击
        setBackgroundDrawable(new BitmapDrawable());
        ;//点击外部消失
        setTouchInterceptor(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_OUTSIDE) {
                    dismiss();
                    return true;
                }
                return false;
            }
        });

    }

    /**
     * 计算popupWindow的高度和宽度
     *
     * @param context
     */
    private void calWidthAndHeight(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(outMetrics);
        mWidth = (int) (outMetrics.widthPixels * 0.9);
    }

}
