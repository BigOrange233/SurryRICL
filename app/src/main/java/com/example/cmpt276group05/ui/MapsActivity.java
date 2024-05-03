package com.example.cmpt276group05.ui;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.cmpt276group05.R;
import com.example.cmpt276group05.adapter.CustomInfoWindowAdapter;
import com.example.cmpt276group05.callback.DownloadListener;
import com.example.cmpt276group05.callback.ParseFinishListener;
import com.example.cmpt276group05.callback.SearchListener;
import com.example.cmpt276group05.constant.BusinessConstant;
import com.example.cmpt276group05.filter.TextFilter;
import com.example.cmpt276group05.model.FavManager;
import com.example.cmpt276group05.model.FilterDto;
import com.example.cmpt276group05.model.Inspection;
import com.example.cmpt276group05.model.InspectionEntry;
import com.example.cmpt276group05.model.InspectionManager;
import com.example.cmpt276group05.model.Restaurant;
import com.example.cmpt276group05.model.RestaurantDto;
import com.example.cmpt276group05.model.RestaurantEntry;
import com.example.cmpt276group05.model.RestaurantManager;
import com.example.cmpt276group05.net.ApiService;
import com.example.cmpt276group05.net.RetrofitManager;
import com.example.cmpt276group05.utils.FileUtils;
import com.example.cmpt276group05.utils.SPUtils;
import com.example.cmpt276group05.widget.BaseDialog;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.gson.Gson;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Array;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Interceptor;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

/*
 * UI for mapsActivity
 */
//show update dialog
//show downloading dialog
//show downloading cancel dialog
//get json data from API
//get CSV files by stream
//add search and filter feature

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private List<Marker> markers = new ArrayList<>();
    private RestaurantManager restaurantManager;
    private InspectionManager inspectionManager;

    // Src: CodingWithMitch Tutorial - https://youtu.be/Vt6H9TOmsuo
    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COURSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;
    private static final float DEFAULT_ZOOM = 12.5f;

    //vars
    private Location lastLocation = null;
    private Boolean mLocationPermissionsGranted = false;
    private GoogleMap mMap;
    private FusedLocationProviderClient mLocation;
    private BaseDialog loadDialog, updateDialog,cancelDialog,inspectionDialog;
    private Call<ResponseBody> inspectionCall,restaurantCall;
    public static final String TAG = MapsActivity.class.getName();

    private List<RestaurantDto> dataList = new ArrayList<>();
    private TextFilter textFilter;
    private CheckBox favBtn;
    private EditText editText;
    private Spinner spinner;
    private SearchView searchView;
    public static final int QUEST_CODE = 0x0001;
    FilterDto filterDto = new FilterDto();

    @Override
    public void onBackPressed() {
        FavManager.getInstance().saveFavIntoSp(MapsActivity.this);
        finishAffinity();
    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps_1);
        Toolbar toolbar = findViewById(R.id.map_toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionbar1 = getSupportActionBar();
        actionbar1.setTitle(R.string.map_view_of_restaurants);
        FavManager.getInstance().init(MapsActivity.this);

        restaurantManager = RestaurantManager.getInstance(getApplicationContext());
        inspectionManager = InspectionManager.getInstance(getApplicationContext());

        getLocationPermission();

        initData(false);
        initView();
        showUpdateDialog();
        initEvent();

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        Toast.makeText(MapsActivity.this, getString(R.string.map_is_ready), Toast.LENGTH_SHORT).show();
        if (mLocationPermissionsGranted) {
            getGPSLocation();

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                            != PackageManager.PERMISSION_GRANTED) {

                return;
            }
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(true);
            mMap.getUiSettings().setZoomControlsEnabled(true);

//            populateMarkers();

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        mLocationPermissionsGranted = false;

        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0) {
                for (int grantResult : grantResults) {
                    if (grantResult != PackageManager.PERMISSION_GRANTED) {
                        mLocationPermissionsGranted = false;
                        return;
                    }
                }
                mLocationPermissionsGranted = true;
                //initialize our map
                initMap();
            }
        }
    }

    //init dialog
    private void initView(){
        loadDialog = new BaseDialog(this, R.layout.dialog_loading);
        updateDialog = new BaseDialog(this, R.layout.dialog_confirm_update);
        cancelDialog = new BaseDialog(this,R.layout.dialog_cancel);
        inspectionDialog = new BaseDialog(this,R.layout.dialog_inspection);

        searchView = findViewById(R.id.sv_search);
        favBtn = findViewById(R.id.cb_fav);
        editText = findViewById(R.id.et_number);
        spinner = findViewById(R.id.sp_level);
    }
    //init restaurant & inspection data
    private void initData(boolean force){
        textFilter = new TextFilter(dataList, new SearchListener<RestaurantDto>() {
            @Override
            public void onFinish(List<RestaurantDto> dataList) {
                mMap.clear();
                markers.clear();
                for(RestaurantDto restaurant : dataList){
                    String name = restaurant.getName();
                    String address = restaurant.getAddress();

                    double latitude = restaurant.getLatitude();
                    double longitude = restaurant.getLongitude();
                    mMap.setInfoWindowAdapter(new CustomInfoWindowAdapter(MapsActivity.this));

                    String snippet;
                    Marker marker;

                    try {
                        String hazardLevelMostRecent = inspectionManager
                                .getMostRecentInspection(restaurant.getTrackingNumber())
                                .getHazardRating();
                        snippet = address + "\n" + getString(R.string.hazard_level_of_most_recent_inspection) + hazardLevelMostRecent.toUpperCase();
                        int resourceID = 0;
                        switch (hazardLevelMostRecent) {
                            case "Low":
                                resourceID = R.drawable.low_bmp;
                                break;
                            case "Moderate":
                                resourceID = R.drawable.mid_bmp;
                                break;
                            case "High":
                                resourceID = R.drawable.high_bmp;
                                break;
                        }

                        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), resourceID);
                        BitmapDescriptor bitmapDescriptor = BitmapDescriptorFactory.fromBitmap(bitmap);
                        marker = mMap.addMarker(new MarkerOptions()
                                .position(new LatLng(latitude,longitude))
                                .title(name)
                                .snippet(snippet)
                                .icon(bitmapDescriptor));
                        markers.add(marker);
                        // Null occurs when there is no inspection available - use a question mark icon instead of custom icon
                    } catch (NullPointerException e) {
                        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.question_bmp);
                        BitmapDescriptor bitmapDescriptor = BitmapDescriptorFactory.fromBitmap(bitmap);
                        snippet = address + " - " + getString(R.string.no_inspection_found);
                        marker = mMap.addMarker(new MarkerOptions()
                                .position(new LatLng(latitude,longitude))
                                .title(name)
                                .snippet(snippet)
                                .icon(bitmapDescriptor));
                        markers.add(marker);
                    }

                    String trackingNumber = restaurant.getTrackingNumber();
                    marker.setTag(trackingNumber);
                    mMap.setOnInfoWindowClickListener(marker1 -> {
                        Intent intent = new Intent(MapsActivity.this, InspectionList.class);
                        String trackingNumber1 = marker1.getTag().toString();
                        intent.putExtra("Tracking_Number", trackingNumber1);
                        startActivity(intent);
                    });
                }
            }
        });
        inspectionManager = InspectionManager.getInstance(getApplicationContext());
        restaurantManager = RestaurantManager.getInstance(getApplicationContext());
        if(force){
            inspectionManager.setInited(false);
            restaurantManager.setInited(false);
        }

        inspectionManager.initData(null);
        restaurantManager.initData(new ParseFinishListener() {
            @Override
            public void onFinish() {
                while(!inspectionManager.isInited()){
                    try{
                        Thread.sleep(1000);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(mMap!=null){
                            inspectionDialog.show();
                            inspectionDialog.findViewById(R.id.btn_ok).setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    inspectionDialog.dismiss();
                                }
                            });
                            populateMarkers();
                        }
                    }
                });
            }
        });
    }

    private void initEvent(){
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                if(!TextUtils.isEmpty(editText.getText().toString())){
                    filterDto.setCriticalNumber(editText.getText().toString());
                }
                if(favBtn.isChecked()){
                    filterDto.setFavourite(true);
                }else{
                    filterDto.setFavourite(false);
                }

                if(!TextUtils.isEmpty(spinner.getSelectedItem().toString())){
                    filterDto.setHazardLevel(spinner.getSelectedItem().toString());
                }

                filterDto.setName(s);
                if(textFilter!=null){
                    textFilter.filter(new Gson().toJson(filterDto));
                }
                return false;
            }
        });

        favBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!TextUtils.isEmpty(editText.getText().toString())){
                    filterDto.setCriticalNumber(editText.getText().toString());
                }
                if(favBtn.isChecked()){
                    filterDto.setFavourite(true);
                }else{
                    filterDto.setFavourite(false);
                }

                if(!TextUtils.isEmpty(spinner.getSelectedItem().toString())){
                    filterDto.setHazardLevel(spinner.getSelectedItem().toString());
                }


                int id = searchView.getContext().getResources().getIdentifier("android:id/search_src_text", null, null);
                EditText  textView = (EditText ) searchView.findViewById(id);
                filterDto.setName(textView.getText().toString());
                if(textFilter!=null){
                    textFilter.filter(new Gson().toJson(filterDto));
                }
            }
        });

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if(!TextUtils.isEmpty(editText.getText().toString())){
                    filterDto.setCriticalNumber(editText.getText().toString());
                }
                if(favBtn.isChecked()){
                    filterDto.setFavourite(true);
                }else{
                    filterDto.setFavourite(false);
                }

                if(!TextUtils.isEmpty(spinner.getSelectedItem().toString())){
                    filterDto.setHazardLevel(spinner.getSelectedItem().toString());
                }


                int id = searchView.getContext().getResources().getIdentifier("android:id/search_src_text", null, null);
                EditText  textView = (EditText ) searchView.findViewById(id);
                filterDto.setName(textView.getText().toString());
                if(textFilter!=null){
                    textFilter.filter(new Gson().toJson(filterDto));
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_map, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.go_to_list) {
            Intent intent = new Intent(this, HomeActivity.class);
            if(!TextUtils.isEmpty(editText.getText().toString())){
                filterDto.setCriticalNumber(editText.getText().toString());
            }
            if(favBtn.isChecked()){
                filterDto.setFavourite(true);
            }

            if(!TextUtils.isEmpty(spinner.getSelectedItem().toString())){
                filterDto.setHazardLevel(spinner.getSelectedItem().toString());
            }

            int id = searchView.getContext().getResources().getIdentifier("android:id/search_src_text", null, null);
            EditText  textView = (EditText ) searchView.findViewById(id);
            filterDto.setName(textView.getText().toString());

            intent.putExtra(BusinessConstant.FILTER,new Gson().toJson(filterDto));
            startActivityForResult(intent,QUEST_CODE);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }



    private void getGPSLocation() {
        mLocation = LocationServices.getFusedLocationProviderClient(this);
        try {
            if (mLocationPermissionsGranted) {
                // Centers map on new location when GPS detects change in position
                LocationListener locationListener = new LocationListener() {
                    @Override
                    public void onLocationChanged(Location location) {
                        if (lastLocation == null) {
                            lastLocation = location;
                        }
                        moveCamera(new LatLng(location.getLatitude(),location.getLongitude()), DEFAULT_ZOOM);
                    }

                    @Override
                    public void onStatusChanged(String s, int i, Bundle bundle) {

                    }

                    @Override
                    public void onProviderEnabled(String s) {

                    }

                    @Override
                    public void onProviderDisabled(String s) {

                    }
                };
                LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,1000,1,locationListener);

                Task location = mLocation.getLastLocation();
                location.addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        try {
                            // Checks for priority of setting location of restaurant if intent passes that information through
                            // If no information passes through, it'll default to gps camera view
                            String trackingNumber = getIntent().getExtras().getString("trackingNumber");

                            for (Marker marker : markers) {
                                if (marker.getTag().equals(trackingNumber)) {
                                    moveCamera(marker.getPosition(), DEFAULT_ZOOM);
                                    marker.showInfoWindow();
                                }
                            }
                        } catch (NullPointerException e) {
                            Location current = (Location) task.getResult();
                            if (current != null) {
                                moveCamera(new LatLng(current.getLatitude(), current.getLongitude()), DEFAULT_ZOOM);
                            }
                        }
                    } else {
                        Toast.makeText(MapsActivity.this, "Cannot get current location", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        } catch (SecurityException e) {
            Log.e("Error", "SecurityException " + e.getMessage());
        }
    }

    private void moveCamera(LatLng latLng, float zoom) {
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,zoom));
    }


    private void initMap(){
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(MapsActivity.this);

    }

    private void populateMarkers() {
        dataList.clear();
        for (int x = 0; x < restaurantManager.getNumRestaurant(); x++) {
            Restaurant res = restaurantManager.get(x);
            Inspection ins = inspectionManager.getMostRecentInspection(res.getTrackingNumber());
            RestaurantDto restaurantDto = new RestaurantDto();
            restaurantDto.setTrackingNumber(res.getTrackingNumber());
            restaurantDto.setName(res.getName());
            restaurantDto.setAddress(res.getAddress());
            restaurantDto.setLatitude(res.getLatitude());
            restaurantDto.setLongitude(res.getLongitude());
            restaurantDto.setIndex(x);
            try {
                restaurantDto.setIssue(String.valueOf(ins.getNumCritViolations() + ins.getNumNonCritViolations()));
                restaurantDto.setHazard(ins.getHazardRating());
                restaurantDto.setDate(ins.adjustTime());
            } catch (NullPointerException e) {
                restaurantDto.setIssue(getString(R.string.no_inspection_found));
                restaurantDto.setHazard("Low");
                restaurantDto.setDate(getString(R.string.no_inspection_found));
            }

            dataList.add(restaurantDto);
        }

        textFilter.setOriginData(dataList);
        FilterDto filterDto = new FilterDto();
        textFilter.filter(new Gson().toJson(filterDto));
    }

    private void getLocationPermission(){
        String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION};

        if(ContextCompat.checkSelfPermission(this.getApplicationContext(),
                FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            if(ContextCompat.checkSelfPermission(this.getApplicationContext(),
                    COURSE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                mLocationPermissionsGranted = true;
                initMap();
            }else{
                ActivityCompat.requestPermissions(this,
                        permissions,
                        LOCATION_PERMISSION_REQUEST_CODE);
            }
        }else{
            ActivityCompat.requestPermissions(this,
                    permissions,
                    LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    //add dialog event
    private void initUpdateDialogEvent(){
        updateDialog.findViewById(R.id.btn_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateDialog.dismiss();
            }
        });

        updateDialog.findViewById(R.id.btn_ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                confirmUpdate();
                updateDialog.dismiss();
            }
        });

        loadDialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    cancelDialog.show();
                    initCancelDialogEvent();
                }
                return false;
            }
        });
    }

    //init cancel Dialog event
    private void initCancelDialogEvent() {
        cancelDialog.findViewById(R.id.btn_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cancelDialog.dismiss();
            }
        });

        cancelDialog.findViewById(R.id.btn_ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cancelUpdate();
                cancelDialog.dismiss();
            }
        });
    }


    //get updated data
    private void confirmUpdate(){
        //restaurant data
        final Retrofit retrofit = RetrofitManager.getIntance(BusinessConstant.GET_BASE_URL);
        Call<RestaurantEntry> call = retrofit.create(ApiService.class).getData(BusinessConstant.API_RESTAURANT);
        call.enqueue(new Callback<RestaurantEntry>() {
            @Override
            public void onResponse(Call<RestaurantEntry> call, Response<RestaurantEntry> response) {
                if (response.body().getResult().getResources() != null && response.body().getResult().getResources().size() > 0) {
                    try {
                        String lastUpdateStr = SPUtils.get(MapsActivity.this, BusinessConstant.RESTAURANT_UPDATE_DATE, "");
                        String modifyDateStr = response.body().getResult().getMetadata_modified();
                        if (TextUtils.isEmpty(lastUpdateStr)) {//never update yet
                            getRestaurantData(response);
                        } else {
                            LocalDateTime modify = LocalDateTime.parse(modifyDateStr);
                            LocalDateTime lastUpdate = LocalDateTime.parse(lastUpdateStr);
                            if (modify.toInstant(ZoneOffset.of("+8")).toEpochMilli() >
                                    lastUpdate.toInstant(ZoneOffset.of("+8")).toEpochMilli()) {
                                getRestaurantData(response);
                            }
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<RestaurantEntry> call, Throwable t) {

            }
        });
    }

    /*
     * show update dialog
     * */
    private void showUpdateDialog() {
        if (SPUtils.contains(MapsActivity.this, BusinessConstant.UPDATE_TIME)) {
            long lastUpdateTime = SPUtils.get(MapsActivity.this, BusinessConstant.UPDATE_TIME, 0L);
            if (System.currentTimeMillis() - lastUpdateTime > BusinessConstant.UPDATE_INTEVAL) {
                updateDialog.show();
                initUpdateDialogEvent();
            }
        } else {
            updateDialog.show();
            initUpdateDialogEvent();
        }
    }


    /*
     * get restaurant csv file
     * */
    private void getRestaurantData(Response<RestaurantEntry> restaurantEntryResponse) {
        //restaurant data
        if (restaurantEntryResponse.body().getResult().getResources() != null && restaurantEntryResponse.body().getResult().getResources().size() > 0) {
            //get csv file
            for (RestaurantEntry.ResultBean.ResourcesBean resourcesBean : restaurantEntryResponse.body().getResult().getResources()) {
                if (resourcesBean.getFormat().equals(BusinessConstant.CSV_FORMAT)) {
                    String url = resourcesBean.getUrl();
                    Log.i(TAG, url);

                    Retrofit retrofit1 = RetrofitManager.getIntance(BusinessConstant.GET_BASE_URL);

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            loadDialog.show();
                            loadDialog.findViewById(R.id.btn_cancel).setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    cancelDialog.show();
                                    initCancelDialogEvent();
                                }
                            });
                        }
                    });

                    int start = url.indexOf(BusinessConstant.DATA_SET);
                    if (start != -1) {
                        String param = url.substring(start);
                        restaurantCall = retrofit1.create(ApiService.class).download(param);
                        restaurantCall.enqueue(new Callback<ResponseBody>() {
                            @Override
                            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                FileUtils.generateCsvFileFromStream(getExternalFilesDir(Environment.DIRECTORY_PICTURES) + BusinessConstant.RESTAURANT_CSV_TEMP_FILE_PATH,
                                        response.body().byteStream(), response.body().contentLength(),
                                        new DownloadListener() {
                                            @Override
                                            public void onStart() {


                                            }

                                            @Override
                                            public void onProgress(int progress) {

                                            }

                                            @Override
                                            public void onFinish(String path) {
                                                getInspectionData(restaurantEntryResponse);
                                            }

                                            @Override
                                            public void onFail(String errorInfo) {
                                                loadDialog.dismiss();
                                                Toast.makeText(MapsActivity.this,R.string.data_load_fail,Toast.LENGTH_SHORT).show();
                                            }
                                        });
                            }

                            @Override
                            public void onFailure(Call<ResponseBody> call, Throwable t) {
                                loadDialog.dismiss();
                                Toast.makeText(MapsActivity.this,R.string.data_load_fail,Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                    break;
                }
            }
        }
    }

    //get inspection csv data
    private void getInspectionData(Response<RestaurantEntry> restaurantEntryResponse) {
        final Retrofit retrofit = RetrofitManager.getIntance(BusinessConstant.GET_BASE_URL);
        //inspection data
        Call<InspectionEntry> call = retrofit.create(ApiService.class).getInspectionData(BusinessConstant.API_INSPECTION);
        call.enqueue(new Callback<InspectionEntry>() {
            @Override
            public void onResponse(Call<InspectionEntry> call, Response<InspectionEntry> response) {
                if (response.body().getResult().getResources() != null && response.body().getResult().getResources().size() > 0) {
                    //get csv file
                    for (InspectionEntry.ResultBean.ResourcesBean resourcesBean : response.body().getResult().getResources()) {
                        if (resourcesBean.getFormat().equals(BusinessConstant.CSV_FORMAT)) {
                            String url = resourcesBean.getUrl();
                            Log.i(TAG, url);

                            Retrofit retrofit1 = RetrofitManager.getIntance(BusinessConstant.GET_BASE_URL);

                            int start = url.indexOf(BusinessConstant.DATA_SET);
                            if (start != -1) {
                                String param = url.substring(start);
                                inspectionCall = retrofit1.create(ApiService.class).download(param);
                                inspectionCall.enqueue(new Callback<ResponseBody>() {
                                    @Override
                                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                        FileUtils.generateCsvFileFromStream(getExternalFilesDir(Environment.DIRECTORY_PICTURES) + BusinessConstant.INSPECTION_CSV_TEMP_FILE_PATH,
                                                response.body().byteStream(), response.body().contentLength(),
                                                new DownloadListener() {
                                                    @Override
                                                    public void onStart() {

                                                    }

                                                    @Override
                                                    public void onProgress(int progress) {

                                                    }

                                                    @Override
                                                    public void onFinish(String path) {
                                                        loadDialog.dismiss();

                                                        SPUtils.put(MapsActivity.this, BusinessConstant.UPDATE_TIME,System.currentTimeMillis());
                                                        SPUtils.put(MapsActivity.this, BusinessConstant.RESTAURANT_UPDATE_DATE,
                                                                restaurantEntryResponse.body().getResult().getMetadata_modified());
                                                        //rename temp csv to real csv
                                                        if(new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES) + BusinessConstant.INSPECTION_CSV_FILE_PATH).exists()){
                                                            new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES) + BusinessConstant.INSPECTION_CSV_FILE_PATH).delete();
                                                        }

                                                        new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES) + BusinessConstant.INSPECTION_CSV_TEMP_FILE_PATH).renameTo(
                                                                new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES) + BusinessConstant.INSPECTION_CSV_FILE_PATH));

                                                        if(new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES) + BusinessConstant.RESTAURANT_CSV_FILE_PATH).exists()){
                                                            new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES) + BusinessConstant.RESTAURANT_CSV_FILE_PATH).delete();
                                                        }

                                                        new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES) + BusinessConstant.RESTAURANT_CSV_TEMP_FILE_PATH).renameTo(
                                                                new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES) + BusinessConstant.RESTAURANT_CSV_FILE_PATH));

                                                        initData(true);

                                                    }

                                                    @Override
                                                    public void onFail(String errorInfo) {
                                                        Toast.makeText(MapsActivity.this,R.string.data_load_fail,Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                    }

                                    @Override
                                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                                        loadDialog.dismiss();
                                        Toast.makeText(MapsActivity.this,R.string.data_load_fail,Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                            break;
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<InspectionEntry> call, Throwable t) {
                loadDialog.dismiss();
            }
        });
    }

    //cancel downloading dismiss dialog
    private void cancelUpdate(){
        updateDialog.dismiss();
        restaurantCall.cancel();
        inspectionCall.cancel();
    }

    //okHTTP log interceptor
    class LogCatInterceptor implements Interceptor {
        @Override
        public okhttp3.Response intercept(Chain chain) throws IOException {
            return new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY).intercept(chain);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case QUEST_CODE:
                if(resultCode==HomeActivity.RESULT_OK){
                    filterDto = new Gson().fromJson(data.getStringExtra(BusinessConstant.FILTER),FilterDto.class);
                    if(filterDto!=null){
                        favBtn.setChecked(filterDto.isFavourite());
                        int id = searchView.getContext().getResources().getIdentifier("android:id/search_src_text", null, null);
                        EditText  textView = (EditText ) searchView.findViewById(id);
                        textView.setText(filterDto.getName());
                        editText.setText(filterDto.getCriticalNumber());
                        String[] values = getResources().getStringArray(R.array.hazard_array);
                        for(int i=0;i<values.length;i++){
                            if(values[i].equals(filterDto.getHazardLevel())){
                                spinner.setSelection(i);
                            }
                        }
                    }
                }
                break;
        }
    }
}
