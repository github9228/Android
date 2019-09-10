package com.example.device.activity;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.example.device.R;
import com.example.device.util.DateUtil;
import com.example.device.util.SwitchUtil;

public class LocationActivity extends AppCompatActivity {
    private static final String TAG = "LocationActivity";
    private TextView tv_location;
    private String mLocation = "";
    private LocationManager mLocationMgr;           //定位管理器对象
    private Criteria mCriteria = new Criteria();        //定位准则对象
    private Handler mHandler = new Handler();       //处理器
    private  boolean isLocationEnable = false;      //定位服务是否可用

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);
        tv_location = findViewById(R.id.tv_location);
        SwitchUtil.checkGpsIsOpen(this, "需要打开定位功能才能查看定位结果信息");
    }

    @Override
    protected void onResume() {
        super.onResume();
        mHandler.removeCallbacks(mRefresh);     //移除刷新任务
        initLocation();
        mHandler.postDelayed(mRefresh, 100);        //延迟启动
    }

    //初始化定位服务
    private void initLocation() {
        mLocationMgr = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        //设置定位精度
        mCriteria.setAccuracy(Criteria.ACCURACY_FINE);
        //设置是否需要海拔高度
        mCriteria.setAltitudeRequired(true);
        //设置是否需要方位信息
        mCriteria.setBearingRequired(true);
        //设置是否允许运营商收费
        mCriteria.setCostAllowed(true);
        //设置对电源的需求
        mCriteria.setPowerRequirement(Criteria.POWER_LOW);
        String bestProvider = mLocationMgr.getBestProvider(mCriteria, true);
        if (mLocationMgr.isProviderEnabled(bestProvider)) {     //定位提供者当前可用
            tv_location.setText("正在获取" + bestProvider + "定位对象");
            mLocation = String.format("定位类型=%s",bestProvider);
            beginLocation(bestProvider);
            isLocationEnable = true;
        } else {        //定位提供者暂不可用
            tv_location.setText("\n" + bestProvider + "定位不可用");
            isLocationEnable = false;
        }
    }

    //设置定位结果文本
    private void setLocationText(Location location) {
        if (location != null) {
            String desc = String.format("%s\n定位对象信息如下：" +
                    "\n\t其中时间：%s" + "\n\t其中经度：%f，纬度：%f" +
                    "\n\t其中高度：%d米，精度：%d米", mLocation, DateUtil.getNowDateTimeFormat(),
                    location.getLongitude(), location.getLatitude(),
                    Math.round(location.getAltitude()), Math.round(location.getAccuracy()));
            Log.d(TAG, "setLocationText: desc");
            tv_location.setText(desc);
        } else {
            tv_location.setText(mLocation + "\n暂未获取到定位对象");
        }
    }

    //k开始定位
    private void beginLocation(String method) {
        //检查当前设备是否已经开启了定位功能
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "请授予定位权限并开启定位功能", Toast.LENGTH_SHORT).show();
            return;
        }
        //设置定位管理器的位置变更监听器
        mLocationMgr.requestLocationUpdates(method, 300, 0 , mLocationListener);
        //获取最后一次定位成功的位置信息
        Location location = mLocationMgr.getLastKnownLocation(method);
        setLocationText(location);
    }

    //定义一个位置变更监听器
    private LocationListener mLocationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            setLocationText(location);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    };

    //定义一个舒心任务，若无法定位则每秒就尝试刷新
    private Runnable mRefresh = new Runnable() {
        @Override
        public void run() {
            if (!isLocationEnable) {
                initLocation();
                mHandler.postDelayed(this , 100);
            }
        }
    };

    @Override
    protected void onDestroy() {
        if (mLocationMgr != null) {
            //移除定位管理器的位置变更监听器
            mLocationMgr.removeUpdates(mLocationListener);
        }
        super.onDestroy();
    }
}
