package com.example.mostafahussien.fkrny.Activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;

import com.example.mostafahussien.fkrny.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AboutApp extends AppCompatActivity {
    @BindView(R.id.aboutAppView) LinearLayout linearLayout;
    Animation animation;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_app);
        ButterKnife.bind(this);
        //animation= AnimationUtils.loadAnimation(this,R.anim.about_activity_fade);         apply animation after startActivity in mainActivity --> best performance
       // linearLayout.startAnimation(animation);
    }

}
