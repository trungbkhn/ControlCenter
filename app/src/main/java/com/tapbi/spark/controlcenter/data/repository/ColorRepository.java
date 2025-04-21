package com.tapbi.spark.controlcenter.data.repository;

import android.content.Context;

import com.tapbi.spark.controlcenter.common.Constant;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class ColorRepository {
    @Inject
    public ColorRepository() {
    }

    private List<String> listColor() {
        return Arrays.asList("#2084FE",
                "#9D8563",
                "#8E8E93",
                "#01CD3B",
                "#7B66FF",
                "#FFC700",
                "#FF6482",
                "#FF6961",
                "#0174CD");
    }

    private List<String> listIcon(Context context) {
        List<String> listIcon = new ArrayList<>();
        try {
            for (String icon : context.getAssets().list("icon")
            ) {
                listIcon.add(Constant.PATH + icon);
            }
            return listIcon;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public Single<List<String>> getListColor() {
        return Single.fromCallable(this::listColor).subscribeOn(Schedulers.io());
    }

    public Single<List<String>> getListIcon(Context context) {
        return Single.fromCallable(() -> listIcon(context)).subscribeOn(Schedulers.io());
    }

}
