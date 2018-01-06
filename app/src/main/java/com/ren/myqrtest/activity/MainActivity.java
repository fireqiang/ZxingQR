package com.ren.myqrtest.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.ren.myqrtest.R;

public class MainActivity extends AppCompatActivity {
    private Button btn_scanQr;
    private Button btn_scanPhoto;
    private Button btn_createQR;
    private ImageView image;

    private String imgPath = "http://cs.vmoiver.com/Uploads/Banner/2016/12/26/5860936a7e30d.jpg";//网络图片
//    private String imgPath = "/storage/emulated/0/Pictures/Screenshots/Screenshot_20171026-143630.png";//本地图片




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);// 全屏
        setContentView(R.layout.activity_main);
        btn_scanQr = (Button) findViewById(R.id.btn_scanQr);
        btn_scanPhoto = (Button) findViewById(R.id.btn_scanPhoto);
        btn_createQR = (Button) findViewById(R.id.btn_createQR);
        image = (ImageView) findViewById(R.id.image);

        Glide.with(this).load(imgPath).into(image);

        btn_scanQr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this,ScanQRActivity.class);
                startActivity(intent);
            }
        });

        btn_scanPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this,ScanPhotoActivity.class);
                intent.putExtra(ScanPhotoActivity.INTENT_IMAGE_PATH,imgPath);
                startActivity(intent);
            }
        });

        btn_createQR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this,CreateQRActivity.class);
                startActivity(intent);
            }
        });
    }
}
