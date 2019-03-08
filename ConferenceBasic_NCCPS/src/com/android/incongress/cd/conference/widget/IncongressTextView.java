package com.android.incongress.cd.conference.widget;

import com.android.incongress.cd.conference.base.AppApplication;

import android.content.Context;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;
import android.widget.TextView;

public class IncongressTextView extends AppCompatTextView {

	public IncongressTextView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		if(AppApplication.mTypeface!=null){
			setTypeface(AppApplication.mTypeface);
		}
	}

	public IncongressTextView(Context context) {
		super(context);
		if(AppApplication.mTypeface!=null){
			setTypeface(AppApplication.mTypeface);
		}
	}
	public IncongressTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
		if(AppApplication.mTypeface!=null){
			setTypeface(AppApplication.mTypeface);
		}
	}

}
