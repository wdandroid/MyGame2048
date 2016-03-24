package com.cskaoyan.lan.view;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.FrameLayout;
import android.widget.TextView;

/**
 * Created by Lan on 2016/3/21.
 */
public class NumberItem extends FrameLayout{




    private TextView mTv;
    private int number;


    public NumberItem(Context context) {
        super(context);
        initView(0);
    }

    public NumberItem(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(0);
    }


    public NumberItem(Context context,int number) {
        super(context);
        initView(number);
    }

    private void initView(int number) {

        setBackgroundColor(Color.GRAY);

        mTv = new TextView(getContext());
        LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT);
        params.setMargins(5, 5, 5, 5);  //这里的5表示5个像素（在代码里给控件设置的所有数字，如无单位，默认单位是px）
        mTv.setGravity(Gravity.CENTER);

 /*       mTv.setBackgroundColor(Color.WHITE);
        mTv.setText(number + "");*/
        setTextNumber(number);

        addView(mTv, params);

        this.number=number;
    }



    //向外界返回当前控件内保存的数字
    public int getNumber(){return number;}

    public void setTextNumber(int num){

        //更改控件的显示数字的 同时，应该把里面保存数字值的number同时更改
        number=num;

        if (num==0)
            mTv.setText("");
        else
            mTv.setText(num+"");


        switch (num){

            case 0:
                mTv.setBackgroundColor(0x00000000); //ARGB
                break;
            case 2:
                mTv.setBackgroundColor(0xFFFFF5EE);

                break;
            case 4:
                mTv.setBackgroundColor(0xFFFFEC8B);

                break;
            case 8:
                mTv.setBackgroundColor(0xFFFFE4C4);
                break;
            case 16:
                mTv.setBackgroundColor(0xFFFFDAB9);
                break;
            case 32:
                mTv.setBackgroundColor(0xFFFFC125);
                break;
            case 64:
                mTv.setBackgroundColor(0xFFFFB6C1);
                break;
            case 128:
                mTv.setBackgroundColor(0xFFFFA500);
                break;
            case 256:
                mTv.setBackgroundColor(0xFFFF83FA);
                break;
            case 512:
                mTv.setBackgroundColor(0xFFFF7F24);
                break;
            case 1024:
                mTv.setBackgroundColor(0xFFFF6A6A);
                break;
            case 2048:
                mTv.setBackgroundColor(0xFFFF1493);
                break;
            case 4096:
                mTv.setBackgroundColor(0xFFFF3030);
                break;

        }
    }
}
