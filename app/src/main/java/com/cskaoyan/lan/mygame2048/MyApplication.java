package com.cskaoyan.lan.mygame2048;

import android.app.Application;
import android.content.SharedPreferences;
import android.util.Log;

/**
 * Created by Lan on 2016/3/22.
 */

//一般用该类来保存app的全局信息(全局共享数据)

//还可以用来保存app挂掉的信息（留遗言）

//当当前application 当前应用创建的时候创建。每次app启动的时候都会创建。

public class MyApplication extends Application{

    private int i ;

    private  int LineNumber;
    private  int HighestRecord;
    private int Target;

    private SharedPreferences sp;

    @Override
    public void onCreate() {
        super.onCreate();
        //i =100;
        Log.i("MyApplication","onCreate");

        sp=getSharedPreferences("config",MODE_PRIVATE);
        LineNumber = sp.getInt("LineNumber", 4);
        HighestRecord =sp.getInt("HighestRecord",0);
        Target =sp.getInt("Target",2048);

        //设置一个捕获到全局未捕获异常的捕获器。
        Thread.currentThread().setUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread thread, Throwable ex) {

                  ex.printStackTrace();
                  //可以在这里把异常信息通过网络发送给服务器。
                  //可以把异常信息保存到sd卡上。
                  System.exit(0);

            }
        });

    }

    public int geti(){return  i;}


    public int getHighestRecord() {
        return HighestRecord;
    }

    public int getLineNumber() {
        return LineNumber;
    }

    public SharedPreferences getSp() {
        return sp;
    }

    public int getTarget() {
        return Target;
    }


    public void setHighestRecord(int highestRecord) {
        HighestRecord = highestRecord;
        SharedPreferences.Editor editor =sp.edit();
        editor.putInt("HighestRecord", highestRecord);
        editor.commit();
    }

    public void setLineNumber(int lineNumber) {
        LineNumber = lineNumber;
        SharedPreferences.Editor editor =sp.edit();

        editor.putInt("LineNumber", LineNumber);
        editor.commit();

    }

    public void setTarget(int target) {
        Target = target;
        SharedPreferences.Editor editor =sp.edit();
        editor.putInt("Target", Target);
        editor.commit();

    }
}
