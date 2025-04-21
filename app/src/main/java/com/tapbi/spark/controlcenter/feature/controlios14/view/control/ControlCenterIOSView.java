package com.tapbi.spark.controlcenter.feature.controlios14.view.control;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.tapbi.spark.controlcenter.R;
import com.tapbi.spark.controlcenter.common.Constant;
import com.tapbi.spark.controlcenter.data.model.ItemControl;
import com.tapbi.spark.controlcenter.data.repository.ThemeHelper;
import com.tapbi.spark.controlcenter.databinding.LayoutControlCenterIosBinding;
import com.tapbi.spark.controlcenter.eventbus.EventSaveControl;
import com.tapbi.spark.controlcenter.feature.SpaceItemDecorator;
import com.tapbi.spark.controlcenter.feature.controlios14.helper.BlurBackground;
import com.tapbi.spark.controlcenter.feature.controlios14.model.controlios.ControlCenterIosModel;
import com.tapbi.spark.controlcenter.feature.controlios14.model.controlios.DataSetupViewControlModel;
import com.tapbi.spark.controlcenter.feature.controlios14.view.SpanSize;
import com.tapbi.spark.controlcenter.feature.controlios14.view.SpannedGridLayoutManager;
import com.tapbi.spark.controlcenter.feature.controlios14.view.control.base.BaseControlCenterIos;
import com.tapbi.spark.controlcenter.service.NotyControlCenterServicev614;
import com.tapbi.spark.controlcenter.utils.DensityUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;

import kotlin.jvm.functions.Function1;
import timber.log.Timber;

public class ControlCenterIOSView extends BaseControlCenterIos {

    private LayoutControlCenterIosBinding binding;
    private Context context;

    public ControlCenterIOSView(@NonNull Context context) {
        super(context);
//        init(context);
    }

    public ControlCenterIOSView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
//        init(context);
    }

    public ControlCenterIOSView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
//        init(context);
    }

    @Override
    public void init(Context context) {
        Timber.e("NVQ onViewInit+++");
        try {
            this.context = context;
            binding = LayoutControlCenterIosBinding.inflate(LayoutInflater.from(context), this, true);
            itemControl = ThemeHelper.itemControl;
            try {
                typeface = (itemControl.getFont() != null && !itemControl.getFont().isEmpty() && !itemControl.getFont().equals("font_default")) ? Typeface.createFromAsset(context.getAssets(), Constant.PATH_FOLDER_FONT.concat(itemControl.getFont())) : null;
            } catch (Exception e) {
            }
            binding.settingExpand.changeFont(typeface, "");
            binding.volumeExpand.changeFont(typeface);
            binding.musicExpand.changeFont(typeface);
            binding.screenTimeoutLayout.changeFont(typeface);

            initViewControl();
        } catch (Exception e) {
            Timber.e("hachung Exception:" + e);
        }

    }

    @Override
    public void changeData(ItemControl itemControl) {
        this.itemControl = itemControl;
        initViewControl();
    }
    @Override
    public void initViewControl() {
        if (itemControl != null && itemControl.getControlCenterOS() != null) {
            orientation = new DensityUtils().getOrientationWindowManager(getContext());
            binding.statusView.changeColorStatus(Color.parseColor(itemControl.getColorStatus()));
            createItemViewControlCenterIOS = new CreateItemViewControlCenterIOS(context, binding.root, binding.clControl, binding.settingExpand, binding.screenTimeoutLayout, binding.viewFocus, binding.brightnessExpand, binding.volumeExpand, binding.statusView, binding.imgViewTouch, binding.imgTouchCloseControl, binding.musicExpand, new DataSetupViewControlModel(itemControl.getId(), itemControl.getIdCategory(), typeface));
            if (orientation == Configuration.ORIENTATION_PORTRAIT) {
                listControl1 = (ArrayList<ControlCenterIosModel>) itemControl.getControlCenterOS().getListControlCenterStyleVerticalTop();
                binding.nest2.setVisibility(ControlCenterIOSView.GONE);
                binding.viewMargin.setVisibility(ControlCenterIOSView.VISIBLE);
                controlCenterIOSAdapter1 = new ControlCenterIOSAdapter(context, listControl1, createItemViewControlCenterIOS);
                initAdapterControl(binding.rclControl1, controlCenterIOSAdapter1, listControl1);
            } else {
                listControl1 = (ArrayList<ControlCenterIosModel>) itemControl.getControlCenterOS().getControlCenterStyleHorizontal().getListControlLeft();
                listControl2 = (ArrayList<ControlCenterIosModel>) itemControl.getControlCenterOS().getControlCenterStyleHorizontal().getListControlRight();
                binding.nest2.setVisibility(ControlCenterIOSView.VISIBLE);
                binding.viewMargin.setVisibility(ControlCenterIOSView.VISIBLE);
                controlCenterIOSAdapter1 = new ControlCenterIOSAdapter(context, listControl1, createItemViewControlCenterIOS);
                controlCenterIOSAdapter2 = new ControlCenterIOSAdapter(context, listControl2, createItemViewControlCenterIOS);
                initAdapterControl(binding.rclControl1, controlCenterIOSAdapter1, listControl1);
                initAdapterControl(binding.rclControl2, controlCenterIOSAdapter2, listControl2);
            }
        }
        setBgNew();
    }


    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            if (listControl1.isEmpty() && itemControl != null && itemControl.getControlCenterOS() != null)
                listControl1 = (ArrayList<ControlCenterIosModel>) itemControl.getControlCenterOS().getListControlCenterStyleVerticalTop();
            if (binding != null)
                initSizeRecyclerView(binding.rclControl1, controlCenterIOSAdapter1, w, listControl1,orientation);
        } else {
            if (binding != null) {
                initSizeRecyclerView(binding.rclControl1, controlCenterIOSAdapter1, w, listControl1,orientation);
                initSizeRecyclerView(binding.rclControl2, controlCenterIOSAdapter2, w, listControl2,orientation);
            }
        }
    }

    public static void initSizeRecyclerView(RecyclerView recyclerView, ControlCenterIOSAdapter controlCenterIOSAdapter, int width, ArrayList<ControlCenterIosModel> list,int orientation) {
        int widthRcl = width;
        if (orientation != Configuration.ORIENTATION_PORTRAIT) {
            widthRcl = width / 2;
        }
//        heightRcl = widthRcl / 16 * ratioH + padding;

        int totalHeight = 0;
        int spanCount = 16; // Số lượng cột của GridLayoutManager
        int totalWidth = widthRcl; // Tổng chiều rộng của RecyclerView
        int columnWidth = totalWidth / spanCount; // Chiều rộng của mỗi cột

        int spanRemaining = spanCount; // Số lượng cột còn lại trong hàng hiện tại
        int maxHeightInRow = 0; // Chiều cao lớn nhất trong hàng hiện tại

        for (int i = 0; i < list.size(); i++) {

            // Lấy chiều cao item thứ i theo tỷ lệ (giả sử chiều cao của item phụ thuộc vào chiều rộng của nó)
            int itemHeight = (int) (columnWidth * list.get(i).getRatioHeight()); // Tính chiều cao item theo tỷ lệ width:height

            // Cập nhật chiều cao lớn nhất trong hàng
            maxHeightInRow = Math.max(maxHeightInRow, itemHeight);

            // Trừ đi số cột item này chiếm
            spanRemaining -= list.get(i).getRatioWidght();

            // Khi spanRemaining <= 0, nghĩa là hàng đã đầy và chúng ta cần chuyển sang hàng mới
            if (spanRemaining <= 0) {
                // Cộng chiều cao của hàng hiện tại vào tổng chiều cao
                totalHeight += maxHeightInRow;
                // Reset các thông số cho hàng mới
                spanRemaining = spanCount; // Reset lại số lượng cột còn lại cho hàng mới
                maxHeightInRow = 0; // Reset chiều cao lớn nhất của hàng mới
            }
        }

        // Nếu còn các item trong hàng mà chưa cộng chiều cao
        if (spanRemaining < spanCount) {
            totalHeight += maxHeightInRow;
        }


        LayoutParams layoutParams = (LayoutParams) recyclerView.getLayoutParams();
        layoutParams.height = totalHeight;

        recyclerView.setLayoutParams(layoutParams);


    }
    @Override
    public void initAdapterControl(RecyclerView recyclerView, ControlCenterIOSAdapter controlCenterIOSAdapter, ArrayList<ControlCenterIosModel> list) {
        initSizeRecyclerView(recyclerView, controlCenterIOSAdapter, DensityUtils.getScreenWidth(), list,orientation);
        SpannedGridLayoutManager spannedGridLayoutManager = new SpannedGridLayoutManager(SpannedGridLayoutManager.Orientation.VERTICAL, 16);
        recyclerView.setLayoutManager(spannedGridLayoutManager);
        recyclerView.setAdapter(controlCenterIOSAdapter);
        ControlCenterIOSAdapter finalControlCenterIOSAdapter = controlCenterIOSAdapter;
        spannedGridLayoutManager.setSpanSizeLookup(new SpannedGridLayoutManager.SpanSizeLookup(new Function1<Integer, SpanSize>() {
            @Override
            public SpanSize invoke(Integer integer) {
                ControlCenterIosModel model = finalControlCenterIOSAdapter.getListControl().get(integer.intValue());
                return new SpanSize(model.getRatioWidght(), model.getRatioHeight());
            }
        }));

        recyclerView.addItemDecoration(new SpaceItemDecorator(10, 10, 10, 10));

        GestureDetector gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                View child = recyclerView.findChildViewUnder(e.getX(), e.getY());
                if (child == null) {
                    if (NotyControlCenterServicev614.getInstance() != null) {
                        NotyControlCenterServicev614.getInstance().closeNotyCenter();
                    }
                }
                return true;
            }

            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                // Xử lý khi vuốt (có thể bỏ qua nếu không cần)
                return false;
            }
        });

        recyclerView.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
            @Override
            public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
                gestureDetector.onTouchEvent(e);
                return false;
            }

            @Override
            public void onTouchEvent(RecyclerView rv, MotionEvent e) {
                // Không cần xử lý
            }

            @Override
            public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {
                // Không cần xử lý
            }
        });
    }
    @Override
    public void setBgNew() {
        if (itemControl != null) {
            if (itemControl.getTypeBackground().equals(Constant.TRANSPARENT)) {
                binding.imgBackground.setImageDrawable(null);
            } else if (itemControl.getTypeBackground().equals(Constant.REAL_TIME)) {
                binding.imgBackground.setColorFilter(ContextCompat.getColor(context, R.color.color_background_real_time));
                binding.imgBackground.setImageBitmap(BlurBackground.getInstance().getBitmapBgBlur());
            } else {
                binding.imgBackground.setImageBitmap(BlurBackground.getInstance().getBitmapBgNotBlur());
            }
        }

    }


    @Override
    public void show() {
    }

    @Subscribe
    public void onEventUpdateControl(EventSaveControl eventSaveControl) {
        eventUpdate(eventSaveControl);
        if (eventSaveControl.getAction().equals(Constant.EVENT_UPDATE_STATE_VIEW_CONTROL)) {
            if (binding.nest2 != null) {
                binding.nest2.fullScroll(View.FOCUS_UP);
            }
            if (binding.nest1 != null) {
                Log.d("duongcvc", "onEventUpdateControl: 1");
                binding.nest1.fullScroll(View.FOCUS_UP);
            }
            Log.d("duongcvc", "onEventUpdateControl: ");
            createItemViewControlCenterIOS.updateState();
        }
    }


    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP) {
            setHideViewExpand();
        }
        return super.dispatchKeyEvent(event);
    }

    public interface OnControlCenterListener {
        void onExit();

        void onClose();
    }


}
