package com.ren.myqrtest.activity;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.ChecksumException;
import com.google.zxing.DecodeHintType;
import com.google.zxing.FormatException;
import com.google.zxing.NotFoundException;
import com.google.zxing.RGBLuminanceSource;
import com.google.zxing.Result;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.QRCodeReader;
import com.ren.myqrtest.R;

import java.util.Hashtable;

/**
 * 二维码识别activity
 */
public class ScanPhotoActivity extends AppCompatActivity {
    private final int HANDLE_WHAT_SCAN_SUCCESS = 0;
    private Bitmap mScanBitmap;
    private String mResultCode = null;
    private RelativeLayout mRootView;
    private boolean mStopThread = false;

    private String mImagPath = null;
    public static String INTENT_IMAGE_PATH = "imagePath";// 图片路径

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);// 全屏
        setContentView(R.layout.activity_scan_photo);
        mImagPath = getIntent().getStringExtra(INTENT_IMAGE_PATH);

        findViewById();

        scanPhotoQr();
    }

    /**
     * 控件初始化
     */
    private void findViewById() {
        mRootView = (RelativeLayout) findViewById(R.id.scan_qrphoto_rootview);
    }

    /**
     * 识别图中二维码
     */
    private void scanPhotoQr() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(500);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Result result = scanningImage(mImagPath);
                // 数据返回
                Message msg = new Message();
                msg.what = HANDLE_WHAT_SCAN_SUCCESS;
                msg.obj = result;
                mHandler.sendMessage(msg);
            }
        }).start();
    }

    /**
     * handler用于将识别后的二维码信息传回UI线程
     */
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case HANDLE_WHAT_SCAN_SUCCESS:
                    if (!mStopThread) {
                        Result result = (Result) msg.obj;
                        if (result == null) {
                            Toast.makeText(ScanPhotoActivity.this,"未识别到二维码",Toast.LENGTH_SHORT).show();
                        } else {
                            mResultCode = result.toString();
                            Toast.makeText(ScanPhotoActivity.this,mResultCode,Toast.LENGTH_SHORT).show();
                        }
                        finish();
                        mRootView.setVisibility(View.GONE);
                    }
                    break;

                default:
                    break;
            }
        }
    };

    /**
     * 根据图片路径识别二维码
     *
     * @param path
     * @return
     */
    protected Result scanningImage(String path) {
        if (TextUtils.isEmpty(path)) {
            return null;
        }
        Hashtable<DecodeHintType, String> hints = new Hashtable<>();
        hints.put(DecodeHintType.CHARACTER_SET, "utf-8"); // 设置二维码内容的编码
        // boolean isUrlPath = Util.adjustIsURL(path);// 是否为网络地址
        // if (isUrlPath) { // 是网络图片
        mScanBitmap = getBitmapFromUrl(path);
        // } else { // 是本地图片
        // mScanBitmap = getBitmapFromUrl(path);
        // }
        if (mScanBitmap == null) {
            return null;
        }
        int width = mScanBitmap.getWidth();
        int height = mScanBitmap.getHeight();
        int[] data = new int[width * height];
        mScanBitmap.getPixels(data, 0, width, 0, 0, width, height);
        Result result = null;
        RGBLuminanceSource source = new RGBLuminanceSource(width, height, data);
        BinaryBitmap bitmap1 = new BinaryBitmap(new HybridBinarizer(source));
        QRCodeReader reader = new QRCodeReader();
        try {
            result = reader.decode(bitmap1, hints);
        } catch (NotFoundException e) {
            e.printStackTrace();
        } catch (ChecksumException e) {
            e.printStackTrace();
        } catch (FormatException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
        }
        return result;
    }

    /**
     * 根据图片的url路径获得Bitmap对象
     *
     * @param url
     * @return
     */
    private Bitmap getBitmapFromUrl(String url) {
        Bitmap bitmap = null;
        try {
            bitmap = Glide.with(this).load(url).asBitmap().into(300, 300).get();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mStopThread = true;
    }
}
