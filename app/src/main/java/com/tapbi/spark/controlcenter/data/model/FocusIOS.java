package com.tapbi.spark.controlcenter.data.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class FocusIOS {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private String name;
    private String imageLink;
    private String colorFocus;
    private int modeAllowPeople;
    private Boolean isStartAutoTime;
    private Boolean isStartAutoLocation;
    private Boolean isStartAutoAppOpen;
    private Boolean isDefault;
    private Boolean isStartCurrent;

    public FocusIOS() {

    }

    public FocusIOS(String name, String imageLink, String colorFocus
            , int modeAllowPeople, Boolean isStartAutoTime, Boolean isStartAutoLocation
            , Boolean isStartAutoAppOpen, Boolean isDefault, Boolean isStartCurrent) {
        this.name = name;
        this.imageLink = imageLink;
        this.colorFocus = colorFocus;
        this.modeAllowPeople = modeAllowPeople;
        this.isStartAutoTime = isStartAutoTime;
        this.isStartAutoLocation = isStartAutoLocation;
        this.isStartAutoAppOpen = isStartAutoAppOpen;
        this.isDefault = isDefault;
        this.isStartCurrent = isStartCurrent;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getModeAllowPeople() {
        return modeAllowPeople;
    }

    public void setModeAllowPeople(int modeAllowPeople) {
        this.modeAllowPeople = modeAllowPeople;
    }

    public Boolean getStartAutoTime() {
        return isStartAutoTime;
    }

    public void setStartAutoTime(Boolean startAutoTime) {
        isStartAutoTime = startAutoTime;
    }

    public Boolean getStartAutoLocation() {
        return isStartAutoLocation;
    }

    public void setStartAutoLocation(Boolean startAutoLocation) {
        isStartAutoLocation = startAutoLocation;
    }

    public Boolean getStartAutoAppOpen() {
        return isStartAutoAppOpen;
    }

    public void setStartAutoAppOpen(Boolean startAutoAppOpen) {
        isStartAutoAppOpen = startAutoAppOpen;
    }

    public Boolean getDefault() {
        return isDefault;
    }

    public void setDefault(Boolean aDefault) {
        isDefault = aDefault;
    }

    public Boolean getStartCurrent() {
        return isStartCurrent;
    }

    public void setStartCurrent(Boolean startCurrent) {
        isStartCurrent = startCurrent;
    }

    public String getImageLink() {
        return imageLink;
    }

    public void setImageLink(String imageLink) {
        this.imageLink = imageLink;
    }

    public String getColorFocus() {
        return colorFocus;
    }

    public void setColorFocus(String colorFocus) {
        this.colorFocus = colorFocus;
    }
    public FocusIOS cloneValue(){
        FocusIOS focusIOS=new FocusIOS();
        focusIOS.setName(getName());
        focusIOS.setId(getId());
        focusIOS.setImageLink(getImageLink());
        focusIOS.setStartCurrent(getStartCurrent());
        focusIOS.setColorFocus(getColorFocus());
        focusIOS.setStartAutoTime(getStartAutoTime());
        focusIOS.setStartAutoAppOpen(getStartAutoAppOpen());
        focusIOS.setStartAutoLocation(getStartAutoLocation());
        focusIOS.setModeAllowPeople(getModeAllowPeople());
        focusIOS.setDefault(getDefault());
        return focusIOS;
    }
}
