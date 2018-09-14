package com.example.application.widget;

import android.graphics.PointF;

/**
 * 动作指示器
 * Author: hrb
 * Date: 2017/03/01 16:02
 * Copyright (c) 2016 d2cmall. All rights reserved.
 */
public class SlideIndicator {

    public int POS_START = 0;
    protected int mOffsetToRefresh = 0;
    private PointF mPtLastMove = new PointF();
    private float mOffsetX;
    private float mOffsetY;
    private int mCurrentPos = 0;
    private int mLastPos = 0;
    private int mRightWidth;
    private int mPressedPos = 0;

    private float mRatioOfHeaderHeightToRefresh = 1.2f;
    private float mResistance = 1.7f;
    private boolean mIsUnderTouch = false;

    public boolean isUnderTouch() {
        return mIsUnderTouch;
    }

    public float getResistance() {
        return mResistance;
    }

    public void setResistance(float resistance) {
        mResistance = resistance;
    }

    public void onRelease() {
        mIsUnderTouch = false;
    }

    protected void processOnMove(float currentX, float currentY, float offsetX, float offsetY) {
        setOffset(offsetX/mResistance, offsetY);
    }

    public int getOffsetToRefresh() {
        return mOffsetToRefresh;
    }

    public void setOffsetToRefresh(int offset) {
        mRatioOfHeaderHeightToRefresh = mRightWidth * 1f / offset;
        mOffsetToRefresh = offset;
    }

    public void onPressDown(float x, float y) {
        mIsUnderTouch = true;
        mPressedPos = mCurrentPos;
        mPtLastMove.set(x, y);
    }

    public final void onMove(float x, float y) {
        float offsetX = x - mPtLastMove.x;
        float offsetY = (y - mPtLastMove.y);
        processOnMove(x, y, offsetX, offsetY);
        mPtLastMove.set(x, y);
    }

    protected void setOffset(float x, float y) {
        mOffsetX = x;
        mOffsetY = y;
    }

    public float getOffsetX() {
        return mOffsetX;
    }

    public float getOffsetY() {
        return mOffsetY;
    }

    public int getLastPosY() {
        return mLastPos;
    }

    public int getCurrentPosY() {
        return mCurrentPos;
    }

    /**
     * Update current position before update the UI
     */
    public final void setCurrentPos(int current) {
        mLastPos = mCurrentPos;
        mCurrentPos = current;
        onUpdatePos(current, mLastPos);
    }

    protected void onUpdatePos(int current, int last) {

    }

    public int getRightWidth(){ return mRightWidth;};

    public void setRightWidth(int width){
        mRightWidth=width;
        updateWidth();
    }

    private void updateWidth() {
        mOffsetToRefresh = (int) (mRatioOfHeaderHeightToRefresh * mRightWidth);
    }

    public void convertFrom(SlideIndicator ptrSlider) {
        mCurrentPos = ptrSlider.mCurrentPos;
        mLastPos = ptrSlider.mLastPos;
        mRightWidth = ptrSlider.mRightWidth;
    }

    public boolean hasLeftStartPosition() {
        return mCurrentPos < POS_START;
    }

    public boolean isInStartPosition() {
        return mCurrentPos == POS_START;
    }

    public boolean isAlreadyHere(int to) {
        return mCurrentPos == to;
    }

    public boolean willOverTop(int to) {
        return to > POS_START;
    }

    public void setStart(int startPotion){
        POS_START=startPotion;
        mCurrentPos=startPotion;
    }

    public int getStart(){
        return POS_START;
    }
}
