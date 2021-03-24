package com.example.todoproject.Main;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;

import com.example.todoproject.R;

import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.todoproject.AppDefault.AppDefaultFragment;
import com.example.todoproject.Utility.ItemTouchHelperClass;
import com.example.todoproject.Utility.RecyclerViewEmptySupport;
import com.example.todoproject.Utility.StoreRetrieveData;
import com.example.todoproject.Utility.ToDoItem;
import  com.example.todoproject.AddToDO.AddToDoFragment;
import com.example.todoproject.AddToDO.AddToDoActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

import  static android.content.Context.MODE_PRIVATE;

public class MainFragment extends AppDefaultFragment {

    private String theme = "name_of_theme";
    private int mTheme = -1;
    public static final String THEME_REFERENCES =  "com.todoproject.thereref";
    public static final String THEMR_SAVED = "com.todoproject.savedtheme";
    public static final String LIGHTTHEME = "com.todoproject.lighttheme";
    public static final String CHANGE_OCCURED = "com.todoproject.changeoccured"
    private StoreRetrieveData storeRetrieveData;
    public static final String FILENAME = "todoitems.json";
    private ArrayList<ToDoItem> mToDoItemsArrayList;
    private MainFragment.BasicListAdapter adapter;
    private CoordinatorLayout mCoordLayout;
    private FloatingActionButton mAddToDoItemFAB;
    public static final String TODOITEM = "com.todoproject.com.todoproject.MainActivity";
    public static final String SHARED_PREF_DATA_SET_CHANGE = "com.todoproject.datasetchanged";
    private  static final int REQUEST_ID_TO_DO_ITEM = 100;
    private RecyclerViewEmptySupport mRecyclerView;
    private CustomRecylerScrollViewListener customRecylerScrollViewListener;
    private ItemTouchHelper itemTouchHelper;
    private static final String RECREAT_ACTIVITY = "com.todoproject.recreateactivity";

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
        mCoordLayout = (CoordinatorLayout) view.findViewById(R.id.myCoordinatorLayout);
        mAddToDoItemFAB = (FloatingActionButton) view.findViewById(R.id.addToDoItemFAB);

        mAddToDoItemFAB.setOnClickListener((v) ->{
            Intent newToDO = new Intent(getContext(),AddToDoActivity.class);
            ToDoItem item = new ToDoItem("","",false,null);
            int color = ColorGenerator.MATERIAL.getRandomColor();
            item.setToDoColor(color);
            newToDO.putExtra(TODOITEM,item);
            startActivityForResult(newToDO, REQUEST_ID_TO_DO_ITEM);
        });

        mRecyclerView = (RecyclerViewEmptySupport) view.findViewById(R.id.toDoRecycleView);
        if(theme.equals(LIGHTTHEME)){
            mRecyclerView.setBackgroundColor(getResources().getColor(R.color.primary_lightest));

        }
        mRecyclerView.setEmptyView(view.findViewById(R.id.toDoEmptyView));
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        customRecylerScrollViewListener = new CustomRecylerScrollViewListener() {
            @Override
            public void show() {
                mAddToDoItemFAB.animate().translationY(0).setInterpolator(new DecelerateInterpolator(2)).start();
            }

            @Override
            public void hide() {
                CoordinatorLayout.LayoutParams lp = (CoordinatorLayout.LayoutParams) mAddToDoItemFAB.getLayoutParams();
                int fabMargin = lp.bottomMargin;
                mAddToDoItemFAB.animate().translationY(mAddToDoItemFAB.getHeight()+fabMargin).setInterpolator(new AccelerateInterpolator(2.0f)).start();
            }
        };

        ItemTouchHelper.Callback callback = new ItemTouchHelperClass(adapter);
        itemTouchHelper = new ItemTouchHelper(callback);
        itemTouchHelper.attachToRecyclerView(mRecyclerView);

        mRecyclerView.setAdapter(adapter);
    }

    public static ArrayList<ToDoItem> getLocallyStoredData(StoreRetrieveData storeRetrieveData){
        ArrayList<ToDoItem> items = null;
        try{
            items = storeRetrieveData.loadFromFile();
        } catch (IOException | JSONException e){
            e.printStackTrace();
        }

        if(items == null){
            items = new ArrayList<>();
        }
        return items;
    }

    @Override
    public void onResume() {
        super.onResume();
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(SHARED_PREF_DATA_SET_CHANGE, MODE_PRIVATE);
        if (sharedPreferences.getBoolean(ReminderFragment.EXIT, false)) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean(ReminderFragment.EXIT, false);
            editor.apply();
            getActivity().finish();
        }

        if (getActivity().getSharedPreferences(THEME_REFERENCES, MODE_PRIVATE).getBoolean(RECREAT_ACTIVITY, false)) {
            SharedPreferences.Editor editor = getActivity().getSharedPreferences(THEME_REFERENCES, MODE_PRIVATE).edit();
            editor.putBoolean(RECREAT_ACTIVITY, false);
            editor.apply();
            getActivity().recreate();
        }
    }
        @Override
        public void onStart(){
            super.onStart();
            SharedPreferences sharedPreferences = getActivity().getSharedPreferences(SHARED_PREF_DATA_SET_CHANGE,MODE_PRIVATE);
            if(sharedPreferences.getBoolean(CHANGE_OCCURED,false)){
                mToDoItemsArrayList = getLocallyStoredData((storeRetrieveData));
                adapter = new MainFragment.BasicListAdapter(mToDoItemsArrayList);
                mRecyclerView.setAdapter(adapter);
                setAlarms();

                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean(CHANGE_OCCURED,false);
                editor.apply();
            }
        }

        private void setAlarms(){
        if(mToDoItemsArrayList != null){
            for(ToDoItem item:mToDoItemsArrayList){
                if(item.hasReminder()&&item.getToDoDate() != null){
                    if(item.getToDoDate().before((new Date())){
                        item.setToDoDate(null);
                        continue;
                    }
                    Intent i = new Intent(getContext(),TodoNotificationService.class);
                }
            }
        }
        }
}
