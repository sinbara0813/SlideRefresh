package com.example.application.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.Scroller;

import com.example.application.R;

/**
 * 侧滑刷新
 * Author: hrb
 * Date: 2017/03/01 14:34
 * Copyright (c) 2016 d2cmall. All rights reserved.
 */
public class SlideRefreshView extends ViewGroup {

    public static boolean DEBUG = false;
    protected final String LOG_TAG = SlideRefreshView.class.getSimpleName();
    private final static int TOUCH_STATE_INIT = -1;
    private final static int TOUCH_STATE_PREPARE = 1;
    private final static int TOUCH_STATE_OVER = 2;
    private int mState = TOUCH_STATE_INIT;
    private int mDurationToCloseHeader = 1000;
    private int mRightWidth;
    private int mContentWidth;
    private boolean scrollOver;

    private SlideBridge mHandler;
    private SlideIndicator mPtrIndicator;
    private View slipRightView;
    private ScrollChecker scrollChecker;
    private View mRightView;
    private ImageView iv;
    private RefreshTextView tv;


    public SlideRefreshView(Context context, AttributeSet attrs) {
        this(context,attrs,0);
    }

    public SlideRefreshView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    private void initView() {
        mPtrIndicator=new SlideIndicator();
        scrollChecker=new ScrollChecker();
    }

    @Override
    protected void onFinishInflate() {
        int childCount=getChildCount();
        if (childCount==1){
            slipRightView= getChildAt(0);
        }else if (childCount==2){
            slipRightView= getChildAt(0);
            mRightView=getChildAt(1);
            iv= (ImageView) mRightView.findViewById(R.id.right_iv);
            tv= (RefreshTextView) mRightView.findViewById(R.id.right_tv);
        }
        super.onFinishInflate();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (scrollChecker != null) {
            scrollChecker.destroy();
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        if (slipRightView != null) {
            measureContentView(slipRightView, widthMeasureSpec, heightMeasureSpec);
        }

        if (mRightView != null) {
            measureChildWithMargins(mRightView, widthMeasureSpec, 0, heightMeasureSpec, 0);
            MarginLayoutParams lp = (MarginLayoutParams) mRightView.getLayoutParams();
            mRightWidth=mRightView.getMeasuredWidth()+lp.leftMargin+lp.rightMargin;
            mPtrIndicator.setRightWidth(mRightWidth);
        }
    }

    private void measureContentView(View child,
                                    int parentWidthMeasureSpec,
                                    int parentHeightMeasureSpec) {
        final MarginLayoutParams lp = (MarginLayoutParams) child.getLayoutParams();
        if (mRightView!=null){
            MarginLayoutParams params = (MarginLayoutParams) mRightView.getLayoutParams();
            params.height=lp.height;
        }
        final int childWidthMeasureSpec = getChildMeasureSpec(parentWidthMeasureSpec,
                getPaddingLeft() + getPaddingRight() + lp.leftMargin + lp.rightMargin, lp.width);
        final int childHeightMeasureSpec = getChildMeasureSpec(parentHeightMeasureSpec,
                getPaddingTop() + getPaddingBottom() + lp.topMargin, lp.height);

        child.measure(childWidthMeasureSpec, childHeightMeasureSpec);
        mContentWidth=child.getMeasuredWidth();
        mPtrIndicator.setStart(mContentWidth);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        layoutChildren();
    }

    private void layoutChildren() {
        int offsetX = mPtrIndicator.getCurrentPosY();
        int paddingLeft = getPaddingLeft();
        int paddingTop = getPaddingTop();

        if (slipRightView != null) {
            MarginLayoutParams lp = (MarginLayoutParams) slipRightView.getLayoutParams();
            final int left = paddingLeft + lp.leftMargin+offsetX-mContentWidth;
            final int top = paddingTop + lp.topMargin ;
            final int right = left + slipRightView.getMeasuredWidth();
            final int bottom = top + slipRightView.getMeasuredHeight();
            slipRightView.layout(left, top, right, bottom);
        }

        if (mRightView != null) {
            MarginLayoutParams lp = (MarginLayoutParams) mRightView.getLayoutParams();
            final int left = paddingLeft + lp.leftMargin+ offsetX;
            final int top = paddingTop + lp.topMargin;
            final int right = left + mRightView.getMeasuredWidth();
            final int bottom = top + mRightView.getMeasuredHeight();
            mRightView.layout(left, top, right, bottom);
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent e) {
        if (!isEnabled() || slipRightView == null || mRightView == null) {
            return super.dispatchTouchEvent(e);
        }
        int action = e.getAction();
        switch (action) {
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                mPtrIndicator.onRelease();
                if (mPtrIndicator.hasLeftStartPosition()) {
                    onRelease();
                    return super.dispatchTouchEvent(e);
                } else {
                    return super.dispatchTouchEvent(e);
                }
            case MotionEvent.ACTION_DOWN:
                mPtrIndicator.onPressDown(e.getX(), e.getY());

                scrollChecker.abortIfWorking();

                super.dispatchTouchEvent(e);
                return true;

            case MotionEvent.ACTION_MOVE:
                mPtrIndicator.onMove(e.getX(), e.getY());
                float offsetX = mPtrIndicator.getOffsetX();
                float offsetY = mPtrIndicator.getOffsetY();

                boolean moveLeft = offsetX < 0;
                boolean moveRight = !moveLeft;
                boolean canMoveRight = mPtrIndicator.hasLeftStartPosition();

                if (moveLeft && mHandler != null && !mHandler.checkCanDoRefresh(this,  slipRightView, mRightView)) {
                    return super.dispatchTouchEvent(e);
                }

                if ((moveRight && canMoveRight) || moveLeft) {
                    movePos(offsetX);
                    return true;
                }
        }
        return super.dispatchTouchEvent(e);
    }

    private void movePos(float deltaX) {
        if ((deltaX > 0 && mPtrIndicator.isInStartPosition())) {
            if (DEBUG) {
                Log.e(LOG_TAG, String.format("has reached the right"));
            }
            return;
        }

        int to = mPtrIndicator.getCurrentPosY() + (int) deltaX;

        if (mPtrIndicator.willOverTop(to)) {
            if (DEBUG) {
                Log.e(LOG_TAG, String.format("over top"));
            }
            to = mPtrIndicator.getStart();
        }
        mPtrIndicator.setCurrentPos(to);
        int change = to - mPtrIndicator.getLastPosY();
        updatePos(change);
    }

    private void updatePos(int change) {
        if (change == 0) {
            return;
        }
        mRightView.offsetLeftAndRight(change);
        slipRightView.offsetLeftAndRight(change);
        invalidate();
        if (mPtrIndicator.getCurrentPosY()<=mContentWidth-mRightWidth){
            scrollOver=true;
            //rightView UI变化
            if (mState!=TOUCH_STATE_OVER){
                mState=TOUCH_STATE_OVER;
                iv.clearAnimation();
                Animation animation= AnimationUtils.loadAnimation(getContext(),R.anim.arrow_down);
                iv.startAnimation(animation);
                tv.refresh(true);
            }
        }else {
            //rightView UI变化
            if (mState==TOUCH_STATE_OVER){
                mState=TOUCH_STATE_PREPARE;
                iv.clearAnimation();
                Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.arrow_up);
                iv.startAnimation(animation);
                tv.refresh(false);
            }
        }
    }

    private void onRelease(){
        //简单实现返回初始状态 也可以释放到某个位置
        if (scrollOver){
            if (mHandler!=null){
                mHandler.scollOver(this, slipRightView,mRightView);
            }
        }
        tryScrollBackToTop();
    }

    private void tryScrollBackToTop() {
        if (!mPtrIndicator.isUnderTouch()) {
            scrollChecker.tryToScrollTo(mPtrIndicator.getStart(), mDurationToCloseHeader);
        }
    }

    public void setHandler(SlideBridge handler){
        mHandler=handler;
    }

    @Override
    protected boolean checkLayoutParams(ViewGroup.LayoutParams p) {
        return p instanceof LayoutParams;
    }

    @Override
    protected ViewGroup.LayoutParams generateDefaultLayoutParams() {
        return new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
    }

    @Override
    protected ViewGroup.LayoutParams generateLayoutParams(ViewGroup.LayoutParams p) {
        return new LayoutParams(p);
    }

    @Override
    public ViewGroup.LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new LayoutParams(getContext(), attrs);
    }

    public static class LayoutParams extends MarginLayoutParams {

        public LayoutParams(Context c, AttributeSet attrs) {
            super(c, attrs);
        }

        public LayoutParams(int width, int height) {
            super(width, height);
        }

        @SuppressWarnings({"unused"})
        public LayoutParams(MarginLayoutParams source) {
            super(source);
        }

        public LayoutParams(ViewGroup.LayoutParams source) {
            super(source);
        }
    }

    class ScrollChecker implements Runnable {

        private int mLastFlingY;
        private Scroller mScroller;
        private boolean mIsRunning = false;
        private int mStart;
        private int mTo;

        public ScrollChecker() {
            mScroller = new Scroller(getContext());
        }

        public void run() {
            boolean finish = !mScroller.computeScrollOffset() || mScroller.isFinished();
            int curY = mScroller.getCurrY();
            int deltaY = curY - mLastFlingY;
            if (!finish) {
                mLastFlingY = curY;
                movePos(deltaY);
                post(this);
            } else {
                finish();
            }
        }

        private void finish() {
            reset();
        }

        private void reset() {
            mState=TOUCH_STATE_INIT;
            scrollOver=false;
            mIsRunning = false;
            mLastFlingY = 0;
            removeCallbacks(this);
        }

        private void destroy() {
            reset();
            if (!mScroller.isFinished()) {
                mScroller.forceFinished(true);
            }
        }

        public void abortIfWorking() {
            if (mIsRunning) {
                if (!mScroller.isFinished()) {
                    mScroller.forceFinished(true);
                }
                reset();
            }
        }

        public void tryToScrollTo(int to, int duration) {
            if (mPtrIndicator.isAlreadyHere(to)) {
                return;
            }
            mStart = mPtrIndicator.getCurrentPosY();
            mTo = to;
            int distance = to - mStart;

            removeCallbacks(this);

            mLastFlingY = 0;

            if (!mScroller.isFinished()) {
                mScroller.forceFinished(true);
            }
            mScroller.startScroll(0, 0, 0, distance, duration);
            post(this);
            mIsRunning = true;
        }
    }
}
