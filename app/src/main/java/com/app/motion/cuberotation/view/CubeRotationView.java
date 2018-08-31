package com.app.motion.cuberotation.view;

import android.content.Context;
import android.graphics.Camera;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;

public class CubeRotationView extends ViewGroup implements SeekBar.OnSeekBarChangeListener {

    private final String TAG = this.getClass().getSimpleName();
    private final float FACTOR = 0.2f;
    private int mWidth;
    private int mHeight;
    private Camera mCamera;
    private Matrix matrix;
    private Matrix mRotateMatrix;

    private int testAngle;

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
        mRotateMatrix = new Matrix();
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
            int itemWidth = firstView.getMeasuredWidth();
            int itemHeight = firstView.getMeasuredHeight();
            int top = (mHeight - itemHeight) / 2;
            int left = (mWidth - itemWidth) / 2;
            for (int i = 0; i < childCount; i++) {
                final View currentView = this.getChildAt(i);
                currentView.layout(left, top, left + itemWidth, top + itemHeight);
                top += itemHeight;
            }
        }
    }


    @Override
    protected void dispatchDraw(Canvas canvas) {
        Log.d("wanghaha", "----dispatchDraw");

        canvas.save();
        canvas.setMatrix(mRotateMatrix);
        mCamera.save();
        final int childCount = this.getChildCount();
        for (int i = 0; i < childCount; i++) {
            drawChild(canvas, i);
        }
        mCamera.restore();
        canvas.restore();
    }


    private void drawChild(Canvas canvas, int index) {
        final View childView = this.getChildAt(index);
        if (index == 0) {
            drawChild(canvas, childView, getDrawingTime());
            return;
        }
        if (index == 1) {
            mCamera.rotateX(-90);
            mCamera.getMatrix(matrix);
            matrix.preTranslate(-1 * mWidth / 2, -1 * childView.getTop());
            matrix.postTranslate(mWidth / 2, childView.getTop());
            canvas.concat(matrix);
            drawChild(canvas, childView, getDrawingTime());
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.save();
        canvas.drawLine(getWidth() / 2, 0, getWidth() / 2, getHeight(), new Paint(Paint.ANTI_ALIAS_FLAG));
        canvas.drawLine(0, getHeight() / 2, getWidth(), getHeight() / 2, new Paint(Paint.ANTI_ALIAS_FLAG));
        canvas.restore();
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

        int value = (int) (progress * 1.0 / 100 * 360);
        testAngle = value;
        mCamera.save();
        mCamera.translate(0, 0, 600);
        mCamera.rotateY(value);
        mCamera.getMatrix(mRotateMatrix);
        mCamera.restore();
        mRotateMatrix.preTranslate(-1 * getWidth() / 2, -1 * getHeight() / 2);
        mRotateMatrix.postTranslate(getWidth() / 2, getHeight() / 2);
        invalidate();
        postInvalidate();
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }
}
