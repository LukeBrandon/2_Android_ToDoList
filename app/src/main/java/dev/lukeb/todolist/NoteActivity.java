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
import android.widget.EditText;
import android.widget.TextView;


public class NoteActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "NoteActivity";

    TextView etNoteTitle;
    EditText etNoteContent;
    EditText etDatePicker;
    Button btnSave;
    boolean isUpdate;
    int id;


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

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: Saving note with title: \"" + etNoteTitle.getText() + "\" and content: \"" + etNoteContent.getText() + "\"");

                ContentValues myCV = new ContentValues();

                // Put key_value pairs based on the column names, and the values
                myCV.put(ToDoProvider.TODO_TABLE_COL_TITLE, etNoteTitle.getText().toString());
                myCV.put(ToDoProvider.TODO_TABLE_COL_CONTENT, etNoteContent.getText().toString());
                myCV.put(ToDoProvider.TODO_TABLE_COL_DUE_DATE, etDatePicker.getText().toString());
                //myCV.put(ToDoProvider.TODO_TABLE_COL_DONE, false);

                // Perform the insert function or update depending on isUpdate
                if(isUpdate) {

                    // Todo provider update function takes the id to be updated appended to the URI
                    Uri uri = Uri.parse(ToDoProvider.CONTENT_URI.toString() + "/" + id);
                    Log.d(TAG, "onClick: Updating Note with ID: " + id + " // and URI: " + uri.toString());
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

    void getIntentFromListActivity(){
        Log.d(TAG, "getIntentIfPossible: Checking Intent for extras");

        Intent intent = getIntent();
        if(intent.hasExtra("todo_title") && intent.hasExtra("todo_description") && intent.hasExtra("isUpdate")){
            Log.d(TAG, "getIntentIfPossible: Has extras on Intent");
            etNoteTitle.setText(intent.getStringExtra("todo_title"));
            etNoteContent.setText(intent.getStringExtra("todo_description"));
            this.isUpdate = intent.getBooleanExtra("isUpdate", false);
            this.id = intent.getIntExtra("todo_id", -1); // Used when updating a todo instead of adding a new one
        } else {
            Log.d(TAG, "getIntentIfPossible: Intent has no extras");
        }

    }

    @Override
    public void onClick(View v) {
        Log.d(TAG, "onClick: Something clicked.");
    }

}
