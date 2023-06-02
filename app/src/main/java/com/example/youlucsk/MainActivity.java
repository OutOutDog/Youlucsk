package com.example.youlucsk;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.ActivityGroup;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;

import java.util.ArrayList;

public class MainActivity extends ActivityGroup {

    //地图定位
    public LocationClient mLocationClient = null;
    private LocationClientOption option = null;
    private MyLocationListener myListener = new MyLocationListener();

    private final int SDK_PERMISSION_REQUEST = 127;
    TabHost tabHost;
    private String permissionInfo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //定位权限
        getPersimmions();

        tabHost = findViewById(R.id.tabHost);
        tabHost.setup(this.getLocalActivityManager());
//        yemianzhuan();
        //创建标签1
        TabHost.TabSpec tabspec01 = tabHost.newTabSpec("tab01");

        tabspec01.setIndicator(tabView("首页",R.drawable.radio_msg));

        tabspec01.setContent(new Intent(MainActivity.this,ym1.class));
        tabHost.addTab(tabspec01);



        //创建标签1
        TabHost.TabSpec tabspec02 = tabHost.newTabSpec("tab02");

        tabspec02.setIndicator(tabView("周边",R.drawable.radio_friend));

        tabspec02.setContent(new Intent(MainActivity.this,ym2.class));
        tabHost.addTab(tabspec02);


        //创建标签1
        TabHost.TabSpec tabspec03 = tabHost.newTabSpec("tab03");

        tabspec03.setIndicator(tabView("路线",R.drawable.radio_look));

        tabspec03.setContent(new Intent(MainActivity.this,ym3.class));
        tabHost.addTab(tabspec03);

        //创建标签1
        TabHost.TabSpec tabspec04 = tabHost.newTabSpec("tab04");

        tabspec04.setIndicator(tabView("我的",R.drawable.radio_my));

        tabspec04.setContent(new Intent(MainActivity.this,ym4.class));
        tabHost.addTab(tabspec04);


//        选择默认显示
        tabHost.setCurrentTabByTag("tab02");

    }

    /**
     * 监听当前位置
     */
    public class MyLocationListener extends BDAbstractLocationListener {
        @Override
        public void onReceiveLocation(BDLocation location) {
            //mapView 销毁后不在处理新接收的位置
            if (location == null ) {
                return;
            }
//            Log.e(TAG, "当前“我”的位置：" + location.getAddrStr());
//            Toast.makeText(MainActivity.this, "位置是"+location.getAddrStr(), Toast.LENGTH_SHORT).show();

            if (location.getLocType() == BDLocation.TypeGpsLocation
                    || location.getLocType() == BDLocation.TypeNetWorkLocation) {
//                navigateTo(location);
                String cityName = location.getCity();
//                diqu.setText(cityName);
//                Log.e(TAG, "当前定位城市：" + location.getCity());
                Toast.makeText(MainActivity.this, cityName, Toast.LENGTH_SHORT).show();
            }
        }
    }


    /**
     * 初始化定位相关
     */
    private void initLocation() {
        // 声明LocationClient类
        mLocationClient = new LocationClient(getApplicationContext());

        // 利用LocationClientOption类配置定位SDK参数
        option = new LocationClientOption();
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
        // 可选，设置定位模式，默认高精度  LocationMode.Hight_Accuracy：高精度； LocationMode. Battery_Saving：低功耗； LocationMode. Device_Sensors：仅使用设备；

        option.setCoorType("bd09ll");
        // 可选，设置返回经纬度坐标类型，默认gcj02
        // gcj02：国测局坐标；
        // bd09ll：百度经纬度坐标；
        // bd09：百度墨卡托坐标；
        // 海外地区定位，无需设置坐标类型，统一返回wgs84类型坐标

        option.setOpenGps(true);
        // 可选，设置是否使用gps，默认false
        // 使用高精度和仅用设备两种定位模式的，参数必须设置为true

        option.setLocationNotify(true);
        // 可选，设置是否当GPS有效时按照1S/1次频率输出GPS结果，默认false

        option.setIgnoreKillProcess(true);
        // 可选，定位SDK内部是一个service，并放到了独立进程。
        // 设置是否在stop的时候杀死这个进程，默认（建议）不杀死，即setIgnoreKillProcess(true)

        option.SetIgnoreCacheException(false);
        // 可选，设置是否收集Crash信息，默认收集，即参数为false

        option.setEnableSimulateGps(false);
        // 可选，设置是否需要过滤GPS仿真结果，默认需要，即参数为false

        option.setIsNeedLocationDescribe(true);
        // 可选，是否需要位置描述信息，默认为不需要，即参数为false

        option.setIsNeedLocationPoiList(true);
        // 可选，是否需要周边POI信息，默认为不需要，即参数为false

        option.setIsNeedAddress(true);// 获取详细信息
        //设置扫描间隔
//        option.setScanSpan(10000);
        option.setAddrType("all");
        mLocationClient.setLocOption(option);
        // 注册监听函数
        mLocationClient.registerLocationListener(myListener);
        mLocationClient.start();
    }

    @TargetApi(23)
    private void getPersimmions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            ArrayList<String> permissions = new ArrayList<String>();
            /***
             * 定位权限为必须权限，用户如果禁止，则每次进入都会申请
             */
            // 定位精确位置
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                permissions.add(Manifest.permission.ACCESS_FINE_LOCATION);
            }
            if (checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                permissions.add(Manifest.permission.ACCESS_COARSE_LOCATION);
            }
            /*
             * 读写权限和电话状态权限非必要权限(建议授予)只会申请一次，用户同意或者禁止，只会弹一次
             */
            // 读写权限
            if (addPermission(permissions, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                permissionInfo += "Manifest.permission.WRITE_EXTERNAL_STORAGE Deny \n";
            }
            if (permissions.size() > 0) {
                requestPermissions(permissions.toArray(new String[permissions.size()]), SDK_PERMISSION_REQUEST);
            }
        }
    }

    @TargetApi(23)
    private boolean addPermission(ArrayList<String> permissionsList, String permission) {
        // 如果应用没有获得对应权限,则添加到列表中,准备批量申请
        if (checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
            if (shouldShowRequestPermissionRationale(permission)) {
                return true;
            } else {
                permissionsList.add(permission);
                return false;
            }
        } else {
            return true;
        }
    }



    public View tabView(String showTitle, int tabIcon) {
        View tabView = LayoutInflater.from(this).inflate(R.layout.tablayout,null,false);
        //不用样式        不作为根元素
        TextView titleTxt01 = (TextView) tabView.findViewById(R.id.tabTxt);
        titleTxt01.setText(showTitle);

        ImageView titleImg = (ImageView) tabView.findViewById(R.id.tabImg);
        titleImg.setBackgroundResource(tabIcon);

        return tabView;
    }

}