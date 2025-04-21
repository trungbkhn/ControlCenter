package com.tapbi.spark.controlcenter.data.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class ItemTurnOn {

    public static final int TYPE_HOUR = 1;
    public static final int TYPE_EVENING = 2;
    public static final int TYPE_LOCATION = 3;

    @PrimaryKey(autoGenerate = true)
    private int id;

    private String nameFocus;
    private Boolean isStart;
    private Boolean isStartFocus;
    private long timeStart;
    private long timeEnd;
    private Boolean monDay;
    private Boolean tueDay;
    private Boolean wedDay;
    private Boolean thuDay;
    private Boolean friDay;
    private Boolean satDay;
    private Boolean sunDay;
    private String nameLocation;
    private Double latitude;
    private Double longitude;
    private String packageName;
    private String nameApp;
    private String typeEvent;
    private Long lastModify;
    private int type = -1;


    public ItemTurnOn(String nameFocus, Boolean isStart, Boolean isStartFocus, long timeStart, long timeEnd
            , Boolean monDay, Boolean tueDay, Boolean wedDay, Boolean thuDay, Boolean friDay
            , Boolean satDay, Boolean sunDay, String nameLocation, Double latitude, Double longitude
            , String packageName, String nameApp, String typeEvent, Long lastModify) {
        this.nameFocus = nameFocus;
        this.isStart = isStart;
        this.isStartFocus = isStartFocus;
        this.timeStart = timeStart;
        this.timeEnd = timeEnd;
        this.monDay = monDay;
        this.tueDay = tueDay;
        this.wedDay = wedDay;
        this.thuDay = thuDay;
        this.friDay = friDay;
        this.satDay = satDay;
        this.sunDay = sunDay;
        this.nameLocation = nameLocation;
        this.latitude = latitude;
        this.longitude = longitude;
        this.packageName = packageName;
        this.nameApp = nameApp;
        this.typeEvent = typeEvent;
        this.lastModify = lastModify;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNameFocus() {
        return nameFocus;
    }

    public void setNameFocus(String nameFocus) {
        this.nameFocus = nameFocus;
    }

    public Boolean getStart() {
        return isStart;
    }

    public void setStart(Boolean start) {
        isStart = start;
    }

    public Boolean getStartFocus() {
        return isStartFocus;
    }

    public void setStartFocus(Boolean startFocus) {
        isStartFocus = startFocus;
    }

    public long getTimeStart() {
        return timeStart;
    }

    public void setTimeStart(long timeStart) {
        this.timeStart = timeStart;
    }

    public long getTimeEnd() {
        return timeEnd;
    }

    public void setTimeEnd(long timeEnd) {
        this.timeEnd = timeEnd;
    }

    public Boolean getMonDay() {
        return monDay;
    }

    public void setMonDay(Boolean monDay) {
        this.monDay = monDay;
    }

    public Boolean getTueDay() {
        return tueDay;
    }

    public void setTueDay(Boolean tueDay) {
        this.tueDay = tueDay;
    }

    public Boolean getWedDay() {
        return wedDay;
    }

    public void setWedDay(Boolean wedDay) {
        this.wedDay = wedDay;
    }

    public Boolean getThuDay() {
        return thuDay;
    }

    public void setThuDay(Boolean thuDay) {
        this.thuDay = thuDay;
    }

    public Boolean getFriDay() {
        return friDay;
    }

    public void setFriDay(Boolean friDay) {
        this.friDay = friDay;
    }

    public Boolean getSatDay() {
        return satDay;
    }

    public void setSatDay(Boolean satDay) {
        this.satDay = satDay;
    }

    public Boolean getSunDay() {
        return sunDay;
    }

    public void setSunDay(Boolean sunDay) {
        this.sunDay = sunDay;
    }

    public String getNameLocation() {
        return nameLocation;
    }

    public void setNameLocation(String nameLocation) {
        this.nameLocation = nameLocation;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getNameApp() {
        return nameApp;
    }

    public void setNameApp(String nameApp) {
        this.nameApp = nameApp;
    }

    public String getTypeEvent() {
        return typeEvent;
    }

    public void setTypeEvent(String typeEvent) {
        this.typeEvent = typeEvent;
    }

    public Long getLastModify() {
        return lastModify;
    }

    public void setLastModify(Long lastModify) {
        this.lastModify = lastModify;
    }


    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
