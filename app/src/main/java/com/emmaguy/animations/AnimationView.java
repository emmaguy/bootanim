package com.emmaguy.animations;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import java.util.Arrays;

public class AnimationView extends View {
    private final BootAnimationDrawable mDrawable;

    public AnimationView(Context context) {
        this(context, null);
    }

    public AnimationView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AnimationView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        mDrawable = new BootAnimationDrawable(Arrays.asList(Color.RED, Color.YELLOW, Color.GREEN, Color.BLUE));
        mDrawable.setCallback(this);
        setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Log.d("lolz", " x:" + event.getX() + " y:" + event.getY());
                return false;
            }
        });
    }

    public boolean getShouldDrawPath() {
        return mDrawable.getShouldDrawPath();
    }

    public void setShouldDrawPath(boolean shouldDrawPath) {
        mDrawable.setShouldDrawPath(shouldDrawPath);
    }

    public void setRange(float range) {
        mDrawable.setRange(range);
    }

    @Override
    protected void onVisibilityChanged(View changedView, int visibility) {
        super.onVisibilityChanged(changedView, visibility);
        if (visibility == VISIBLE) {
            mDrawable.start();
        } else {
            mDrawable.stop();
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mDrawable.setBounds(0, 0, w, h);
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);

        mDrawable.draw(canvas);
    }

    @Override
    protected boolean verifyDrawable(Drawable who) {
        return who == mDrawable || super.verifyDrawable(who);
    }
}
