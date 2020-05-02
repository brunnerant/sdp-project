package ch.epfl.qedit.view.util;

import static androidx.recyclerview.widget.ItemTouchHelper.ACTION_STATE_DRAG;
import static androidx.recyclerview.widget.ItemTouchHelper.ACTION_STATE_IDLE;
import static androidx.recyclerview.widget.ItemTouchHelper.DOWN;
import static androidx.recyclerview.widget.ItemTouchHelper.UP;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import ch.epfl.qedit.R;
import java.util.List;
import java.util.Objects;

/**
 * This class is a utility class that allows to create editable lists. It supports the addition,
 * removal and reordering of items. Items additionally have an edit button that can be linked to a
 * custom action. Since this class extends the View class through RecyclerView, it can be used in
 * layout files, which saves the burden of recreating a recycler view every time it is needed. Note
 * that to enable the reordering of items through drag and drop, you should set the attribute
 * dragAndDrop to true in the XMl file.
 */
public class ListEditView extends RecyclerView {

    /**
     * This interface is used to retrieve the text for one of the items in the list.
     *
     * @param <T> the type of the list items
     */
    public interface GetItemText<T> {
        String getText(T item);
    }

    /** This enumerates the events that can occur on an item */
    public enum EventType {
        Select,
        RemoveRequest,
        EditRequest
    }

    /** This interface is used to react to events that happen on an item. */
    public interface ItemListener {
        /**
         * This handles an event on a specific item of the list. Note that in case an item is
         * deselected, position will be -1.
         *
         * @param position the position of the item on which the event occurred
         * @param type the type of event that occurred
         */
        void onItemEvent(int position, EventType type);
    }

    /**
     * This interface allows to be notified when items are moved in the list. Note that it only
     * makes sense to register a MoveListener if the list has drag and drop implemented.
     */
    public interface MoveListener {
        /**
         * This handles the event of an item being moved to another position in the list.
         *
         * @param from the position from which the item was moved
         * @param to the position to which the item was moved
         */
        void onItemMoved(int from, int to);
    }

    private Adapter adapter;
    private boolean dragAndDrop = false;

    // This class is holding the view and data for one item
    private final class ItemHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final TextView text;
        private final LinearLayout overlayButtons;

        ItemHolder(@NonNull View itemView) {
            super(itemView);
            text = itemView.findViewById(android.R.id.text1);
            overlayButtons = itemView.findViewById(R.id.overlay_buttons);
            itemView.setOnClickListener(this);

            itemView.findViewById(R.id.edit_button)
                    .setOnClickListener(
                            new OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    adapter.notifyItem(getLayoutPosition(), EventType.EditRequest);
                                }
                            });

            itemView.findViewById(R.id.delete_button)
                    .setOnClickListener(
                            new OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    adapter.notifyItem(
                                            getLayoutPosition(), EventType.RemoveRequest);
                                }
                            });
        }

        // This changes the text of an item when the recycler view wants to reuse it
        void onTextChanged(String newText) {
            text.setText(newText);
        }

        // This toggles the visibility
        void onSelected(boolean selected) {
            overlayButtons.setVisibility(selected ? View.VISIBLE : View.INVISIBLE);
        }

        // This handles the toggling of the overlay buttons when an item is clicked
        @Override
        public void onClick(View v) {
            int previousQuestion = adapter.selectedQuestion;
            adapter.selectedQuestion = getLayoutPosition();

            if (previousQuestion == adapter.selectedQuestion) {
                adapter.selectedQuestion = NO_POSITION;
                adapter.notifyItemChanged(previousQuestion);
            } else {
                adapter.notifyItemChanged(previousQuestion);
                adapter.notifyItemChanged(adapter.selectedQuestion);
            }

            adapter.notifyItem(adapter.selectedQuestion, EventType.Select);
        }
    }

    /**
     * This class is the central part of the ListEditView. It is used as an interface between the UI
     * and the underlying data. It is through this class that items can be added and removed, and
     * that callbacks can be bound to react to different events.
     *
     * @param <T> the type of the underlying items
     */
    public static class Adapter<T> extends RecyclerView.Adapter<ItemHolder> {

        private final List<T> items;
        private int selectedQuestion = NO_POSITION;
        private final GetItemText<T> getText;
        private ListEditView listEditView;
        private ItemListener itemListener = null;
        private MoveListener moveListener = null;

        // This class is used to enable drag-and-drop of items
        private class ItemTouchCallback extends ItemTouchHelper.Callback {
            @Override
            public int getMovementFlags(
                    @NonNull RecyclerView recyclerView,
                    @NonNull RecyclerView.ViewHolder viewHolder) {
                return makeFlag(ACTION_STATE_IDLE, UP | DOWN)
                        | makeFlag(ACTION_STATE_DRAG, UP | DOWN);
            }

            @Override
            public boolean onMove(
                    @NonNull RecyclerView recyclerView,
                    @NonNull RecyclerView.ViewHolder viewHolder,
                    @NonNull RecyclerView.ViewHolder target) {
                moveItem(viewHolder.getAdapterPosition(), target.getAdapterPosition());
                return true;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {}
        }

        /**
         * An adapter is used to hold the items of the list edit view. It contains a generic list of
         * items, along with a function to retrieve the text from an item.
         *
         * @param items the list of items to display
         * @param getText a function to retrieve the text from one item
         */
        public Adapter(List<T> items, GetItemText<T> getText) {
            this.items = Objects.requireNonNull(items);
            this.getText = Objects.requireNonNull(getText);
        }

        /**
         * Adds an item to the list.
         *
         * @param item the item to add
         */
        public void addItem(T item) {
            items.add(item);
            notifyItemInserted(items.size() - 1);
        }

        /**
         * Removes an item from the list.
         *
         * @param position the item to remove
         */
        public void removeItem(int position) {
            if (position == selectedQuestion) selectedQuestion = NO_POSITION;

            items.remove(position);
            notifyItemRemoved(position);
        }

        /**
         * Updates an item in the list.
         *
         * @param position of the item in the list that will be updated
         */
        public void updateItem(int position) {
            notifyItemChanged(position);
        }

        /**
         * Registers an event listener for the items of the list. It can be used to be notified when
         * an item is selected, removed, or when the user clicked on the edit button.
         *
         * @param listener the listener that wants to be notified
         */
        public void setItemListener(ItemListener listener) {
            this.itemListener = listener;
        }

        /**
         * Registers an move listener for the items of the list. It can be used to be notified when
         * an item is moved through the list.
         *
         * @param listener the listener that wants to be notified
         */
        public void setMoveListener(MoveListener listener) {
            this.moveListener = listener;
        }

        /**
         * Selects the item at the given position. If position is -1, it deselects what was
         * selected.
         *
         * @param pos the position of the item that must be selected
         */
        public void selectItem(int pos) {
            if (pos < -1 || pos >= items.size()) throw new IllegalArgumentException();

            int previous = selectedQuestion;
            selectedQuestion = pos;

            if (pos == -1) {
                notifyItemChanged(previous);
            } else if (pos != previous) {
                notifyItemChanged(previous);
                notifyItemChanged(pos);
                notifyItem(pos, EventType.Select);
            }
        }

        @NonNull
        @Override
        public ItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return listEditView
            .new ItemHolder(
                    LayoutInflater.from(parent.getContext())
                            .inflate(R.layout.list_edit_item, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull ItemHolder holder, int position) {
            holder.onTextChanged(getText.getText(items.get(position)));
            holder.onSelected(position == selectedQuestion);
        }

        @Override
        public int getItemCount() {
            return items.size();
        }

        // This handles items being moved around
        private void moveItem(int from, int to) {
            if (from == to) return;
            items.add(to, items.remove(from));

            if (from == selectedQuestion) selectedQuestion = to;
            else if (from < selectedQuestion && to >= selectedQuestion) selectedQuestion--;
            else if (from > selectedQuestion && to <= selectedQuestion) selectedQuestion++;

            notifyItemMoved(from, to);
            if (moveListener != null) moveListener.onItemMoved(from, to);
        }

        private void setListEditView(ListEditView listEditView) {
            this.listEditView = listEditView;
        }

        private void notifyItem(int position, EventType type) {
            if (itemListener != null) itemListener.onItemEvent(position, type);
        }
    }

    public ListEditView(Context context) {
        super(context);
        init(context);
    }

    public ListEditView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);

        TypedArray a =
                context.getTheme().obtainStyledAttributes(attrs, R.styleable.ListEditView, 0, 0);

        try {
            // We retrieve whether drag and drop should be enabled from the XML file.
            // By default it is not enabled.
            dragAndDrop = a.getBoolean(R.styleable.ListEditView_dragAndDrop, false);
        } finally {
            a.recycle();
        }
    }

    /**
     * Binds this ListEditView with the given adapter. See the documentation of Adapter to see how
     * to build an adapter.
     *
     * @param adapter the ListEditAdapter to bind with the ListEditView
     */
    public void setAdapter(Adapter adapter) {
        this.adapter = Objects.requireNonNull(adapter);
        adapter.setListEditView(this);
        super.setAdapter(adapter);

        // If drag and drop is enabled, we have to attach the item touch helper, because
        // it is responsible for handling gestures
        if (dragAndDrop)
            new ItemTouchHelper(adapter.new ItemTouchCallback()).attachToRecyclerView(this);
    }

    // This handles the initialization of the various properties of the recycler view which
    // are needed by every constructor
    private void init(Context context) {
        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        super.setHasFixedSize(true);
        super.setLayoutManager(new LinearLayoutManager(context));
        super.addItemDecoration(new DividerItemDecoration(context, layoutManager.getOrientation()));
    }
}
