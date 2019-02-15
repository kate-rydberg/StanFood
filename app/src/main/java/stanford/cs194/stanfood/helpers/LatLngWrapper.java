package stanford.cs194.stanfood.helpers;

import com.google.android.gms.maps.model.LatLng;

public class LatLngWrapper {
    private double latitude;
    private double longitude;

    public LatLngWrapper(){}

    public LatLngWrapper(double lat, double lng){
        latitude = lat;
        longitude = lng;
    }

    public double getLatitude() { return latitude; }
    public double getLongitude() { return longitude; }

    public LatLng getLatLng(){
        return new LatLng(latitude, longitude);
    }
}
