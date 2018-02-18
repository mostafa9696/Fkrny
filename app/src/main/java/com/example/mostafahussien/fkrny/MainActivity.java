package com.example.mostafahussien.fkrny;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.CheckBox;
import android.widget.TextView;

import com.astuetz.PagerSlidingTabStrip;
import com.example.mostafahussien.fkrny.Activities.AboutApp;
import com.example.mostafahussien.fkrny.Activities.AddEditActivity;
import com.example.mostafahussien.fkrny.Activities.PrefActivity;
import com.example.mostafahussien.fkrny.Adapter.ViewPagerAdapter;
import com.example.mostafahussien.fkrny.Fragment.TabsFragment;
import com.example.mostafahussien.fkrny.ListenersInterfac.ReminderListener;
import com.example.mostafahussien.fkrny.ListenersInterfac.SelectedListener;
import com.example.mostafahussien.fkrny.Model.Message;

import org.greenrobot.eventbus.EventBus;


import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements ReminderListener,SelectedListener, View.OnClickListener {
    @BindView(R.id.tabs)PagerSlidingTabStrip pagerSlidingTabStrip;
    @BindView(R.id.viewpager)ViewPager viewPager;
    @BindView(R.id.fab_button)FloatingActionButton floatingActionButton;
    @BindView(R.id.addFab)FloatingActionButton addFab;
    @BindView(R.id.deleteFab)FloatingActionButton deleteFab;
    @BindView(R.id.toolbar) Toolbar toolbar;
    ViewPagerAdapter adapter;
    public boolean deleteMultipleItemsMode=false , isOpen=false;
    @BindView(R.id.counterText) TextView counterTextView;
    int selectedCounter=0;
    TabsFragment tabsFragment;
    Animation fab_open,fab_close,rotate_forward,rotate_backward;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        counterTextView.setText(R.string.app_name);
        adapter=new ViewPagerAdapter(getSupportFragmentManager(),this);
        viewPager.setAdapter(adapter);
        pagerSlidingTabStrip.setViewPager(viewPager);
        tabsFragment=new TabsFragment();
        loadAnimation();
        floatingActionButton.setOnClickListener(this);
        addFab.setOnClickListener(this);
        deleteFab.setOnClickListener(this);
    }
    public void loadAnimation(){
        fab_open= AnimationUtils.loadAnimation(getApplicationContext(),R.anim.fab_open);
        fab_close = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.fab_close);
        rotate_forward = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.rotate_forward);
        rotate_backward = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.rotate_back);
    }
    @Override
    public void onClick(View v) {
        int id=v.getId();
        if(id==R.id.fab_button){
            startAnimation();
        } else if(id==R.id.addFab){
            Intent intent=new Intent(this,AddEditActivity.class);
            startActivity(intent);
        } else if(id==R.id.deleteFab) {
            EventBus.getDefault().postSticky(new Message("deleteFab"));
            deleteMultipleItemsMode=true;
            updateToolbar();
            closeFab();
        }
    }
    public void startAnimation(){
        if(isOpen){
            closeFab();
            deleteMultipleItemsMode=false;
        } else {
            floatingActionButton.startAnimation(rotate_forward);
            addFab.startAnimation(fab_open);
            deleteFab.startAnimation(fab_open);
            addFab.setClickable(true);
            deleteFab.setClickable(true);
            isOpen = true;
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                Intent preferenceIntent = new Intent(this, PrefActivity.class);
                startActivity(preferenceIntent);
                return true;
            case R.id.action_about:
                Intent aboutIntent = new Intent(this, AboutApp.class);
                startActivity(aboutIntent);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                return true;
            case R.id.multiple_item_delete:
                EventBus.getDefault().postSticky(new Message("delete"));
                clearDeleteMultipleMode();
                return true;
            case android.R.id.home:
                EventBus.getDefault().postSticky(new Message("back"));
                clearDeleteMultipleMode();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onBackPressed() {
        if(deleteMultipleItemsMode) {
            clearDeleteMultipleMode();
            EventBus.getDefault().postSticky(new Message("back"));
        } else {
            super.onBackPressed();
        }
    }
    public void clearDeleteMultipleMode(){
        deleteMultipleItemsMode=false;
        toolbar.getMenu().clear();
        toolbar.inflateMenu(R.menu.menu_main);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        selectedCounter=0;
        counterTextView.setText(R.string.app_name);
    }
    @Override
    public void updateToolbar() {
        selectedCounter=0;
        deleteMultipleItemsMode=true;
        toolbar.getMenu().clear();
        toolbar.inflateMenu(R.menu.delete_multiple_items_actionbar);
        counterTextView.setText("0 item selected");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public void onSelectListner(View v, int pos) {
        if(((CheckBox)v).isChecked())
        {
            selectedCounter++;
        } else {
            selectedCounter--;
        }
        counterTextView.setText(String.valueOf(selectedCounter)+" item selected");

    }

    @Override
    protected void onResume() {
        super.onResume();
        deleteMultipleItemsMode=false;
        clearDeleteMultipleMode();
        if(isOpen)
         closeFab();
    }
    public void closeFab(){
        floatingActionButton.startAnimation(rotate_backward);
        addFab.startAnimation(fab_close);
        deleteFab.startAnimation(fab_close);
        addFab.setClickable(false);
        deleteFab.setClickable(false);
        isOpen=false;
    }
}
