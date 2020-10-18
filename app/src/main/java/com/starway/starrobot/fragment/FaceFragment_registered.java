package com.starway.starrobot.fragment;


import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.starway.starrobot.R;
import com.starway.starrobot.activity.BaseActivity;
import com.starway.starrobot.activity.backstage.CameraActivity;
import com.starway.starrobot.mscability.StarMscAbility;
import com.starway.starrobot.mscability.facedetect.CameraHelper;
import com.starway.starrobot.mscability.facedetect.FaceDetectHelper;
import com.starway.starrobot.mscability.facedetect.FaceRegisterHelper;
import com.starway.starrobot.mscability.facegroup.FaceGroupHelper;
import com.starway.starrobot.mscability.speech.SpeechHelper;
import com.starway.starrobot.mscability.speech.TTS;
import com.starway.starrobot.utils.SPUtils;

import java.io.ByteArrayOutputStream;

import static android.app.Activity.RESULT_OK;
import static android.hardware.Camera.CameraInfo.CAMERA_FACING_BACK;


public class FaceFragment_registered extends Fragment implements View.OnClickListener {
    private Context mContext;
    private final static String SPEECH_APPID = "5ab303c5";
    private final static String GROUP_ID = "GROUP_ID";
    private String mGroupId = "3994134796";
    private TextView mTxtGroupid;
    private EditText mEdtAuthid;
    private MenuFragment.OnMenuBtnClickListener listeners;
    // 拍照得到的照片文件
    private Bitmap mImage = null;
    private View group_create;
    private View group_delete;
    private View online_register;
    private View recognition_face;
    private View take_pic;

    /**
     * 摄像头帮助类
     */
    private  CameraHelper mCameraHelper;

    /**
     * 是否正在拍照中
     */
    private boolean mPictureTakeStatus = false;

    /**
     * SurfaceView
     */
    private FrameLayout mSurfaceView;

    public FaceFragment_registered.onFaceDetectedListener listener;

    public FaceFragment_registered() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view =inflater.inflate(R.layout.fragment_face_registered, container, false);

        StarMscAbility.getInstance().initWithAppid(this.getContext(), SPEECH_APPID);


        mTxtGroupid = view.findViewById(R.id.groupid);
        mEdtAuthid = view.findViewById(R.id.online_authid);
        group_create=view.findViewById(R.id.group_create);
        group_delete=view.findViewById(R.id.group_delete);
        online_register=view.findViewById(R.id.online_register);
        recognition_face=view.findViewById(R.id.recognition_face);
        take_pic=view.findViewById( R.id.take_pic);

        group_create.setOnClickListener(this);
        group_delete.setOnClickListener(this);
        online_register.setOnClickListener(this);
        recognition_face.setOnClickListener(this);
        take_pic.setOnClickListener(this);

        initGroupId();//初始化人脸组
       

        return view;
    }

    public void  init(String userid){
        listener.onFaceDetected(userid);
    }



    @Override
    public void onHiddenChanged(boolean hidden) {
        if (hidden) {
            System.out.println("被隐藏");
            // TODO :如果需要注销操作在这里进行

        } else {
            // TODO: 初始化注册操作

        }
        super.onHiddenChanged(hidden);
    }

   public FaceFragment_registered setListener(FaceFragment_registered.onFaceDetectedListener listener) {
        this.listener = listener;
        return this;
    }


    /**
     * 当识别度人脸后的结果回调
     */
    public interface onFaceDetectedListener{
        void onFaceDetected(String arg);
    }






    /**
     * 点击事件
     */
    @Override
    public void onClick(View v) {
        if (true) {
            switch (v.getId()) {
                case R.id.group_create:
                    showToast("创建组");
                    createGroup();
                    queryGroups();
                    break;
                case R.id.group_delete:
                    showToast("查询组");
                    queryGroups();
                    // deleteGroup(mGroupId);
                    break;
                case R.id.online_register:
                    registerFace();
                    break;
                case R.id.recognition_face:
                    recognitionFace();
                    break;
                case R.id.take_pic:
                    Intent mIntent = new Intent(getActivity(), CameraActivity.class);
                    startActivityForResult(mIntent, 1000);
                    //queryGroups();
                    break;
            }
        }
    }

    /**
     * 人脸识别
     */
    private void recognitionFace(){
        if(mGroupId.equals("")){
            showToast("Please Create GroupId Frist");
            return;
        }
        if(mImage==null){
            showToast("请拍照");
        }
        final FaceDetectHelper faceDetectHelper = new FaceDetectHelper(this.getContext());
        faceDetectHelper.setOnFaceDetectListener(new FaceDetectHelper.OnFaceDetectListener() {
            @Override
            public void onFaceDetectSuccess(String s) {
                if("".equals(s)){
                    showToast(" 没有匹配到人脸");
                }else {
                    showToast("识别成功，欢迎您：" + s);
                    init(s);
                    faceDetectHelper.closeFaceDetect();
                }
            }
        });
        faceDetectHelper.deleteFace(mImage);
    }

    /**
     * 人脸注册
     */
    private void registerFace(){
        if(mGroupId.equals("")){
            showToast("Please Create GroupId Frist");
            return;
        }
        if(mImage==null){
            showToast("请拍照");
        }
        if(null==mEdtAuthid.getText()||mEdtAuthid.getText().toString().equals("")){
            showToast("请输入userId");
        }
        FaceRegisterHelper registerHelper = new FaceRegisterHelper(this.getContext());
        registerHelper.setOnRegisterListener(new FaceRegisterHelper.OnRegisterListener() {
            @Override
            public void onSucceed() {
                showToast("注册成功:"+mEdtAuthid.getText());
            }

            @Override
            public void onError(String s) {
                showToast("注册失败:"+s);
            }
        });
        registerHelper.startRegister(mEdtAuthid.getText().toString(),mImage);

//        FaceGroupHelper faceGroupHelper = new FaceGroupHelper(this);
//        faceGroupHelper.deleteFace(faceid, groupid, callback);
    }

    /**
     * 查询全部组
     */
    private void queryGroups() {
        new FaceGroupHelper(this.getContext()).queryGroups(new FaceGroupHelper.onResultCallback() {
            @Override
            public void onResult(boolean flag, String result) {
                // Log.d("queryGroups", result);
                showToast(result);
            }
        });
    }

    /**
     * 创建组
     */
    private void createGroup() {
        new FaceGroupHelper(this.getContext()).createGroup(new FaceGroupHelper.onResultCallback() {
            @Override
            public void onResult(boolean flag, String result) {
                String Result;
                if (flag) {
                    mGroupId = result;
                    mTxtGroupid.setText(result);
                    SPUtils.put(mContext, GROUP_ID, result);
                    showToast("创建组成功：" + result + "-请牢记你的GroupId！！！");
                } else {
                    showToast("创建组失败：" + result);
                }
            }
        });
    }

    /**
     * 删除组
     */
    private void deleteGroup(String groupid) {
        if(mGroupId.equals("")){
            showToast("Please Create GroupId Frist");
            return;
        }
        new FaceGroupHelper(this.getContext()).deleteGroup(groupid, new FaceGroupHelper.onResultCallback() {
            @Override
            public void onResult(boolean flag, String result) {
                if (flag) {
                    mTxtGroupid.setText("");
                    StarMscAbility.getInstance().setGroupID(mGroupId);
                    SPUtils.remove(mContext, GROUP_ID);
                    showToast("删除组成功");
                } else {
                    showToast("删除组失败" + result);
                }
            }
        });
    }

    /**
     * 获取GroupId
     */
    private void initGroupId() {
        String groupid = String.valueOf(SPUtils.get(this.getContext(), GROUP_ID, ""));
        if (!"".equals("3994134796")) {
            mGroupId = "3994134796";
            StarMscAbility.getInstance().setGroupID(mGroupId);
            mTxtGroupid.setText("3994134796");
        } else {
            showToast("GroupId is null");
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_OK) {
            return;
        }
        String fileSrc = null;
        if (requestCode == 1000) {
            fileSrc = data.getStringExtra("bitmap");
            if (null != fileSrc) {
                // mPath = fileSrc;
                // 获取图片的宽和高
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inJustDecodeBounds = false;

                // 压缩图片
                options.inSampleSize = Math.max(1, (int) Math.ceil(Math.max(
                        (double) options.outWidth / 512f,
                        (double) options.outHeight / 512f)));
                mImage = BitmapFactory.decodeFile(fileSrc, options);


                // 若mImageBitmap为空则图片信息不能正常获取
                if (null == mImage) {
                    showToast("图片信息无法正常获取！");
                    return;
                }


                ByteArrayOutputStream baos = new ByteArrayOutputStream();

                //可根据流量及网络状况对图片进行压缩
                mImage.compress(Bitmap.CompressFormat.JPEG, 80, baos);
                // mImageData = baos.toByteArray();

                ((ImageView)getActivity().findViewById(R.id.online_img)).setImageBitmap(mImage);
            }
        }

    }

    private void showToast(String value) {
        Toast.makeText(this.getContext(), value, Toast.LENGTH_SHORT).show();
    }






}

