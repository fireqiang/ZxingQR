package com.ren.myqrtest.activity;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.ren.myqrtest.R;
import com.ren.myqrtest.utils.QrCodeUtil;

/**
 * 二维码生成activity
 */
public class CreateQRActivity extends AppCompatActivity {
    private EditText edt_content;
    private Button btn_createNormalQR;
    private Button btn_createLogoQR;
    private ImageView img_result;

    private static final int HANDLER_NORMAL_QRCODE = 1;
    private static final int HANDLER_LOGO_QRCODE = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_creat_qr);
        edt_content = (EditText) findViewById(R.id.edt_content);
        btn_createNormalQR = (Button) findViewById(R.id.btn_createNormalQR);
        btn_createLogoQR = (Button) findViewById(R.id.btn_createLogoQR);
        img_result = (ImageView) findViewById(R.id.img_result);

        btn_createNormalQR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String data = edt_content.getText().toString().trim();
                if(TextUtils.isEmpty(data)){
                    return;
                }
                createNormalQR(data, 0);
            }
        });

        btn_createLogoQR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String data = edt_content.getText().toString().trim();
                if(TextUtils.isEmpty(data)){
                    return;
                }
                createLogoQR(data, 300);
            }
        });
    }

    /**
     * 生成普通二维码
     *
     * @param data
     * @param size
     */
    private void createNormalQR(final String data, final int size) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Bitmap bitmap = QrCodeUtil.createQRImage(data, size);
                Message message = Message.obtain();
                message.what = HANDLER_NORMAL_QRCODE;
                message.obj = bitmap;
                mHandler.sendMessage(message);
            }
        }).start();
    }

    /**
     * @param data
     * @param size
     */
    private void createLogoQR(final String data, final int size) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Bitmap bitmap = null;
                try {
                    bitmap = Glide.with(CreateQRActivity.this).load("http://cs.vmoiver.com/Uploads/Banner/2016/12/23/585d0beb347df.jpg").asBitmap().into(300, 300).get();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Bitmap bitmapResult = QrCodeUtil.createQRCodeWithLogo(data, bitmap, size);
                Message message = Message.obtain();
                message.what = HANDLER_LOGO_QRCODE;
                message.obj = bitmapResult;
                mHandler.sendMessage(message);
            }
        }).start();
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case HANDLER_NORMAL_QRCODE:
                    Bitmap bitmap = (Bitmap) msg.obj;
                    if (bitmap != null) {
                        img_result.setImageBitmap(bitmap);
                    }
                    break;
                case HANDLER_LOGO_QRCODE:
                    Bitmap bitmapResult = (Bitmap) msg.obj;
                    if (bitmapResult != null) {
                        img_result.setImageBitmap(bitmapResult);
                    }
                    break;
            }
            super.handleMessage(msg);
        }
    };
}
