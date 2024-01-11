package com.example.fastfood;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fastfood.Interface.ItemClickListener;
import com.example.fastfood.Model.Food;
import com.example.fastfood.ViewHolder.FoodViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mancj.materialsearchbar.MaterialSearchBar;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class FoodList extends AppCompatActivity {

    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    FirebaseDatabase database;
    DatabaseReference foodList;
    FirebaseRecyclerAdapter<Food, FoodViewHolder> adapter;

    FirebaseRecyclerAdapter<Food, FoodViewHolder> searchAdapter;
    List<String> suggestList = new ArrayList<>();
    MaterialSearchBar materialSearchBar;

    String categoryId = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_list);

        // Firebase
        database = FirebaseDatabase.getInstance("https://fastfooddb-12713-default-rtdb.asia-southeast1.firebasedatabase.app");
        foodList = database.getReference("Food");

        recyclerView = findViewById(R.id.recycler_food);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        // Kiểm tra Intent có dữ liệu không
        if (getIntent() != null && getIntent().hasExtra("CategoryId")) {
            categoryId = getIntent().getStringExtra("CategoryId");
            if (categoryId != null && !categoryId.isEmpty()) {
                loadListFood(categoryId);
            } else {
                Toast.makeText(this, "CategoryId is empty or null", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Intent does not have CategoryId", Toast.LENGTH_SHORT).show();
        }
        materialSearchBar = findViewById(R.id.searchBar);

        materialSearchBar.setHint("Nhập món ăn");
        loadSuggest();
        materialSearchBar.setLastSuggestions(suggestList);
        materialSearchBar.setCardViewElevation(10);
        materialSearchBar.addTextChangeListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // Your code here
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                List<String> suggest = new ArrayList<>();
                for (String search : suggestList) {
                    if (search.toLowerCase().contains(charSequence.toString().toLowerCase())) {
                        suggest.add(search);
                    }
                }
                materialSearchBar.updateLastSuggestions(suggest);
            }

            @Override
            public void afterTextChanged(Editable editable) {
                // Leave it empty or add your code here if needed
            }
        });
        materialSearchBar.setOnSearchActionListener(new MaterialSearchBar.OnSearchActionListener() {
            @Override
            public void onSearchStateChanged(boolean enabled) {
                // reset list lai khi dong search
                if (!enabled)
                    recyclerView.setAdapter(adapter);
            }

            @Override
            public void onSearchConfirmed(CharSequence text) {
                // hien len khi search xong
                startSearch(text);
            }

            private void startSearch(CharSequence text) {
                FirebaseRecyclerOptions<Food> options =
                        new FirebaseRecyclerOptions.Builder<Food>()
                                .setQuery(foodList.orderByChild("menuId").equalTo(categoryId), Food.class)
                                .build();
                searchAdapter = new FirebaseRecyclerAdapter<Food, FoodViewHolder>(options) {
                    @NonNull
                    @Override
                    public FoodViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(parent.getContext())
                                .inflate(R.layout.food_item, parent, false);
                        return new FoodViewHolder(view);
                    }

                    @Override
                    protected void onBindViewHolder(@NonNull FoodViewHolder holder, int position, @NonNull Food model) {
                        Picasso.get().load(model.getImage()).into(holder.food_image);
                        holder.food_name.setText(model.getName());

                        final Food local = model;
                        holder.setItemClickListener(new ItemClickListener() {
                            @Override
                            public void onClick(View view, int position, boolean isLongClick) {
                                // Use holder.getAdapterPosition() to get the correct position
                                String foodId = searchAdapter.getRef(holder.getAdapterPosition()).getKey();
                                showFoodDetail(foodId);
                            }
                        });
                    }
                };
                recyclerView.setAdapter(searchAdapter);
            }

            @Override
            public void onButtonClicked(int buttonCode) {
                // Your code here if a button is clicked
            }
        });
    }

    private void loadSuggest() {
        foodList.orderByChild(String.valueOf("menuId".equals(categoryId)))
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                            Food item = postSnapshot.getValue(Food.class);
                            suggestList.add(item.getName());
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        // Handle the error if needed
                    }
                });
    }

    private void loadListFood(String categoryId) {
        FirebaseRecyclerOptions<Food> options =
                new FirebaseRecyclerOptions.Builder<Food>()
                        .setQuery(foodList.orderByChild("menuId").equalTo(categoryId), Food.class)
                        .build();
        adapter = new FirebaseRecyclerAdapter<Food, FoodViewHolder>(options) {
            @NonNull
            @Override
            public FoodViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.food_item, parent, false);
                return new FoodViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull FoodViewHolder holder, int position, @NonNull Food model) {
                Picasso.get().load(model.getImage()).into(holder.food_image);
                holder.food_name.setText(model.getName());

                final Food local = model;
                holder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {
                        String foodId = adapter.getRef(holder.getAdapterPosition()).getKey();
                        showFoodDetail(foodId);
                    }
                });
            }
        };
        recyclerView.setAdapter(adapter);
    }

    private void showFoodDetail(String foodId) {
        // Implement how you want to display the details of the selected food item
        Intent foodDetailIntent = new Intent(FoodList.this, FoodDetail.class);
        foodDetailIntent.putExtra("foodId", foodId);
        startActivity(foodDetailIntent);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (adapter != null) {
            adapter.startListening();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (adapter != null) {
            adapter.stopListening();
        }
    }
}
