package com.example.todoproject.Main;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.todoproject.R;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.todoproject.AppDefault.AppDefaultFragment;
import com.example.todoproject.Reminder.ReminderFragment;
import com.example.todoproject.Utility.ItemTouchHelperClass;
import com.example.todoproject.Utility.RecyclerViewEmptySupport;
import com.example.todoproject.Utility.StoreRetrieveData;
import com.example.todoproject.Utility.ToDoItem;
import  com.example.todoproject.AddToDO.AddToDoFragment;
import com.example.todoproject.AddToDO.AddToDoActivity;
import com.example.todoproject.Utility.TodoNotidicationService;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;

import static android.app.Activity.RESULT_CANCELED;
import static android.content.Context.ALARM_SERVICE;
import  static android.content.Context.MODE_PRIVATE;

public class MainFragment extends AppDefaultFragment {

    private String theme = "name_of_theme";
    private int mTheme = -1;
    public static final String THEME_REFERENCES =  "com.todoproject.thereref";
    public static final String THEMR_SAVED = "com.todoproject.savedtheme";
    public static final String LIGHTTHEME = "com.todoproject.lighttheme";
    public static final String DARKTHEME = "com.todoproject.darktheme";
    public static final String CHANGE_OCCURED = "com.todoproject.changeoccured";
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
    private ToDoItem mJustDeletedToDoItem;
    private int mIndexOfDeletedToDoItem;
    public static final String DATE_TIME_FORMAT_12_HOUR = "MMM d, yyyy  h:mm a";
    public static final String DATE_TIME_FORMAT_24_HOUR = "MMM d, yyyy  k:mm";


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
                    if(item.getToDoDate().before(new Date())){
                        item.setToDoDate(null);
                        continue;
                    }
                    Intent i = new Intent(getContext(), TodoNotidicationService.class);
                    i.putExtra(TodoNotidicationService.TODOUUID,item.getIdentifier());
                    i.putExtra(TodoNotidicationService.TODOTEXT,item.getToDoText());
                    createAlarm(i, item.getIdentifier().hashCode(),item.getToDoDate().getTime());
                }
            }
        }
        }
        @Override
        public void onActivityResult(int requestCode, int resultCode,Intent data){
        if(resultCode != RESULT_CANCELED && requestCode == REQUEST_ID_TO_DO_ITEM){
            ToDoItem item = (ToDoItem) data.getSerializableExtra(TODOITEM);
            if(item.getToDoText().length()<=0) {
                return;
            }

            boolean existed = false;

            if(item.hasReminder()&&item.getToDoDate() != null){
                Intent i =new Intent(getContext(),TodoNotidicationService.class);
                i.putExtra(TodoNotidicationService.TODOTEXT,item.getToDoText());
                i.putExtra(TodoNotidicationService.TODOUUID,item.getIdentifier());
                createAlarm(i,item.getIdentifier().hashCode(),item.getToDoDate().getTime());

            }
            for(int i=0;i<mToDoItemsArrayList.size();i++){
                if(item.getIdentifier().equals(mToDoItemsArrayList.get(i).getIdentifier())){
                    mToDoItemsArrayList.set(i,item);
                    existed = true;
                    adapter.notifyDataSetChanged();
                    break;
                }
            }
            if(!existed){
                addToDataStore(item);
            }
        }
        }
        private AlarmManager getAlarmManager(){
        return (AlarmManager) getActivity().getSystemService(ALARM_SERVICE);
        }
        private boolean doesPendingIntendExitst(Intent i , int requestCode){
        Context context;
        Intent intent;
        PendingIntent pi = PendingIntent.getService(getContext(), requestCode, i, PendingIntent.FLAG_UPDATE_CURRENT);
        return pi!=null;
        }

        private void createAlarm(Intent i,int requestCode,long timeInMillis){
        AlarmManager am = getAlarmManager();
        PendingIntent pi = PendingIntent.getService(getContext(),requestCode,i,PendingIntent.FLAG_UPDATE_CURRENT);
        am.set(AlarmManager.RTC_WAKEUP,timeInMillis,pi);

        }

        private void deleteAlarm(Intent i, int requestCode){
            if (doesPendingIntendExitst(i, requestCode)) {
                PendingIntent pi = PendingIntent.getService(getContext(),requestCode,i,PendingIntent.FLAG_NO_CREATE);
                pi.cancel();
                getAlarmManager().cancel(pi);
                Log.d("Group8","PI Cancelled"+doesPendingIntendExitst(i,requestCode));
            }
        }

        private void addToDataStore(ToDoItem item){
        mToDoItemsArrayList.add(item);
        adapter.notifyItemInserted(mToDoItemsArrayList.size()-1);
        }

        public class BasicListAdapter extends RecyclerView.Adapter<BasicListAdapter.ViewHolder> implements ItemTouchHelperClass.ItemTouchHelperAdapter{
        private  ArrayList<ToDoItem> items;

        @Override
            public void onItemMoved(int fromPosition, int toPosition)
        {
            if(fromPosition<toPosition){
                for(int i =fromPosition;i<toPosition;i++){
                    Collections.swap(items,i,i+1);
                }
            } else {
                for(int i =fromPosition;i>toPosition;i--){
                    Collections.swap(items, i,i-1);
                }
            }
            notifyItemMoved(fromPosition, toPosition);
        }

        @Override
        public void onItemRemoved(final int position) {
            mJustDeletedToDoItem = items.remove(position);
            mIndexOfDeletedToDoItem = position;
            Intent i = new Intent(getContext(), TodoNotidicationService.class);
            deleteAlarm(i, mJustDeletedToDoItem.getIdentifier().hashCode());
            notifyItemRemoved(position);

            String toShow = "ToDo";
            Snackbar.make(mCoordLayout, "Delete" + toShow, Snackbar.LENGTH_LONG).setAction("UNDO", (v) -> {
                items.add(mIndexOfDeletedToDoItem, mJustDeletedToDoItem);
                if (mJustDeletedToDoItem.getToDoDate() != null && mJustDeletedToDoItem.hasReminder()) {
                    Intent intend = new Intent(getContext(), TodoNotidicationService.class);
                    intend.putExtra(TodoNotidicationService.TODOTEXT, mJustDeletedToDoItem.getToDoText());
                    intend.putExtra(TodoNotidicationService.TODOUUID, mJustDeletedToDoItem.getIdentifier());
                    createAlarm(intend, mJustDeletedToDoItem.getIdentifier().hashCode(), mJustDeletedToDoItem.getToDoDate().getTime());
                }
                notifyItemInserted(mIndexOfDeletedToDoItem);
            }).show();
        }

            @Override
            public BasicListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
               View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_circle_try, parent, false);
                return new ViewHolder(v);
            }

            @Override
            public void onBindViewHolder(final BasicListAdapter.ViewHolder holder,final int position) {
                ToDoItem item = items.get(position);
                SharedPreferences sharedPreferences = getActivity().getSharedPreferences(THEME_REFERENCES,MODE_PRIVATE);
                int bgColor;

                int toDoTextColor;
                if(sharedPreferences.getString(THEMR_SAVED,LIGHTTHEME).equals(LIGHTTHEME)){
                    bgColor = Color.WHITE;
                    toDoTextColor = getResources().getColor(R.color.secondary_text);

                }else{
                    bgColor = Color.DKGRAY;
                    toDoTextColor = Color.WHITE;
                }
                holder.linearLayout.setBackgroundColor(bgColor);
                if (item.hasReminder() && item.getToDoDate() != null) {
                    holder.mToDoTextView.setMaxLines(1);
                    holder.mTimeTextView.setVisibility(View.VISIBLE);
                } else {
                    holder.mTimeTextView.setVisibility(View.GONE);
                    holder.mToDoTextView.setMaxLines(2);
                }
                holder.mToDoTextView.setText(item.getToDoText());
                holder.mToDoTextView.setTextColor(toDoTextColor);
                TextDrawable myDrawable = TextDrawable.builder().beginConfig()
                        .textColor(Color.WHITE)
                        .useFont(Typeface.DEFAULT)
                        .toUpperCase()
                        .endConfig()
                        .buildRound(item.getToDoText().substring(0, 1), item.getToDoColor());
                holder.mColorImageView.setImageDrawable(myDrawable);
                if (item.getToDoDate() != null) {
                    String timeToShow;
                    if (android.text.format.DateFormat.is24HourFormat(getContext())) {
                        timeToShow = AddToDoFragment.formatDate(MainFragment.DATE_TIME_FORMAT_24_HOUR, item.getToDoDate());
                    } else {
                        timeToShow = AddToDoFragment.formatDate(MainFragment.DATE_TIME_FORMAT_12_HOUR, item.getToDoDate());
                    }
                    holder.mTimeTextView.setText(timeToShow);
                }

            }

            @Override
            public int getItemCount() {
                return items.size();
            }

            BasicListAdapter(ArrayList<ToDoItem> items){
            this.items= items;
            }

            public class ViewHolder extends RecyclerView.ViewHolder{
                View mView;
                LinearLayout linearLayout;
                ImageView mColorImageView;
                TextView mTimeTextView;
                TextView mToDoTextView;

                public ViewHolder(View v){
                    super(v);
                    mView = v;
                    v.setOnClickListener((view)->{
                        ToDoItem item = items.get(ViewHolder.this.getAdapterPosition());
                        Intent i = new Intent(getContext(),AddToDoActivity.class);
                        i.putExtra(TODOITEM,item);
                        startActivityForResult(i,REQUEST_ID_TO_DO_ITEM);
                    });

                    mToDoTextView = (TextView) v.findViewById(R.id.todoListItemTimeTextView);
                    mTimeTextView = (TextView) v.findViewById(R.id.toDoListItemTimeTextView);
                    mColorImageView = (ImageView) v.findViewById(R.id.toDoListItemColorImageView);
                    linearLayout = (LinearLayout) v.findViewById(R.id.listItemLinearLayout);
                }
            }
        }
        @Override
        public void onPause(){
        super.onPause();
        try {
            storeRetrieveData.saveToFile(mToDoItemsArrayList);
        }catch (JSONException|IOException e)
        {
            e.printStackTrace();
        }
        }

        @Override
    public void onDestroy(){
        super.onDestroy();
        mRecyclerView.removeOnScrollListener(customRecylerScrollViewListener);
        }

        @Override
    protected  int layoutRes(){return  R.layout.fragment_main;}

    public static MainFragment newInstance(){return new MainFragment();}

}
