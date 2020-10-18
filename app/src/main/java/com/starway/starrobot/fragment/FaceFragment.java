package com.starway.starrobot.fragment;

/**
 * 人脸识别功能
 * 大大志
 * 2018.08.29
 */

import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.starway.starrobot.R;
import com.starway.starrobot.activity.BaseActivity;
import com.starway.starrobot.mscability.StarMscAbility;
import com.starway.starrobot.mscability.facedetect.CameraHelper;
import com.starway.starrobot.mscability.facedetect.FaceDetectHelper;
import com.starway.starrobot.mscability.speech.SpeechHelper;
import com.starway.starrobot.mscability.speech.TTS;
import com.starway.starrobot.utils.SPUtils;

import static android.hardware.Camera.CameraInfo.CAMERA_FACING_BACK;


public class FaceFragment extends Fragment implements CameraHelper.GetBitmapListener, CameraHelper.OnCameraPrepareListener {
    /**
     * 摄像头帮助类
     */
    private CameraHelper mCameraHelper;

    private FrameLayout mSurfaceView;

    /**
     * 拍照图片
     */
    private final static String SPEECH_APPID = "5ab303c5";
    private final static String GROUP_ID = "GROUP_ID";
    private String mGroupId = "3994134796";

    private Handler handler=new Handler();

    public onFaceDetectedListener listener;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_face, container, false);
        mCameraHelper = new CameraHelper(getActivity());
        mSurfaceView = view.findViewById(R.id.surface_frame);
        //初始化摄像头
        //initCamera();
//        StarMscAbility.getInstance().initWithAppid(this.getContext(), SPEECH_APPID);//初始化人脸识别
        initGroupId();//初始化人脸组
        return view;
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        if (hidden) {
            handler.removeCallbacks(takePhoto);
            mCameraHelper.closeCamera();
            Log.i("message", "摄像头被注销");
        } else {
            // TODO: 初始化注册操作
            BaseActivity activity = (BaseActivity) getActivity();
            activity.setTitle(R.string.face_recognition);

            activity.setSubtitle("");
            activity.showFuncButton(false);

            initCamera();

            faceDetecting("请盯着我的眼睛，让我猜猜你是谁？");
        }
        super.onHiddenChanged(hidden);
    }

    private Runnable takePhoto=new Runnable() {
        @Override
        public void run() {
            mCameraHelper.takePicture();
        }
    };

    private void faceDetecting(String tip){
        SpeechHelper.getInstance().speak(tip, new TTS.onSpeakCallback() {
            @Override
            public void onSpeak(String s) {
                Log.i("test","开始拍照");
                handler.postDelayed(takePhoto, 2000);
            }
        });
    }

    /**
     * 处理摄像头初始化等操作
     */
    private void initCamera() {
        try {
            Log.i("啦啦啦", "初始化相机");
            mCameraHelper.setGetBitmapListener(this);
            mCameraHelper.setOnCameraPrepareListener(this);
            mCameraHelper.openCamera(mSurfaceView, CAMERA_FACING_BACK);//这里应该是前置摄像头
            Log.i("啦啦啦", "到这里了");
        } catch (Exception ex) {
            Log.i("初始化摄像头失败", "失败了");
            ex.printStackTrace();
        }
    }

    public FaceFragment setListener(onFaceDetectedListener listener) {
        this.listener = listener;
        return this;
    }

    /**
     * 获取图片
     */
    @Override
    public void getBitmap(Bitmap bitmap) {
        Log.i("test","拍照结束");
        final FaceDetectHelper faceDetectHelper = new FaceDetectHelper(this.getContext());
        faceDetectHelper.setOnFaceDetectListener(new FaceDetectHelper.OnFaceDetectListener() {
            @Override
            public void onFaceDetectSuccess(String s) {
                if ("".equals(s)) {
                    faceDetecting("小途没有认出来你，让我再猜猜你吧");
                } else {
                    if(!listener.onFaceDetected(s)){
                        //faceDetecting("小途猜出来了您，您是"+s);
                    }
                }
                faceDetectHelper.closeFaceDetect();
            }
        });
        faceDetectHelper.deleteFace(bitmap);
    }


    @Override
    public void prepare() {
    }

    /**
     * 当识别到人脸后的结果回调
     */
    public interface onFaceDetectedListener {
        boolean onFaceDetected(String arg);
    }

    /**
     * 获取GroupId
     */
    private void initGroupId() {
        String groupid = String.valueOf(SPUtils.get(this.getContext(), GROUP_ID, ""));
        if (!"".equals("3994134796")) {
            mGroupId = "3994134796";
            StarMscAbility.getInstance().setGroupID(mGroupId);
            // showToast("GroupId is 3994134796");
        } else {
            //showToast("GroupId is null");
        }
    }


}
