package com.learn.notecatcam;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.learn.notecatcam.constant.StorageName;
import com.learn.notecatcam.constant.StorageVariable;
import com.learn.notecatcam.popup.Device_popup;
import com.learn.notecatcam.popup.SettingPopup;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private int PERMISSION_CODE = 101;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_main);
//
//        List<DashboardItem> items= new ArrayList<>();
//        items.add(new DashboardItem("Real Note Cam",R.drawable.ic_camera));
//        items.add(new DashboardItem("Note Cam From Device",R.drawable.ic_camera));
//        RecyclerView recyclerView = findViewById(R.id.recycle_view);
//        recyclerView.setLayoutManager(new GridLayoutManager(this,2));
//        recyclerView.setAdapter(new MyAdapter(getApplicationContext(),items));


        getPermission();
        /* Store the Location and Notes */





        CardView realTimeNoteCam = findViewById(R.id.real_note_cam);
        CardView noteCamFromPhotos = findViewById(R.id.device_note_cam);
        CardView settingPopup = findViewById(R.id.setting_popup);

        realTimeNoteCam.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, NoteCam.class));

        });

        noteCamFromPhotos.setOnClickListener(v -> {
             new Device_popup().showDialog(this);

//            startActivity(new Intent(MainActivity.this, DeviceActivity.class));
        });

        settingPopup.setOnClickListener(v->{
            new SettingPopup().showDialog(this);
        });


    }

    private void getPermission() {
        String[] permissions = {android.Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.ACCESS_FINE_LOCATION};

        for (String permission : permissions) {
            if (checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(permissions, PERMISSION_CODE);
                Log.e("getPermission",permission);
            }
        }


    }




    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        boolean allChecked = true;
        if (requestCode == PERMISSION_CODE) {
            for(int grantResult: grantResults){
            if (grantResult != PackageManager.PERMISSION_GRANTED)
                Log.e("Permission failed", String.valueOf(grantResult));

            }

        }

    }
}