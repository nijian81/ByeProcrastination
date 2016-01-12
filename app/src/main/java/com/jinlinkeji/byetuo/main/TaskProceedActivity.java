package com.jinlinkeji.byetuo.main;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.avos.avoscloud.PushService;
import com.avos.avoscloud.im.v2.AVIMClient;
import com.avos.avoscloud.im.v2.AVIMConversation;
import com.avos.avoscloud.im.v2.AVIMConversationQuery;
import com.avos.avoscloud.im.v2.AVIMException;
import com.avos.avoscloud.im.v2.Conversation;
import com.avos.avoscloud.im.v2.callback.AVIMClientCallback;
import com.avos.avoscloud.im.v2.callback.AVIMConversationCreatedCallback;
import com.avos.avoscloud.im.v2.callback.AVIMConversationQueryCallback;
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
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.radar.RadarNearbyResult;
import com.baidu.mapapi.radar.RadarNearbySearchOption;
import com.baidu.mapapi.radar.RadarSearchError;
import com.baidu.mapapi.radar.RadarSearchListener;
import com.baidu.mapapi.radar.RadarSearchManager;
import com.baidu.mapapi.radar.RadarUploadInfo;
import com.baidu.mapapi.radar.RadarUploadInfoCallback;
import com.jinlinkeji.byetuo.R;
import com.jinlinkeji.byetuo.utils.App;
import com.jinlinkeji.byetuo.utils.ConversationType;
import com.jinlinkeji.byetuo.utils.ProgressWheel;
import com.jinlinkeji.byetuo.utils.RoundedImageView;
import com.jinlinkeji.byetuo.utils.StatusUtils;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class TaskProceedActivity extends Activity implements View.OnClickListener, RadarUploadInfoCallback, RadarSearchListener, BDLocationListener, BaiduMap.OnMarkerClickListener, BaiduMap.OnMapClickListener {

    private int flag = 0;  //flag=0表示暂停，flag=1表示开始
    private TextView timerValue;
    private long startTime = 0L;
    private Handler customHandler = new Handler();
    long timeInMilliseconds = 0L;
    long timeSwapBuff = 0L;
    long updatedTime = 0L;
    private boolean uploadAuto = false;
    private LatLng pt = null;
    private Intent intent;
    private MapView mMapView = null;
    private ImageView switchKey;
    // 定位相关
    LocationClient mLocClient;
    BDLocation bdLocation = new BDLocation();
    public MyLocationListenner myListener = new MyLocationListenner();
    BaiduMap mBaiduMap;
    private int uploadFlag = 1;
    // UI相关
    boolean isFirstLoc = true;// 是否首次定位
    private ProgressDialog progressDialog;
    private List<LatLng> pts;
    // 周边雷达相关
    RadarNearbyResult listResult = null;
    private double longitude, latitude;
    //用于显示本人的自定义头像
    private BitmapDescriptor mCurrentMarker;
    private MyLocationConfiguration.LocationMode mCurrentMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.task_proceed);

        intent = getIntent();
        timeSwapBuff = intent.getLongExtra("timeSwapBuff", 0);
        switchKey = (ImageView) findViewById(R.id.switchKey);
        switchKey.setOnClickListener(this);
        startTime = SystemClock.uptimeMillis();
        customHandler.postDelayed(updateTimerThread, 0);
        timerValue = (TextView) findViewById(R.id.time);
        SDKInitializer.initialize(getApplicationContext());
        App.getInstance().addActivity(this);
        // 设置默认打开的 Activity
        PushService.setDefaultPushCallback(this, TaskProceedActivity.class);
        pts = new ArrayList<>();
        //获取地图控件引用
        mMapView = (MapView) findViewById(R.id.bmapView);
        // 地图初始化
        mMapView = (MapView) findViewById(R.id.bmapView);
        mBaiduMap = mMapView.getMap();
        // 开启定位图层
        mBaiduMap.setMyLocationEnabled(true);
        mBaiduMap.setOnMarkerClickListener(this);
        // 定位初始化
        mLocClient = new LocationClient(this);
        mLocClient.registerLocationListener(myListener);
        LocationClientOption option = new LocationClientOption();
        option.setOpenGps(true);// 打开gps
        option.setCoorType("bd09ll"); // 设置坐标类型
        // TODO
        //这里设置的是雷达搜索的范围，如果想增大搜索范围可以自己定义
        option.setScanSpan(2000);
        mLocClient.setLocOption(option);
        mLocClient.start();
        //周边雷达设置监听
        RadarSearchManager.getInstance().addNearbyInfoListener(this);
        //周边雷达设置用户，id为空默认是设备标识
        //TODO
        //这里的setUserID是作为周边雷达的唯一标志，可以用用户ID来唯一标志，这里我是用设置的UUID来设置的，你改的时候可以将当前用户的ID来设置这个参数。
        RadarSearchManager.getInstance().setUserID(App.getInstance().getDeviceId());
        //对IM进行初始化
        //TODO
        //这个ID和上面的id一样，可以自己定制
        App.setClientIdToPre(App.getInstance().deviceId);
        mCurrentMode = MyLocationConfiguration.LocationMode.NORMAL;
    }

    public void openClient(String selfId, final String otherId) {
        //TODO
        //这里的deviceID是得到一个唯一的会话实例，这个ID可以用当前用户的ID代替，我这里用的是设备的UUID来代替
        AVIMClient imClient = AVIMClient.getInstance(App.getInstance().deviceId);
        Log.e("aaa", "我在外面");
        imClient.open(new AVIMClientCallback() {
            @Override
            public void done(final AVIMClient avimClient, AVIMException e) {
                Log.e("aaa", "我进来了");
                if (e == null) {
                    fetchConversationWithClientIds(Arrays.asList(otherId), ConversationType.OneToOne, new AVIMConversationCreatedCallback() {
                        @Override
                        public void done(AVIMConversation avimConversation, AVIMException e) {
                            if (e == null) {
                                intent = new Intent();
                                intent.setAction("android.intent.action.Chat");
                                intent.putExtra("conversation_id", avimConversation.getConversationId());
                                intent.putExtra("timeSwapBuff", timeSwapBuff);
                                startActivity(intent);
                            }
                        }
                    });
                }
            }
        });
    }

    private void fetchConversationWithClientIds(List<String> clientIds, final ConversationType type, final
    AVIMConversationCreatedCallback
            callback) {
        final AVIMClient imClient = App.getIMClient();
        final List<String> queryClientIds = new ArrayList<String>();
        queryClientIds.addAll(clientIds);
        if (!clientIds.contains(imClient.getClientId())) {
            queryClientIds.add(imClient.getClientId());
        }
        AVIMConversationQuery query = imClient.getQuery();
        query.whereEqualTo(Conversation.ATTRIBUTE_MORE + ".type", type.getValue());
        query.whereContainsAll(Conversation.COLUMN_MEMBERS, queryClientIds);
        query.findInBackground(new AVIMConversationQueryCallback() {
            @Override
            public void done(List<AVIMConversation> list, AVIMException e) {
                if (e != null) {
                    callback.done(null, e);
                } else {
                    if (list == null || list.size() == 0) {
                        Map<String, Object> attributes = new HashMap<String, Object>();
                        attributes.put(ConversationType.KEY_ATTRIBUTE_TYPE, type.getValue());
                        imClient.createConversation(queryClientIds, attributes, callback);
                    } else {
                        callback.done(list.get(0), null);
                    }
                }
            }
        });
    }

    private Runnable updateTimerThread = new Runnable() {

        public void run() {
            timeInMilliseconds = SystemClock.uptimeMillis() - startTime;
            updatedTime = timeSwapBuff + timeInMilliseconds;
            int secs = (int) (updatedTime / 1000);
            int mins = secs / 60;
            int hour = mins / 60;
            secs = secs % 60;
            timerValue.setText("0" + hour + ":"
                    + String.format("%02d", mins) + ":"
                    + String.format("%02d", secs));
            customHandler.postDelayed(this, 0);
        }
    };

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
        // 退出时销毁定位
        mLocClient.stop();
        // 释放周边雷达相关
        RadarSearchManager.getInstance().removeNearbyInfoListener(this);
        RadarSearchManager.getInstance().clearUserInfo();
        RadarSearchManager.getInstance().destroy();
        // 释放地图
        mMapView.onDestroy();
        mBaiduMap = null;
        super.onDestroy();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.switchKey:
                if (flag == 0) {
                    timeSwapBuff += timeInMilliseconds;
                    customHandler.removeCallbacks(updateTimerThread);
                    flag = 1;
                    switchKey.setImageResource(R.mipmap.begin_icon);
                } else {
                    startTime = SystemClock.uptimeMillis();
                    customHandler.postDelayed(updateTimerThread, 0);
                    switchKey.setImageResource(R.mipmap.pause);
                    flag = 0;
                }
                break;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        finish();
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

        timeSwapBuff += timeInMilliseconds;
        customHandler.removeCallbacks(updateTimerThread);
        openClient(App.getInstance().deviceId, marker.getExtraInfo().getString("des"));
        overridePendingTransition(R.anim.enter_from_right, R.anim.exit_to_left);

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

        if (error == RadarSearchError.RADAR_NO_ERROR) {
            // 上传成功

        } else {
            // 上传失败
            Toast.makeText(TaskProceedActivity.this, "单次上传位置失败", Toast.LENGTH_LONG)
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
                    .direction(0).latitude(location.getLatitude())
                    .longitude(location.getLongitude()).build();
            //进度条
            ProgressWheel progressWheel = new ProgressWheel(TaskProceedActivity.this);
            progressWheel.setBarWidth(7);
            progressWheel.setBarColor(Color.rgb(239, 7, 7));
            progressWheel.setContourColor(Color.TRANSPARENT);
            progressWheel.setProgress(290);
            //圆角图标
            RoundedImageView roundedImageView = new RoundedImageView(TaskProceedActivity.this);
            roundedImageView.setImageResource(R.mipmap.car_map_icon);
            roundedImageView.setBackgroundResource(R.mipmap.portrait_backgound);
            ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(TaskProceedActivity.this).build();
            ImageLoader.getInstance().init(config);
            //TODO
            //这里的URI代表本人头像的服务器连接地址，到时候可以替换掉
            ImageLoader.getInstance().displayImage("http://ac-srqndt5a.clouddn.com/cUPrD9v28RjCZGQBfjrsJaUxDZyfNNGG5jhbbiL8", roundedImageView, StatusUtils.normalImageOptions);
            //用ViewGroup去组合两个元素
            RelativeLayout relativeLayout = new RelativeLayout(TaskProceedActivity.this);
            RelativeLayout.LayoutParams rlp = new RelativeLayout.LayoutParams(100, 100);
            rlp.addRule(RelativeLayout.CENTER_IN_PARENT);//addRule参数对应RelativeLayout XML布局的属性
            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(170, 170);
            layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);//addRule参数对应RelativeLayout XML布局的属性
            relativeLayout.addView(roundedImageView, rlp);
            relativeLayout.addView(progressWheel, layoutParams);

            // 修改为自定义marker
            mCurrentMarker = BitmapDescriptorFactory
                    .fromView(relativeLayout);
            mBaiduMap
                    .setMyLocationConfigeration(new MyLocationConfiguration(
                            mCurrentMode, true, mCurrentMarker));
            if (mBaiduMap != null) {
                mBaiduMap.setMyLocationData(locData);
            }
            pt = new LatLng(location.getLatitude(), location.getLongitude());
            longitude = location.getLongitude();
            latitude = location.getLatitude();
            if (pt != null && uploadFlag == 1) {
                //上传自己的位置信息
                RadarUploadInfo info = new RadarUploadInfo();
                info.pt = pt;
                info.comments = App.getInstance().deviceId;
                pts.add(0, pt);
                RadarSearchManager.getInstance().uploadInfoRequest(info);
                new Upload().execute();
                uploadFlag = 0;
                //发起检索周边的请求
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
        if (mBaiduMap != null) {
            mBaiduMap.clear();
        }
        if (res != null && res.infoList != null && res.infoList.size() > 0) {
            for (int i = 0; i < res.infoList.size(); i++) {
                //进度条
                ProgressWheel progressWheel = new ProgressWheel(this);
                progressWheel.setBarWidth(7);
                progressWheel.setBarColor(Color.rgb(239, 7, 7));
                progressWheel.setContourColor(Color.TRANSPARENT);
                progressWheel.setProgress(290);
                //圆角图标
                RoundedImageView roundedImageView = new RoundedImageView(this);
                roundedImageView.setImageResource(R.mipmap.car_map_icon);
                roundedImageView.setBackgroundResource(R.mipmap.portrait_backgound);
                ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(this).build();
                ImageLoader.getInstance().init(config);
                //TODO
                //这个URI代表雷达搜索周边结果的头像URI,res.infoList.get(i).comments这个参数代表的是周边人的用户ID，可以通过这个ID去查询其对应的头像URI
                ImageLoader.getInstance().displayImage("http://ac-srqndt5a.clouddn.com/cUPrD9v28RjCZGQBfjrsJaUxDZyfNNGG5jhbbiL8", roundedImageView, StatusUtils.normalImageOptions);
                //用ViewGroup去组合两个元素
                RelativeLayout relativeLayout = new RelativeLayout(this);

                RelativeLayout.LayoutParams rlp = new RelativeLayout.LayoutParams(100, 100);
                rlp.addRule(RelativeLayout.CENTER_IN_PARENT);//addRule参数对应RelativeLayout XML布局的属性

                RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(170, 170);
                layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);//addRule参数对应RelativeLayout XML布局的属性

                relativeLayout.addView(roundedImageView, rlp);
                relativeLayout.addView(progressWheel, layoutParams);
                //设置地图悬浮层
                BitmapDescriptor ff3 = BitmapDescriptorFactory
                        .fromView(relativeLayout);
                MarkerOptions option = new MarkerOptions().icon(ff3).position(
                        res.infoList.get(i).pt);
                Bundle des = new Bundle();
                if (res.infoList.get(i).comments == null
                        || res.infoList.get(i).comments.equals("")) {
                    //这里的des是周边雷达唯一标志用户的字段，这里用用户的用户名来标志
                    des.putString("des", App.getInstance().getDeviceId());
                } else {
                    des.putString("des", res.infoList.get(i).comments);
                }
                option.extraInfo(des);
                mBaiduMap.addOverlay(option);
            }
        }
    }

}
