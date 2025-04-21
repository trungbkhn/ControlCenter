package com.tapbi.spark.controlcenter.data.model;

import java.util.List;

public class ActionClick {
    private String languageCode;
    private List<TextActionClick> textActionClickList;

    public String getLanguageCode() {
        return languageCode;
    }

    public void setLanguageCode(String languageCode) {
        this.languageCode = languageCode;
    }

    public List<TextActionClick> getActionClickList() {
        return textActionClickList;
    }

    public void setActionClickList(List<TextActionClick> textActionClickList) {
        this.textActionClickList = textActionClickList;
    }
}
