package com.xuhoys.xuhong_gizwits_andorid.ui;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.xuhoys.xuhong_gizwits_andorid.MainActivity;
import com.xuhoys.xuhong_gizwits_andorid.R;

import java.util.ArrayList;
import java.util.List;

/**
 * 包名：com.xuhoys.xuhong_gizwits_andorid.ui
 * 文件名：xuhong_gizwits_andorid
 * 创建时间：2018-03-19 23:24
 * 创建者：xuhong
 * CSDN: http://blog.csdn.net/xh870189248
 * 描述：闪屏页
 */

public class SplashActivity extends AppCompatActivity {


    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 107) {
                startActivity(new Intent(SplashActivity.this, MainActivity.class));
                finish();
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        checkAndroidPrimisson();
    }

    private void checkAndroidPrimisson() {

        //如果当前手机是android6.0或以上的系统的话，需要动态授权
        if (Build.VERSION.SDK_INT >= 23) {
            requestRunPerMisson(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE
                    , Manifest.permission.WRITE_EXTERNAL_STORAGE
                    , Manifest.permission.ACCESS_FINE_LOCATION
                    , Manifest.permission.ACCESS_WIFI_STATE
                    , Manifest.permission.READ_PHONE_STATE});
        } else {
            mHandler.sendEmptyMessageDelayed(107, 2500);
        }
    }


    private void requestRunPerMisson(String[] strings) {

        int status = 0;
        for (String permisson : strings) {
            //检查当前的权限是否已经授权了
            if (ContextCompat.checkSelfPermission(this, permisson) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, strings, 108);
            } else {
                status++;
            }
        }

        if (status == 5) {
            mHandler.sendEmptyMessageDelayed(107, 2500);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 108:
                if (grantResults.length > 0) {
                    //用来存储被拒绝的权限
                    List<String> denioedPermisson = new ArrayList<>();
                    for (int i = 0; i < grantResults.length; i++) {
                        int grantPermisson = grantResults[i];
                        String permisson = permissions[i];
                        if (grantPermisson != PackageManager.PERMISSION_GRANTED) {
                            denioedPermisson.add(permisson);
                        }
                    }

                    if (denioedPermisson.isEmpty()) {
                        //权限全部通过
                        mHandler.sendEmptyMessage(107);
                    } else {
                        Toast.makeText(this, "您拒绝了部分的权限，需要您手动去开启！", Toast.LENGTH_SHORT).show();
                        mHandler.sendEmptyMessageDelayed(107, 2500);
                    }


                }
                break;
        }

    }
}
