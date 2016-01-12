package com.jinlinkeji.byetuo.main;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.radar.RadarNearbyResult;
import com.baidu.mapapi.radar.RadarNearbySearchOption;
import com.baidu.mapapi.radar.RadarSearchError;
import com.baidu.mapapi.radar.RadarSearchListener;
import com.baidu.mapapi.radar.RadarSearchManager;
import com.baidu.mapapi.radar.RadarUploadInfo;
import com.baidu.mapapi.radar.RadarUploadInfoCallback;
import com.baidu.mapapi.search.core.CityInfo;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.poi.OnGetPoiSearchResultListener;
import com.baidu.mapapi.search.poi.PoiDetailResult;
import com.baidu.mapapi.search.poi.PoiNearbySearchOption;
import com.baidu.mapapi.search.poi.PoiResult;
import com.baidu.mapapi.search.poi.PoiSearch;
import com.baidu.mapapi.search.sug.OnGetSuggestionResultListener;
import com.baidu.mapapi.search.sug.SuggestionResult;
import com.baidu.mapapi.search.sug.SuggestionSearch;
import com.jinlinkeji.byetuo.R;
import com.jinlinkeji.byetuo.utils.PoiSearchResult;

import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends Activity implements View.OnClickListener , RadarUploadInfoCallback, RadarSearchListener, BDLocationListener, BaiduMap.OnMarkerClickListener, BaiduMap.OnMapClickListener,OnGetPoiSearchResultListener, OnGetSuggestionResultListener {

    private RelativeLayout  scene,building,busStation,mall,school,gym,subwayStation,restaurant;
    private List <PoiSearchResult>list;
    private Intent intent;
    private ImageView back;
    private EditText search;
    private LatLng pt = null;
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
    //POI相关
    private PoiSearch mPoiSearch = null;
    private SuggestionSearch mSuggestionSearch = null;
    private BaiduMap mBaiduMap = null;
    private PoiResult poiResult;
    /**
     * 搜索关键字输入窗口
     */
    private AutoCompleteTextView keyWorldsView = null;
    private ArrayAdapter<String> sugAdapter = null;
    private int load_Index = 0;
    private double longitude,latitude;
    private MyAdapter myAdapter;
    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search);

        intent = getIntent();
        longitude = intent.getDoubleExtra("longitude", 0.0);
        latitude = intent.getDoubleExtra("latitude", 0.0);
        back = (ImageView)findViewById(R.id.back);
        back.setOnClickListener(this);
        list = new ArrayList<>();
        myAdapter = new MyAdapter();
        myAdapter.setList(list);
        listView = (ListView) findViewById(R.id.listView);
        listView.setAdapter(myAdapter);
        scene = (RelativeLayout)findViewById(R.id.scene);
        scene.setOnClickListener(this);
        building = (RelativeLayout)findViewById(R.id.building);
        building.setOnClickListener(this);
        busStation = (RelativeLayout)findViewById(R.id.busStation);
        busStation.setOnClickListener(this);
        mall = (RelativeLayout)findViewById(R.id.mall);
        mall.setOnClickListener(this);
        school = (RelativeLayout)findViewById(R.id.school);
        school.setOnClickListener(this);
        gym = (RelativeLayout)findViewById(R.id.gym);
        gym.setOnClickListener(this);
        subwayStation = (RelativeLayout)findViewById(R.id.subwayStation);
        subwayStation.setOnClickListener(this);
        restaurant = (RelativeLayout)findViewById(R.id.restaurant);
        restaurant.setOnClickListener(this);
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
        //poi周边搜索
        // 初始化搜索模块，注册搜索事件监听
        mPoiSearch = PoiSearch.newInstance();
        mPoiSearch.setOnGetPoiSearchResultListener(this);
        mSuggestionSearch = SuggestionSearch.newInstance();
        mSuggestionSearch.setOnGetSuggestionResultListener(this);
        search = (EditText)findViewById(R.id.search);
        search.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    //执行搜索操作
                    //POI搜索
                    PoiNearbySearchOption poiNearbySearchOption = new PoiNearbySearchOption();
                    pt = new LatLng(latitude, longitude);
                    poiNearbySearchOption.location(pt);
                    poiNearbySearchOption.keyword(search.getText().toString());
                    poiNearbySearchOption.radius(20000);
                    mPoiSearch.searchNearby(poiNearbySearchOption);
                    return true;
                }
                return false;
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

    @Override
    public void onClick(View view) {
        PoiNearbySearchOption poiNearbySearchOption = new PoiNearbySearchOption();
        pt = new LatLng(latitude, longitude);
        poiNearbySearchOption.location(pt);
        poiNearbySearchOption.radius(20000);
        switch (view.getId()) {
            case R.id.back:
                intent = new Intent();
                intent.setAction("android.intent.action.MyLocation");
                startActivity(intent);
                overridePendingTransition(R.anim.enter_from_left, R.anim.exit_to_right);
                break;
            case R.id.scene:
                poiNearbySearchOption.keyword("景点");
                mPoiSearch.searchNearby(poiNearbySearchOption);
                break;
            case R.id.building:
                poiNearbySearchOption.keyword("办公建筑");
                mPoiSearch.searchNearby(poiNearbySearchOption);
                break;
            case R.id.busStation:
                poiNearbySearchOption.keyword("公交站");
                mPoiSearch.searchNearby(poiNearbySearchOption);
                break;
            case R.id.mall:
                poiNearbySearchOption.keyword("商场");
                mPoiSearch.searchNearby(poiNearbySearchOption);
                break;
            case R.id.school:
                poiNearbySearchOption.keyword("学校");
                mPoiSearch.searchNearby(poiNearbySearchOption);
                break;
            case R.id.gym:
                poiNearbySearchOption.keyword("健身场所");
                mPoiSearch.searchNearby(poiNearbySearchOption);
                break;
            case R.id.subwayStation:
                poiNearbySearchOption.keyword("地铁站");
                mPoiSearch.searchNearby(poiNearbySearchOption);
                break;
            case R.id.restaurant:
                poiNearbySearchOption.keyword("餐厅");
                mPoiSearch.searchNearby(poiNearbySearchOption);
                break;

        }
    }

    @Override
    public void onReceiveLocation(BDLocation bdLocation) {

    }

    @Override
    public void onGetPoiResult(PoiResult result) {
        if (result == null
                || result.error == SearchResult.ERRORNO.RESULT_NOT_FOUND) {
            Toast.makeText(SearchActivity.this, "未找到结果", Toast.LENGTH_LONG)
                    .show();
            return;
        }
        if (result.error == SearchResult.ERRORNO.NO_ERROR) {
            for(int i=0;i<result.getAllPoi().size();i++){
                list.add(new PoiSearchResult(result.getAllPoi().get(i).name,result.getAllPoi().get(i).address,result.getAllPoi().get(i).location.latitude,result.getAllPoi().get(i).location.longitude));
            }
            myAdapter.setList(list);
            myAdapter.notifyDataSetChanged();
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                    PoiSearchResult poiSearchResult = (PoiSearchResult) adapterView.getItemAtPosition(position);
                    Intent intent = new Intent();
                    intent.putExtra("latitude", poiSearchResult.getLatitude());
                    intent.putExtra("longitude", poiSearchResult.getLongitude());
                    intent.putExtra("myLatitude",latitude);
                    intent.putExtra("myLongitude",longitude);
                    intent.setAction("android.intent.action.SearchResult");
                    startActivity(intent);
                    overridePendingTransition(R.anim.enter_from_right, R.anim.exit_to_left);
                }
            });
            list = new ArrayList<>();
            return;
        }
        if (result.error == SearchResult.ERRORNO.AMBIGUOUS_KEYWORD) {

            // 当输入关键字在本市没有找到，但在其他城市找到时，返回包含该关键字信息的城市列表
            String strInfo = "在";
            for (CityInfo cityInfo : result.getSuggestCityList()) {
                strInfo += cityInfo.city;
                strInfo += ",";
            }
            strInfo += "找到结果";
            Toast.makeText(SearchActivity.this, strInfo, Toast.LENGTH_LONG)
                    .show();
        }
    }

    @Override
    public void onGetPoiDetailResult(PoiDetailResult poiDetailResult) {

    }

    @Override
    public void onGetSuggestionResult(SuggestionResult suggestionResult) {

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
    public void onGetNearbyInfoList(RadarNearbyResult radarNearbyResult, RadarSearchError radarSearchError) {

    }

    @Override
    public void onGetUploadState(RadarSearchError radarSearchError) {

    }

    @Override
    public void onGetClearInfoState(RadarSearchError radarSearchError) {

    }

    @Override
    public RadarUploadInfo OnUploadInfoCallback() {
        return null;
    }

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

            if (pt != null && uploadFlag == 1) {
                RadarUploadInfo info = new RadarUploadInfo();
                info.pt = pt;
                pts.add(0, pt);
                RadarSearchManager.getInstance().uploadInfoRequest(info);
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

    public class MyAdapter extends BaseAdapter {

        List<PoiSearchResult> list;
        private Context context;

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int index) {
            return list.get(index);
        }

        @Override
        public long getItemId(int index) {
            return index;
        }

        @Override
        public View getView(int position, View view, ViewGroup arg2) {

            view = LayoutInflater.from(SearchActivity.this).inflate(R.layout.poi_item, null);

            TextView name = (TextView) view.findViewById(R.id.name);
            TextView address = (TextView) view.findViewById(R.id.address);
            name.setText(this.list.get(position).getName());
            address.setText(this.list.get(position).getAddress());

            return view;
        }

        public void setContext(Context context) {
            this.context = context;
        }

        public void setList(List<PoiSearchResult> list) {
            this.list = list;
        }
    }

}
