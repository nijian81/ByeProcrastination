package com.jinlinkeji.byetuo.main;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Matrix;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.Transformation;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.overlayutil.PoiOverlay;
import com.baidu.mapapi.radar.RadarNearbyResult;
import com.baidu.mapapi.radar.RadarNearbySearchOption;
import com.baidu.mapapi.radar.RadarSearchError;
import com.baidu.mapapi.radar.RadarSearchListener;
import com.baidu.mapapi.radar.RadarSearchManager;
import com.baidu.mapapi.radar.RadarUploadInfo;
import com.baidu.mapapi.radar.RadarUploadInfoCallback;
import com.baidu.mapapi.search.core.CityInfo;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.poi.OnGetPoiSearchResultListener;
import com.baidu.mapapi.search.poi.PoiDetailResult;
import com.baidu.mapapi.search.poi.PoiDetailSearchOption;
import com.baidu.mapapi.search.poi.PoiNearbySearchOption;
import com.baidu.mapapi.search.poi.PoiResult;
import com.baidu.mapapi.search.poi.PoiSearch;
import com.baidu.mapapi.search.sug.OnGetSuggestionResultListener;
import com.baidu.mapapi.search.sug.SuggestionResult;
import com.baidu.mapapi.search.sug.SuggestionSearch;
import com.jinlinkeji.byetuo.R;
import com.jinlinkeji.byetuo.utils.App;
import com.jinlinkeji.byetuo.utils.RoundedImageView;
import com.jinlinkeji.byetuo.utils.StatusUtils;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class SearchResultActivity extends Activity implements View.OnClickListener, RadarUploadInfoCallback, RadarSearchListener, BDLocationListener, BaiduMap.OnMarkerClickListener, BaiduMap.OnMapClickListener {

    private LatLng pt = null;
    private Intent intent;
    // 定位相关
    LocationClient mLocClient;
    public MyLocationListenner myListener = new MyLocationListenner();
    private MyLocationConfiguration.LocationMode mCurrentMode;
    BitmapDescriptor mCurrentMarker;
    private MapView mMapView = null;
    private int uploadFlag = 1;
    // UI相关
    boolean isFirstLoc = true;// 是否首次定位
    private List<LatLng> pts;
    // 周边雷达相关
    RadarNearbyResult listResult = null;
    private double longitude, latitude;
    //POI相关
    private PoiSearch mPoiSearch = null;
    private SuggestionSearch mSuggestionSearch = null;
    private BaiduMap mBaiduMap = null;
    private Marker mMarkerA;
    /**
     * 搜索关键字输入窗口
     */
    private ImageView back;
    // 初始化全局 bitmap 信息，不用时及时 recycle
    BitmapDescriptor bdA = BitmapDescriptorFactory
            .fromResource(R.mipmap.icon_marka);
    private Double myLongitude,myLatitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_result);

        back = (ImageView)findViewById(R.id.back);
        back.setOnClickListener(this);
        SDKInitializer.initialize(getApplicationContext());
        App.getInstance().addActivity(this);
        //圆角图标
        RoundedImageView roundedImageView = new RoundedImageView(this);
        roundedImageView.setImageResource(R.mipmap.car_map_icon);
        roundedImageView.setBackgroundResource(R.mipmap.portrait_backgound);
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(this).build();
        ImageLoader.getInstance().init(config);
        ImageLoader.getInstance().displayImage("http://ac-mrq9g7oc.clouddn.com/DOER6FTVED3ADZB1Pey4VhuxkhTbOSGiaKF3g4wk", roundedImageView, StatusUtils.normalImageOptions);
        //定制图层
        //底部的圆环背景
        ImageView backImageView = new ImageView(this);
        backImageView.setImageResource(R.mipmap.portrait_backgound);
        //用ViewGroup去组合两个元素
        MyRelativeLayout relativeLayout = new MyRelativeLayout(this);
        RelativeLayout.LayoutParams rlp = new RelativeLayout.LayoutParams(100,100);
        rlp.addRule(RelativeLayout.CENTER_IN_PARENT);//addRule参数对应RelativeLayout XML布局的属性
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(200,200);
        layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);//addRule参数对应RelativeLayout XML布局的属性
        relativeLayout.addView(roundedImageView, rlp);
        relativeLayout.addView(backImageView, layoutParams);
        mCurrentMarker = BitmapDescriptorFactory
                .fromView(relativeLayout);
        mCurrentMode = MyLocationConfiguration.LocationMode.NORMAL;
        pts = new ArrayList<>();
        // 地图初始化
        mMapView = (MapView) findViewById(R.id.bmapView);
        mBaiduMap = mMapView.getMap();
        // 开启定位图层
        mBaiduMap.setMyLocationEnabled(true);
        mBaiduMap.setMyLocationConfigeration(new MyLocationConfiguration(mCurrentMode, true, mCurrentMarker));
        // 定位初始化
        mLocClient = new LocationClient(this);
        mLocClient.registerLocationListener(myListener);
        LocationClientOption option = new LocationClientOption();
        option.setOpenGps(true);// 打开gps
        option.setCoorType("bd09ll"); // 设置坐标类型
        option.setScanSpan(1000);
        mLocClient.setLocOption(option);
        mLocClient.start();
        //周边雷达设置监听
        RadarSearchManager.getInstance().addNearbyInfoListener(this);
        //周边雷达设置用户，id为空默认是设备标识
        RadarSearchManager.getInstance().setUserID(null);
        MapStatusUpdate msu = MapStatusUpdateFactory.zoomTo(14.0f);
        mBaiduMap.setMapStatus(msu);
        intent = getIntent();
        longitude = intent.getDoubleExtra("longitude", 0.0);
        latitude = intent.getDoubleExtra("latitude", 0.0);
        myLatitude = intent.getDoubleExtra("myLatitude",0.0);
        myLongitude = intent.getDoubleExtra("myLongitude",0.0);
        LatLng llA = new LatLng(latitude, longitude);
        OverlayOptions ooA = new MarkerOptions().position(llA).icon(bdA)
                .zIndex(9).draggable(true);
        mBaiduMap.addOverlay(ooA);

    }

    @Override
    protected void onResume() {
        super.onResume();
        //在activity执行onResume时执行mMapView. onResume ()，实现地图生命周期管理
        mMapView.onResume();
        //周边雷达设置监听
        RadarSearchManager.getInstance().addNearbyInfoListener(this);
        //周边雷达设置用户，id为空默认是设备标识
        RadarSearchManager.getInstance().setUserID(null);
    }

    @Override
    protected void onPause() {
        super.onPause();
        //在activity执行onPause时执行mMapView. onPause ()，实现地图生命周期管理
        mMapView.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //在activity执行onDestroy时执行mMapView.onDestroy()，实现地图生命周期管理
        mMapView.onDestroy();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back:
                intent = new Intent();
                intent.setAction("android.intent.action.Search");
                intent.putExtra("longitude",myLongitude);
                intent.putExtra("latitude",myLatitude);
                startActivity(intent);
                overridePendingTransition(R.anim.enter_from_right, R.anim.exit_to_left);
                break;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        this.finish();
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onReceiveLocation(BDLocation arg0) {

    }

    @Override
    public void onMapClick(LatLng latLng) {

    }

    @Override
    public boolean onMapPoiClick(MapPoi mapPoi) {
        return false;
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        return false;
    }

    @Override
    public void onGetNearbyInfoList(RadarNearbyResult result, RadarSearchError error) {
        if (error == RadarSearchError.RADAR_NO_ERROR) {
            // 获取成功
            listResult = result;
            // 处理数据
            parseResultToMap(listResult);
        } else {
            Toast.makeText(this, "查询周边失败", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onGetUploadState(RadarSearchError error) {
        // TODO Auto-generated method stub
        if (error == RadarSearchError.RADAR_NO_ERROR) {
            // 上传成功

        } else {
            // 上传失败
            Toast.makeText(SearchResultActivity.this, "单次上传位置失败", Toast.LENGTH_LONG)
                    .show();
        }
    }

    @Override
    public void onGetClearInfoState(RadarSearchError radarSearchError) {

    }

    @Override
    public RadarUploadInfo OnUploadInfoCallback() {
        return null;
    }


    private class MyPoiOverlay extends PoiOverlay {

        public MyPoiOverlay(BaiduMap baiduMap) {
            super(baiduMap);
        }

        @Override
        public boolean onPoiClick(int index) {
            super.onPoiClick(index);
            PoiInfo poi = getPoiResult().getAllPoi().get(index);
            // if (poi.hasCaterDetails) {
            mPoiSearch.searchPoiDetail((new PoiDetailSearchOption())
                    .poiUid(poi.uid));
            // }
            return true;
        }
    }

    /**
     * 定位SDK监听函数
     */
    public class MyLocationListenner implements BDLocationListener {

        @Override
        public void onReceiveLocation(BDLocation location) {
            // map view 销毁后不在处理新接收的位置
            if (location == null || mMapView == null)
                return;
            MyLocationData locData = new MyLocationData.Builder()
                    .accuracy(location.getRadius())
                            // 此处设置开发者获取到的方向信息，顺时针0-360
                    .direction(100).latitude(location.getLatitude())
                    .longitude(location.getLongitude()).build();
            mBaiduMap.setMyLocationData(locData);
            pt = new LatLng(location.getLatitude(), location.getLongitude());
            longitude = location.getLongitude();
            latitude = location.getLatitude();
            if (pt != null && uploadFlag == 1) {
                RadarUploadInfo info = new RadarUploadInfo();
                info.pt = pt;
                pts.add(0, pt);
                RadarSearchManager.getInstance().uploadInfoRequest(info);
                new Upload().execute();
                uploadFlag = 0;
                RadarNearbySearchOption option = new RadarNearbySearchOption()
                        .centerPt(pt).pageNum(0).radius(2000);
                RadarSearchManager.getInstance().nearbyInfoRequest(option);
            }
            if (isFirstLoc) {
                isFirstLoc = false;
                LatLng ll = new LatLng(location.getLatitude(),
                        location.getLongitude());
                MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(ll);
                mBaiduMap.animateMapStatus(u);
            }
        }

        public void onReceivePoi(BDLocation poiLocation) {

        }
    }

    // 在工作线程中上传当前位置信息 尼见 2014-02-28
    class Upload extends AsyncTask<Object, Object, Object> {

        @Override
        protected Objects doInBackground(Object... params) {
            return null;
        }
    }

    /**
     * 更新结果地图
     *
     * @param res
     */
    public void parseResultToMap(RadarNearbyResult res) {

        if (res != null && res.infoList != null && res.infoList.size() > 0) {
            for (int i = 0; i < res.infoList.size(); i++) {

            }
        }
    }

    public class MyRelativeLayout extends RelativeLayout {
        public MyRelativeLayout(Context context, AttributeSet attrs, int defStyle) {
            super(context, attrs, defStyle);
            init();
        }

        public MyRelativeLayout(Context context, AttributeSet attrs) {
            super(context, attrs);
            init();
        }

        public MyRelativeLayout(Context context) {
            super(context);
            init();
        }

        private void init() {
            setStaticTransformationsEnabled(true);
        }

        @Override
        protected boolean getChildStaticTransformation(View child, Transformation t) {
            t.setTransformationType(Transformation.TYPE_MATRIX);
            Matrix m = t.getMatrix();
            m.reset();
            m.postRotate(-100, child.getWidth() / 2.0f, child.getHeight() / 2.0f);
            return true;
        }
    }

}
