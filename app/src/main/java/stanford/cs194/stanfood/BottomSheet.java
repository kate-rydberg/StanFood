package stanford.cs194.stanfood;

import android.support.design.widget.BottomSheetBehavior;
import android.view.View;


public class BottomSheet {
    private BottomSheetBehavior<View> mBottomSheetBehavior;

    public BottomSheet(View bottomSheet) {
        mBottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
        collapse();
    }

    public void setText() {
        // TODO
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
