package ch.epfl.qedit.view.util;

import static androidx.recyclerview.widget.ItemTouchHelper.ACTION_STATE_DRAG;
import static androidx.recyclerview.widget.ItemTouchHelper.ACTION_STATE_IDLE;
import static androidx.recyclerview.widget.ItemTouchHelper.DOWN;
import static androidx.recyclerview.widget.ItemTouchHelper.UP;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.PopupMenu;
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
import java.util.function.Function;

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

    /** This interface is used to react to events that happen on an item. */
    public interface ItemListener {
        /** The event code for an item being clicked on */
        int CLICK = -1;

        /**
         * This handles an event on a specific item of the list. The event can either be an item
         * being clicked on, or an item of the "three dots" menu being clicked on. In the former
         * case, the code CLICK is passed. Otherwise, the index of the menu item is passed. This
         * allows custom menus to be built in an easy way.
         *
         * @param position the position of the item on which the event occurred.
         * @param eventCode the code of the event that occurred.
         */
        void onItemEvent(int position, int eventCode);
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
    private final class ItemHolder extends RecyclerView.ViewHolder
            implements PopupMenu.OnMenuItemClickListener {
        private final TextView text;
        private final ImageButton threeDots;

        ItemHolder(@NonNull View itemView) {
            super(itemView);

            text = itemView.findViewById(android.R.id.text1);
            threeDots = itemView.findViewById(R.id.list_item_three_dots);

            // When an item is clicked on, we pass the CLICK event to the listener
            itemView.setOnClickListener(
                    v -> adapter.notifyItem(getAdapterPosition(), ItemListener.CLICK));

            threeDots.setOnClickListener(
                    v -> {
                        PopupMenu menu = new PopupMenu(itemView.getContext(), threeDots);
                        menu.setOnMenuItemClickListener(this);

                        // The popup menu contains the items specified in the adapter
                        for (int i = 0; i < adapter.popupMenuItems.size(); i++)
                            // The cast is somehow necessary
                            menu.getMenu()
                                    .add(
                                            Menu.NONE, // the group id (no group)
                                            i, // the item id
                                            i, // the order
                                            (CharSequence)
                                                    adapter.popupMenuItems.get(i)); // the text

                        menu.show();
                    });
        }

        // This changes the text of an item when the recycler view wants to reuse it
        void onTextChanged(String newText) {
            text.setText(newText);
        }

        @Override
        // This is called when an item in the popup menu is clicked
        public boolean onMenuItemClick(MenuItem item) {
            int id = item.getItemId();

            // We pass as an event the index of the item in the popup menu
            adapter.notifyItem(getAdapterPosition(), id);

            return true; // the event was handled
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

        // The bound list edit view
        private ListEditView listEditView;

        // The items, and a function to retrieve their text
        private final List<T> items;
        private final Function<T, String> getText;

        // The listeners that should be given events
        private ItemListener itemListener = null;
        private MoveListener moveListener = null;

        // This is the list of strings that the popup menus should contain
        private final List<String> popupMenuItems;

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
         * items, along with a function to retrieve the text from an item. It also contains a list
         * of items that should be displayed when clicking on the "three dots" button
         *
         * @param items the list of items to display
         * @param getText a function to retrieve the text from one item
         * @param popupMenuItems the list of items to show in the popup menu, when clicking on the
         *     three dots
         */
        public Adapter(List<T> items, Function<T, String> getText, List<String> popupMenuItems) {
            this.items = Objects.requireNonNull(items);
            this.getText = Objects.requireNonNull(getText);
            this.popupMenuItems = Objects.requireNonNull(popupMenuItems);
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
            items.remove(position);
            notifyItemRemoved(position);
        }

        /**
         * Updates an item in the list.
         *
         * @param position of the item in the list that will be updated
         */
        public void updateItem(int position, T newValue) {
            items.set(position, newValue);
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
         * Registers a move listener for the items of the list. It can be used to be notified when
         * an item is moved through the list.
         *
         * @param listener the listener that wants to be notified
         */
        public void setMoveListener(MoveListener listener) {
            this.moveListener = listener;
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
            holder.onTextChanged(getText.apply(items.get(position)));
        }

        @Override
        public int getItemCount() {
            return items.size();
        }

        // This handles items being moved around
        private void moveItem(int from, int to) {
            if (from == to) return;
            items.add(to, items.remove(from));

            notifyItemMoved(from, to);
            if (moveListener != null) moveListener.onItemMoved(from, to);
        }

        private void setListEditView(ListEditView listEditView) {
            this.listEditView = listEditView;
        }

        private void notifyItem(int position, int eventCode) {
            if (itemListener != null) itemListener.onItemEvent(position, eventCode);
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
        super.setLayoutManager(new LinearLayoutManager(context));
        super.addItemDecoration(new DividerItemDecoration(context, layoutManager.getOrientation()));
    }
}
