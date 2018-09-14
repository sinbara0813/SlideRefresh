package com.example.application.widget;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;

import cn.bingoogolapple.bgabanner.BGABanner;

/**
 * Fixme
 * Author: hrb
 * Date: 2017/03/01 17:53
 * Copyright (c) 2016 d2cmall. All rights reserved.
 */
public class MyBGABanner extends BGABanner {

    private boolean isRight;

    public MyBGABanner(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setListener();
    }

    public MyBGABanner(Context context, AttributeSet attrs) {
        super(context, attrs);
        setListener();
    }

    private void setListener(){
        setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                isRight=(position==mViews.size()-1);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    public boolean isRight() {
        if (mAdapter==null){
            return false;
        }else {
            return isRight;
        }
    }
}
