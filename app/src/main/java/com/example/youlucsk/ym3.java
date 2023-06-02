package com.example.youlucsk;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.transition.Slide;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.model.inner.GeoPoint;
import com.baidu.mapapi.search.core.CityInfo;
import com.baidu.mapapi.search.core.PoiDetailInfo;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.core.RouteLine;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.baidu.mapapi.search.poi.OnGetPoiSearchResultListener;
import com.baidu.mapapi.search.poi.PoiCitySearchOption;
import com.baidu.mapapi.search.poi.PoiDetailResult;
import com.baidu.mapapi.search.poi.PoiDetailSearchResult;
import com.baidu.mapapi.search.poi.PoiIndoorResult;
import com.baidu.mapapi.search.poi.PoiNearbySearchOption;
import com.baidu.mapapi.search.poi.PoiResult;
import com.baidu.mapapi.search.poi.PoiSearch;
import com.baidu.mapapi.search.poi.PoiSortType;
import com.baidu.mapapi.search.route.BikingRouteResult;
import com.baidu.mapapi.search.route.DrivingRoutePlanOption;
import com.baidu.mapapi.search.route.DrivingRouteResult;
import com.baidu.mapapi.search.route.IndoorRouteResult;
import com.baidu.mapapi.search.route.MassTransitRouteResult;
import com.baidu.mapapi.search.route.OnGetRoutePlanResultListener;
import com.baidu.mapapi.search.route.PlanNode;
import com.baidu.mapapi.search.route.RoutePlanSearch;
import com.baidu.mapapi.search.route.SuggestAddrInfo;
import com.baidu.mapapi.search.route.TransitRouteResult;
import com.baidu.mapapi.search.route.WalkingRouteLine;
import com.baidu.mapapi.search.route.WalkingRouteResult;
import com.baidu.mapapi.search.sug.OnGetSuggestionResultListener;
import com.baidu.mapapi.search.sug.SuggestionResult;
import com.baidu.mapapi.search.sug.SuggestionSearch;
import com.baidu.mapapi.search.sug.SuggestionSearchOption;
import com.baidu.mapapi.utils.DistanceUtil;
import com.example.youlucsk.BJ3L.NodeUtils;
import com.example.youlucsk.XuLie.xLDRR;
import com.example.youlucsk.adapter.Adapter_SearchAddress;
import com.example.youlucsk.jisuan.Floyd;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ym3 extends AppCompatActivity implements OnGetPoiSearchResultListener, OnGetRoutePlanResultListener, BaiduMap.OnMapClickListener{

    //地图定位
    public LocationClient mLocationClient = null;
    private LocationClientOption option = null;
    private MyLocationListener myListener = new MyLocationListener();;

    private RadioGroup radioGroup;
    private RadioButton radioButton;

    private LinearLayout ll_poiSearch;

    private ScrollView scrollView;
    private LinearLayout llVipNumContainer;
    private LinearLayout llAddVipNum;
    private Button btnYes;
    private TextView diqu;
    private Button btn_yes;

    private final int SDK_PERMISSION_REQUEST = 127;
    private String permissionInfo;

    private static String TAG = "BJ2";
    //poi检索
    private GeoCoder mGeoCoder;//反向地理解析，获取周边poi
    private SuggestionSearch mSuggestionSearch = null;//地点检索输入提示检索（Sug检索）
    private int radius = 300;//poi检索半径
    private ListView lv_poiSearch;
    private Context mContext;
    private String keyword = "";
    private PoiSearch mPoiSearch = null;
    private String cityName = "";
    private int pageSize = 50;
    private int loadIndex = 0;//分页页码
    private List<PoiInfo> poiInfoListForSearch = new ArrayList<>();//检索结果集合
    private List<PoiInfo> poiInfoListForGeoCoder = new ArrayList<>();//地理反向解析获取的poi
    private Adapter_SearchAddress adapter_searchAddress;
    private LatLng currentLatLng;//当前所在位置


    //    TextView tVc2;
    // 搜索模块，也可去掉地图模块独立使用
    private RoutePlanSearch mSearch = null;

    // 驾车路线结果
    private DrivingRouteResult mDrivingRouteResult = null;
    private RouteLine mRouteLine = null;
    //    private DrivingRouteLine mDrivingRouteLine = null;
    private boolean mUseDefaultIcon = false;
    private boolean hasShowDialog = false;

    // 选择路线策略view
    private Spinner mSpinner;

    // 驾车路线规划参数
    private DrivingRoutePlanOption mDrivingRoutePlanOption;
    private NodeUtils mNodeUtils;
    private EditText mEditStartCity;
    private EditText mEditEndCity;
    private AutoCompleteTextView mStrartNodeView;
    private AutoCompleteTextView mEndNodeView;
    private CheckBox mTrafficPolicyCB;


    int[][] lux = null;
    //    int ixh = 0;
    int jxh = 0;
    String[] dizhiw;


    String[] dizhi = new String[20];
    LatLng[] zuobiao = new LatLng[20];
    int ctu = 0;//270行的循环用的

    //    private List<? extends RouteLine> mRouteLines;
//    mRouteLine[] Arrarou;
//    Map<String, RouteLine> mapLin = new HashMap<>();
    Map<String, DrivingRouteResult> mapdrr = new HashMap<>();
//    Map<String,DrivingRouteLine> mapdrl = new HashMap<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
        getWindow().setAllowEnterTransitionOverlap(true);
        getWindow().setAllowReturnTransitionOverlap(true);
        Slide slide = new Slide();
        slide.setDuration(1000);
        getWindow().setReturnTransition(slide);
        getWindow().setEnterTransition(slide);


        setContentView(R.layout.activity_ym3);



        mContext = ym3.this;
        lv_poiSearch = findViewById(R.id.lv_poiSearch);
        ll_poiSearch = findViewById(R.id.ll_poiSearch);
        btn_yes = findViewById(R.id.btn_yes);

//        tVc2 = findViewById(R.id.tVc2);
        //定位权限
//        getPersimmions();

        //初始化地图及定位
//        initMap();
        initLocation();
        //初始化地理解析、建议搜索、poi搜索
        initGeoCoder();
        initSuggestionSearch();
        initPoiSearch();


        // 初始化搜索模块，注册事件监听
        mSearch = RoutePlanSearch.newInstance();
        mSearch.setOnGetRoutePlanResultListener(this);

        //监听输入框

//        xf();

        initView();
        setListeners();



        monitorEditTextChage();




        //        列表单击事件
        ListView.OnItemClickListener listenerL = new ListView.OnItemClickListener(){
            //
            @Override
            public void onItemClick(AdapterView<?> parent, final View view, int position, long id) {

//                PoiInfo poin = poiInfoListForGeoCoder.get(position);
                PoiInfo poin = poiInfoListForSearch.get(position);

                String a1=poin.getName().toString();
                LatLng a2=poin.location;
                for (int i = 0; i < llVipNumContainer.getChildCount(); i++) {
                    View viewItem = llVipNumContainer.getChildAt(i);
                    EditText et011 = viewItem.findViewById(R.id.et_vip_number);
                    if(et011.hasFocus()){
                        zuobiao[i] = a2;
                        dizhi[i] = a1;
                        et011.setText(a1);
                    }

                };
            }
        };
        lv_poiSearch.setOnItemClickListener(listenerL);



    }







/*    //输入框焦点判断
        EditText.OnFocusChangeListener listenerlec = new View.OnFocusChangeListener() {
        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            v.setTooltipText("wang");
        }
    }*/

//                setOnFocusChangeListener



    private void initView(){
        diqu = findViewById(R.id.diqu);
        scrollView = findViewById(R.id.scroll_view);
        llVipNumContainer = findViewById(R.id.ll_vip_num_container);
        llAddVipNum = findViewById(R.id.ll_add_vip_num);
        btnYes = findViewById(R.id.btn_yes);

        for(int i = 0; i < 3; i++){
            addViewItem();
        }
    }

    //点击事件
    private void setListeners(){
        llAddVipNum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addViewItem();
            }
        });

        btnYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<String> list = getDataList();
                if(list.size() <= 0){
                    Toast.makeText(ym3.this, "请输入市内地点", Toast.LENGTH_SHORT).show();
                }else{
                    dizhiw = new String[llVipNumContainer.getChildCount()];
                    boolean pd = true;
                    for(int i = 0;i<dizhiw.length;i++){
//                        tVc2.setText(tVc2.getText()+"\n"+dizhi[i]);
                        dizhiw[i] = dizhi[i];
//                        Toast.makeText(mContext, dizhiw[i], Toast.LENGTH_SHORT).show();
                        if (dizhiw[i] == null){
                            pd = false;
                            break;
                        }
                    }
                    if(pd){
                        String acte = "";
                        for(String str : dizhiw){
                            acte += str+",";
                        }
//                        tVc2.setText(tVc2.getText()+acte);
                        lux = new int[dizhiw.length][dizhiw.length];

                        for (int i = 0 ; i < lux.length ;i++){
                            for (int j = 0 ; j < lux[i].length ; j++){
                                lux[i][j] = 0;
                            }
                        }
                        String diqudiz = diqu.getText().toString().trim();
                        jxh = 0;
                        for(int i = 0;i<dizhiw.length;i++){
                            for (int j = 0;j<dizhiw.length;j++){
                                // 初始化搜索模块，注册事件监听
//                                mSearch = RoutePlanSearch.newInstance();
//                                mSearch.setOnGetRoutePlanResultListener(BJ3.this);

                                // 实际使用中请对起点终点城市进行正确的设定

//                                setac[i][j] = dizhiw[i]-dizhiw[j]\
//                                tVc2.setText(tVc2.getText()+"\n"+"从["+dizhiw[i]+"]到["+dizhiw[j]+"]");
//                                地点规划
//                                 设置起终点信息 起点参数
                                Lxff(diqudiz,dizhiw[i],diqudiz,dizhiw[j]);
//                                PlanNode startNode = PlanNode.withCityNameAndPlaceName(diqudiz, dizhiw[i]);
//                                // 终点参数
//                                PlanNode endNode = PlanNode.withCityNameAndPlaceName(diqudiz, dizhiw[j]);
//
////                                // 设置起终点信息 起点参数
////                                PlanNode startNode = PlanNode.withLocation(zuobiao[i]);
////                                // 终点参数
////                                PlanNode endNode = PlanNode.withLocation(zuobiao[j]);
//
//
//                                //路线策略
//                                mDrivingRoutePlanOption = new DrivingRoutePlanOption();
//                                // 时间优先策略，  默认时间优先
//                                mDrivingRoutePlanOption.policy(DrivingRoutePlanOption.DrivingPolicy.ECAR_TIME_FIRST);
//                                // 关闭路况
////                                mDrivingRoutePlanOption.trafficPolicy(DrivingRoutePlanOption.DrivingTrafficPolicy.ROUTE_PATH);
//                                // 发起驾车路线规划
////                                mSearch.drivingSearch(mDrivingRoutePlanOption
////                                        .from(startNode) // 起点
////                                        .to(endNode)); // 终点
//                                /*Toast.makeText(mContext, dizhiw[j]+"----"+dizhiw[i], Toast.LENGTH_SHORT).show();*/
////                                Log.d(zuobiao[j]+"----"+zuobiao[i], "");
//                                Toast.makeText(mContext, zuobiao[j]+"----"+zuobiao[i], Toast.LENGTH_SHORT).show();
//                                mSearch.drivingSearch(mDrivingRoutePlanOption.from(startNode).to(endNode));
//                                mSearch.destroy();
//                                mRouteLine.getDistance()
//                                String jlc = jLchangd+"";
//                                Toast.makeText(BJ3.this, dizhiw[i]+"到达"+dizhiw[j]+"距离是"+jlc+"米", Toast.LENGTH_SHORT).show();


                            }
                        }

                        Toast.makeText(mContext, "结束了", Toast.LENGTH_SHORT).show();


                        /*fanx fa = flyd.Sf(0);
                        List<Integer> ljss = fa.getLjs();
//                        System.out.println("距离为："+flyd.Sf(0).getCd());
                        for(Integer lx:ljss) {
                            acct += dizhiw[lx]+"->";
                        }
                        acct = acct.substring(0, acct.length() - 2);
                        acct += "距离为："+fa.getCd();
*/
//                        lux.length;

//                        intent.putIntegerArrayListExtra(a);
//                        startActivity(intent);

//                        Toast.makeText(BJ3.this, sb.toString(), Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(mContext, "地址不可为空", Toast.LENGTH_SHORT).show();
                    }
                    /*StringBuilder sb = new StringBuilder("");
                    for(String str : list){
                        sb.append(str).append(",");
                    }
                    Toast.makeText(BJ3.this, sb.toString(), Toast.LENGTH_SHORT).show();*/
                }
            }
        });
    }

    //路线规划方法
    public void Lxff(String q1,String d1,String q2,String d2){
        mRouteLine = null;
//        Toast.makeText(mContext, "循环", Toast.LENGTH_SHORT).show();
//                                 设置起终点信息 起点参数
        PlanNode startNode = PlanNode.withCityNameAndPlaceName(q1, d1);
        // 终点参数
        PlanNode endNode = PlanNode.withCityNameAndPlaceName(q2, d2);

//                                // 设置起终点信息 起点参数
//                                PlanNode startNode = PlanNode.withLocation(zuobiao[i]);
//                                // 终点参数
//                                PlanNode endNode = PlanNode.withLocation(zuobiao[j]);


        //路线策略
        mDrivingRoutePlanOption = new DrivingRoutePlanOption();
        // 时间优先策略，  默认时间优先
        mDrivingRoutePlanOption.policy(DrivingRoutePlanOption.DrivingPolicy.ECAR_TIME_FIRST);
        // 关闭路况
//                                mDrivingRoutePlanOption.trafficPolicy(DrivingRoutePlanOption.DrivingTrafficPolicy.ROUTE_PATH);
        // 发起驾车路线规划
        mDrivingRoutePlanOption.trafficPolicy(DrivingRoutePlanOption.DrivingTrafficPolicy.ROUTE_PATH);
//                                mSearch.drivingSearch(mDrivingRoutePlanOption
//                                        .from(startNode) // 起点
//                                        .to(endNode)); // 终点
        /*Toast.makeText(mContext, dizhiw[j]+"----"+dizhiw[i], Toast.LENGTH_SHORT).show();*/
//                                Log.d(zuobiao[j]+"----"+zuobiao[i], "");
//        Toast.makeText(mContext, zuobiao[j]+"----"+zuobiao[i], Toast.LENGTH_SHORT).show();
        mSearch.drivingSearch(mDrivingRoutePlanOption.from(startNode).to(endNode));
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
     * @param result 驾车路线结果
     */
    @Override
    public void onGetDrivingRouteResult(DrivingRouteResult result) {

//        Toast.makeText(mContext, "进入了lux"+lux.length+"jxh"+jxh, Toast.LENGTH_SHORT).show();

//        mDrivingRouteLine = null;
        int ctimg = 1;
        if (result != null && result.error == SearchResult.ERRORNO.AMBIGUOUS_ROURE_ADDR) {
            // 起终点或途经点地址有岐义，通过以下接口获取建议查询信息
//            SuggestAddrInfo saif = result.getSuggestAddrInfo();
//            PoiInfo zdd = saif.getSuggestEndNode().get(0); //终点地址
//
//            PoiInfo qdd = saif.getSuggestStartNode().get(0); //起点地址
            result.getSuggestAddrInfo();
//            Toast.makeText(mContext, qdd.name+"------"+qdd.name, Toast.LENGTH_SHORT).show();
//            Lxff(qdd.city,qdd.name,zdd.city,qdd.name);


            ctimg = 0;
//            lux[ixh][jxh] = 0;
//            tVc2.setText(tVc2.getText()+"\n"+"下标为["+ixh+"]["+jxh+"]的数据是地点不正确"+"起点是"+mRouteLine.getStarting().getTitle()+"终点是"+mRouteLine.getTerminal().getTitle());

//            Toast.makeText(this, "起终点或途经点地址有岐义,通过 result.getSuggestAddrInfo()接口获取建议查询信息", Toast.LENGTH_SHORT).show();
            return;
        }

        jxh++;
        if (result == null || result.error == SearchResult.ERRORNO.RESULT_NOT_FOUND) {
//            tVc2.setText(tVc2.getText()+"\n"+"下标为["+ixh+"]["+jxh+"]的数据是没有结果");
            ctimg = 0;
            Toast.makeText(this, "抱歉，未找到结果", Toast.LENGTH_SHORT).show();
            Toast.makeText(mContext, "2", Toast.LENGTH_SHORT).show();
            return;
        }
        if (result.error == SearchResult.ERRORNO.NO_ERROR) {
            Toast.makeText(mContext, "3", Toast.LENGTH_SHORT).show();

            if (result.getRouteLines().size() > 1) {
                mDrivingRouteResult = result;
                Toast.makeText(mContext, "4", Toast.LENGTH_SHORT).show();
//                mRouteLine = result.getRouteLines().get(0);.getDistance()
//                mDrivingRouteResult.getRouteLines().get(0);
//                result.getRouteLines()

                mRouteLine = mDrivingRouteResult.getRouteLines().get(0);
//                mDrivingRouteLine = mDrivingRouteResult.getRouteLines().get(0);
                /*                String[] acd= mapp.get("路线");*/


//                Toast.makeText(mContext, "从"+qdxx+"到"+zdxx+"距离为"+(lux[ixh][jxh]+""), Toast.LENGTH_SHORT).show();
//                Toast.makeText(this,mRouteLine.getDistance()+"", Toast.LENGTH_SHORT).show();
                Log.d("数量为", "结果数"+result.getRouteLines().size());

            } else if (result.getRouteLines().size() == 1) {
//                mRouteLine = result.getRouteLines().get(0);
//                RouteLine ac = new RouteLine();
                mRouteLine = result.getRouteLines().get(0);
                Toast.makeText(mContext, "5", Toast.LENGTH_SHORT).show();
//                mDrivingRouteLine = result.getRouteLines().get(0);
                /*                String[] acd= mapp.get("路线");*/
/*                int acc = mRouteLine.getDistance();  //获取路线长度
                String qdxx = mRouteLine.getStarting().getTitle();  //获取路线起点
                String zdxx = mRouteLine.getTerminal().getTitle();   //获取路线终点
//                if(acc != 101 && acc !=1){

                if(acc !=1 && acc !=101){
                    lux[ixh][jxh] = acc;
                    tVc2.setText(tVc2.getText()+"\n"+"下标为["+ixh+"]["+jxh+"]的数据是"+acc+"起点是"+mRouteLine.getStarting().getTitle()+"终点是"+mRouteLine.getTerminal().getTitle());
                }else{
                    tVc2.setText(tVc2.getText()+"\n"+"下标为["+ixh+"]["+jxh+"]的数据是0"+"起点是"+mRouteLine.getStarting().getTitle()+"终点是"+mRouteLine.getTerminal().getTitle());
                    lux[ixh][jxh] = 0;
                }*/

//                Toast.makeText(mContext, "从"+qdxx+"到"+zdxx+"距离为"+(lux[ixh][jxh]+""), Toast.LENGTH_SHORT).show();
//                Toast.makeText(this, mRouteLine.getDistance()+"", Toast.LENGTH_SHORT).show();
//                mDrivingRouteResult.getRouteLines().get(0);
            } else {
//                tVc2.setText(tVc2.getText()+"\n"+"下标为["+ixh+"]["+jxh+"]的数据是结果数小于0"+"起点是"+mRouteLine.getStarting().getTitle()+"终点是"+mRouteLine.getTerminal().getTitle());
                ctimg = 0;
                Log.d("route result", "结果数<0");
                Toast.makeText(mContext, "6", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        if(mRouteLine == null) {
            mRouteLine = result.getRouteLines().get(0);
//            mDrivingRouteLine = mDrivingRouteResult.getRouteLines().get(0);
//            tVc2.setText(tVc2.getText() + "\n" + "数据错误" + "起点是" + mRouteLine.getStarting().getTitle() + "终点是" + mRouteLine.getTerminal().getTitle());

        }else{
            String qdxx = mRouteLine.getStarting().getTitle();  //获取路线起点
            String zdxx = mRouteLine.getTerminal().getTitle();   //获取路线终点
            for(int i = 0;i < dizhiw.length; i++){
                if (qdxx.equals(dizhiw[i])){
                    for(int j = 0;j < dizhiw.length; j++) {
                        if (zdxx.equals(dizhiw[j])) {

                            int acc = mRouteLine.getDistance();  //获取路线长度
//                            mapLin.put((i+"")+","+(j+""),mRouteLine);
                            mapdrr.put((i+"")+","+(j+""),result);
                            if(acc !=1 && acc!=101){
//                                tVc2.setText(tVc2.getText()+"\n"+"下标为["+i+"]["+j+"]的数据是"+acc+"起点是"+mRouteLine.getStarting().getTitle()+"终点是"+mRouteLine.getTerminal().getTitle());
                                lux[i][j] = acc;
                            }else{
//                                tVc2.setText(tVc2.getText()+"\n"+"下标为["+i+"]["+j+"]的数据是0"+"起点是"+mRouteLine.getStarting().getTitle()+"终点是"+mRouteLine.getTerminal().getTitle());
                                lux[i][j] = 0;
                            }
                            break;
                        }
                    }
                    break;

                }
            }
        }



//        (lux.length-1 == ixh)  && (
//        Toast.makeText(mContext, "lux"+lux.length+"jxh"+jxh, Toast.LENGTH_SHORT).show();
        if (lux.length*lux.length == jxh) {
            String sf = "";
//        Toast.makeText(mContext, "lux"+lux.length+"jxh"+jxh, Toast.LENGTH_SHORT).show();
            for (int i = 0 ; i < lux.length ;i++){
                for (int j = 0 ; j < lux[i].length ; j++){
                    sf = sf + "," + lux[i][j]+"";

                }
            }
            Toast.makeText(mContext, sf, Toast.LENGTH_SHORT).show();
            Log.d("route result000************************************************", sf);
            Toast.makeText(mContext, "经过2", Toast.LENGTH_SHORT).show();
            String acct = "";
            Floyd flyd = new Floyd(lux);


//            inite.putExtra();
//            tVc2.setText(tVc2.getText() + "\n");
/*            for (int i = 0; i < lux.length; i++) {
                for (int j = 0; j < lux[i].length; j++) {
                    tVc2.setText(tVc2.getText() + (lux[i][j] + "")+"\t");
                    acct += lux[i][j];
                }
            }*/
            Log.d("选款", "循环提示 ");
            List<Integer> ljss = flyd.Sf(0).getLjs();
//            List<RouteLine> rouList = new ArrayList<>();
            List<DrivingRouteResult> drrList = new ArrayList<>();
            for (int i = 0; i<ljss.size()-1;i++){
                drrList.add(mapdrr.get(((ljss.get(i)+"")+","+(ljss.get(i+1)+""))));
//                drlList.add(mapdrl.get(((ljss.get(i)+"")+","+(ljss.get(i+1)+""))));
//                keySet(((ljss.get(i)+"")+(ljss.get(i+1)+""))
            }

            Intent intent = new Intent(ym3.this,ym3_11.class);
            xLDRR xld = new xLDRR(drrList);
            intent.putExtra("xld",xld);
//            intent.putExtra("xll",xll);
//            inte.putExter("122",xlh);
            startActivity(intent);
//            for(Integer lx:ljss) {
//                tVc2.setText(tVc2.getText() + (lx+"->"));
//                System.out.print(lx+"->");
//            }
//            tVc2.setText(tVc2.getText() + "\n");

        }
    }

    @Override
    public void onGetIndoorRouteResult(IndoorRouteResult indoorRouteResult) {

    }

    @Override
    public void onGetBikingRouteResult(BikingRouteResult bikingRouteResult) {

    }


    /**
     * 添加item
     */
    private void addViewItem(){
        if(llVipNumContainer.getChildCount()>20){
            Toast.makeText(mContext, "如需添加更多请充值VIP", Toast.LENGTH_SHORT).show();
        }else{

            View viewItem = LayoutInflater.from(this).inflate(R.layout.item_add_vip_num, llVipNumContainer,false);
            llVipNumContainer.addView(viewItem);
            sortViewItem();
            //添加并且排序之后将布局滚动到底部，方便用户继续添加
            scrollView.post(new Runnable() {
                @Override
                public void run() {
                    scrollView.fullScroll(ScrollView.FOCUS_DOWN);
                }
            });
            //监听输入框
            monitorEditTextChage();
        }


    }

    /**
     * 该方法主要用于排序（每个item中的序号），主要针对从中间删除item的情况
     */
    private void sortViewItem(){
        for (ctu = 0; ctu < llVipNumContainer.getChildCount(); ctu++) {
            final View viewItem = llVipNumContainer.getChildAt(ctu);
            TextView tvIndex = (TextView) viewItem.findViewById(R.id.tv_index);
            if(ctu==0){
                tvIndex.setText("起点");
            }else{
                tvIndex.setText("地点" + ctu + "");
            }
            LinearLayout llDelete = (LinearLayout) viewItem.findViewById(R.id.ll_delete);

            llDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(llVipNumContainer.getChildCount()<=2) {
                        Toast.makeText(ym3.this, "地点最少两个", Toast.LENGTH_SHORT).show();
                    }else{
                        for(int j = ctu; j<dizhi.length-1; j++){
                            dizhi[j] = dizhi[j+1];
                        }
                        llVipNumContainer.removeView(viewItem);
                        sortViewItem();

                    }

                }
            });
        }
        ctu = 0;
    }

    @Override
    public void onMapClick(LatLng latLng) {

    }

    @Override
    public void onMapPoiClick(MapPoi mapPoi) {

    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    /**
     * 监听当前位置
     */
    public class MyLocationListener extends BDAbstractLocationListener {

        @Override
        public void onReceiveLocation(BDLocation location) {
            //定位权限
            getPersimmions();

            //初始化地图及定位
//        initMap();
//            initLocation();
            //mapView 销毁后不在处理新接收的位置
            if (location == null ) {
                return;
            }
            Log.e(TAG, "当前“我”的位置：" + location.getAddrStr());
            Toast.makeText(mContext, "位置是"+location.getAddrStr(), Toast.LENGTH_SHORT).show();
            Toast.makeText(mContext, "位置是111", Toast.LENGTH_SHORT).show();
            if (location.getLocType() == BDLocation.TypeGpsLocation
                    || location.getLocType() == BDLocation.TypeNetWorkLocation) {
//                navigateTo(location);
                cityName = location.getCity();
                diqu.setText(cityName);
                Log.e(TAG, "当前定位城市：" + location.getCity());
                Toast.makeText(ym3.this, "cityName", Toast.LENGTH_SHORT).show();
            }
        }
    }
    /**
     * 初始化定位相关
     */
    private void initLocation() {
        // 声明LocationClient类
        mLocationClient = new LocationClient(getApplicationContext());
//        mLocationClient = new LocationClient(this);

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

        option.setLocationNotify(false);  ///   *******************************************
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
//        option.setScanSpan(1000);


        mLocationClient.setLocOption(option);
        // 注册监听函数
        mLocationClient.registerLocationListener(myListener);
        mLocationClient.start();
    }




    @TargetApi(23)
    private void getPersimmions() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            ArrayList<String> permissions = new ArrayList<String>();
            // 检查该权限是否已经获取
            int i = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
            // 权限是否已经 授权 GRANTED---授权  DINIED---拒绝
            if (i != PackageManager.PERMISSION_GRANTED) {
                           // 如果没有授予该权限，就去提示用户请求
                Toast.makeText(mContext, "定位", Toast.LENGTH_SHORT).show();
            }
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









    /*
     * 输入框监听器
     * */
    //输入框监听器
    private void monitorEditTextChage() {
        for (int i = 0; i < llVipNumContainer.getChildCount(); i++) {
            final View viewItem = llVipNumContainer.getChildAt(i);
            EditText et01 = viewItem.findViewById(R.id.et_vip_number);
            et01.addTextChangedListener(new TextWatcher() {

                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void afterTextChanged(Editable editable) {

                    keyword = editable.toString();

                    if (keyword.length() <= 0) {
                        //当清空文本后展示地图，隐藏搜索结果
                        showMapView();
                        return;
                    }
                    /* 使用建议搜索服务获取建议列表，结果在onSuggestionResult()中更新 */
                    /* 由于我们需要滑动地图展示周边poi，所以就不用建议搜索列表来搜索poi了，搜索时直接利用城市和输入的关键字进行城市内检索poi */
                    mSuggestionSearch.requestSuggestion((new SuggestionSearchOption())
                            .keyword(keyword)
                            .city(cityName));
                    searchCityPoiAddress();
                }


            });
        }

    }




    private List<String> getDataList() {
        List<String> result = new ArrayList<>();
        for (int i = 0; i < llVipNumContainer.getChildCount(); i++) {
            View itemView = llVipNumContainer.getChildAt(i);
            EditText et = (EditText) itemView.findViewById(R.id.et_vip_number);
            if (et != null) {
                String vipNum = et.getText().toString().trim();
                if (!TextUtils.isEmpty(vipNum)) {
                    result.add(vipNum);
                }
            }
        }
        return result;
    }
    /**
     * 展示搜索的布局
     */
    private void showSeachView() {
//        ll_mapView.setVisibility(View.GONE);
        btn_yes.setVisibility(View.GONE);
        ll_poiSearch.setVisibility(View.VISIBLE);
    }

    /**
     * 展示地图的布局
     */
    private void showMapView() {
//        ll_mapView.setVisibility(View.VISIBLE);
        btn_yes.setVisibility(View.VISIBLE);
        ll_poiSearch.setVisibility(View.GONE);
    }


    //-----------------------------------------反向地理解析，获取周边poi列表--------------------------------------------------

    /**
     * 反向地理解析,结果中含有poi信息，用于刚进入地图和移动地图时使用
     */
    private void initGeoCoder() {
        mGeoCoder = GeoCoder.newInstance();
        mGeoCoder.setOnGetGeoCodeResultListener(new OnGetGeoCoderResultListener() {
            @Override
            public void onGetGeoCodeResult(GeoCodeResult geoCodeResult) {

            }

            @Override
            public void onGetReverseGeoCodeResult(ReverseGeoCodeResult reverseGeoCodeResult) {
                if (poiInfoListForGeoCoder != null) {
                    poiInfoListForGeoCoder.clear();
                }
                if (reverseGeoCodeResult.error.equals(SearchResult.ERRORNO.NO_ERROR)) {
                    //获取城市
                    ReverseGeoCodeResult.AddressComponent addressComponent = reverseGeoCodeResult.getAddressDetail();
                    cityName = addressComponent.city;
                    diqu.setText(cityName);
                    //获取poi列表
                    if (reverseGeoCodeResult.getPoiList() != null) {
                        poiInfoListForGeoCoder = reverseGeoCodeResult.getPoiList();
                    }
                } else {
                    Toast.makeText(mContext, "该位置范围内无信息", Toast.LENGTH_SHORT);
                }
                initGeoCoderListView();
            }
        });
    }




    //-----------------------------------------建议搜索（sug检索）------------------------------------------------------------------
    private void initSuggestionSearch() {
        // 初始化建议搜索模块，注册建议搜索事件监听(sug搜索)
        mSuggestionSearch = SuggestionSearch.newInstance();
        mSuggestionSearch.setOnGetSuggestionResultListener(new OnGetSuggestionResultListener() {
            /**
             * 获取在线建议搜索结果，得到requestSuggestion返回的搜索结果
             * @param suggestionResult    Sug检索结果
             */
            @Override
            public void onGetSuggestionResult(SuggestionResult suggestionResult) {
                if (suggestionResult == null || suggestionResult.getAllSuggestions() == null) {
                    Toast.makeText(mContext, "未找到结果", Toast.LENGTH_LONG).show();
                    return;
                }

                List<SuggestionResult.SuggestionInfo> sugList = suggestionResult.getAllSuggestions();
                for (SuggestionResult.SuggestionInfo info : sugList) {
                    if (info.key != null) {
                        Log.e(TAG, "搜索结果：" + info.toString());
                        Log.e(TAG, "key：" + info.key);
                        DecimalFormat df = new DecimalFormat("######0");
                        //用当前所在位置算出距离
                        String distance = df.format(DistanceUtil.getDistance(currentLatLng, info.pt));
                        Log.e(TAG, "距离：" + distance);
                    }
                }

            }
        });
    }


    //--------------------------------------------------poi检索---------------------------------------------------------------------
    private void initPoiSearch() {
        // 初始化搜索模块，注册搜索事件监听
        mPoiSearch = PoiSearch.newInstance();
        mPoiSearch.setOnGetPoiSearchResultListener(this);
    }

    /**
     * 获取POI搜索结果，包括searchInCity，searchNearby，searchInBound返回的搜索结果
     *
     * @param poiResult Poi检索结果，包括城市检索，周边检索，区域检索
     */
    @Override
    public void onGetPoiResult(PoiResult poiResult) {
        if (poiInfoListForSearch != null) {
            poiInfoListForSearch.clear();
        }
        if (poiResult == null || poiResult.error == SearchResult.ERRORNO.RESULT_NOT_FOUND) {
            Toast.makeText(mContext, "未找到结果1", Toast.LENGTH_LONG).show();
            initPoiSearchListView();
            return;
        }

        if (poiResult.error == SearchResult.ERRORNO.NO_ERROR) {
            poiInfoListForSearch = poiResult.getAllPoi();
            showSeachView();
            initPoiSearchListView();
            return;
        }

        if (poiResult.error == SearchResult.ERRORNO.AMBIGUOUS_KEYWORD) {
            // 当输入关键字在本市没有找到，但在其他城市找到时，返回包含该关键字信息的城市列表
            String strInfo = "在";

            for (CityInfo cityInfo : poiResult.getSuggestCityList()) {
                strInfo += cityInfo.city;
                strInfo += ",";
            }

            strInfo += "找到结果";
            Toast.makeText(mContext, strInfo, Toast.LENGTH_LONG).show();
        }
    }

    /**
     * 获取POI详情搜索结果，得到searchPoiDetail返回的搜索结果
     * V5.2.0版本之后，该方法废弃，使用{@link #onGetPoiDetailResult(PoiDetailSearchResult)}代替
     *
     * @param poiDetailResult POI详情检索结果
     */
    @Override
    public void onGetPoiDetailResult(PoiDetailResult poiDetailResult) {
        if (poiDetailResult.error != SearchResult.ERRORNO.NO_ERROR) {
            Toast.makeText(mContext, "抱歉，未找到结果2", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(mContext,
                    poiDetailResult.getName() + ": " + poiDetailResult.getAddress(),
                    Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onGetPoiDetailResult(PoiDetailSearchResult poiDetailSearchResult) {
        if (poiDetailSearchResult.error != SearchResult.ERRORNO.NO_ERROR) {
            Toast.makeText(mContext, "抱歉，未找到结果3", Toast.LENGTH_SHORT).show();
        } else {
            List<PoiDetailInfo> poiDetailInfoList = poiDetailSearchResult.getPoiDetailInfoList();
            if (null == poiDetailInfoList || poiDetailInfoList.isEmpty()) {
                Toast.makeText(mContext, "抱歉，检索结果为空", Toast.LENGTH_SHORT).show();
                return;
            }

            for (int i = 0; i < poiDetailInfoList.size(); i++) {
                PoiDetailInfo poiDetailInfo = poiDetailInfoList.get(i);
                if (null != poiDetailInfo) {
                    Toast.makeText(mContext,
                            poiDetailInfo.getName() + ": " + poiDetailInfo.getAddress(),
                            Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    @Override
    public void onGetPoiIndoorResult(PoiIndoorResult poiIndoorResult) {

    }

    private void initGeoCoderListView() {
        adapter_searchAddress = new Adapter_SearchAddress(poiInfoListForGeoCoder, mContext, currentLatLng);
        lv_poiSearch.setAdapter(adapter_searchAddress);
        /*adapter_searchAddress = new Adapter_SearchAddress(poiInfoListForGeoCoder, mContext, currentLatLng);
        lv_poiSearch.setAdapter(adapter_searchAddress);*/
    }




    private void initPoiSearchListView() {
        adapter_searchAddress = new Adapter_SearchAddress(poiInfoListForSearch, mContext, currentLatLng);
        lv_poiSearch.setAdapter(adapter_searchAddress);
    }

    /**
     * 周边搜索
     */
    private void searchNearbyProcess(LatLng center) {
        //以定位点为中心，搜索半径以内的
        PoiNearbySearchOption nearbySearchOption = new PoiNearbySearchOption()
                .keyword(keyword)
                .sortType(PoiSortType.distance_from_near_to_far)
                .location(center)
                .radius(radius)
                .pageCapacity(pageSize)
                .pageNum(loadIndex);

        mPoiSearch.searchNearby(nearbySearchOption);
    }


    /**
     * 响应城市内搜索
     */
    public void searchCityPoiAddress() {
        mPoiSearch.searchInCity((new PoiCitySearchOption())
                .city(cityName)
                .keyword(keyword)//必填
                .pageCapacity(pageSize)
                .pageNum(loadIndex));//分页页码
    }



    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            //重写返回键
            if (keyword.trim().length() > 0) {//如果输入框还有字，就返回到地图界面并清空输入框
                showMapView();
//                et_keyword02.setText("");
            } else {
                finish();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onResume() {
        super.onResume();
//        mMapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
//        mMapView.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        mMapView.onDestroy();
        // 当不需要定位图层时关闭定位图层
//        mBaiduMap.setMyLocationEnabled(false);
        // 取消监听函数
        if (mLocationClient != null) {

            mLocationClient.unRegisterLocationListener(myListener);
        }
        mPoiSearch.destroy();
        mSuggestionSearch.destroy();
        mGeoCoder.destroy();
    }



}