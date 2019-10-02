package dev.lukeb.todolist;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);


        initializeComponents();
        getIntentIfPossible();
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
                Log.d(TAG, "onClick: Saving new note with title: \"" + etNoteTitle.getText() + "\" and content: \"" + etNoteContent.getText() + "\"");

                ContentValues myCV = new ContentValues();

                // Put key_value pairs based on the column names, and the values
                myCV.put(ToDoProvider.TODO_TABLE_COL_TITLE, etNoteTitle.getText().toString());
                myCV.put(ToDoProvider.TODO_TABLE_COL_CONTENT, etNoteContent.getText().toString());
                myCV.put(ToDoProvider.TODO_TABLE_COL_DUE_DATE, "9/2/19");
                //myCV.put(ToDoProvider.TODO_TABLE_COL_DONE, false);

                // Perform the insert function using the ContentProvider
                getContentResolver().insert(ToDoProvider.CONTENT_URI,myCV);

                // Creates intent to pass back to the list activity when save button is clicked
                Intent intentToPassBackToListActivity = new Intent();
                intentToPassBackToListActivity.putExtra("todo_title", etNoteTitle.getText().toString());
                intentToPassBackToListActivity.putExtra("todo_content", etNoteContent.getText().toString());
                setResult(RESULT_OK, intentToPassBackToListActivity);
                finish();
            }
        });
    }

    void getIntentIfPossible(){
        Log.d(TAG, "getIntentIfPossible: Checking Intent for extras");

        Intent intent = getIntent();
        if(intent.hasExtra("todo_title") && intent.hasExtra("todo_description")){
            Log.d(TAG, "getIntentIfPossible: Has extras on Intent");
            etNoteTitle.setText(intent.getStringExtra("todo_title"));
            etNoteContent.setText(intent.getStringExtra("todo_description"));
        } else {
            Log.d(TAG, "getIntentIfPossible: Intent has no extras.  Is a new Note");
        }
        
        if(intent.hasExtra("todos")){
            Log.d(TAG, "getIntentIfPossible: Has todos on intent, ");
        }
    }

    @Override
    public void onClick(View v) {
        Log.d(TAG, "onClick: Something clicked.");
    }

}
