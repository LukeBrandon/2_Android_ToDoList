package dev.lukeb.todolist;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

public class ToDoProvider extends ContentProvider {
    private static final String TAG = "ToDoProvider";

    private static final String DBNAME = "ToDoDB";
    // Authority is the package name
    private static final String AUTHORITY = "dev.lukeb.todolist";

    private static final String TABLE_NAME = "ToDoList";

    public static final String TODO_TABLE_COL_ID = "_ID";
    public static final String TODO_TABLE_COL_TITLE = "TITLE";
    public static final String TODO_TABLE_COL_CONTENT = "CONTENT";
    //public static final boolean TODO_TABLE_COL_DONE = false;
    public static final String TODO_TABLE_COL_DUE_DATE = "DUE_DATE";

    public static final Uri CONTENT_URI =
            Uri.parse("content://" + AUTHORITY + "/"+TABLE_NAME);

    //Table create string based on column names
    private static final String SQL_CREATE_MAIN = "CREATE TABLE " +
            TABLE_NAME+ " " +                       // Table's name
            "(" +                           // The columns in the table
            TODO_TABLE_COL_ID + " INTEGER PRIMARY KEY, " +
            TODO_TABLE_COL_TITLE + " TEXT," +
            TODO_TABLE_COL_CONTENT + " TEXT," +
            //TODO_TABLE_COL_DONE + " BOOLEAN," +
            TODO_TABLE_COL_DUE_DATE + " TEXT)";


    //URI Matcher object to facilitate switch cases between URIs
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    private MainDatabaseHelper mOpenHelper;

    //Constructor adds two URIs, use for case statements
    public ToDoProvider() {
        //Match to the authority and the table name, assign 1
        sUriMatcher.addURI(AUTHORITY,TABLE_NAME,1);
        //Match to the authority and the table name, and an ID, assign 2
        sUriMatcher.addURI(AUTHORITY,TABLE_NAME+"/#",2);
        onCreate();
    }

    @Override
    public boolean onCreate() {
        Log.d(TAG, "onCreate: Creating mOpenHelper");
        mOpenHelper = new MainDatabaseHelper(getContext());
        return true;
    }

    //Delete functionality for the Content Provider
    //Pass the URI for the table and the ID to be deleted
    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        switch (sUriMatcher.match(uri)){
            //Match on URI with ID
            case 2:
                String id = uri.getPathSegments().get(1);
                selection = TODO_TABLE_COL_ID + "=" + id +
                        (!TextUtils.isEmpty(selection) ? "AND (" + selection  + ")" : "");
                break;
            default:
                //Else, error is thrown
                throw new IllegalArgumentException("Unsupported URI: " + uri);
        }
        //Delete from the database, return integer value for how many rows are deleted
        int deleteCount = mOpenHelper.getWritableDatabase().delete(
                TABLE_NAME,selection,selectionArgs);
        //Notify calling context
        getContext().getContentResolver().notifyChange(uri,null);
        //Return number of rows deleted (if any)
        return deleteCount;
    }

    //Not implemented. Would return the MIME type requests
    @Override
    public String getType(Uri uri) {
        // TODO: Implement this to handle requests for the MIME type of the data
        // at the given URI.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    //Insert functionality of the Content Provider
    //Pass a ContentValues object with the values to be inserted
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        switch (sUriMatcher.match(uri)){
            //Match against a URI with just the table name
            case 1:
                break;
            default:
                //Otherwise, error is thrown
                Log.e(TAG, "URI not recognized " + uri);
        }
        //Insert into the table, return the id of the inserted row
        long id = mOpenHelper.getWritableDatabase().insert(TABLE_NAME,null,values);
        //Notify context of change
        getContext().getContentResolver().notifyChange(uri,null);
        //Return the URI with the ID at the end
        return Uri.parse(CONTENT_URI+"/" + id);
    }

    //Query Functionality for the Content Provider
    //Pass either the table name, or the table name with an ID
    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {

        //Use an SQLiteQueryBuilder object to create the query
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        //Set the table to be queried
        queryBuilder.setTables(TABLE_NAME);

        //Match on either the URI with or without an ID
        switch (sUriMatcher.match(uri)) {
            case 1:
                //If no ID, and no sort order, specify the sort order as by ID Ascending
                if (TextUtils.isEmpty(sortOrder)) sortOrder = "_ID ASC";
                break;
            case 2:
                //Otherwise, set the selection of the URI to the ID
                selection = selection + "_ID = " + uri.getLastPathSegment();
                break;
            default:
                Log.e(TAG, "URI not recognized " + uri);
        }
        //Query the database based on the columns to be returned, the selection criteria and
        // arguments, and the sort order
        Cursor cursor = queryBuilder.query(mOpenHelper.getWritableDatabase(), projection, selection,
                selectionArgs, null, null, sortOrder);
        //Return the cursor object
        return cursor;
    }

    //Update functionality for the Content Provider
    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        switch (sUriMatcher.match(uri)){
            case 1:
                //Allow update based on multiple selections
                break;
            case 2:
                //Allow updates based on a single ID
                String id = uri.getPathSegments().get(1);
                selection = TODO_TABLE_COL_ID + "=" + id +
                        (!TextUtils.isEmpty(selection) ?
                        "AND (" + selection + ")" : "");
                break;
            default:
                throw new IllegalArgumentException("Unsupported URI: " + uri);
        }
        //Perform updates and return the number that were updated
        int updateCount = mOpenHelper.getWritableDatabase().update(TABLE_NAME,values,
                selection,selectionArgs);
        //Notify the context
        getContext().getContentResolver().notifyChange(uri,null);
        //Return the number of rows updated
        return updateCount;
    }


    protected static final class MainDatabaseHelper extends SQLiteOpenHelper {

        MainDatabaseHelper(Context context) {
            super(context, DBNAME, null, 1);
        }

        public void onCreate(SQLiteDatabase db) {
            db.execSQL(SQL_CREATE_MAIN);
        }

        public void onUpgrade(SQLiteDatabase db, int int1, int int2){
            db.execSQL("DROP TABLE IF EXISTS " + ToDoProvider.TABLE_NAME);
            onCreate(db);
        }
    }

}


