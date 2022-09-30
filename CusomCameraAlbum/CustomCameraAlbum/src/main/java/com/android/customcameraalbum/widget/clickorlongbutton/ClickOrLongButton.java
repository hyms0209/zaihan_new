package com.android.customcameraalbum.widget.clickorlongbutton;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Cap;
import android.graphics.Paint.Style;
import android.graphics.RectF;
import android.os.Looper;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;

import com.android.customcameraalbum.R;
import com.android.customcameraalbum.camera.listener.ClickOrLongListener;

import java.util.ArrayList;

import com.android.customcameraalbum.common.utils.DisplayMetricsUtils;

import static com.android.customcameraalbum.camera.common.Constants.BUTTON_STATE_BOTH;
import static com.android.customcameraalbum.camera.common.Constants.BUTTON_STATE_ONLY_CLICK;
import static com.android.customcameraalbum.camera.common.Constants.BUTTON_STATE_ONLY_LONG_CLICK;

/**
 * 点击或者长按的按钮
 *
 * @author zhongjh
 */
public class ClickOrLongButton extends View {

    private static final String TAG = "ClickOrLongButton";
    /**
     * 满进度
     */
    private static final float FULL_PROGRESS = 1f;
    /**
     * 90度
     */
    private static final int NINETY_DEGREES = 90;
    /**
     * 录制时间
     */
    private float timeLimitInMils = 10000.0F;
    /**
     * 当前录制位置集，计算时间后的百分比
     */
    private final ArrayList<Float> mCurrentLocation = new ArrayList<>();
    /**
     * 当前录制的节点，以360度为单位
     */
    private Float mCurrentSumNumberDegrees = 0F;
    /**
     * 当前录制的时间点
     */
    private Long mCurrentSumTime = 0L;
    /**
     * 最短录制时间限制
     */
    private int mMinDuration = 1500;
    /**
     * 记录当前录制多长的时间秒
     */
    private long mRecordedTime;
    private static final float PROGRESS_LIM_TO_FINISH_STARTING_ANIM = 0.1F;
    private int mBoundingBoxSize;
    private int mOutCircleWidth;
    private float mInnerCircleRadius;

    private TouchTimeHandler touchTimeHandler;
    private boolean touchable;
    private boolean recordable;

    private Paint centerCirclePaint;
    private Paint outBlackCirclePaint;
    private Paint outMostBlackCirclePaint;
    private float innerCircleRadiusToDraw;
    /**
     * 外圈的画布
     */
    private RectF outMostCircleRect;
    private float outBlackCircleRadius;
    private float outMostBlackCircleRadius;
    private int colorRoundBorder;
    private int colorRecord;
    private int colorWhiteP60;
    private float startAngle270;
    private float percentInDegree;
    private float centerX;
    private float centerY;
    /**
     * 按下去显示的进度外圈
     */
    private Paint processBarPaint;
    /**
     * 静止状态时的外圈
     */
    private Paint outMostWhiteCirclePaint;
    /**
     * 静止状态时的进度外圈
     */
    private Paint outProcessCirclePaint;
    /**
     * 静止状态时的进度外圈
     */
    private Paint outProcessIntervalCirclePaint;
    private Paint translucentPaint;
    private int translucentCircleRadius = 0;
    private float outMostCircleRadius;
    private float innerCircleRadiusWhenRecord;
    private long btnPressTime;
    /**
     * 当前状态
     */
    private int recordState;
    /**
     * 未启动状态
     */
    private static final int RECORD_NOT_STARTED = 0;
    /**
     * 启动状态
     */
    private static final int RECORD_STARTED = 1;
    /**
     * 结束状态
     */
    private static final int RECORD_ENDED = 2;

    /**
     * 按钮可执行的功能状态（拍照,录制,两者）
     */
    private int mButtonState;
    /**
     * 是否分段录制的模式
     */
    private boolean mIsSectionMode;


    private final TouchTimeHandler.Task updateUITask = new TouchTimeHandler.Task() {
        @Override
        public void run() {
            if (mIsSectionMode && mCurrentLocation.size() > 0) {
                // 当处于分段录制模式并且有分段数据的时候，关闭启动前奏
                mMinDuration = 0;
            }
            long timeLapse = System.currentTimeMillis() - btnPressTime;
            mRecordedTime = (timeLapse - mMinDuration);
            mRecordedTime = mRecordedTime + mCurrentSumTime;
            float percent = mRecordedTime / timeLimitInMils;
            Log.d(TAG, "mCurrentSumTime " + percent);
            Log.d(TAG, "mRecordedTime " + mRecordedTime);
            if (!mActionDown && timeLapse >= 1) {
                boolean actionDown = mClickOrLongListener != null && (mButtonState == BUTTON_STATE_ONLY_CLICK || mButtonState == BUTTON_STATE_BOTH);
                Log.d(TAG, "actionDown " + actionDown);
                if (actionDown) {
                    mClickOrLongListener.actionDown();
                    mActionDown = true;
                }
            }

            if (timeLapse >= mMinDuration) {
                synchronized (ClickOrLongButton.this) {
                    if (recordState == RECORD_NOT_STARTED) {
                        recordState = RECORD_STARTED;
                        if (mClickOrLongListener != null) {
                            mClickOrLongListener.onLongClick();
                            // 如果禁止点击，那么就轮到长按触发actionDown
                            if (!mActionDown && mClickOrLongListener != null && mButtonState == BUTTON_STATE_ONLY_LONG_CLICK) {
                                // 如果禁止点击也不能触发该事件
                                mClickOrLongListener.actionDown();
                                mActionDown = true;
                            }
                        }
                    }
                }
                if (!recordable) {
                    return;
                }
                centerCirclePaint.setColor(colorRecord);
                outMostWhiteCirclePaint.setColor(colorRoundBorder);
                percentInDegree = (360.0F * percent);
                if (mIsSectionMode) {
                    if (mCurrentLocation.size() > 0 || (timeLapse - mMinDuration) >= mMinDuration) {
                        mCurrentSumNumberDegrees = percentInDegree;
                    }
                }


                if (percent <= FULL_PROGRESS) {
                    if (percent <= PROGRESS_LIM_TO_FINISH_STARTING_ANIM) {
                        float curOutCircleWidth = mOutCircleWidth;
                        processBarPaint.setStrokeWidth(curOutCircleWidth);
                        outProcessCirclePaint.setStrokeWidth(curOutCircleWidth);
                        outProcessIntervalCirclePaint.setStrokeWidth(curOutCircleWidth);
                        outMostWhiteCirclePaint.setStrokeWidth(curOutCircleWidth);
                        outBlackCircleRadius = (outMostCircleRadius - curOutCircleWidth / 2.0F);
                        outMostBlackCircleRadius = (curOutCircleWidth / 2.0F + (outMostCircleRadius));
                        outMostCircleRect = new RectF(centerX - outMostCircleRadius, centerY - outMostCircleRadius, centerX + outMostCircleRadius, centerY + outMostCircleRadius);
                        translucentCircleRadius = (int) (outMostCircleRadius);
                        innerCircleRadiusToDraw = innerCircleRadiusWhenRecord;
                    }
                    invalidate();
                } else {
                    reset();
                }
            }
        }
    };

    public ClickOrLongButton(Context paramContext) {
        super(paramContext);
        init();
    }

    public ClickOrLongButton(Context paramContext, AttributeSet paramAttributeSet) {
        super(paramContext, paramAttributeSet);
        init();
    }

    public ClickOrLongButton(Context paramContext, AttributeSet paramAttributeSet, int paramInt) {
        super(paramContext, paramAttributeSet, paramInt);
        init();
    }

    private void init() {
        touchable = recordable = true;
        // 整块
        mBoundingBoxSize = DisplayMetricsUtils.dip2px(100.0F);
        // 外线宽度
        mOutCircleWidth = DisplayMetricsUtils.dip2px(10F);
        mInnerCircleRadius = DisplayMetricsUtils.dip2px(32.0F);

        // 调取样式中的颜色
        TypedArray arrayRoundBorder = getContext().getTheme().obtainStyledAttributes(new int[]{R.attr.click_long_button_round_border});
        int defaultRoundBorderColor = ResourcesCompat.getColor(
                getResources(), R.color.click_long_button_round_border,
                getContext().getTheme());
        TypedArray arrayInnerCircleInOperation = getContext().getTheme().obtainStyledAttributes(new int[]{R.attr.click_long_button_inner_circle_in_operation});
        int defaultInnerCircleInOperationColor = ResourcesCompat.getColor(
                getResources(), R.color.click_long_button_inner_circle_in_operation,
                getContext().getTheme());
        TypedArray arrayInnerCircleNoOperation = getContext().getTheme().obtainStyledAttributes(new int[]{R.attr.click_long_button_inner_circle_no_operation});
        int defaultInnerCircleNoOperationColor = ResourcesCompat.getColor(
                getResources(), R.color.click_long_button_inner_circle_no_operation,
                getContext().getTheme());

        TypedArray arrayInnerCircleNoOperationInterval = getContext().getTheme().obtainStyledAttributes(new int[]{R.attr.click_button_inner_circle_in_operation_interval});
        int defaultInnerCircleNoOperationColorInterval = ResourcesCompat.getColor(
                getResources(), R.color.click_button_inner_circle_no_operation_interval,
                getContext().getTheme());

        colorRecord = arrayInnerCircleInOperation.getColor(0, defaultInnerCircleInOperationColor);
        colorRoundBorder = arrayRoundBorder.getColor(0, defaultRoundBorderColor);
        colorWhiteP60 = arrayInnerCircleNoOperation.getColor(0, defaultInnerCircleNoOperationColor);

        initProcessBarPaint();
        initOutCircle(arrayInnerCircleNoOperationInterval, defaultInnerCircleNoOperationColorInterval);
        initCenterCircle();


        // 状态为两者都可以
//        mButtonState = BUTTON_STATE_BOTH;
    }

    /**
     * 初始化内圈操作中样式
     */
    private void initProcessBarPaint() {
        processBarPaint = new Paint();
        processBarPaint.setColor(colorRecord);
        processBarPaint.setAntiAlias(true);
        processBarPaint.setStrokeWidth(mOutCircleWidth);
        processBarPaint.setStyle(Style.STROKE);
    }

    /**
     * 初始化外圈样式
     */
    private void initOutCircle(TypedArray arrayInnerCircleNoOperationInterval, int defaultInnerCircleNoOperationColorInterval) {
        int colorInterval = arrayInnerCircleNoOperationInterval.getColor(0, defaultInnerCircleNoOperationColorInterval);

        outMostWhiteCirclePaint = new Paint();
        outMostWhiteCirclePaint.setColor(colorRoundBorder);
        outMostWhiteCirclePaint.setAntiAlias(true);
        outMostWhiteCirclePaint.setStrokeWidth(mOutCircleWidth);
        outMostWhiteCirclePaint.setStyle(Style.STROKE);

        outProcessCirclePaint = new Paint();
        outProcessCirclePaint.setColor(colorRecord);
        outProcessCirclePaint.setAntiAlias(true);
        outProcessCirclePaint.setStrokeWidth(mOutCircleWidth);
        outProcessCirclePaint.setStyle(Style.STROKE);

        outProcessIntervalCirclePaint = new Paint();
        outProcessIntervalCirclePaint.setColor(colorInterval);
        outProcessIntervalCirclePaint.setAntiAlias(true);
        outProcessIntervalCirclePaint.setStrokeWidth(mOutCircleWidth);
        outProcessIntervalCirclePaint.setStyle(Style.STROKE);
    }

    /**
     * 初始化内圈未操作中样式
     */
    private void initCenterCircle() {
        int colorBlackP40 = ContextCompat.getColor(getContext(), R.color.black_forty_percent);
        int colorBlackP80 = ContextCompat.getColor(getContext(), R.color.black_eighty_percent);
        int colorTranslucent = ContextCompat.getColor(getContext(), R.color.circle_shallow_translucent_bg);

        centerCirclePaint = new Paint();
        centerCirclePaint.setColor(colorWhiteP60);
        centerCirclePaint.setAntiAlias(true);
        centerCirclePaint.setStyle(Style.FILL_AND_STROKE);
        outBlackCirclePaint = new Paint();
        outBlackCirclePaint.setColor(colorBlackP40);
        outBlackCirclePaint.setAntiAlias(true);
        outBlackCirclePaint.setStyle(Style.STROKE);
        outBlackCirclePaint.setStrokeWidth(1.0F);
        outMostBlackCirclePaint = new Paint();
        outMostBlackCirclePaint.setColor(colorBlackP80);
        outMostBlackCirclePaint.setAntiAlias(true);
        outMostBlackCirclePaint.setStyle(Style.STROKE);
        outMostBlackCirclePaint.setStrokeWidth(1.0F);
        translucentPaint = new Paint();
        translucentPaint.setColor(colorTranslucent);
        translucentPaint.setAntiAlias(true);
        translucentPaint.setStyle(Style.FILL_AND_STROKE);
        centerX = (mBoundingBoxSize / 2f);
        centerY = (mBoundingBoxSize / 2f);
        outMostCircleRadius = DisplayMetricsUtils.dip2px(37.0F);
        innerCircleRadiusWhenRecord = DisplayMetricsUtils.dip2px(32.0F);
        innerCircleRadiusToDraw = mInnerCircleRadius;
        outBlackCircleRadius = (outMostCircleRadius - mOutCircleWidth / 2.0F);
        outMostBlackCircleRadius = (outMostCircleRadius + mOutCircleWidth / 2.0F);
        startAngle270 = 270.0F;
        percentInDegree = 0.0F;
        outMostCircleRect = new RectF(centerX - outMostCircleRadius, centerY - outMostCircleRadius, centerX + outMostCircleRadius, centerY + outMostCircleRadius);
        touchTimeHandler = new TouchTimeHandler(Looper.getMainLooper(), updateUITask);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
//        canvas.drawCircle(centerX, centerY, translucentCircleRadius, translucentPaint);

        //center white-p40 circle  中心点+半径32，所以直接64就是内圈的宽高度了
        canvas.drawCircle(centerX, centerY, innerCircleRadiusToDraw, centerCirclePaint);

        // 静止状态时的外圈
        canvas.drawArc(outMostCircleRect, startAngle270, 360F, false, outMostWhiteCirclePaint);

        // 点击时的外圈进度
        canvas.drawArc(outMostCircleRect, startAngle270, percentInDegree, false, processBarPaint);

        // 静止状态时的外圈进度
        canvas.drawArc(outMostCircleRect, startAngle270, mCurrentSumNumberDegrees, false, outProcessCirclePaint);
        Log.d(TAG, "onDraw percentInDegree" + percentInDegree);
        Log.d(TAG, "onDraw mCurrentSumNumberDegrees" + mCurrentSumNumberDegrees);

        // 从这个顺序来看，即是从270为开始
        for (Float item : mCurrentLocation) {
            canvas.drawArc(outMostCircleRect, item, 1, false, outProcessIntervalCirclePaint);
            Log.d(TAG, "canvas.drawArc " + item);
        }

        canvas.drawCircle(centerX, centerY, outBlackCircleRadius, outBlackCirclePaint);
        canvas.drawCircle(centerX, centerY, outMostBlackCircleRadius, outMostBlackCirclePaint);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(mBoundingBoxSize, mBoundingBoxSize);
    }

    @Override
    @SuppressLint("ClickableViewAccessibility")
    public boolean onTouchEvent(MotionEvent event) {
        if (!touchable) {
            return false;
        }
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                Log.d(TAG, "onTouchEvent: down");
                // 是否支持长按
                boolean longClick = mClickOrLongListener != null
                        && (mButtonState == BUTTON_STATE_ONLY_LONG_CLICK || mButtonState == BUTTON_STATE_BOTH);
                if (longClick) {
                    startTicking();
                }
                break;
            case MotionEvent.ACTION_UP:
                Log.d(TAG, "onTouchEvent: up");
                reset();
                break;
            default:
                break;
        }
        return true;
    }

    /**
     * 重置
     */
    private void reset() {
        Log.d(TAG, "reset: " + recordState);
        synchronized (ClickOrLongButton.this) {
            if (recordState == RECORD_STARTED) {
                if (mClickOrLongListener != null) {
                    if (mRecordedTime < mMinDuration) {
                        // 回调录制时间过短
                        mClickOrLongListener.onLongClickShort(mRecordedTime);
                    } else {
                        // 回调录制结束
                        mClickOrLongListener.onLongClickEnd(mRecordedTime);
                    }
                }
                recordState = RECORD_ENDED;
            } else if (recordState == RECORD_ENDED) {
                // 回到初始状态
                recordState = RECORD_NOT_STARTED;
            } else {
                if (mClickOrLongListener != null) {
                    // 拍照
                    mClickOrLongListener.onClick();
                }
            }
        }
        mActionDown = false;
        touchTimeHandler.clearMsg();
        percentInDegree = 0.0F;
        centerCirclePaint.setColor(colorWhiteP60);
        outMostWhiteCirclePaint.setColor(colorRoundBorder);
        innerCircleRadiusToDraw = mInnerCircleRadius;
        outMostCircleRect = new RectF(centerX - outMostCircleRadius, centerY - outMostCircleRadius, centerX + outMostCircleRadius, centerY + outMostCircleRadius);
        translucentCircleRadius = 0;
        processBarPaint.setStrokeWidth(mOutCircleWidth);
        outProcessCirclePaint.setStrokeWidth(mOutCircleWidth);
        outMostWhiteCirclePaint.setStrokeWidth(mOutCircleWidth);
        outProcessIntervalCirclePaint.setStrokeWidth(mOutCircleWidth);
        outBlackCircleRadius = (outMostCircleRadius - mOutCircleWidth / 2.0F);
        outMostBlackCircleRadius = (outMostCircleRadius + mOutCircleWidth / 2.0F);
        invalidate();
    }

    public boolean isTouchable() {
        return touchable;
    }

    public boolean isRecordable() {
        return recordable;
    }

    public void setRecordable(boolean recordable) {
        this.recordable = recordable;
    }

    public void setTouchable(boolean touchable) {
        this.touchable = touchable;
    }

    private void startTicking() {
        synchronized (ClickOrLongButton.this) {
            if (recordState != RECORD_NOT_STARTED) {
                recordState = RECORD_NOT_STARTED;
            }
        }
        btnPressTime = System.currentTimeMillis();
        touchTimeHandler.sendLoopMsg(0L, 16L);
    }

    /**
     * 数据设置成适合当前圆形
     * <p>
     * // 计算方式1：270至360是一个初始点，类似0-90
     * // 计算方式2: 所以如果是小于90点，就直接+270
     * // 计算方式3：如果大于等于90点，就直接-90
     *
     * @return numberDegrees
     */
    private float getNumberDegrees(float numberDegrees) {
        if (numberDegrees >= NINETY_DEGREES) {
            numberDegrees = numberDegrees - 90;
        } else {
            numberDegrees = numberDegrees + 270;
        }
        return numberDegrees;
    }

    /**
     * 按钮回调接口
     */
    private ClickOrLongListener mClickOrLongListener;
    /**
     * 判断是否已经调用过isActionDwon,结束后重置此值
     */
    private boolean mActionDown;

    // region 对外方法

    /**
     * 设置最长录制时间
     *
     * @param duration 时间
     */
    public void setDuration(int duration) {
        timeLimitInMils = duration;
    }

    /**
     * 最短录制时间
     *
     * @param duration 时间
     */
    public void setMinDuration(int duration) {
        mMinDuration = duration;
    }

    /**
     * 设置当前已录制的时间，用于分段录制
     */
    public void setCurrentTime(ArrayList<Long> currentTimes) {
        mCurrentLocation.clear();
        mCurrentSumNumberDegrees = 0F;
        mCurrentSumTime = 0L;
        // 当前录制时间集
        // 计算百分比红点
        for (int i = 0; i < currentTimes.size(); i++) {
            // 获取当前时间占比
            float percent = currentTimes.get(i) / timeLimitInMils;
            // 根据360度，以这个占比计算是具体多少度
            float numberDegrees = percent * 360;
            // 数据设置规范,适合当前圆形
            mCurrentLocation.add(getNumberDegrees(numberDegrees));
            mCurrentSumNumberDegrees = numberDegrees;

            mCurrentSumTime = currentTimes.get(i);
            Log.d(TAG, "setCurrentTime mCurrentSumTime " + mCurrentSumTime);
            Log.d(TAG, "setCurrentTime mCurrentSumNumberDegrees " + mCurrentSumNumberDegrees);
        }
    }

    /**
     * 设置是否分段模式，分段录制的动画稍微不一样
     */
    public void setSectionMode(boolean isSectionMode) {
        this.mIsSectionMode = isSectionMode;
    }

    /**
     * 设置回调接口
     *
     * @param clickOrLongListener 回调接口
     */
    public void setRecordingListener(ClickOrLongListener clickOrLongListener) {
        this.mClickOrLongListener = clickOrLongListener;
    }

    /**
     * 设置按钮功能（点击和长按）
     *
     * @param buttonStateBoth {@link com.android.customcameraalbum.camera.common.Constants#BUTTON_STATE_ONLY_CLICK 只能点击
     * @link com.android.customcameraalbum.camera.common.Constants#BUTTON_STATE_ONLY_LONG_CLICK 只能长按
     * @link com.android.customcameraalbum.camera.common.Constants#BUTTON_STATE_BOTH 两者皆可
     * }
     */
    public void setButtonFeatures(int buttonStateBoth) {
        this.mButtonState = buttonStateBoth;
    }

    public int getButtonFeatures() {
        return this.mButtonState;
    }

    /**
     * 重置状态
     */
    public void resetState() {
        // 回到初始状态
        recordState = RECORD_NOT_STARTED;
    }

    // endregion

}
