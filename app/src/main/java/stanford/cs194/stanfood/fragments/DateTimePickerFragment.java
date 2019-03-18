package stanford.cs194.stanfood.fragments;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.design.widget.TabLayout;
import android.support.v4.app.DialogFragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;

import stanford.cs194.stanfood.R;

public class DateTimePickerFragment extends DialogFragment {
    private TextView mTextView;
    public static DateTimePickerFragment newInstance(TextView textView) {
        DateTimePickerFragment f = new DateTimePickerFragment();
        f.mTextView = textView;
        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.date_time_picker, container, false);
        final ViewPager viewPager = (ViewPager) view.findViewById(R.id.view_pager);
        Button button_done = view.findViewById(R.id.button_done);
        button_done.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {
                doneButtonListener(view);
            }
        });

        Button button_cancel = view.findViewById(R.id.button_cancel);
        button_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelButtonListener(v);
            }
        });

        viewPager.setAdapter(new PagerAdapter() {
            @Override
            public Object instantiateItem(ViewGroup container, int position) {
                return viewPager.getChildAt(position);
            }

            @Override
            public CharSequence getPageTitle(int position) {
                switch (position) {
                    case 0:
                        return "Date";
                    case 1:
                        return "Time";
                }
                return super.getPageTitle(position);
            }

            @Override
            public int getCount() {
                return viewPager.getChildCount();
            }

            @Override
            public boolean isViewFromObject(View view, Object object) {
                return view == object;
            }
        });
        final TabLayout layoutTab = (TabLayout) view.findViewById(R.id.tabs);
        layoutTab.setupWithViewPager(viewPager);

        return view;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void doneButtonListener(View v){
        DatePicker datePicker = v.findViewById(R.id.date_picker);
        TimePicker timePicker = v.findViewById(R.id.time_picker);

        int year = datePicker.getYear();
        int month = datePicker.getMonth();
        int day = datePicker.getDayOfMonth();

        int hourOfDay = timePicker.getHour();
        int minute = timePicker.getMinute();

        Calendar dateCal = new GregorianCalendar(year, month, day, hourOfDay, minute);

        String datePattern = "EEE, MMM d hh:mm aaa";
        SimpleDateFormat sdf = new SimpleDateFormat(datePattern);
        mTextView.setText(sdf.format(dateCal.getTime()));
        getFragmentManager().popBackStack();
    }

    private void cancelButtonListener(View v){
        getFragmentManager().popBackStack();
    }
}
