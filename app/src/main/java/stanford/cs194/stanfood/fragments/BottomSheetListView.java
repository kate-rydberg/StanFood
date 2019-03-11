package stanford.cs194.stanfood.fragments;

import android.content.Context;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.NestedScrollView;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.AbsListView;
import android.widget.LinearLayout;
import android.widget.ListView;

public class BottomSheetListView extends ListView {

    public BottomSheetListView (Context context, AttributeSet p_attrs) {
        super (context, p_attrs);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        LinearLayout parent = (LinearLayout) getParent();
        NestedScrollView grandparent = (NestedScrollView) parent.getParent();
        if (canScrollVertically(this)) {
            ViewCompat.setNestedScrollingEnabled(grandparent, false);
        }
        else{
            ViewCompat.setNestedScrollingEnabled(grandparent, true);
        }
        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        LinearLayout parent = (LinearLayout) getParent();
        NestedScrollView grandparent = (NestedScrollView) parent.getParent();
        if (canScrollVertically(this)) {
            ViewCompat.setNestedScrollingEnabled(grandparent, false);
        }
        else{
            ViewCompat.setNestedScrollingEnabled(grandparent, true);
        }
        return super.onTouchEvent(ev);
    }

    /**
     * Checks for whether the listview is at top or if all of the items
     * are available in the view.
     */
    public boolean canScrollVertically (AbsListView view) {
        boolean canScroll = false;

        if (view != null && view.getChildCount() > 0) {

            boolean isNotOnTop = view.getFirstVisiblePosition() != 0 || view.getChildAt(0).getTop() != 0;

            if (isNotOnTop)  canScroll = true;
        }

        return canScroll;
    }

}
