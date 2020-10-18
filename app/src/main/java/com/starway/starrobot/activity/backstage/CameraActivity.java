package com.starway.starrobot.activity.backstage;

import android.content.Intent;
import android.graphics.Bitmap;
import android.hardware.Camera;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.starway.starrobot.R;
import com.starway.starrobot.mscability.facedetect.CameraHelper;
import com.starway.starrobot.mscability.speech.SpeechHelper;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;


/**
 * 高拍仪
 */
public class CameraActivity extends AppCompatActivity implements CameraHelper.GetBitmapListener, CameraHelper.OnCameraPrepareListener {

    /**
     * 摄像头帮助类
     */
   private CameraHelper mCameraHelper;

    /**
     * 是否正在拍照中
     */
    private boolean mPictureTakeStatus = false;

    /**
     * SurfaceView
     */
    private FrameLayout mSurfaceView;

    /**
     * 照片展示
     */
    private ImageView mPicView;

    /**
     * 返回按钮
     */
    private Button mBackBtn;

    /**
     * 拍照按钮
     */
    private ImageButton mShutterBtn;

    /**
     * 裁剪按钮
     */
    private Button mCropBtn;

    /**
     * 拍照图片
     */
    private Bitmap mBitmap;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_high);
        mCameraHelper = new CameraHelper(this);
        initView();
    }


    protected void initView() {
        mPicView = (ImageView) findViewById(R.id.iv_pic);
        mSurfaceView = (FrameLayout) findViewById(R.id.surface_frame);
        mBackBtn = (Button) findViewById(R.id.btn_back);
        mCropBtn = (Button) findViewById(R.id.btn_crop);
        mShutterBtn = (ImageButton) findViewById(R.id.btn_shutter);
        //初始化摄像头
        initCamera();

    }



    /**
     * 处理摄像头初始化等操作
     */
    private void initCamera() {
        try {
            SpeechHelper.getInstance().speak("首先拍张正脸的照片");
            mCameraHelper.setGetBitmapListener(this);
            mCameraHelper.setOnCameraPrepareListener(this);
            mCameraHelper.openCamera(mSurfaceView, Camera.CameraInfo.CAMERA_FACING_BACK);
        } catch (Exception ex) {
            showToast("初始化摄像头出现异常：" + ex.getMessage());
            ex.printStackTrace();
        }
    }

    /**
     * 获取图片
     */
    @Override
    public void getBitmap(final Bitmap bitmap) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mBitmap = bitmap;
                mPicView.setImageBitmap(bitmap);
                mPicView.setVisibility(View.VISIBLE);
                mCropBtn.setVisibility(View.VISIBLE);
                mBackBtn.setVisibility(View.VISIBLE);
                mPictureTakeStatus = false;
            }
        });
    }

    /**
     * 拍照点击事件
     **/
    public void shutterClick(View view) {
        if (mPictureTakeStatus) {
            return;
        }
        mPictureTakeStatus = true;
       mCameraHelper.takePicture();

    }


    public void cropBtnClick(View view) {
        Intent intent = new Intent();
        intent.putExtra("bitmap", saveBitmap());
        setResult(RESULT_OK, intent);
        CameraActivity.this.finish();
    }

    /**
     * 保存方法
     */
    public String saveBitmap() {
        Log.e("ZGG ", "保存图片");
        File f = new File("/sdcard/LoveRobot/", System.currentTimeMillis() + ".jpg");
        f.mkdirs();
        if (f.exists()) {
            f.delete();
        }

        try {
            f.createNewFile();
            FileOutputStream out = new FileOutputStream(f);
            mBitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.flush();
            out.close();
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(CameraActivity.this, "保存成功,文件路径:sdcard/LoveRobot/", Toast.LENGTH_LONG).show();
                }
            });
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return f.getAbsolutePath();
    }

    /**
     * 返回
     *
     * @param view
     */
    public void backBtnClick(View view) {
        mPicView.setVisibility(View.GONE);
        mBackBtn.setVisibility(View.GONE);
        mCropBtn.setVisibility(View.GONE);
        mShutterBtn.setVisibility(View.VISIBLE);
    }


    /**
     * 初始化监听
     */
    @Override
    public void prepare() {
        mShutterBtn.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mCameraHelper.closeCamera();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void showToast(String value) {
        Toast.makeText(this, value, Toast.LENGTH_SHORT).show();
    }

}
