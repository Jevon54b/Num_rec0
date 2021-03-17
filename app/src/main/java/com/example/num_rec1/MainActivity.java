package com.example.num_rec1;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.hardware.Camera.AutoFocusCallback;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.ShutterCallback;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.Toast;

import com.chaquo.python.Kwarg;
import com.chaquo.python.PyObject;
import com.chaquo.python.Python;
import com.chaquo.python.android.AndroidPlatform;

import com.example.num_rec1.PythonCall;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;



public class MainActivity extends Activity implements SurfaceHolder.Callback{
    private static final String tag="USBCamera";
    private boolean isPreview = false;
    private CameraSurfaceView mCameraSurfaceView = null;
    private SurfaceHolder mySurfaceHolder = null;
    private ImageButton mPhotoImgBtn = null;
    private Camera myCamera = null;
    private Bitmap mBitmap = null;
    private AutoFocusCallback myAutoFocusCallback = null;
    private PictureProcess ppc;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Full Screen
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        int flag = WindowManager.LayoutParams.FLAG_FULLSCREEN;
        Window myWindow = this.getWindow();
        myWindow.setFlags(flag, flag);
        setContentView(R.layout.activity_main);
        //Init SurfaceView
        mCameraSurfaceView = (CameraSurfaceView)findViewById(R.id.cameraSurfaceView);
        mySurfaceHolder = mCameraSurfaceView.getHolder();
        mySurfaceHolder.setFormat(PixelFormat.TRANSLUCENT);//translucent半透明 transparent透明
        mySurfaceHolder.addCallback(this);
        mySurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

        mCameraSurfaceView.setEventListener(new OnCameraSurfaceViewEventListener(){
            public void onTouchEvent(){
                if(isPreview && myCamera!=null){
                    //Toast.makeText(MainActivity.this, "Saving Picture to SD Card...", Toast.LENGTH_SHORT).show();
                    myCamera.takePicture(myShutterCallback, null, myJpegCallback);
                }
            }
        });
        myAutoFocusCallback = new AutoFocusCallback() {
            public void onAutoFocus(boolean success, Camera camera) {
                //Do nothing
            }
        };
        PythonCall.initPython(this);
    }


    public void surfaceChanged(SurfaceHolder holder, int format, int width,int height) {
        initCamera();
    }

    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    protected void onResume() {
        if(getRequestedOrientation()!=ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE){
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }
        super.onResume();
    }

    public void surfaceCreated(SurfaceHolder holder) {
        myCamera = Camera.open();
        try {
            myCamera.setPreviewDisplay(mySurfaceHolder);
        } catch (IOException e) {
            if(null != myCamera){
                myCamera.release();
                myCamera = null;
            }
            e.printStackTrace();
        }
    }


    public void surfaceDestroyed(SurfaceHolder holder) {
        if(null != myCamera) {
            myCamera.setPreviewCallback(null);
            myCamera.stopPreview();
            isPreview = false;
            myCamera.release();
            myCamera = null;
        }

    }

    public void initCamera() {
        if(isPreview){
            myCamera.stopPreview();
        }
        if(null != myCamera){
            Camera.Parameters myParam = myCamera.getParameters();
            myParam.setPictureFormat(PixelFormat.JPEG);//设置拍照后存储的图片格式
//            //分辨率参数查询
//            List<Camera.Size> pictureSizes = myParam.getSupportedPictureSizes();
//            int length = pictureSizes.size();
//            for (int i = 0; i < length; i++) {
//                Log.i("Size","SupportedPictureSizes : " + pictureSizes.get(i).width + "x" + pictureSizes.get(i).height);
//            }

            //设置分辨率，支持如下分辨率 "1280x720,1184x656,960x720,960x544,864x480,800x448,544x288,352x288,320x176"
            //预览和拍照的分辨率必须是相同的
            myParam.setPictureSize(544, 288);
            myParam.setPreviewSize(544, 288);
//            myParam.setPictureSize(864, 480);
//            myParam.setPreviewSize(864, 480);

            myCamera.setDisplayOrientation(0);
            myParam.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
            myCamera.setParameters(myParam);
            myCamera.startPreview();
            myCamera.autoFocus(myAutoFocusCallback);
            isPreview = true;
        }
    }

    ShutterCallback myShutterCallback = new ShutterCallback()
    {
        public void onShutter() {
            //Do nothing
        }
    };

    PictureCallback myRawCallback = new PictureCallback()
    {
        public void onPictureTaken(byte[] data, Camera camera) {
            //Do nothing
        }
    };

    PictureCallback myJpegCallback = new PictureCallback()
    {
        public void onPictureTaken(byte[] data, Camera camera) {
            if(null != data) {
                mBitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                myCamera.stopPreview();
                isPreview = false;
            }
            Matrix matrix = new Matrix();

            matrix.postRotate((float) 0.0);
            Bitmap rotaBitmap = Bitmap.createBitmap(mBitmap, 0, 0, mBitmap.getWidth(), mBitmap.getHeight(), matrix, false);
            if(null != rotaBitmap) {
                saveJpeg(rotaBitmap);
            }

            myCamera.startPreview();
            isPreview = true;
        }
    };



    public void saveJpeg(Bitmap bm){
		/*String savePath = "/udisk/camera";
		File folder = new File(savePath);
		if(!folder.exists()) {
			folder.mkdir();
		}*/

//        callPythonCode();
//        Log.d(TAG,"data:"+bm.toString());

        TCPClient client=new TCPClient();
        client.waiting();
        //建立tcp连接
        long dataTake = System.currentTimeMillis();
        String jpegName = "IMG_" + dataTake + ".jpg";
        Log.i(tag, "SaveToFile: " + jpegName);
        try {
            //FileOutputStream fout = new FileOutputStream(jpegName);
            //BufferedOutputStream bos = new BufferedOutputStream(client.outputStream);set
            ByteArrayOutputStream baos=new ByteArrayOutputStream();
            ppc=new PictureProcess();
            ppc.process(bm);

//            bm.compress(Bitmap.CompressFormat.JPEG, 30, baos);

            AssetCtrl.setAssetManager(getAssets());//获取access，给python加载用
//            PythonCall.imcomeints(pixels);//python代码调用（临时）
//            PythonCall.callgetdataTest();
//            PythonCall.callStart();
            PythonCall.test1DataCNN(ppc.getPixels());

            client.send(baos);
            client.closeLink();
            Toast.makeText(MainActivity.this, "File Name: " + jpegName, Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Log.i(tag, "Save jpeg to sdcard fail!");
            e.printStackTrace();
            Toast.makeText(MainActivity.this, "Saving Picture fail!", Toast.LENGTH_SHORT).show();
        }

		/*int bytes = mBitmap.getByteCount();
		ByteBuffer buf = ByteBuffer.allocate(bytes);
		mBitmap.copyPixelsToBuffer(buf);
		byte[] byteArray = buf.array();
		client.send(byteArray);*/
    }

    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
        MainActivity.this.finish();
    }
	/*public void clickButton(View view) {
	}*/
}
