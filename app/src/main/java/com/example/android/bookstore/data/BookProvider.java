package com.example.android.bookstore.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.example.android.bookstore.data.BookContract.BookEntry;

public class BookProvider extends ContentProvider {

    BookDbHelper bookDbHelper;

    public static final String LOG_TAG = BookProvider.class.getSimpleName();

    public static final int BOOKS = 100;

    public static final int BOOKS_ID = 101;

    public static final UriMatcher mUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        mUriMatcher.addURI(BookContract.CONTENT_AUTHORITY, BookContract.PATH_BOOK, BOOKS);

        mUriMatcher.addURI(BookContract.CONTENT_AUTHORITY, BookContract.PATH_BOOK + "/#", BOOKS_ID);
    }

    @Override
    public boolean onCreate() {
        bookDbHelper = new BookDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection,
                        @Nullable String selection, @Nullable String[] selectionArgs,
                        @Nullable String sortOrder) {

        SQLiteDatabase database = bookDbHelper.getReadableDatabase();

        Cursor cursor;


        int matcher = mUriMatcher.match(uri);

        switch (matcher){
            case BOOKS:
                cursor = database.query(
                        BookEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            case BOOKS_ID:
                selection =BookEntry._ID+"=?";
                selectionArgs = new String[]{
                        String.valueOf(ContentUris.parseId(uri))
                };
                cursor = database.query(
                        BookEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );

                break;

                default:
                    throw new IllegalArgumentException("CanNot resolve uri"+uri);

        }
        cursor.setNotificationUri(getContext().getContentResolver(),uri);

        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        int matcher = mUriMatcher.match(uri);
        switch (matcher){
            case BOOKS:
                return BookEntry.CONTENT_LIST_TYPE;

            case BOOKS_ID:
                return BookEntry.CONTENT_ITEM_TYPE;

            default:
                throw  new IllegalArgumentException("Unknown Uri "+uri+"With Uri"+matcher);
        }

    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {

        final int match = mUriMatcher.match(uri);
        switch (match) {
            case BOOKS:
                return insertBook(uri,values);
            default:
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }

    }

    private Uri insertBook(Uri uri,ContentValues values){

        SQLiteDatabase database = bookDbHelper.getWritableDatabase();

        String bookName = values.getAsString(BookEntry.COLUMN_PRODUCT_NAME);
        if (bookName==null){
            throw new IllegalArgumentException("Enter Book Name");
        }

        String bookPrice = values.getAsString(BookEntry.COLUMN_PRICE);
        if (bookPrice==null){
            throw new IllegalArgumentException("Enter Book Price");
        }

        String bookQuantity = values.getAsString(BookEntry.COLUMN_QUANTITY);
        if (bookQuantity==null){
            throw new IllegalArgumentException("Enter Book Quantity");
        }

        String supplierName = values.getAsString(BookEntry.COLUMN_SUPPLIER_NAME);
        if (supplierName==null){
            throw new IllegalArgumentException("Enter Supplier Name");
        }

        String supplierPhoneNumber = values.getAsString(BookEntry.COLUMN_SUPPLIER_PHONE_NUMBER);
        if (supplierPhoneNumber==null){
            throw new IllegalArgumentException("Enter Supplier Phone Number");
        }

        long id = database.insert(BookEntry.TABLE_NAME,null,values);

        if (id==-1){
            Log.e(LOG_TAG,"Unable to insert"+uri);
        }
        getContext().getContentResolver().notifyChange(uri,null);

        return ContentUris.withAppendedId(uri,id);
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        SQLiteDatabase database = bookDbHelper.getWritableDatabase();
        int rowsDeleted;
        final  int match = mUriMatcher.match(uri);
        switch (match){
            case BOOKS:
                rowsDeleted=database.delete(BookEntry.TABLE_NAME,selection,selectionArgs);
                break;
            case BOOKS_ID:
                selection = BookEntry._ID+"=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                rowsDeleted=database.delete(BookEntry.TABLE_NAME,selection,selectionArgs);
                break;
            default:
                 throw  new IllegalArgumentException("deletion is not Supported for"+uri);
        }
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return rowsDeleted;
    }

    @Override
    public int update(@NonNull Uri uri,
                      @Nullable ContentValues values,
                      @Nullable String selection, @Nullable String[] selectionArgs) {

        final int match = mUriMatcher.match(uri);

        switch (match){
            case BOOKS:
                return updateBook(uri,values,selection,selectionArgs);
            case BOOKS_ID:
                selection = BookEntry._ID+"=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                return updateBook(uri,values,selection,selectionArgs);
            default:
                throw new IllegalArgumentException("Update Is Not Supported For The Uri"+uri);
        }

    }

    public int updateBook(Uri uri,ContentValues values,String selection,String[] selectionArgs){
        SQLiteDatabase database = bookDbHelper.getWritableDatabase();

        String bookName = values.getAsString(BookEntry.COLUMN_PRODUCT_NAME);
        if (bookName==null){
            throw new IllegalArgumentException("Enter Book Name");
        }

        String bookPrice = values.getAsString(BookEntry.COLUMN_PRICE);
        if (bookPrice==null){
            throw new IllegalArgumentException("Enter Book Price");
        }

        String bookQuantity = values.getAsString(BookEntry.COLUMN_QUANTITY);
        if (bookQuantity==null){
            throw new IllegalArgumentException("Enter Book Quantity");
        }

        String supplierName = values.getAsString(BookEntry.COLUMN_SUPPLIER_NAME);
        if (supplierName==null){
            throw new IllegalArgumentException("Enter Supplier Name");
        }

        String supplierPhoneNumber = values.getAsString(BookEntry.COLUMN_SUPPLIER_PHONE_NUMBER);
        if (supplierPhoneNumber==null){
            throw new IllegalArgumentException("Enter Supplier Phone Number");
        }

        if (values.size() == 0) {
            return 0;
        }

        int rowsUpdated = database.update(BookEntry.TABLE_NAME,values,selection,selectionArgs);

        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return rowsUpdated;
    }
}
