package cn.lixiang.customviewtools.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import java.util.HashMap;

import cn.lixiang.customviewtools.R;


/**
 * 这是一个简约的折线图  适合展示一个趋势 而并非精确数据
 * Created by Administrator on 2017/7/7.
 */
public class MyViewLineChart extends View {
    //View 的宽和高
    private int mWidth, mHeight;
    //Y轴字体的大小
    private float mYAxisFontSize = 15;
    //X轴字体的距离白线的距离
    private float mXamongWline = 20;
    //X轴字体的大小
    private float mXAxisFontSize = 15;
    //底部线的颜色
    private int mLineColor = Color.parseColor("#FFFFFF");
    //给Y轴上字留多大的空间
    private float mFontAmongWidth = 50;
    //Y轴上的字体离Y轴的距离
    private float mYFontAmongWidth = 10;
    //每条线之间相隔的高度
    private float mLineAmongHeight = 60;
    //底部线条的宽度
    private float mStrokeWidth = 3.0f;

    //点的集合
    private HashMap<Integer, Integer> mPointMap;
    //X轴的文字
    private String[] mXAxis;
    //Y轴的文字
    private String[] mYAxis;


    private Bitmap mWBitmap;
    private Bitmap mRBitmap;
    private Bitmap mBitmap;
    private Paint mAxisPaint;
    private Paint mXline;
    private Paint mBitPaint;

    public MyViewLineChart(Context context) {
        this(context, null);
    }

    public MyViewLineChart(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MyViewLineChart(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initPaint();
        initData();
        initAttr(context,attrs);

    }

    private void initAttr(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.MyViewLineChart);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        if (widthMode == MeasureSpec.EXACTLY) {
            mWidth = widthSize;
        } else if (widthMode == MeasureSpec.AT_MOST) {
            throw new IllegalArgumentException("width must be EXACTLY,you should set like android:width=\"200dp\"");
        }

        if (heightMode == MeasureSpec.EXACTLY) {
            mHeight = heightSize;
        } else if (widthMeasureSpec == MeasureSpec.AT_MOST) {
            throw new IllegalArgumentException("height must be EXACTLY,you should set like android:height=\"200dp\"");
        }
        setMeasuredDimension(mWidth, mHeight);
    }

    /**
     * 初始化所以画笔
     */
    private void initPaint() {

        //画坐标线的轴的字体
        mAxisPaint = new Paint();
        mAxisPaint.setTextSize(mYAxisFontSize);
        mAxisPaint.setColor(Color.parseColor("#FFFFFF"));
        //画最下面的线
        mXline = new Paint();
        mXline.setStrokeWidth(mStrokeWidth);
        mXline.setAntiAlias(true);
        mXline.setColor(Color.parseColor("#FFFFFF"));

        //画虚线bitmap
        mBitPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mBitPaint.setFilterBitmap(true);
        mBitPaint.setDither(true);
    }

    /**
     * 初始化一些数据
     */
    private void initData() {
        //白折点
        mWBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.w_zhedian);
        //红折点
        mRBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.r_zhedian);
        //画虚线
        mBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.wangdoujia_line);

    }

    @Override
    protected void onDraw(Canvas canvas) {

        if (mXAxis.length > 0) {

            //测量字体大小
            float mXFontSize = mAxisPaint.measureText(mXAxis[0]);
            Log.d("BBBBB", mXFontSize + "字体大小");
            //测量Y轴文字的高度 用来画第一个数
            Paint.FontMetrics fm = mAxisPaint.getFontMetrics();
            int yItemHeight = (int) Math.ceil(fm.descent - fm.ascent);
            Log.d("BBBBB", yItemHeight + "字高度");

            //X点的坐标
            float[] mXpoint = new float[mXAxis.length];
            //Y点的坐标
            float[] mYpoint = new float[mXAxis.length];
            //每个字体的间隔
            float XAmongWidth = (mWidth - 7 * mXFontSize - mFontAmongWidth) / 6;
            //图形中Y坐标轴的总高度
            float mYHeight = mYAxis.length * (mBitmap.getHeight() + mLineAmongHeight);
            //开始绘制最下方的白线的字体
            for (int i = 0; i < mXAxis.length; i++) {
                canvas.drawText(mXAxis[i], mFontAmongWidth + XAmongWidth * i + mXFontSize * i, mHeight, mAxisPaint);
                //设置每个点的坐标
                mXpoint[i] = mFontAmongWidth + XAmongWidth * i + mXFontSize * (i - 1/2);
                //设置Y点的坐标
                mYpoint[i] =mHeight - mXamongWline - mYAxisFontSize -mStrokeWidth -  (mYHeight * mPointMap.get(i)/15);
                Log.d("BBBBB",mYpoint[i]+","+mPointMap.get(i));
            }

            //画底部的线
            canvas.drawLine(mFontAmongWidth, mHeight - mXamongWline - mYAxisFontSize, mWidth, mHeight - mXamongWline - mYAxisFontSize, mXline);
            for (int i = 0; i < mYAxis.length; i++) {
                //绘制虚线
                canvas.drawBitmap(mBitmap, mFontAmongWidth, mHeight - mStrokeWidth - mLineAmongHeight * (i + 1) - mBitmap.getHeight() * i - mXamongWline - mYAxisFontSize, mBitPaint);
            }

            float maxYTextSize = 0;
            //找出最大的Y周字体宽度
            for (int i = 0; i <mYAxis.length ; i++) {
                if (mAxisPaint.measureText(mYAxis[i]) >= maxYTextSize){
                    maxYTextSize = mAxisPaint.measureText(mYAxis[i]);
                }
            }



            //画Y周的数据
            for (int i = 0; i < mYAxis.length; i++) {
                if (i == 0){
                    canvas.drawText(mYAxis[i], mYFontAmongWidth+ Math.abs(maxYTextSize-mAxisPaint.measureText(mYAxis[i])), mHeight + mXFontSize/2 - mYAxisFontSize -mStrokeWidth-mXamongWline-mLineAmongHeight*(i+1), mAxisPaint);
                }else {
                    canvas.drawText(mYAxis[i], mYFontAmongWidth + Math.abs(maxYTextSize-mAxisPaint.measureText(mYAxis[i])), mHeight + mXFontSize/2 - mYAxisFontSize -mStrokeWidth-mXamongWline-mLineAmongHeight*(i+1) - mBitmap.getHeight()*i, mAxisPaint);
                }

            }
            //Log.d("BBBBB",Math.sqrt(Math.pow(5 - 2, 2) + Math.pow(6- 2, 2))+"wqqwq");

            //开始画点
            for (int i = 0; i < mXAxis.length; i++) {
                if (i < mXAxis.length -1){
                    //画两个点之间的连线
                    double pow = Math.sqrt(Math.pow(mXpoint[i + 1] - mXpoint[i], 2) + Math.pow(mYpoint[i + 1] - mYpoint[i], 2));
                    float vX = (float) (mWBitmap.getWidth() / 2 / pow * (mXpoint[i + 1] - mXpoint[i]));
                    float vY = (float) (mWBitmap.getHeight() /2 / pow *( Math.abs( (mYpoint[i + 1] - mYpoint[i]))));
                   // Log.v("BBBBB",vX+","+vY);
                    if (mPointMap.get(i+1) > mPointMap.get(i)){
                        canvas.drawLine(mXpoint[i] + vX,mYpoint[i] -vY,mXpoint[i+1] - vX,mYpoint[i+1]+vY,mXline);
                    }else {
                        canvas.drawLine(mXpoint[i] + vX,mYpoint[i] +vY,mXpoint[i+1] - vX,mYpoint[i+1]-vY,mXline);
                    }
                }

                if (i == mXAxis.length - 1){
                    //画红点
                    canvas.drawBitmap(mRBitmap,mXpoint[i]- mRBitmap.getWidth()/2,mYpoint[i]- mRBitmap.getHeight()/2,mBitPaint);
                }else {
                    //画白点
                    canvas.drawBitmap(mWBitmap,mXpoint[i] - mWBitmap.getWidth()/2,mYpoint[i] - mWBitmap.getHeight()/2,mBitPaint);
                }

            }


        }



       /* //画 Y 轴
        //存放每个Y轴的坐标
        int[] yPoints = new int[mYAxis.length];

        //计算Y轴 每个刻度的间距
        int yInterval = (int) ((mHeight - mYAxisFontSize*(mYAxis.length/2)) / (mYAxis.length -1));
        Log.d("AAAAA",  yInterval + "间距");


        //测量Y轴文字的高度 用来画第一个数
        Paint.FontMetrics fm = mAxisPaint.getFontMetrics();
        int yItemHeight = (int) Math.ceil(fm.descent - fm.ascent);
        Log.d("AAAAA", yItemHeight + "字高度");

        for (int i = 0; i < mYAxis.length; i++) {
            if (i == mYAxis.length -1){
                //画最后那条白线
                canvas.drawLine(40, mYAxisFontSize / 2 + i * yInterval + 2* mBitmap.getHeight(), DisplayUtils.dip2px(getContext(), mBitmap.getWidth()),mYAxisFontSize / 2 + i * yInterval, mXline);
            }else {
                canvas.drawText(mYAxis[i], 0, mYAxisFontSize + i * yInterval, mAxisPaint);
                yPoints[i] = (int) (mYAxisFontSize + i * yInterval);
            }

            canvas.drawBitmap(mBitmap, 40, mYAxisFontSize / 2 + i * yInterval, mBitPaint);

        }
        //Y轴总的高度
        int yAll = 5*yInterval + mBitmap.getHeight() * 8 ;

        //画数字上面的一行线
        Log.d("AAAAA", mHeight + "高度");
        Log.d("AAAAA", mBitmap.getHeight() + "虚线高度");



        if (mXAxis.length == 0) {
            return;
        }
        //画 X 轴
        //x轴的刻度集合
        int[] xPoints = new int[mXAxis.length];

        Log.e("wing", xPoints.length + "");
        //计算Y轴开始的原点坐标
        int xItemX = 40;

        //计算x轴 刻度间距
        float allSize = 0;
        for (int i = 0; i < mXAxis.length ; i++) {
            allSize+=mAxisPaint.measureText(mXAxis[i]);
        }
        int xInterval = (int) ((mWidth - mAxisPaint.measureText(mYAxis[0])- allSize - DisplayUtils.dip2px(getContext(),5)) / (mXAxis.length -1));

        //获取X轴刻度Y坐标
        int xItemY = (int) (mYAxisFontSize*(mYAxis.length/2) + (mYAxis.length - 1 ) * yInterval);



        for (int i = 0; i < mXAxis.length; i++) {
            if (i == 0){
                canvas.drawText(mXAxis[i], xItemX , xItemY, mAxisPaint);
                xPoints[i] = (int) (i * xInterval + xItemX );
            }else {
                float allXSize = 0;
                for (int j = 0; j < i; j++) {
                    allXSize += mAxisPaint.measureText(mXAxis[j]);
                }
                canvas.drawText(mXAxis[i], i * xInterval + xItemX+allXSize , xItemY, mAxisPaint);
                xPoints[i] = (int) (i * xInterval + xItemX+ allXSize  );
            }
            //            Log.e("wing", xPoints[i] + "");
        }

        //画点
        Paint pointPaint = new Paint();
        pointPaint.setColor(mLineColor);
        pointPaint.setStyle(Paint.Style.FILL);


        //画红点
        Paint redPointPaint = new Paint();
        redPointPaint.setColor(Color.parseColor("#FF0000"));


        //设置线
        Paint linePaint = new Paint();
        linePaint.setColor(mLineColor);
        linePaint.setAntiAlias(true);
        //设置线条宽度
        linePaint.setStrokeWidth(mStrokeWidth);



        for (int i = 0; i < mXAxis.length; i++) {
            if (mPointMap.get(i) == null) {
                throw new IllegalArgumentException("PointMap has incomplete data!");
            }

            //画点
            //canvas.drawBitmap(mwBitmap,xPoints[i], yPoints[mPointMap.get(i)],mBitPaint);

            Integer point = mPointMap.get(i);
            int yPoint = (int) (yAll-(point.doubleValue() / 15) * yAll);
            canvas.drawCircle(xPoints[i]+8, yPoint, mPointRadius, pointPaint);
            if (i == mXAxis.length - 1) {
                canvas.drawCircle(xPoints[i]+8, yPoint, mPointRadius, redPointPaint);
            }

            if (i > 0) {
                Integer PremapData = mPointMap.get(i - 1);

                int yPreValue = (int) (yAll- (PremapData.doubleValue() / 15) * yAll);
                Log.d("CCCCC",PremapData.doubleValue() / 15+"Y坐标变换");

                Integer NexmapData = mPointMap.get(i);
                int yNexValue = (int) (yAll-(NexmapData.doubleValue() / 15) * yAll);

                canvas.drawLine(xPoints[i - 1], yPreValue, xPoints[i], yNexValue, linePaint);
            }
        }
*/
    }

    /**
     * 设置map数据
     *
     * @param data
     */
    public void setData(HashMap<Integer, Integer> data) {
        mPointMap = data;
        invalidate();
    }

    /**
     * 设置Y轴文字
     *
     * @param yItem
     */
    public void setYItem(String[] yItem) {
        mYAxis = yItem;
    }

    /**
     * 设置X轴文字
     *
     * @param xItem
     */
    public void setXItem(String[] xItem) {
        mXAxis = xItem;
    }

    public void setLineColor(int color) {
        mLineColor = color;
        invalidate();
    }
}
