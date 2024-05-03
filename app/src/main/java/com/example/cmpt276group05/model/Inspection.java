package com.example.cmpt276group05.model;

import android.util.Log;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/*
 * Data Class for Inspection
 */

public class Inspection {
    private String trackingNumber;
    private Date inspectionDate;
    private String inspectionType;
    private int numCritViolations;
    private int numNonCritViolations;
    private String hazardRating;
    private String violationReport;

    public String getTrackingNumber() {
        return trackingNumber;
    }

    public void setTrackingNumber(String trackingNumber) {
        this.trackingNumber = trackingNumber;
    }

    public Date getInspectionDate() {
        return inspectionDate;
    }

    public void setInspectionDate(Date inspectionDate) {
        this.inspectionDate = inspectionDate;
    }

    public String getInspectionType() {
        return inspectionType;
    }

    public void setInspectionType(String inspectionType) {
        this.inspectionType = inspectionType;
    }

    public int getNumCritViolations() {
        return numCritViolations;
    }

    public void setNumCritViolations(int numCritViolations) {
        this.numCritViolations = numCritViolations;
    }

    public int getNumNonCritViolations() {
        return numNonCritViolations;
    }

    public void setNumNonCritViolations(int numNonCritViolations) {
        this.numNonCritViolations = numNonCritViolations;
    }

    public String getHazardRating() {
        return hazardRating;
    }

    public void setHazardRating(String hazardRating) {
        this.hazardRating = hazardRating;
    }

    public String getViolationReport() {
        return violationReport;
    }

    public void setViolationReport(String violationReport) {
        this.violationReport = violationReport;
    }


    public String adjustTime(){

        Date now = new Date();
        Date inspect = inspectionDate;
        long diffInMillies = Math.abs(inspect.getTime() - now.getTime());
        double diffInDays = TimeUnit.MINUTES.convert(diffInMillies, TimeUnit.MILLISECONDS) / 60 / 24;

        String timeInfo = "";

        if (diffInDays <= 30) {
            timeInfo = diffInDays + " days ago";
        } else if (diffInDays <= 365) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd", Locale.CANADA);
            timeInfo = dateFormat.format(inspect);
        } else {
            SimpleDateFormat dateFormat = new SimpleDateFormat("MMM YYYY", Locale.CANADA);
            timeInfo = dateFormat.format(inspect);
        }

        return timeInfo;
    }

    @Override
    public String toString() {
        return "Inspection{" +
                "trackingNumber='" + trackingNumber + '\'' +
                ", inspectionDate=" + inspectionDate +
                ", inspectionType='" + inspectionType + '\'' +
                ", numCritViolations=" + numCritViolations +
                ", numNonCritViolations=" + numNonCritViolations +
                ", hazardRating='" + hazardRating + '\'' +
                ", violationReport='" + violationReport + '\'' +
                '}';
    }
}
