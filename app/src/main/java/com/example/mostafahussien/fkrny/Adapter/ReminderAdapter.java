package com.example.mostafahussien.fkrny.Adapter;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.support.transition.Fade;
import android.support.transition.Transition;
import android.support.transition.TransitionSet;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.mostafahussien.fkrny.Activities.ViewTask;
import com.example.mostafahussien.fkrny.MainActivity;
import com.example.mostafahussien.fkrny.Model.Reminder;
import com.example.mostafahussien.fkrny.R;
import com.example.mostafahussien.fkrny.utils.DateTimeUtil;

import java.util.Calendar;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ReminderAdapter extends RecyclerView.Adapter<ReminderAdapter.ViewHolder> {
    public interface OnLongViewClickListener {
        public void onLongClikView();
    }
    public interface OnSelectedItems {
        public void onSelect(View view , int pos);
    }
    private Context context;
    private List<Reminder> reminderList;
    private OnLongViewClickListener onLongViewClickListener;
    private OnSelectedItems onSelectedItems;
    MainActivity mainActivity;
    public ReminderAdapter(Context context, List<Reminder> reminderList,OnLongViewClickListener listener ,OnSelectedItems onSelected ) {
        this.context = context;
        this.reminderList = reminderList;
        this.mainActivity=(MainActivity)context;
        onLongViewClickListener=listener;
        onSelectedItems=onSelected;
    }

    @Override
    public ReminderAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_notification_list,parent,false),mainActivity);
    }
    @Override
    public void onBindViewHolder(ReminderAdapter.ViewHolder holder, final int position) {
        Reminder reminder=new Reminder();
        int IconID=context.getResources().getIdentifier(reminderList.get(position).getIcon(), "drawable", context.getPackageName());
        reminder=reminderList.get(position);
        Calendar calendar= DateTimeUtil.parseDateAndTime(reminder.getDateAndTime());
        // show header for item if it is the first in data group like (Today OR 13 sep ..)
        if(position>0&&reminder.getDate().equals(reminderList.get(position-1).getDate())){ // el task de zy ely 2ablha fe date f msh hn3ml date seperate
            holder.textSeparator.setVisibility(View.GONE);
        } else {
            String taskDate=DateTimeUtil.getAppropriateDateFormat(context,calendar);
            holder.textSeparator.setVisibility(View.VISIBLE);
            holder.textSeparator.setText(taskDate);
        }
        holder.content.setText(reminder.getContent());
        holder.title.setText(reminder.getTitle());
        holder.time.setText(DateTimeUtil.toStringReadableTime(calendar,context));
        holder.icon.setImageResource(IconID);
        Log.d("gg11", String.valueOf(mainActivity.deleteMultipleItemsMode));
        if(!mainActivity.deleteMultipleItemsMode)           //      make checkBox appear whene long select view
        {
            holder.checkBox.setVisibility(View.GONE);
        } else {
            holder.checkBox.setVisibility(View.VISIBLE);
            holder.checkBox.setChecked(false);
        }
        GradientDrawable gradientDrawable=(GradientDrawable)holder.circle.getDrawable();
        gradientDrawable.setColor(Color.parseColor(reminder.getColour()));
        holder.view.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                onLongViewClickListener.onLongClikView();
                return true;
            }
        });
        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(context, ViewTask.class);
                intent.putExtra("NOTIFICATION_ID",reminderList.get(position).getId());
                if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.LOLLIPOP){
                    CardView cardView=(CardView) view.findViewById(R.id.notification_card);
                    TransitionSet set=new TransitionSet();
                    Transition transition=new Fade();
                    transition.excludeTarget(android.R.id.statusBarBackground, true);
                    transition.excludeTarget(android.R.id.navigationBarBackground, true);
                    transition.excludeTarget(R.id.fab_button, true);
                    transition.excludeTarget(R.id.recycler_view, true);
                    transition.setDuration(360);
                    set.addTransition(transition);
                    ((Activity) context).getWindow().setSharedElementsUseOverlay(false);
                    ((Activity) context).getWindow().setReenterTransition(null);
                    ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(((Activity) context), cardView, "cardTransition");
                    ActivityCompat.startActivity(((Activity) context), intent, options.toBundle());
                } else {
                    context.startActivity(intent);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return reminderList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        @BindView(R.id.notification_circle) ImageView circle;
        @BindView(R.id.header_separator) TextView textSeparator;
        @BindView(R.id.notification_title) TextView title;
        @BindView(R.id.notification_content) TextView content;
        @BindView(R.id.notification_icon) ImageView icon;
        @BindView(R.id.notification_time) TextView time;
        @BindView(R.id.check_list_items)CheckBox checkBox;
        @BindView(R.id.notification_card)CardView cardView;
        MainActivity mainActivity;
        private View view;

        public ViewHolder(View itemView, MainActivity mainActivity) {
            super(itemView);
            this.view=itemView;
            this.mainActivity=mainActivity;
            ButterKnife.bind(this,view);
            checkBox.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            onSelectedItems.onSelect(v,getAdapterPosition());
        }
    }
    public void updateAdapter(List<Reminder>selectedItems){
        for(Reminder rem : selectedItems)
        {
            reminderList.remove(rem);
        }
        notifyDataSetChanged();
    }

}
