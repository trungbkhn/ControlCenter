package com.tapbi.spark.controlcenter.utils;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import timber.log.Timber;

public class MyDebugTree extends Timber.DebugTree {
    String fileName;

    @Override
    protected String createStackElementTag(StackTraceElement element) {
        fileName = element.getFileName();
        return String.format("(%s:%s)#%s",
                element.getFileName(),
                element.getLineNumber(),
                element.getMethodName());
    }

    @Override
    protected void log(int priority, @Nullable String tag, @NonNull String message, @Nullable Throwable t) {
        String mTag;
        String mMessage;
        mTag = fileName;
        mMessage = tag + " " + message;
        super.log(priority, mTag, mMessage, t);
    }
}