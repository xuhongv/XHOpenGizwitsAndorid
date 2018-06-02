package com.xuhoys.xuhong_gizwits_andorid;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.gizwits.gizwifisdk.api.GizWifiDevice;
import com.gizwits.gizwifisdk.api.GizWifiSDK;
import com.gizwits.gizwifisdk.enumration.GizEventType;
import com.gizwits.gizwifisdk.enumration.GizWifiDeviceNetStatus;
import com.gizwits.gizwifisdk.enumration.GizWifiErrorCode;
import com.gizwits.gizwifisdk.listener.GizWifiDeviceListener;
import com.gizwits.gizwifisdk.listener.GizWifiSDKListener;
import com.qmuiteam.qmui.widget.QMUITopBar;
import com.qmuiteam.qmui.widget.dialog.QMUIDialog;
import com.qmuiteam.qmui.widget.dialog.QMUIDialogAction;
import com.qmuiteam.qmui.widget.dialog.QMUITipDialog;
import com.xuhoys.xuhong_gizwits_andorid.Utils.SharePreUtil;
import com.xuhoys.xuhong_gizwits_andorid.Utils.WifiAdminUtils;
import com.xuhoys.xuhong_gizwits_andorid.adapter.LVDevicesAdapter;
import com.xuhoys.xuhong_gizwits_andorid.ui.DevicesControlActivity.DevivePetAvtivity;
import com.xuhoys.xuhong_gizwits_andorid.ui.NetConfigActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class MainActivity extends AppCompatActivity {


    private ListView listView;

    private LVDevicesAdapter adapter;

    private List<GizWifiDevice> gizWifiDeviceList;

    private SwipeRefreshLayout mSwipeRefreshLayout;

    //刷新的弹窗
    private QMUITipDialog refleshTipDialog;
    private QMUITipDialog mTipDialog;

    private WifiAdminUtils wifiAdminUtils;


    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 105)
                adapter.notifyDataSetChanged();
        }
    };
    private String uid;
    private String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initSDK();
        initView();
    }

    private void initView() {

        final QMUITopBar topBar = findViewById(R.id.topBar);
        topBar.setTitle("教学App");
        //右边添加加号的图标
        topBar.addRightImageButton(R.mipmap.ic_add, R.id.topbar_right_icon).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, NetConfigActivity.class));
            }
        });


        gizWifiDeviceList = new ArrayList<>();
        listView = findViewById(R.id.listView);
        adapter = new LVDevicesAdapter(this, gizWifiDeviceList);
        listView.setAdapter(adapter);

        //轻触的点击事件
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                startControl(gizWifiDeviceList.get(position));
            }
        });

        //长按三秒的点击事件
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long l) {
                showLongDialogOnClick(gizWifiDeviceList.get(position));
                return true;
            }
        });

        getBoundDevices();


        mSwipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        //设置下拉的颜色
        mSwipeRefreshLayout.setColorSchemeResources(android.R.color.white);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.app_color_theme_1, R.color.app_color_theme_2
                , R.color.app_color_theme_3, R.color.app_color_theme_4, R.color.app_color_theme_5
                , R.color.app_color_theme_6, R.color.app_color_theme_7);
        //手动调用通知系统测量
        mSwipeRefreshLayout.measure(0, 0);
        //打开页面就是下啦的状态
        mSwipeRefreshLayout.setRefreshing(true);
        //设置手动下啦的监听事件
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refleshTipDialog = new QMUITipDialog.Builder(MainActivity.this)
                        .setTipWord("正在刷新...")
                        .setIconType(QMUITipDialog.Builder.ICON_TYPE_LOADING)
                        .create();
                refleshTipDialog.show();


                //这里面的是可以在主线程调用的
                mSwipeRefreshLayout.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        //拿到SDK里面的设备
                        if (GizWifiSDK.sharedInstance().getDeviceList().size() != 0) {
                            gizWifiDeviceList.clear();
                            gizWifiDeviceList.addAll(GizWifiSDK.sharedInstance().getDeviceList());
                            adapter.notifyDataSetChanged();
                        }
                        refleshTipDialog.dismiss();
                        mSwipeRefreshLayout.setRefreshing(false);


                        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                        NetworkInfo info = connectivityManager.getActiveNetworkInfo();

                        //获取到了手机已经处于断开网络的状态
                        if (info == null || !info.isConnected()) {
                            mTipDialog = new QMUITipDialog.Builder(MainActivity.this)
                                    .setIconType(QMUITipDialog.Builder.ICON_TYPE_FAIL)
                                    .setTipWord("获取失败，请检查网络！")
                                    .create();
                            mTipDialog.show();
                            listView.setVisibility(View.GONE);

                        } else {

                            listView.setVisibility(View.VISIBLE);
                            //显示另外一个弹窗,如果获取到的设备为空
                            if (gizWifiDeviceList.size() == 0) {
                                mTipDialog = new QMUITipDialog.Builder(MainActivity.this)
                                        .setIconType(QMUITipDialog.Builder.ICON_TYPE_NOTHING)
                                        .setTipWord("暂无设备")
                                        .create();
                                mTipDialog.show();
                            } else {
                                mTipDialog = new QMUITipDialog.Builder(MainActivity.this)
                                        .setIconType(QMUITipDialog.Builder.ICON_TYPE_SUCCESS)
                                        .setTipWord("获取成功")
                                        .create();
                                mTipDialog.show();
                            }

                        }
                        mSwipeRefreshLayout.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                mTipDialog.dismiss();
                            }
                        }, 1500);


                    }
                }, 3000);


            }
        });
        //3s之后自动收回来
        listView.postDelayed(new Runnable() {
            @Override
            public void run() {
                mSwipeRefreshLayout.setRefreshing(false);
            }
        }, 3000);


    }


    //跳转控制
    private void startControl(GizWifiDevice gizWifiDevice) {

        if (gizWifiDevice.getNetStatus() == GizWifiDeviceNetStatus.GizDeviceOffline)
            return;

        gizWifiDevice.setListener(mWifiDeviceListener);
        gizWifiDevice.setSubscribe("b6642a6a5a784c898286d3edf77f99e0", true);


    }

    private void showLongDialogOnClick(final GizWifiDevice device) {

        //显示弹窗
        String[] items = new String[]{"重命名", "解绑设备"};
        new QMUIDialog.MenuDialogBuilder(this).addItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                switch (i) {
                    case 0:
                        showReNameDialog(device);
                        break;
                    case 1:
                        showDelateDialog(device);
                        break;
                }
                dialogInterface.dismiss();
            }

        }).show();


    }

    //解绑设备
    private void showDelateDialog(final GizWifiDevice device) {

        new QMUIDialog.MessageDialogBuilder(this)
                .setTitle("您可解绑远程设备哦！").setMessage("确定要解绑设备？")
                .addAction("取消", new QMUIDialogAction.ActionListener() {
                    @Override
                    public void onClick(QMUIDialog dialog, int index) {
                        dialog.dismiss();
                    }
                })
                .addAction("删除", new QMUIDialogAction.ActionListener() {
                    @Override
                    public void onClick(QMUIDialog dialog, int index) {
                        GizWifiSDK.sharedInstance().unbindDevice(uid, token, device.getDid());
                        dialog.dismiss();
                    }
                })
                .show();

    }


    //重命名操作
    private void showReNameDialog(final GizWifiDevice device) {
        final QMUIDialog.EditTextDialogBuilder builder = new QMUIDialog.EditTextDialogBuilder(this);
        builder.setTitle("重命名操作")
                .setInputType(InputType.TYPE_CLASS_TEXT)
                .setPlaceholder("在此输入新名字")
                .addAction("取消", new QMUIDialogAction.ActionListener() {
                    @Override
                    public void onClick(QMUIDialog dialog, int index) {
                        dialog.dismiss();
                    }
                })
                .addAction("确定", new QMUIDialogAction.ActionListener() {
                    @Override
                    public void onClick(QMUIDialog dialog, int index) {
                        String newName = builder.getEditText().getText().toString().trim();
                        //判断是否输入为空
                        if (newName.isEmpty()) {
                            Toast.makeText(MainActivity.this, "修改失败!输入为空！", Toast.LENGTH_SHORT).show();
                        } else {
                            device.setListener(mWifiDeviceListener);
                            device.setCustomInfo(null, newName);
                        }
                        dialog.dismiss();
                    }
                })
                .show();


    }

    private void getBoundDevices() {

        uid = SharePreUtil.getString(this, "_uid", null);
        token = SharePreUtil.getString(this, "_token", null);
        Log.e("==w", "uid;" + uid);
        Log.e("==w", "_token;" + token);

        if (uid != null && token != null)
            GizWifiSDK.sharedInstance().getBoundDevices(uid, token);
    }


    private void initSDK() {

        GizWifiSDK.sharedInstance().setListener(mListener);
        // 设置 AppInfo
        ConcurrentHashMap<String, String> appInfo = new ConcurrentHashMap<>();
        appInfo.put("appId", "1cc897ccdeb248a79a5bed9d1230c0eb");
        appInfo.put("appSecret", "54289f10528147faaded3f05d6f000e1");

        // 设置要过滤的设备 productKey 列表。不过滤则直接传 null
        List<ConcurrentHashMap<String, String>> productInfo = new ArrayList<>();

        ConcurrentHashMap<String, String> product = new ConcurrentHashMap<>();
        product.put("productKey", "de9e8d18d9394cce9081b25a531e552b");
        product.put("productSecret", "b6642a6a5a784c898286d3edf77f99e0");
        productInfo.add(product);

        GizWifiSDK.sharedInstance().startWithAppInfo(this, appInfo, productInfo, null, false);
    }


    private GizWifiSDKListener mListener = new GizWifiSDKListener() {


        @Override
        public void didUnbindDevice(GizWifiErrorCode result, String did) {
            if (result == GizWifiErrorCode.GIZ_SDK_SUCCESS) {
                // 解绑成功
                Toast.makeText(MainActivity.this, "恭喜，解绑成功", Toast.LENGTH_SHORT).show();
            } else {
                // 解绑失败
                Toast.makeText(MainActivity.this, "解绑失败：" + result, Toast.LENGTH_SHORT).show();
            }

        }

        @Override
        public void didNotifyEvent(GizEventType eventType, Object eventSource, GizWifiErrorCode eventID, String eventMessage) {
            super.didNotifyEvent(eventType, eventSource, eventID, eventMessage);
            Log.e("==w", "didNotifyEvent：" + eventType);
            //如果我们 的SDK初始化成功的话，就匿名登录
            //匿名登录。匿名方式登录，不需要注册用户账号。
            if (eventType == GizEventType.GizEventSDK) {
                GizWifiSDK.sharedInstance().userLoginAnonymous();
            }
        }

        @Override
        public void didUserLogin(GizWifiErrorCode result, String uid, String token) {
            super.didUserLogin(result, uid, token);
            if (result == GizWifiErrorCode.GIZ_SDK_SUCCESS) {
                // 登录成功
                Log.e("xuhongYss", "登录成功");
                SharePreUtil.putString(MainActivity.this, "_uid", uid);
                SharePreUtil.putString(MainActivity.this, "_token", token);

            } else {
                // 登录失败
                Log.e("xuhongYss", "登录失败");
            }
        }


        /**
         *
         * @param result
         * @param deviceList 已经在局域网发现的设备，包括 我们的一个未绑定的设备
         */
        @Override
        public void didDiscovered(GizWifiErrorCode result, List<GizWifiDevice> deviceList) {
            super.didDiscovered(result, deviceList);
            Log.e("xuhong1213", "didDiscovered：" + deviceList);
            //每次拿到数据都要清空一下设备集合
            gizWifiDeviceList.clear();
            gizWifiDeviceList.addAll(deviceList);
            for (int i = 0; i < deviceList.size(); i++) {
                //判断此设备是否已经绑定
                if (!deviceList.get(i).isBind()) {
                    startBindDevice(deviceList.get(i));
                }

            }
            mHandler.sendEmptyMessage(105);

        }

    };

    private void startBindDevice(GizWifiDevice device) {
        if (uid != null && token != null)
            GizWifiSDK.sharedInstance().bindRemoteDevice(uid, token, device.getMacAddress()
                    , "de9e8d18d9394cce9081b25a531e552b"
                    , "b6642a6a5a784c898286d3edf77f99e0");
    }

    @Override
    protected void onResume() {
        super.onResume();
        //保证了每次打开页面能正常的回调SDK监听
        GizWifiSDK.sharedInstance().setListener(mListener);
    }


    private GizWifiDeviceListener mWifiDeviceListener = new GizWifiDeviceListener() {


        @Override
        public void didSetSubscribe(GizWifiErrorCode result, GizWifiDevice device, boolean isSubscribed) {
            super.didSetSubscribe(result, device, isSubscribed);
            Log.e("xuhong", "订阅结果：" + result);
            Log.e("xuhong", "订阅设备：" + device);

            //如果为成功的订阅了回调，则可以跳转
            if (result == GizWifiErrorCode.GIZ_SDK_SUCCESS) {
                Intent intent = new Intent(MainActivity.this, DevivePetAvtivity.class);
                intent.putExtra("_device", device);
                startActivity(intent);
            }


        }

        @Override
        public void didSetCustomInfo(GizWifiErrorCode result, GizWifiDevice device) {
            super.didSetCustomInfo(result, device);
            if (result == GizWifiErrorCode.GIZ_SDK_SUCCESS) {
                // 修改成功
                if (GizWifiSDK.sharedInstance().getDeviceList().size() != 0) {
                    gizWifiDeviceList.clear();
                    gizWifiDeviceList.addAll(GizWifiSDK.sharedInstance().getDeviceList());
                    adapter.notifyDataSetChanged();
                    Toast.makeText(MainActivity.this, "修改成功", Toast.LENGTH_SHORT).show();
                }
            } else {
                // 修改失败
                Toast.makeText(MainActivity.this, "修改失败!", Toast.LENGTH_SHORT).show();
            }
        }
    };
}
