package com.ren.myqrtest.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Rect;
import android.hardware.Camera;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.zxing.Result;
import com.ren.myqrtest.R;
import com.ren.myqrtest.utils.Util;
import com.ren.myqrtest.zxing.camera.CameraManager;
import com.ren.myqrtest.zxing.decode.DecodeThread;
import com.ren.myqrtest.zxing.utils.CaptureActivityHandler;
import com.ren.myqrtest.zxing.utils.IQrScanCallback;
import com.ren.myqrtest.zxing.utils.InactivityTimer;

import java.io.IOException;

import static android.view.WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;

/**
 * 二维码扫描activity
 */
public class ScanQRActivity extends AppCompatActivity implements SurfaceHolder.Callback, View.OnClickListener {

    private CameraManager cameraManager;
    private CaptureActivityHandler handler;
    private InactivityTimer inactivityTimer;
    private View mCancel_btn;

    private SurfaceView scanPreview = null;
    private LinearLayout scanContainer;
    private FrameLayout scanCropView;
    private ImageView scanLine;
    private Rect mCropRect = null;

    private boolean isHasSurface = false;

    private LinearLayout scanQrCode_top;

    private TranslateAnimation animation;// 扫描动画

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(FLAG_KEEP_SCREEN_ON);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);// 全屏
        setContentView(R.layout.activity_scan_qr);

        scanPreview = (SurfaceView) findViewById(R.id.scanQrCode_surfaceView_sfv);
        scanContainer = (LinearLayout) findViewById(R.id.scanQrCode_container);
        scanCropView = (FrameLayout) findViewById(R.id.scanQrCode_crop_view);
        scanLine = (ImageView) findViewById(R.id.scanQrCode_scan_line_iv);
        ToggleButton tbtn = (ToggleButton) findViewById(R.id.scanQrCode_flashlight_tbtn);
        mCancel_btn = findViewById(R.id.scanQrCode_cancel_btn);
        scanQrCode_top = (LinearLayout) findViewById(R.id.scanQrCode_top);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            scanQrCode_top.setPadding(0, Util.getStatusBarHeight(this), 0, 0);
        }

        inactivityTimer = new InactivityTimer(this);

        animation = new TranslateAnimation(Animation.RELATIVE_TO_PARENT, 0.0f, Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f, Animation.RELATIVE_TO_PARENT, 0.9f);
        animation.setDuration(2500);
        animation.setRepeatCount(-1);
        animation.setRepeatMode(Animation.RESTART);
        scanLine.startAnimation(animation);

        mCancel_btn.setOnClickListener(this);

        tbtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    cameraManager.openLight();
                } else {
                    cameraManager.offLight();
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        startScanQr();
    }

    /**
     * 开始扫描二维码
     */
    public void startScanQr() {
//        if(cameraManager == null){
        cameraManager = new CameraManager(getApplication());
//        }
        handler = null;
        if (isHasSurface) {
            initCamera(scanPreview.getHolder());
        } else {
            scanPreview.getHolder().addCallback(this);
        }
        inactivityTimer.onResume();
        scanLine.startAnimation(animation);
    }

    private void initCamera(SurfaceHolder surfaceHolder) {
        if (surfaceHolder == null) {
            throw new IllegalStateException("No SurfaceHolder provided");
        }
        if (cameraManager.isOpen()) {
            return;
        }
        try {
            cameraManager.openDriver(surfaceHolder);
            if (handler == null) {
                handler = new CaptureActivityHandler(mQrScanListener, cameraManager, DecodeThread.ALL_MODE);
            }

            initCrop();
        } catch (IOException ioe) {
            displayFrameworkBugMessageAndExit();
        } catch (RuntimeException e) {
            displayFrameworkBugMessageAndExit();
        }
    }

    private void displayFrameworkBugMessageAndExit() {
        Toast.makeText(this,"相机打开出错，请检查权限",Toast.LENGTH_SHORT).show();
        finish();
    }

    private IQrScanCallback mQrScanListener = new IQrScanCallback() {

        @Override
        public Handler onGetHandler() {
            return handler;
        }

        @Override
        public Camera.Size onGetCameraPreviewSize() {
            return cameraManager.getPreviewSize();
        }

        @Override
        public Rect onGetCropRect() {
            return mCropRect;
        }

        @Override
        public void onHandleDecode(Result result, Bundle bundle) {
            handleDecode(result, bundle);
        }

        @Override
        public void onSetScanResult(int activityResult, Intent intent) {
            setResult(Activity.RESULT_OK, intent);
            finish();
        }
    };

    private void handleDecode(final Result rawResult, Bundle bundle) {
        inactivityTimer.onActivity();
        //震动
        vibrate();
        handler.postDelayed(new Runnable() {

            @Override
            public void run() {
                Toast.makeText(ScanQRActivity.this, rawResult.getText(), Toast.LENGTH_SHORT).show();
                startScanQr();
            }
        }, 100);
    }
    /**
     * 震动
     */
    private void vibrate() {
        Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        vibrator.vibrate(200);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (handler != null) {
            handler.quitSynchronously();
            handler = null;
        }
        inactivityTimer.onPause();
        cameraManager.closeDriver();
        if (!isHasSurface) {
            scanPreview.getHolder().removeCallback(this);
        }
        scanLine.clearAnimation();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        inactivityTimer.shutdown();
        getWindow().clearFlags(FLAG_KEEP_SCREEN_ON);
    }

    private void initCrop() {
        int cameraWidth = cameraManager.getCameraResolution().y;
        int cameraHeight = cameraManager.getCameraResolution().x;

        /** 获取布局中扫描框的位置信息 */
        int[] location = new int[2];
        scanCropView.getLocationInWindow(location);

        int cropLeft = location[0];
        int cropTop = location[1] - Util.getStatusBarHeight(this);

        int cropWidth = scanCropView.getWidth();
        int cropHeight = scanCropView.getHeight();

        /** 获取布局容器的宽高 */
        int containerWidth = scanContainer.getWidth();
        int containerHeight = scanContainer.getHeight();

        /** 计算最终截取的矩形的左上角顶点x坐标 */
        int x = cropLeft * cameraWidth / containerWidth;
        /** 计算最终截取的矩形的左上角顶点y坐标 */
        int y = cropTop * cameraHeight / containerHeight;

        /** 计算最终截取的矩形的宽度 */
        int width = cropWidth * cameraWidth / containerWidth;
        /** 计算最终截取的矩形的高度 */
        int height = cropHeight * cameraHeight / containerHeight;

        /** 生成最终的截取的矩形 */
        mCropRect = new Rect(x, y, width + x, height + y);
    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        if (surfaceHolder == null) {
        }
        if (!isHasSurface) {
            isHasSurface = true;
            initCamera(surfaceHolder);
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        isHasSurface = false;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.scanQrCode_cancel_btn:
                finish();
                break;
        }
    }
}
