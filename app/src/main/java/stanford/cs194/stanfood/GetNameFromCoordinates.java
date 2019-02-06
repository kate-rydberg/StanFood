package stanford.cs194.stanfood;

import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class GetNameFromCoordinates extends AsyncTask<Object, Void, String[]> {
    @Override
    protected String[] doInBackground(Object ... params) {
        String pinId = (String) params[0];
        LatLng loc = (LatLng) params[1];
        Geocoder gcd = new Geocoder(App.getContext(), Locale.US);
        String addressLine = "";
        try {
            List<Address> addresses = gcd.getFromLocation(loc.latitude, loc.longitude, 1);
            Address addr = addresses.get(0);
            for (int n = 0; n <= addr.getMaxAddressLineIndex(); n++) {
                addressLine += addr.getAddressLine(n);
            }
        } catch (IOException e) {
            e.printStackTrace();
            Log.d("ERROR", e.toString());
        }
        String[] ret = {pinId, addressLine};
        return ret;
    }

    @Override
    protected void onPostExecute(String[] result) {
        String pinId = result[0];
        String addressLine = result[1];
        Database db = new Database();
        db.dbRef.child("pins/"+pinId+"/name").setValue(addressLine);
    }
}
