package stanford.cs194.stanfood.fragments;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import stanford.cs194.stanfood.R;

public class MapClockFragment extends DialogFragment {
    private OnMapClockSuccessListener callback;
    private static DateTimePickerFragment startDateTimeFrag;
    private static DateTimePickerFragment endDateTimeFrag;
    private Date startDate;
    private Date endDate;

    public static MapClockFragment newInstance() {
        MapClockFragment f = new MapClockFragment();
        startDateTimeFrag = DateTimePickerFragment.newInstance();
        endDateTimeFrag = DateTimePickerFragment.newInstance();
        return f;
    }

    public interface OnMapClockSuccessListener {
        void onSuccess(Date start, Date end);
    }

    public void setDateRange(Date start, Date end){
        this.startDate = start;
        this.endDate = end;
    }

    public void setOnSuccessListener(OnMapClockSuccessListener listener){
        callback = listener;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.map_clock_dialog, container, false);
        final TextView startDateTime = view.findViewById(R.id.startDateTime);
        final TextView endDateTime = view.findViewById(R.id.endDateTime);
        Button done_button = view.findViewById(R.id.button_done);

        startDateTimeFrag.setOnSuccessListener(new DateTimePickerFragment.OnDateTimePickerSuccessListener() {
            @Override
            public void onSuccess(Calendar dateCal) {
                setText(startDateTime, dateCal);
            }
        });

        endDateTimeFrag.setOnSuccessListener(new DateTimePickerFragment.OnDateTimePickerSuccessListener() {
            @Override
            public void onSuccess(Calendar dateCal) {
                setText(endDateTime, dateCal);
            }
        });

        String datePattern = "EEE, MMM d hh:mm aaa";
        SimpleDateFormat sdf = new SimpleDateFormat(datePattern);
        startDateTime.setText(sdf.format(startDate));
        endDateTime.setText(sdf.format(endDate));

        startDateTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchDateTimePickerDialog(v, startDateTimeFrag);
            }
        });
        endDateTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchDateTimePickerDialog(v, endDateTimeFrag);
            }
        });
        done_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String start = (String) startDateTime.getText();
                String end = (String) endDateTime.getText();
                doneButtonListener(v, start, end);
            }
        });
        return view;
    }

    private void launchDateTimePickerDialog(View v, Fragment fragment){
        FragmentTransaction fragmentTransaction = getChildFragmentManager().beginTransaction();
        fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_NONE);
        fragmentTransaction.replace(R.id.container, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    private void doneButtonListener(View v, String startDateTime, String endDateTime){
        /**
         * note years are not accounted for in datePattern format, which can
         * lead to unexpected behavior
         */
        String datePattern = "EEE, MMM d hh:mm aaa";
        SimpleDateFormat sdf = new SimpleDateFormat(datePattern);
        try {
            Date startDate = sdf.parse(startDateTime);
            Date endDate = sdf.parse(endDateTime);
            if(startDate.after(endDate)){
                String toastMessage = "Start time must precede end time";
                Toast toast = Toast.makeText(getContext(), toastMessage, Toast.LENGTH_SHORT);
                final int BOTTOM_SHEET_PEEK_HEIGHT = (int)getContext().getResources().
                        getDimension(R.dimen.bottom_sheet_peek_height);
                toast.setGravity(Gravity.BOTTOM, 0, BOTTOM_SHEET_PEEK_HEIGHT);
                toast.show();
            }
            else {
                callback.onSuccess(startDate, endDate);
                dismiss();
            }
        } catch (ParseException e) {
            Log.d("ERRROR", e.toString());
        }
    }

    private void setText(TextView textView, Calendar cal){
        String datePattern = "EEE, MMM d hh:mm aaa";
        SimpleDateFormat sdf = new SimpleDateFormat(datePattern);
        textView.setText(sdf.format(cal.getTime()));
    }
}
