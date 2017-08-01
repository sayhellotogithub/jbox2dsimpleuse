package com.iblogstreet.jbox2dsimpleuse;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.iblogstreet.jbox2dsimpleuse.widget.CollisionView;

import static android.hardware.SensorManager.SENSOR_DELAY_UI;

public class MainActivity
        extends AppCompatActivity
        implements SensorEventListener
{
    CollisionView mCollisionView;
    int[] mImgSource = {R.mipmap.view_1,
                        R.mipmap.view_2,
                        R.mipmap.view_3,
                        R.mipmap.view_4,
                        R.mipmap.view_5,
                        R.mipmap.view_6};

    SensorManager mSensorManager;
    Sensor        mSensor;
    LinearLayout  mLlViewList;
    FrameLayout.LayoutParams mLayoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                                                                          ViewGroup.LayoutParams.WRAP_CONTENT);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
    }

    @Override
    public void onContentChanged() {
        super.onContentChanged();
        initView();
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
    }

    private void initView() {
        mCollisionView = (CollisionView) findViewById(R.id.collisionView);
        mLlViewList = (LinearLayout) findViewById(R.id.llViewList);
        mLayoutParams.gravity = Gravity.TOP;
        for (int i = 0; i < mImgSource.length; i++) {
            ImageView box = new ImageView(this);
            box.setImageResource(mImgSource[i]);
            box.setContentDescription("this is img" + i);
            box.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(MainActivity.this,
                                   v.getContentDescription()
                                    .toString(),
                                   Toast.LENGTH_SHORT)
                         .show();
                    if (!isExistView(v)) {
                        mLlViewList.removeView(v);
                        v.setTag(R.id.view_circle_tag, true);
                        mCollisionView.addView(v, mLayoutParams);
                    }
                }
            });
            mLlViewList.addView(box);
        }
    }

    private boolean isExistView(View view) {
        int child = mCollisionView.getChildCount();
        for (int i = 0; i < child; i++) {
            if (mCollisionView.getChildAt(i) == view) {
                return true;
            }
        }
        return false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(this, mSensor, SENSOR_DELAY_UI);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            Log.e("SensorEvent", "onSensorChanged: " + event.values[0] + "");
            float x = -event.values[0] * 2;//注意这里取反
            float y = event.values[1] * 3;
            mCollisionView.onSensorChanged(x, y);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
