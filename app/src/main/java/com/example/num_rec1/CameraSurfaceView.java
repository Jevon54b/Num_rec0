package com.example.num_rec1;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class CameraSurfaceView extends SurfaceView implements SurfaceHolder.Callback {

    private com.example.num_rec1.OnCameraSurfaceViewEventListener mListener;

    public CameraSurfaceView(Context context, AttributeSet attrs){
        super(context,attrs);
    }

    public CameraSurfaceView(Context context) {
        super(context);
    }

    public void setEventListener(com.example.num_rec1.OnCameraSurfaceViewEventListener eventListener) {
        mListener=eventListener;
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {

    }
    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {}

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        switch(action){
            case MotionEvent.ACTION_DOWN:
                Log.i("Camera", "TakePicture");
                if(mListener!=null)
                    mListener.onTouchEvent();
                break;
            default:
        }
        return true; //processed
    }

}
