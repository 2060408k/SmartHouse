package com.example.pbkou.smarthouse;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.example.pbkou.smarthouse.Database.LoginActivity;
import com.example.pbkou.smarthouse.HouseSettings.House_Settings;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

/**
 * Created by pbkou on 08/03/2017.
 */

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {
    private MapView mapView;
    private double lat=0;
    private double lon=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        //set the toolbar
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        String[] arr = getIntent().getExtras().getString("USER_LOCATION").split("_");
        if (arr.length>=2){
            lat = Double.parseDouble(arr[0]);
            lon = Double.parseDouble(arr[1]);
        }

        mapView = (MapView)findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(new OnMapReadyCallback() {

            @Override
            public void onMapReady(GoogleMap googleMap) {
                LatLng coordinates = new LatLng(lat, lon);
                googleMap.addMarker(new MarkerOptions().position(coordinates));
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(coordinates, 15));
                mapView.onResume();
            }
        });



    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        LatLng TutorialsPoint = new LatLng(21 , 57);//lat lon
        if (lat != 0 && lon !=0) TutorialsPoint = new LatLng(lat , lon);//lat lon
        Marker TP = googleMap.addMarker(new MarkerOptions()
                .position(TutorialsPoint).title("TutorialsPoint"));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.action_bar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch(id) {
            case R.id.house_settings:
                startActivity(new Intent(this,House_Settings.class));
                break;
            case R.id.activity_notifications:
                startActivity(new Intent(this,notifications.class));
                break;
            case R.id.add_conv_add:
                startActivity(new Intent(this,AddConversation.class));
                break;
            case R.id.activitys:
                startActivity(new Intent(this,MainActivity.class));
                break;
            case R.id.activity_show_tasks:
                startActivity(new Intent(this,Tasks.class));
                break;
            case R.id.activity_room_deciding:
                startActivity(new Intent(this,RoomDecidingActivity.class));
                break;
            case R.id.change_activity:
                startActivity(new Intent(this,LoginActivity.class));
                break;
            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }
}
