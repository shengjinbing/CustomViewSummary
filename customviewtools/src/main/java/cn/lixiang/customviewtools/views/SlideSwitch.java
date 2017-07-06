package cn.lixiang.customviewtools.views;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by Administrator on 2017/7/6 0006.
 */

public class SlideSwitch extends View {

    private Paint mPaint;
    private Bitmap mSlide_btn_bitmap;
    private Bitmap mSwitch_bg;

    private final int ACTION_NONE = 0;
    private final int  ACTION_DOWN = 1;
    private final int ACTION_MOVE = 2;
    private final int ACTION_UP = 3;

    private boolean isOpen = true;
    private int state = ACTION_NONE;

    private int mWidth;
    private int mBtn_width;
    private int mHeight;
    private int mBtn_height;


    public SlideSwitch(Context context) {
        this(context, null);
    }

    public SlideSwitch(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SlideSwitch(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttr(context, attrs);
        initPaint();
    }

    private void initAttr(Context context, AttributeSet attrs) {
    }

    /**
     * @param res 设置滑块资源
     */
    public void setSlideBtnRes(int res) {
        mSlide_btn_bitmap = BitmapFactory.decodeResource(getResources(), res);
        mBtn_width = mSlide_btn_bitmap.getWidth();
        mBtn_height = mSlide_btn_bitmap.getHeight();

    }

    /**
     * @param res 设置switch背景资源
     */
    public void setSlideBgRes(int res) {
        mSwitch_bg = BitmapFactory.decodeResource(getResources(), res);

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (mSwitch_bg != null) {
            int width = mSwitch_bg.getWidth();
            int height = mSwitch_bg.getHeight();
            setMeasuredDimension(width, height);
        } else {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        mWidth = w;
        mHeight = h;
        super.onSizeChanged(w, h, oldw, oldh);
    }


    private void initPaint() {
        mPaint = new Paint();

    }


    @Override
    protected void onDraw(Canvas canvas) {
        if (mSwitch_bg != null) {
            canvas.drawBitmap(mSwitch_bg, 0, 0, mPaint);
        }

        if (mSlide_btn_bitmap != null) {
            switch (state){
                case ACTION_DOWN:
                case ACTION_MOVE:
                    if (isOpen){
                        if (mDx <= mBtn_width/2f){
                            canvas.drawBitmap(mSlide_btn_bitmap, 0, 0, mPaint);
                        }else if (mDx > mBtn_width/2f && mDx < mWidth - mBtn_width/2f){
                            canvas.drawBitmap(mSlide_btn_bitmap, mDx - mBtn_width/2f, 0, mPaint);
                        }else if (mDx > mWidth - mBtn_width/2f){
                            canvas.drawBitmap(mSlide_btn_bitmap, mWidth-mBtn_width, 0, mPaint);
                        }
                    }else {
                        if (mDx > mWidth - mBtn_width/2f){
                            canvas.drawBitmap(mSlide_btn_bitmap, mWidth-mBtn_width, 0, mPaint);
                        }else if (mDx < mWidth - mBtn_width/2f && mDx > mBtn_width/2f){
                            canvas.drawBitmap(mSlide_btn_bitmap, mDx - mBtn_width/2f, 0, mPaint);
                        }else if (mDx <  mBtn_width/2f){
                            canvas.drawBitmap(mSlide_btn_bitmap, 0, 0, mPaint);
                        }
                    }
                    break;
                case ACTION_NONE:
                case ACTION_UP:
                    if (isOpen){
                        canvas.drawBitmap(mSlide_btn_bitmap, 0, 0, mPaint);
                    }else if (!isOpen){
                        canvas.drawBitmap(mSlide_btn_bitmap, mWidth-mBtn_width, 0, mPaint);

                    }
                    break;
                default:
                    break;
            }



        }
    }

    private float mDx;
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                state = ACTION_DOWN;
                mDx = event.getX();
                invalidate();
                break;
            case MotionEvent.ACTION_MOVE:
                state = ACTION_MOVE;
                mDx = event.getX();
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                state = ACTION_UP;
                mDx =  event.getX();
                if (mDx < mWidth/2f && !isOpen){
                    isOpen = true;
                    mOpendState.getSwitchSate(isOpen);
                }else if (mWidth/2f < mDx && isOpen){
                    isOpen = false;
                    mOpendState.getSwitchSate(isOpen);
                }
                invalidate();
                break;
            default:
                break;
        }

        return true;
    }
    OnSwitchOpendState mOpendState;
    public void setOnSwitchOpendState(OnSwitchOpendState opendState){
        mOpendState = opendState;
    }

    public interface OnSwitchOpendState{
        void getSwitchSate(boolean isOpen);
    }
}
