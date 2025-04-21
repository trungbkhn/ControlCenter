package com.tapbi.spark.controlcenter.feature.controlcenter.view.noty.view.itemnoty;

import static com.tapbi.spark.controlcenter.common.Constant.LIGHT;
import static com.tapbi.spark.controlcenter.common.Constant.STYLE_SELECTED;
import static com.tapbi.spark.controlcenter.feature.controlcenter.view.noty.view.itemnoty.Utils.getViewWeight;
import static com.tapbi.spark.controlcenter.feature.controlcenter.view.noty.view.itemnoty.Utils.setTint;
import static com.tapbi.spark.controlcenter.feature.controlcenter.view.noty.view.itemnoty.Utils.setViewWidth;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.Handler;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.animation.Animation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.Group;
import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.RecyclerView;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import com.orhanobut.hawk.Hawk;
import com.tapbi.spark.controlcenter.R;
import com.tapbi.spark.controlcenter.common.Constant;
import com.tapbi.spark.controlcenter.data.repository.ThemeHelper;
import com.tapbi.spark.controlcenter.feature.controlcenter.background.WorkSnoozedNotification;
import com.tapbi.spark.controlcenter.feature.controlcenter.cb.ITouchItemView;
import com.tapbi.spark.controlcenter.feature.controlios14.model.NotyGroup;
import com.tapbi.spark.controlcenter.feature.controlios14.model.NotyModel;
import com.tapbi.spark.controlcenter.feature.controlios14.view.noty.NotyCenterViewOS;
import com.tapbi.spark.controlcenter.service.NotificationListener;
import com.tapbi.spark.controlcenter.utils.DensityUtils;
import com.tapbi.spark.controlcenter.utils.MethodUtils;
import com.tapbi.spark.controlcenter.utils.TinyDB;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import timber.log.Timber;


public class ChildSwipeLayout extends FrameLayout implements View.OnTouchListener {
    public static final int ITEM_STATE_LEFT_EXPAND = 0;
    public static final int ITEM_STATE_RIGHT_EXPAND = 1;
    public static final int ITEM_STATE_COLLAPSED = 2;
    private static final String TAG = "SwipeLayout";
    private static final int NO_ID = 0;
    private static final long ANIMATION_MIN_DURATION = 100;
    private static final long ANIMATION_MAX_DURATION = 300;
    public NotyModel notiModel;
    public ImageView imgStateSnoozed, imgState, imgThumb, imgImage, imgImageNoti;
    public boolean first = true;
    int id;
    boolean shouldPerformLongClick;
    boolean longClickPerformed;
    boolean invokedFromLeft;
    private int layoutId;
    private int[] rightColors;
    private int[] rightIcons;
    private int[] rightIconColors;
    private int[] rightTextColors;
    private ConstraintLayout layoutNoti;
    private ConstraintLayout layoutSnoozed;
    private ConstraintLayout layoutSettingNoty;
    private TinyDB tinyDB;
    private String[] rightTexts;
    private int itemWidth;
    private int rightLayoutMaxWidth;
    private View mainLayout;
    private LinearLayout rightLinear, rightLinearWithoutLast;
    private int iconSize;
    private float textSize;
    private int textTopMargin;
    private int fullSwipeEdgePadding;
    private View[] rightViews;
    private boolean swipeEnabled = true;
    private boolean canFullSwipeFromRight;
    private boolean autoHideSwipe = true;
    private boolean onlyOneSwipe = true;
    private RecyclerView.OnScrollListener onScrollListener;
    private NotyCenterViewOS.OnNotyCenterCloseListener onNotyCenterCloseListener;
    private Group groupDetail, groupDetailExpand;
    private TextView tv15Minutes, tv30Minutes, tv1Hours, tv1Day, name1, detailName1, name2, detailName2, tvNameNoty, tvTitle, tvMsg, textUndo, textTimeSnoozed, tvNameApp, tvTimeNoty, tvWaning, detailWaning, tvSilent, detailSilent, tvDone, tvOffNotification;
    private int pos = 0;
    //    private NotyGroup notyGroup;
    private boolean isCollapse = false;
    private boolean isShowIconExpaned = false;
    private boolean showRecyclerView = true;
    private View bgWaning, bgSilent;
    private ImageView imgGoSettingNotification;
    private Group groupOptionSnoozed;
    private int resourceExpand = R.drawable.ic_expaned_noty;
    private int resourceCollapse = R.drawable.ic_collapse_noty;
    public OnClickListener imgStateClick = new OnClickListener() {
        @Override
        public void onClick(View v) {
            if (notiModel.getState() == NotyGroup.STATE.NONE) {
                notiModel.setState(NotyGroup.STATE.EXPAND);
                imgImage.setVisibility(VISIBLE);
                imgThumb.setVisibility(INVISIBLE);
            } else {
                notiModel.setState(NotyGroup.STATE.NONE);
                imgImage.setVisibility(GONE);
                imgThumb.setVisibility(VISIBLE);
            }
            setUpText();
            isCollapse = true;
        }
    };
    private float prevRawX = -1;
    private boolean directionLeft;
    private boolean movementStarted;
    private long lastTime;
    private long downTime;
    private float speed;
    private float downRawX;
    private float downX, downY;
    private int color_gray_waning = ContextCompat.getColor(getContext(), R.color.color_gray_waning);
    private CountDownTimer countDownTimer;
    private ITouchItemView iTouchItemView;
    private OnTouchListener onTouchListener = (v, event) -> true;
    private Handler longClickHandler = new Handler();
    private Runnable longClickRunnable = new Runnable() {
        @Override
        public void run() {
            if (shouldPerformLongClick) {
                if (performLongClick()) {
                    longClickPerformed = true;
                    setPressed(false);
                }
            }
        }
    };
    private Animation.AnimationListener collapseListener = new Animation.AnimationListener() {
        @Override
        public void onAnimationStart(Animation animation) {
        }

        @Override
        public void onAnimationEnd(Animation animation) {
            Timber.e("hachung :onAnimationEnd");
            clickBySwipe();
        }

        @Override
        public void onAnimationRepeat(Animation animation) {
        }
    };
    private WeightAnimation collapseAnim;
    private WeightAnimation expandAnim;
    private long duration = 15;
    private TimeUnit timeUnit = TimeUnit.MINUTES;

    public ChildSwipeLayout(Context context) {
        this(context, null);
    }

    public ChildSwipeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        tinyDB = new TinyDB(getContext());
        fullSwipeEdgePadding = getResources().getDimensionPixelSize(R.dimen.full_swipe_edge_padding);
        if (attrs != null) {
            setUpAttrs(attrs);
        }
        setUpView();
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        setAutoHideSwipe(autoHideSwipe);
        setOnlyOneSwipe(onlyOneSwipe);
    }

    @Override
    protected void onDetachedFromWindow() {
        setItemState(ITEM_STATE_COLLAPSED, false);
        super.onDetachedFromWindow();
    }

    @Override
    public void addView(View child, int index, ViewGroup.LayoutParams params) {
        if (mainLayout != null) super.addView(child, index, params);
        else {
            mainLayout = child;
            setUpView();
        }
    }

    public void setColorLightDark() {
        PorterDuff.Mode mode = PorterDuff.Mode.SRC_ATOP;
//        if (tinyDB.getInt(STYLE_SELECTED, LIGHT) == LIGHT) {
//            int bg_ripple_notyv = R.drawable.bg_ripple_noty;
//            int color_title = ContextCompat.getColor(getContext(), R.color.color_title);
//            int bg_while_ripple_select_setting = R.drawable.bg_while_ripple_select_setting;
//
//            layoutNoti.setBackgroundResource(bg_ripple_notyv);
//            layoutSnoozed.setBackgroundResource(bg_ripple_notyv);
//            layoutSettingNoty.setBackgroundResource(bg_ripple_notyv);
//            tvTimeNoty.setTextColor(color_gray_waning);
//            detailName1.setTextColor(color_title);
//            detailName2.setTextColor(color_title);
//            tvMsg.setTextColor(color_gray_waning);
//            tvTitle.setTextColor(color_title);
//            name1.setTextColor(color_title);
//            name2.setTextColor(color_title);
//            tvNameNoty.setTextColor(color_title);
//            tvNameApp.setTextColor(color_title);
//            detailWaning.setTextColor(color_gray_waning);
//            detailSilent.setTextColor(color_gray_waning);
//            tv15Minutes.setTextColor(color_gray_waning);
//            tv30Minutes.setTextColor(color_gray_waning);
//            tv1Hours.setTextColor(color_gray_waning);
//            tv1Day.setTextColor(color_gray_waning);
//            textTimeSnoozed.setTextColor(color_title);
//            imgState.setColorFilter(color_gray_waning, mode);
//            imgStateSnoozed.setColorFilter(color_gray_waning, mode);
//            imgGoSettingNotification.setColorFilter(color_gray_waning, mode);
//            imgStateSnoozed.setColorFilter(color_gray_waning, mode);
//            tv1Day.setBackgroundResource(bg_while_ripple_select_setting);
//            tv15Minutes.setBackgroundResource(bg_while_ripple_select_setting);
//            tv30Minutes.setBackgroundResource(bg_while_ripple_select_setting);
//            tv1Hours.setBackgroundResource(bg_while_ripple_select_setting);
//
//        } else {
//            int bg_ripple_noty_dark = R.drawable.bg_ripple_noty_dark;
//            int colorPrimary = ContextCompat.getColor(getContext(), R.color.colorPrimary);
//            int color_date = ContextCompat.getColor(getContext(), R.color.color_date);
//            int bg_while_ripple_select_setting_dark = R.drawable.bg_while_ripple_select_setting_dark;
//            layoutNoti.setBackgroundResource(bg_ripple_noty_dark);
//            layoutSnoozed.setBackgroundResource(bg_ripple_noty_dark);
//            layoutSettingNoty.setBackgroundResource(bg_ripple_noty_dark);
//            tvTimeNoty.setTextColor(colorPrimary);
//            detailName1.setTextColor(colorPrimary);
//            detailName2.setTextColor(colorPrimary);
//            tvMsg.setTextColor(colorPrimary);
//            tvTitle.setTextColor(colorPrimary);
//            name1.setTextColor(colorPrimary);
//            name2.setTextColor(colorPrimary);
//            tvNameNoty.setTextColor(colorPrimary);
//            tvNameApp.setTextColor(colorPrimary);
//            detailWaning.setTextColor(color_date);
//            detailSilent.setTextColor(color_date);
//            tv15Minutes.setTextColor(colorPrimary);
//            tv30Minutes.setTextColor(colorPrimary);
//            tv1Hours.setTextColor(colorPrimary);
//            tv1Day.setTextColor(colorPrimary);
//            textTimeSnoozed.setTextColor(colorPrimary);
//            imgState.setColorFilter(colorPrimary, mode);
//            imgStateSnoozed.setColorFilter(colorPrimary, mode);
//            imgGoSettingNotification.setColorFilter(colorPrimary, mode);
//            imgStateSnoozed.setColorFilter(colorPrimary, mode);
//            tv1Day.setBackgroundResource(bg_while_ripple_select_setting_dark);
//            tv15Minutes.setBackgroundResource(bg_while_ripple_select_setting_dark);
//            tv30Minutes.setBackgroundResource(bg_while_ripple_select_setting_dark);
//            tv1Hours.setBackgroundResource(bg_while_ripple_select_setting_dark);
//        }
        int bg_ripple_notyv = R.drawable.bg_ripple_noty;
        int color_title = ContextCompat.getColor(getContext(), R.color.color_title);
        int bg_while_ripple_select_setting = R.drawable.bg_while_ripple_select_setting;

        layoutNoti.setBackgroundResource(bg_ripple_notyv);
        layoutSnoozed.setBackgroundResource(bg_ripple_notyv);
        layoutSettingNoty.setBackgroundResource(bg_ripple_notyv);
        tvTimeNoty.setTextColor(color_gray_waning);
        detailName1.setTextColor(color_title);
        detailName2.setTextColor(color_title);
        tvMsg.setTextColor(color_gray_waning);
        tvTitle.setTextColor(color_title);
        name1.setTextColor(color_title);
        name2.setTextColor(color_title);
        tvNameNoty.setTextColor(color_title);
        tvNameApp.setTextColor(color_title);
        detailWaning.setTextColor(color_gray_waning);
        detailSilent.setTextColor(color_gray_waning);
        tv15Minutes.setTextColor(color_gray_waning);
        tv30Minutes.setTextColor(color_gray_waning);
        tv1Hours.setTextColor(color_gray_waning);
        tv1Day.setTextColor(color_gray_waning);
        textTimeSnoozed.setTextColor(color_title);
        imgState.setColorFilter(color_gray_waning, mode);
        imgStateSnoozed.setColorFilter(color_gray_waning, mode);
        imgGoSettingNotification.setColorFilter(color_gray_waning, mode);
        imgStateSnoozed.setColorFilter(color_gray_waning, mode);
        tv1Day.setBackgroundResource(bg_while_ripple_select_setting);
        tv15Minutes.setBackgroundResource(bg_while_ripple_select_setting);
        tv30Minutes.setBackgroundResource(bg_while_ripple_select_setting);
        tv1Hours.setBackgroundResource(bg_while_ripple_select_setting);
        setBgColor();
        setTypeFace(tv15Minutes, tv30Minutes, tv1Hours, tv1Day, name1, detailName1, name2, detailName2, tvNameNoty, tvTitle, tvMsg, textUndo, textTimeSnoozed, tvNameApp, tvTimeNoty, tvWaning, detailWaning, tvSilent, detailSilent, tvDone, tvOffNotification);
    }

    public void setBgColor() {
        String color = "#FFFFFF";
        if (ThemeHelper.itemControl.getIdCategory() == Constant.VALUE_SHADE) {
            if (ThemeHelper.itemControl.getMiShade() != null && ThemeHelper.itemControl.getMiShade().getBackgroundColorNoty() != null) {
                color = ThemeHelper.itemControl.getMiShade().getBackgroundColorNoty();
            }
        } else if (ThemeHelper.itemControl.getIdCategory() == Constant.VALUE_PIXEL) {
            if (ThemeHelper.itemControl.getPixel() != null && ThemeHelper.itemControl.getPixel().getBackgroundSelectControl() != null) {
                color = ThemeHelper.itemControl.getPixel().getBackgroundSelectControl();
            }
        }
        layoutNoti.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor(color)));
        layoutSnoozed.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor(color)));
        layoutSettingNoty.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor(color)));
    }


    public void setTypeFace(TextView... textViews) {
        Typeface typeface = Typeface.createFromAsset(getContext().getAssets(), Constant.FOLDER_FONT_CONTROL_ASSETS + ThemeHelper.itemControl.getFont());
        for (TextView textView : textViews) {
            textView.setTypeface(typeface);
        }
    }

    public void setCallBackUpdateHeight(NotyCenterViewOS.OnNotyCenterCloseListener onNotyCenterCloseListener, int pos, NotyModel notyModel, ITouchItemView iTouchItemView) {
        this.pos = pos;
        this.onNotyCenterCloseListener = onNotyCenterCloseListener;
        this.iTouchItemView = iTouchItemView;
        notiModel = notyModel;

        if (notiModel.getState() == NotyGroup.STATE.SNOOZED || notiModel.getState() == NotyGroup.STATE.EXPANDSNOOZED) {
            groupDetail.setVisibility(INVISIBLE);
            groupDetailExpand.setVisibility(INVISIBLE);
        } else {
            setUpText();
        }
    }

    private void setUpCountDown() {
        countDownTimer = new CountDownTimer(8000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {

            }

            @Override
            public void onFinish() {
                Timber.e("onFinish: " + duration + timeUnit);
                if (notiModel == null) {
                    return;
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    if (NotificationListener.getInstance() != null) {
                        NotificationListener.getInstance().snoozeNoti(notiModel.getKeyNoty(), DensityUtils.timeUnitToDuration(timeUnit, duration));
                    }
                } else {
                    Data.Builder data = new Data.Builder();
                    NotyGroup notySnoozed = new NotyGroup();
                    notySnoozed.setGroupKey(notiModel.getGroupKey());
                    notySnoozed.setPackageName(notiModel.getPakage());

                    ArrayList<NotyModel> listNoti = new ArrayList<>();
                    listNoti.add(notiModel);
                    notySnoozed.setNotyModels(listNoti);


                    String keyNotySnoozed = System.currentTimeMillis() + "";

                    Hawk.put(keyNotySnoozed, new DensityUtils().serializeToJson(notySnoozed));

                    data.putString(Constant.KEY_PUT_NOTY_GROUP, keyNotySnoozed);

                    OneTimeWorkRequest workSnoozed = new OneTimeWorkRequest
                            .Builder(WorkSnoozedNotification.class)
                            .setInitialDelay(duration, timeUnit)
                            .addTag(getContext().getString(R.string.text_snoozed))
                            .setInputData(data.build())
                            .build();

                    WorkManager workManager = WorkManager.getInstance(getContext());
                    workManager.enqueue(workSnoozed);

                    if (NotificationListener.getInstance() != null) {
                        NotificationListener.getInstance().deleteNoty(notiModel.getPakage(), notiModel.getIdNoty(), notiModel.getKeyNoty());
                    }
                }


            }

        };
    }

    private void setUpText() {
        String nameApp = MethodUtils.getAppNameFromPackageName(getContext(), notiModel.getPakage());

        tvTitle.setText(notiModel.getTitle());
        tvMsg.setText(notiModel.getContent());
        tvTimeNoty.setText(MethodUtils.getTimeAgo(getContext(), notiModel.getTime()));
        tvNameApp.setText(nameApp);
        tvNameNoty.setText(nameApp);
        isShowIconExpaned = false;
        groupDetail.setVisibility(View.VISIBLE);
        groupDetailExpand.setVisibility(View.INVISIBLE);
        tvMsg.post(() -> {
            checkEllipsis(tvTitle);
            checkEllipsis(tvMsg);
            if (isShowIconExpaned || isCollapse) {
                imgState.setVisibility(View.VISIBLE);
            } else {
                imgState.setVisibility(View.INVISIBLE);
            }
            if (notiModel.getState() == NotyGroup.STATE.NONE) {
                setImgState(true);
                tvTitle.setEllipsize(TextUtils.TruncateAt.END);
                tvTitle.setMaxLines(1);
                tvMsg.setMaxLines(1);
                tvMsg.setEllipsize(TextUtils.TruncateAt.END);
            } else {
                setImgState(false);
                tvTitle.setEllipsize(null);
                tvMsg.setEllipsize(null);
                tvMsg.setMaxLines(100);
                tvTitle.setMaxLines(100);
            }
        });

    }

    private void setImgState(boolean b) {
        if (b) {
            imgState.setImageResource(resourceExpand);
        } else {
            imgState.setImageResource(resourceCollapse);
        }
    }

    private void setUpView() {
        if (layoutId != -NO_ID) {
            mainLayout = LayoutInflater.from(getContext()).inflate(layoutId, null);
        }
        if (mainLayout != null) {
            compareArrays(rightColors, rightIcons);
            compareArrays(rightIconColors, rightIcons);

            addView(mainLayout);

            createItemLayouts();
            mainLayout.bringToFront();
            mainLayout.setOnTouchListener(this);
            findView();
            setColorLightDark();
            setClick();
        }
    }

    private void setTextViewDrawableColor(TextView textView, int color) {
        for (Drawable drawable : textView.getCompoundDrawables()) {
            if (drawable != null) {
                drawable.setColorFilter(new PorterDuffColorFilter(color, PorterDuff.Mode.SRC_IN));
            }
        }
    }

    private void setClickOnOffNotification(boolean b) {
        int resourceCollapse = ContextCompat.getColor(getContext(), R.color.color_undo);
        int boder_gray = R.drawable.boder_gray;
        int boder_blue = R.drawable.boder_blue;
        if (b) {
            tvDone.setText(getContext().getString(R.string.text_done));
            bgWaning.setBackgroundResource(boder_blue);
            bgSilent.setBackgroundResource(boder_gray);
            tvWaning.setTextColor(resourceCollapse);
            tvSilent.setTextColor(color_gray_waning);
            setTextViewDrawableColor(tvWaning, resourceCollapse);
            setTextViewDrawableColor(tvSilent, color_gray_waning);
            detailWaning.setVisibility(VISIBLE);
            detailSilent.setVisibility(GONE);
//      tvWaning.setCompoundDrawablesWithIntrinsicBounds(R.drawable.icon, 0, 0, 0);
        } else {
            tvDone.setText(getContext().getString(R.string.text_apply));
            bgWaning.setBackgroundResource(boder_gray);
            bgSilent.setBackgroundResource(boder_blue);
            tvWaning.setTextColor(color_gray_waning);
            tvSilent.setTextColor(resourceCollapse);
            setTextViewDrawableColor(tvWaning, color_gray_waning);
            setTextViewDrawableColor(tvSilent, resourceCollapse);
            detailWaning.setVisibility(GONE);
            detailSilent.setVisibility(VISIBLE);
        }
    }

    private void findView() {
        layoutNoti = findViewById(R.id.layoutNoti);
        layoutSnoozed = findViewById(R.id.layoutSnoozed);
        layoutSettingNoty = findViewById(R.id.layoutSettingNoty);
//        groupNoty = findViewById(R.id.groupNoty);
        bgWaning = findViewById(R.id.bgWaning);
        bgSilent = findViewById(R.id.bgSilent);
        tvWaning = findViewById(R.id.tvWaning);
        detailWaning = findViewById(R.id.detailWaning);
        tvSilent = findViewById(R.id.tvSilent);
        detailSilent = findViewById(R.id.detailSilent);
        tvDone = findViewById(R.id.tvDone);
        tvNameApp = findViewById(R.id.tvNameApp);
        groupDetail = findViewById(R.id.groupDetail);
        groupDetailExpand = findViewById(R.id.groupDetailExpand);
        tvNameNoty = findViewById(R.id.tvNameNoty);
        tvTitle = findViewById(R.id.tvTitle);
        tvMsg = findViewById(R.id.tvMsg);
        imgStateSnoozed = findViewById(R.id.imgStateSnoozed);
        textTimeSnoozed = findViewById(R.id.textTimeSnoozed);
        textUndo = findViewById(R.id.textUndo);
        imgState = findViewById(R.id.imgState);
        tv15Minutes = findViewById(R.id.tv15Minutes);
        tv30Minutes = findViewById(R.id.tv30Minutes);
        tv1Hours = findViewById(R.id.tv1Hours);
        tv1Day = findViewById(R.id.tv1Day);
        name1 = findViewById(R.id.name1);
        detailName1 = findViewById(R.id.detailName1);
        name2 = findViewById(R.id.name2);
        detailName2 = findViewById(R.id.detailName2);
        imgThumb = findViewById(R.id.imgThumb);
        imgImage = findViewById(R.id.imgImage);
        tvTimeNoty = findViewById(R.id.tvTimeNoty);
        imgGoSettingNotification = findViewById(R.id.imgGoSettingNotification);
        tvOffNotification = findViewById(R.id.tvOffNotification);
        groupOptionSnoozed = findViewById(R.id.groupOptionSnoozed);
    }

    private void setClick() {
        textUndo.setOnClickListener(v -> {
            setSateView(0);
            setItemState(SwipeLayout.ITEM_STATE_COLLAPSED, true);
            cancelTimerSnoozed();
        });
        imgState.setOnClickListener(imgStateClick);
        imgStateSnoozed.setOnClickListener(v -> {
            if (groupOptionSnoozed.getVisibility() == VISIBLE) {
                imgStateSnoozed.setImageResource(resourceExpand);
                groupOptionSnoozed.setVisibility(GONE);
            } else {
                imgStateSnoozed.setImageResource(resourceCollapse);
                groupOptionSnoozed.setVisibility(VISIBLE);
            }
        });
        tv15Minutes.setOnClickListener(v -> {
            setTextSnoozed(tv15Minutes.getText().toString());
            selectTimeSnoozed(tv15Minutes.getText().toString());
        });
        tv30Minutes.setOnClickListener(v -> {
            setTextSnoozed(tv30Minutes.getText().toString());
            selectTimeSnoozed(tv30Minutes.getText().toString());
        });
        tv1Hours.setOnClickListener(v -> {
            setTextSnoozed(tv1Hours.getText().toString());
            selectTimeSnoozed(tv1Hours.getText().toString());
        });
        tv1Day.setOnClickListener(v -> {
            setTextSnoozed(tv1Day.getText().toString());
            selectTimeSnoozed(tv1Day.getText().toString());
        });
        bgWaning.setOnClickListener(v -> setClickOnOffNotification(true));
        bgSilent.setOnClickListener(v -> setClickOnOffNotification(false));
        tvDone.setOnClickListener(v -> {
            if (tvDone.getText().toString().equals(getContext().getString(R.string.text_done))) {
                setSateView(0);
            } else {
                intentToSettingNotificationApp();
            }
        });
        imgGoSettingNotification.setOnClickListener(v -> intentToSettingNotificationApp());
        tvOffNotification.setOnClickListener(v -> intentToSettingNotificationApp());
        layoutSnoozed.setOnTouchListener(onTouchListener);
        layoutSettingNoty.setOnTouchListener(onTouchListener);

    }

    private void compareArrays(int[] arr1, int[] arr2) {
        if (arr1 != null && arr2 != null) {
            if (arr1.length < arr2.length) {
                throw new IllegalStateException("Drawable array shouldn't be bigger than color array");
            }
        }
    }

    public void invalidateSwipeItems() {
        createItemLayouts();
    }

    private void createItemLayouts() {
        if (rightIcons != null) {
            rightLayoutMaxWidth = itemWidth * rightIcons.length;
            if (rightLinear != null) {
                try {
                    removeView(rightLinear);
                } catch (Exception e) {
                }
            }
            rightLinear = createLinearLayout(Gravity.RIGHT);
            rightLinearWithoutLast = createLinearLayout(Gravity.RIGHT);
            rightLinearWithoutLast.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, rightIcons.length - 1));
            addView(rightLinear);
            rightViews = new View[rightIcons.length];
            rightLinear.addView(rightLinearWithoutLast);
            addSwipeItems(rightIcons, rightIconColors, rightColors, rightTexts, rightTextColors, rightLinear, rightLinearWithoutLast, rightViews, false);
        }
    }

    private void addSwipeItems(int[] icons, int[] iconColors, int[] backgroundColors, String[] texts, int[] textColors,
                               LinearLayout layout, LinearLayout layoutWithout, View[] views, boolean left) {

        for (int i = 0; i < icons.length; i++) {
            int backgroundColor = NO_ID;
            if (backgroundColors != null) {
                backgroundColor = backgroundColors[i];
            }

            int iconColor = NO_ID;
            if (iconColors != null) iconColor = iconColors[i];

            String txt = null;
            if (texts != null) txt = texts[i];

            int textColor = NO_ID;
            if (textColors != null)
                textColor = textColors[i];

            ViewGroup swipeItem = createSwipeItem(icons[i], iconColor, backgroundColor, txt, textColor, left);
            swipeItem.setClickable(true);
            swipeItem.setFocusable(true);
            swipeItem.setOnClickListener(view -> {
                if (rightViews != null) {
                    for (int i1 = 0; i1 < rightViews.length; i1++) {
                        View v = rightViews[i1];
                        if (v == view) {
                            if (rightViews.length == 1 || getViewWeight(rightLinearWithoutLast) > 0)
                                if (i1 == 0) {
                                    setSateView(2);
                                } else {
                                    textTimeSnoozed.setText(getContext().getString(R.string.text_15_minutes));
                                    setSateView(1);
                                    duration = 15;
                                    timeUnit = TimeUnit.MINUTES;
                                    startTimerSnoozed();
                                }
                            setItemState(SwipeLayout.ITEM_STATE_COLLAPSED, true);
                            break;
                        }
                    }
                }
            });
            views[i] = swipeItem;
            if (i == icons.length - (!left ? 1 : icons.length)) {
                layout.addView(swipeItem);
            } else {
                layoutWithout.addView(swipeItem);
            }
        }
    }

    private void setSateView(int i) {
        layoutSnoozed.setVisibility(GONE);
        layoutNoti.setVisibility(GONE);
        layoutSettingNoty.setVisibility(GONE);
        switch (i) {
            case 0:
                layoutNoti.setVisibility(VISIBLE);
                break;
            case 1:
                layoutSnoozed.setVisibility(VISIBLE);
                break;
            case 2:
                layoutSettingNoty.setVisibility(VISIBLE);
                break;

        }
    }

    public void setAlphaAtIndex(boolean left, int index, float alpha) {
        View[] views = rightViews;
        if (index <= views.length - 1) {
            views[index].setAlpha(alpha);
        }
    }

    public void setEnableAtIndex(boolean left, int index, boolean enabled) {
        View[] views = rightViews;
        if (index <= views.length - 1) {
            views[index].setEnabled(enabled);
        }
    }

    public float getAlphaAtIndex(boolean left, int index) {
        View[] views = rightViews;
        if (index <= views.length - 1) {
            return views[index].getAlpha();
        }
        return 1;
    }

    public boolean isEnabledAtIndex(boolean left, int index) {
        View[] views = rightViews;
        if (index <= views.length - 1) {
            return views[index].isEnabled();
        }
        return true;
    }

    public View[] getRightViews() {
        return rightViews;
    }

    @Override
    public void setOnClickListener(OnClickListener l) {
        mainLayout.setOnClickListener(l);
    }

    private Drawable getRippleDrawable() {
        int[] attrs = new int[]{android.R.attr.selectableItemBackground};
        TypedArray ta = getContext().obtainStyledAttributes(attrs);
        Drawable ripple = ta.getDrawable(0);
        ta.recycle();
        return ripple;
    }

    private ViewGroup createSwipeItem(int icon, int iconColor, int backgroundColor, String text, int textColor, boolean left) {
        FrameLayout frameLayout = new FrameLayout(getContext());
        frameLayout.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, 1f));
        if (Build.VERSION.SDK_INT >= 16) {
            View view = new View(getContext());
            view.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            view.setBackground(getRippleDrawable());
            frameLayout.addView(view);
        }
        if (backgroundColor != NO_ID) {
            frameLayout.setBackgroundColor(backgroundColor);
        }

        ImageView imageView = new ImageView(getContext());
        Drawable drawable = ContextCompat.getDrawable(getContext(), icon);
        if (iconColor != NO_ID) {
            drawable = setTint(drawable, iconColor);
        }
        imageView.setImageDrawable(drawable);

        RelativeLayout relativeLayout = new RelativeLayout(getContext());
        int gravity = Gravity.CENTER_VERTICAL;
        if (left) {
            gravity |= Gravity.RIGHT;
        } else {
            gravity |= Gravity.LEFT;
        }
        relativeLayout.setLayoutParams(new LayoutParams(itemWidth, ViewGroup.LayoutParams.WRAP_CONTENT, gravity));

        RelativeLayout.LayoutParams imageViewParams = new RelativeLayout.LayoutParams(iconSize, iconSize);
        imageViewParams.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE);
        imageView.setLayoutParams(imageViewParams);
        imageView.setId(++id);
        relativeLayout.addView(imageView);

        if (text != null) {
            TextView textView = new TextView(getContext());
            textView.setMaxLines(2);

            if (textSize > 0) {
                textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
            }

            if (textColor != NO_ID) {
                textView.setTextColor(textColor);
            }

            textView.setText(text);
            textView.setGravity(Gravity.CENTER);
            RelativeLayout.LayoutParams textViewParams = new RelativeLayout.LayoutParams(itemWidth, ViewGroup.LayoutParams.WRAP_CONTENT);
            textViewParams.addRule(RelativeLayout.BELOW, id);
            textViewParams.topMargin = textTopMargin;
            relativeLayout.addView(textView, textViewParams);

        }
        frameLayout.setOnTouchListener(this);
        frameLayout.addView(relativeLayout);

        return frameLayout;
    }

    private LinearLayout createLinearLayout(int gravity) {
        LinearLayout linearLayout = new LinearLayout(getContext());
        linearLayout.setOrientation(LinearLayout.HORIZONTAL);
        LayoutParams params = new LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT);
        params.gravity = gravity;
        linearLayout.setLayoutParams(params);
        return linearLayout;
    }

    private void setUpAttrs(AttributeSet attrs) {
        final TypedArray array = getContext().obtainStyledAttributes(attrs, R.styleable.SwipeLayout);
        if (array != null) {
            layoutId = array.getResourceId(R.styleable.SwipeLayout_foregroundLayout, NO_ID);
            itemWidth = array.getDimensionPixelSize(R.styleable.SwipeLayout_swipeItemWidth, 100);
            iconSize = array.getDimensionPixelSize(R.styleable.SwipeLayout_iconSize, ViewGroup.LayoutParams.MATCH_PARENT);
            textSize = array.getDimensionPixelSize(R.styleable.SwipeLayout_textSize, NO_ID);
            textTopMargin = array.getDimensionPixelSize(R.styleable.SwipeLayout_textTopMargin, 20);
            canFullSwipeFromRight = array.getBoolean(R.styleable.SwipeLayout_canFullSwipeFromRight, false);
            onlyOneSwipe = array.getBoolean(R.styleable.SwipeLayout_onlyOneSwipe, true);
            autoHideSwipe = array.getBoolean(R.styleable.SwipeLayout_autoHideSwipe, true);

            int rightColorsRes = array.getResourceId(R.styleable.SwipeLayout_rightItemColors, NO_ID);
            int rightIconsRes = array.getResourceId(R.styleable.SwipeLayout_rightItemIcons, NO_ID);
            int rightTextRes = array.getResourceId(R.styleable.SwipeLayout_rightStrings, NO_ID);
            int rightTextColorRes = array.getResourceId(R.styleable.SwipeLayout_rightTextColors, NO_ID);
            int rightIconColors = array.getResourceId(R.styleable.SwipeLayout_rightIconColors, NO_ID);

            initiateArrays(rightColorsRes, rightIconsRes, rightTextRes, rightTextColorRes, rightIconColors);
            array.recycle();
        }
    }

    private void initiateArrays(int rightColorsRes, int rightIconsRes, int rightTextRes, int rightTextColorRes,
                                int rightIconColorsRes) {
        Resources res = getResources();

        if (rightColorsRes != NO_ID) rightColors = res.getIntArray(rightColorsRes);
        if (rightIconsRes != NO_ID && !isInEditMode())
            rightIcons = new int[2];
        rightIcons[0] = tinyDB.getInt(STYLE_SELECTED, LIGHT) == LIGHT ? R.drawable.ic_rcc_noti_setting : R.drawable.ic_rcc_noti_setting_dark;
        rightIcons[1] = tinyDB.getInt(STYLE_SELECTED, LIGHT) == LIGHT ? R.drawable.ic_rcc_noti : R.drawable.ic_rcc_noti_dark;
        if (rightTextRes != NO_ID) rightTexts = res.getStringArray(rightTextRes);
        if (rightTextColorRes != NO_ID) rightTextColors = res.getIntArray(rightTextColorRes);
        if (rightIconColorsRes != NO_ID) rightIconColors = res.getIntArray(rightIconColorsRes);
    }

    public void setRightColors(int[] rightColors) {
        this.rightColors = rightColors;
    }

    public void setRightIcons(int[] rightIcons) {
        this.rightIcons = rightIcons;
    }

    public void setRightIconColors(int[] rightIconColors) {
        this.rightIconColors = rightIconColors;
    }

    public void setRightTextColors(int[] rightTextColors) {
        this.rightTextColors = rightTextColors;
    }

    public void setRightTexts(String[] rightTexts) {
        this.rightTexts = rightTexts;
    }

    private int[] fillDrawables(TypedArray ta) {
        int[] drawableArr = new int[ta.length()];
        for (int i = 0; i < ta.length(); i++) {
            drawableArr[i] = ta.getResourceId(i, NO_ID);
        }
        ta.recycle();
        return drawableArr;
    }

    private void clearAnimations() {
        mainLayout.clearAnimation();

        if (rightLinear != null)
            rightLinear.clearAnimation();
        if (rightLinearWithoutLast != null)
            rightLinearWithoutLast.clearAnimation();
    }

    @Override
    public void setPressed(boolean pressed) {
        super.setPressed(pressed);
        if (Build.VERSION.SDK_INT >= 21)
            drawableHotspotChanged(downX, downY);
    }

    private void clickBySwipe() {
        if (NotificationListener.getInstance() != null) {
            Timber.e("hachung isDelete:"+notiModel.isDelete());
            if (notiModel.isDelete()) {
                NotificationListener.getInstance().deleteNoty(notiModel.getPakage(), notiModel.getIdNoty(), notiModel.getKeyNoty());
            } else {
                collapseItem(true);
            }
        }
    }

    @Override
    public boolean onTouch(View view, MotionEvent event) {
        if (swipeEnabled && rightIcons != null) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    downX = event.getX();
                    downY = event.getY();
                    downTime = lastTime = System.currentTimeMillis();
                    downRawX = prevRawX = event.getRawX();
                    if (ViewCompat.getTranslationX(mainLayout) == 0) {
                        if (rightLinearWithoutLast != null) {
                            Utils.setViewWeight(rightLinearWithoutLast, rightViews.length - 1);
                        }
                    }
                    return true;

                case MotionEvent.ACTION_MOVE:
                    if (Math.abs(prevRawX - event.getRawX()) < 20 && !movementStarted) {
                        if (System.currentTimeMillis() - lastTime >= 50 && !isPressed() && !isExpanding() && !longClickPerformed) {
                            view.setPressed(true);

                            if (!shouldPerformLongClick) {
                                shouldPerformLongClick = true;
                                longClickHandler.removeCallbacks(longClickRunnable);
                                longClickHandler.postDelayed(longClickRunnable, ViewConfiguration.getLongPressTimeout());
                            }
                        }
                        return false;
                    }

                    if (view.isPressed()) view.setPressed(false);

                    shouldPerformLongClick = false;
                    movementStarted = true;
                    collapseOthersIfNeeded();

                    clearAnimations();

                    directionLeft = prevRawX - event.getRawX() > 0;
                    float delta = Math.abs(prevRawX - event.getRawX());
                    speed = (System.currentTimeMillis() - lastTime) / delta;

                    int rightLayoutWidth = 0;
                    int leftLayoutWidth = 0;

                    if (directionLeft) {
                        float left = ViewCompat.getTranslationX(mainLayout) - delta;

                        if (left < -rightLayoutMaxWidth) {
                            if (!canFullSwipeFromRight) {
                                left = -rightLayoutMaxWidth;
                            } else if (left < -getWidth()) {
                                left = -getWidth();
                            }
                        }

                        if (canFullSwipeFromRight) {
                            if (ViewCompat.getTranslationX(mainLayout) <= -(getWidth() - fullSwipeEdgePadding)) {
                                if (getViewWeight(rightLinearWithoutLast) > 0 &&
                                        (collapseAnim == null || collapseAnim.hasEnded())) {

                                    view.setPressed(false);
                                    rightLinearWithoutLast.clearAnimation();

                                    if (expandAnim != null) expandAnim = null;

                                    collapseAnim = new WeightAnimation(0, rightLinearWithoutLast);
                                    startAnimation(collapseAnim);
                                }
                            } else {
                                if (getViewWeight(rightLinearWithoutLast) < rightIcons.length - 1F &&
                                        (expandAnim == null || expandAnim.hasEnded())) {
                                    view.setPressed(false);
                                    rightLinearWithoutLast.clearAnimation();

                                    if (collapseAnim != null) collapseAnim = null;

                                    expandAnim = new WeightAnimation(rightIcons.length - 1, rightLinearWithoutLast);
                                    startAnimation(expandAnim);
                                }
                            }
                        }
                        if (iTouchItemView != null) {
                            iTouchItemView.onHorizontalScroll();
                        }
                        ViewCompat.setTranslationX(mainLayout, left);

                        if (rightLinear != null) {
                            rightLayoutWidth = (int) Math.abs(left);
                            setViewWidth(rightLinear, rightLayoutWidth);
                        }


                    } else {
                        float right = ViewCompat.getTranslationX(mainLayout) + delta;
                        if (right > 0) return true;
                        if (iTouchItemView != null) {
                            iTouchItemView.onHorizontalScroll();
                        }
                        ViewCompat.setTranslationX(mainLayout, right);


                        if (rightLinear != null) {
                            rightLayoutWidth = (int) Math.abs(ViewCompat.getTranslationX(mainLayout));
                            setViewWidth(rightLinear, rightLayoutWidth);
                        }
                    }

                    if (Math.abs(ViewCompat.getTranslationX(mainLayout)) > itemWidth / 5 && getParent() != null) {
                        getParent().requestDisallowInterceptTouchEvent(true);
                    }
                    prevRawX = event.getRawX();
                    lastTime = System.currentTimeMillis();
                    return true;

                case MotionEvent.ACTION_UP:
                    finishMotion(event);
                    if (movementStarted) {
                        finishSwipeAnimated();
                    } else {
                        view.setPressed(false);
                        if (System.currentTimeMillis() - downTime < ViewConfiguration.getTapTimeout()) {
                            view.setPressed(true);
                            view.performClick();
                            view.setPressed(false);
                        }
                    }

                    return false;
                case MotionEvent.ACTION_CANCEL:
                    finishMotion(event);
                    if (movementStarted)
                        finishSwipeAnimated();
                    return false;
            }

        }
        return false;
    }

    private void collapseOthersIfNeeded() {
        if (!onlyOneSwipe) return;
        ViewParent parent = getParent();
        if (parent != null && parent instanceof RecyclerView) {
            RecyclerView recyclerView = (RecyclerView) parent;
            int count = recyclerView.getChildCount();
            for (int i = 0; i < count; i++) {
                View item = recyclerView.getChildAt(i);
                if (item != this && item instanceof SwipeLayout) {
                    SwipeLayout swipeLayout = (SwipeLayout) item;
                    if (ViewCompat.getTranslationX(swipeLayout.getSwipeableView()) != 0 && !swipeLayout.inAnimatedState()) {
                        swipeLayout.setItemState(ITEM_STATE_COLLAPSED, true);
                    }
                }
            }
        }
    }

    public View getSwipeableView() {
        return mainLayout;
    }

    private void finishMotion(MotionEvent event) {
        directionLeft = event.getRawX() - downRawX < 0;

        longClickHandler.removeCallbacks(longClickRunnable);
        shouldPerformLongClick = false;
        longClickPerformed = false;
    }

    private void finishSwipeAnimated() {
        shouldPerformLongClick = false;
        setPressed(false);
        if (getParent() != null) {
            getParent().requestDisallowInterceptTouchEvent(false);
        }
        movementStarted = false;
        LinearLayout animateView = null;
        boolean left = false;
        int requiredWidth = 0;

        if (ViewCompat.getTranslationX(mainLayout) < 0) {
            left = false;
            animateView = rightLinear;
            if (rightLinear != null) {

                int reqWidth = directionLeft ? rightLayoutMaxWidth / 3 : (rightLayoutMaxWidth - (rightLayoutMaxWidth / 3));

                if (rightLinear.getWidth() >= reqWidth) {
                    requiredWidth = rightLayoutMaxWidth;
                }

                if (requiredWidth == rightLayoutMaxWidth && directionLeft) {
                    if (ViewCompat.getTranslationX(mainLayout) <= -(getWidth() - fullSwipeEdgePadding)) {
                        requiredWidth = getWidth();
                        invokedFromLeft = false;
                    }
                }

                ViewCompat.setTranslationX(mainLayout, -rightLinear.getWidth());
            }
        }
        long duration = (long) (100 * speed);

        if (animateView != null) {
            SwipeAnimation swipeAnim = new SwipeAnimation(animateView, requiredWidth, mainLayout, left);

            if (duration < ANIMATION_MIN_DURATION) duration = ANIMATION_MIN_DURATION;
            else if (duration > ANIMATION_MAX_DURATION) duration = ANIMATION_MAX_DURATION;
            swipeAnim.setDuration(duration);

            LinearLayout layoutWithout = rightLinearWithoutLast;
            View[] views = rightViews;
            invokedFromLeft = false;

            if (requiredWidth == getWidth()) {
                if (getViewWeight(layoutWithout) == 0 && getWidth() != Math.abs(ViewCompat.getTranslationX(mainLayout)))
                    swipeAnim.setAnimationListener(collapseListener);
                else if (collapseAnim != null && !collapseAnim.hasEnded()) {
                    collapseAnim.setAnimationListener(collapseListener);
                } else if (getViewWeight(layoutWithout) == 0 || getWidth() == Math.abs(ViewCompat.getTranslationX(mainLayout))) {
                    Timber.e("hachung :clickBySwipe");
                    clickBySwipe();
                } else {
                    layoutWithout.clearAnimation();
                    if (collapseAnim != null) collapseAnim.cancel();
                    collapseAnim = new WeightAnimation(0, layoutWithout);
                    collapseAnim.setAnimationListener(collapseListener);
                    layoutWithout.startAnimation(collapseAnim);
                }
            } else {
                WeightAnimation weightAnimation = new WeightAnimation(views.length - 1, layoutWithout);
                layoutWithout.startAnimation(weightAnimation);
            }

            animateView.startAnimation(swipeAnim);
        }
    }

    @Deprecated
    public void closeItem() {
        collapseItem(true);
    }

    private void collapseItem(boolean animated) {
        if (rightLinear != null && rightLinear.getWidth() > 0) {
            setViewWidth(rightLinearWithoutLast, rightViews.length - 1);

            if (animated) {
                SwipeAnimation swipeAnim = new SwipeAnimation(rightLinear, 0, mainLayout, false);
                rightLinear.startAnimation(swipeAnim);
            } else {
                ViewCompat.setTranslationX(mainLayout, 0);
                setViewWidth(rightLinear, 0);
            }
        }
    }

    private void checkEllipsis(TextView textView) {
        if (isShowIconExpaned) return;
        if (textView == null || textView.getLayout() == null || textView.getLayout().getText() == null) {
            isShowIconExpaned = false;
            return;
        }
        isShowIconExpaned = !(textView.getLayout().getText().toString()).equals(textView.getText().toString());
    }

    public void setItemState(int state, boolean animated) {
        switch (state) {
            case ITEM_STATE_COLLAPSED:
                collapseItem(animated);
                break;
            case ITEM_STATE_RIGHT_EXPAND:
                int requiredWidthRight = rightIcons.length * itemWidth;
                if (animated) {
                    SwipeAnimation swipeAnim = new SwipeAnimation(rightLinear, requiredWidthRight, mainLayout, false);
                    rightLinear.startAnimation(swipeAnim);
                } else {
                    ViewCompat.setTranslationX(mainLayout, -requiredWidthRight);
                    setViewWidth(rightLinear, requiredWidthRight);
                }
                break;
        }

    }

    public boolean isSwipeEnabled() {
        return swipeEnabled;
    }

    public void setSwipeEnabled(boolean enabled) {
        swipeEnabled = enabled;
    }

    public boolean inAnimatedState() {
        if (rightLinear != null) {
            Animation anim = rightLinear.getAnimation();
            if (anim != null && !anim.hasEnded()) return true;
        }
        return false;
    }

    public void setAutoHideSwipe(boolean autoHideSwipe) {
        this.autoHideSwipe = autoHideSwipe;
        ViewParent parent = getParent();
        if (parent != null && parent instanceof RecyclerView) {
            RecyclerView recyclerView = (RecyclerView) parent;
            if (onScrollListener != null) recyclerView.removeOnScrollListener(onScrollListener);
            if (autoHideSwipe)
                recyclerView.addOnScrollListener(onScrollListener = new RecyclerView.OnScrollListener() {
                    @Override
                    public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                        super.onScrollStateChanged(recyclerView, newState);
                        if (newState == RecyclerView.SCROLL_STATE_DRAGGING && ViewCompat.getTranslationX(mainLayout) != 0) {
                            setItemState(ITEM_STATE_COLLAPSED, true);
                        }
                    }
                });
        }
    }

    public void setOnlyOneSwipe(boolean onlyOneSwipe) {
        this.onlyOneSwipe = onlyOneSwipe;
    }

    public boolean isLeftExpanding() {
        return ViewCompat.getTranslationX(mainLayout) > 0;
    }

    public boolean isRightExpanding() {
        return ViewCompat.getTranslationX(mainLayout) < 0;
    }

    public boolean isExpanding() {
        return isRightExpanding() || isLeftExpanding();
    }

    public boolean isRightExpanded() {
        return rightLinear != null && rightLinear.getWidth() >= rightLayoutMaxWidth;
    }


    private void setTextSnoozed(String textSnoozed) {
        textTimeSnoozed.setText(textSnoozed);
    }

    private void intentToSettingNotificationApp() {
        Intent intent = new Intent();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            intent.setAction(Settings.ACTION_APP_NOTIFICATION_SETTINGS);
            intent.putExtra(Settings.EXTRA_APP_PACKAGE, notiModel.getPakage());
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            intent.setAction("android.settings.APP_NOTIFICATION_SETTINGS");
            intent.putExtra("app_package", notiModel.getPakage());
            intent.putExtra("app_uid", notiModel.getUid());
        } else {
            intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            intent.addCategory(Intent.CATEGORY_DEFAULT);
            intent.setData(Uri.parse("package:" + notiModel.getPakage()));
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        getContext().startActivity(intent);
        onNotyCenterCloseListener.closeEnd();
    }

    private void selectTimeSnoozed(String s) {
        cancelTimerSnoozed();

        if (s.equals(getContext().getString(R.string.text_30_minutes))) {
            duration = 30;
            timeUnit = TimeUnit.MINUTES;
        } else if (s.equals(getContext().getString(R.string.text_1_house))) {
            duration = 1;
            timeUnit = TimeUnit.HOURS;
        } else if (s.equals(getContext().getString(R.string.text_1_day))) {
            duration = 1;
            timeUnit = TimeUnit.DAYS;
        } else {
            duration = 15;
            timeUnit = TimeUnit.MINUTES;
        }

        startTimerSnoozed();
    }

    private void cancelTimerSnoozed() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
    }


    private void startTimerSnoozed() {
        if (countDownTimer == null) {
            setUpCountDown();
        }
        if (countDownTimer != null) {
            countDownTimer.start();
        }
    }


    private void disableSnoozed() {

    }

    public void collapseAll(boolean animated) {
        ViewParent parent = getParent();
        if (parent != null && parent instanceof RecyclerView) {
            RecyclerView recyclerView = (RecyclerView) parent;
            int count = recyclerView.getChildCount();
            for (int i = 0; i < count; i++) {
                View item = recyclerView.getChildAt(i);
                if (item instanceof SwipeLayout) {
                    SwipeLayout swipeLayout = (SwipeLayout) item;
                    if (ViewCompat.getTranslationX(swipeLayout.getSwipeableView()) != 0) {
                        swipeLayout.setItemState(ITEM_STATE_COLLAPSED, animated);
                    }
                }
            }
        }
    }

    public void setCanFullSwipeFromRight(boolean fullSwipeFromRight) {
        canFullSwipeFromRight = fullSwipeFromRight;
    }
}