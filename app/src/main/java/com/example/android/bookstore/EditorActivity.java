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
import android.text.Editable;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
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

    int quantity;

    public static final int EXISTING_BOOK_LOADER = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        Intent intent = getIntent();
        mcurrenturi = intent.getData();
        if (mcurrenturi == null) {
            setTitle(getString(R.string.new_data));
            invalidateOptionsMenu();
        } else {
            setTitle(getString(R.string.edit_data));
            getLoaderManager().initLoader(EXISTING_BOOK_LOADER, null, this);
        }

        mProductName = (EditText) findViewById(R.id.product_name);
        mPrice = (EditText) findViewById(R.id.price);
        mQuantity = (EditText) findViewById(R.id.quantity);
        mSupplierName = (EditText) findViewById(R.id.supplier_name);
        mSupplierPhoneNumber = (EditText) findViewById(R.id.supplier_number);
        mIncrementButton = (Button) findViewById(R.id.positive_btn);
        mDecrementButton = (Button) findViewById(R.id.negative_btn);
        mCallButton = (Button) findViewById(R.id.call_btn);



        /**
         * Increment button which increase the quantity
         */
        mIncrementButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                quantity = Integer.parseInt(mQuantity.getText().toString());
                quantity++;
                mQuantity.setText(Integer.toString(quantity));
            }
        });

        /**
         * Decrement button which decrease the quantity
         */

        mDecrementButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (quantity > 0) {
                    quantity = Integer.parseInt(mQuantity.getText().toString());
                    quantity--;
                    mQuantity.setText(Integer.toString(quantity));
                }
            }
        });

        /**
         * call Button which make a call to the given number
         */

        mCallButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:" + mSupplierPhoneNumber.getText().toString()));
                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivity(intent);
                }
            }
        });

    }

    /**
     * Insert book data in database
     */

    private void insertData() {

        String productNameString = mProductName.getText().toString().trim();
        String priceInt = String.valueOf(mPrice.getText().toString().trim());
        String quantityInt = String.valueOf(mQuantity.getText().toString().trim());
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


        if (mcurrenturi == null) {
            Uri uri = getContentResolver().insert(BookEntry.CONTENT_URI, values);
            if (uri == null) {
                Toast.makeText(this, "Insertion failed", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Data Saved Successfully", Toast.LENGTH_SHORT).show();
            }
        } else {
            int rowsAffected = getContentResolver().update(mcurrenturi, values, null, null);
            if (rowsAffected == 0) {
                Toast.makeText(this, "Data Updated Failed", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Data Updated", Toast.LENGTH_SHORT).show();
            }
        }

        finish();
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
        if (menuId == R.id.delete_btn) {
            showDeleteConfirmationDialog();
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = {
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

        if (data.moveToFirst()) {

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
            mPrice.setText(String.valueOf(bookPrice));
            mQuantity.setText(String.valueOf(bookQuantity));
            mSupplierName.setText(supplierName);
            mSupplierPhoneNumber.setText(supplierPhoneNumber);
        }

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mProductName.setText("");
        mPrice.setText("");
        mQuantity.setText("");
        mSupplierName.setText("");
        mSupplierPhoneNumber.setText("");

    }

    /**
     * Show Confirmation dialog to delete book data
     */

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

    /**
     * DeleteBook Method which helps To delete Book data
     */

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
