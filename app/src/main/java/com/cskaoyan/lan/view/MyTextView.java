package com.cskaoyan.lan.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by Lan on 2016/3/21.
 */

//自定义textview，使它变成一个带边框的textview

public class MyTextView extends TextView {


    //一般在代码中new出该控件的时候，会使用这个构造初始化。
    public MyTextView(Context context) {
        super(context);
    }

    //这个构造方法一般是给系统调用的。系统通过该方法实例化控件的时候，会把xml里定义的属性一并传进来，供控件使用。
    public MyTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }


    @Override
    protected void onDraw(Canvas canvas) {


        //先在画布上画上一个我们自己定的外框
        Paint p1 = new Paint();
        p1.setColor(Color.RED);
        p1.setStyle(Paint.Style.STROKE);

        //必须是该控件已经调用过onMeasure方法测量自己的宽高之后才可以得到正确的值。
        canvas.drawRect(1,1,getMeasuredWidth(),getMeasuredHeight(),p1);

        //然后让他自己画出textview的内容和背景
        super.onDraw(canvas);
    }
}
