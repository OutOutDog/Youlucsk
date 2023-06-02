package com.example.youlucsk;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.overlayutil.DrivingRouteOverlay;
import com.baidu.mapapi.overlayutil.OverlayManager;
import com.baidu.mapapi.search.core.RouteLine;
import com.baidu.mapapi.search.core.RouteNode;
import com.baidu.mapapi.search.route.BikingRouteResult;
import com.baidu.mapapi.search.route.DrivingRouteLine;
import com.baidu.mapapi.search.route.DrivingRouteResult;
import com.baidu.mapapi.search.route.IndoorRouteResult;
import com.baidu.mapapi.search.route.MassTransitRouteResult;
import com.baidu.mapapi.search.route.OnGetRoutePlanResultListener;
import com.baidu.mapapi.search.route.TransitRouteResult;
import com.baidu.mapapi.search.route.WalkingRouteResult;
import com.example.youlucsk.BJ3L.NodeUtils;
import com.example.youlucsk.XuLie.xLDRR;

import java.util.ArrayList;
import java.util.List;

public class ym3_11 extends AppCompatActivity implements OnGetRoutePlanResultListener, BaiduMap.OnMapClickListener{


    // 地图View
    private MapView mMapView = null;
    private BaiduMap mBaidumap = null;

    // 浏览路线节点相关
    private Button mBtnPre = null; // 上一个节点
    private Button mBtnNext = null; // 下一个节点
    private RouteLine mRouteLine = null;
    private OverlayManager mRouteOverlay = null;
    // 驾车路线规划参数
    private NodeUtils mNodeUtils = null;
//    TextView tv_4;
    private TextView tvr2;
    //路线
    private DrivingRouteLine routeLine = new DrivingRouteLine();
    //途经点
    List<RouteNode> rnLine = new ArrayList<>();
    int zzlc = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ym3_11);
        tvr2 = findViewById(R.id.tvr2);
        mMapView = (MapView) findViewById(R.id.map);
        // 初始化地图
        mBaidumap = mMapView.getMap();
        mNodeUtils = new NodeUtils(this, mBaidumap);
        mBaidumap.setOnMapClickListener(this);

//        tv_4 = findViewById(R.id.tv_4);
        Intent intent = getIntent();
        xLDRR xld = intent.getParcelableExtra("xld");
        if(xld != null ) {
            luXcd(xld);

        }else{
            Toast.makeText(this, "数据未传输到位", Toast.LENGTH_SHORT).show();
        }
        tvr2.setText(zzlc+"");
    }

    //    传递路线数据到本页面
    public void luXcd(xLDRR xld){
        List<DrivingRouteResult> results = xld.drrList;
        tvr2.setText("有个路线"+results.size()+"");
        RouteNode starting = null;
        RouteNode terminal = null;
        List<DrivingRouteLine.DrivingStep> steps = new ArrayList<>();
        int duration = 0;
        int distance = 0;
        for (int i = 0 ; i < results.size() ; i++){
            DrivingRouteResult result = results.get(i);
            RouteLine drl = result.getRouteLines().get(0);
            steps.addAll(drl.getAllStep());
//            for (int j = 0;j<steps1.size();j++){
//                steps.add(steps1.get(j));
//            }
//            steps.addAll(steps1);//获取路线中的所有路段
            if (i==0){
                starting = drl.getStarting();//起点
                RouteNode rn = drl.getTerminal();
                rnLine.add(rn);
            }else if (i==(results.size()-1)){
                terminal = drl.getTerminal();//终点
            }else{
                RouteNode rn = drl.getTerminal();
                rnLine.add(rn);
            }
            duration += drl.getDuration();//耗时
            distance += drl.getDistance();//长度

        }
        zzlc = distance;
        Toast.makeText(this, distance+"", Toast.LENGTH_SHORT).show();
        routeLine.setDistance(distance);//设置路线长度
        routeLine.setDuration(duration);//设置路线耗时
        routeLine.setStarting(starting);//设置路线起点信息
        routeLine.setSteps(steps);//设置路线中的所有路段
        routeLine.setTerminal(terminal);//设置路线终点信息

        setTitle(starting.getTitle()+"到"+terminal.getTitle());//设置路线名称

        dquhuatu(routeLine);

        LatLng llng = starting.getLocation();
        MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(llng);
        mBaidumap.animateMapStatus(u);
    }

    public void dquhuatu(DrivingRouteLine result){
        DrivingRouteOverlay overlay = new MyDrivingRouteOverlay(mBaidumap);
        mBaidumap.setOnMarkerClickListener(overlay);
        mRouteOverlay = overlay;
        overlay.setData(routeLine);
        overlay.addToMap();
        overlay.zoomToSpan();
        for(RouteNode rn : rnLine){
            huiZhi(rn);
        }
        //按钮隐藏
/*        mBtnPre.setVisibility(View.VISIBLE);
        mBtnNext.setVisibility(View.VISIBLE);*/
    }
    //    绘制点坐标
    public void huiZhi(RouteNode routeNode){
        LatLng point = routeNode.getLocation();
        //定义Maker坐标点
//        LatLng point = new LatLng(39.963175, 116.400244);
//构建Marker图标
        BitmapDescriptor bitmap = BitmapDescriptorFactory
                .fromResource(R.drawable.icon_marka);
//构建MarkerOption，用于在地图上添加Marker
        OverlayOptions option = new MarkerOptions()
                .position(point)
                .icon(bitmap);
//在地图上添加Marker，并显示
        mBaidumap.addOverlay(option);

        //用来构造InfoWindow的Button
        Button button = new Button(getApplicationContext());
        button.setBackgroundResource(R.drawable.popup);
        button.setText(routeNode.getTitle());

//构造InfoWindow
//point 描述的位置点
//-100 InfoWindow相对于point在y轴的偏移量
        InfoWindow mInfoWindow = new InfoWindow(button, point, -100);

//使InfoWindow生效
        mBaidumap.showInfoWindow(mInfoWindow);

    }



    // 定制RouteOverly
    private class MyDrivingRouteOverlay extends DrivingRouteOverlay {

        private MyDrivingRouteOverlay(BaiduMap baiduMap) {
            super(baiduMap);
        }
    }
    @Override
    public void onMapClick(LatLng latLng) {
        // 隐藏当前InfoWindow
        mBaidumap.hideInfoWindow();
    }

    @Override
    public void onMapPoiClick(MapPoi mapPoi) {

    }

    @Override
    public void onGetWalkingRouteResult(WalkingRouteResult walkingRouteResult) {

    }

    @Override
    public void onGetTransitRouteResult(TransitRouteResult transitRouteResult) {

    }

    @Override
    public void onGetMassTransitRouteResult(MassTransitRouteResult massTransitRouteResult) {

    }

    /**
     * 驾车路线结果回调
     *
     *  驾车路线结果
     */
    //@param result
    @Override
    public void onGetDrivingRouteResult(DrivingRouteResult drivingRouteResult) {

    }

    @Override
    public void onGetIndoorRouteResult(IndoorRouteResult indoorRouteResult) {

    }

    @Override
    public void onGetBikingRouteResult(BikingRouteResult bikingRouteResult) {

    }
}
