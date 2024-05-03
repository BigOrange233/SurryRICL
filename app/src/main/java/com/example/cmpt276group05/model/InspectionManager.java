package com.example.cmpt276group05.model;

import android.content.Context;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

import com.example.cmpt276group05.R;
import com.example.cmpt276group05.callback.ParseFinishListener;
import com.example.cmpt276group05.constant.BusinessConstant;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/*
 * Manager for Inspection objects. Has methods for sorting and adding/ deleting inspections
 */


public class  InspectionManager implements Iterable<Inspection>{

    private static List<Inspection> inspectionList = new ArrayList<>();
    private static Context context;
    private static String[] types={"Low","Moderate","High"};
    // Allows access to files
    private ExecutorService pool = Executors.newFixedThreadPool(1);
    private boolean isInited = false;

    @Override
    public Iterator<Inspection> iterator() {
        return inspectionList.iterator();
    }

    // Singleton Support
    private static InspectionManager instance;
    private InspectionManager() {
        // Prevent anyone else from instantiating
    }

    public static InspectionManager getInstance(Context ctx) {
        if (instance == null) {
            instance = new InspectionManager();
            context = ctx.getApplicationContext();
        }else{
            context = ctx.getApplicationContext();
        }
        return instance;
    }

    //init data
    public void initData(ParseFinishListener parseFinishListener){
        if(!isInited){
            pool.execute(new Runnable() {
                @Override
                public void run() {
                    readInspectionData();
                    sortArrayList();
                    if(parseFinishListener!=null){
                        parseFinishListener.onFinish();
                    }
                    isInited = true;
                }
            });
        }else{
            if(parseFinishListener!=null){
                parseFinishListener.onFinish();
            }
        }
    }

    public static void add(Inspection inspection) {
        inspectionList.add(inspection);
    }

    // Get inspection at index
    public static Inspection get(int index) {
        return inspectionList.get(index);
    }

    // Get list of inspections relating to tracking number, in most recent order
    // Assumes list is presorted
    public List<Inspection> getList(String trackingNumber) {
        List<Inspection> listOfInspections = new ArrayList<>();
        boolean foundCluster = false;
        boolean endOfCluster = false;
        int index = 0;
        while ((!foundCluster || !endOfCluster) && index<inspectionList.size()) {
            if (inspectionList.get(index).getTrackingNumber().equals(trackingNumber)) {
                listOfInspections.add(inspectionList.get(index));
                foundCluster = true;

            // Reaching this condition implies that you reached the end of the cluster
            } else if (foundCluster){
                endOfCluster = true;
            }

            index++;
        }
        return listOfInspections;
    }

    public int getNumInspection() {
        return inspectionList.size();
    }

    public void removeAtIndex(int index) {
        inspectionList.remove(index);
    }

    // Sorts by restaurants, then sorts clustered restaurant inspections by most recent inspection
    private static void sortArrayList() {
        Collections.sort(inspectionList, Comparator.comparing(Inspection::getTrackingNumber)
                    .thenComparing(Inspection::getInspectionDate).reversed());
    }

    private static void readInspectionData() {
        String[] line = new String[7];
        try {

            InputStream is=context.getResources().openRawResource(R.raw.inspectionreports_itr1);
            File file = new File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)+
                    BusinessConstant.INSPECTION_CSV_FILE_PATH);
            if(file.exists()){
                try {
                    is = new FileInputStream(file);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }

            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(is, StandardCharsets.ISO_8859_1)
            );
            CSVReader csvReader = new CSVReader(reader);

            // Skip over header
            csvReader.readNext();

            while ((line = csvReader.readNext()) != null) {
                Inspection inspection = new Inspection();
                inspection.setTrackingNumber(line[0]);

                // Formatting with date
                SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd", Locale.CANADA);
                if(!TextUtils.isEmpty(line[1])){
                    Date date = format.parse(line[1]);
                    inspection.setInspectionDate(date);
                }

                inspection.setInspectionType(line[2]);
                if(!TextUtils.isEmpty(line[3])){
                    inspection.setNumCritViolations(Integer.parseInt(line[3]));
                }

                if(!TextUtils.isEmpty(line[4])){
                    inspection.setNumNonCritViolations(Integer.parseInt(line[4]));
                }

                if(line[5].indexOf("Low")!=-1||line[5].indexOf("Moderate")!=-1||line[5].indexOf("High")!=-1){
                    inspection.setHazardRating(line[5]);
                }else{
                    inspection.setViolationReport(line[5]);
                }

                if(line[6].indexOf("Low")!=-1||line[6].indexOf("Moderate")!=-1||line[6].indexOf("High")!=-1){
                    inspection.setHazardRating(line[6]);
                }else{
                    inspection.setViolationReport(line[6]);
                }

                inspectionList.add(inspection);

//                Log.d("Myactivity", "Just Created: " + inspection);
            }
        } catch (IOException | CsvValidationException | ParseException e) {
//            Log.wtf("MyActivity", "Error reading data file on line " + line, e);
            e.printStackTrace();
        }
    }


    // Gets most recent inspection using tracking number of restaurant. Use only if tracking number
    // is known
    public Inspection getMostRecentInspection(String trackingNumber) {
        for (int x = 0; x < inspectionList.size(); x++) {
            if (inspectionList.get(x).getTrackingNumber().equals(trackingNumber)) {
                return inspectionList.get(x);
            }
        }
        return null;
    }


    public int numOfIssuesFound(String trackingNumber) {
        Inspection target = getMostRecentInspection(trackingNumber);
        return target.getNumCritViolations() + target.getNumNonCritViolations();
    }

    public boolean isInited() {
        return isInited;
    }

    public void setInited(boolean inited) {
        isInited = inited;
    }
}
