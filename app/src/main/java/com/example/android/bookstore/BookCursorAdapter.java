package com.example.android.bookstore;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.TextView;
import com.example.android.bookstore.data.BookContract.BookEntry;

public class BookCursorAdapter extends CursorAdapter {

    public BookCursorAdapter(Context context, Cursor c) {
        super(context, c, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        TextView bookNameTextView = (TextView)view.findViewById(R.id.book_name);
        TextView bookPriceTextView = (TextView)view.findViewById(R.id.book_price);
        TextView bookQuantityTextView = (TextView)view.findViewById(R.id.book_quantity);
        Button sale = (Button)view.findViewById(R.id.btn_sale);

        int bookNameColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_PRODUCT_NAME);
        int bookPriceColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_PRICE);
        int bookQuantityColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_QUANTITY);

        String bookName = cursor.getString(bookNameColumnIndex);
        int bookPrice = cursor.getInt(bookPriceColumnIndex);
        int bookQuantity = cursor.getInt(bookQuantityColumnIndex);

        bookNameTextView.setText(bookName);
        bookPriceTextView.setText(Integer.toString(bookPrice));
        bookQuantityTextView.setText(Integer.toString(bookQuantity));

    }
}
