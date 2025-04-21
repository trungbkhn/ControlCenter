package com.tapbi.spark.controlcenter.data.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
@Entity
public class ItemPeople {
    @PrimaryKey(autoGenerate = true)
    private int id;
    @ColumnInfo
    private String contactId;
    @ColumnInfo
    private String name;
    @ColumnInfo
    private String phone;
    @ColumnInfo
    private String image;
    @ColumnInfo
    private boolean isStart;
    @ColumnInfo
    private String nameFocus;

    public ItemPeople(String contactId, String name, String phone, String image, boolean isStart, String nameFocus) {
        this.contactId = contactId;
        this.name = name;
        this.phone = phone;
        this.image = image;
        this.isStart = isStart;
        this.nameFocus = nameFocus;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getContactId() {
        return contactId;
    }

    public void setContactId(String contactId) {
        this.contactId = contactId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public boolean isStart() {
        return isStart;
    }

    public void setStart(boolean start) {
        isStart = start;
    }

    public String getNameFocus() {
        return nameFocus;
    }

    public void setNameFocus(String nameFocus) {
        this.nameFocus = nameFocus;
    }
}
