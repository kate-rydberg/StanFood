package stanford.cs194.stanfood.fragments;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.GoogleMap;

import stanford.cs194.stanfood.R;

public class BottomSheet{
    private View bottomSheetView;
    private BottomSheetBehavior<View> mBottomSheetBehavior;
    private Context context;
    private GoogleMap mMap;
    private float BOTTOM_SHEET_EXPANDED_HEIGHT;
    private final float BOTTOM_SHEET_PEEK_HEIGHT;

    public BottomSheet(Context context, View bottomSheet, final GoogleMap mMap) {
        this.bottomSheetView = bottomSheet;
        this.mBottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
        collapse();
        this.context = context;
        this.mMap = mMap;

        this.BOTTOM_SHEET_EXPANDED_HEIGHT = mBottomSheetBehavior.getPeekHeight();
        this.BOTTOM_SHEET_PEEK_HEIGHT = mBottomSheetBehavior.getPeekHeight();
    }

    public float getExpandedHeight() {
        return BOTTOM_SHEET_EXPANDED_HEIGHT;
    }

    public float getPeekHeight() {
        return BOTTOM_SHEET_PEEK_HEIGHT;
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
                    mMap.setPadding(0, 0, 0, (int)BOTTOM_SHEET_EXPANDED_HEIGHT);
                } else if (newState == mBottomSheetBehavior.STATE_COLLAPSED) {
                    mMap.setPadding(0, 0, 0, (int)BOTTOM_SHEET_PEEK_HEIGHT);
                }
            }
            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                if (!Float.isNaN(slideOffset)) {
                    float padding = slideOffset * (BOTTOM_SHEET_EXPANDED_HEIGHT - BOTTOM_SHEET_PEEK_HEIGHT) + BOTTOM_SHEET_PEEK_HEIGHT;
                    mMap.setPadding(0, 0, 0, (int) padding);
                }
            }
        });
    }

    // initExpandedHeight: initialize the expanded height to the height set in values/dimens
    public void initExpandedHeight() {
        ViewGroup.LayoutParams params = bottomSheetView.getLayoutParams();
        if (params.height == BOTTOM_SHEET_PEEK_HEIGHT) {
            BOTTOM_SHEET_EXPANDED_HEIGHT = context.getResources().getDimension(R.dimen.bottom_sheet_expanded_height);
            params.height = (int)BOTTOM_SHEET_EXPANDED_HEIGHT;
            bottomSheetView.setLayoutParams(params);
        }
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
