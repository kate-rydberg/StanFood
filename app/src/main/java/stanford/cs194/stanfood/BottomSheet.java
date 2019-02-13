package stanford.cs194.stanfood;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.util.Log;
import android.view.View;

import com.google.android.gms.maps.GoogleMap;


public class BottomSheet {
    private BottomSheetBehavior<View> mBottomSheetBehavior;
    private Context context;
    private GoogleMap mMap;

    public BottomSheet(View bottomSheet, Context context, final GoogleMap mMap) {
        mBottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
        collapse();
        this.context = context;
        this.mMap = mMap;
    }

    public void setText() {
        // TODO
    }

    public void moveListener() {
        mBottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {

            /**
             * Changes padding of the map when the state of the bottom sheet is changed.
             * e.g. by clicking a marker.
             */
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                if (newState == mBottomSheetBehavior.STATE_EXPANDED) {
                    float expanded_height = context.getResources().getDimension(R.dimen.bottom_sheet_expanded_height);
                    mMap.setPadding(0, 0, 0, (int)expanded_height);
                } else if (newState == mBottomSheetBehavior.STATE_COLLAPSED) {
                    int peek_height = mBottomSheetBehavior.getPeekHeight();
                    mMap.setPadding(0, 0, 0, peek_height);
                }
            }
            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                float expanded_height = context.getResources().getDimension(R.dimen.bottom_sheet_expanded_height);
                Log.d("context", Float.toString(expanded_height));
                int peek_height = mBottomSheetBehavior.getPeekHeight();
                float padding = slideOffset * (expanded_height - peek_height) + peek_height;
                mMap.setPadding(0, 0, 0, (int)padding);
            }

        });
    }

    public void setPeakHeightPadding(final GoogleMap mMap ){
        mMap.setPadding(0, 0, 0, mBottomSheetBehavior.getPeekHeight());
    }

    // expand: expands the bottom sheet
    public void expand() {
        if (mBottomSheetBehavior.isHideable()) {
            mBottomSheetBehavior.setHideable(false);
        }
        mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
    }

    // collapse: collapse the bottom sheet to peek height
    public void collapse() {
        if (mBottomSheetBehavior.isHideable()) {
            mBottomSheetBehavior.setHideable(false);
        }
        mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
    }

    // hide: hides the bottom sheet
    public void hide() {
        mBottomSheetBehavior.setHideable(true);
        mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
    }

    // isExpanded: returns true if the bottom sheet is expanded
    public boolean isExpanded() {
        return mBottomSheetBehavior.getState() == mBottomSheetBehavior.STATE_EXPANDED;
    }

    // isExpanded: returns true if the bottom sheet is expanded
    public boolean isCollapsed() {
        return mBottomSheetBehavior.getState() == mBottomSheetBehavior.STATE_COLLAPSED;
    }

    // isExpanded: returns true if the bottom sheet is expanded
    public boolean isHidden() {
        return mBottomSheetBehavior.getState() == mBottomSheetBehavior.STATE_HIDDEN;
    }
}
