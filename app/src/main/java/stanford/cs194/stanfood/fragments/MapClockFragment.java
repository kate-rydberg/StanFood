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
import java.util.GregorianCalendar;

import stanford.cs194.stanfood.R;

public class MapClockFragment extends DialogFragment {
    private static DateTimePickerFragment startDateTimeFrag;
    private static DateTimePickerFragment endDateTimeFrag;

    public static MapClockFragment newInstance() {
        MapClockFragment f = new MapClockFragment();
        return f;
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
        startDateTimeFrag = DateTimePickerFragment.newInstance(startDateTime);
        endDateTimeFrag = DateTimePickerFragment.newInstance(endDateTime);

        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);
        Calendar dateCal = new GregorianCalendar(year, month, day);

        String datePattern = "EEE, MMM d hh:mm aaa";
        SimpleDateFormat sdf = new SimpleDateFormat(datePattern);
        startDateTime.setText(sdf.format(dateCal.getTime()));

        dateCal.add(Calendar.DATE, 7);
        endDateTime.setText(sdf.format(dateCal.getTime()));

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
                Toast toast = Toast.makeText(getContext(), "Start time must precede end time", Toast.LENGTH_SHORT);
                final int BOTTOM_SHEET_PEEK_HEIGHT = (int)getContext().getResources().getDimension(R.dimen.bottom_sheet_peek_height);
                toast.setGravity(Gravity.BOTTOM, 0, BOTTOM_SHEET_PEEK_HEIGHT);
                toast.show();
            }
            else {
                dismiss();
            }
        } catch (ParseException e) {
            Log.d("ERRROR", e.toString());
        }
    }
}
