package com.example.todoproject.Reminder;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;


import fr.ganfra.materialspinner.MaterialSpinner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.todoproject.AppDefault.AppDefaultFragment;
import com.example.todoproject.Main.MainActivity;
import com.example.todoproject.Main.MainFragment;
import com.example.todoproject.R;
import com.example.todoproject.Utility.StoreRetrieveData;
import com.example.todoproject.Utility.ToDoItem;
import com.example.todoproject.Utility.TodoNotidicationService;

import org.json.JSONException;

import static android.content.Context.MODE_PRIVATE;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

public class ReminderFragment extends AppDefaultFragment {
    private TextView mToDoTextTextView;
    private Button mRemoveToDoButton;
    private MaterialSpinner mSnoozeSpinner;
    private String[] snoozeOptionArray;
    private StoreRetrieveData storeRetrieveData;
    private ArrayList<ToDoItem> mToDoItems;
    private ToDoItem mItem;
    private static final String EXIT = "com.todoproject.exit";
    private TextView mSnoozeTextView;
    String theme;

    @Override
    public void onViewCreated(View view, @NonNull Bundle savedInstanceState){
        super.onViewCreated(view,savedInstanceState);

        theme = getActivity().getSharedPreferences(MainFragment.THEME_REFERENCES, MODE_PRIVATE).getString(MainFragment.THEMR_SAVED, MainFragment.LIGHTTHEME);
        if(theme.equals(MainFragment.LIGHTTHEME)){
            getActivity().setTheme(R.style.CustomStyle_LightTheme);
        } else {
            getActivity().setTheme(R.style.CustomStyle_DarkTheme);
        }
        storeRetrieveData = new StoreRetrieveData(getContext(),MainFragment.FILENAME);
        mToDoItems = MainFragment.getLocallyStoredData(storeRetrieveData);

        ((AppCompatActivity) getActivity()).setSupportActionBar((Toolbar) view.findViewById(R.id.toolbar));

        Intent i = getActivity().getIntent();
        UUID id = (UUID) i.getSerializableExtra(TodoNotidicationService.TODOUUID);
        mItem = null;
        for(ToDoItem toDoItem:mToDoItems){
            if(toDoItem.getIdentifier().equals(id)){
                mItem = toDoItem;
                break;
            }
        }

        snoozeOptionArray = getResources().getStringArray(R.array.snooze_options);

        mRemoveToDoButton = (Button) view.findViewById(R.id.toDoReminderRemoveButton);
        mToDoTextTextView = (TextView) view.findViewById(R.id.toDoReminderTextViewBody);
        mSnoozeTextView = (TextView) view.findViewById(R.id.reminderViewSnoozeTextView);
        mSnoozeSpinner = (MaterialSpinner) view.findViewById(R.id.toDoReminderSnoozeSpinner);

        mToDoTextTextView.setText(mItem.getToDoText());

        if(theme.equals(MainFragment.LIGHTTHEME)){
            mSnoozeTextView.setTextColor(getResources().getColor(R.color.secondary_text));
        } else {
            mSnoozeTextView.setTextColor(Color.WHITE);
            mSnoozeTextView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_snooze_white_24dp,0,0,0);
        }

        mRemoveToDoButton.setOnClickListener((v) -> {
            mToDoItems.remove(mItem);
            changedOccurred();
            saveData();
            closeApp();
        });

        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), R.layout.spinner_text_view,snoozeOptionArray);
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        mSnoozeSpinner.setAdapter(adapter);
     }

     @Override
    protected int layoutRes(){return R.layout.fragment_reminder}
    private void closeApp() {
        Intent i = new Intent(getContext(), MainActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(MainFragment.SHARED_PREF_DATA_SET_CHANGE,MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(EXIT,true);
        editor.apply();
        startActivity(i);
    }

    public boolean onCreateOptionsMenu(Menu menu)
    {
        getActivity().getMenuInflater().inflate(R.menu_reminder,menu);
        return true;
    }

    private void changedOccurred()
    {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(MainFragment.SHARED_PREF_DATA_SET_CHANGE,MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(MainFragment.CHANGE_OCCURED,true);
        editor.apply();
    }

    private Date addTimeToDate(int mins){
        Date date = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.MINUTE,mins);
        return calendar.getTime();
    }

    private int valueFromSpinner(){
        switch(mSnoozeSpinner.getSelectedItemPosition()){
            case 0:
                return 10;
            case  1:
                return 30;
            case 2:
                return 60;
            default:
                return 0;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item ) {
        switch (item.getItemId()) {
            case R.id.toDoReminderDoneMenuItem:

                Date date = addTimeToDate(valueFromSpinner());
                mItem.setColorDate(date);
                mItem.setmHasReminder(true);
                Log.d("group8", "Date changed to: " + date);
                changedOccurred();
                saveData();

                closeApp();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void saveData(){
        try {
            storeRetrieveData.saveToFile(mToDoItems);
        } catch (JSONException | IOException e){
            e.printStackTrace();
        }
    }

    public static ReminderFragment newInstance(){return  new ReminderFragment();}

}
