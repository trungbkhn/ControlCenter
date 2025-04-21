package com.tapbi.spark.controlcenter.feature.mishade.view;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;


import com.tapbi.spark.controlcenter.R;
import com.tapbi.spark.controlcenter.common.Constant;
import com.tapbi.spark.controlcenter.feature.controlios14.model.InfoSystem;
import com.tapbi.spark.controlcenter.feature.mishade.adapter.ActionMiShadeAdapter;
import com.tapbi.spark.controlcenter.service.NotyControlCenterServicev614;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ItemTouchCall extends ItemTouchHelper.Callback {

    private List<InfoSystem> listSystem = new ArrayList<>();
    private List<InfoSystem> listInfoSystem;
    private RecyclerView.Adapter itemAdapter;

    private boolean startMoving = false;
    private OnSelectedItem onSelectedItem;

    public void setOnSelectedItem(OnSelectedItem onSelectedItem) {
        this.onSelectedItem = onSelectedItem;
    }

    public ItemTouchCall(RecyclerView.Adapter itemAdapter, List<InfoSystem> listSystem) {
        this.itemAdapter = itemAdapter;
        this.listSystem = listSystem;
    }

    public List<InfoSystem> getListSystem() {
        return listSystem;
    }


    @Override
    public void onSelectedChanged(@Nullable RecyclerView.ViewHolder viewHolder, int actionState) {
        super.onSelectedChanged(viewHolder, actionState);
        startMoving = !startMoving;
        onSelectedItem.onSelectedChange(startMoving);
    }

    @Override
    public void onMoved(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, int fromPos, @NonNull RecyclerView.ViewHolder target, int toPos, int x, int y) {
        super.onMoved(recyclerView, viewHolder, fromPos, target, toPos, x, y);
        onSelectedItem.onMoved();
    }

    @Override
    public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
        listInfoSystem = ((ActionMiShadeAdapter) itemAdapter).getListAction();
        if (viewHolder instanceof ActionMiShadeAdapter.ViewHolder) {
            if (viewHolder.getItemViewType() == Constant.TYPE_VIEW_HEADER) {
                return makeFlag(0, 0);
            } else {
                boolean isExit = false;
                if (listSystem.size() > 0 && listSystem.size() <= 9) {
                    for (InfoSystem infoSystem : listSystem) {
                        if (infoSystem.getName().equals(listInfoSystem.get(viewHolder.getAbsoluteAdapterPosition()).getName())) {
                            isExit = true;
                            break;
                        }
                    }
                    if (isExit) {
                        NotyControlCenterServicev614.getInstance().showToast(viewHolder.itemView.getContext().getString(R.string.the_minimum_controls, 8));
                        return makeFlag(0, 0);
                    } else {
                        return makeFlag(ItemTouchHelper.ACTION_STATE_DRAG,
                                ItemTouchHelper.DOWN | ItemTouchHelper.UP | ItemTouchHelper.START | ItemTouchHelper.END);
                    }
                } else {
                    return makeFlag(ItemTouchHelper.ACTION_STATE_DRAG,
                            ItemTouchHelper.DOWN | ItemTouchHelper.UP | ItemTouchHelper.START | ItemTouchHelper.END);
                }
            }
        } else {
            return makeFlag(0, 0);
        }
    }

    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
        int currentPosition = viewHolder.getAbsoluteAdapterPosition();
        int newPosition = target.getAbsoluteAdapterPosition();

        if (newPosition == 0) {
            return true;
        }
        if (currentPosition < newPosition) {
            for (int i = currentPosition; i < newPosition; i++) {
                if (itemAdapter instanceof ActionMiShadeAdapter) {
                    Collections.swap(listInfoSystem, i, i+1);
                }
            }
        } else {
            for (int i = currentPosition; i > newPosition; i--) {
                if (itemAdapter instanceof ActionMiShadeAdapter) {
                    Collections.swap(listInfoSystem, i, i - 1);
                }
            }
        }
        int pos = -1;
        for (int i = 1; i < listInfoSystem.size(); i++) {
            if (listInfoSystem.get(i).getName().equals("TITLE_2")) {
                pos = i;
                break;
            }
        }
        listSystem.clear();
        for (int i = 1; i < pos; i++) {
            listSystem.add(listInfoSystem.get(i));
        }

        itemAdapter.notifyItemMoved(currentPosition, newPosition);
        return true;
    }

    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
    }

    public interface OnSelectedItem {
        void onSelectedChange(boolean moving);

        void onMoved();
    }

}
