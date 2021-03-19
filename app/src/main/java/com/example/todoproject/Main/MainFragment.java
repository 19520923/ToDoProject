package com.example.todoproject.Main;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import com.example.todoproject.R;

import androidx.annotation.Nullable;

import com.example.todoproject.AppDefault.AppDefaultFragment;
import com.example.todoproject.Utility.StoreRetrieveData;
import com.example.todoproject.Utility.ToDoItem;

import java.util.ArrayList;

public class MainFragment extends AppDefaultFragment {

    private String theme = "name_of_theme";
    private int mTheme = -1;
    private static final String THEME_REFERENCES =  "com.todoproject.thereref";
    private static final int MODE_PRIVATE = 0;
    private static final String THEMR_SAVED = "com.todoproject.savedtheme";
    private static final String LIGHTTHEME = "com.todoproject.lighttheme";
    private static final String CHANGE_OCCURED = "com.todoproject.changeoccured"
    private StoreRetrieveData storeRetrieveData;
    private static final String FILENAME = "todoitems.json";
    private ArrayList<ToDoItem> mToDoItemsArrayList;
    private MainFragment.BasicListAdapter adapter;


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);
        theme = getActivity().getSharedPreferences(THEME_REFERENCES, MODE_PRIVATE).getString(THEMR_SAVED, LIGHTTHEME);
        if(theme.equals(LIGHTTHEME)){
            mTheme = R.style.CustomStyle_LightTheme;
        } else{
            mTheme = R.style.CustomStyle_DarkTheme;
        }
        this.getActivity().setTheme(mTheme);

        super.onCreate(savedInstanceState);

        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(SHARED_PREF_DATA_SET_CHANGE,MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(CHANGE_OCCURED,false);
        editor.apply();

        storeRetrieveData = new StoreRetrieveData(getContext(),FILENAME);
        mToDoItemsArrayList = getLocallyStoredData(storeRetrieveData);
        adapter = new MainFragment.BasicListAdapter(mToDoItemsArrayList);
        setAlarms();
        mCoorLayout
    }

}
