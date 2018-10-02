package com.example.android.bookstore;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.example.android.bookstore.data.BookContract.BookEntry;
import com.example.android.bookstore.data.BookDbHelper;

public class EditorActivity extends AppCompatActivity {

    private EditText mProductName;

    private EditText mPrice;

    private EditText mQuantity;

    private EditText mSupplierName;

    private EditText mSupplierPhoneNumber;

    public BookDbHelper bookDbHelper = new BookDbHelper(this);



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        mProductName = (EditText) findViewById(R.id.product_name);
        mPrice = (EditText) findViewById(R.id.price);
        mQuantity = (EditText) findViewById(R.id.quantity);
        mSupplierName = (EditText) findViewById(R.id.supplier_name);
        mSupplierPhoneNumber = (EditText) findViewById(R.id.supplier_number);

    }

    private void insertData() {

        String productNameString = mProductName.getText().toString().trim();
        String priceInt = mPrice.getText().toString().trim();
        String quantityInt = mQuantity.getText().toString().trim();
        String supplierNameString = mSupplierName.getText().toString().trim();
        String supplierPhoneNumberInt = String.valueOf(mSupplierPhoneNumber.getText());

        int price = Integer.parseInt(priceInt);

        int quantity = Integer.parseInt(quantityInt);

        SQLiteDatabase database = bookDbHelper.getWritableDatabase();

        //Content values used used to put the values to the database

        ContentValues values = new ContentValues();

        values.put(BookEntry.COLUMN_PRODUCT_NAME, productNameString);
        values.put(BookEntry.COLUMN_PRICE, price);
        values.put(BookEntry.COLUMN_QUANTITY, quantity);
        values.put(BookEntry.COLUMN_SUPPLIER_NAME, supplierNameString);
        values.put(BookEntry.COLUMN_SUPPLIER_PHONE_NUMBER, supplierPhoneNumberInt);

        long newRowId = database.insert(BookEntry.TABLE_NAME, null, values);

        if (newRowId == -1) {
            Toast.makeText(this, "Error With Saving The data", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Data Saved Successfully", Toast.LENGTH_SHORT).show();
            finish();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_item_editor, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int menuId = item.getItemId();

        if (menuId == R.id.save_btn) {

            //Method to insert the data into database
            insertData();
        }
        if (menuId==R.id.delete_btn){

        }

        return super.onOptionsItemSelected(item);
    }


}
