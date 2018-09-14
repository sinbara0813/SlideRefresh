package com.example.application.widget;

import android.view.View;

/**
 * 动作回调
 * Author: hrb
 * Date: 2017/03/02 09:40
 * Copyright (c) 2016 d2cmall. All rights reserved.
 */
public interface SlideBridge {

    public boolean checkCanDoRefresh(final SlideRefreshView frame, final View content, final View header);

    public void scollOver(final SlideRefreshView frame, final View content, final View header);
}
