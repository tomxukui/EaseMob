package com.hyphenate.easeui.ui;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationConfiguration.LocationMode;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.CoordinateConverter;
import com.hyphenate.easeui.EaseConstant;
import com.hyphenate.easeui.R;
import com.hyphenate.easeui.utils.EaseToastUtil;
import com.hyphenate.easeui.widget.EaseToolbar;

public class EaseBaiduMapActivity extends EaseBaseActivity {

    private EaseToolbar toolbar;
    private MapView v_map;
    private ProgressDialog progressDialog;

    private double mLatitude;
    private double mLongtitude;
    private BDLocation mLastLocation;

    private BaiduSDKReceiver mBaiduReceiver;
    private LocationClient mLocClient;
    private OnLocationListenner mOnLocationListenner;

    @Override
    protected int getLayoutResID() {
        return R.layout.ease_activity_baidumap;
    }

    @Override
    protected void initData() {
        super.initData();
        mLatitude = getIntent().getDoubleExtra(EaseConstant.EXTRA_LATITUDE, 0);
        mLongtitude = getIntent().getDoubleExtra(EaseConstant.EXTRA_LONGITUDE, 0);

        mBaiduReceiver = new BaiduSDKReceiver();
        mOnLocationListenner = new OnLocationListenner();
    }

    @Override
    protected void initView() {
        super.initView();
        toolbar = findViewById(R.id.toolbar);
        v_map = findViewById(R.id.v_map);
    }

    @Override
    protected void initActionBar() {
        setSupportActionBar(toolbar);
        super.initActionBar();
    }

    @Override
    protected void setView() {
        super.setView();
        v_map.setLongClickable(true);
        v_map.getMap().setMapStatus(MapStatusUpdateFactory.zoomTo(15.0f));

        IntentFilter iFilter = new IntentFilter();
        iFilter.addAction(SDKInitializer.SDK_BROADTCAST_ACTION_STRING_PERMISSION_CHECK_ERROR);
        iFilter.addAction(SDKInitializer.SDK_BROADCAST_ACTION_STRING_NETWORK_ERROR);
        registerReceiver(mBaiduReceiver, iFilter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.ease_menu_baidu_map, menu);
        MenuItem menuItem_location = menu.findItem(R.id.ease_action_location);
        menuItem_location.setVisible(mLatitude == 0);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.ease_action_location) {
            if (mLastLocation != null) {
                Intent intent = new Intent();
                intent.putExtra(EaseConstant.EXTRA_LATITUDE, mLastLocation.getLatitude());
                intent.putExtra(EaseConstant.EXTRA_LONGITUDE, mLastLocation.getLongitude());
                intent.putExtra(EaseConstant.EXTRA_ADDRESS, mLastLocation.getAddrStr());
                setResult(RESULT_OK, intent);
                finish();
                overridePendingTransition(R.anim.slide_in_from_left, R.anim.slide_out_to_right);
            }

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle arg0) {
        SDKInitializer.initialize(getApplicationContext());
        super.onCreate(arg0);

        if (mLatitude == 0) {
            v_map.getMap().setMyLocationConfiguration(new MyLocationConfiguration(LocationMode.NORMAL, true, null));
            showMapWithLocationClient();

        } else {
            showMap(mLatitude, mLongtitude);
        }
    }

    private void showMap(double latitude, double longtitude) {
        LatLng llA = new LatLng(latitude, longtitude);
        CoordinateConverter converter = new CoordinateConverter();
        converter.coord(llA);
        converter.from(CoordinateConverter.CoordType.COMMON);
        LatLng convertLatLng = converter.convert();
        OverlayOptions ooA = new MarkerOptions().position(convertLatLng).icon(BitmapDescriptorFactory.fromResource(R.drawable.ease_icon_marka)).zIndex(4).draggable(true);
        v_map.getMap().addOverlay(ooA);
        MapStatusUpdate u = MapStatusUpdateFactory.newLatLngZoom(convertLatLng, 17.0f);
        v_map.getMap().animateMapStatus(u);
    }

    private void showMapWithLocationClient() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setMessage(getResources().getString(R.string.Making_sure_your_location));

        progressDialog.setOnCancelListener(arg0 -> {
            if (progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
            finish();
        });

        progressDialog.show();

        mLocClient = new LocationClient(this);
        mLocClient.registerLocationListener(mOnLocationListenner);

        LocationClientOption option = new LocationClientOption();
        option.setOpenGps(true);
        option.setCoorType("gcj02");
        option.setScanSpan(30000);
        option.setIsNeedAddress(true);
        mLocClient.setLocOption(option);
    }

    @Override
    protected void onPause() {
        v_map.onPause();
        if (mLocClient != null) {
            mLocClient.stop();
        }
        super.onPause();
        mLastLocation = null;
    }

    @Override
    protected void onResume() {
        v_map.onResume();
        if (mLocClient != null) {
            mLocClient.start();
        }
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        if (mLocClient != null) {
            mLocClient.stop();
        }
        v_map.onDestroy();
        unregisterReceiver(mBaiduReceiver);
        super.onDestroy();
    }

    private class BaiduSDKReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if (action != null) {
                switch (action) {

                    case SDKInitializer.SDK_BROADTCAST_ACTION_STRING_PERMISSION_CHECK_ERROR: {
                        EaseToastUtil.show(R.string.please_check);
                    }
                    break;

                    case SDKInitializer.SDK_BROADCAST_ACTION_STRING_NETWORK_ERROR: {
                        EaseToastUtil.show(R.string.Network_error);
                    }
                    break;

                    default:
                        break;

                }
            }
        }

    }

    private class OnLocationListenner extends BDAbstractLocationListener {

        @Override
        public void onReceiveLocation(BDLocation location) {
            if (location == null) {
                return;
            }

            if (progressDialog != null) {
                progressDialog.dismiss();
            }

            if (mLastLocation != null) {
                if (mLastLocation.getLatitude() == location.getLatitude() && mLastLocation.getLongitude() == location.getLongitude()) {
                    return;
                }
            }

            mLastLocation = location;
            v_map.getMap().clear();

            LatLng llA = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
            CoordinateConverter converter = new CoordinateConverter();
            converter.coord(llA);
            converter.from(CoordinateConverter.CoordType.COMMON);
            LatLng convertLatLng = converter.convert();
            OverlayOptions ooA = new MarkerOptions().position(convertLatLng).icon(BitmapDescriptorFactory
                    .fromResource(R.drawable.ease_icon_marka))
                    .zIndex(4).draggable(true);
            v_map.getMap().addOverlay(ooA);
            MapStatusUpdate u = MapStatusUpdateFactory.newLatLngZoom(convertLatLng, 17.0f);
            v_map.getMap().animateMapStatus(u);
        }

    }

    public static Intent buildIntent(Context context) {
        return new Intent(context, EaseBaiduMapActivity.class);
    }

    public static Intent buildIntent(Context context, double latitude, double longtitude) {
        Intent intent = new Intent(context, EaseBaiduMapActivity.class);
        intent.putExtra(EaseConstant.EXTRA_LATITUDE, latitude);
        intent.putExtra(EaseConstant.EXTRA_LONGITUDE, longtitude);
        return intent;
    }

}