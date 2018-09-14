package com.example.application.activity;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.application.R;
import com.example.application.controler.UniversalImageLoader;
import com.example.application.widget.SlideBridge;
import com.example.application.widget.SlideRefreshView;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.imageaware.ImageViewAware;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.util.ArrayList;
import java.util.List;

import cn.bingoogolapple.bgabanner.BGABanner;

/**
 * demo
 * Author: hrb
 * Date: 2017/03/01 11:14
 * Copyright (c) 2016 d2cmall. All rights reserved.
 */
public class DemoActivity extends Activity {

    private BGABanner bgaBanner;
    private SlideRefreshView rootView;
    private boolean isRight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demo);
        rootView= (SlideRefreshView) findViewById(R.id.root_view);
        bgaBanner= (BGABanner) findViewById(R.id.carouselView);
        UniversalImageLoader.getInstance().init(getApplicationContext());
        initData();
        initListener();
    }

    private void initListener(){
        bgaBanner.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                isRight=(position==bgaBanner.getItemCount()-1);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        rootView.setHandler(new SlideBridge() {
            @Override
            public boolean checkCanDoRefresh(SlideRefreshView frame, View content, View header) {
                return isRight;
            }

            @Override
            public void scollOver(SlideRefreshView frame, View content, View header) {
                Toast.makeText(DemoActivity.this,"滑动到商品详情",Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void initData() {
        WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point p=new Point();
        display.getSize(p);
        bgaBanner.getLayoutParams().height=Math.round(p.x * 700 / 1000);
        List<String> images=new ArrayList<>();
        images.add("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1491028970865&di=a0be6b3ea473b81ba5c8055ac58c1255&imgtype=0&src=http%3A%2F%2Fi.guancha.cn%2Fnews%2F2015%2F10%2F16%2F20151016135551745.jpg");
        images.add("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1491029025967&di=59416c33523bfcc921c22fcb67ac9d0e&imgtype=0&src=http%3A%2F%2Fwww.wallcoo.com%2Fanimal%2FDogs_Summer_and_Winter%2Fwallpapers%2F1920x1200%2FDogsB10_Lucy.jpg");
        images.add("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1491029025967&di=7e9e9a86ff74093530dbe109aa1e65a8&imgtype=0&src=http%3A%2F%2Fwww.yw020.com%2Ffile%2Fupload%2F201505%2F07%2F11-12-15-29-3.JPG");
        images.add("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1491029025966&di=06db80bc996a3cbfdb4095344a7d6949&imgtype=0&src=http%3A%2F%2Fa4.att.hudong.com%2F38%2F47%2F19300001391844134804474917734_950.png");
        images.add("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1491029025964&di=6a6bc599a81e34d83af25d75cf52019c&imgtype=0&src=http%3A%2F%2Fimage.ajunews.com%2Fcontent%2Fimage%2F2014%2F09%2F17%2F20140917102600112243.jpg");
        bgaBanner.setAdapter(new BGABanner.Adapter() {
            @Override
            public void fillBannerItem(BGABanner banner, View view, Object model, int position) {
                String url = (String) model;
                if (view instanceof ViewGroup){
                    ImageView imageView= (ImageView) ((ViewGroup)view).getChildAt(0);
                    final ImageView imageTag= (ImageView) ((ViewGroup)view).getChildAt(1);
                    ImageViewAware aware = new ImageViewAware(imageView, false);
                    UniversalImageLoader.getInstance().displayImage(url, aware, R.mipmap.ic_launcher, R.mipmap.ic_launcher, new ImageLoadingListener() {
                        @Override
                        public void onLoadingStarted(String imageUri, View view) {
                        }

                        @Override
                        public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                        }

                        @Override
                        public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                            imageTag.setVisibility(View.VISIBLE);
                        }

                        @Override
                        public void onLoadingCancelled(String imageUri, View view) {
                        }
                    });
                }else {
                    ImageViewAware aware = new ImageViewAware((ImageView) view, false);
                    UniversalImageLoader.getInstance().displayImage(url, aware, R.mipmap.ic_launcher, R.mipmap.ic_launcher);
                }
            }
        });
        bgaBanner.setData(images, null);
     }
}
