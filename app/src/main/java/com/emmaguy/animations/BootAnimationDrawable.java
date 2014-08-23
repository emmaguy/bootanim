package com.emmaguy.animations;

import android.animation.ValueAnimator;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.Rect;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.animation.LinearInterpolator;

import java.util.ArrayList;
import java.util.List;

public class BootAnimationDrawable extends Drawable implements Animatable, ValueAnimator.AnimatorUpdateListener {
    private static final int CIRCLE_RADIUS = 25;
    private static final long ANIMATION_DURATION = 6000;

    private boolean mShouldDrawPath = false;

    private final Matrix mMatrix;
    private final Paint mLinePaint;

    private PathMeasure mPathMeasure;
    private Path mAnimPath;
    private float mPathLength;

    private ValueAnimator mCirclePositionAnimator;
    private final ArrayList<Paint> mCirclePaints = new ArrayList<Paint>();

    private final float[] mPos = new float[2];
    private final float[] mTan = new float[2];

    private boolean mIsRunning;

    private float mCenterX;
    private float mCenterY;
    private int mWidth;
    private int mHeight;
    private float mRange;

    public BootAnimationDrawable(List<Integer> circles) {
        mLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mLinePaint.setStyle(Paint.Style.STROKE);
        mLinePaint.setStrokeWidth(5);
        mLinePaint.setColor(Color.WHITE);

        mMatrix = new Matrix();

        initialiseCircles(circles);
    }

    private void initialisePath() {
        mAnimPath = new Path();
        mAnimPath.moveTo(mCenterX, mCenterY);
        mAnimPath.quadTo(mWidth * 0.26f, mHeight * 0.41f, mWidth * 0.44f, mHeight * 0.40f);
        mAnimPath.quadTo(mWidth * 0.69f, mHeight * 0.47f, mWidth * 0.59f, mHeight * 0.67f);
        mAnimPath.quadTo(mWidth * 0.43f, mHeight * 0.86f, mWidth * 0.16f, mHeight * 0.75f);
        mAnimPath.quadTo(mWidth * 0.03f, mHeight * 0.58f, mWidth * 0.24f, mHeight * 0.32f);
        mAnimPath.quadTo(mWidth * 0.59f, mHeight * 0.24f, mWidth * 0.76f, mHeight * 0.34f);
        mAnimPath.quadTo(mWidth * 0.96f, mHeight * 0.45f, mWidth * 0.77f, mHeight * 0.62f);
        mAnimPath.quadTo(mWidth * 0.52f, mHeight * 0.72f, mWidth * 0.34f, mHeight * 0.49f);
        mAnimPath.quadTo(mWidth * 0.32f, mHeight * 0.19f, mWidth * 0.62f, mHeight * 0.06f);
        mAnimPath.quadTo(mWidth * 0.88f, mHeight * 0.13f, mWidth * 0.90f, mHeight * 0.27f);
        mAnimPath.quadTo(mWidth * 0.66f, mHeight * 0.44f, mWidth * 0.51f, mHeight * 0.50f);
        mAnimPath.close();

        mPathMeasure = new PathMeasure(mAnimPath, false);
        mPathLength = mPathMeasure.getLength();
    }

    private void initialisePositionAnimator() {
        mCirclePositionAnimator = ValueAnimator.ofFloat(0, mPathLength);
        mCirclePositionAnimator.setDuration(ANIMATION_DURATION);
        mCirclePositionAnimator.addUpdateListener(this);
        mCirclePositionAnimator.setRepeatMode(ValueAnimator.RESTART);
        mCirclePositionAnimator.setRepeatCount(ValueAnimator.INFINITE);
        mCirclePositionAnimator.setInterpolator(new LinearInterpolator());
    }

    public boolean getShouldDrawPath() {
        return mShouldDrawPath;
    }

    public void setShouldDrawPath(boolean shouldDrawPath) {
        mShouldDrawPath = !shouldDrawPath;
    }

    @Override
    protected void onBoundsChange(Rect bounds) {
        super.onBoundsChange(bounds);

        mCenterX = bounds.width() / 2.0f;
        mCenterY = bounds.height() / 2.0f;
        mWidth = bounds.width();
        mHeight = bounds.height();
        Log.d("lol", "x: " + mCenterX + " y: " + mCenterY + " w: " + bounds.width() + " h: " + bounds.height());
        stop();
        initialisePath();
        initialisePositionAnimator();
        start();
    }

    private void initialiseCircles(List<Integer> numberOfCircles) {
        for (Integer circleColour : numberOfCircles) {
            Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
            paint.setColor(circleColour);

            mCirclePaints.add(paint);
        }
    }

    @Override
    public void start() {
        if (isRunning()) {
            return;
        }

        mIsRunning = true;
        if (mCirclePositionAnimator != null) {
            mCirclePositionAnimator.start();
        }
        invalidateSelf();
    }

    @Override
    public void stop() {
        if (!isRunning()) {
            return;
        }

        mIsRunning = false;
        if (mCirclePositionAnimator != null) {
            mCirclePositionAnimator.cancel();
        }
        invalidateSelf();
    }

    @Override
    public boolean isRunning() {
        return mIsRunning;
    }

    @Override
    public void draw(Canvas canvas) {
        if (mShouldDrawPath) {
            canvas.drawPath(mAnimPath, mLinePaint);
        }

        float offset = mPathLength / mCirclePaints.size();
        int i = 1;
        for (Paint p : mCirclePaints) {
            float animatedValue = (Float) mCirclePositionAnimator.getAnimatedValue() + offset * i;
            if (animatedValue >= mPathLength) {
                animatedValue -= mPathLength;
            }

            mPathMeasure.getPosTan(animatedValue, mPos, mTan);

            mMatrix.reset();

            int circleRadius = scaleCircleIfCloseToCenter();
            canvas.drawCircle(mPos[0], mPos[1], circleRadius, p);

            i++;
        }
    }

    private int scaleCircleIfCloseToCenter() {
        int circleRadius = CIRCLE_RADIUS;

        if (withinRangeOf(mPos[0], mCenterX, mRange) && withinRangeOf(mPos[1], mCenterY, mRange)) {
            // scale down so it appears to be going into the distance
            float difference = Math.min(Math.abs(mPos[0] - mCenterX), Math.abs(mPos[1] - mCenterY));

            circleRadius *= Math.max(difference * 2 / mRange, 0.3f);
        }
        return Math.min(CIRCLE_RADIUS, circleRadius);
    }

    private boolean withinRangeOf(float position, float y, float range) {
        return between(y, position - range, position + range);
    }

    private static boolean between(float x, float min, float max) {
        return x > min && x < max;
    }

    @Override
    public void setAlpha(int alpha) {

    }

    @Override
    public void setColorFilter(ColorFilter cf) {

    }

    @Override
    public int getOpacity() {
        return 0;
    }

    @Override
    public void onAnimationUpdate(ValueAnimator animation) {
        invalidateSelf();
    }

    public void setRange(float range) {
        mRange = range;
    }
}
