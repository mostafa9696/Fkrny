package com.example.mostafahussien.fkrny.Activities;

import android.preference.PreferenceFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.mostafahussien.fkrny.Fragment.PrefFragment;
import com.example.mostafahussien.fkrny.R;

public class PrefActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pref);
        PrefFragment pref=new PrefFragment() ;
        getFragmentManager().beginTransaction().replace(R.id.content_frame,pref).commit();
    }
}
