package com.example.android.bookstore;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.example.android.bookstore.data.BookContract.BookEntry;
import com.example.android.bookstore.data.BookDbHelper;

public class MainActivity extends AppCompatActivity {

    FloatingActionButton fab;

    private BookDbHelper mDbHelper = new BookDbHelper(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fab = (FloatingActionButton) findViewById(R.id.floatingActionButton);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, EditorActivity.class);
                startActivity(intent);
            }
        });


    }

    @Override
    protected void onStart() {
        TextView dispalydata = (TextView) findViewById(R.id.text_view_data);
        dispalydata.setText(null);
        super.onStart();
        queryData();
    }

    private void queryData() {

        //This method used to retrieve the data from the database

        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        String[] projection = {
                BookEntry._ID,
                BookEntry.COLUMN_PRODUCT_NAME,
                BookEntry.COLUMN_PRICE,
                BookEntry.COLUMN_QUANTITY,
                BookEntry.COLUMN_SUPPLIER_NAME,
                BookEntry.COLUMN_SUPPLIER_PHONE_NUMBER
        };

        Cursor cursor = db.query(
                BookEntry.TABLE_NAME,
                projection,
                null,
                null,
                null,
                null,
                null);

        TextView dispalydata = (TextView) findViewById(R.id.text_view_data);

        if (cursor.getCount() == 0) {
            dispalydata.setText("No Data to display");
        } else {


            try {
                dispalydata.append(BookEntry._ID + "-"
                        + BookEntry.COLUMN_PRODUCT_NAME + " - "
                        + BookEntry.COLUMN_PRICE + " - "
                        + BookEntry.COLUMN_QUANTITY + " - "
                        + BookEntry.COLUMN_SUPPLIER_NAME + " - "
                        + BookEntry.COLUMN_SUPPLIER_PHONE_NUMBER + "\n");

                int idColumnIndex = cursor.getColumnIndex(BookEntry._ID);
                int productNameColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_PRODUCT_NAME);
                int PriceColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_PRICE);
                int quantityColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_QUANTITY);
                int supplierNameColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_SUPPLIER_NAME);
                int supplierNumberColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_SUPPLIER_PHONE_NUMBER);

                while (cursor.moveToNext()) {
                    int currentId = cursor.getInt(idColumnIndex);
                    String currentProduct = cursor.getString(productNameColumnIndex);
                    int currentPrice = cursor.getInt(PriceColumnIndex);
                    int currentQuantity = cursor.getInt(quantityColumnIndex);
                    String currentSupplierName = cursor.getString(supplierNameColumnIndex);
                    String currentSupplierNumber = cursor.getString(supplierNumberColumnIndex);

                    dispalydata.append(("\n" + currentId + " - "
                            + currentProduct + " - "
                            + currentPrice + " - "
                            + currentQuantity + " - "
                            + currentSupplierName + " - "
                            + currentSupplierNumber));

                }

            } finally {
                cursor.close();
            }
        }

    }
}
