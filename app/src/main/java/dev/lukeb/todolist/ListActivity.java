package dev.lukeb.todolist;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import dev.lukeb.todolist.model.ToDoProvider;
import dev.lukeb.todolist.model.Todo;
import dev.lukeb.todolist.view.TodoAdapter;


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

    void initializeComponents(){
        findViewById(R.id.btnNewNote).setOnClickListener(this);
    }

    void initRecyclerView(){
        // Bind the recycler view
        RecyclerView rvTodos =  findViewById(R.id.rvTodos);

        todos = new ArrayList<>();
        adapter = new TodoAdapter(this, todos);

        rvTodos.setAdapter(adapter);
        rvTodos.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
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
                ToDoProvider.TODO_TABLE_COL_DUE_DATE,
                ToDoProvider.TODO_TABLE_COL_DONE};

        //Perform a query to get all rows in the DB
        Cursor mCursor = getContentResolver().query(ToDoProvider.CONTENT_URI,projection,null,null,null);

        int idColumnIndex = mCursor.getColumnIndex(ToDoProvider.TODO_TABLE_COL_ID);
        int titleColumnIndex = mCursor.getColumnIndex(ToDoProvider.TODO_TABLE_COL_TITLE);
        int contentColumnIndex = mCursor.getColumnIndex(ToDoProvider.TODO_TABLE_COL_CONTENT);
        int dateColumnIndex = mCursor.getColumnIndex(ToDoProvider.TODO_TABLE_COL_DUE_DATE);
        int doneColumnIndex = mCursor.getColumnIndex(ToDoProvider.TODO_TABLE_COL_DONE);

        mCursor.moveToFirst();
        while(!mCursor.isAfterLast()) {
            Todo newTerm = new Todo(mCursor.getInt(idColumnIndex), mCursor.getString(titleColumnIndex), mCursor.getString(contentColumnIndex),
                    mCursor.getLong(dateColumnIndex), mCursor.getInt(doneColumnIndex)==1);
            todos.add(newTerm);
            mCursor.moveToNext();
        }
    }

    @Override
    public void onClick(View v){
        switch (v.getId()){
            //If new Note, call createNewNote()
            case R.id.btnNewNote:

                Intent intent = new Intent(this, NoteActivity.class);
                intent.putExtra("isUpdate", false);

                startActivity(intent);

                break;
            //This shouldn't happen
            default:
                throw new UnsupportedOperationException("ERROR, something that should not have been called was called in the lsit activity onClick()");
        }
    }


}
