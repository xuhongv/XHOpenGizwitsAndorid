package com.xuhoys.xuhong_gizwits_andorid.ui;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;

import com.gizwits.gizwifisdk.api.GizWifiDevice;
import com.gizwits.gizwifisdk.api.GizWifiSDK;
import com.gizwits.gizwifisdk.enumration.GizWifiConfigureMode;
import com.gizwits.gizwifisdk.enumration.GizWifiErrorCode;
import com.gizwits.gizwifisdk.enumration.GizWifiGAgentType;
import com.gizwits.gizwifisdk.listener.GizWifiSDKListener;
import com.qmuiteam.qmui.widget.QMUITopBar;
import com.xuhoys.xuhong_gizwits_andorid.R;
import com.xuhoys.xuhong_gizwits_andorid.Utils.SharePreUtil;
import com.xuhoys.xuhong_gizwits_andorid.Utils.WifiAdminUtils;

import java.util.ArrayList;
import java.util.List;

import static com.gizwits.gizwifisdk.enumration.GizWifiGAgentType.GizGAgentESP;

public class NetConfigActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText mEtAPPaw;
    private CheckBox mCbPaw;

    private TextView mTvAPssid;
    private Button mBtnAdd;

    private WifiAdminUtils adminUtils;

    //进度弹窗
    private ProgressDialog dialog;


    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            if (msg.what == 105) {
                dialog.setMessage("配网成功");
                //有结果回调
                dialog.getButton(DialogInterface.BUTTON_NEGATIVE).setVisibility(View.VISIBLE);
                dialog.getButton(DialogInterface.BUTTON_POSITIVE).setVisibility(View.VISIBLE);

            } else if (msg.what == 106) {
                dialog.setMessage("配网失败");
                //有结果回调
                dialog.getButton(DialogInterface.BUTTON_NEGATIVE).setVisibility(View.VISIBLE);
                dialog.getButton(DialogInterface.BUTTON_POSITIVE).setVisibility(View.VISIBLE);

            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_net_config);
        initView();
    }

    @Override
    protected void onResume() {
        super.onResume();

        //先拿到我们手机当前连接的wifi名字
        String ssid = adminUtils.getWifiConnectedSsid();

        //判断是否拿到了wifi名字
        if (ssid != null) {
            mTvAPssid.setText(ssid);
        } else {
            mTvAPssid.setText("");
        }

        //如果拿不到当前连接wifi的名字, 就把这个搜索的按钮不可点击
        boolean isEmptyAPSSID = TextUtils.isEmpty(ssid);
        if (isEmptyAPSSID) {
            mBtnAdd.setEnabled(false);
            mEtAPPaw.setEnabled(false);
        }


    }

    private void initView() {

        adminUtils = new WifiAdminUtils(this);
        QMUITopBar topBar = findViewById(R.id.topBar);
        topBar.setTitle("添加设备");
        topBar.addLeftImageButton(R.mipmap.ic_back, R.id.topbar_left_icon).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        mEtAPPaw = findViewById(R.id.etApPassword);
        mEtAPPaw.addTextChangedListener(new TextWatcher() {

            //这是编辑框之前的数据
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            //这是编辑框正在编辑的回调
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (!charSequence.toString().isEmpty()) {
                    mCbPaw.setVisibility(View.VISIBLE);
                } else {
                    mCbPaw.setVisibility(View.GONE);
                }
            }

            //这是编辑框编辑完成之后的回调
            @Override
            public void afterTextChanged(Editable editable) {

            }
        });


        mCbPaw = findViewById(R.id.cbPaw);
        mCbPaw.setVisibility(View.GONE);//默认的眼睛是隐藏的
        mCbPaw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    mEtAPPaw.setInputType(99);//表示要显示密码
                } else {
                    mEtAPPaw.setInputType(0x81);//表示要显示密码
                }
            }
        });


        mTvAPssid = findViewById(R.id.tvAPSsid);

        mBtnAdd = findViewById(R.id.btnAdd);
        mBtnAdd.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {

        if (view.getId() == R.id.btnAdd) {

            String tvSSID = mTvAPssid.getText().toString().intern();//wifi名字
            String tvPas = mEtAPPaw.getText().toString().intern();//wifi密码

            if (!tvSSID.isEmpty()) {
                dialog = new ProgressDialog(NetConfigActivity.this);
                dialog.setMessage("正在配网中...");
                dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                dialog.setCancelable(false);//屏幕外不可点击
                dialog.setButton(DialogInterface.BUTTON_NEGATIVE, "取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialog.dismiss();
                    }
                });
                dialog.setButton(DialogInterface.BUTTON_POSITIVE, "好的", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialog.dismiss();
                        finish();//点击好的就摧毁当前的页面
                    }
                });
                dialog.show();

                //没有回调结果的话，我们把弹窗的按钮隐藏
                dialog.getButton(DialogInterface.BUTTON_NEGATIVE).setVisibility(View.GONE);
                dialog.getButton(DialogInterface.BUTTON_POSITIVE).setVisibility(View.GONE);

                //开始配网
                startAirLink(tvSSID, tvPas);

            }
        }
    }


    private void startAirLink(String ssid, String pas) {


        GizWifiSDK.sharedInstance().setListener(listener);
        List<GizWifiGAgentType> types = new ArrayList<>();
        types.add(GizGAgentESP);//因为我们只要8266的配网


        //GizWifiSDK.sharedInstance().setDeviceOnboardingDeploy(); 这个是新版本的 配网方式,为了兼容旧设备，我们不采用这种方法
        GizWifiSDK.sharedInstance().setDeviceOnboarding(ssid, pas, GizWifiConfigureMode.GizWifiAirLink, null, 60, types);


    }

    private GizWifiSDKListener listener = new GizWifiSDKListener() {
        @Override
        public void didSetDeviceOnboarding(GizWifiErrorCode result, GizWifiDevice device) {
            super.didSetDeviceOnboarding(result, device);
            if (result == GizWifiErrorCode.GIZ_SDK_SUCCESS) {
                mHandler.sendEmptyMessage(105);
            } else {
                mHandler.sendEmptyMessage(106);
            }


        }
    };

}
