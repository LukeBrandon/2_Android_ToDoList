package dev.lukeb.todolist;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

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
        Log.d(TAG, "onBindViewHolder: called");

        // Get the data model based on position
        Todo todo = mTodos.get(position);

        // Set item views based on your views and data model
        viewHolder.todoTextView.setText(mTodos.get(position).getTitle());
        viewHolder.doneButton.setText(mTodos.get(position).getDone() ? "DONE" : "NOT DONE");

        // Onclick listener for the individual views in the RecyclerView
        viewHolder.parentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                Log.d(TAG, "onClick: clicked on: " + mTodos.get(position));

                Toast.makeText(mContext, mTodos.get(position).getTitle(), Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(mContext, NoteActivity.class);
                intent.putExtra("todo_title", mTodos.get(position).getTitle());
                intent.putExtra("todo_description", mTodos.get(position).getDescription());
                mContext.startActivity(intent);
            }
        });
    }

    // Returns the total count of items in the list
    @Override
    public int getItemCount() {
        return mTodos.size();
    }

    public void addTodo(Todo todo){
        this.mTodos.add(todo);
    }


    // Used to hold each individual recycler view view (list components)
    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView todoTextView;
        public Button doneButton;
        LinearLayout parentLayout;

        public ViewHolder(View itemView) {
            super(itemView);

            todoTextView = (TextView) itemView.findViewById(R.id.todo_name);
            doneButton = (Button) itemView.findViewById(R.id.todo_button);
            parentLayout = (LinearLayout) itemView.findViewById(R.id.parent_layout);
        }
    }
}
