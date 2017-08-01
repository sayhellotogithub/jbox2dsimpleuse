package com.iblogstreet.jbox2dsimpleuse.jbox;

/*
 *  @项目名：  JBox2DSimpleUse 
 *  @包名：    com.iblogstreet.jbox2dsimpleuse.jbox
 *  @文件名:   JboxImpl
 *  @创建者:   Army
 *  @创建时间:  2017/7/30 23:22
 *  @描述：    TODO
 */

import android.util.Log;
import android.view.View;

import com.iblogstreet.jbox2dsimpleuse.R;

import org.jbox2d.collision.shapes.CircleShape;
import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.collision.shapes.Shape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.FixtureDef;
import org.jbox2d.dynamics.World;

import java.util.Random;

public class JboxImpl {
    private final static String TAG = "JboxImpl";
    private World mWorld;//模拟世界
    private float mWorldFrequency = 1f / 60f;//模拟世界频率
    private int mWidth, mHeight;
    private       float  mDensity = 0.5f;
    private       float  mRatio   = 50;//坐标映射比例
    private final Random mRandom  = new Random();

    private int   mVelocityIterations = 5;//速率迭代器
    private int   mPositionIterations = 30;////迭代次数
    private float mFriction           = 0.8f;//摩擦系数
    private float mRestitution        = 0.5f;//补偿系数

    public JboxImpl(float density) {
        Log.e(TAG, "JboxImpl:density " + density);
        this.mDensity = 0.5f;
    }

    public void setWolrdSize(int width, int height) {
        this.mHeight = height;
        this.mWidth = width;
    }

    public void createWorld() {
        if (mWorld == null) {
            mWorld = new World(new Vec2(0, 10f));
            updateLeftAndRightBounds();
            updateTopAndBottomBounds();
        }
    }

    public void startWorld() {
        if (mWorld != null) {
            mWorld.step(mWorldFrequency, mVelocityIterations, mPositionIterations);
        }
    }

    public void createBody(View view) {
        BodyDef bodyDef = new BodyDef();
        bodyDef.setType(BodyType.DYNAMIC); //设置动态属性，物体可以运动

        bodyDef.position.set(switchPositionToBody(view.getX() + view.getWidth() / 2),
                             switchPositionToBody(view.getY() + view.getHeight() / 2));
        Shape   shape    = null;
        Boolean isCircle = (Boolean) view.getTag(R.id.view_circle_tag);
        if (isCircle != null && isCircle) {
            shape = createCircleShape(switchPositionToBody(view.getWidth() / 2));
            FixtureDef fixtureDef = createFixtureDef(shape);
            Body       body       = mWorld.createBody(bodyDef);
            body.createFixture(fixtureDef);
            view.setTag(R.id.view_body_tag, body);
            body.setLinearVelocity(new Vec2(mRandom.nextFloat(), mRandom.nextFloat()));
        } else {
            Log.i(TAG, "createBody view tag is not circle");
        }
    }

    private Shape createCircleShape(float v) {
        CircleShape circleShape = new CircleShape();
        circleShape.setRadius(v / 2);
        return circleShape;
    }

    //创建材料
    private FixtureDef createFixtureDef(Shape shape) {
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.setShape(shape);
        fixtureDef.setDensity(mDensity);
        fixtureDef.setFriction(mFriction);//摩擦系数
        fixtureDef.setRestitution(mRestitution);//补偿系数
        return fixtureDef;
    }

    /**
     * 设置顶部及底部边界
     */
    private void updateTopAndBottomBounds() {
        BodyDef bodyDef = new BodyDef();
        bodyDef.setType(BodyType.STATIC);//设置静态属性，物体不运动
        PolygonShape box       = new PolygonShape();
        float        boxWidth  = switchPositionToBody(mWidth);
        float        boxHeight = switchPositionToBody(mRatio);
        box.setAsBox(boxWidth, boxHeight);//设置为矩形

        FixtureDef fixtureDef = createFixtureDef(box);

        bodyDef.position.set(0, -boxHeight);

        Body topBody = mWorld.createBody(bodyDef);
        topBody.createFixture(fixtureDef);

        bodyDef.position.set(0, switchPositionToBody(mHeight) + boxHeight);

        Body bottomBody = mWorld.createBody(bodyDef);
        bottomBody.createFixture(fixtureDef);

    }

    /**
     * 设置左边及右边界
     */
    private void updateLeftAndRightBounds() {
        BodyDef bodyDef = new BodyDef();
        bodyDef.setType(BodyType.STATIC);
        PolygonShape box       = new PolygonShape();
        float        boxWidth  = switchPositionToBody(mRatio);
        float        boxHeight = switchPositionToBody(mHeight);
        box.setAsBox(boxWidth, boxHeight);//设置为矩形
        FixtureDef fixtureDef = createFixtureDef(box);

        bodyDef.position.set(-boxWidth, 0);

        Body leftBody = mWorld.createBody(bodyDef);
        leftBody.createFixture(fixtureDef);

        bodyDef.position.set(switchPositionToBody(mWidth) + boxWidth, 0);

        Body rightBody = mWorld.createBody(bodyDef);
        rightBody.createFixture(fixtureDef);

    }


    private float switchPositionToBody(float viewPosition) {
        return viewPosition / mRatio;
    }

    public float switchPositionToVIew(float bodyPosition) {
        return bodyPosition * mRatio;
    }

    public boolean isBodyView(View view) {
        Body body = (Body) view.getTag(R.id.view_body_tag);
        return body != null;
    }

    public float getViewX(View view) {
        Body body = (Body) view.getTag(R.id.view_body_tag);
        if (body != null) {
            return switchPositionToVIew(body.getPosition().x) - view.getWidth() / 2;
        } else { return 0; }
    }

    public float getViewY(View view) {
        Body body = (Body) view.getTag(R.id.view_body_tag);
        if (body != null) {
            return switchPositionToVIew(body.getPosition().y) - view.getHeight() / 2;
        } else { return 0; }
    }

    public float getViewRotate(View view) {
        //角度转弧度
        Body body = (Body) view.getTag(R.id.view_body_tag);
        if (body != null) {
            float angle = body.getAngle();
            return (float) (angle / Math.PI * 180f) % 360;
        } else { return 0; }
    }

    public void applyLinearImpulse(float x, float y, View view) {
        Body body = (Body) view.getTag(R.id.view_body_tag);
        if (body != null) {
            Vec2 vec2 = new Vec2(x, y);
            body.applyLinearImpulse(vec2, body.getPosition(), true);//让物体做线性运动，运动完后就自行停止
        }
    }

}
