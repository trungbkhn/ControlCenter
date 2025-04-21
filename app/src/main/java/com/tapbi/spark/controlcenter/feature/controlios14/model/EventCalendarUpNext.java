package com.tapbi.spark.controlcenter.feature.controlios14.model;

public class EventCalendarUpNext {
    private String id;
    private String name;
    private String startAt;
    private String endAt;
    private String description;
    private int color;

    public EventCalendarUpNext() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStartAt() {
        return startAt;
    }

    public void setStartAt(String startAt) {
        this.startAt = startAt;
    }

    public String getEndAt() {
        return endAt;
    }

    public void setEndAt(String endAt) {
        this.endAt = endAt;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    @Override
    public String toString() {
        return "EventCalendarUpNext{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", startAt='" + startAt + '\'' +
                ", endAt='" + endAt + '\'' +
                ", description='" + description + '\'' +
                ", color=" + color +
                '}';
    }
}
