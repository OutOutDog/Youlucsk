package com.example.youlucsk;

import android.Manifest;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;

import java.util.ArrayList;
import java.util.List;

public class ym1 extends AppCompatActivity {
    public LocationClient mLocationClient = null;
    private MyLocationListener myListener = new MyLocationListener();
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ym1);

        mLocationClient = new LocationClient(getApplicationContext());
        mLocationClient.registerLocationListener(myListener);
//        // 权限请求
        getPermissionMethod();

    }
    // 权限请求
    private void getPermissionMethod() {
        List<String> permissionList = new ArrayList<>();

        if(ContextCompat.checkSelfPermission(ym1.this, Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED){
            permissionList.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }

        Log.i(TAG, "getPermissionMethod: permissionListSize:"+permissionList.size());
        if (!permissionList.isEmpty()){ //权限列表不是空
            String[] permissions = permissionList.toArray(new String[permissionList.size()]);
            ActivityCompat.requestPermissions(ym1.this,permissions,1);
        }else{
            Log.i(TAG, "getPermissionMethod: requestLocation !permissionList.isEmpty()里");
            requestLocation();
        }
    }
    //开启 start 定位，默认只启动一次，需要自己设置间隔次数
    private void requestLocation() {
        initLocation();//其他请求设置
        mLocationClient.start();  //定位请求开启
    }

    private void initLocation() {
        LocationClientOption option = new LocationClientOption();
        option.setIsNeedAddress(true);
        option.setLocationMode(LocationClientOption.LocationMode.Device_Sensors);  // 定位模式是仅限设备模式，也就是仅允许GPS来定位。
//        option.setScanSpan(2000);
        mLocationClient.setLocOption(option);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case 1:
                if(grantResults.length>0){
                    for (int result:grantResults){
                        if (result != PackageManager.PERMISSION_GRANTED){
                            Toast.makeText(this, "必须统一所有权限才能使用本程序", Toast.LENGTH_SHORT).show();
                            finish();
                            return;
                        }
                    }
                    requestLocation();
                }else
                {
                    Toast.makeText(this, "发生未知错误", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
        }
    }

    private static final String TAG = "MainActivity";

    public class MyLocationListener extends BDAbstractLocationListener {
        @Override
        public void onReceiveLocation( BDLocation location){
            //此处的BDLocation为定位结果信息类，通过它的各种get方法可获取定位相关的全部结果
            //以下只列举部分获取地址相关的结果信息
            //更多结果信息获取说明，请参照类参考中BDLocation类中的说明
            Log.d(TAG, "run: 纬度："+location.getLatitude());
            String addr = location.getAddrStr();    //获取详细地址信息
            Log.d(TAG, "onReceiveLocation:详细地址信 "+addr);
            String country = location.getCountry();    //获取国家
            Log.d(TAG, "onReceiveLocation: 国家："+country);
            String province = location.getProvince();    //获取省份
            Log.d(TAG, "onReceiveLocation: 省份："+province);
            String city = location.getCity();    //获取城市
            Log.d(TAG, "onReceiveLocation: 城市："+city);
            String district = location.getDistrict();    //获取区县
            Log.d(TAG, "onReceiveLocation: 区县信息："+district);
            String street = location.getStreet();    //获取街道信息
            Log.d(TAG, "onReceiveLocation: 街道信息："+street);

            if (location.getLocType() == BDLocation.TypeNetWorkLocation)
                Log.d(TAG, "onReceiveLocation: 定位方式：NET"+location.getLocType());
            else if(location.getLocType() == BDLocation.TypeGpsLocation )
                Log.d(TAG, "onReceiveLocation: 定位方式：GPS"+location.getLocType());
            else
                Log.d(TAG, "onReceiveLocation: 定位出错");
        }

    }
}


