package com.example.mostafahussien.fkrny.Fragment;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;

import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.mostafahussien.fkrny.Activities.AddEditActivity;
import com.example.mostafahussien.fkrny.Adapter.ReminderAdapter;
import com.example.mostafahussien.fkrny.MainActivity;
import com.example.mostafahussien.fkrny.Model.Message;
import com.example.mostafahussien.fkrny.Model.Reminder;
import com.example.mostafahussien.fkrny.R;
import com.example.mostafahussien.fkrny.Receiver.AlarmReceiver;
import com.example.mostafahussien.fkrny.Receiver.SnoozeReceiver;
import com.example.mostafahussien.fkrny.ListenersInterfac.ReminderListener;
import com.example.mostafahussien.fkrny.ListenersInterfac.SelectedListener;
import com.example.mostafahussien.fkrny.TaskDatabase;
import com.example.mostafahussien.fkrny.utils.AlarmUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TabsFragment extends Fragment  {

    ReminderListener reminderListener;
    SelectedListener selectedListener;
    List<Reminder>selectedItems=new ArrayList<>();
    public void setReminderListener(ReminderListener listener)
    {
        reminderListener=listener;
    }
    public void setSelectedListener(SelectedListener listener)
    {
        selectedListener=listener;
    }
    @BindView(R.id.empty_view) LinearLayout linearLayout;
    @BindView(R.id.empty_icon) ImageView imageView;
    @BindView(R.id.recycler_view)RecyclerView recyclerView;
    @BindView(R.id.empty_text)TextView emptyText;
    private int reminderType;
    private ReminderAdapter reminderAdapter;
    private List<Reminder> reminderList;
    public boolean deleteMultipleItemsMode=false;
    MainActivity mainActivity;
    Paint p=new Paint();
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_tabs, container, false);
    }
    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }
    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this,view);
        LinearLayoutManager layoutManager=new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        reminderList=new ArrayList<>();
        reminderType=this.getArguments().getInt("Type");        // type is active or inctive to fetch tasks due to this type
        if(reminderType==Reminder.INACTIVE) {
            emptyText.setText("No InActive Notification");
            imageView.setImageResource(R.drawable.ic_notifications_off_black_empty);
        }
        mainActivity=(MainActivity)getContext();
        reminderList=getTasks();
        reminderAdapter=new ReminderAdapter(getContext(),reminderList, new ReminderAdapter.OnLongViewClickListener() {
            @Override
            public void onLongClikView() {
                deleteMultipleItemsMode=true;
                reminderAdapter.notifyDataSetChanged();
                reminderListener.updateToolbar();
            }
        }, new ReminderAdapter.OnSelectedItems() {
            @Override
            public void onSelect(View v , int pos) {
                if(((CheckBox)v).isChecked())
                {
                    selectedItems.add(reminderList.get(pos));
                } else {
                    selectedItems.remove(reminderList.get(pos));
                }
                selectedListener.onSelectListner(v,pos);
            }
        });
        recyclerView.setAdapter(reminderAdapter);
        if(reminderAdapter.getItemCount()==0){
            recyclerView.setVisibility(View.GONE);
            linearLayout.setVisibility(View.VISIBLE);
        }
        initSwipe();
    }
    public void initSwipe(){
        ItemTouchHelper.SimpleCallback simpleCallback=new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                int position=viewHolder.getAdapterPosition();
                Reminder selectedReminder=reminderList.get(position);

                if(direction==ItemTouchHelper.LEFT) {
                    List<Reminder>tmp=new ArrayList<>();
                    tmp.add(selectedReminder);
                    ReminderAdapter adapter2 = reminderAdapter;
                    adapter2.updateAdapter(tmp);
                    TaskDatabase database = TaskDatabase.getInstance(getContext().getApplicationContext());
                        database.deleteTask(selectedReminder);
                        if (selectedReminder.getNumberToShow() > selectedReminder.getNumberShown())            // active task
                        {
                            Intent alarmInent = new Intent(getContext(), AlarmReceiver.class);
                            AlarmUtil.cancelAlarm(getContext(), alarmInent, selectedReminder.getId());
                            Intent snoozeIntent = new Intent(getContext(), SnoozeReceiver.class);
                            AlarmUtil.cancelAlarm(getContext(), snoozeIntent, selectedReminder.getId());
                        }
                    database.close();
                } else {
                    Intent intent=new Intent(getContext(), AddEditActivity.class);
                    intent.putExtra("NOTIFICATION_ID", selectedReminder.getId());
                    startActivity(intent);
                }
            }

            @Override
            public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                Bitmap icon;
                if(actionState==ItemTouchHelper.ACTION_STATE_SWIPE) {
                    View itemView=viewHolder.itemView;
                    float height = (float) itemView.getBottom() - (float) itemView.getTop();
                    float width = height / 3;
                    if(dX > 0){
                        p.setColor(Color.parseColor("#388E3C"));
                        RectF background = new RectF((float) itemView.getLeft(), (float) itemView.getTop(), dX,(float) itemView.getBottom());
                        c.drawRect(background,p);
                        icon = BitmapFactory.decodeResource(getResources(), R.drawable.ic_create_white_24dp);
                        RectF icon_dest = new RectF((float) itemView.getLeft() + width ,(float) itemView.getTop() + width,(float) itemView.getLeft()+ 2*width,(float)itemView.getBottom() - width);
                        c.drawBitmap(icon,null,icon_dest,p);
                    } else {
                        p.setColor(Color.parseColor("#D32F2F"));
                        RectF background = new RectF((float) itemView.getRight() + dX, (float) itemView.getTop(),(float) itemView.getRight(), (float) itemView.getBottom());
                        c.drawRect(background,p);
                        icon = BitmapFactory.decodeResource(getResources(), R.drawable.ic_delete_white_24dp);
                        RectF icon_dest = new RectF((float) itemView.getRight() - 2*width ,(float) itemView.getTop() + width,(float) itemView.getRight() - width,(float)itemView.getBottom() - width);
                        c.drawBitmap(icon,null,icon_dest,p);
                    }
                }
                    super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }
        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);
    }
    public List<Reminder>getTasks() {
        TaskDatabase database = TaskDatabase.getInstance(getContext().getApplicationContext());
        List<Reminder> reminderList = database.getTasks(reminderType);
        database.close();
        return reminderList;
    }

    public void updateList(){  // to update task from active to inActive
        reminderList.clear();
        reminderList.addAll(getTasks());
        reminderAdapter.notifyDataSetChanged();
        if(reminderAdapter.getItemCount()==0){
            recyclerView.setVisibility(View.GONE);
            linearLayout.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            linearLayout.setVisibility(View.GONE);
        }
    }
    private BroadcastReceiver receiver=new BroadcastReceiver() {     // recyclerView must be update if a task is change its status(active or inActive)
        @Override
        public void onReceive(Context context, Intent intent) {

            updateList();
        }
    };
    @Override
    public void onResume() {        // to handle case if back to this activity and a task is change its status (active or inActive) so we should update the recyclerView
        deleteMultipleItemsMode=false;
        reminderAdapter.notifyDataSetChanged();
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(receiver, new IntentFilter("BROADCAST_REFRESH"));  // BROADCAST_REFRESH is a intent action which is declared in AlarmReceiver class
        updateList();
        super.onResume();
    }

    @Override
    public void onPause() {
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(receiver);
        super.onPause();
    }
    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onEvent(Message message){
        if(message.getMessage()=="deleteFab") {
            deleteMultipleItemsMode=true;
            reminderAdapter.notifyDataSetChanged();
        } else {
            if (message.getMessage() == "delete") {                                                  // if delete case
                ReminderAdapter adapter2 = reminderAdapter;
                adapter2.updateAdapter(selectedItems);
                TaskDatabase database = TaskDatabase.getInstance(getContext().getApplicationContext());
                for (Reminder rem : selectedItems) {
                    database.deleteTask(rem);
                    if (rem.getNumberToShow() > rem.getNumberShown())            // active task
                    {
                        Intent alarmInent = new Intent(getContext(), AlarmReceiver.class);
                        AlarmUtil.cancelAlarm(getContext(), alarmInent, rem.getId());
                        Intent snoozeIntent = new Intent(getContext(), SnoozeReceiver.class);
                        AlarmUtil.cancelAlarm(getContext(), snoozeIntent, rem.getId());
                    }
                }
                database.close();

            } else if (message.getMessage() == "back") {            // if back case without prees delete button
                reminderAdapter.notifyDataSetChanged();
            }
            selectedItems.clear();
            deleteMultipleItemsMode = false;
        }
    }



}
