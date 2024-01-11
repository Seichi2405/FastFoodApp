package com.example.fastfood;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.fastfood.Database.Database;
import com.example.fastfood.Model.Food;
import com.example.fastfood.Model.Order;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class FoodDetail extends AppCompatActivity {

    TextView food_name, food_price, food_description;
    ImageView food_image;
    CollapsingToolbarLayout collapsingToolbarLayout;
    NumberPicker numberPicker;
    TextView tv_number;
    String foodId = "";
    FirebaseDatabase database;
    DatabaseReference food;
    FloatingActionButton btnCart;
    Food currentFood;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_detail);

        tv_number = findViewById(R.id.tv_number);
        numberPicker = findViewById(R.id.numberPicker);
        food_description = findViewById(R.id.food_description);
        food_name = findViewById(R.id.food_name);
        food_price = findViewById(R.id.food_price);
        food_image = findViewById(R.id.food_image);
        btnCart = findViewById(R.id.btnCard);
        btnCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Database(getBaseContext()).addToCart(new Order(
                        foodId,
                        currentFood.getName(),
                        numberPicker.getValue(),
                        currentFood.getPrice(),
                        currentFood.getDiscount()
                ));
                Toast.makeText(FoodDetail.this, "Đã thêm vào giỏ hàng", Toast.LENGTH_SHORT).show();
            }
        });

        collapsingToolbarLayout = findViewById(R.id.collapsing);
        collapsingToolbarLayout.setExpandedTitleTextAppearance(R.style.ExpandedAppbar);
        collapsingToolbarLayout.setCollapsedTitleTextAppearance(R.style.CollapsedAppbar);

        // Get foodId from intent
        if (getIntent() != null)
            foodId = getIntent().getStringExtra("foodId");
        assert foodId != null;
        if (!foodId.isEmpty()) {
            // Initialize Firebase
            database = FirebaseDatabase.getInstance("https://fastfooddb-12713-default-rtdb.asia-southeast1.firebasedatabase.app");
            food = database.getReference("Food");

            // Fetch and display food details
            getDetailFood();
        }

        // NumberPicker
        numberPicker.setMinValue(0);
        numberPicker.setMaxValue(10);
        tv_number.setText((String.format("Số Lượng: %s", numberPicker.getValue())));

        numberPicker.setOnValueChangedListener((numberPicker, oldVal, newVal) -> tv_number.setText(String.format("Số Lượng: %s", newVal)));
    }

    private void getDetailFood() {
        DatabaseReference foodDetailRef = food.child(foodId); // Append foodId to the reference

        foodDetailRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    currentFood = snapshot.getValue(Food.class);
                    if (food != null) {
                        // Display food details
                        Picasso.get().load(currentFood.getImage()).into(food_image);
                        collapsingToolbarLayout.setTitle(currentFood.getName());
                        food_price.setText(currentFood.getPrice());
                        food_name.setText(currentFood.getName());
                        food_description.setText(currentFood.getDescription());
                    } else {
                        Log.e("FoodDetail", "Food object is null");
                    }
                } else {
                    Log.e("FoodDetail", "Food snapshot does not exist");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("FoodDetail", "Error retrieving food detail: " + error.getMessage());
            }
        });
    }
}
