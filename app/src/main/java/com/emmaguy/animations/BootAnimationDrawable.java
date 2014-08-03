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
import android.view.animation.LinearInterpolator;

import java.util.ArrayList;
import java.util.List;

public class BootAnimationDrawable extends Drawable implements Animatable, ValueAnimator.AnimatorUpdateListener {
    private static final int CIRCLE_RADIUS = 10;
    private static final int ANIMATION_PADDING = 100;

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

    public BootAnimationDrawable(List<Integer> circles) {
        mLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mLinePaint.setStyle(Paint.Style.STROKE);
        mLinePaint.setStrokeWidth(1);
        mLinePaint.setColor(Color.BLACK);

        mMatrix = new Matrix();

        initialiseCircles(circles);
    }

    private void initialisePath(int width) {
        mAnimPath = new Path();
        mAnimPath.moveTo(ANIMATION_PADDING, ANIMATION_PADDING);
        mAnimPath.lineTo(width - ANIMATION_PADDING, ANIMATION_PADDING);
        mAnimPath.lineTo(ANIMATION_PADDING, width / 2 - ANIMATION_PADDING);
        mAnimPath.lineTo(width - ANIMATION_PADDING, width / 2 - ANIMATION_PADDING);
        mAnimPath.lineTo(ANIMATION_PADDING, ANIMATION_PADDING);
        mAnimPath.close();

        mPathMeasure = new PathMeasure(mAnimPath, false);
        mPathLength = mPathMeasure.getLength();
    }

    private void initialisePositionAnimator() {
        mCirclePositionAnimator = ValueAnimator.ofFloat(0, mPathLength);
        mCirclePositionAnimator.setDuration(3000);
        mCirclePositionAnimator.addUpdateListener(this);
        mCirclePositionAnimator.setRepeatMode(ValueAnimator.RESTART);
        mCirclePositionAnimator.setRepeatCount(ValueAnimator.INFINITE);
        mCirclePositionAnimator.setInterpolator(new LinearInterpolator());
    }

    @Override
    protected void onBoundsChange(Rect bounds) {
        super.onBoundsChange(bounds);

        stop();
        initialisePath(bounds.width());
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
        if(mCirclePositionAnimator != null) {
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
        if(mCirclePositionAnimator != null) {
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
        canvas.drawPath(mAnimPath, mLinePaint);

        float offset = mPathLength / mCirclePaints.size();
        int i = 1;
        for (Paint p : mCirclePaints) {
            float animatedValue = (Float) mCirclePositionAnimator.getAnimatedValue() + offset * i;
            if (animatedValue >= mPathLength) {
                animatedValue -= mPathLength;
            }

            mPathMeasure.getPosTan(animatedValue, mPos, mTan);

            mMatrix.reset();
            mMatrix.postTranslate(mPos[0] - CIRCLE_RADIUS, mPos[1] - CIRCLE_RADIUS);
            canvas.drawCircle(mPos[0], mPos[1], CIRCLE_RADIUS, p);

            i++;
        }
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
}
