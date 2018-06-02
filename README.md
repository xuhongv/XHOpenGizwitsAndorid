

----------


<p align="center">
  <img src="https://github.com/xuhongv/XHOpenGizwitsAndorid/blob/master/doc/github.png" width="550px" height="360px" alt="Banner" />
</p>




### 一.前言。

 
- <font color=black> 此开源框架 `XHOpenSouresGizAndroid`设计在于个人以后的面试作品要用到，希望大家多多支持，开源于 GitHub , 欢迎收藏star一下！

- <font color=green>此工程全部由我小徐一个人完成，以下博文如有不对或触犯了您的权益，请留言，本人及时修改！

- <font color=red> **基础版**：伴有视频讲解：http://open.21ic.com/open/lesson/756 ，基础版本适合我们大多人的开发，代码开源免费，欢迎star !；

- <font color=red> **进阶版**：伴有视频讲解（后面会补充），进阶版加进了支持扫描、本地扫描添加设备，支持多设备支持控制！录制视频都嗓子沙哑了，收点小费；

> <font color=black>此框架只用了一周星期做了出来，因为对机智云的框架比较熟悉了 ！期间SDK初始化出了问题，去咨询了机智云的官方人员，至于为什么要做此框架，因为在实际开中，我们发现机智云自动生成的代码，有各种我们用不到的强大功能，比如以下：

 - ①、~~繁琐的登录注册功能，有时候我们仅仅想控制设备而已...~~
 
 - ②、~~繁琐的各种我们用不到的企业开发者的推送功能，比如极光推送、百度推送....~~

 - ③、~~繁琐的第三方登录，微信登录、QQ登录等...~~

 - ④、~~哈哈，我们还想去掉那个机智云的广告，商城接口...~~

 - ⑤、~~因为绝大部分开发者都是用8266，抛弃繁琐的要选择各种芯片的界面...~~

 - ⑥、~~对于开发者来说，最主要的是耦合性太高了，一动代码全部都要动...~~

> 抛弃上面我们不需要的功能之后，我想主要实现以下几大功能即可：

- ①、**实现自动匿名登录，没有各种第三方登录选择，无须手动点击“跳过”进去设备界面。**

- ②、**实现一键配网，去掉softAP模式配网，无须选择各种芯片，固定为乐鑫的esp8266。**

- ③、**一旦检测到了局域网的新设备，实现自动绑定设备。**

- ④、**工程不采用设计框架，解耦性强，依赖的架包是`  'appcompat-v7:26.0.0-alpha1'`版本，全新的控件，全新的MD风格。**

- ⑤、**开放接口给开发者，实现换颜色的功能。**

- ⑥、**开放修改设备的PK和PS接口、开发者的APP的Appid给开发者修改。**

- ⑦、**支持多设备添加在同一个APP，即一个APP控制多个设备，详情见Demo代码。**


----------
### 二 . 进阶版本界面欣赏。


----------
![这里写图片描述](http://img.blog.csdn.net/20180412103614348?watermark/2/text/aHR0cDovL2Jsb2cuY3Nkbi5uZXQveGg4NzAxODkyNDg=/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70/gravity/SouthEast)


----------
 - ①. 可以看到定时器的定时界面是一个漂亮的采集器；
 - ②. 主界面有2种不同类型的设备。

----------
![这里写图片描述](http://img.blog.csdn.net/20180416150615982?watermark/2/text/aHR0cDovL2Jsb2cuY3Nkbi5uZXQveGg4NzAxODkyNDg=/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70/gravity/SouthEast)

----------
### 二 . 进阶版本的开放接口修改。


----------

- 首先上一张工程截图：


----------
![这里写图片描述](http://img.blog.csdn.net/20180416151625610?watermark/2/text/aHR0cDovL2Jsb2cuY3Nkbi5uZXQveGg4NzAxODkyNDg=/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70/gravity/SouthEast)


----------
 - 工程里面内嵌了可以控制2个产品的界面；
 
 - 其硬件是8266，代码开源如下，需要需要调试项目的设备，私聊我，我开虚拟机你调试：
  
     - 微信宠物屋标准数据点产品： [点我下载源码](https://github.com/xuhongv/StudyInEsp8266/tree/master/Gizkit_soc_pet)
     - 2017年国庆节时候在机智云社区发表一个定时开关灯产品： [点我下载源码](https://github.com/xuhongv/StudyInEsp8266/tree/master/GokitTimerLight)


----------

#### 2.1 怎么样添加产品?


----------


 - 第一步 ：  
 


----------

1. 如果想完全替代为你的产品，首先在 `Constant.java` 文件，把 app id 和app sercret 替换为您的产品对应的属性；

2. 如果要把这个app作为2个或以上控制设备的话，<font color=red>**确保你的全部设备的应用配置都要绑定在同一个产品的应用！**</font>  

3. 每个产品都有唯一的key 和 密钥 ，请依次按照下面顺序格式写入其他您产品信息；
 


----------

```
    //app id
    public static final String APP_ID = "ba8298ff18f949ef9de43b0eabd7eaca";

    //app sercret
    public static final String APP_SECRET = "cea5cc1fa67b4affac803f43686c4ef9";


    /*********************************
     * 以下为各种产品信息
     **********************************************/

    //微信宠物屋产品Key
    public static final String PET_PK = "3218bec508fb49109e2d3eb19f43154f";
    //微信宠物屋产品密钥
    public static final String PET_PS = "d66ae067aefc409989d7b5e0b0004dec";

    //定时开关灯产品Key
    public static final String TIMER_PK = "02ace125034540bfa64dc77bff1d45a8";
    //定时开关灯产品密钥
    public static final String TIMER_PS = "cb27cef9d72d41cf84174e1e14dd330e";
```

  


----------
 - 第二步 ：  

1. 找到`MainAvtivity.java`下面的代码，下面的代码意义在于把产品信息初始化在SDK中，注意每个产品对应一个`ConcurrentHashMap<String, String> `对象！

----------
![这里写图片描述](http://img.blog.csdn.net/20180416153502718?watermark/2/text/aHR0cDovL2Jsb2cuY3Nkbi5uZXQveGg4NzAxODkyNDg=/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70/gravity/SouthEast)

 


----------
备注：


```
        //此处初始化sdk

        //这个是绑定app id和appSecret
        ConcurrentHashMap<String, String> appInfo = new ConcurrentHashMap<>();
        appInfo.put("appId", Constant.APP_ID);
        appInfo.put("appSecret", Constant.APP_SECRET);

        List<ConcurrentHashMap<String, String>> productInfo = new ArrayList<>();

        //这个是绑定微信宠物屋的productKey和productSecret
        ConcurrentHashMap<String, String> product = new ConcurrentHashMap<>();
        product.put("productKey", Constant.PET_PK);
        product.put("productSecret", Constant.PET_PS);
        productInfo.add(product);

       //这个是定时开关灯的productKey和productSecret
        ConcurrentHashMap<String, String> product2 = new ConcurrentHashMap<>();
        product2.put("productKey", Constant.TIMER_PK);
        product2.put("productSecret", Constant.TIMER_PS);
        productInfo.add(product2);

```


----------
 - 第三步 ：  

   - 新建一个Activity , 继承 `BaseDeviceControlActivity` ，注意在 AndroidMenifest 注册 ；
   
   - 其对应的布局一定要有 `com.qmuiteam.qmui.widget.QMUITopBar` 导航栏，其在布局文件的代码直接为：
  - 这个Activity的`onCreat()`方法里面，必须初始化上面的QMUITopBar，见下面的`initView()`：
  

```
    <com.qmuiteam.qmui.widget.QMUITopBar
        android:id="@+id/topBar"
        app:qmui_topbar_bg_color="@color/colorPrimary"
        app:qmui_topbar_need_separator="true"
        android:layout_width="match_parent"
        android:layout_height="?attr/qmui_topbar_height" />
```

```
   private void initView() {
        //显示状态返回箭头
        //设置标题，如果备注名为空则显示产品云端注册名字，否则显示备注名
        qmuiTopBar = findViewById(R.id.topBar);
        qmuiTopBar.setTitle(gizWifiDevice.getAlias().isEmpty() ? gizWifiDevice.getProductName() : gizWifiDevice.getAlias());
        qmuiTopBar.addLeftImageButton(R.mipmap.ic_back, R.id.topbar_left_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

```
----------
 - 第四步 ：
  
   - 初始化一个Activity文件基本好了之后，我们的逻辑控制跳转修改，在`MainActivity.java`里面代码，把下面截图的switch里面修改即可，原理是根据产品的PK数值跳转的：
   
   
   ![这里写图片描述](http://img.blog.csdn.net/20180416154759514?watermark/2/text/aHR0cDovL2Jsb2cuY3Nkbi5uZXQveGg4NzAxODkyNDg=/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70/gravity/SouthEast) 

----------
 - 第五步 ：
    
 细节增加，在`BaseDeviceControlActivity.java`文件里面的`onDestroy()`方法里面，添加 取消订阅设备操作，否则无法新界面无法同步设备！

```
  //取消订阅
    @Override
    protected void onDestroy() {
        super.onDestroy();
        gizWifiDevice.setListener(null);
        switch (gizWifiDevice.getProductKey()) {
            case Constant.PET_PK:
                gizWifiDevice.setSubscribe(Constant.PET_PS, false);
                break;
            case Constant.TIMER_PK:
                gizWifiDevice.setSubscribe(Constant.TIMER_PS, false);
                break;
        }
    }
```

 ----------
 - 第六步 ：
  
   剖析云端下发数据？在控制界面重写`didReceiveData()`方法即可！
  


----------
 - 进阶版本源代码下载：http://www.demodashi.com/demo/12847.html


