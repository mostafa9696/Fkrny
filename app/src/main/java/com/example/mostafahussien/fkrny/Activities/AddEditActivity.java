package com.example.mostafahussien.fkrny.Activities;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.afollestad.materialdialogs.color.ColorChooserDialog;
import com.example.mostafahussien.fkrny.Dialog.IconPicker;
import com.example.mostafahussien.fkrny.Dialog.TaskRepeat;
import com.example.mostafahussien.fkrny.Model.Reminder;
import com.example.mostafahussien.fkrny.Model.TaskColour;
import com.example.mostafahussien.fkrny.R;
import com.example.mostafahussien.fkrny.Receiver.AlarmReceiver;
import com.example.mostafahussien.fkrny.TaskDatabase;
import com.example.mostafahussien.fkrny.utils.AlarmUtil;
import com.example.mostafahussien.fkrny.utils.DateTimeUtil;

import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AddEditActivity extends AppCompatActivity implements ColorChooserDialog.ColorCallback,
        IconPicker.IconSelectionListener,TaskRepeat.RepeatListener{
    @BindView(R.id.notification_content) EditText contentEditText;
    @BindView(R.id.time)
    TextView timeText;
    @BindView(R.id.create_coordinator)
    CoordinatorLayout coordinatorLayout;
    @BindView(R.id.notification_title)
    EditText titleEditText;
    @BindView(R.id.date) TextView dateText;
    @BindView(R.id.repeat_day) TextView repeatText;
    @BindView(R.id.show_times_number) EditText timesEditText;
    @BindView(R.id.bottom_row) LinearLayout bottomRow;
    @BindView(R.id.show) TextView showText;
    @BindView(R.id.select_icon_text) TextView iconText;
    @BindView(R.id.select_colour_text) TextView colourText;
    @BindView(R.id.colour_icon)
    ImageView imageColourSelect;
    @BindView(R.id.selected_icon) ImageView imageIconSelect;
    @BindView(R.id.error_time) ImageView imageWarningTime;
    @BindView(R.id.error_date) ImageView imageWarningDate;
    @BindView(R.id.error_show) ImageView imageWarningShow;
    @BindView(R.id.toolbar)
    Toolbar toolbar;

    private String icon;
    private String colour;
    private Calendar calendar;
    private boolean[] daysOfWeek = new boolean[7];
    private int timesShown = 0;         // times of task ring
    private int timesToShow = 1;        // times to show task
    private int id;
    private int interval = 1;           // after 1 hour || 1 day || 1 week || 1 year
    private int repeatType;             // index of array (notReapeat,hour,day,week,year)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_close_white_24dp);
        if(getActionBar()!=null){
            getActionBar().setDisplayHomeAsUpEnabled(true);
        }
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(null);
        }
        repeatType=Reminder.DOES_NOT_REPEAT;
        calendar=Calendar.getInstance();
        icon="ic_notifications_white_24dp";
        colour="#8e8e8e";
        id=getIntent().getIntExtra("NOTIFICATION_ID",0);
        bottomRow.setVisibility(View.GONE);
        if (id == 0) {      // in add case not sent extra so default value is 0
            TaskDatabase database = TaskDatabase.getInstance(this);
            id = database.getLastTaskID() + 1;                      // increment 1 at last task id
            database.close();
        } else {                    // in edit case
            Log.d("e44", "o1");
            assignReminderValues();
        }
    }
    public void assignReminderValues(){
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        TaskDatabase database=TaskDatabase.getInstance(this);
        Reminder reminder=database.getNotification(id);
        database.close();
        timesShown=reminder.getNumberShown();
        repeatType = reminder.getRepeatType();
        icon = reminder.getIcon();
        colour = reminder.getColour();
        calendar = DateTimeUtil.parseDateAndTime(reminder.getDateAndTime());
        titleEditText.setText(reminder.getTitle());
        contentEditText.setText(reminder.getContent());
        dateText.setText(DateTimeUtil.toStringReadableDate(calendar));
        timeText.setText(DateTimeUtil.toStringReadableTime(calendar, this));
        timesEditText.setText(String.valueOf(reminder.getNumberToShow()));
        colourText.setText(colour);
        imageColourSelect.setColorFilter(Color.parseColor(colour));
        if(!icon.equals("Default icon")){
            imageIconSelect.setImageResource(getResources().getIdentifier(icon,"drawable",getPackageName()));
            iconText.setText("Your selected icon");
        }
        if (reminder.getRepeatType() != Reminder.DOES_NOT_REPEAT) {
            int repeatIndex=reminder.getRepeatType();
            final String[]repeatElement={"Not Repeat","Every Hour","Every Day","Every Month","Every Year"};
            repeatText.setText(repeatElement[repeatIndex]);
        }
    }
    @OnClick(R.id.time_row)
    public void selectTime(){
        TimePickerDialog timePickerDialog=new TimePickerDialog(AddEditActivity.this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                calendar.set(Calendar.MINUTE, minute);
                timeText.setText(DateTimeUtil.toStringReadableTime(calendar, getApplicationContext()));
            }
        },calendar.get(Calendar.HOUR_OF_DAY),calendar.get(Calendar.MINUTE), DateFormat.is24HourFormat(this));
        timePickerDialog.show();
    }
    @OnClick(R.id.date_row)
    public void selectDate(View view){
        DatePickerDialog datePickerDialog=new DatePickerDialog(AddEditActivity.this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, month);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                dateText.setText(DateTimeUtil.toStringReadableDate(calendar));
            }
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }
    @OnClick(R.id.icon_select)
    public void iconSelector() {
        DialogFragment dialog = new IconPicker();
        dialog.show(getSupportFragmentManager(), "IconPicker");
    }

    @Override
    public void onColorSelection(@NonNull ColorChooserDialog dialog, @ColorInt int selectedColor) { // to add selected colour to main dialog (max colours is 15) and save it to database
        colour = String.format("#%06X", (0xFFFFFF & selectedColor));
        imageColourSelect.setColorFilter(selectedColor);
        colourText.setText(colour);
        TaskDatabase database = TaskDatabase.getInstance(this);
        database.addColour(new TaskColour(selectedColor, DateTimeUtil.toStringDateTimeWithSeconds(Calendar.getInstance())));
        database.close();
    }

    @Override
    public void onIconSelection(DialogFragment dialog, String iconName, String iconType, int iconResId) {
        icon=iconName;
        iconText.setText(iconType);
        imageIconSelect.setImageResource(iconResId);
        dialog.dismiss();
    }
    @OnClick(R.id.colour_select)
    public void colourSelector() {
        TaskDatabase database = TaskDatabase.getInstance(this);
        int[] colours = database.getColoursArray();
        database.close();
        new ColorChooserDialog.Builder(this, R.string.select_colour)
                .allowUserColorInputAlpha(false)
                .customColors(colours, null)
                .preselect(Color.parseColor(colour))
                .show();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_create, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.action_save:
                validateInput();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
    public void validateInput(){
        imageWarningDate.setVisibility(View.GONE);
        imageWarningShow.setVisibility(View.GONE);
        imageWarningTime.setVisibility(View.GONE);
        Calendar nowCalendar=Calendar.getInstance();
        if(timeText.getText().equals("Set time")){
            calendar.set(Calendar.HOUR_OF_DAY, nowCalendar.get(Calendar.HOUR_OF_DAY));
            calendar.set(Calendar.MINUTE, nowCalendar.get(Calendar.MINUTE));
        }
        if (dateText.getText().equals("Set Date")) {
            calendar.set(Calendar.YEAR, nowCalendar.get(Calendar.YEAR));
            calendar.set(Calendar.MONTH, nowCalendar.get(Calendar.MONTH));
            calendar.set(Calendar.DAY_OF_MONTH, nowCalendar.get(Calendar.DAY_OF_MONTH));
        } // else set before in selectDate function

        // Check if the number of times to show notification is empty
        if (timesEditText.getText().toString().isEmpty()) {
            timesEditText.setText("1");
        }
        timesToShow = Integer.parseInt(timesEditText.getText().toString());     // get number of repeat times in case of repeat times
        if (repeatType == Reminder.DOES_NOT_REPEAT) {
            timesToShow = timesShown + 1;
        }
        // Check if selected date is before today's date
        if (DateTimeUtil.toLongDateAndTime(calendar) < DateTimeUtil.toLongDateAndTime(nowCalendar)) {
            Snackbar.make(coordinatorLayout, "You set an unvalid date", Snackbar.LENGTH_SHORT).show();
            imageWarningTime.setVisibility(View.VISIBLE);
            imageWarningDate.setVisibility(View.VISIBLE);
            // Check if title is empty
        } else if (titleEditText.getText().toString().trim().isEmpty()) {
            Snackbar.make(coordinatorLayout, "Title can't be empty", Snackbar.LENGTH_SHORT).show();
            Animation shake = AnimationUtils.loadAnimation(this, R.anim.shake_view);
            titleEditText.startAnimation(shake);
        } else {
            Log.d("yy7","t1");
            saveNotification();
        }
    }
    @OnClick(R.id.repeat_row)
    public void repeatSelector() {
        DialogFragment dialog = new TaskRepeat();
        dialog.show(getSupportFragmentManager(), "RepeatSelector");
    }
    @Override
    public void onReapeatClick(DialogFragment dialogFragment, int index, String text) {
        interval = 1;
        repeatType = index;
        repeatText.setText(text);
        if(index>0)
            bottomRow.setVisibility(View.VISIBLE);

    }
    public void saveNotification(){
        TaskDatabase database=TaskDatabase.getInstance(this);
        Reminder savedReminder=new Reminder();
        savedReminder.setId(id);
        savedReminder.setTitle(titleEditText.getText().toString());
        savedReminder.setContent(contentEditText.getText().toString());
        savedReminder.setDateAndTime(DateTimeUtil.toStringDateAndTime(calendar));
        savedReminder.setNumberToShow(timesToShow);
        savedReminder.setNumberShown(timesShown);
        savedReminder.setRepeatType(repeatType);
        savedReminder.setIcon(icon);
        savedReminder.setColour(colour);
        savedReminder.setInterval(interval);
        database.addNewTask(savedReminder);
        database.close();
        Toast.makeText(this,"Saved",Toast.LENGTH_SHORT).show();
        Intent alarmReciever = new Intent(this, AlarmReceiver.class);
        calendar.set(Calendar.SECOND, 0);
        AlarmUtil.setAlarm(this, alarmReciever, savedReminder.getId(), calendar);
        Log.d("yy7","t2");
        finish();
    }

}
