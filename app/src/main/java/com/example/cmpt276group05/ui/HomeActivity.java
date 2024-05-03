package com.example.cmpt276group05.ui;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.SearchView;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.cmpt276group05.R;
import com.example.cmpt276group05.adapter.RestaurantAdapter;
import com.example.cmpt276group05.callback.ParseFinishListener;
import com.example.cmpt276group05.callback.SearchListener;
import com.example.cmpt276group05.constant.BusinessConstant;
import com.example.cmpt276group05.filter.TextFilter;
import com.example.cmpt276group05.model.FilterDto;
import com.example.cmpt276group05.model.Inspection;
import com.example.cmpt276group05.model.InspectionManager;
import com.example.cmpt276group05.model.Restaurant;
import com.example.cmpt276group05.model.RestaurantDto;
import com.example.cmpt276group05.model.RestaurantManager;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;
// Main Activity.java
//input data to a custom layout
//change the date format to better indicate the date and time period
//add icon for restaurant and Hazard indicators
//add the search and filter feature
public class HomeActivity extends AppCompatActivity {
    public static final String TAG = MainActivity.class.getName();
    private RestaurantManager restaurantManager;
    private InspectionManager inspectionManager;
    private SearchView searchView;
    private ListView listView;
    private RestaurantAdapter adapter;
    private List<RestaurantDto> dataList = new ArrayList<>();
    private TextFilter textFilter;
    private Toolbar toolbar;
    private CheckBox favBtn;
    private EditText editText;
    private Spinner spinner;
    private FilterDto filterDto;
    public static final int RESULT_OK = 0x002;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        initView();
        initData();
        initEvent();
    }

    private void initView(){
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionbar1 = getSupportActionBar();
        actionbar1.setTitle(R.string.surrey_restaurant_inspection);

        listView = findViewById(R.id.lv_list);
        searchView = findViewById(R.id.sv_search);
        favBtn = findViewById(R.id.cb_fav);
        editText = findViewById(R.id.et_number);
        spinner = findViewById(R.id.sp_level);
    }

    private void initData(){
        filterDto = new Gson().fromJson(getIntent().getStringExtra("filter"),FilterDto.class);

        adapter = new RestaurantAdapter(this,dataList,R.layout.customview);
        listView.setAdapter(adapter);
        textFilter = new TextFilter(dataList, new SearchListener() {
            @Override
            public void onFinish(List dataList) {
                adapter.notifyDataSetChanged();
            }
        });
        initAdapterData(false);

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

    private void initAdapterData(boolean force){
        inspectionManager = InspectionManager.getInstance(getApplicationContext());
        inspectionManager.initData(null);
        restaurantManager = RestaurantManager.getInstance(getApplicationContext());
        if(force){
            restaurantManager.setInited(false);
            dataList.clear();
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
                    Inspection ins = inspectionManager.getMostRecentInspection(res.getTrackingNumber());
                    RestaurantDto restaurantDto = new RestaurantDto();
                    restaurantDto.setTrackingNumber(res.getTrackingNumber());
                    restaurantDto.setName(res.getName());
                    restaurantDto.setIndex(i);
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

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        adapter.notifyDataSetChanged();
                        textFilter.setOriginData(dataList);
                        if(filterDto!=null){
                            textFilter.filter(new Gson().toJson(filterDto));
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
                textFilter.filter(new Gson().toJson(filterDto));
                return false;
            }
        });

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
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
                textFilter.filter(new Gson().toJson(filterDto));
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
                textFilter.filter(new Gson().toJson(filterDto));
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
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

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        intent.putExtra(BusinessConstant.FILTER, new Gson().toJson(filterDto));
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        initData();
    }
}
