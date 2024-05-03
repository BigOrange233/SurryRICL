package com.example.cmpt276group05.model;

//filter condition
public class FilterDto {
    private boolean isFavourite=false;
    private String name;
    private String criticalNumber;
    private String hazardLevel;

    public boolean isFavourite() {
        return isFavourite;
    }

    public void setFavourite(boolean favourite) {
        isFavourite = favourite;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCriticalNumber() {
        return criticalNumber;
    }

    public void setCriticalNumber(String criticalNumber) {
        this.criticalNumber = criticalNumber;
    }

    public String getHazardLevel() {
        return hazardLevel;
    }

    public void setHazardLevel(String hazardLevel) {
        this.hazardLevel = hazardLevel;
    }
}
