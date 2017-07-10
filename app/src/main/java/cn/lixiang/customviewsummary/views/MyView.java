package cn.lixiang.customviewsummary.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

/**
 * Created by Administrator on 2017/7/7 0007.
 */

public class MyView extends View{

    private Paint mPaint;
    private Paint mPaint1;
    private int mWidth;
    private int mHeight ;


    public MyView(Context context) {
        this(context,null);
    }

    public MyView(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public MyView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initPaint();
    }

    private void initPaint() {
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(2f);


        mPaint1 = new Paint();
        mPaint1.setAntiAlias(true);
        mPaint1.setStyle(Paint.Style.STROKE);
        mPaint1.setStrokeWidth(2f);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        mWidth = w;
        mHeight = h;
        super.onSizeChanged(w, h, oldw, oldh);
    }

    @Override
    protected void onDraw(Canvas canvas) {

        RectF rectF = new RectF(0,0,200,200);
        mPaint.setColor(Color.GREEN);
        canvas.drawRect(rectF,mPaint);
        canvas.save();

        canvas.translate(mWidth/2f,mHeight/2);
        RectF rectF1 = new RectF(0,-100,100,0);
        mPaint1.setColor(Color.RED);
        canvas.drawRect(rectF1,mPaint1);
        //保存所以状态
        Log.d("BBBBB",canvas.getSaveCount()+"");
        //保存的只是画布的状态而已
        //canvas.restore();
        Log.d("BBBBB",canvas.getSaveCount()+"");

        RectF rectF3 = new RectF(0,0,50,50);
        mPaint1.setColor(Color.BLACK);
        canvas.drawRect(rectF3,mPaint1);
    }
}
