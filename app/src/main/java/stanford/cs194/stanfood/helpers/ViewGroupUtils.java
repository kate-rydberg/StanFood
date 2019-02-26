package stanford.cs194.stanfood.helpers;

import android.view.View;
import android.view.ViewGroup;

public class ViewGroupUtils {

    private static ViewGroup getParent(View view) {
        return (ViewGroup)view.getParent();
    }

    public static void removeView(View view, ViewGroup parent) {
        if (parent == null) {
            parent = getParent(view);
            if (parent == null) return;
        }
        parent.removeView(view);
    }

    public static void hideView(View view) {
        if(view != null) {
            view.setVisibility(View.GONE);
        }
    }

    public static void revealView(View view) {
        if(view != null) {
            view.setVisibility(View.VISIBLE);
        }
    }

    public static void replaceView(View currentView, View newView, ViewGroup parent) {
        if (parent == null) {
            parent = getParent(currentView);
            if(parent == null) return;
        }
        final int index = parent.indexOfChild(currentView);
        removeView(currentView, parent);
        parent.addView(newView, index);
    }

    public static void replaceOnlyChild(View newView, ViewGroup parent) {
        if(parent == null) return;
        parent.removeViewAt(0);
        parent.addView(newView, 0);
    }

    public static void softReplaceView(View currentView, View newView, ViewGroup parent) {
        if(parent == null) {
            return;
        }
        final int index = parent.indexOfChild(currentView);
        hideView(currentView);;
        parent.addView(newView, index);
    }
}