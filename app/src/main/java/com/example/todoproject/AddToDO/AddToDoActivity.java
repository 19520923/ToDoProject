package com.example.todoproject.AddToDO;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.todoproject.AppDefault.AppDefaultActivity;
import com.example.todoproject.R;


public class AddToDoActivity extends AppDefaultActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState){super.onCreate(savedInstanceState);}

    @Override
    protected int contentViewLayoutRes(){ return R.layout.activity_add_to_do;}

    @NonNull
    @Override
    protected Fragment createInitialFragment(){return AddToDoFragment.newInstance();}

    @Override
    protected void onResume() {super.onResume();}
}
