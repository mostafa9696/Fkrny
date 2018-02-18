package com.example.mostafahussien.fkrny.Activities;

import android.app.AlarmManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Build;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.transition.Explode;
import android.transition.Slide;
import android.transition.Transition;
import android.transition.TransitionSet;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.example.mostafahussien.fkrny.MainActivity;
import com.example.mostafahussien.fkrny.Model.Reminder;
import com.example.mostafahussien.fkrny.R;
import com.example.mostafahussien.fkrny.Receiver.AlarmReceiver;
import com.example.mostafahussien.fkrny.Receiver.SnoozeReceiver;
import com.example.mostafahussien.fkrny.TaskDatabase;
import com.example.mostafahussien.fkrny.utils.AlarmUtil;
import com.example.mostafahussien.fkrny.utils.DateTimeUtil;

import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ViewTask extends AppCompatActivity {
    @BindView(R.id.notification_title)
    TextView notificationTitleText;
    @BindView(R.id.notification_time) TextView notificationTimeText;
    @BindView(R.id.notification_content) TextView contentText;
    @BindView(R.id.notification_icon)
    ImageView iconImage;
    @BindView(R.id.notification_circle) ImageView circleImage;
    @BindView(R.id.time) TextView timeText;
    @BindView(R.id.date) TextView dateText;
    @BindView(R.id.repeat) TextView repeatText;
    @BindView(R.id.shown) TextView shownText;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.detail_layout)
    LinearLayout linearLayout;
    @BindView(R.id.toolbar_shadow)
    View shadowView;
    @BindView(R.id.scroll)
    ScrollView scrollView;
    @BindView(R.id.header) View headerView;
    @BindView(R.id.view_coordinator)
    CoordinatorLayout coordinatorLayout;
    @BindView(R.id.check_list_items)
    CheckBox checkBox;
    private Reminder reminder;
    private boolean hideMarkAsDone;
    private boolean reminderChanged;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_task);
        ButterKnife.bind(this);
        makeAnimitaion();
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        if (getActionBar() != null) getActionBar().setDisplayHomeAsUpEnabled(true);
        if (getSupportActionBar() != null) getSupportActionBar().setTitle("Fkrny");
        // Add drawable shadow and adjust layout if build version is before lollipop
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            linearLayout.setPadding(0, 0, 0, 0);
            shadowView.setVisibility(View.VISIBLE);
        } else {
            ViewCompat.setElevation(headerView, getResources().getDimension(R.dimen.toolbar_elevation));
        }
        TaskDatabase database = TaskDatabase.getInstance(this);
        Intent intent = getIntent();
        int mReminderId = intent.getIntExtra("NOTIFICATION_ID", 0);
        // Check if notification has been deleted
        if (database.isTaskPresent(mReminderId)) {
            reminder = database.getNotification(mReminderId);
            database.close();
        } else {
            database.close();
            BackToMain();
        }
    }
    private BroadcastReceiver messageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {             // when this activity is opened and the notification is appear (received an event) so the activity must be update its views like shown .. out of ..
            reminderChanged =  true;        // from active to inActive
            updateReminder();
        }
    };
    @Override
    protected void onResume() {
        LocalBroadcastManager.getInstance(this).registerReceiver(messageReceiver, new IntentFilter("BROADCAST_REFRESH"));
        updateReminder();
        super.onResume();
    }
    @Override
    public void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(messageReceiver);
        super.onPause();
    }
    public void updateReminder() {
        TaskDatabase database = TaskDatabase.getInstance(this);
        reminder = database.getNotification(reminder.getId());
        database.close();
        fillViews();
    }
    public void fillViews(){
        checkBox.setVisibility(View.INVISIBLE);
        Calendar calendar = DateTimeUtil.parseDateAndTime(reminder.getDateAndTime());
        notificationTitleText.setText(reminder.getTitle());
        contentText.setText(reminder.getContent());
        dateText.setText(DateTimeUtil.toStringReadableDate(calendar));
        iconImage.setImageResource(getResources().getIdentifier(reminder.getIcon(), "drawable", getPackageName()));
        circleImage.setColorFilter(Color.parseColor(reminder.getColour()));
        String readableTime = DateTimeUtil.toStringReadableTime(calendar, this);
        timeText.setText(readableTime);
        notificationTimeText.setText(readableTime);
        final String[]repeatElement={"Not Repeat","Every Hour","Every Day","Every Month","Every Year"};
        repeatText.setText(repeatElement[reminder.getRepeatType()]);
        shownText.setText("Shown "+String.valueOf(reminder.getNumberShown())+" out of "+String.valueOf(reminder.getNumberToShow()));
        // Hide "Mark as done" action if reminder is inactive
        hideMarkAsDone = reminder.getNumberToShow() <= reminder.getNumberShown();
        invalidateOptionsMenu();
    }
    public void makeAnimitaion(){
        reminderChanged = true;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            // Enter transitions
            TransitionSet setEnter = new TransitionSet();

            Transition slideDown = new Explode();
            slideDown.addTarget(headerView);
            slideDown.excludeTarget(scrollView, true);
            slideDown.setDuration(500);
            setEnter.addTransition(slideDown);

            Transition fadeOut = new Slide(Gravity.BOTTOM);
            fadeOut.addTarget(scrollView);
            fadeOut.setDuration(500);
            setEnter.addTransition(fadeOut);

            TransitionSet setExit = new TransitionSet();

            Transition slideDown2 = new Explode();
            slideDown2.addTarget(headerView);
            slideDown2.setDuration(570);
            setExit.addTransition(slideDown2);

            Transition fadeOut2 = new Slide(Gravity.BOTTOM);
            fadeOut2.addTarget(scrollView);
            fadeOut2.setDuration(280);
            setExit.addTransition(fadeOut2);

            getWindow().setEnterTransition(setEnter);
            getWindow().setReturnTransition(setExit);
        }
    }
    public void BackToMain() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_viewer, menu);
        if (hideMarkAsDone) {
            menu.findItem(R.id.action_mark_as_done).setVisible(false);
        }
        return true;
    }
    public void deleteTask(){
        new AlertDialog.Builder(this, R.style.Dialog)
                .setMessage("Do you want to delete this task ?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteTaskConfirmation();
                    }
                })
                .setNegativeButton("No", null).show();
    }
    public void deleteTaskConfirmation(){
        TaskDatabase database = TaskDatabase.getInstance(this);
        database.deleteTask(reminder);
        database.close();
        Intent alarmIntent = new Intent(this, AlarmReceiver.class);
        AlarmUtil.cancelAlarm(this, alarmIntent, reminder.getId());
        Intent snoozeIntent = new Intent(this, SnoozeReceiver.class);
        AlarmUtil.cancelAlarm(this, snoozeIntent, reminder.getId());
        finish();
    }
    public void editTask() {
        Intent intent = new Intent(this, AddEditActivity.class);
        intent.putExtra("NOTIFICATION_ID", reminder.getId());
        startActivity(intent);
        finish();
    }
    public void markAsDoneTask(){
        Intent Aintent=new Intent(getApplicationContext(),AlarmReceiver.class);
        AlarmUtil.cancelAlarm(this,Aintent,reminder.getId());
        reminder.setDateAndTime(DateTimeUtil.toStringDateAndTime(Calendar.getInstance()));
        reminder.setNumberShown(reminder.getNumberShown()+1);
        TaskDatabase database = TaskDatabase.getInstance(this);
        database.addNewTask(reminder);
        fillViews();
        database.close();
        Snackbar.make(coordinatorLayout, "This task is marked as done !", Snackbar.LENGTH_SHORT).show();
    }
    public void shareTask(){
        Intent intent = new Intent(); intent.setAction(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, reminder.getTitle() + "\n" + reminder.getContent());
        startActivity(Intent.createChooser(intent,"Share"));
    }
    @Override
    public void onBackPressed() {
        if (reminderChanged) {
            finish();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.action_delete:
                deleteTask();
                return true;
            case R.id.action_edit:
                editTask();
                return true;
            case R.id.action_share:
                shareTask();
                return true;
            case R.id.action_mark_as_done:
                markAsDoneTask();             // in active notification type to make it done
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
