package com.example.login_gui_firebase.search.view;

import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.login_gui_firebase.R;
import com.example.login_gui_firebase.model.pojo.Area;
import com.example.login_gui_firebase.model.pojo.Categories;
import com.example.login_gui_firebase.model.pojo.Ingredients;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class SearchAdaptor extends RecyclerView.Adapter<SearchAdaptor.SearchViewHolder> {

    private List<Object> items = new ArrayList<>();
    private OnItemClickListener listener;
    private Set<Object> selectedItems = new HashSet<>();

    public interface OnItemClickListener {
        void onCategoryClick(Categories category);
        void onAreaClick(Area area);
        void onIngredientClick(Ingredients ingredient);
    }

    public void setItems(List<Object> items) {
        this.items = items;
        notifyDataSetChanged();
    }

    public void setSelectedItems(Set<Object> selectedItems) {
        this.selectedItems = selectedItems;
        notifyDataSetChanged();
    }
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public SearchViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_meal_card, parent, false);
        return new SearchViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SearchViewHolder holder, int position) {
        Object item = items.get(position);

        if (item instanceof Categories) {
            Categories category = (Categories) item;
            holder.bindCategory(category);
        } else if (item instanceof Area) {
            Area area = (Area) item;
            holder.bindArea(area);
        } else if (item instanceof Ingredients) {
            Ingredients ingredient = (Ingredients) item;
            holder.bindIngredient(ingredient);
        }

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                if (item instanceof Categories) {
                    listener.onCategoryClick((Categories) item);
                } else if (item instanceof Area) {
                    listener.onAreaClick((Area) item);
                } else if (item instanceof Ingredients) {
                    listener.onIngredientClick((Ingredients) item);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    class SearchViewHolder extends RecyclerView.ViewHolder {
        private TextView itemName, itemDescription;
        private ImageView itemImage;

        public SearchViewHolder(@NonNull View itemView) {
            super(itemView);
            itemName = itemView.findViewById(R.id.itemCategory);
            itemImage = itemView.findViewById(R.id.mealImage);
        }

        void bindCategory(Categories category) {
            itemName.setText(category.getStrCategory());


            Glide.with(itemView.getContext())
                    .load(category.getStrCategoryThumb())
                    .placeholder(R.drawable.image_placeholder)
                    .into(itemImage);
            itemImage.setVisibility(View.VISIBLE);
        }

        void bindArea(Area area) {
            itemName.setText(area.getStrArea());

            String countryCode = getCountryCode(area.getStrArea());
            if (countryCode != null) {
                String flagUrl = "https://flagsapi.com/" + countryCode.toUpperCase() + "/flat/64.png";

                Glide.with(itemView.getContext())
                        .load(flagUrl)
                        .placeholder(R.drawable.image_placeholder)
                        .listener(new RequestListener<Drawable>() {
                            @Override
                            public boolean onLoadFailed(@Nullable GlideException e, Object model,
                                                        Target<Drawable> target, boolean isFirstResource) {
                                Log.e("FlagError", "Failed to load flag for " + countryCode, e);
                                return false;
                            }

                            @Override
                            public boolean onResourceReady(Drawable resource, Object model,
                                                           Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                return false;
                            }
                        })
                        .into(itemImage);
                itemImage.setVisibility(View.VISIBLE);
            } else {
                itemImage.setVisibility(View.GONE);
            }
        }


        private String getCountryCode(String areaName) {
            Map<String, String> areaToCodeMap = new HashMap<String, String>() {{
                put("American", "US");
                put("British", "GB");
                put("Canadian", "CA");
                put("Chinese", "CN");
                put("Croatian", "HR");
                put("Dutch", "NL");
                put("Egyptian", "EG");
                put("Filipino", "PH");
                put("French", "FR");
                put("Greek", "GR");
                put("Indian", "IN");
                put("Irish", "IE");
                put("Italian", "IT");
                put("Jamaican", "JM");
                put("Japanese", "JP");
                put("Kenyan", "KE");
                put("Malaysian", "MY");
                put("Mexican", "MX");
                put("Moroccan", "MA");
                put("Polish", "PL");
                put("Portuguese", "PT");
                put("Russian", "RU");
                put("Spanish", "ES");
                put("Thai", "TH");
                put("Tunisian", "TN");
                put("Turkish", "TR");
                put("Ukrainian", "UA");
                put("Uruguayan", "UY");
                put("Vietnamese", "VN");
            }};

            // Handle case sensitivity and trim whitespace
            String normalizedArea = areaName.trim();
            return areaToCodeMap.get(normalizedArea);
        }

        void bindIngredient(Ingredients ingredient) {
            itemName.setText(ingredient.getStrIngredient());

            String ingredientName = ingredient.getStrIngredient().replace(" ", "_");
            String imageUrl = "https://www.themealdb.com/images/ingredients/" + ingredientName + "-Small.png";

            Glide.with(itemView.getContext())
                    .load(imageUrl)
                    .placeholder(R.drawable.image_placeholder)
                    .into(itemImage);

            itemImage.setVisibility(View.VISIBLE);
        }
    }
}
