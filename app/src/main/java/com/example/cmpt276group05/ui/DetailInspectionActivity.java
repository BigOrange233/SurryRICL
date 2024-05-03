package com.example.cmpt276group05.ui;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.cmpt276group05.R;
import com.example.cmpt276group05.adapter.ViolationAdapter;
import com.example.cmpt276group05.constant.BusinessConstant;
import com.example.cmpt276group05.model.Inspection;
import com.example.cmpt276group05.model.Violation;
import com.google.gson.Gson;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class DetailInspectionActivity extends AppCompatActivity {
    private Inspection inspection=null;
    private TextView tvDate,tvType,tvCritical,tvHazard;
    private ListView lvViolations;
    private SimpleDateFormat sfd = new SimpleDateFormat ("MMM dd,yyyy", Locale.UK);
    private ViolationAdapter violationAdapter;
    private List<Violation> violationEntities;
    private Toolbar toolbar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inspection_detail);
        initView();
        initData();
        initEvent();
    }

    /*
    * init activity view
    * */
    protected void initView() {
        tvDate = findViewById(R.id.tv_date);
        tvType = findViewById(R.id.tv_type);
        tvCritical = findViewById(R.id.tv_critical);
        tvHazard = findViewById(R.id.tv_hazard);
        lvViolations = findViewById(R.id.lv_violation);
        toolbar = findViewById(R.id.toolbar);
    }

    /*
    * init activity data
    * */
    protected void initData() {
        //get inspectdata by intent
        String inspectData = getIntent().getStringExtra(BusinessConstant.INSPECTION_DATA);
        if(!TextUtils.isEmpty(inspectData)){
            inspection = new Gson().fromJson(inspectData,Inspection.class);
        }
        if(inspection!=null){
            tvDate.setText(sfd.format(inspection.getInspectionDate()));
            tvType.setText(inspection.getInspectionType());
            tvCritical.setText(inspection.getNumCritViolations()+"/"+inspection.getNumNonCritViolations());
            int drawableId=R.mipmap.hazardous_low;
            switch (inspection.getHazardRating()){
                case BusinessConstant.HAZARD_LOW:
                    tvHazard.setTextColor(Color.GREEN);
                    drawableId = R.mipmap.hazardous_low;
                    break;
                case BusinessConstant.HAZARD_MID:
                    tvHazard.setTextColor(Color.YELLOW);
                    drawableId = R.mipmap.hazardous_mid;
                    break;
                case BusinessConstant.HAZARD_HIGH:
                    tvHazard.setTextColor(Color.RED);
                    drawableId = R.mipmap.hazardous_high;
                    break;
            }
            tvHazard.setText(inspection.getHazardRating());
            Drawable drawable = getResources().getDrawable(drawableId);
            // 这一步必须要做,否则不会显示.
            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
            tvHazard.setCompoundDrawables(null, null, drawable, null);

            violationEntities = getViolationList(inspection.getViolationReport());
            violationAdapter = new ViolationAdapter(this,violationEntities,R.layout.item_violation);
            lvViolations.setAdapter(violationAdapter);
        }
    }

    /*
    * init activity event
    * */
    protected void initEvent() {
        lvViolations.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            //add item click event and show toast
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Toast.makeText(DetailInspectionActivity.this,violationEntities.get(i).getDesc(), Toast.LENGTH_SHORT).show();
            }
        });

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    /*
    * parse inspection violationReport to violation list
    * @param desc
    * */
    private List<Violation> getViolationList(String desc){
        List<Violation> violationEntities = new ArrayList<>();
        if(TextUtils.isEmpty(desc)){
            return violationEntities;
        }
        String[] violations = desc.split("\\|");
        for(String violation : violations){
            Violation violationEntity = new Violation();
            String[] ps = violation.split(",");
            if(ps.length>0){
                violationEntity.setCode(ps[0]);
            }
            if(ps.length>1){
                violationEntity.setCirtical(ps[1]);
            }

            if(ps.length>2){
                violationEntity.setDesc(ps[2]);
            }

            if(ps.length>3){
                violationEntity.setRepeat(ps[3]);
            }

            violationEntities.add(violationEntity);
        }
        return violationEntities;
    }

    /*
    * go to inspection datail activity
    * */
    public static void goToInspectionDetail(Context context,Inspection inspection){
        Intent intent = new Intent(context,DetailInspectionActivity.class);
        intent.putExtra(BusinessConstant.INSPECTION_DATA, new Gson().toJson(inspection));
        context.startActivity(intent);
    }
}