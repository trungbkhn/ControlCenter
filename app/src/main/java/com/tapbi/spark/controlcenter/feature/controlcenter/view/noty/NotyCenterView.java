package com.tapbi.spark.controlcenter.feature.controlcenter.view.noty;

import static com.tapbi.spark.controlcenter.common.Constant.FORMAT_SIMPLE_DATE;
import static com.tapbi.spark.controlcenter.common.Constant.LIGHT;
import static com.tapbi.spark.controlcenter.common.Constant.PORTRAIT;
import static com.tapbi.spark.controlcenter.common.Constant.REAL_TIME;
import static com.tapbi.spark.controlcenter.common.Constant.STYLE_SELECTED;
import static com.tapbi.spark.controlcenter.common.Constant.TRANSPARENT;
import static me.everything.android.ui.overscroll.IOverScrollState.STATE_IDLE;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.text.format.DateUtils;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.Group;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.orhanobut.hawk.Hawk;
import com.tapbi.spark.controlcenter.App;
import com.tapbi.spark.controlcenter.R;
import com.tapbi.spark.controlcenter.common.Constant;
import com.tapbi.spark.controlcenter.common.models.ItemAddedNoty;
import com.tapbi.spark.controlcenter.data.repository.ThemeHelper;
import com.tapbi.spark.controlcenter.feature.NotyManager;
import com.tapbi.spark.controlcenter.feature.controlcenter.adapter.AdapterNotyMi;
import com.tapbi.spark.controlcenter.feature.controlcenter.cb.ITouchItemView;
import com.tapbi.spark.controlcenter.feature.controlios14.helper.BlurBackground;
import com.tapbi.spark.controlcenter.feature.controlios14.model.NotyGroup;
import com.tapbi.spark.controlcenter.feature.controlios14.model.NotyModel;
import com.tapbi.spark.controlcenter.feature.controlios14.view.PermissionNotificationView;
import com.tapbi.spark.controlcenter.feature.controlios14.view.noty.NotyCenterViewOS;
import com.tapbi.spark.controlcenter.interfaces.ListenerAnim;
import com.tapbi.spark.controlcenter.service.NotificationListener;
import com.tapbi.spark.controlcenter.service.NotyControlCenterServicev614;
import com.tapbi.spark.controlcenter.utils.DensityUtils;
import com.tapbi.spark.controlcenter.utils.StringUtils;
import com.tapbi.spark.controlcenter.utils.TinyDB;
import com.tapbi.spark.controlcenter.utils.helper.rcvhepler.NpaLinearLayoutManager;
import com.tapbi.spark.controlcenter.views.ReverseInterpolator;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

import me.everything.android.ui.overscroll.IOverScrollDecor;
import me.everything.android.ui.overscroll.IOverScrollStateListener;
import me.everything.android.ui.overscroll.IOverScrollUpdateListener;
import me.everything.android.ui.overscroll.OverScrollDecoratorHelper;

public class NotyCenterView extends ConstraintLayout implements ITouchItemView {
    private final float spaceSwipeHideVertical = App.widthHeightScreenCurrent.h / 20f;
    private final float spaceSwipeHideHorizontal = App.widthHeightScreenCurrent.w / 20f;
    private NotyCenterViewOS.OnNotyCenterCloseListener onNotyCenterCloseListener;
    private ConstraintLayout parentChild;
    private SimpleDateFormat sdf;
    private TextView tvTime, tvDate;
    private ImageView imgGoToNotificationAccess, btnClearNoty, bg;
    private RecyclerView rccNoty;
    private Group groupNoty;
    private AdapterNotyMi adapterNotyMi;
    private TinyDB tinyDB;
    private IOverScrollDecor decorRcc = null;
    private IOverScrollDecor decorLayoutParent = null;
    private boolean readyClose = false;
    private final IOverScrollStateListener iOverScrollStateListener = new IOverScrollStateListener() {
        @Override
        public void onOverScrollStateChange(IOverScrollDecor decor, int oldState, int newState) {
            if (newState == STATE_IDLE && readyClose) {
                readyClose = false;
                onNotyCenterCloseListener.closeEnd();
                setAlpha(1f);
                decorRcc.detach();
                decorLayoutParent.detach();

            }
        }

        @Override
        public void onScrollStateCancel() {
            setAlpha(1f);
        }
    };
    private PermissionNotificationView permissionNotificationView;
    private Context mContext;
    private int orientation;
    private final IOverScrollUpdateListener iOverScrollUpdateListener = new IOverScrollUpdateListener() {
        @Override
        public void onOverScrollUpdate(IOverScrollDecor decor, int state, float offset) {
            if (!readyClose) {
                float distanceDownY;
                if (orientation == Configuration.ORIENTATION_PORTRAIT) {
                    distanceDownY = (offset) / -spaceSwipeHideVertical * 100;
                } else {
                    distanceDownY = (offset) / -spaceSwipeHideHorizontal * 100;
                }
                float percentOfNumberOne = distanceDownY / 100;
                setAlpha(1f - percentOfNumberOne);
            }
            if (orientation == Configuration.ORIENTATION_PORTRAIT ? offset < -spaceSwipeHideVertical : offset < -spaceSwipeHideHorizontal) {
                readyClose = true;
            }

        }


    };

    public NotyCenterView(@NonNull Context context) {
        super(context);
        init(context);
    }

    public NotyCenterView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public NotyCenterView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        mContext = context;
        tinyDB = new TinyDB(getContext());
        sdf = new SimpleDateFormat(FORMAT_SIMPLE_DATE, Locale.getDefault());
        //orientation = getResources().getConfiguration().orientation;
        orientation = DensityUtils.getOrientationWindowManager(getContext());
        if (orientation == PORTRAIT) {
            LayoutInflater.from(getContext()).inflate(R.layout.layout_noti_mi_control, this, true);
        } else {
            LayoutInflater.from(getContext()).inflate(R.layout.layout_noti_mi_control_land, this, true);
        }
        findView();
        setClick();
        setUpBg();
        setTextDateTime();
        setUpColorIconClear();
        updateStatusNotificationAccess();
    }


    public void setOnNotyCenterCloseListener(NotyCenterViewOS.OnNotyCenterCloseListener onNotyCenterCloseListener) {
        this.onNotyCenterCloseListener = onNotyCenterCloseListener;
        setUpAdapter();
        setUpRcc();
        setUpOverScroll();
    }

    public void setUpColorIconClear() {
        if (tinyDB.getInt(STYLE_SELECTED, LIGHT) == LIGHT) {
            btnClearNoty.setColorFilter(ContextCompat.getColor(getContext(), R.color.color_title), PorterDuff.Mode.SRC_ATOP);
            btnClearNoty.setBackgroundResource(R.drawable.bg_oval_while_ripple_setting);
        } else {
            btnClearNoty.setColorFilter(ContextCompat.getColor(getContext(), R.color.colorPrimary), PorterDuff.Mode.SRC_ATOP);
            btnClearNoty.setBackgroundResource(R.drawable.bg_oval_while_ripple_setting_dark);
        }
    }

    public void updateColor() {
        setUpColorIconClear();
        setUpAdapter();
        setUpRcc();
    }

    private void setUpAdapter() {
        adapterNotyMi = new AdapterNotyMi(rccNoty, onNotyCenterCloseListener, this);
        adapterNotyMi.setData(NotyManager.INSTANCE.getListNotyGroup());
    }

    private void findView() {
        parentChild = findViewById(R.id.parentChild);
        tvTime = findViewById(R.id.tvTime);
        bg = findViewById(R.id.bg);
        tvDate = findViewById(R.id.tvDate);
        btnClearNoty = findViewById(R.id.btnClearNoty);
        rccNoty = findViewById(R.id.rccNoty);
        groupNoty = findViewById(R.id.groupNoty);
        imgGoToNotificationAccess = findViewById(R.id.imgGoToNotificationAccess);
        if (ThemeHelper.itemControl.getIdCategory() == Constant.VALUE_CONTROL_CENTER) {
            imgGoToNotificationAccess.clearColorFilter();
            if (ThemeHelper.itemControl.getControlCenter() != null && ThemeHelper.itemControl.getControlCenter().getColorIconSettings() != null) {
                tvTime.setTextColor(Color.parseColor(ThemeHelper.itemControl.getControlCenter().getColorIconSettings()));
                imgGoToNotificationAccess.setColorFilter(Color.parseColor(ThemeHelper.itemControl.getControlCenter().getColorIconSettings()));
            }
        } else {
            imgGoToNotificationAccess.setColorFilter(Color.BLACK);
            tvTime.setTextColor(Color.BLACK);
            tvDate.setTextColor(Color.BLACK);
        }
        permissionNotificationView = findViewById(R.id.permissionNotiView);

    }

    private void setClick() {
        parentChild.setOnClickListener(v -> {

        });
        imgGoToNotificationAccess.setOnClickListener(v -> intentSettingNotificationListener());
        btnClearNoty.setOnClickListener(v -> clearAllNoty());
        permissionNotificationView.setClickListener(this::animationHideMain);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {

        switch (ev.getAction()) {
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                animate().alpha(1f).setDuration(200).start();
                break;
        }

        return super.onInterceptTouchEvent(ev);
    }

    private void setUpRcc() {
        if (adapterNotyMi != null) {
            rccNoty.setLayoutManager(new NpaLinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
            rccNoty.setAdapter(adapterNotyMi);
        }
//        rccNoty.setHasFixedSize(true);
//        rccNoty.setItemAnimator(null);
    }

    private void setUpOverScroll() {
        decorRcc = OverScrollDecoratorHelper.setUpOverScroll(rccNoty, OverScrollDecoratorHelper.ORIENTATION_VERTICAL);


        decorRcc.setOverScrollStateListener(iOverScrollStateListener);

        decorRcc.setOverScrollUpdateListener(iOverScrollUpdateListener);

        decorLayoutParent = OverScrollDecoratorHelper.setUpStaticOverScroll(parentChild, OverScrollDecoratorHelper.ORIENTATION_VERTICAL);


        decorLayoutParent.setOverScrollStateListener(iOverScrollStateListener);

        decorLayoutParent.setOverScrollUpdateListener(iOverScrollUpdateListener);
    }

    public void setTextDateTime() {
        String date = sdf.format(System.currentTimeMillis());
        String[] dates = date.split("_");
        tvTime.setText(dates[1]);
        tvDate.setText(StringUtils.INSTANCE.uppercaseFirstCharacters(DateUtils.formatDateTime(getContext(), System.currentTimeMillis(), 18)));

    }

    private void setUpNewNotyListener() {
        if (NotificationListener.getInstance() != null) {
            if (NotificationListener.getInstance().isFirstLoad) {
                NotificationListener.getInstance().loadInFirstUse();
            }
        }
    }


    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        setUpNewNotyListener();
    }

    public void setUpBg() {
        if (bg != null && ThemeHelper.itemControl != null) {
            bg.clearColorFilter();
            String typeBg = ThemeHelper.itemControl.getTypeBackground();
            if (typeBg.equals(TRANSPARENT)) {
                bg.setImageDrawable(null);
            } else if (typeBg.equals(REAL_TIME) && NotyControlCenterServicev614.getInstance() != null && NotyControlCenterServicev614.getInstance().getResultDataMediaProjection() != null) {
                bg.setColorFilter(ContextCompat.getColor(getContext(), R.color.color_background_real_time));
                bg.setImageBitmap(BlurBackground.getInstance().getBitmapBgBlur());
            } else {
                String backgroundColor = ThemeHelper.itemControl.getBackgroundColor();
                if (!backgroundColor.isEmpty()) {
                    bg.setBackgroundColor(Color.parseColor(backgroundColor));
                } else {
                    bg.setImageBitmap(BlurBackground.getInstance().getBitmapBgNotBlur());
                }
            }
        }

    }


    public void touchShow(ListenerAnim listenerAnim) {
        decorRcc.attachView();
        decorLayoutParent.attachView();
        animate().cancel();
        setAlpha(0);
        animate().alpha(1).setDuration(300).withEndAction(() -> {
            if (listenerAnim != null) {
                listenerAnim.onAnimEnd();
            }
        }).start();

        TranslateAnimation translateAnimation = new TranslateAnimation(0f, 0f, 0f, 150f);
        translateAnimation.setDuration(300);
        translateAnimation.setInterpolator(new ReverseInterpolator());
        parentChild.startAnimation(translateAnimation);
    }

    private void intentSettingNotificationListener() {
        try {
            Intent intent = new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS");
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            getContext().startActivity(intent);
        } catch (Exception e) {
        }
        animationHideMain();
    }

    private void clearAllNoty() {
        if (NotificationListener.getInstance() != null) {
            NotificationListener.getInstance().deleteAllNoty();
            animationHideMain();
        }
    }

    private void animationHideMain() {
        animate().cancel();
        animate().alpha(0f).setDuration(300).withEndAction(() -> {
            onNotyCenterCloseListener.closeEnd();
            setAlpha(1f);
        }).start();
    }


    @Override
    public void onHorizontalScroll() {

    }


    public void notiRemoved(int positionGroup, int posChild, boolean isRemovedGroup, String idNoty) {
        post(() -> {
            if (adapterNotyMi != null) {
//                if (isRemovedGroup) {
//                    adapterNotyMi.removeGroup(positionGroup);
//                } else {
//                    if (NotyManager.INSTANCE.getListNotyGroup().size() > positionGroup) {
//                        adapterNotyMi.removedItemInGroup(positionGroup, NotyManager.INSTANCE.getListNotyGroup().get(positionGroup), posChild);
//                    }
//                }
                adapterNotyMi.setData(NotyManager.INSTANCE.getListNotyGroup());
            }
        });
    }

    public void notyAdded(ItemAddedNoty itemAddedNoty) {
        if (adapterNotyMi != null) {
            adapterNotyMi.setAddedItem(itemAddedNoty);
        }
    }

    public void reloadNoty() {
        if (adapterNotyMi != null) {
            adapterNotyMi.reloadDataAdapter();
        }
    }

    public void updateStatusNotificationAccess() {
        permissionNotificationView.setVisibility(NotificationListener.getInstance() != null ? View.GONE : View.VISIBLE);
        groupNoty.setVisibility(NotificationListener.getInstance() != null ? View.VISIBLE : View.GONE);
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP) {
            animationHideMain();
        }
        return super.dispatchKeyEvent(event);
    }


    public void setNotySnoozed(String key) {
        if (adapterNotyMi != null) {
            return;
        }
        NotyGroup notySnoozed = new DensityUtils().deserializeFromJson(Hawk.get(key));
        Hawk.delete(key);
        if (notySnoozed == null || notySnoozed.getNotyModels().isEmpty()) {
            return;
        }
        for (NotyModel notyModel : notySnoozed.getNotyModels()) {
            notyModel.setTime(System.currentTimeMillis());
        }
        ArrayList<NotyGroup> notyGroups = new ArrayList<>(NotyManager.INSTANCE.getListNotyGroup());
        notyGroups.add(0, notySnoozed);
        adapterNotyMi.setData(notyGroups);

    }

}
