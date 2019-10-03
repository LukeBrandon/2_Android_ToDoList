package dev.lukeb.todolist;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;

import dev.lukeb.todolist.model.Todo;


//Create HomeActivity and implement the OnClick listener
public class ListActivity extends AppCompatActivity implements View.OnClickListener{

    private static final String TAG = "ListActivity";

    ArrayList<Todo> todos;
    TodoAdapter adapter;
    ToDoProvider toDoProvider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        toDoProvider = new ToDoProvider();

        initializeComponents();
        initRecyclerView();
    }

    //Set the OnClick Listener for buttons
    void initializeComponents(){
        findViewById(R.id.btnNewNote).setOnClickListener(this);
        findViewById(R.id.btnDeleteNote).setOnClickListener(this);
    }

    void initRecyclerView(){
        // Bind the recycler view
        RecyclerView rvTodos =  findViewById(R.id.rvTodos);

        todos = new ArrayList<>();
        adapter = new TodoAdapter(this, todos);

        rvTodos.setAdapter(adapter);
        rvTodos.setLayoutManager(new LinearLayoutManager(this));
    }

//    @Override
    public void onResume(){
        super.onResume();
        Log.d(TAG, "onResume: Getting updated Todos from SQLite DB");

        // Update the todos from the , clear then repopulate with newest data
        todos.clear();
        adapter.notifyDataSetChanged();     // This forces the recycler veiw to update

        //Set the projection for the columns to be returned
        String[] projection = {
                ToDoProvider.TODO_TABLE_COL_ID,
                ToDoProvider.TODO_TABLE_COL_TITLE,
                ToDoProvider.TODO_TABLE_COL_CONTENT,
                ToDoProvider.TODO_TABLE_COL_DUE_DATE};

        //Perform a query to get all rows in the DB
        Cursor mCursor = getContentResolver().query(ToDoProvider.CONTENT_URI,projection,null,null,null);

        int idColumnIndex = mCursor.getColumnIndex(ToDoProvider.TODO_TABLE_COL_ID);
        int titleColumnIndex=mCursor.getColumnIndex(ToDoProvider.TODO_TABLE_COL_TITLE);
        int contentColumnIndex = mCursor.getColumnIndex(ToDoProvider.TODO_TABLE_COL_CONTENT);
        int dateColumnIndex = mCursor.getColumnIndex(ToDoProvider.TODO_TABLE_COL_DUE_DATE);

        mCursor.moveToFirst();
        while(!mCursor.isAfterLast()) {
            Log.d(TAG, "onResume: title from DB: " + mCursor.getString(titleColumnIndex));
            Log.d(TAG, "onResume: content from DB: " + mCursor.getString(contentColumnIndex));
            Todo newTerm = new Todo(mCursor.getInt(idColumnIndex), mCursor.getString(titleColumnIndex), mCursor.getString(contentColumnIndex), mCursor.getString(dateColumnIndex), false); // SHOULD GET TEH VALUE FROM THE DB
            todos.add(newTerm);
            mCursor.moveToNext();
        }
    }

    @Override
    public void onClick(View v){
        switch (v.getId()){
            //If new Note, call createNewNote()
            case R.id.btnNewNote:
//                if(Build.VERSION >= 23){
//                      // Create alarm in the new way, need to give it AlarmReceiver.class which is made by new -> other -> BroadcastReceiver
//                } else {
//
//                }
                Log.d(TAG, "onClick: Starting NoteActivity from ListActivity OnClick");
                Intent intent = new Intent(this, NoteActivity.class);
                intent.putExtra("isUpdate", false);

                startActivity(intent);
                break;
            //If delete note, call deleteNewestNote()
            case R.id.btnDeleteNote:
                deleteNewestNote();
                break;
            //This shouldn't happen
            default:
                Log.e(TAG, "onClick: ERROR, something that should not have been called was called");
                break;
        }
    }


    //Delete the newest note placed into the database
    void deleteNewestNote(){
        //Create the projection for the query
        String[] projection = {
                ToDoProvider.TODO_TABLE_COL_ID,
                ToDoProvider.TODO_TABLE_COL_TITLE,
                ToDoProvider.TODO_TABLE_COL_CONTENT,
                ToDoProvider.TODO_TABLE_COL_DUE_DATE};

        //Perform the query, with ID Descending
        Cursor myCursor = getContentResolver().query(ToDoProvider.CONTENT_URI,projection,null,null,"_ID DESC");
        if(myCursor != null & myCursor.getCount() > 0) {
            //Move the cursor to the beginning
            myCursor.moveToFirst();
            //Get the ID (int) of the newest note (column 0)
            int newestId = myCursor.getInt(0);
            //Delete the note
            int didWork = getContentResolver().delete(Uri.parse(ToDoProvider.CONTENT_URI + "/" + newestId), null, null);
            //If deleted, didWork returns the number of rows deleted (should be 1)
            if (didWork == 1) {
                //If it didWork, then create a Toast Message saying that the note was deleted
                Toast.makeText(getApplicationContext(), "Deleted Note " + newestId, Toast.LENGTH_LONG).show();
            }
        } else{
            Toast.makeText(getApplicationContext(), "No Note to delete!", Toast.LENGTH_LONG).show();

        }
    }

}
