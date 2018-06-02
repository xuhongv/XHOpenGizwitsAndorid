package com.xuhoys.xuhong_gizwits_andorid.ui.DevicesControlActivity;


import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import com.gizwits.gizwifisdk.enumration.GizWifiErrorCode;
import com.xuhoys.xuhong_gizwits_andorid.R;

import java.util.concurrent.ConcurrentHashMap;

public class DevivePetAvtivity extends BaseDevicesControlActivity implements View.OnClickListener, SeekBar.OnSeekBarChangeListener {


    private Switch mMSwitchRedLight;
    private Spinner mMSpinnerColorValue;
    private TextView mTvResultRed;
    private SeekBar mMSeekBarRedValue;
    private TextView mTvResultGreen;
    private SeekBar mMSeekBarGreenValue;
    private TextView mTvResultBlue;
    private SeekBar mMSeekBarBlueValue;
    private TextView mTvResultMotor;
    private SeekBar mMSeekBarMotorValue;
    private Switch mMSwitchRedInf;
    private TextView mMTVTemper;
    private TextView mMTVHum;


    private static final String KEY_TEMPERATURE = "Temperature";
    private static final String KEY_HUMIDITY = "Humidity";
    private static final String KEY_R = "LED_R";
    private static final String KEY_G = "LED_G";
    private static final String KEY_B = "LED_B";
    private static final String KEY_LED_COLOR = "LED_Color";
    private static final String KEY_MOTOR_SPEED = "Motor_Speed";
    private static final String KEY_INFRARED = "Infrared";
    private static final String KEY_LED_ONOFF = "LED_OnOff";


    //临时的全局变量
    private int tempTemperture = 0;
    private int tempHumdity = 0;
    private boolean tempSwitch = false;
    private int tempLEDColor = 0;
    private int tempLED_R = 0;
    private int tempLED_G = 0;
    private int tempLED_B = 0;
    private int tempMotorSpeed = 0;
    private boolean tempRedInf = false;


    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {

        @Override
        public void dispatchMessage(Message msg) {
            super.dispatchMessage(msg);

            if (msg.what == 108) {
                updataUI();
            }
        }
    };


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activiity_device_pet);
        initView();
    }

    private void initView() {
        mTopBar = findViewById(R.id.topBar);
        //同步这个设备的名字
        String tempTitle = mDevice.getAlias().isEmpty() ? mDevice.getProductName() : mDevice.getAlias();
        mTopBar.setTitle(tempTitle);
        mTopBar.addLeftImageButton(R.mipmap.ic_back, R.id.topbar_left_icon).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        bindViews();
    }

    private void bindViews() {

        mMSwitchRedLight = (Switch) findViewById(R.id.mSwitchRedLight);
        mMSpinnerColorValue = (Spinner) findViewById(R.id.mSpinnerColorValue);

        mTvResultRed = (TextView) findViewById(R.id.tvResultRed); //文本框 不需要点击事件
        mMSeekBarRedValue = (SeekBar) findViewById(R.id.mSeekBarRedValue);
        mTvResultGreen = (TextView) findViewById(R.id.tvResultGreen);
        mMSeekBarGreenValue = (SeekBar) findViewById(R.id.mSeekBarGreenValue);
        mTvResultBlue = (TextView) findViewById(R.id.tvResultBlue);
        mMSeekBarBlueValue = (SeekBar) findViewById(R.id.mSeekBarBlueValue);
        mTvResultMotor = (TextView) findViewById(R.id.tvResultMotor);
        mMSeekBarMotorValue = (SeekBar) findViewById(R.id.mSeekBarMotorValue);
        mMSwitchRedInf = (Switch) findViewById(R.id.mSwitchRedInf);
        mMTVTemper = (TextView) findViewById(R.id.mTVTemper);
        mMTVHum = (TextView) findViewById(R.id.mTVHum);

        //UI触摸点击事件初始化
        mMSwitchRedLight.setOnClickListener(this);

        mMSpinnerColorValue.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                //这里是用户点击组合颜色的选项的回调
                switch (i) {
                    case 0:
                        break;
                    //黄色
                    case 1:
                        sendCommand(KEY_LED_COLOR, 1);
                        break;
                    //紫色
                    case 2:
                        sendCommand(KEY_LED_COLOR, 2);
                        break;
                    //粉色
                    case 3:
                        sendCommand(KEY_LED_COLOR, 3);
                        break;

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        mMSeekBarRedValue.setOnSeekBarChangeListener(this);
        mMSeekBarRedValue.setMax(254);

        mMSeekBarGreenValue.setOnSeekBarChangeListener(this);
        mMSeekBarGreenValue.setMax(254);

        mMSeekBarBlueValue.setOnSeekBarChangeListener(this);
        mMSeekBarBlueValue.setMax(254);

        mMSeekBarMotorValue.setOnSeekBarChangeListener(this);
        mMSeekBarMotorValue.setMax(10);// -4 -3 -2 -1 0 1 2 3 4 5
    }


    @Override
    protected void receiveCloudData(GizWifiErrorCode result, ConcurrentHashMap<String, Object> dataMap) {
        super.receiveCloudData(result, dataMap);
        Log.e("xuhongYss", "DevivePetAvtivity 控制界面的下发数据:" + dataMap);

        if (result == GizWifiErrorCode.GIZ_SDK_SUCCESS) {
            //如果下发数据不为null
            if (dataMap != null) {
                parseReceiveData(dataMap);
            }

        }

    }


    private void updataUI() {
        mMTVTemper.setText(tempTemperture + "°");
        mMTVHum.setText(tempHumdity + "%");
        mMSwitchRedLight.setChecked(tempSwitch);

        mMSeekBarRedValue.setProgress(tempLED_R);
        mTvResultRed.setText(tempLED_R + "");

        mMSeekBarGreenValue.setProgress(tempLED_G);
        mTvResultGreen.setText(tempLED_G + "");

        mMSeekBarBlueValue.setProgress(tempLED_B);
        mTvResultBlue.setText(tempLED_B + "");


        // 1  2  3  4 5 6 7 8 9 10
        //-4 -3 -2 -1 0 1 2 3 4 5
        mMSeekBarMotorValue.setProgress(tempMotorSpeed + 5);
        mTvResultMotor.setText(tempMotorSpeed + "");


        mMSwitchRedInf.setChecked(tempRedInf);

        mMSpinnerColorValue.setSelection(tempLEDColor);
    }


    private void parseReceiveData(ConcurrentHashMap<String, Object> dataMap) {


        if (dataMap.get("data") != null) {
            ConcurrentHashMap<String, Object> temperDataMap = (ConcurrentHashMap<String, Object>) dataMap.get("data");
            for (String dataKey : temperDataMap.keySet()) {

                //主要是通过我们在云端定义的标志点
                //温度
                if (dataKey.equals(KEY_TEMPERATURE)) {
                    tempTemperture = (int) temperDataMap.get(KEY_TEMPERATURE);
                }


                //湿度
                if (dataKey.equals(KEY_HUMIDITY)) {
                    tempHumdity = (int) temperDataMap.get(KEY_HUMIDITY);
                }


                //组合颜色
                if (dataKey.equals(KEY_LED_COLOR)) {
                    tempLEDColor = (int) temperDataMap.get(KEY_LED_COLOR);
                }


                //R
                if (dataKey.equals(KEY_R)) {
                    tempLED_R = (int) temperDataMap.get(KEY_R);
                }

                //G
                if (dataKey.equals(KEY_G)) {
                    tempLED_G = (int) temperDataMap.get(KEY_G);
                }

                //B
                if (dataKey.equals(KEY_B)) {
                    tempLED_B = (int) temperDataMap.get(KEY_B);
                }


                //电机
                if (dataKey.equals(KEY_MOTOR_SPEED)) {
                    tempMotorSpeed = (int) temperDataMap.get(KEY_MOTOR_SPEED);
                }


                //红外
                if (dataKey.equals(KEY_INFRARED)) {
                    tempRedInf = (boolean) temperDataMap.get(KEY_INFRARED);
                }


                //红灯开启按钮
                if (dataKey.equals(KEY_LED_ONOFF)) {
                    tempSwitch = (boolean) temperDataMap.get(KEY_LED_ONOFF);
                }


            }

            mHandler.sendEmptyMessage(108);
        }


    }


    //按钮的点击事件的回调
    @Override
    public void onClick(View view) {

        if (view.getId() == R.id.mSwitchRedLight) {
            sendCommand(KEY_LED_ONOFF, mMSwitchRedLight.isChecked());
        }
    }


    //拖动条的触摸事件事件的回调（手机离开手机屏幕屏幕触发）
    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

        //通过获取seekBar的ID来识别是哪个拖动条
        switch (seekBar.getId()) {
            case R.id.mSeekBarRedValue:
                sendCommand(KEY_R, seekBar.getProgress());
                break;
            case R.id.mSeekBarGreenValue:
                sendCommand(KEY_G, seekBar.getProgress());
                break;
            case R.id.mSeekBarBlueValue:
                sendCommand(KEY_B, seekBar.getProgress());
                break;
            case R.id.mSeekBarMotorValue:
                sendCommand(KEY_MOTOR_SPEED, seekBar.getProgress() - 5);
                break;
            default:
                break;

        }

    }


    @Override
    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }


}
