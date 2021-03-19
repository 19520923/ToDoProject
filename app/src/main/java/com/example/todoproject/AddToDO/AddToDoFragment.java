package com.example.todoproject.AddToDO;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.todoproject.R;


import androidx.annotation.Nullable;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;

import com.example.todoproject.AppDefault.AppDefaultFragment;

import java.util.Date;

public class AddToDoFragment extends AppDefaultFragment implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {
    private static final String TAG = "AddToDoFragment";
    private Date mLastEdited;

    private EditText mToDoTextBodyEditText;
    private EditText mToDoTextBodyDescription;

    private SwitchCompat mToDoDateSwitch;
    private LinearLayout mUserDataSpinnerContainingLinearLayout;

    private String theme;
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);
        ImageButton reminderIconImageButton;
        TextView reminderRemindMeTextView;


    }

    @Override
    protected int layoutRes(){return R.layout.fragment_add_to_do;}
    public static AddToDoFragment newInstance() { return new AddToDoFragment();
    }
}
