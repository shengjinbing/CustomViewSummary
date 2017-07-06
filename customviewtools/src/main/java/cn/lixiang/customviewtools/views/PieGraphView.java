package cn.lixiang.customviewtools.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Transformation;

import cn.lixiang.customviewtools.R;
import cn.lixiang.customviewtools.bean.PieGroupInfo;
import cn.lixiang.customviewtools.bean.PieItem;
import cn.lixiang.customviewtools.bean.PieItemGroup;
import cn.lixiang.customviewtools.utils.GeomTool;


/**
 * 饼状图控件。
 * Created by hxw on 2016/8/24.
 */
public class PieGraphView extends View {
    private static final int ANIM_MODE_NONE = 0;
    private static final int ANIM_MODE_ROTATE = 1;
    private static final int ANIM_MODE_SHOW_OUT = 2;
    private static final int ANIM_MODE_GROW = 3;
    public static final int GROW_MODE_MOVE_OUT = 1;
    public static final int GROW_MODE_BOLD = 2;
    /**
     * 增大某扇形时，它超出的高度占大圆半径的比例（这些值的范围限定后续进行处理）
     */
    private float mGrowWidthFactor = 0.1f;
    /**
     * 圆环占大圆半径的比例(这些值的范围限定后续进行处理)
     */
    private float mRingWidthFactor = 0.35f;
    /**
     * 根据mRingWidthFactor计算得出，减少计算，不要修改
     */
    private float mRingWidth;
    /**
     * 被增大扇形的左右角度间距
     */
    private  float mGrownPieGap = 2f;
    private RectF mBigOval = new RectF(), mSmallOval = new RectF(), mGrownOval = new RectF();
    /**
     * 整个绘制覆盖的正方形区域，包括增长后的
     */
    private RectF mCanvasRect = new RectF();
    /**
     * 小圆内接正方形区域
     */
    private RectF mTitleRect = new RectF();
    private Paint mPaintOuter, mPaintInner, mTextPaint, mItemCenterPaint;
    private Animation mAnimShowOut;
    private Animation mAnimRotate;
    private Animation mAnimGrow;
    private int mGrowMode = GROW_MODE_MOVE_OUT;
    /**
     * 绘制各个扇形的开始角度。控制在-360~360 调用setStartAngle对我赋值！
     */
    private int mStartAngle = 90;
    /**
     * 对从mStartAngle绘制后的扇形旋转的角度。控制在-360~360 调用setRotation对我赋值！
     */
    private int mRotation = 0;
    private int[] colors; //
    private float[] angles;
    private float mShowOutProgress = 1f;
    private int mRotateDelta;
    private float mRotateAnimProgress;
    private int mAnimMode = ANIM_MODE_NONE;
    private int mCurrentItem = -1;
    private int mCurrentGroup;
    private Point mItemCenter = new Point();
    private int mRotateDuration = 500;
    private int mGrowDuration = 200;
    private int mShowOutDuration = 500;
    private PieGroupInfo[] mGroupViewInfos;
    private PieItemGroup[] mGroups;

    // region 增大某扇形相关字段
    private int mGrownItem = -1;
    private float mGrowProgress = 1f;
    /**
     * 根据mGrowWidthFactor计算得出，减少计算，不要修改
     */
    private float mGrownWidth;
    // endregion

    public PieGraphView(Context context, AttributeSet attrs) {
        super(context, attrs);
        getAttributes(context, attrs);
        initPaints();
        initAnims();
        initAction();
    }

    private void initAction() {
        this.setLongClickable(false);
    }

    /**
     * 初始化动画
     */
    private void initAnims() {
        //第一次执行的动画
        mAnimShowOut = new Animation() {
            @Override
            protected void applyTransformation(final float interpolatedTime, final Transformation t) {
                //interpolatedTime为执行进度，执行完毕的时候为1.0f
                mShowOutProgress = interpolatedTime;
                invalidate();
                if (interpolatedTime >= 1.0f) {
                    //取消动画
                    cancel();
                    // mAnimMode = ANIM_MODE_NONE;
                    // 目前动画都是通过Animation完成的，而不是在onDraw中递归invalidate实现，所以为了
                    // 避免两个连续的动画产生“跳跃”，将下一个旋转动画放到下个UI循环中
                    post(new Runnable() {
                        @Override
                        public void run() {
                            int item = Math.max(0, angles.length - 1);
                            //执行下一个动画
                            setCurrentItem(item, false);
                        }
                    });
                }
            }
        };
        mAnimShowOut.setDuration(mShowOutDuration);

        mAnimRotate = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                mRotateAnimProgress = interpolatedTime;
                // 旋转操作可以通过改变开始绘制的角度，也可以旋转整个View
                // 设置旋转角度后会使得可点击区域不再是沿着水平/竖直方向的正方形，所以不采用
                invalidate();

                if (interpolatedTime >= 1.0f) {
                    cancel();
                    // mAnimMode = ANIM_MODE_NONE;
                    setRotation(mRotation + mRotateDelta);
                    mRotateDelta = 0;

                    post(new Runnable() {
                        @Override
                        public void run() {
                            growItem(mCurrentItem);
                        }
                    });
                }
            }
        };
        mAnimRotate.setDuration(mRotateDuration);

        //实现有一个扇形凸起的动画
        mAnimGrow = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, final Transformation t) {
                mGrowProgress = interpolatedTime;
                invalidate();
                if (interpolatedTime >= 1.0f) {
                    cancel();
                    mAnimMode = ANIM_MODE_NONE;
                    if (mItemChangeListener != null) {
                        PieItemGroup group = mGroups[mCurrentGroup];
                        mItemChangeListener.onItemSelected(group, group.items[mCurrentItem]);
                    }
                }
            }
        };
        mAnimGrow.setDuration(mGrowDuration);
    }

    private void getAttributes(Context context, AttributeSet attrs) {
        if (attrs == null)
            return;
        // TODO 设置各种xml attributes
        TypedArray attributes = context.obtainStyledAttributes(attrs,R.styleable.PieGraphView);
        mStartAngle=attributes.getInt(R.styleable.PieGraphView_piestartAngle,mStartAngle);
        mGrowWidthFactor=attributes.getFloat(R.styleable.PieGraphView_growWidthFactor,mGrowWidthFactor);
        mRingWidthFactor=attributes.getFloat(R.styleable.PieGraphView_ringWidthFactor,mRingWidthFactor);
        mGrownPieGap=attributes.getFloat(R.styleable.PieGraphView_grownPieGap,mGrownPieGap);

    }

    private void setRotation(int value) {
        mRotation = value % 360;
    }

    /**
     * 初始化一些画笔
     */
    private void initPaints() {
        mPaintOuter = new Paint();
        mPaintOuter.setAntiAlias(true);
        mPaintOuter.setStyle(Paint.Style.FILL);
        mPaintOuter.setColor(Color.WHITE);

        mPaintInner = new Paint(mPaintOuter);
        mPaintInner.setColor(Color.WHITE);

        mTextPaint = new Paint(mPaintOuter);
        mTextPaint.setColor(Color.BLACK);
        mTextPaint.setTextAlign(Paint.Align.CENTER);

        mItemCenterPaint = new Paint(mPaintOuter);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (mGroups == null)
            return;

        canvas.drawColor(Color.TRANSPARENT);
        switch (mAnimMode) {
            case ANIM_MODE_ROTATE:
                //第二部分进行选择调用
                drawRotatedPie(canvas);
                //画内圆白色的
                canvas.drawArc(mSmallOval, 0, 360, true, mPaintInner);
                break;
            case ANIM_MODE_SHOW_OUT:
                //开始进行动画展开绘画
                animDrawProceed(canvas, mShowOutProgress);
                //画内圆白色的
                canvas.drawArc(mSmallOval, 0, 360, true, mPaintInner);
                break;
            case ANIM_MODE_GROW:
            case ANIM_MODE_NONE:
                //画扇形动画
                drawGrownPie(canvas);
                break;
        }

        // 使用自定义ViewGroup去设置中间标题区域更好，或者直接使用帧布局覆盖在上面更灵活。
        // 可以把mTitleRect暴漏给外界访问，这里简单的绘制文本。
        PieItemGroup group = mGroups[mCurrentGroup];
        String title = "[" + group.id + "] [" + group.items[mCurrentItem].id + "]";
        Paint.FontMetricsInt fontMetrics = mTextPaint.getFontMetricsInt();
        float y = (mTitleRect.bottom + mTitleRect.top - fontMetrics.bottom - fontMetrics.top) / 2;
        canvas.drawText(title, mTitleRect.centerX(), y, mTextPaint);
    }

    /**
     * 画扇形动画的部分
     * @param canvas
     */
    private void drawGrownPie(Canvas canvas) {
        if (angles == null)
            return;
        final float rotatedStart = this.mStartAngle + mRotation;
        float rotatedEnd = rotatedStart + 360f;
        float currentItemStart = 0f, currentItemSweep = 360f;
        for (int i = angles.length - 1; i >= 0; i--) {
            float itemAngle = angles[i] + 0.5f;
            float sweepStart = rotatedEnd - itemAngle;
            float sweep = itemAngle;

            mPaintOuter.setColor(colors[i]);
            RectF oval = mBigOval;

            if (sweepStart < rotatedStart) {
                sweepStart = rotatedStart;
                sweep = rotatedEnd - rotatedStart;
            }

            if (mGrownItem == i) {
                sweepStart += mGrownPieGap;
                sweep -= 2 * mGrownPieGap;

                currentItemStart = sweepStart;
                currentItemSweep = sweep;

                float padding = mGrownWidth * (1f - mGrowProgress);
                mGrownOval.set(mCanvasRect);
                mGrownOval.inset(padding, padding);
                oval = mGrownOval;
            }

            // 绘制扇形圆环
            canvas.drawArc(oval, sweepStart, sweep, true, mPaintOuter);

            // 绘制圆环上扇形的中心“点”
            int middleAngle = (int) (sweepStart + sweep / 2);
            float radius = (mSmallOval.width() + mRingWidth) / 2f;
            if (mGrownItem == i && mGrowMode == GROW_MODE_MOVE_OUT) {
                radius += mGrowProgress * mGrownWidth;
            } else if (mGrownItem == i && mGrowMode == GROW_MODE_BOLD) {
                radius += mGrowProgress * mGrownWidth / 2f;
            }
            calcAngleMiddleInRing(middleAngle, radius, mItemCenter);
            drawItemCenterIcon(canvas, middleAngle, colors[i], mItemCenter);

            if (sweepStart < rotatedStart)
                break;
            rotatedEnd -= itemAngle;
        }

        // 绘制内圆，分当前扇形和非当前扇形两部分
        mGrownOval.set(mSmallOval);
        float grownRadius = mGrownWidth * mGrowProgress;

        float otherStart = currentItemStart + currentItemSweep;
        float otherSweep = 360f - currentItemSweep;
        if (mGrowMode == GROW_MODE_MOVE_OUT) {
            mGrownOval.inset(-grownRadius, -grownRadius);
        } else if (mGrowMode == GROW_MODE_BOLD) {
            // mGrownOval.inset(grownRadius, grownRadius);
        }

        canvas.drawArc(mGrownOval, currentItemStart - 0.5f, currentItemSweep + 1f, true, mPaintInner);
        canvas.drawArc(mSmallOval, otherStart, otherSweep, true, mPaintInner);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        updateBounds(w, h);
    }

    private void updateBounds(int w, int h) {
        GeomTool.calcMaxSquareRect(new RectF(0, 0, w, h), mCanvasRect);
        float maxDiameter = mCanvasRect.width();

        // 公式： maxDiameter = bigOvalDiameter + bigOvalDiameter * mGrowWidthFactor
        float bigOvalRadius = maxDiameter / (1 + mGrowWidthFactor) / 2;
        mGrownWidth = bigOvalRadius * mGrowWidthFactor;
        mRingWidth = bigOvalRadius * mRingWidthFactor;

        float bigOvalPadding = mGrownWidth;
        mBigOval.set(mCanvasRect);
        mBigOval.inset(bigOvalPadding, bigOvalPadding);

        float smallOvalPadding = mGrownWidth + mRingWidth;
        mSmallOval.set(mCanvasRect);
        mSmallOval.inset(smallOvalPadding, smallOvalPadding);

        float smallOvalRadius = bigOvalRadius - mRingWidth;
        float titleRectHalfWidth = smallOvalRadius / 1.4142f; // 1.4142f是根号2的大约值
        float titleRectPadding = (maxDiameter / 2) - titleRectHalfWidth;
        mTitleRect.set(mCanvasRect);
        mTitleRect.inset(titleRectPadding, titleRectPadding);

        mTextPaint.setTextSize(mTitleRect.height() / 8f);
    }

    /**
     * 根据进度值，以动画方式画饼状图。动画方式：圆环行进的方式从头到尾出现直至完全展示。
     *
     * @param canvas   onDraw中得到的画布
     * @param progress 进度 0~1
     */
    private void animDrawProceed(Canvas canvas, float progress) {
        float rotatedStart = this.mStartAngle;
        float end = rotatedStart + 360f * progress;
        Log.d("BBBBB","rotatedStart"+rotatedStart);
        Log.d("BBBBB","end"+end);
        drawPieFromEnd(canvas, rotatedStart, end);
    }

    /**
     * 画圆，起始角度根据mStartAngle和当前的旋转（旋转是相对起始角度绘制后的圆）得到
     *
     * @param canvas onDraw中得到的画布
     */
    private void drawRotatedPie(Canvas canvas) {
        int currentRotate = mRotation + (int) (mRotateAnimProgress * mRotateDelta);
        float rotatedStart = this.mStartAngle + currentRotate;
        float end = rotatedStart + 360f;
        drawPieFromEnd(canvas, rotatedStart, end);
    }

    /**
     * 从尾部开始绘制圆环，只绘制endAngle到startAngle之间的，不一定绘制所有圆环。
     * 从头部和尾部选择一个开始绘制，其实是没有影响的
     *
     * @param canvas
     * @param startAngle
     * @param endAngle
     */
    private void drawPieFromEnd(Canvas canvas, float startAngle, float endAngle) {
        if (angles == null)
            return;
        for (int i = angles.length - 1; i >= 0; i--) {
            float itemAngle = angles[i] + 0.5f;
            float sweepStart = endAngle - itemAngle;
            mPaintOuter.setColor(colors[i]);

            float radius = mSmallOval.width() / 2f + mRingWidth / 2f;
            if (sweepStart >= startAngle) {
                Log.d("BBBBB","大");
                canvas.drawArc(mBigOval, sweepStart, itemAngle, true, mPaintOuter);
                //下面是画每个item中间的点
                int middleAngle = (int) (sweepStart + itemAngle / 2);
                calcAngleMiddleInRing(middleAngle, radius, mItemCenter);
                drawItemCenterIcon(canvas, middleAngle, colors[i], mItemCenter);
            } else {
                Log.d("BBBBB","小");
                /**
                 * 这里主要是判断，加入第一次angles[i] > 更新需要扫描的角度的情况
                 */
                itemAngle = endAngle - startAngle;
                canvas.drawArc(mBigOval, startAngle, itemAngle, true, mPaintOuter);
                int middleAngle = (int) (startAngle + itemAngle / 2);
                calcAngleMiddleInRing(middleAngle, radius, mItemCenter);
                drawItemCenterIcon(canvas, middleAngle, colors[i], mItemCenter);
                break;
            }
            endAngle -= itemAngle;
        }
    }

    /**
     * 让整个圆旋转到targetDegree的角度，旋转是相对mStartAngle开始绘制的圆而言
     *
     * @param targetDegree 应该介于0-360，是从第一个扇形片段作为0度算出来的角度，不是从X正轴开始的角度
     * @param smartRotate  是否抄近路旋转？
     */
    private void rotateToDegree(float targetDegree, boolean smartRotate) {
        // 使得 targetDegree 介于0-360
        targetDegree = (targetDegree + 360) % 360;
        int targetRotate = (int) -targetDegree;

        mRotateDelta = (targetRotate - mRotation) % 360;

        if (smartRotate) {
            // 将旋转控制在180度内
            if (mRotateDelta > 180) {
                mRotateDelta = mRotateDelta - 360;
            } else if (mRotateDelta < -180) {
                mRotateDelta = 360 + mRotateDelta;
            }
        }

        runAnimRotate();
    }

    private void runAnimRotate() {
        mAnimMode = ANIM_MODE_ROTATE;
        clearAnimation();
        mAnimRotate.cancel();
        startAnimation(mAnimRotate);
    }

    private float calcCenter(int itemIndex) {
        float centerDegree = 0;

        if (itemIndex == 0) {
            centerDegree = angles[0] / 2;
        } else if (itemIndex == angles.length - 1) {
            centerDegree = 360 - angles[angles.length - 1] / 2;
        } else {
            for (int i = 0; i < itemIndex; i++) {
                centerDegree += angles[i];
            }
            centerDegree += angles[itemIndex] / 2;
        }

        return centerDegree;
    }

    private int calcClickItem(float x, float y) {
        if (angles == null)
            return -1;
        final float centerX = mBigOval.centerX();
        final float centerY = mBigOval.centerY();
        float outerRadius = mBigOval.width() / 2;
        float innerRadius = mSmallOval.width() / 2;

        // 计算点击的坐标(x, y)和圆中心点形成的角度，角度从0-360，顺时针增加
        int clickedDegree = GeomTool.calcAngle(x, y, centerX, centerY);
        double clickRadius = GeomTool.calcDistance(x, y, centerX, centerY);

        // 判断是否刚好点击角度在当前选择的item的范围？这时圆环的宽度需要特别处理
        float currentItemHalfAngle = (angles[mCurrentItem] - 2 * mGrownPieGap) / 2f;
        float currentItemStartAngle = mStartAngle - currentItemHalfAngle;
        float currentItemEndAngle = mStartAngle + currentItemHalfAngle;

        if (clickedDegree < currentItemEndAngle && clickedDegree > currentItemStartAngle) {
            // 重新计算innerRadius、outerRadius ** 点击处理的时候动画肯定结束了
            if (mGrowMode == GROW_MODE_MOVE_OUT) {
                innerRadius += mGrownWidth;
                outerRadius += mGrownWidth;
            } else if (mGrowMode == GROW_MODE_BOLD) {
                outerRadius += mGrownWidth;
            }

            if (clickRadius < outerRadius && clickRadius > innerRadius) {
                return mCurrentItem;
            }
        }

        if (clickRadius < innerRadius) {
            // 点击发生在小圆内部，也就是点击到标题区域
            onTitleRegionClicked();
            return -1;
        } else if (clickRadius > outerRadius) {
            // 点击发生在大圆环外
            return -2;
        }

        // 计算出来的clickedDegree是整个View原始的，被点击item需要考虑startAngle。
        int startAngle = mStartAngle + mRotation;
        int angleStart = startAngle;
        for (int i = 0; i < angles.length; i++) {
            int itemStart = (angleStart + 360) % 360;
            float end = itemStart + angles[i];
            if (end >= 360f) {
                if (clickedDegree >= itemStart && clickedDegree < 360)
                    return i;
                if (clickedDegree >= 0 && clickedDegree < (end - 360))
                    return i;
            } else {
                if (clickedDegree >= itemStart && clickedDegree < end) {
                    return i;
                }
            }

            angleStart += angles[i];
        }

        return -3;
    }

    private void onTitleRegionClicked() {
        showOutGroup((mCurrentGroup + 1) % mGroups.length);
    }

    private void showOutGroup(int index) {
        mRotation = 0;
        mRotateDelta = 0;
        mCurrentItem = 0;
        //当前第几组，默认选中0
        mCurrentGroup = index;
        if (mGroups == null)
            return;
        PieGroupInfo info = mGroupViewInfos[index];
        angles = info.angles;
        Log.d("BBBBB",angles[0]+","+angles[1]+","+angles[2]+","+angles[3]+"");
        colors = info.colors;
        runShowOutAnim();
    }

    private void runShowOutAnim() {
        clearAnimation();
        //当前模式,z展开模式
        mAnimMode = ANIM_MODE_SHOW_OUT;
        startAnimation(mAnimShowOut);
    }

    private void growItem(int item) {
        mGrownItem = item;
        runGrowAnim();
    }

    private void runGrowAnim() {
        mAnimMode = ANIM_MODE_GROW;
        clearAnimation();
        startAnimation(mAnimGrow);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN && mAnimMode == ANIM_MODE_NONE) {
            int item = calcClickItem(event.getX(), event.getY());
            if (item >= 0 && item < angles.length) {
                setCurrentItem(item, true);
            }
        }

        return super.onTouchEvent(event);
    }

    /**
     * @param item
     * @param smartRotate
     */
    private void setCurrentItem(int item, boolean smartRotate) {
        if (mCurrentItem != item) {
            mCurrentItem = item;
            //如果选中的不是当前的item则回复到原来第一次进来的位子
            rotateToDegree(calcCenter(item), smartRotate);
        } else {
            growItem(item);
        }
    }

    /**
     * 根据传递的角度计算角度落在圆环上的中点
     *
     * @param angle       角度，从X正轴开始计算，0-360
     * @param resultPoint 携带计算出的坐标点
     */
    private void calcAngleMiddleInRing(int angle, float radius, Point resultPoint) {
        if (resultPoint == null)
            resultPoint = new Point();
        float cx = mSmallOval.centerX();
        float cy = mSmallOval.centerY();

        GeomTool.calcCirclePoint(angle, radius, cx, cy, resultPoint);
    }

    private void drawItemCenterIcon(Canvas canvas, int middleAngle, int itemColor, Point center) {
        int color = itemColor / 2;
        mItemCenterPaint.setColor(color);
        // 这里当角度非常小的时候，半径有可能显示不完全——超出
        canvas.drawCircle(center.x, center.y, mRingWidth / 4f, mItemCenterPaint);
    }

    public RectF getTitleRect() {
        return new RectF(mTitleRect);
    }


    /**
     * 传递数据
     * @param groups
     */
    public void setData(PieItemGroup[] groups) {
        //数据为空，结束
        if (groups == null || groups.length == 0)
            return;

        mGroups = groups;
        mGroupViewInfos = new PieGroupInfo[groups.length];
        for (int i = 0; i < groups.length; i++) {
            PieItemGroup group = groups[i];
            PieItem[] items = group.items;
            // calcGroupAngles
            PieGroupInfo info = new PieGroupInfo();
            info.colors = new int[items.length];
            info.angles = new float[items.length];

            double total = 0;
            //将外部传进来的数据进行包装，属性只要颜色和对应的旋转角度
            for (int j = 0; j < items.length; j++) {
                info.colors[j] = items[j].color;
                total += items[j].value;
            }

            for (int j = 0; j < items.length; j++) {
                info.angles[j] = (float) ((items[j].value / total) * 360f);
            }

            mGroupViewInfos[i] = info;
        }
        //默认选中第几组
        showOutGroup(0);
    }

    private ItemChangeListener mItemChangeListener;

    public interface ItemChangeListener {
        void onItemSelected(PieItemGroup group, PieItem item);
    }

    public void setItemChangeListener(ItemChangeListener listener) {
        mItemChangeListener = listener;
    }

    /**
     * 设置选择的项目增长（高亮）的模式，GROW_MODE_xxx常量
     */
    public void setGrowMode(int mode) {
        switch (mode) {
            case GROW_MODE_MOVE_OUT:
            case GROW_MODE_BOLD:
                mGrowMode = mode;
                break;
        }
    }

    /**
     * 设置圆环宽度系数，最终宽度=大圆半径 * 宽度系数
     *
     * @param factor 0 < factor <= 0.5
     */
    public void setRingWidthFactor(float factor) {
        if (factor <= 0)
            return;
        factor = GeomTool.clamp(factor, 0f, 0.5f);
        mRingWidthFactor = factor;
    }

    /**
     * 选择某项进行高亮时，此项增长的半径值得系数，系数是大圆半径的半分比
     *
     * @param factor 0 < factor <= 0.2
     */
    public void setGrowWidthFactor(float factor) {
        if (factor <= 0)
            return;
        factor = GeomTool.clamp(factor, 0f, 0.2f);
        mGrowWidthFactor = factor;
    }
}