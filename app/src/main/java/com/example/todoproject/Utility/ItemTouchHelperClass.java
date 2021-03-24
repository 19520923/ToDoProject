package com.example.todoproject.Utility;


import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

public class ItemTouchHelperClass extends ItemTouchHelper.Callback{
    private ItemTouchHelperAdapter adapter;

    public interface ItemTouchHelperAdapter {
        void onItemMoved(int fromPosition, int toPosition);

        void onItemRemoved(int position);
    }

    public ItemTouchHelperClass(ItemTouchHelperAdapter ad){adapter = ad;}

    @Override
    public boolean isLongPressDragEnabled(){return true;}
    @Override
    public boolean isItemViewSwipeEnabled(){return true;}
    @Override
    public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder){
        int upFlags = ItemTouchHelper.UP|ItemTouchHelper.DOWN;
        int swipeFlags = ItemTouchHelper.START|ItemTouchHelper.END;
        return makeMovementFlags(upFlags,swipeFlags);
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction){
        adapter.onItemRemoved(viewHolder.getAdapterPosition());
    }

    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
        adapter.onItemMoved(viewHolder.getAdapterPosition(), target.getAdapterPosition());
        return true;
    }
}
