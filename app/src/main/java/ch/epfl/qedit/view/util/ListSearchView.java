package ch.epfl.qedit.view.util;

import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

import ch.epfl.qedit.search.SearchableList;

public class ListSearchView {
    public static class Adapter<T, E extends SearchableList<T>>
            extends ListEditView.Adapter<T> implements Filterable {
        private final E e;
        //private List<T> items;
        private List<T> backup;


        /**
         * An adapter is used to hold the items of the list edit view. It contains a generic list of
         * items, along with a function to retrieve the text from an item.
         *
         * @param e the list of items to display
         * @param getText        a function to retrieve the text from one item
         */
        public Adapter(E e, ListEditView.GetItemText<T> getText) {
            super(e.e, getText);
            //this.items = searchablePair.e;
            this.e = e;
            backup = new ArrayList<>(e.e);
            //this.items = Objects.requireNonNull(e.e);

        }

        public void addItem(T item) {
            backup.add(item);
            super.addItem(item);
        }

        public void removeItem(int position) {
            backup.remove(position);
            super.removeItem(position);
        }

        @NonNull
        @Override
        public ListEditView.ItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return super.onCreateViewHolder(parent, viewType);
        }

        @Override
        public void onBindViewHolder(@NonNull ListEditView.ItemHolder holder, int position) {
            super.onBindViewHolder(holder, position);
        }

        @Override
        public int getItemCount() {
            return super.getItemCount();
        }

        private void subFilter(List<T> filtered, CharSequence constraint) {
            String pattern = constraint.toString().toLowerCase().trim();

            for (int i = 0; i < e.e.size(); ++i) {
                T searched = e.search(pattern, i);

                if (searched != null) {
                    filtered.add(searched);
                }
            }
        }

        @Override
        public Filter getFilter() {
            return new Filter() {
                @Override
                protected FilterResults performFiltering(CharSequence constraint) {
                    List<T> filtered = new ArrayList<>();

                    e.e = new ArrayList<>(backup);
                    if (constraint == null || constraint.length() == 0) {
                        filtered.addAll(e.e);
                    } else {
                        subFilter(filtered, constraint);
                    }

                    FilterResults r = new FilterResults();
                    r.values = new ArrayList<>(filtered);
                    return r;
                }

                @Override
                protected void publishResults(CharSequence constraint, FilterResults results) {
                    clear();
                    addAll((List) results.values);
                    notifyDataSetChanged();
                }
            };
        }
    }
}