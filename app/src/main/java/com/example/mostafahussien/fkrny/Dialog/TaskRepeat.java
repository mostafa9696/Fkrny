package com.example.mostafahussien.fkrny.Dialog;

import android.app.Dialog;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;

import com.example.mostafahussien.fkrny.R;

public class TaskRepeat extends DialogFragment {
    public interface RepeatListener{
        void onReapeatClick(DialogFragment dialogFragment,int index,String text);
    }
    RepeatListener listener;
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        listener = (RepeatListener) context;
    }

    @Override
    @NonNull
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final String[]repeatElement={"Not Repeat","Every Hour","Every Day","Every Month","Every Year"};
        AlertDialog.Builder builder=new AlertDialog.Builder(getContext(), R.style.Dialog);
        builder.setItems(repeatElement, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int index) {
                listener.onReapeatClick(TaskRepeat.this, index, repeatElement[index]);
            }
        });
        return builder.create();
    }
}
