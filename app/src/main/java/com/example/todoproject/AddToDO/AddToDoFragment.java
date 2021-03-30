package com.example.todoproject.AddToDO;

import android.animation.Animator;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.example.todoproject.Main.MainFragment;
import com.example.todoproject.R;


import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NavUtils;


import com.example.todoproject.AppDefault.AppDefaultFragment;
import com.example.todoproject.Utility.ToDoItem;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.time.RadialPickerLayout;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;


import android.text.format.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;
import static android.content.Context.INPUT_METHOD_SERVICE;
import static android.content.Context.MODE_PRIVATE;


public class AddToDoFragment extends AppDefaultFragment implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {
    private static final String TAG = "AddToDoFragment";
    private Date mLastEdited;

    private EditText mToDoTextBodyEditText;
    private EditText mToDoTextBodyDescription;

    private SwitchCompat mToDoDateSwitch;
    private LinearLayout mUserDataSpinnerContainingLinearLayout;

    private String theme;
    private TextView mReminderTextView;
    private String combinationText;
    private EditText mDateEditText;
    private EditText mTimeEditText;
    private String mDefaultTimeOptions12h[];
    private String mDefaultTimeOptions24h[];
    private Button mCopyClipboard;

    private ToDoItem mUserToDoItem;
    private FloatingActionButton mToDoSendFloatingActionButton;
    private String mUserEnteredText;
    private String mUserEnteredDescription;
    private boolean mUserHasReminder;
    private Toolbar mToolbar;
    private Date mUserReminderDate;
    private int mUserColor;
    private LinearLayout mContainerLayout;

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);
        ImageButton reminderIconImageButton;
        TextView reminderRemindMeTextView;
        theme = getActivity().getSharedPreferences(MainFragment.THEME_REFERENCES,MODE_PRIVATE).getString(MainFragment.THEMR_SAVED,MainFragment.LIGHTTHEME);
        if(theme.equals(MainFragment.LIGHTTHEME)){
            getActivity().setTheme(R.style.CustomStyle_LightTheme);
            Log.d("Group8","Light Theme");
        } else{
            getActivity().setTheme(R.style.CustomStyle_DarkTheme);
        }

        final Drawable cross = getResources().getDrawable(R.drawable.ic_clear_white_24dp);
        if(cross != null){
            cross.setColorFilter(getResources().getColor(R.color.icons), PorterDuff.Mode.SRC_ATOP);

        }

        mToolbar = (Toolbar) view.findViewById(R.id.toolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(mToolbar);

        if(((AppCompatActivity) getActivity()).getSupportActionBar()!=null){
            ((AppCompatActivity) getActivity()).getSupportActionBar().setElevation(0);
            ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayShowTitleEnabled(false);
            ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            ((AppCompatActivity) getActivity()).getSupportActionBar().setHomeAsUpIndicator(cross);
        }
        mUserToDoItem = (ToDoItem) getActivity().getIntent().getSerializableExtra(MainFragment.TODOITEM);
        mUserEnteredText = mUserToDoItem.getToDoText();
        mUserEnteredDescription=mUserToDoItem.getmToDoDescription();
        mUserHasReminder = mUserToDoItem.hasReminder();
        mUserReminderDate = mUserToDoItem.getToDoDate();
        mUserColor = mUserToDoItem.getToDoColor();

        reminderIconImageButton = (ImageButton) view.findViewById(R.id.userToDoReminderIconImageButton);
        reminderRemindMeTextView = (TextView) view.findViewById(R.id.userToDoReminderMeTextView);
        if(theme.equals(MainFragment.DARKTHEME)){
            reminderIconImageButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_add_alarm_grey_200_24dp));
            reminderRemindMeTextView.setTextColor(Color.WHITE);
        }

        mCopyClipboard = (Button) view.findViewById(R.id.copyClipBoard);
        mContainerLayout = (LinearLayout) view.findViewById(R.id.toDoReminderAndDateContainerLayout);
        mUserDataSpinnerContainingLinearLayout = (LinearLayout) view.findViewById(R.id.toDoEnterDateLinearLayout);
        mToDoTextBodyEditText = (EditText) view.findViewById(R.id.userToDoEditText);
        mToDoTextBodyDescription = (EditText) view.findViewById(R.id.userToDoDescription);
        mToDoDateSwitch = (SwitchCompat) view.findViewById(R.id.toDoHasDateSWitchCompat);
        mToDoSendFloatingActionButton = (FloatingActionButton) view.findViewById(R.id.makeToDoFloatingActionButton);
        mReminderTextView = (TextView) view.findViewById(R.id.newToDoDateTimeReminderTextView);

        mCopyClipboard.setOnClickListener((v) -> {
            String toDoTextContainer = mToDoTextBodyEditText.getText().toString();
            String toDoTextBodyDescriptionContainer = mToDoTextBodyDescription.getText().toString();
            ClipboardManager clipboard = (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
            combinationText = "Title: "+toDoTextContainer + "\nDescription: "+ toDoTextBodyDescriptionContainer+"\nCopied from Notes";
            ClipData clip = ClipData.newPlainText("Text", combinationText);
            clipboard.setPrimaryClip(clip);
            Toast.makeText(getContext(),"Copied to clipboard",Toast.LENGTH_SHORT).show();
        });

        mContainerLayout.setOnClickListener((v)->{
            hideKeyboard(mToDoTextBodyEditText);
            hideKeyboard(mToDoTextBodyDescription);
        });

        if(mUserHasReminder && (mUserReminderDate != null)){
            setReminderTextView();
            setEnterDateLayoutVisibleViewWithAnimation(true);
        }

        if(mUserReminderDate == null){
            mToDoDateSwitch.setChecked((false));
            mReminderTextView.setVisibility(View.INVISIBLE);
        }

        mToDoTextBodyEditText.requestFocus();
        mToDoTextBodyEditText.setText(mUserEnteredText);
        mToDoTextBodyDescription.setText(mUserEnteredDescription);
        Context context;
        InputMethodManager imm = (InputMethodManager) this.getActivity().getSystemService(INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,InputMethodManager.HIDE_IMPLICIT_ONLY);
        mToDoTextBodyEditText.setSelection(mToDoTextBodyEditText.length());

        mToDoTextBodyEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start,int count,int after){

            }

            @Override
            public void onTextChanged(CharSequence s,int start,int before, int count){
                mUserEnteredText = s.toString();
            }

            @Override
            public void afterTextChanged(Editable s){

            }


        });

        mToDoTextBodyDescription.setText(mUserEnteredDescription);
        mToDoTextBodyDescription.setSelection(mToDoTextBodyDescription.length());
        mToDoTextBodyDescription.addTextChangedListener(new TextWatcher(){
            @Override
            public void beforeTextChanged(CharSequence s,int start,int count,int after){

            }
            @Override
            public void onTextChanged(CharSequence s, int start,int before, int count){
                mUserEnteredDescription = s.toString();


            }

            @Override
            public void afterTextChanged(Editable e){}
        });

        setEnterDateLayoutVisible(mToDoDateSwitch.isChecked());
        mToDoDateSwitch.setChecked(mUserHasReminder&&(mUserReminderDate !=null));
        mToDoDateSwitch.setOnCheckedChangeListener((buttonView,isChecked)->{
            if(!isChecked){
                mUserReminderDate = null;
            }

            mUserHasReminder = isChecked;
            setDateAndTimeEditText();
            setEnterDateLayoutVisibleViewWithAnimation(isChecked);
            hideKeyboard(mToDoTextBodyEditText);
            hideKeyboard(mToDoTextBodyDescription);
        });

        mToDoSendFloatingActionButton.setOnClickListener((v)->{
            if(mToDoTextBodyEditText.length() <= 0){
                mToDoTextBodyEditText.setError("Please enter");

            } else if(mUserReminderDate != null && mUserReminderDate.before(new Date())){
                makeResult(RESULT_CANCELED);
            } else {
                makeResult(RESULT_OK);
                getActivity().finish();
            }
            hideKeyboard(mToDoTextBodyEditText);
            hideKeyboard(mToDoTextBodyDescription);
        });

        mDateEditText = (EditText) view.findViewById(R.id.newToDoDateEditText);
        mTimeEditText = (EditText) view.findViewById(R.id.newToDoTImeEditText);

        mDateEditText.setOnClickListener((v)->{
            Date date;
            hideKeyboard(mToDoTextBodyEditText);
            if(mUserToDoItem.getToDoDate() != null){
                date = mUserReminderDate;
            } else {
                date = new Date();

            }
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            int year = calendar.get(Calendar.YEAR);
            int month= calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);
            DatePickerDialog datePickerDialog = DatePickerDialog.newInstance(AddToDoFragment.this,year,month,day);
            if(theme.equals(MainFragment.DARKTHEME)){
                datePickerDialog.setThemeDark(true);
            }
            datePickerDialog.show(getFragmentManager(),"DateFragment");
        });

        mTimeEditText.setOnClickListener((v)->{
           Date date;
           hideKeyboard(mToDoTextBodyEditText);
           if(mUserToDoItem.getToDoDate() != null){
               date = mUserReminderDate;
           } else {
               date = new Date();
           }
           Calendar calendar = Calendar.getInstance();
           calendar.setTime(date);
           int hour = calendar.get(Calendar.HOUR_OF_DAY);
           int minute = calendar.get(Calendar.MINUTE);

           TimePickerDialog timePickerDialog = TimePickerDialog.newInstance(AddToDoFragment.this,hour,minute, DateFormat.is24HourFormat(getContext()));
           if(theme.equals(MainFragment.DARKTHEME)){
               timePickerDialog.setThemeDark(true);
           }
           timePickerDialog.show(getFragmentManager(),"TimeFragment");
        });
        setDateAndTimeEditText();

    }
    private  void setDateAndTimeEditText(){
        if(mUserToDoItem.hasReminder()&&mUserReminderDate != null){
            String userDate = formatDate("d MMM, yyy",mUserReminderDate);
            String formatToUse;
            if(DateFormat.is24HourFormat(getContext())){
                formatToUse = "k:mm";

            } else {
                formatToUse = "h:mm a";
            }
            String userTime = formatDate(formatToUse, mUserReminderDate);
            mTimeEditText.setText(userTime);
            mDateEditText.setText(userTime);
        } else {
            mDateEditText.setText("Today");
            boolean time24 = DateFormat.is24HourFormat(getContext());
            Calendar calendar = Calendar.getInstance();
            if(time24){
                calendar.set(Calendar.HOUR_OF_DAY,calendar.get(Calendar.HOUR_OF_DAY)+1);

            } else {
                calendar.set(Calendar.HOUR,calendar.get(Calendar.HOUR)+1);
            }
            calendar.set(Calendar.MINUTE,0);
            mUserReminderDate = calendar.getTime();
            Log.d("Group8","Imagined Date: "+ mUserReminderDate);
            String timeString;
            if(time24){
                timeString = formatDate("k:mm",mUserReminderDate);
            } else {
                timeString = formatDate("h:mm a",mUserReminderDate);
            }
            mTimeEditText.setText(timeString);
        }
    }

    private  String getThemeSet(){
        return getActivity().getSharedPreferences(MainFragment.THEME_REFERENCES,MODE_PRIVATE).getString(MainFragment.THEMR_SAVED,MainFragment.LIGHTTHEME);
    }

    private  void hideKeyboard(EditText et){
        InputMethodManager imm= (InputMethodManager) getActivity().getSystemService(INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(et.getWindowToken(),0);
    }

    public void setDate(int year, int month, int day){
        Calendar calendar = Calendar.getInstance();
        int hour, minute;
        Calendar reminderCalendar = Calendar.getInstance();
        reminderCalendar.set(year, month, day);
        if(reminderCalendar.before(calendar)){
            return;
        }

        if(mUserReminderDate !=null){
            calendar.setTime(mUserReminderDate);
        }
        if(DateFormat.is24HourFormat(getContext())){
            hour = calendar.get(Calendar.HOUR_OF_DAY);
        } else {
            hour = calendar.get(Calendar.HOUR);
        }
        minute = calendar.get(Calendar.MINUTE);
        calendar.set(year, month, day, hour, minute);
        mUserReminderDate = calendar.getTime();
        setReminderTextView();
        setDateEditText();
    }

    public void setTime(int hour, int minute){
        Calendar calendar = Calendar.getInstance();
        if(mUserReminderDate != null){
            calendar.setTime(mUserReminderDate);
        }
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        Log.d("Group8","Time set: "+ hour);
        calendar.set(year,month,day,hour,minute,0);
        mUserReminderDate = calendar.getTime();

        setReminderTextView();
        setTimeEditText();
    }

    public void setDateEditText(){
        String dateFormat = "d MMM, yyyy";
        mDateEditText.setText(formatDate(dateFormat,mUserReminderDate));
    }

    public void  setTimeEditText(){
        String dateFormat;
        if(DateFormat.is24HourFormat(getContext())){
            dateFormat = "k:mm";
        } else {
            dateFormat = "h:mm a";
        }
        mTimeEditText.setText(formatDate(dateFormat,mUserReminderDate));
    }

    public void setReminderTextView(){
        if (mUserReminderDate != null) {
            mReminderTextView.setVisibility(View.VISIBLE);
            if (mUserReminderDate.before(new Date())) {
                Log.d("Group8", "DATE is " + mUserReminderDate);
                mReminderTextView.setText(getString(R.string.date_error_check_again));
                mReminderTextView.setTextColor(Color.RED);
                return;
            }
            Date date = mUserReminderDate;
            String dateString = formatDate("d MMM, yyyy", date);
            String timeString;
            String amPmString = "";

            if (android.text.format.DateFormat.is24HourFormat(getContext())) {
                timeString = formatDate("k:mm", date);
            } else {
                timeString = formatDate("h:mm", date);
                amPmString = formatDate("a", date);
            }
            String finalString = String.format(getResources().getString(R.string.remind_date_and_time), dateString, timeString, amPmString);
            mReminderTextView.setTextColor(getResources().getColor(R.color.secondary_text));
            mReminderTextView.setText(finalString);
        } else {
            mReminderTextView.setVisibility(View.INVISIBLE);
        }
    }

    public void makeResult(int result){
        Log.d(TAG,"makeResult - ok : in");
        Intent i = new Intent();
        if(mUserEnteredText.length()>0){
            String capitalizedString = Character.toUpperCase(mUserEnteredText.charAt(0))+ mUserEnteredText.substring(1);
            mUserToDoItem.setmToDoText(capitalizedString);
            Log.d(TAG,"Description" + mUserEnteredDescription);
            mUserToDoItem.setmToDoDescription(mUserEnteredDescription);
        } else {
            mUserToDoItem.setmToDoText(mUserEnteredText);
            Log.d(TAG,"Description:"+mUserEnteredDescription);
            mUserToDoItem.setmToDoDescription(mUserEnteredDescription);
        }

        if (mUserReminderDate != null) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(mUserReminderDate);
            calendar.set(Calendar.SECOND, 0);
            mUserReminderDate = calendar.getTime();
        }
        mUserToDoItem.setmHasReminder(mUserHasReminder);
        mUserToDoItem.setToDoDate(mUserReminderDate);
        mUserToDoItem.setToDoColor(mUserColor);
        i.putExtra(MainFragment.TODOITEM, mUserToDoItem);
        getActivity().setResult(result, i);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case  android.R.id.home:
                if(NavUtils.getParentActivityName(getActivity())!=null){
                    makeResult(RESULT_CANCELED);
                    NavUtils.navigateUpFromSameTask(getActivity());
            }
            hideKeyboard(mToDoTextBodyEditText);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public static String formatDate(String formatString, Date dateToFormat){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(formatString);
        return simpleDateFormat.format(dateToFormat);
    }

    @Override
    public void onTimeSet(TimePickerDialog timePickerDialog, int hour, int minute, int seconds){
        setTime(hour,minute);
    }

    @Override
    public void onDateSet(DatePickerDialog radialPickerLayout,int year, int month,int day){
        setDate(year,month,day);
    }

    public void setEnterDateLayoutVisible(boolean checked){
        if(checked){
            mUserDataSpinnerContainingLinearLayout.setVisibility(View.VISIBLE);

        } else {
            mUserDataSpinnerContainingLinearLayout.setVisibility(View.INVISIBLE);
        }
    }

    public void setEnterDateLayoutVisibleViewWithAnimation(boolean checked){
        if(checked){
            setReminderTextView();
            mUserDataSpinnerContainingLinearLayout.animate().alpha(1.0f).setDuration(500).setListener(
                    new Animator.AnimatorListener() {
                        @Override
                        public void onAnimationStart(Animator animation) {
                            mUserDataSpinnerContainingLinearLayout.setVisibility(View.VISIBLE);
                        }

                        @Override
                        public void onAnimationEnd(Animator animation) {
                        }

                        @Override
                        public void onAnimationCancel(Animator animation) {
                        }

                        @Override
                        public void onAnimationRepeat(Animator animation) {
                        }
                    }
            );
        } else {
            mUserDataSpinnerContainingLinearLayout.animate().alpha(0.0f).setDuration(500).setListener(
                    new Animator.AnimatorListener() {
                        @Override
                        public void onAnimationStart(Animator animation) {

                        }

                        @Override
                        public void onAnimationEnd(Animator animation) {
                            mUserDataSpinnerContainingLinearLayout.setVisibility(View.INVISIBLE);
                        }

                        @Override
                        public void onAnimationCancel(Animator animation) {

                        }

                        @Override
                        public void onAnimationRepeat(Animator animation) {

                        }
                    }
            );
        }
    }

    @Override
    protected int layoutRes(){return R.layout.fragment_add_to_do;}
    public static AddToDoFragment newInstance() { return new AddToDoFragment(); }
}
