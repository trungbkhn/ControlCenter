package com.tapbi.spark.controlcenter.feature.controlios14.model;

import android.app.PendingIntent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.widget.RemoteViews;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.Expose;
import com.tapbi.spark.controlcenter.R;
@Entity
public class NotyModel {
    @PrimaryKey(autoGenerate = true)
    private int id;
    @Expose
    private int idNoty;
    @Expose
    private String keyNoty = "";
    @Ignore
    private Bitmap imaBitmap;
    @Expose
    private String content;
    @Expose
    private String pakage;
    @Expose
    @Ignore
    private PendingIntent pendingIntent;
    @Expose
    private String tagNoty;
    @Expose
    private long time;
    @Expose
    private String title;
    @Expose
    private String groupKey;
    @Ignore
    private RemoteViews remoteView;

    @Ignore
    private RemoteViews remoteView2;
    @Expose
    private int uid;
    private NotyGroup.STATE state;
    private boolean isDelete;
    @Ignore
    private Drawable iconApp = new ColorDrawable(Color.TRANSPARENT);

    @Ignore
    private boolean canShow = true;

    public NotyModel(){

    }


    public boolean isDelete() {
        return isDelete;
    }

    public void setDelete(boolean delete) {
        isDelete = delete;
    }

    public boolean isCanDelete() {
        return isDelete;
    }

    public NotyGroup.STATE getState() {
        return state;
    }

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public void setState(NotyGroup.STATE state) {
        this.state = state;
    }

    public int getIdNoty() {
        return idNoty;
    }

    public void setIdNoty(int idNoty) {
        this.idNoty = idNoty;
    }

    public String getKeyNoty() {
        if (keyNoty == null){
            return "";
        }
        return keyNoty;
    }

    public void setKeyNoty(String keyNoty) {
        this.keyNoty = keyNoty;
    }

    public Bitmap getImaBitmap() {
        return imaBitmap;
    }

    public void setImaBitmap(Bitmap imaBitmap) {
        this.imaBitmap = imaBitmap;
    }

    public String getContent() {
        if (content == null){
            return "";
        }
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getPakage() {
        return pakage;
    }

    public void setPakage(String pakage) {
        this.pakage = pakage;
    }

    public PendingIntent getPendingIntent() {
        return pendingIntent;
    }

    public void setPendingIntent(PendingIntent pendingIntent) {
        this.pendingIntent = pendingIntent;
    }

    public void setRemoteView(RemoteViews remoteView) {
        this.remoteView = remoteView;
    }

    public RemoteViews getRemoteView() {
        return remoteView;
    }

    public String getGroupKey() {
        return groupKey;
    }

    public void setGroupKey(String groupKey) {
        this.groupKey = groupKey;
    }

    public String getTagNoty() {
        return tagNoty;
    }

    public void setTagNoty(String tagNoty) {
        this.tagNoty = tagNoty;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getTitle() {
        if (title == null) {
            return "";
        }
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Drawable getIconApp() {
        return iconApp;
    }

    public void setIconApp(Drawable iconApp) {
        this.iconApp = iconApp;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isCanShow() {
        return canShow;
    }

    public void setCanShow(boolean canShow) {
        this.canShow = canShow;
    }

    public RemoteViews getRemoteView2() {
        return remoteView2;
    }

    public void setRemoteView2(RemoteViews remoteView2) {
        this.remoteView2 = remoteView2;
    }


    @Override
    public String toString() {
        return "NotyModel{" +
                "id=" + id +
                ", idNoty=" + idNoty +
                ", keyNoty='" + keyNoty + '\'' +
                ", content='" + content + '\'' +
                ", pakage='" + pakage + '\'' +
                ", time=" + time +
                ", title='" + title + '\'' +
                ", uid=" + uid +
                ", canShow=" + canShow +
                '}';
    }
}
