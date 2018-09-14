/**
 * Copyright (C) 2016 Hyphenate Inc. All rights reserved.
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package onemessageui.view.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.tencent.map.geolocation.TencentLocation;
import com.tencent.map.geolocation.TencentLocationListener;
import com.tencent.map.geolocation.TencentLocationManager;
import com.tencent.map.geolocation.TencentLocationRequest;
import com.tencent.mapsdk.raster.model.BitmapDescriptorFactory;
import com.tencent.mapsdk.raster.model.Circle;
import com.tencent.mapsdk.raster.model.CircleOptions;
import com.tencent.mapsdk.raster.model.LatLng;
import com.tencent.mapsdk.raster.model.Marker;
import com.tencent.mapsdk.raster.model.MarkerOptions;
import com.tencent.tencentmap.mapsdk.map.MapView;
import com.tencent.tencentmap.mapsdk.map.TencentMap;

import oneapp.onechat.oneandroid.onewallet.util.StringUtils;
import oneapp.onechat.oneandroid.onewallet.util.ToastUtils;
import onewalletui.ui.BaseActivity;
import sdk.android.onechatui.R;

public class TencentMapActivity extends BaseActivity implements
        TencentLocationListener {

    private ImageView mBackIv;
    private TextView mTitleTv, mRightTv;

    private ImageButton btnShowLocation;

    private TencentLocationManager locationManager;
    private TencentLocationRequest locationRequest;
    private Marker myLocation;
    private Circle accuracy;
    private MapView mapView;
    private TencentMap tencentMap;

    double latitude;
    double longtitude;
    String address;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        mapView = (MapView) findViewById(R.id.mapview);
        mapView.onCreate(savedInstanceState);
        tencentMap = mapView.getMap();
        tencentMap.setZoom(18);

        mBackIv = (ImageView) findViewById(R.id.img_back);
        mBackIv.setVisibility(View.VISIBLE);
        mBackIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                back(view);
            }
        });
        mTitleTv = (TextView) findViewById(R.id.txt_title);
        mRightTv = (TextView) findViewById(R.id.txt_right);

        mRightTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendLocation(view);
            }
        });

        Intent intent = getIntent();
        double latitude = intent.getDoubleExtra("latitude", 0);
        double longtitude = intent.getDoubleExtra("longitude", 0);
        String address = intent.getStringExtra("address");
        if (!StringUtils.equalsNull(address)) {
            mTitleTv.setText(address);
            showMap(latitude, longtitude);
            mRightTv.setVisibility(View.GONE);
        } else {
            mRightTv.setVisibility(View.VISIBLE);
            mRightTv.setText(getString(R.string.send));
            mTitleTv.setText(getString(R.string.send_location));
            getMyLocation();
        }
    }

    protected void getMyLocation() {
        btnShowLocation = (ImageButton) findViewById(R.id.btn_show_location);
        btnShowLocation.setVisibility(View.VISIBLE);
        locationManager = TencentLocationManager.getInstance(this);
        locationRequest = TencentLocationRequest.create();
        locationManager.requestLocationUpdates(locationRequest, this);

        bindListener();
        if (Build.VERSION.SDK_INT >= 23) {
            String[] permissions = {
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.READ_PHONE_STATE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
            };

            if (checkSelfPermission(permissions[0]) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(permissions, 0);
            }
        }
    }

    @Override
    protected void onDestroy() {
        mapView.onDestroy();
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        mapView.onPause();
        super.onPause();
    }

    @Override
    protected void onResume() {
        mapView.onResume();
        super.onResume();
    }

    @Override
    protected void onStop() {
        mapView.onStop();
        super.onStop();
    }

    public void back(View v) {
        finish();
    }

    public void sendLocation(View view) {
        Intent intent = this.getIntent();
        if (!StringUtils.equalsNull(address)) {
            intent.putExtra("latitude", latitude);
            intent.putExtra("longitude", longtitude);
            intent.putExtra("address", address);
        }
        this.setResult(RESULT_OK, intent);
        finish();
        overridePendingTransition(R.anim.slide_in_from_left, R.anim.slide_out_to_right);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        //可在此继续其他操作。
    }

    protected void bindListener() {
        btnShowLocation.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                int error = locationManager.requestLocationUpdates(
                        locationRequest, TencentMapActivity.this);
                switch (error) {
                    case 0:
                        Log.e("location", "成功注册监听器");
                        break;
                    case 1:
                        Log.e("location", "设备缺少使用腾讯定位服务需要的基本条件");
                        break;
                    case 2:
                        Log.e("location", "manifest 中配置的 key 不正确");
                        break;
                    case 3:
                        Log.e("location", "自动加载libtencentloc.so失败");
                        break;

                    default:
                        break;
                }
            }
        });
    }

    @Override
    public void onLocationChanged(TencentLocation arg0, int arg1, String arg2) {
        // TODO Auto-generated method stub
        if (arg1 == TencentLocation.ERROR_OK) {
            LatLng latLng = new LatLng(arg0.getLatitude(), arg0.getLongitude());
            if (myLocation == null) {
                myLocation = tencentMap.addMarker(new MarkerOptions().
                        position(latLng).
                        icon(BitmapDescriptorFactory.fromResource(R.drawable.navigation)).
                        anchor(0.5f, 0.5f));
            }
            if (accuracy == null) {
                accuracy = tencentMap.addCircle(new CircleOptions().
                        center(latLng).
                        radius((double) arg0.getAccuracy()).
                        fillColor(0x440000ff).
                        strokeWidth(0f));
            }
            myLocation.setPosition(latLng);
            myLocation.setRotation(arg0.getBearing()); //仅当定位来源于gps有效，或者使用方向传感器
            accuracy.setCenter(latLng);
            accuracy.setRadius(arg0.getAccuracy());
            tencentMap.setCenter(latLng);

            latitude = arg0.getLatitude();
            longtitude = arg0.getLongitude();
            address = arg0.getAddress();

        } else {
            Log.e("location", "location failed:" + arg2);
        }
    }

    private void showMap(double latitude, double longitude) {
        LatLng latLng = new LatLng(latitude, longitude);
        if (myLocation == null) {
            myLocation = tencentMap.addMarker(new MarkerOptions().
                    position(latLng).
                    icon(BitmapDescriptorFactory.fromResource(R.drawable.red_location)).
                    anchor(0.5f, 0.5f));
        }
        myLocation.setPosition(latLng);
        tencentMap.setCenter(latLng);
    }

    @Override
    public void onStatusUpdate(String arg0, int arg1, String arg2) {
        // TODO Auto-generated method stub
        String desc = "";
        switch (arg1) {
            case STATUS_DENIED:
                desc = "权限被禁止";
                break;
            case STATUS_DISABLED:
                desc = "模块关闭";
                break;
            case STATUS_ENABLED:
                desc = "模块开启";
                break;
            case STATUS_GPS_AVAILABLE:
                desc = "GPS可用，代表GPS开关打开，且搜星定位成功";
                break;
            case STATUS_GPS_UNAVAILABLE:
                desc = "GPS不可用，可能 gps 权限被禁止或无法成功搜星";
                break;
            case STATUS_LOCATION_SWITCH_OFF:
                desc = "位置信息开关关闭，在android M系统中，此时禁止进行wifi扫描";
                ToastUtils.simpleToast("GPS不可用,请打开GPS开关");
                break;
            case STATUS_UNKNOWN:
                break;
        }
        Log.e("location", "location status:" + arg0 + ", " + arg2 + " " + desc);
    }
}
