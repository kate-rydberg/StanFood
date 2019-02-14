package stanford.cs194.stanfood;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Date;

public class EventAdapter extends ArrayAdapter {

    private ArrayList<String> name;
    private ArrayList<Long> time;
    private ArrayList<String> description;

    private Context context;

    public EventAdapter(Context context, ArrayList<String> name, ArrayList<Long> time, ArrayList<String> description) {
        super(context, R.layout.list_view, description);
        this.context = context;
        this.name = name;
        this.time = time;
        this.description = description;
    }

    @NonNull
    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View rowView = inflater.inflate(R.layout.list_view, null, true);

        //this code gets references to objects in the list_item.xml file
        TextView eventName = rowView.findViewById(R.id.eventName);
        TextView eventTimeStart = rowView.findViewById(R.id.eventTimeStart);
        TextView eventDescription = rowView.findViewById(R.id.eventDescription);

        //this code sets the values of the objects to values from the arraylists
        if(!name.get(position).equals("")) eventName.setText(name.get(position));
        else  eventName.setText("N/A");

        if(time.get(position)!=null) eventTimeStart.setText(new Date(time.get(position)).toString());
        else eventTimeStart.setText("N/A");

        if(!description.get(position).equals("")) eventDescription.setText(description.get(position));
        else eventDescription.setText("N/A");

        return rowView;
    }

}