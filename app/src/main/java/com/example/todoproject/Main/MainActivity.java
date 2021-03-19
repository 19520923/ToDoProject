package com.example.todoproject.Main;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.fragment.app.Fragment;

import com.example.todoproject.R;
import com.example.todoproject.AppDefault.AppDefaultActivity;

public class MainActivity extends AppDefaultActivity {
    protected  void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(false);
        }
    }

    @Override
    protected int contentViewLayoutRes(){return  R.layout.activity_main;}

    @NonNull
    @Override
    protected Fragment createInitialFragment(){return MainFragment.newInstance();}

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case R.id.aboutMeMenuItem:
                Intent i = new Intent(this,AboutActivity.class);
                startActivity(i);
                return true;
            case R.id.preference:
                Intent intent = new Intent(this,SettingsActivity.class);
                startActivity(intent);
                return true;

                default:
                    return super.onOptionsItemSelected(item);
        }
    }
}
