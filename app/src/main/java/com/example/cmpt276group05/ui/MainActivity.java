// Main Activity.java
//input data to a custom layout
//change the date format to better indicate the date and time period
//add icon for restaurant and Hazard indicators




package com.example.cmpt276group05.ui;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.cmpt276group05.R;
import com.example.cmpt276group05.callback.DownloadListener;
import com.example.cmpt276group05.callback.ParseFinishListener;
import com.example.cmpt276group05.constant.BusinessConstant;
import com.example.cmpt276group05.model.Inspection;
import com.example.cmpt276group05.model.InspectionEntry;
import com.example.cmpt276group05.model.InspectionManager;
import com.example.cmpt276group05.model.Restaurant;
import com.example.cmpt276group05.model.RestaurantEntry;
import com.example.cmpt276group05.model.RestaurantManager;
import com.example.cmpt276group05.net.ApiService;
import com.example.cmpt276group05.net.RetrofitManager;
import com.example.cmpt276group05.utils.FileUtils;
import com.example.cmpt276group05.utils.SPUtils;
import com.example.cmpt276group05.widget.BaseDialog;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;
import okhttp3.Interceptor;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class MainActivity extends AppCompatActivity {
    private RestaurantManager restaurantManager;
    private InspectionManager inspectionManager;
    private BaseDialog loadDialog, updateDialog,cancelDialog;
    public static final String TAG = MainActivity.class.getName();
    int images[] = {R.drawable.one, R.drawable.two, R.drawable.three, R.drawable.four};
    int presetimages[] = {R.mipmap.aw,R.mipmap.fi,R.mipmap.fs,R.mipmap.bp,R.mipmap.se,R.mipmap.bc,R.mipmap.juice,R.mipmap.bs,R.mipmap.dq,R.mipmap.panago};
    //0 is A & W  1 is Freshii 2 is  FreshSlice 3 is boston pizza 4 is seven eleven  5 is Blenz Coffee  6 is booster Juice  7 Browns Socialhouse    8 Dairy Queen  9 Panago
    int favicons[] = {R.mipmap.none,R.mipmap.favorite};
    // 0 is nothing hidden icon  1 is heart Icon which indicate the restaurant is favorite.
    ArrayList<String> TName = new ArrayList<String>();
    ArrayList<String> TIssue = new ArrayList<String>();
    ArrayList<String> THazardC = new ArrayList<String>();
    ArrayList<Integer> HazardSelection = new ArrayList<Integer>();
    int THazardI[] = {R.mipmap.low, R.mipmap.mid, R.mipmap.high};
    ArrayList<String> TDate = new ArrayList<String>();
    private Call<ResponseBody> inspectionCall,restaurantCall;
    private arrayAdapter adapter;
    private boolean showDialog = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
        initData(false);
        if(showDialog){
            showUpdateDialog();
        }
    }//onCreate

    /**
     * Called when the activity has detected the user's press of the back
     * key. The {@link #getOnBackPressedDispatcher() OnBackPressedDispatcher} will be given a
     * chance to handle the back button before the default behavior of
     * {@link Activity#onBackPressed()} is invoked.
     *
     * @see #getOnBackPressedDispatcher()
     */
    @Override
    public void onBackPressed() {
        finishAffinity();
    }

    private void initView() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionbar1 = getSupportActionBar();
        actionbar1.setTitle(R.string.surrey_restaurant_inspection);

        ListView listview = (ListView) findViewById(R.id.list);

        adapter = new arrayAdapter(this, TName);
        listview.setAdapter(adapter);

        //onClickListener

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(MainActivity.this, "Selected", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(MainActivity.this, InspectionList.class);
                //please put your activities name on the class above!
                intent.putExtra("Tracking_Number", restaurantManager.get(position).getTrackingNumber());
                startActivity(intent);
            }
        });


        if(showDialog){
            loadDialog = new BaseDialog(this, R.layout.dialog_loading);
            updateDialog = new BaseDialog(this, R.layout.dialog_confirm_update);
            cancelDialog = new BaseDialog(this,R.layout.dialog_cancel);
        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.go_to_map) {
            startActivity(new Intent(this, MapsActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    class arrayAdapter extends ArrayAdapter<String> {
        Context context;

        arrayAdapter(Context cont, ArrayList<String> name){
            super(cont,R.layout.customview,R.id.name,TName);
            this.context = cont;
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            LayoutInflater layoutinflater = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            View view = layoutinflater.inflate(R.layout.customview, parent, false);
            DateFormat dateFormat = new SimpleDateFormat("yyyy-mm-dd hh:mm:ss");

            ImageView resIcon = view.findViewById(R.id.image);
            TextView name = view.findViewById(R.id.name);
            TextView dates = view.findViewById(R.id.date);
            TextView issues = view.findViewById(R.id.issue);
            TextView hazardcolors = view.findViewById(R.id.hazardcolor);
            ImageView HazardIcons = view.findViewById(R.id.hazardicon);
            ImageView FavIcons = view.findViewById(R.id.favicon);
            resIcon.setImageResource(images[position%4]);
            if(TName.get(position).contains("Boston")){
                //0 is A & W     1 is Freshii     2 is  FreshSlice     3 is boston pizza      4 is seven eleven   5 is Blenz Coffee     6 is booster Juice   7 Browns Socialhouse   8 Dairy Queen    9 Panago
                resIcon.setImageResource(presetimages[3]);
            }else if (TName.get(position).contains("Eleven")){
                resIcon.setImageResource(presetimages[4]);
            }else if (TName.get(position).contains("A&W")){
                resIcon.setImageResource(presetimages[0]);
            }else if (TName.get(position).contains("Freshii")){
                resIcon.setImageResource(presetimages[1]);
            }else if (TName.get(position).contains("Freshslice")){
                resIcon.setImageResource(presetimages[2]);
            }else if (TName.get(position).contains("A & W")){
                resIcon.setImageResource(presetimages[0]);
            }else if (TName.get(position).contains("Blenz")){
                resIcon.setImageResource(presetimages[5]);
            }else if (TName.get(position).contains("Booster")){
                resIcon.setImageResource(presetimages[6]);
            }else if (TName.get(position).contains("Browns")){
                resIcon.setImageResource(presetimages[7]);
            }else if (TName.get(position).contains("Dairy")){
                resIcon.setImageResource(presetimages[8]);
            }else if (TName.get(position).contains("Panago")){
                resIcon.setImageResource(presetimages[9]);
            }

            name.setText(TName.get(position));
            dates.setText(getString(R.string.latest_inspection) + TDate.get(position));
            FavIcons.setImageResource(favicons[1]);
            issues.setText(getString(R.string.number_of_issues_found) + TIssue.get(position));


            try{
                if (THazardC.get(position).equals("Low")) {
                    HazardIcons.setImageResource(THazardI[1]);
                } else if (THazardC.get(position).equals("Moderate")) {
                    HazardIcons.setImageResource(THazardI[1]);
                } else if (THazardC.get(position).equals("High")) {
                    HazardIcons.setImageResource(THazardI[2]);
                }
            }catch (Exception e){
                HazardIcons.setImageResource(THazardI[0]);
            }
            return view;
        }
    }//arrayAdapter

    private void initData(boolean force){
        inspectionManager = InspectionManager.getInstance(getApplicationContext());
        inspectionManager.initData(null);
        restaurantManager = RestaurantManager.getInstance(getApplicationContext());
        if(force){
            restaurantManager.setInited(false);
        }
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
                for (int i = 0; i < restaurantManager.getNumRestaurant(); i++) {
                    Restaurant res = restaurantManager.get(i);
//                    Log.d("print", String.valueOf(res));
                    Inspection ins = inspectionManager.getMostRecentInspection(res.getTrackingNumber());
//                    Log.d("Print", String.valueOf(ins));
                    TName.add(res.getName());
                    // In case no inspections exist yet
                    try {
                        TIssue.add(String.valueOf(ins.getNumCritViolations() + ins.getNumNonCritViolations()));
                        THazardC.add(ins.getHazardRating());
                        TDate.add(ins.adjustTime());
                    } catch (NullPointerException e) {
                        THazardC.add("Low");
                        TIssue.add(getString(R.string.no_inspection_found));
                        TDate.add(getString(R.string.no_inspection_found));
                    }
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        adapter.notifyDataSetChanged();
                    }
                });

            }
        });
    }
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
                        String lastUpdateStr = SPUtils.get(MainActivity.this, BusinessConstant.RESTAURANT_UPDATE_DATE, "");
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
        if (SPUtils.contains(MainActivity.this, BusinessConstant.UPDATE_TIME)) {
            long lastUpdateTime = SPUtils.get(MainActivity.this, BusinessConstant.UPDATE_TIME, 0L);
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
                                                        Toast.makeText(MainActivity.this,R.string.data_load_fail,Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                    }

                                    @Override
                                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                                        loadDialog.dismiss();
                                        Toast.makeText(MainActivity.this,R.string.data_load_fail,Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                            break;
                        }
                    }
                }
            }

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

                                                        SPUtils.put(MainActivity.this, BusinessConstant.UPDATE_TIME,System.currentTimeMillis());
                                                        SPUtils.put(MainActivity.this, BusinessConstant.RESTAURANT_UPDATE_DATE,
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

                                                        //refresh file
                                                        initData(true);
                                                    }

                                                    @Override
                                                    public void onFail(String errorInfo) {
                                                        Toast.makeText(MainActivity.this,R.string.data_load_fail,Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                    }

                                    @Override
                                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                                        loadDialog.dismiss();
                                        Toast.makeText(MainActivity.this,R.string.data_load_fail,Toast.LENGTH_SHORT).show();
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

    private void cancelUpdate(){
        updateDialog.dismiss();
        restaurantCall.cancel();
        inspectionCall.cancel();
    }

    class LogCatInterceptor implements Interceptor {
        @Override
        public okhttp3.Response intercept(Chain chain) throws IOException {
            return new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY).intercept(chain);
        }
    }

}//class
