package cn.lixiang.customviewsummary.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import java.util.List;

import cn.lixiang.customviewsummary.R;
import cn.lixiang.customviewsummary.bean.CakeData;

/**
 * Created by Administrator on 2017/7/4 0004.
 */

public class CakePicture extends View {

    private Paint mPaint;
    // 宽高
    private int mWidth, mHeight;
    //值
    private List<CakeData> mDatas;

    //默认开始角度
    private float mStartAngle = 0;

    public CakePicture(Context context) {
        this(context, null);
    }

    public CakePicture(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CakePicture(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.CakePicture);
        mStartAngle = array.getFloat(R.styleable.CakePicture_startAngle, mStartAngle);


        initPaint();
    }

    private void initPaint() {
        mPaint = new Paint();
        mPaint.setColor(Color.RED);
        mPaint.setAntiAlias(true);
        mPaint.setStrokeWidth(10f);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        mWidth = w;
        mHeight = h;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Log.d("BBBBB","11111");
        if (mDatas == null || mDatas.size() == 0) {
            return;
        }
        // 将画布坐标原点移动到中心位置
        //canvas.translate(mWidth / 2, mHeight / 2);
        Log.d("BBBBB",mDatas.size()+"22222");
        RectF rectF = new RectF(100, 100, 600, 600);
        for (int i = 0; i < mDatas.size(); i++) {
            if (i == 0) {
                mPaint.setColor(Color.RED);
            } else if (i == 1) {
                mPaint.setColor(Color.GRAY);
            } else if (i == 2) {
                mPaint.setColor(Color.GREEN);
            } else if (i == 3) {
                mPaint.setColor(Color.YELLOW);
            } else {
                mPaint.setColor(Color.WHITE);
            }
            canvas.drawArc(rectF, mStartAngle, mDatas.get(i).getAngle(), true, mPaint);
            mStartAngle += mDatas.get(i).getAngle();
        }


    }

    public void setData(List<CakeData> data) {
        Log.d("BBBBB",data.size()+"3333");
        mDatas = data;
        initData(mDatas);
    }

    private void initData(List<CakeData> data) {
        long total = 0;
        for (int i = 0; i < data.size(); i++) {
            total += data.get(i).getValue();
        }

        for (int i = 0; i < data.size(); i++) {
            //设置百分比
            data.get(i).setPercentage(data.get(i).getValue() * 100 / total);
            //设置扫描角度
            data.get(i).setAngle(data.get(i).getValue() * 360 / total);
        }

        invalidate();
    }
}
