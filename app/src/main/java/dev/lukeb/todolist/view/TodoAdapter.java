package dev.lukeb.todolist.view;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import dev.lukeb.todolist.NoteActivity;
import dev.lukeb.todolist.R;
import dev.lukeb.todolist.model.*;

import java.util.List;

public class TodoAdapter extends RecyclerView.Adapter<TodoAdapter.ViewHolder>{
    private static final String TAG = "TodoAdapter";

    private List<Todo> mTodos;
    private Context mContext;

    public TodoAdapter(Context context, List<Todo> todos) {
        mTodos = todos;
        mContext = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        // Inflate the custom layout
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.todo_list_item, parent, false);

        // Return a new holder instance
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    // Involves populating data into the item through holder
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {

        // Set item views based on the data model
        viewHolder.todoTextView.setText(mTodos.get(position).getTitle());
        viewHolder.doneCheckBox.setChecked(mTodos.get(position).getDone());

        viewHolder.doneCheckBox.setOnClickListener(v -> {

            ContentValues myCV = new ContentValues();
            myCV.put(ToDoProvider.TODO_TABLE_COL_TITLE, mTodos.get(position).getTitle());
            myCV.put(ToDoProvider.TODO_TABLE_COL_CONTENT, mTodos.get(position).getDescription());
            myCV.put(ToDoProvider.TODO_TABLE_COL_DUE_DATE, mTodos.get(position).getDate());
            myCV.put(ToDoProvider.TODO_TABLE_COL_DONE, !mTodos.get(position).getDone());

            // Perform the insert function or update depending on isUpdate
            int id = getIdAtPosFromDb(position);

            // Update the db with the new value for done
            Uri uri = Uri.parse(ToDoProvider.CONTENT_URI.toString() + "/" + id);
            mContext.getContentResolver().update(uri, myCV, null, null);

            // Toggle the due date in the todos list
            mTodos.get(position).setDone(!mTodos.get(position).getDone());
        });


        // Onclick listener for the individual views in the RecyclerView
        viewHolder.parentLayout.setOnClickListener(v -> {

                Log.d(TAG, "onClick: clicked on: " + mTodos.get(position));

                Intent intent = new Intent(mContext, NoteActivity.class);
                intent.putExtra("todo_title", mTodos.get(position).getTitle());
                intent.putExtra("todo_description", mTodos.get(position).getDescription());
                intent.putExtra("todo_date", mTodos.get(position).getDate());
                intent.putExtra("todo_done", mTodos.get(position).getDone());
                intent.putExtra("todo_position", position+1);  // position+1 is id in the DB
                intent.putExtra("isUpdate", true);
                mContext.startActivity(intent);
        });
    }

    public int getIdAtPosFromDb(int posInDb){
        String[] projection = {
                ToDoProvider.TODO_TABLE_COL_ID,
                ToDoProvider.TODO_TABLE_COL_TITLE,
                ToDoProvider.TODO_TABLE_COL_CONTENT,
                ToDoProvider.TODO_TABLE_COL_DUE_DATE};

        // Selecting everything from table
        Uri queryUri = Uri.parse(ToDoProvider.CONTENT_URI.toString());

        //Perform the query, with ID Descending
        Cursor cursor = mContext.getContentResolver().query(queryUri, projection, null, null, null);
        int idColumnIndex = cursor.getColumnIndex(ToDoProvider.TODO_TABLE_COL_ID);

        // Cursor moved to the position in the DB to delete
        cursor.moveToPosition(posInDb);

        // Now that cursor is at the correct location, we can return that
        return cursor.getInt(idColumnIndex);
    }

    // Returns the total count of items in the list
    @Override
    public int getItemCount() {
        return mTodos.size();
    }


    // Used to hold each individual recycler view view (list components)
    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView todoTextView;
        public CheckBox doneCheckBox;
        LinearLayout parentLayout;

        public ViewHolder(View itemView) {
            super(itemView);

            todoTextView = (TextView) itemView.findViewById(R.id.todo_name);
            doneCheckBox = (CheckBox) itemView.findViewById(R.id.todo_doneCheckBox);
            parentLayout = (LinearLayout) itemView.findViewById(R.id.parent_layout);
        }
    }
}
