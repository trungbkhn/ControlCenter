package com.tapbi.spark.controlcenter.feature.controlios14.view.control.group3;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.databinding.DataBindingUtil;

import com.tapbi.spark.controlcenter.R;
import com.tapbi.spark.controlcenter.databinding.LayoutChildFocusBinding;

public class ChildFocusView extends ConstraintLayout {

    private LayoutChildFocusBinding binding;

    public ChildFocusView(@NonNull Context context) {
        super(context);
        init();
    }

    public ChildFocusView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ChildFocusView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        binding = DataBindingUtil.inflate(LayoutInflater.from(getContext()), R.layout.layout_child_focus, this, true);

    }
}
