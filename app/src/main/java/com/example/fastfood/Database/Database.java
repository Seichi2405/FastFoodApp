package com.example.fastfood.Database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;

import com.example.fastfood.Model.Order;
import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

import java.util.ArrayList;
import java.util.List;

public class Database extends SQLiteAssetHelper {
    private static final String DB_NAME = "DbFastFood.db";
    private static final int DB_VER = 1;

    public Database(Context context) {
        super(context, DB_NAME, null, DB_VER);
    }

    public List<Order> getCarts() {
        SQLiteDatabase db = getReadableDatabase();
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        String[] sqlSelect = {"ProductName", "ProductId", "Quantity", "Price", "Discount"};
        String sqlTable = "OrderDetail";
        qb.setTables(sqlTable);

        Cursor c = qb.query(db, sqlSelect, null, null, null, null, null);

        final List<Order> result = new ArrayList<>();

        int productIdIndex = c.getColumnIndex("ProductId");
        int productNameIndex = c.getColumnIndex("ProductName");
        int quantityIndex = c.getColumnIndex("Quantity");
        int priceIndex = c.getColumnIndex("Price");
        int discountIndex = c.getColumnIndex("Discount");

        if (c.moveToFirst()) {
            do {
                result.add(new Order(
                        c.getString(productIdIndex),
                        c.getString(productNameIndex),
                        c.getInt(quantityIndex),
                        c.getString(priceIndex),
                        c.getString(discountIndex)
                ));
            } while (c.moveToNext());
        }

        c.close(); // Don't forget to close the cursor when you're done with it
        return result;
    }

    public void addToCart(Order order)
    {
        SQLiteDatabase db=getReadableDatabase();
        String query =String.format("INSERT INTO OrderDetail(ProductId, ProductName, Quantity, Price, Discount) VALUES('%s','%s','%s','%s','%s');",
                order.getProductId(),
                order.getProductName(),
                order.getQuantity(),
                order.getPrice(),
                order.getDiscount());
        db.execSQL(query);
    }

    public void  cleanCart()
    {
        SQLiteDatabase db=getReadableDatabase();
        String query=String.format("DELETE FROM orderDetail");
        db.execSQL(query);
    }
}
