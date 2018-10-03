package com.example.android.bookstore;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.android.bookstore.data.BookContract.BookEntry;
import com.example.android.bookstore.data.BookDbHelper;

public class EditorActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private EditText mProductName;

    private EditText mPrice;

    private EditText mQuantity;

    private EditText mSupplierName;

    private EditText mSupplierPhoneNumber;

    private Uri mcurrenturi;

    private Button mIncrementButton;

    private Button mDecrementButton;

    private Button mCallButton;

    public static final int EXISTING_BOOK_LOADER=0;

    public BookDbHelper bookDbHelper = new BookDbHelper(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        Intent intent = getIntent();
        mcurrenturi = intent.getData();
        if (mcurrenturi==null){
            setTitle(getString(R.string.new_data));
            invalidateOptionsMenu();
        }else{
            setTitle(getString(R.string.edit_data));
            getLoaderManager().initLoader(EXISTING_BOOK_LOADER, null, this);
        }

        mProductName = (EditText) findViewById(R.id.product_name);
        mPrice = (EditText) findViewById(R.id.price);
        mQuantity = (EditText) findViewById(R.id.quantity);
        mSupplierName = (EditText) findViewById(R.id.supplier_name);
        mSupplierPhoneNumber = (EditText) findViewById(R.id.supplier_number);
        mIncrementButton = (Button)findViewById(R.id.positive_btn);
        mDecrementButton = (Button)findViewById(R.id.negative_btn);
        mCallButton = (Button)findViewById(R.id.call_btn);

    }

    private void insertData() {

        String productNameString = mProductName.getText().toString().trim();
        String priceInt = mPrice.getText().toString().trim();
        String quantityInt = mQuantity.getText().toString().trim();
        String supplierNameString = mSupplierName.getText().toString().trim();
        String supplierPhoneNumberInt = String.valueOf(mSupplierPhoneNumber.getText());

        int price = Integer.parseInt(priceInt);

        int quantity = Integer.parseInt(quantityInt);

        //Content values used used to put the values to the database

        ContentValues values = new ContentValues();

        values.put(BookEntry.COLUMN_PRODUCT_NAME, productNameString);
        values.put(BookEntry.COLUMN_PRICE, price);
        values.put(BookEntry.COLUMN_QUANTITY, quantity);
        values.put(BookEntry.COLUMN_SUPPLIER_NAME, supplierNameString);
        values.put(BookEntry.COLUMN_SUPPLIER_PHONE_NUMBER, supplierPhoneNumberInt);

//        long newRowId = database.insert(BookEntry.TABLE_NAME, null, values);
//
//        if (newRowId == -1) {
//            Toast.makeText(this, "Error With Saving The data", Toast.LENGTH_SHORT).show();
//        } else {
//            Toast.makeText(this, "Data Saved Successfully", Toast.LENGTH_SHORT).show();
//            finish();
//        }

        if (mcurrenturi==null){
            Uri uri = getContentResolver().insert(BookEntry.CONTENT_URI,values);
            if (uri==null){
                Toast.makeText(this, "Insertion failed", Toast.LENGTH_SHORT).show();
            }else {
                Toast.makeText(this, "Data Saved Successfully", Toast.LENGTH_SHORT).show();
            }
        }else{
            int rowsAffected = getContentResolver().update(BookEntry.CONTENT_URI,values,null,null);
            if (rowsAffected==0){
                Toast.makeText(this, "Data Updated Failed", Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(this, "Data Updated", Toast.LENGTH_SHORT).show();
            }
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
            finish();
        }
        if (menuId==R.id.delete_btn){
            showDeleteConfirmationDialog();

        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection={
                BookEntry._ID,
                BookEntry.COLUMN_PRODUCT_NAME,
                BookEntry.COLUMN_PRICE,
                BookEntry.COLUMN_QUANTITY,
                BookEntry.COLUMN_SUPPLIER_NAME,
                BookEntry.COLUMN_SUPPLIER_PHONE_NUMBER
        };


        return new CursorLoader(
                this,
                mcurrenturi,
                projection,
                null,
                null,
                null
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data == null || data.getCount() < 1) {
            return;
        }

        if (data.moveToFirst()){

            int bookNameColumnIndex = data.getColumnIndex(BookEntry.COLUMN_PRODUCT_NAME);
            int bookPriceColumnIndex = data.getColumnIndex(BookEntry.COLUMN_PRICE);
            int bookQuantityColumnIndex = data.getColumnIndex(BookEntry.COLUMN_QUANTITY);
            int supplierNameColumnIndex = data.getColumnIndex(BookEntry.COLUMN_SUPPLIER_NAME);
            int supplierPhoneNumberColumnIndex = data.getColumnIndex(BookEntry.COLUMN_SUPPLIER_PHONE_NUMBER);

            String bookName = data.getString(bookNameColumnIndex);
            int bookPrice = data.getInt(bookPriceColumnIndex);
            int bookQuantity = data.getInt(bookQuantityColumnIndex);
            String supplierName = data.getString(supplierNameColumnIndex);
            String supplierPhoneNumber = data.getString(supplierPhoneNumberColumnIndex);

            mProductName.setText(bookName);
            mPrice.setText(Integer.toString(bookPrice));
            mQuantity.setText(Integer.toString(bookQuantity));
            mSupplierName.setText(supplierName);
            mSupplierPhoneNumber.setText(supplierPhoneNumber);
        }

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mProductName.setText("");
        mPrice.setText(0);
        mQuantity.setText(0);
        mSupplierName.setText("");
        mSupplierPhoneNumber.setText("");

    }

    private void showDeleteConfirmationDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_dialog);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                deleteBook();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });


        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void deleteBook() {

        if (mcurrenturi != null) {

            int rowsDeleted = getContentResolver().delete(mcurrenturi, null, null);

            if (rowsDeleted == 0) {

                Toast.makeText(this, "Delete Failed",
                        Toast.LENGTH_SHORT).show();
            } else {

                Toast.makeText(this, "Delete Successful",
                        Toast.LENGTH_SHORT).show();
            }
        }

        finish();
    }
}
