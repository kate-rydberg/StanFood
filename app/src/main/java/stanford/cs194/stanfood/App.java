package stanford.cs194.stanfood;

import android.app.Application;
import android.content.Context;

public class App extends Application {

    private static Application sApp;

    public static Application getApplication() {
        return sApp;
    }

    public static Context getContext() {
        return getApplication().getApplicationContext();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        sApp = this;
    }
}
