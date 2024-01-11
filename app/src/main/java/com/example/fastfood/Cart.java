package com.example.fastfood;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.fastfood.Common.Common;
import com.example.fastfood.Common.Config;
import com.example.fastfood.Database.Database;
import com.example.fastfood.Model.Order;
import com.example.fastfood.Model.Request;
import com.example.fastfood.ViewHolder.CartAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.paypal.android.sdk.payments.PayPalPayment;
import com.paypal.android.sdk.payments.PayPalService;
import com.paypal.android.sdk.payments.PaymentActivity;
import com.paypal.android.sdk.payments.PaymentConfirmation;

import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


public class Cart extends AppCompatActivity {

    private static final int PAYPAL_REQUEST_CODE = 9999;
    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;

    FirebaseDatabase database;
    DatabaseReference requests;
    TextView txtTotalPrice;
    Button btnPlace;
    List<Order> cart = new ArrayList<>();
    CartAdapter adapter;

    //paypal
    static PayPalConfiguration config = new PayPalConfiguration()
            .environment(PayPalConfiguration.ENVIRONMENT_SANDBOX)
            .clientId(Config.PAYPAL_CLIENT_ID);


    String address, comment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        //init paypal

        Intent intent = new Intent(this, PayPalService.class);
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);
        startService(intent);

        //Firebasedatabase
        database = FirebaseDatabase.getInstance("https://fastfooddb-12713-default-rtdb.asia-southeast1.firebasedatabase.app");
        requests = database.getReference("Request");

        //Init
        recyclerView = (RecyclerView)findViewById(R.id.listCart);
        recyclerView.setHasFixedSize(true);
        layoutManager= new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        txtTotalPrice = (TextView)findViewById(R.id.total);
        btnPlace = (Button)findViewById(R.id.btnPlaceOrder);

        btnPlace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAlertDialog();
            }


        });

        loadListFood();

    }
    private void showAlertDialog() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(Cart.this);
        alertDialog.setTitle("One more step!");
        alertDialog.setMessage("nhập địa chỉ");
        final EditText editAdress= new EditText(Cart.this);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
        );
        editAdress.setLayoutParams(lp);
        alertDialog.setView(editAdress); //add edit text alert Dialog
        alertDialog.setIcon(R.drawable.baseline_add_shopping_cart_24);
        alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                //paypal
                address = editAdress.getText().toString();
                String formatAmount= txtTotalPrice.getText().toString()
                        .replace("$","").replace(",","");

                PayPalPayment payPalPayment = new PayPalPayment(
                        new BigDecimal(formatAmount),
                        "USD",
                        "Fastfood",
                        PayPalPayment.PAYMENT_INTENT_SALE
                );

                Intent intent = new Intent(getApplicationContext(), PaymentActivity.class);
                intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);
                intent.putExtra(PaymentActivity.EXTRA_PAYMENT, payPalPayment);

                startActivityForResult(intent, PAYPAL_REQUEST_CODE);



                /*
                //create new request
                Request request= new Request(
                        Common.currentUser.getPhone(),
                        Common.currentUser.getName(),
                        editAdress.getText().toString(),
                        txtTotalPrice.getText().toString(),
                        cart
                );
                //sumit firebase
                // we will using sýstem.CurrentMilli to kay
                requests.child(String.valueOf(System.currentTimeMillis())).setValue(request);
                //delete cart
                try (Database database = new Database(getBaseContext())) {
                    database.cleanCart();
                    Toast.makeText(Cart.this, "cảm ơn đã đặt", Toast.LENGTH_SHORT).show();
                    finish();
                } catch (Exception e) {
                    // Xử lý ngoại lệ nếu có
                    e.printStackTrace();
                }
                */
            }
        });
        alertDialog.setNegativeButton("Không", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        alertDialog.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data); // Add this line

        if (requestCode == PAYPAL_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                PaymentConfirmation confirmation = data.getParcelableExtra(PaymentActivity.EXTRA_RESULT_CONFIRMATION);
                if (confirmation != null) {
                    try {
                        String paymentDetail = confirmation.toJSONObject().toString(4);
                        JSONObject jsonObject = new JSONObject(paymentDetail);

                        //create new request
                        Request request = new Request(
                                Common.currentUser.getPhone(),
                                Common.currentUser.getName(),
                                address,
                                txtTotalPrice.getText().toString(),

                                jsonObject.getJSONObject("response").getString("state"),
                                "0",
                                cart
                        );

                        //sumit firebase
                        // we will using sýstem.CurrentMilli to kay
                        requests.child(String.valueOf(System.currentTimeMillis())).setValue(request);
                        //delete cart
                        try (Database database = new Database(getBaseContext())) {
                            database.cleanCart();
                            Toast.makeText(Cart.this, "cảm ơn đã đặt", Toast.LENGTH_SHORT).show();
                            finish();
                        } catch (Exception e) {
                            // Xử lý ngoại lệ nếu có
                            e.printStackTrace();
                        }

                        // Handle the payment details as needed
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
            else if (resultCode== Activity.RESULT_CANCELED)
                Toast.makeText(this,"Huy thanh toan", Toast.LENGTH_SHORT ).show();
            else if (resultCode==PaymentActivity.RESULT_EXTRAS_INVALID)
                Toast.makeText(this,"Invalid payment", Toast.LENGTH_SHORT).show();
        }
    }

    private void loadListFood() {
        try (Database database = new Database(this)) {
            cart = database.getCarts();
            adapter = new CartAdapter(cart, this);
            recyclerView.setAdapter(adapter);

            // Calculate total price
            int total = 0;
            for (Order order : cart)
                total += Integer.parseInt(order.getPrice()) * order.getQuantity();

            Locale locale = new Locale("en", "US");
            NumberFormat fmt = NumberFormat.getCurrencyInstance(locale);

            txtTotalPrice.setText(fmt.format(total));
        } catch (Exception e) {
            // Handle exceptions if needed
            e.printStackTrace();
        }
    }

}