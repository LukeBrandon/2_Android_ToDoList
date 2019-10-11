package dev.lukeb.todolist;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import java.util.Calendar;

import dev.lukeb.todolist.model.ToDoProvider;
import dev.lukeb.todolist.util.NotificationHandler;


public class NoteActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "NoteActivity";

    Calendar calendar;
    TextView etNoteTitle;
    EditText etNoteContent;
    EditText etDatePicker;
    Button btnCancel;
    Button btnSave;
    Button btnDelete;
    CheckBox cbDone;
    TimePicker tpTimePicker;
    boolean isUpdate;
    int position; // Index of the item in recycler view


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
        etDatePicker = (EditText) findViewById(R.id.dpDatePicker);
        btnSave = (Button) findViewById(R.id.btnSave);
        btnCancel = (Button) findViewById(R.id.btnCancel);
        btnDelete = (Button) findViewById(R.id.btnDelete);
        cbDone = (CheckBox) findViewById(R.id.cbDone);
        tpTimePicker = (TimePicker) findViewById(R.id.tpTimePicker);
        calendar = Calendar.getInstance();

        btnCancel.setOnClickListener(v -> {
                finish();
        });

        btnDelete.setOnClickListener(v -> {
            Log.d(TAG, "onClick: Deleting note with position in recycler view of " + position);

            // Adjust for 0 index of db
            int idInDb = getIdAtPosFromDb(position-1);

            // Make Uri for
            Uri deleteUri = Uri.parse(ToDoProvider.CONTENT_URI + "/" + idInDb);

            // Delete the todo
            int didWork = getContentResolver().delete(deleteUri, null, null);

            // Cancel the notification
            scheduleNotification(etNoteTitle.getText().toString(), etNoteContent.getText().toString(), idInDb, true);

            if (didWork == 1) {
                Log.d(TAG, "onClick: Deletion of note position: " + position + " worked!");
                Toast.makeText(getApplicationContext(), "Deleted Note " + position, Toast.LENGTH_LONG).show();
                finish();
            } else {
                Toast.makeText(getApplicationContext(), "Failed to delete note", Toast.LENGTH_LONG).show();
            }
        });

        btnSave.setOnClickListener(v-> {
            Log.d(TAG, "onClick: Saving note with title: \"" + etNoteTitle.getText() + "\" and content: \"" + etNoteContent.getText() + "\"");

            saveDueTimeInCalendar();
            ContentValues myCV = new ContentValues();
            myCV.put(ToDoProvider.TODO_TABLE_COL_TITLE, etNoteTitle.getText().toString());
            myCV.put(ToDoProvider.TODO_TABLE_COL_CONTENT, etNoteContent.getText().toString());
            myCV.put(ToDoProvider.TODO_TABLE_COL_DUE_DATE, calendar.getTimeInMillis());
            myCV.put(ToDoProvider.TODO_TABLE_COL_DONE, cbDone.isChecked());

            // Perform the insert function or update depending on isUpdate
            int idInDb;
            if(isUpdate) {
                idInDb = getIdAtPosFromDb(position-1);

                // Todo provider update function takes the id to be updated appended to the URI
                Uri uri = Uri.parse(ToDoProvider.CONTENT_URI.toString() + "/" + idInDb);

                // Update in the DB
                getContentResolver().update(uri, myCV, null, null);

            } else{
                // Inserting new note into DB
                getContentResolver().insert(ToDoProvider.CONTENT_URI, myCV);

                // instead of position-1, because this is making a new note but position isn't gotten from intent here, so expecting next one basically
                idInDb = getIdAtPosFromDb(position);
            }

            scheduleNotification(etNoteTitle.getText().toString(), etNoteContent.getText().toString(), idInDb, false);

            // Finishes the activity, taking us back to the list activity
            finish();
        });

        final DatePickerDialog.OnDateSetListener setDate = (view, year, month, day) -> {
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, month);
            calendar.set(Calendar.DAY_OF_MONTH, day);

            // Shows the newest due date when clicks ok
            displayDate(calendar.getTimeInMillis());
        };

        etDatePicker.setOnClickListener( v -> {
            // Onclick method causes the datepicker to pop up
            new DatePickerDialog(this, setDate,
                // Get all of these values from the intent somehow
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH))
                .show();
        });
    }

    private void getIntentFromListActivity(){

        Intent intent = getIntent();

        // Setting all of the UI elements when opening up a NoteActivity to update
        if(intent.hasExtra("todo_title") && intent.hasExtra("todo_description") && intent.hasExtra("todo_position")
                                            && intent.hasExtra("isUpdate") && intent.hasExtra("todo_done")){

            etNoteTitle.setText(intent.getStringExtra("todo_title"));
            etNoteContent.setText(intent.getStringExtra("todo_description"));
            displayDate(intent.getLongExtra("todo_date", calendar.getTimeInMillis()));
            cbDone.setChecked(intent.getBooleanExtra("todo_done", false));
            this.isUpdate = intent.getBooleanExtra("isUpdate", false);
            this.position = intent.getIntExtra("todo_position", -1);
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
        Uri queryUri = Uri.parse(ToDoProvider.CONTENT_URI.toString());

        // Perform the query, with ID Descending
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

    private void scheduleNotification(String todoTitle, String todoContent, int idInDb, boolean cancel){
        // Messing around with notifications stuff here
        AlarmManager alarmManager = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);


        // Creating the intent for opening up the activity when viewing notification
        Intent intent = new Intent(getApplicationContext(), NoteActivity.class);
        intent.putExtra("todo_title", etNoteTitle.getText().toString());
        intent.putExtra("todo_description", etNoteContent.getText().toString());
        intent.putExtra("todo_date", calendar.getTimeInMillis());
        intent.putExtra("todo_done", cbDone.isChecked());
        intent.putExtra("todo_position", position);  // position+1 is id in the DB
        intent.putExtra("isUpdate", this.isUpdate);

        // Used to create an artificial back-stack when opening the notification
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addNextIntentWithParentStack(intent);

        PendingIntent intentForOpeningActivity = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        // Builds the notification
        Notification notification = new NotificationCompat.Builder(getApplicationContext(), App.TODO_LIST_NOTIFICATION_CHANNEL_ID)
                .setSmallIcon(R.drawable.notification_icon)
                .setContentTitle(todoTitle)
                .setContentText(todoContent)
                .setChannelId(App.TODO_LIST_NOTIFICATION_CHANNEL_ID)
                .setContentIntent(intentForOpeningActivity)
                .build();

        // Creates pending intent for the Notification Handler class to receive
        Intent notificationIntent = new Intent(getApplicationContext(), NotificationHandler.class);
        notificationIntent.putExtra(NotificationHandler.NOTIFICATION_ID, idInDb);
        notificationIntent.putExtra(NotificationHandler.NOTIFICATION, notification);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), idInDb, notificationIntent, PendingIntent.FLAG_CANCEL_CURRENT);

        // Set an alarm or delete the alarm depending on the reason that this method is called
        Log.d(TAG, "scheduleNotification: Scheduling alarm for " + calendar.getTime().toString());
        if(!cancel) {
            alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
            Log.d(TAG, "scheduleNotification: scheduled notifiation with id: " + idInDb);
        } else {
            alarmManager.cancel(pendingIntent);
            Log.d(TAG, "initializeComponents: cancelled notification with id: " + idInDb);
        }
    }

    private void saveDueTimeInCalendar(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            calendar.set(Calendar.HOUR_OF_DAY, this.tpTimePicker.getHour());
            calendar.set(Calendar.MINUTE, this.tpTimePicker.getMinute());
        }
    }

    private void displayDate(long dateInMillis){

        calendar.setTimeInMillis(dateInMillis);
        int monthOneIndexed = calendar.get(Calendar.MONTH) + 1;
        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
        int year = calendar.get(Calendar.YEAR);

        String date = "" + monthOneIndexed + "/" + dayOfMonth + "/" + year;

        etDatePicker.setText(date);
    }

}
