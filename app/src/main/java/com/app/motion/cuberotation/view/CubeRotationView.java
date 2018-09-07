package com.app.motion.cuberotation.view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Camera;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.SeekBar;

public class CubeRotationView extends ViewGroup implements SeekBar.OnSeekBarChangeListener {

    private final String TAG = this.getClass().getSimpleName();
    private final float FACTOR = 0.2f;
    private int mWidth;
    private int mHeight;
    private Camera mCamera;
    private Matrix matrix;
    private int mItemWidth;
    private ValueAnimator mValueAnimator;


    public CubeRotationView(Context context) {
        this(context, null);
    }

    public CubeRotationView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CubeRotationView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        setWillNotDraw(false);
        mCamera = new Camera();
        matrix = new Matrix();
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthResult = widthMeasureSpec;
        int heightResult = heightMeasureSpec;
        measureChildren(widthMeasureSpec, heightMeasureSpec);
        final int childCount = this.getChildCount();
        if (childCount > 0) {
            int widthMode = MeasureSpec.getMode(widthMeasureSpec);
            int heightMode = MeasureSpec.getMode(heightMeasureSpec);
            if (widthMode != MeasureSpec.EXACTLY) {
                View firstChild = this.getChildAt(0);
                int itemWidth = firstChild.getMeasuredWidth();
                widthResult = MeasureSpec.makeMeasureSpec((int) (itemWidth * (1 + FACTOR)), MeasureSpec.EXACTLY);
            }
            if (heightMode != MeasureSpec.EXACTLY) {
                View firstChild = this.getChildAt(0);
                int itemHeight = firstChild.getMeasuredHeight();
                heightResult = MeasureSpec.makeMeasureSpec((int) (itemHeight * (1 + FACTOR)), MeasureSpec.EXACTLY);
            }
            setMeasuredDimension(widthResult, heightResult);

        } else {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }

    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mWidth = w;
        mHeight = h;

    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        final int childCount = this.getChildCount();
        if (childCount > 0) {
            View firstView = this.getChildAt(0);
            int itemWidth = mItemWidth = firstView.getMeasuredWidth();
            int itemHeight = firstView.getMeasuredHeight();
            int top = (mHeight - itemHeight) / 2;
            int left = (mWidth - itemWidth) / 2;
            for (int i = 0; i < childCount; i++) {
                final View currentView = this.getChildAt(i);
                currentView.layout(left, top, left + itemWidth, top + itemHeight);
                left += itemWidth;
            }
        }
    }


    @Override
    protected void dispatchDraw(Canvas canvas) {
        Log.d("wanghaha", "----dispatchDraw");
        final int childCount = this.getChildCount();
        for (int i = 0; i < childCount; i++) {
            drawChild(canvas, i);
        }
    }


    private void drawChild(Canvas canvas, int index) {
        final View childView = this.getChildAt(index);
        int currentScrollX = getPaddingLeft() + getScrollX();
        int gap = (mWidth - mItemWidth) / 2;
        int currentLeft = childView.getLeft() - gap;
        //右边超出了屏幕不绘制
        if (currentScrollX + mItemWidth < currentLeft) {
            return;
        }
        //左边超出了屏幕不绘制
        if (currentScrollX > currentLeft + mItemWidth) {
            return;
        }

        float centerX = currentScrollX > currentLeft ? (currentLeft + mItemWidth + gap)
                : currentLeft + gap;
        float centerY = mHeight / 2;
        float degree = (currentScrollX - currentLeft) * 1.0f / mItemWidth * 90 * -1;
        Log.d("wanghaha", "---value:" + (currentScrollX - currentLeft));
        Log.d("wanghaha", "---degree:" + degree);
        if (degree > 90 || degree < -90) {
            return;
        }
        canvas.save();
        mCamera.save();
        mCamera.rotateY(degree);
        mCamera.getMatrix(matrix);

        matrix.preTranslate(-1 * centerX, -1 * centerY);
        matrix.postTranslate(centerX, centerY);
        canvas.concat(matrix);
        drawChild(canvas, childView, getDrawingTime());
        mCamera.restore();
        canvas.restore();
    }


    public void start() {
        mValueAnimator = ValueAnimator.ofInt(0, mItemWidth * 3);
        mValueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                int mAnimatedValue = (int) valueAnimator.getAnimatedValue();
                Log.d("wanghaha", "----animator value:" + mAnimatedValue);
                scrollTo((int) mAnimatedValue, 0);
                invalidate();
            }
        });

        mValueAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationRepeat(Animator animation) {
                scrollTo(0, 0);
            }
        });

        mValueAnimator.setInterpolator(new LinearInterpolator());
        mValueAnimator.setDuration(3000);
        mValueAnimator.setRepeatMode(ValueAnimator.RESTART);
        mValueAnimator.setRepeatCount(ValueAnimator.INFINITE);
        mValueAnimator.start();
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        int distance = (int) (progress * 1.0f / 100 * mItemWidth * 3);
        this.scrollTo(distance, 0);
        postInvalidate();
        invalidate();
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }
}
