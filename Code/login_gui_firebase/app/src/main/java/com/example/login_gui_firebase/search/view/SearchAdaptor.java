package com.example.login_gui_firebase.search.view;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.login_gui_firebase.R;
import com.example.login_gui_firebase.model.pojo.Area;
import com.example.login_gui_firebase.model.pojo.Categories;
import com.example.login_gui_firebase.model.pojo.Ingredients;
import java.util.ArrayList;
import java.util.List;

public class SearchAdaptor extends RecyclerView.Adapter<SearchAdaptor.SearchViewHolder> {

    private List<Object> items = new ArrayList<>();
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(Object item);
    }

    public void setItems(List<?> newItems) {
        this.items.clear();
        if (newItems != null) {
            this.items.addAll(newItems);
        }
        notifyDataSetChanged();
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public SearchViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_search, parent, false);
        return new SearchViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SearchViewHolder holder, int position) {
        Object item = items.get(position);
        String name = "";

        if (item instanceof Categories) {
            name = ((Categories) item).getStrCategory();
        } else if (item instanceof Area) {
            name = ((Area) item).getStrArea();
        } else if (item instanceof Ingredients) {
            name = ((Ingredients) item).getStrIngredient();
        }

        holder.bind(name, item);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    class SearchViewHolder extends RecyclerView.ViewHolder {
        private TextView itemName;

        public SearchViewHolder(@NonNull View itemView) {
            super(itemView);
            itemName = itemView.findViewById(R.id.item_name);
        }

        public void bind(String name, Object item) {
            itemName.setText(name);
            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onItemClick(item);
                }
            });
        }
    }
}