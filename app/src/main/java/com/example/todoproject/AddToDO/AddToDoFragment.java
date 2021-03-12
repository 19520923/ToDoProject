package com.example.todoproject.AddToDO;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.widget.EditText;

import com.example.todoproject.R;


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


    @Override
    protected int layoutRes(){return R.layout.fragment_add_to_do;}
    public static AddToDoFragment newInstance() { return new AddToDoFragment();
    }
}
