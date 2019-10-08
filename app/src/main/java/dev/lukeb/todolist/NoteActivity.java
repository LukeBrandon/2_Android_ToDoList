package dev.lukeb.todolist;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import dev.lukeb.todolist.model.ToDoProvider;


public class NoteActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "NoteActivity";

    TextView etNoteTitle;
    EditText etNoteContent;
    EditText etDatePicker;
    Button btnCancel;
    Button btnSave;
    Button btnDelete;
    CheckBox cbDone;
    boolean isUpdate;
    int position; // Index of the item in recycler view that was pressed


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);

        initializeComponents();
        getIntentFromListActivity();
    }

    //Set the OnClick Listener for buttons
    void initializeComponents() {
        etNoteTitle = (EditText) findViewById(R.id.etNoteTitle);
        etNoteContent = (EditText) findViewById(R.id.etNoteContent);
        etDatePicker = (EditText) findViewById(R.id.etDatePicker);
        btnSave = (Button) findViewById(R.id.btnSave);
        btnCancel = (Button) findViewById(R.id.btnCancel);
        btnDelete = (Button) findViewById(R.id.btnDelete);
        cbDone = (CheckBox) findViewById(R.id.cbDone);

        btnCancel.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                finish();
            }
        });

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: Deleting note with position in recycler view of " + position);

                // Adjust for 0 index of db
                int id = getIdAtPosFromDb(position-1);

                // Make Uri for
                Uri deleteUri = Uri.parse(ToDoProvider.CONTENT_URI + "/" + id);

                // Delete the todo
                int didWork = getContentResolver().delete(deleteUri, null, null);

                if (didWork == 1) {
                    Log.d(TAG, "onClick: Deletion of note position: " + position + " worked!");
                    Toast.makeText(getApplicationContext(), "Deleted Note " + position, Toast.LENGTH_LONG).show();
                    finish();
                } else {
                    Toast.makeText(getApplicationContext(), "Failed to delete note", Toast.LENGTH_LONG).show();
                }
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: Saving note with title: \"" + etNoteTitle.getText() + "\" and content: \"" + etNoteContent.getText() + "\"");

                ContentValues myCV = new ContentValues();
                myCV.put(ToDoProvider.TODO_TABLE_COL_TITLE, etNoteTitle.getText().toString());
                myCV.put(ToDoProvider.TODO_TABLE_COL_CONTENT, etNoteContent.getText().toString());
                myCV.put(ToDoProvider.TODO_TABLE_COL_DUE_DATE, etDatePicker.getText().toString());
                myCV.put(ToDoProvider.TODO_TABLE_COL_DONE, cbDone.isChecked());


                // Perform the insert function or update depending on isUpdate
                if(isUpdate) {
                    int id = getIdAtPosFromDb(position-1);

                    // Todo provider update function takes the id to be updated appended to the URI
                    Uri uri = Uri.parse(ToDoProvider.CONTENT_URI.toString() + "/" + id);
                    Log.d(TAG, "onClick: Updating Note with position: " + position + " // and URI: " + uri.toString());
                    getContentResolver().update(uri, myCV, null, null);
                } else{

                    Log.d(TAG, "onClick: making new note");
                    getContentResolver().insert(ToDoProvider.CONTENT_URI, myCV);
                }

                // Finishes the activity, taking us back to the list activity
                finish();
            }
        });
    }

    private void getIntentFromListActivity(){
        Log.d(TAG, "getIntentIfPossible: Checking Intent for extras");

        Intent intent = getIntent();
        if(intent.hasExtra("todo_title") && intent.hasExtra("todo_description") && intent.hasExtra("todo_position")
                                            && intent.hasExtra("isUpdate") && intent.hasExtra("todo_done")){

            Log.d(TAG, "getIntentIfPossible: Has extras on Intent");
            etNoteTitle.setText(intent.getStringExtra("todo_title"));
            etNoteContent.setText(intent.getStringExtra("todo_description"));
            cbDone.setChecked(intent.getBooleanExtra("todo_done", false));
            this.isUpdate = intent.getBooleanExtra("isUpdate", false);
            this.position = intent.getIntExtra("todo_position", -1); // Used when updating a todo instead of adding a new one
        } else {
            Log.d(TAG, "getIntentIfPossible: Intent has no extras");
        }

    }

    public int getIdAtPosFromDb(int posInDb){
        String[] projection = {
                ToDoProvider.TODO_TABLE_COL_ID,
                ToDoProvider.TODO_TABLE_COL_TITLE,
                ToDoProvider.TODO_TABLE_COL_CONTENT,
                ToDoProvider.TODO_TABLE_COL_DUE_DATE};

        // Selecting everything from table
        Uri queryUri = Uri.parse(ToDoProvider.CONTENT_URI.toString() + "/*");

        //Perform the query, with ID Descending
        Cursor cursor = getContentResolver().query(queryUri,projection,null,null,null);
        int idColumnIndex = cursor.getColumnIndex(ToDoProvider.TODO_TABLE_COL_ID);

        // Cursor moved to the position in the DB to delete
        cursor.moveToPosition(posInDb);

        // Now that cursor is at the correct location, we can return that
        return cursor.getInt(idColumnIndex);
    }

    @Override
    public void onClick(View v) {
        Log.d(TAG, "onClick: Something clicked.");
    }

}
