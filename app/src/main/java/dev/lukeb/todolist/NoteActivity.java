package dev.lukeb.todolist;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;


public class NoteActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "NoteActivity";

    TextView etNoteTitle;
    EditText etNoteContent;
    EditText etDatePicker;

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
    }

    @Override
    public void onClick(View v) {
        Log.d(TAG, "onClick: Something clicked.");

        switch (v.getId()) {
            case R.id.btnSave:
                //Write what is in the new Note to the db
                Log.d(TAG, "onClick: Saving new note with title: \"" + etNoteTitle.getText() + "\" and content: \"" + etNoteContent.getText() + "\"");
                break;
            default:
                Log.d(TAG, "onClick: Nothing clickable was clicked ");
        }
    }

}
