package com.tapbi.spark.controlcenter.feature.controlios14.view.noty.widget;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.tapbi.spark.controlcenter.R;
import com.tapbi.spark.controlcenter.eventbus.EventActionSearch;
import com.tapbi.spark.controlcenter.feature.controlios14.view.noty.MaskView;
import com.tapbi.spark.controlcenter.utils.MethodUtils;

import org.greenrobot.eventbus.EventBus;

public class SearchView extends MaskView {
    public static final String ACTION_SHOW_SEARCH = "action_show_search";
    public static final String ACTION_HIDE_SEARCH = "action_hide_search";
    private Context context;

    private TextView actionCancel;
    private EditText edtSearch;

    private ValueAnimator valueAnimator;
    private int widthSearch;
    private int widthEnableSearch;

    private Rect bounds;

    private OnSearchViewListener onSearchViewListener;
    private TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            String text = s.toString();
            if (onSearchViewListener != null) {
                onSearchViewListener.onTextChange(text);
            }
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };
    private boolean isEnable = true;
    private OnClickListener onClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            if (isEnable) {
                if (v == edtSearch) {
                    enableSearch();

                } else if (v == actionCancel) {
                    disableSearch();
                }
            }
        }
    };

    public SearchView(@NonNull Context context) {
        super(context);
        init(context);
    }

    public SearchView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public SearchView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public void updateBg() {
//        setViewBackground(background);
    }

    @SuppressLint("ClickableViewAccessibility")
    private void init(Context ctx) {
        this.context = ctx;
        LayoutInflater.from(context).inflate(R.layout.view_search, this, true);
        updateBg();
        actionCancel = findViewById(R.id.actionCancel);
        edtSearch = findViewById(R.id.edtSearch);

        edtSearch.setOnClickListener(onClickListener);
        edtSearch.setOnTouchListener((v, event) -> {
            if (isEnable && event.getAction() == MotionEvent.ACTION_DOWN) {
                enableSearch();
            }
            return false;
        });
        actionCancel.setOnClickListener(onClickListener);
        edtSearch.addTextChangedListener(textWatcher);

        bounds = new Rect();
        Paint textPaint = actionCancel.getPaint();
        textPaint.setTextSize(MethodUtils.dp2px(context, 18));
        Typeface typeface = ResourcesCompat.getFont(context, R.font.sf_pro_text_regular);
        if (typeface != null) {
            textPaint.setTypeface(typeface);
        }
        textPaint.getTextBounds(context.getString(R.string.cancel), 0, context.getString(R.string.cancel).length(), bounds);

        widthSearch = getResources().getDisplayMetrics().widthPixels - MethodUtils.dp2px(context, 10);
        widthEnableSearch = widthSearch - bounds.width() - MethodUtils.dp2px(context, 10) * 2;
    }

    public void setEnable(boolean enable) {
        isEnable = enable;
    }

    private void sendBroadCastSearch(String action) {
        Intent intent = new Intent(action);
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
    }

    public void screenOff() {
        if (widthSearch != 0) {
            closeKeyBoard();
            actionCancel.setVisibility(GONE);
        }
    }

    private void enableSearch() {
        edtSearch.setFocusable(true);
        edtSearch.setFocusableInTouchMode(true);
        edtSearch.requestFocus();
        edtSearch.setText("");
        EventBus.getDefault().post(new EventActionSearch(ACTION_SHOW_SEARCH));
        actionCancel.setTranslationX(0);
//        actionCancel.setVisibility(VISIBLE);

        if (valueAnimator != null) {
            valueAnimator.cancel();
        }

        valueAnimator = ValueAnimator.ofFloat(widthSearch, widthEnableSearch);
        valueAnimator.setDuration(300);
        valueAnimator.setInterpolator(new DecelerateInterpolator());
        valueAnimator.addUpdateListener(animation -> {
            float f = (float) animation.getAnimatedValue();
            if (onSearchViewListener != null) {
                onSearchViewListener.onProgressAnimaion(f);
            }

            updateCLipPath((int) f);

        });
        valueAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
//                tvSearch.setVisibility(GONE);
                edtSearch.setPadding(MethodUtils.dpToPx(context, 10f), 0, (widthSearch - widthEnableSearch) + MethodUtils.dpToPx(context, 10f), 0);
                edtSearch.requestLayout();
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        valueAnimator.start();

        if (onSearchViewListener != null) {
            onSearchViewListener.onOpenSearch();

        }
    }

    public void disableSearch() {
        EventBus.getDefault().post(new EventActionSearch(ACTION_HIDE_SEARCH));
        edtSearch.setText("");
        edtSearch.clearFocus();
        if (valueAnimator != null) {
            valueAnimator.cancel();
        }
        valueAnimator = ValueAnimator.ofFloat(widthEnableSearch, widthSearch);
        valueAnimator.setDuration(300);
        valueAnimator.setInterpolator(new DecelerateInterpolator());
        valueAnimator.addUpdateListener(animation -> {
            float f = (float) animation.getAnimatedValue();
            if (onSearchViewListener != null) {
                onSearchViewListener.onProgressAnimaion(f);
            }
            updateCLipPath((int) f);
//                paramsSearch.width = (int) f;
//                searchContainer.requestLayout();
//                actionCancel.setTranslationX(f);
        });
        valueAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                actionCancel.setVisibility(GONE);
                edtSearch.setPadding(MethodUtils.dpToPx(context, 10f), 0, MethodUtils.dpToPx(context, 10f), 0);
                edtSearch.requestLayout();
                closeKeyBoard();
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        valueAnimator.start();
        if (onSearchViewListener != null) {
            onSearchViewListener.onCloseSearch();
        }
    }

    public void closeKeyBoard() {
        MethodUtils.closeKeyboard(context, edtSearch);
        edtSearch.clearFocus();
    }

    public void setOnSearchViewListener(OnSearchViewListener onSearchViewListener) {
        this.onSearchViewListener = onSearchViewListener;
    }

    public interface OnSearchViewListener {
        void onOpenSearch();

        void onCloseSearch();

        void onTextChange(String s);

        void onProgressAnimaion(float f);
    }
}

