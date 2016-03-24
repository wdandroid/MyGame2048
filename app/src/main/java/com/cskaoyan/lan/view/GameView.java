package com.cskaoyan.lan.view;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.WindowManager;
import android.widget.GridLayout;

import com.cskaoyan.lan.mygame2048.Home;
import com.cskaoyan.lan.mygame2048.MyApplication;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by Lan on 2016/3/21.
 */
public class GameView extends GridLayout{


    private static final String TAG = "GameView";
    private int mCollomNumber  ;
    private int mRowNumber ;
    private int mTarget;

    private Home mHome;

    //用于计算上下左右滑动
    float startx=0;
    float starty=0;
    float stopx;
    float stopy;

    //用于计算滑动之后每行或者每列合并后的数组

    List<Integer> caculorList;

    private NumberItem[][] mNumberItemMatrix;
    //记录上一步操作的矩阵
    private int[][]  histroyMatrix;

    private List<Point> blanklist;


    //决定是否可以撤销的标志位
    boolean canRevert =false;


    //保存当前分数的一个成员变量
    private int currentScore;
    private int HighestScore;
    SharedPreferences sp;
    private int width;

    public GameView(Context context) {
        super(context);
        init();
    }



    public GameView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();

    }

    private void init() {

        //得到当前Activity的引用
        mHome =Home.getActivity();
        MyApplication app = (MyApplication) mHome.getApplication();
        mRowNumber=app.getLineNumber();
        mCollomNumber=mRowNumber;
        mTarget=app.getTarget();


        canRevert=false;


        blanklist= new ArrayList<Point>();
        caculorList = new ArrayList<Integer>();
        mNumberItemMatrix= new NumberItem[mRowNumber][mCollomNumber];
        histroyMatrix=new int[mRowNumber][mCollomNumber];

        currentScore=0;
        sp=getContext().getSharedPreferences("config",getContext().MODE_PRIVATE);
        HighestScore=sp.getInt("HighestScore",0);



        WindowManager wm   = (WindowManager) getContext().getSystemService(getContext().WINDOW_SERVICE);

        final Display defaultDisplay = wm.getDefaultDisplay();

        //之前
//      int height =  defaultDisplay.getHeight();
//      int width=  defaultDisplay.getWidth();

        //之后
        DisplayMetrics metrix = new DisplayMetrics();
        defaultDisplay.getMetrics(metrix);
        width = metrix.widthPixels;


        setRowCount(mRowNumber);
        setColumnCount(mCollomNumber);

        for (int i=0;i<mRowNumber;i++)
            for (int j=0;j<mCollomNumber;j++){
                NumberItem item = new NumberItem(getContext(),0);
                //这里的50 应该变成动态获取屏幕宽度，然后除以gridlayout 的列数
//                item.setTextNumber(2);
                addView(item, width / mCollomNumber, width / mCollomNumber); //可以指定增加的子控件宽，高

                //把该item的引用保存在一个二维矩阵里面

                mNumberItemMatrix[i][j]=item;
                //初始化的时候记录当前空白的位置。

                Point p = new Point();
                p.x= i;
                p.y= j ;
                blanklist.add(p);



            }

        //继续初始化棋盘view，一开始看到的时候，里面应有随机出现的两个数字不为0
        //有一个东西来记录当前棋盘上的空白位置

        addRandomNumber();
        //表示随机找两个空白位置，产生一个数字
        addRandomNumber();
//        addRandomNumber();
//        addRandomNumber();

    }

    /*
       在棋盘的空白位置上，随机找到一个位置，给它的item设置一个数。

     */
    private void addRandomNumber() {

        updateBlanklist();
        final int size = blanklist.size();
        final int location  = (int) Math.floor(Math.random() * size);
        final Point point = blanklist.get(location);
        mNumberItemMatrix[point.x][point.y].setTextNumber(Math.random() > 0.5d ? 2 : 4);


    }

    private void updateBlanklist() {
        blanklist.clear();
        for (int i=0;i<mRowNumber;i++)
            for (int j=0;j<mCollomNumber;j++){
              NumberItem  numberitem = mNumberItemMatrix[i][j];
               if (numberitem.getNumber()==0)
                   blanklist.add(new Point(i,j));
            }

    }


    public void restart(){

        removeAllViews();


        init();
        updateCurrentScore();
        Log.i(TAG, "restart GmaeView");


        //举例
        //int i =1/0;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {



        switch (event.getAction()){

            case MotionEvent.ACTION_DOWN:
                startx=(int)event.getX();
                starty=(int)event.getY();

                saveHistroy();
                Log.i(TAG,"ACTION_DOWN");
                break;
            case MotionEvent.ACTION_MOVE:
                Log.i(TAG,"ACTION_MOVE");
                break;

            case MotionEvent.ACTION_UP:
                stopx=(int)event.getX();
                stopy=(int)event.getY();
                Log.i(TAG,"ACTION_UP");

                judgeDeriction(startx,starty,stopx,stopy);

                //判断游戏是否结束 1 可以继续玩 2 成功了  3 gameover
                updateCurrentScore();
                handleResult(isOver());


                break;

        }


        return   true ;//super.onTouchEvent(event);
        //Down MOve ...MOve UP
        //表示当前控件来处理这个触摸事件的序列

    }

    private void updateCurrentScore() {
        //更新当前的分数
        mHome.updateCurrentScore(currentScore);
    }

    //恢复上一步的状态
    public void revert(){

        //方法1，遍历一遍history矩阵，如果里面全是0，就直接return。


        //方法2，添加一个flag，当且仅当histroy矩阵有过赋值之后，才置位1.
        if (canRevert) {
            for (int i = 0; i < mRowNumber; i++)
                for (int j = 0; j < mCollomNumber; j++) {
                    mNumberItemMatrix[i][j].setTextNumber(histroyMatrix[i][j]);

                }
        }
    }


    //把当前的记录保存到history矩阵中
    private void saveHistroy() {

        for (int i=0;i<mRowNumber;i++)
            for (int j=0;j<mCollomNumber;j++){
              histroyMatrix[i][j] =  mNumberItemMatrix[i][j].getNumber() ;
            }

        canRevert=true;
    }

    private void handleResult(int result) {
        if (result==2){//完成游戏

             if (HighestScore<currentScore){

                  MyApplication app = (MyApplication) mHome.getApplication();
                  app.setHighestRecord(currentScore);
                  mHome.updateHighestScore(currentScore);
             }

         new     AlertDialog.Builder(getContext()).
                 setTitle("恭喜")
                 .setMessage("您已经完成游戏！")
                 .setPositiveButton("重新开始", new DialogInterface.OnClickListener() {
                     @Override
                     public void onClick(DialogInterface dialog, int which) {
                       restart();
                     }
                 })
                 .setNegativeButton("挑战更难", new DialogInterface.OnClickListener() {
                     @Override
                     public void onClick(DialogInterface dialog, int which) {
                         mHome.option();
                     }
                 })
                 .show();

        }else  if (result==3){//gameover

            new     AlertDialog.Builder(getContext()).
                    setTitle("失败")
                    .setMessage("游戏结束")
                    .setPositiveButton("重新开始", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            restart();

                        }
                    })
                    .setNegativeButton("退出游戏", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            mHome.finish();//当前Activity关闭
                        }
                    })
                    .show();

        }else{//1 表示继续，则给出一个随机数
            addRandomNumber();
        }
    }


    // 2 成功了    1 可以继续玩   3 gameover
    private int isOver(){

        for (int i=0;i<mRowNumber;i++)
            for (int j=0;j<mCollomNumber;j++){
                
               if (mNumberItemMatrix[i][j].getNumber()==mTarget){
                   return 2;
               }
                
            }

        //说明没有成功。

        updateBlanklist();
        if (blanklist.size()==0){
            //这种情况下如果还有可以合并的，则返回1
             for (int i=0;i<mRowNumber;i++)
               for (int j=0;j<mCollomNumber-1;j++){
                  int current =   mNumberItemMatrix[i][j].getNumber();
                  int next = mNumberItemMatrix[i][j+1] .getNumber();
                    if (current==next){
                        return  1;
                    }
                }

            for (int i=0;i<mRowNumber;i++)
                for (int j=0;j<mCollomNumber-1;j++){
                    int current =   mNumberItemMatrix[j][i].getNumber();
                    int next = mNumberItemMatrix[j+1][i] .getNumber();
                    if (current==next){
                        return  1;
                    }
                }
            //如果没有可以合并的了，返回3
            return 3;
        }
        return 1;
    }


    private void judgeDeriction(float startx, float starty, float stopx, float stopy) {


       float dx= Math.abs(startx-stopx);
       float dy= Math.abs(starty-stopy);
       float baselevel= width/5;

        if(dx>baselevel||dy>baselevel){

            boolean flag =   dx>dy?true:false;

            Log.i(TAG,  "flag="+flag+"startx:"+ startx+", starty: "+starty+"stopx"+stopx+"stopy:"+ stopy);
            if (flag){//水平方向滑动

                if (stopx>startx){
                    //右滑
                    slideRigth();
                }else{
                    //左滑
                    slideLeft();
                }
            }else{ //竖直方向滑动
                if (stopy>starty){
                    //下滑
                    slideDwon();
                }else{
                    //上滑
                    slideUP();
                }


            }


        }





    }

    private void slideUP() {

        int prenumber=-1;
        for(int i =0;i<mRowNumber;i++) {
            for (int j = 0; j < mCollomNumber; j++) {

                final int number = mNumberItemMatrix[j][i].getNumber();

                if (number!=0){
                       if (number!=prenumber&&prenumber!=-1){
                           caculorList.add(prenumber);

                       }else if(prenumber!=-1){
                            caculorList.add(number*2);
                            currentScore+=number*2;
                            prenumber=-1;
                            continue;
                       }
                       prenumber=number;
                }

            }

            //把最后一个prenumber加入到集合中
            if (prenumber!=0&&prenumber!=-1)
                caculorList.add(prenumber);


            //把通过计算后合并的数字放到矩阵中
            for(int p=0;p<caculorList.size();p++){
                mNumberItemMatrix[p][i].setTextNumber(caculorList.get(p));
            }

            //合并长度之后的部分以0来填充
            for (int q=caculorList.size();q<mCollomNumber;q++){
                mNumberItemMatrix[q][i].setTextNumber(0);
            }

            //重置中间变量，为下次循环做准备。
            caculorList.clear();
            prenumber=-1;

        }


        Log.i(TAG, "slide up");
    }

    private void slideDwon() {

        int prenumber=-1;
        for(int i =0;i<mRowNumber;i++) {
            for (int j = mCollomNumber-1; j >=0; j--) {

                final int number = mNumberItemMatrix[j][i].getNumber();

                if (number!=0){
                    if (number!=prenumber&&prenumber!=-1){
                        caculorList.add(prenumber);

                    }else if(prenumber!=-1){
                        caculorList.add(number*2);
                        currentScore+=number*2;
                        prenumber=-1;
                        continue;
                    }
                    prenumber=number;
                }

            }

            //把最后一个prenumber加入到集合中
            if (prenumber!=0&&prenumber!=-1)
                caculorList.add(prenumber);


            //把通过计算后合并的数字放到矩阵中
            for(int p=mCollomNumber-1;p>=mCollomNumber-caculorList.size();p--){
                mNumberItemMatrix[p][i].setTextNumber(caculorList.get(mCollomNumber-1-p));
            }

            //合并长度之后的部分以0来填充
            for (int q=mCollomNumber-caculorList.size()-1;q>=0;q--){
                mNumberItemMatrix[q][i].setTextNumber(0);
            }

            //重置中间变量，为下次循环做准备。
            caculorList.clear();
            prenumber=-1;

        }


         Log.i(TAG, "slide down");
    }

    private void slideLeft() {


        int prenumber=-1;
        for(int i =0;i<mRowNumber;i++) {
            for (int j = 0; j < mCollomNumber; j++) {

                final int number = mNumberItemMatrix[i][j].getNumber();

                if (number!=0){
                    if (number!=prenumber&&prenumber!=-1){
                        caculorList.add(prenumber);

                    }else if(prenumber!=-1){
                        caculorList.add(number*2);
                        currentScore+=number*2;
                        prenumber=-1;
                        continue;
                    }
                    prenumber=number;
                }

            }

            //把最后一个prenumber加入到集合中
            if (prenumber!=0&&prenumber!=-1)
                caculorList.add(prenumber);


            //把通过计算后合并的数字放到矩阵中
            for(int p=0;p<caculorList.size();p++){
                mNumberItemMatrix[i][p].setTextNumber(caculorList.get(p));
            }

            //合并长度之后的部分以0来填充
            for (int q=caculorList.size();q<mCollomNumber;q++){
                mNumberItemMatrix[i][q].setTextNumber(0);
            }

            //重置中间变量，为下次循环做准备。
            caculorList.clear();
            prenumber=-1;

        }

        Log.i(TAG, "slide left");
    }

    private void slideRigth() {

        int prenumber=-1;
        for(int i =0;i<mRowNumber;i++) {
            for (int j = mCollomNumber-1; j>=0; j--) {

                final int number = mNumberItemMatrix[i][j].getNumber();

                if (number!=0){
                    if (number!=prenumber&&prenumber!=-1){
                        caculorList.add(prenumber);

                    }else if(prenumber!=-1){
                        caculorList.add(number*2);
                        currentScore+=number*2;
                        prenumber=-1;
                        continue;
                    }
                    prenumber=number;
                }

            }

            //把最后一个prenumber加入到集合中
            if (prenumber!=0&&prenumber!=-1)
                caculorList.add(prenumber);


            //把通过计算后合并的数字放到矩阵中
            for(int p=mRowNumber-1;p>=mRowNumber-caculorList.size();p--){
                mNumberItemMatrix[i][p].setTextNumber(caculorList.get(mRowNumber-1-p));
            }

            //合并长度之后的部分以0来填充
            for (int q=mRowNumber-caculorList.size()-1; q >=0;q--){
                mNumberItemMatrix[i][q].setTextNumber(0);
            }

            //重置中间变量，为下次循环做准备。
            caculorList.clear();
            prenumber =-1;

        }



        Log.i(TAG, "slide right");
    }


}
