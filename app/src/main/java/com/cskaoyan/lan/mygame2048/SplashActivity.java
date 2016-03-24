package com.cskaoyan.lan.mygame2048;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;

import net.youmi.android.AdManager;
import net.youmi.android.spot.SpotDialogListener;
import net.youmi.android.spot.SpotManager;

public class SplashActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        //在当前的splash页面停留一会，然后自动跳入到home
        new Thread(){
            @Override
            public void run() {
                super.run();

                try {
                    sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                // startActivity(new Intent(this,Home.class));
                // 相当于系统帮忙发消息，然后在主线程执行。
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        startActivity(new Intent(SplashActivity.this,Home.class));
                        finish();
                    }
                });

            }
        }.start();

        //初始化广告
        AdManager.getInstance(  this).init("33aa1ed766c98cda", "cd2a65ca59d6fad3", true);

        //在当前页面加入插屏广告
        SpotManager.getInstance(  this).loadSpotAds();
        SpotManager.getInstance(  this).setSpotOrientation(SpotManager.ORIENTATION_PORTRAIT);
        SpotManager.getInstance(  this).setAnimationType(SpotManager.ANIM_SIMPLE);
        //SpotManager.getInstance(  this).showSpotAds(this);

        //插屏监听接口（可选）

        SpotManager.getInstance(  this).showSpotAds(this, new SpotDialogListener() {
            @Override
            public void onShowSuccess() {
                Log.i("SplashActivity","onShowSuccess");
            }

            @Override
            public void onShowFailed() {
                Log.i("SplashActivity","onShowFailed");

            }

            @Override
            public void onSpotClosed() {
                Log.i("SplashActivity","onSpotClosed");

            }

            @Override
            public void onSpotClick(boolean b) {
                Log.i("SplashActivity","onSpotClick");

            }
        });
    }


    //当用户点击手机的back键会调用该函数
    @Override
    public void onBackPressed() {
       // super.onBackPressed();

        // 如果有需要，可以点击后退关闭插播广告。
        if (!SpotManager.getInstance(  this).disMiss()) {
            // 弹出退出窗口，可以使用自定义退屏弹出和回退动画,参照demo,若不使用动画，传入-1
            super.onBackPressed();
        }
        Log.i("splash","onBackPressed");
    }

    @Override
    protected void onDestroy() {
        SpotManager.getInstance(  this).onDestroy();
        super.onDestroy();
    }
}
