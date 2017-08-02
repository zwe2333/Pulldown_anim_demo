package com.zwe.pulldown_anim_demo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;

import com.zwe.pulldown_anim_demo.widget.TouchPullView;

public class MainActivity extends AppCompatActivity {
    private static final float TOUCH_MOVE_MAX=600;
    private TouchPullView mPullView;
    private float mTouchStartY=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mPullView= (TouchPullView) findViewById(R.id.touch_pull);
        findViewById(R.id.activity_main).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                int action=motionEvent.getActionMasked();//得到意图
                switch (action){
                    case MotionEvent.ACTION_DOWN:
                        mTouchStartY=motionEvent.getY();
                        return true;
                    case MotionEvent.ACTION_MOVE:
                        float y=motionEvent.getY();
                        if (y>=mTouchStartY){
                            float moveSize=y-mTouchStartY;
                            float progress=moveSize>=TOUCH_MOVE_MAX?1:(moveSize/TOUCH_MOVE_MAX);
                            mPullView.setProgress(progress);
                        }
                        return true;
                    case MotionEvent.ACTION_UP:
                        mPullView.release();
                        return true;
                    default:
                        break;
                }
                return false;
            }
        });
    }
}
