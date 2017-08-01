package com.iblogstreet.jbox2dsimpleuse.widget;

/*
 *  @项目名：  JBox2DSimpleUse 
 *  @包名：    com.iblogstreet.jbox2dsimpleuse.widget
 *  @文件名:   CollisionView
 *  @创建者:   Army
 *  @创建时间:  2017/7/30 23:14
 *  @描述：    TODO
 */

import android.content.Context;
import android.graphics.Canvas;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;

import com.iblogstreet.jbox2dsimpleuse.jbox.JboxImpl;

public class CollisionView
        extends FrameLayout
{
    private JboxImpl mJboxImpl;

    public CollisionView(@NonNull Context context) {
        this(context, null);
    }

    public CollisionView(@NonNull Context context, @Nullable AttributeSet attrs)
    {
        this(context, attrs, 0);
    }

    public CollisionView(@NonNull Context context,
                         @Nullable AttributeSet attrs,
                         @AttrRes int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
        setWillNotDraw(false);
        mJboxImpl = new JboxImpl(getResources().getDisplayMetrics().density);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mJboxImpl.setWolrdSize(w, h);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        mJboxImpl.createWorld();//创建世界
        //创建TAG,创建Body
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            View view = getChildAt(i);
            if (!mJboxImpl.isBodyView(view) || changed) {
                mJboxImpl.createBody(view);
            }
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        mJboxImpl.startWorld();
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            View view = getChildAt(i);
            if (mJboxImpl.isBodyView(view)) {
                view.setX(mJboxImpl.getViewX(view));
                view.setY(mJboxImpl.getViewY(view));
                view.setRotation(mJboxImpl.getViewRotate(view));
            }
        }
        invalidate();
    }

    public void onSensorChanged(float x, float y) {
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            View view = getChildAt(i);
            if (mJboxImpl.isBodyView(view)) {
                mJboxImpl.applyLinearImpulse(x, y, view);
            }
        }
    }
}
