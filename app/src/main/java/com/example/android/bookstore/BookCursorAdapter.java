package com.example.android.bookstore;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.example.android.bookstore.data.BookContract.BookEntry;

public class BookCursorAdapter extends CursorAdapter {



    private Context mContext;


    public BookCursorAdapter(Context context, Cursor c) {
        super(context, c, 0);

        mContext = context;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    }

    @Override
    public void bindView(View view, final Context context, final Cursor cursor) {



        TextView bookNameTextView = (TextView) view.findViewById(R.id.book_name);
        TextView bookPriceTextView = (TextView) view.findViewById(R.id.book_price);
        TextView bookQuantityTextView = (TextView) view.findViewById(R.id.book_quantity);
        Button sale = (Button) view.findViewById(R.id.btn_sale);

        int bookIdColumnIndex = cursor.getColumnIndex(BookEntry._ID);
        int bookNameColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_PRODUCT_NAME);
        int bookPriceColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_PRICE);
        final int bookQuantityColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_QUANTITY);

        final int bookId = cursor.getInt(bookIdColumnIndex);
        String bookName = cursor.getString(bookNameColumnIndex);
        final int[] bookPrice = {cursor.getInt(bookPriceColumnIndex)};
        final int[] bookQuantity = {cursor.getInt(bookQuantityColumnIndex)};

        bookNameTextView.setText(bookName);
        bookPriceTextView.setText(Integer.toString(bookPrice[0]));
        bookQuantityTextView.setText(Integer.toString(bookQuantity[0]));

        /**
         * Sale Button Clicked
         */

        sale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mContext = context;
                if (bookQuantity[0] > 0) {

                    bookQuantity[0]--;
                    Uri currentUri = ContentUris.withAppendedId(BookEntry.CONTENT_URI,bookId);
                    ContentValues values = new ContentValues();
                    values.put(BookEntry.COLUMN_QUANTITY, bookQuantity[0]);
                   mContext.getContentResolver().update(currentUri,values,null,null);

                }
            }
        });

    }
}
