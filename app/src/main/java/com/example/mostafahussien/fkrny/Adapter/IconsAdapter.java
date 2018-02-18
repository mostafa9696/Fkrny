package com.example.mostafahussien.fkrny.Adapter;

import android.graphics.drawable.Icon;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;


import com.example.mostafahussien.fkrny.Dialog.IconPicker;
import com.example.mostafahussien.fkrny.Model.TaskIcon;
import com.example.mostafahussien.fkrny.R;
import com.example.mostafahussien.fkrny.TaskDatabase;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class IconsAdapter extends RecyclerView.Adapter<IconsAdapter.ViewHolder>{

    private IconPicker iconPicker;
    private List<TaskIcon> iconList;

    public class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.icon) ImageView imageView;
        private View view;

        public ViewHolder(final View view) {
            super(view);
            this.view = view;
            ButterKnife.bind(this, view);
        }
    }

    public IconsAdapter(IconPicker iconPicker, List<TaskIcon> iconList) {
        this.iconPicker = iconPicker;
        this.iconList = iconList;
    }

    @Override
    public int getItemCount() {
        return iconList.size();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_icon_grid, viewGroup, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, int position) {
        final String iconName = iconList.get(position).getName();
        final int iconResId = viewHolder.view.getContext().getResources().getIdentifier(iconName, "drawable", viewHolder.view.getContext().getPackageName());
        viewHolder.imageView.setImageResource(iconResId);
        viewHolder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TaskDatabase database = TaskDatabase.getInstance(viewHolder.view.getContext());
                iconList.get(viewHolder.getAdapterPosition()).setUseFrequency(iconList.get(viewHolder.getAdapterPosition()).getUseFrequency() + 1);
                database.updateIcon(iconList.get(viewHolder.getAdapterPosition()));
                database.close();

                String name;
                if (!iconName.equals("ic_notifications_white_24dp")) {
                    name = "Custom icon";
                } else {
                    name = "Default icon";
                }

                ((IconPicker.IconSelectionListener) viewHolder.view.getContext()).onIconSelection(iconPicker, iconName, name, iconResId);
            }
        });
    }
}