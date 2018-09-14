package com.example.application.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

/**
 * 文案显示
 * Author: hrb
 * Date: 2017/03/02 15:31
 * Copyright (c) 2016 d2cmall. All rights reserved.
 */
public class RefreshTextView extends View {

    private final String RELEASE = "释放查看图文详情";
    private final String SLIDE = "滑动查看图文详情";
    private String textContent = SLIDE;
    private Paint mPaint;
    private Rect mBounds;
    private float textHeight;
    private float textAllHeight;

    public RefreshTextView(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public RefreshTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init(){
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mBounds = new Rect();
        setPadding(0,0,0,0);
        mPaint.setTextSize(28);
        String text = textContent.substring(0, 1);
        mPaint.getTextBounds(text, 0, text.length(), mBounds);
        textHeight = mBounds.height();
        textAllHeight=25*8+7*10;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        for (int i=0;i<textContent.length();i++){
            canvas.drawText(textContent.substring(i,i+1), 0,(getHeight()-textAllHeight)/2+textHeight*i+i*10,mPaint);
        }
    }

    public void refresh(boolean is) {
        if (is) {
            textContent = RELEASE;
        } else {
            textContent = SLIDE;
        }
        invalidate();
    }
}
