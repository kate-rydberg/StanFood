package stanford.cs194.stanfood.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import stanford.cs194.stanfood.App;
import stanford.cs194.stanfood.R;
import stanford.cs194.stanfood.activities.CreateEventActivity;
import stanford.cs194.stanfood.activities.LoginActivity;
import stanford.cs194.stanfood.activities.UserSettingsActivity;
import stanford.cs194.stanfood.helpers.FirebaseInstanceIdAccessor;

import static android.content.Context.MODE_PRIVATE;


public class NavigationDrawer {
    private Context mContext;
    private DrawerLayout mDrawerLayout;
    private NavigationView mNavigationView;
    private SharedPreferences prefs;
    private FirebaseInstanceIdAccessor instanceIdAccessor;

    public NavigationDrawer(Context context, DrawerLayout drawerLayout, final NavigationView navigationView,
                            FirebaseInstanceIdAccessor instanceIdAccessor) {
        mContext = context;
        mDrawerLayout = drawerLayout;
        mNavigationView = navigationView;
        this.instanceIdAccessor = instanceIdAccessor;

        // Get SharedPreferences for login data
        prefs = context.getSharedPreferences("loginData", MODE_PRIVATE);
    }

    public void openDrawer() {
        mDrawerLayout.openDrawer(GravityCompat.START);
    }

    /**
     * Add listener for events that occur when menu icon is interacted with
     */
    public void addMenuIconListener() {
        mDrawerLayout.addDrawerListener(
                new DrawerLayout.DrawerListener() {
                    @Override
                    public void onDrawerSlide(@NonNull View drawerView, float slideOffset) {
                        // Respond when the drawer's position changes
                    }

                    @Override
                    public void onDrawerOpened(@NonNull View drawerView) {
                        // Respond when the drawer is opened
                        // e.g. make map background darker
                    }

                    @Override
                    public void onDrawerClosed(@NonNull View drawerView) {
                        mNavigationView.setCheckedItem(R.id.blank_option);
                    }

                    @Override
                    public void onDrawerStateChanged(int newState) {
                        // Respond when the drawer motion state changes
                    }
                }
        );
    }

    /**
     * Add listener for the list items in the navigation menu.
     */
    public void addNavigationListener() {
        mNavigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                        // close drawer when item is tapped
                        mDrawerLayout.closeDrawers();

                        int itemId = menuItem.getItemId();
                        switch (itemId) {
                            case R.id.login_signup:
                                startLogIn();
                                break;
                            case R.id.logout:
                                startLogOut();
                                break;
                            case R.id.create_event:
                                startCreateEvent();
                                break;
                            case R.id.user_settings:
                                startUserSettings();
                                break;
                            default:
                                Log.w("navigation", "Invalid Item Selected.");
                        }
                        return true;
                    }
                });
    }
    /**
     * Starts the intent for users to log in or sign up.
     */
    private void startLogIn() {
        Intent intent = new Intent(mContext, LoginActivity.class);
        intent.putExtra("createEvent", false);
        mContext.startActivity(intent);
    }

    /**
     * Starts the intent for users to log out.
     * Only visible if the user is logged in.
     */
    private void startLogOut() {
        instanceIdAccessor.removeInstanceId();
        AuthUI.getInstance()
                .signOut(App.getContext())
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    public void onComplete(@NonNull Task<Void> task) {
                        Log.d("Authentication", "User successfully logged out");
                        String text = "Log-Out successful!";
                        Toast toast = Toast.makeText(mContext, text, Toast.LENGTH_SHORT);
                        final int BOTTOM_SHEET_PEEK_HEIGHT = (int)mContext.getResources().getDimension(R.dimen.bottom_sheet_peek_height);
                        toast.setGravity(Gravity.BOTTOM, 0, BOTTOM_SHEET_PEEK_HEIGHT);
                        toast.show();

                        setLogOutPrefs();
                        setAuthenticationMenuOptions();
                    }
                });
    }

    /**
     * Starts the intent for users to create an event.
     * If the user is logged in, starts CreateEventActivity immediately.
     * If the user is not logged in, starts LoginActivity with the intention
     * to start CreateEventActivity immediately afterwards if the login succeeds.
     */
    private void startCreateEvent() {
        boolean isLoggedIn = prefs.getBoolean("isLoggedIn", false);
        Intent intent;
        if (isLoggedIn) {
            intent = new Intent(mContext, CreateEventActivity.class);
        } else {
            intent = new Intent(mContext, LoginActivity.class);
            intent.putExtra("createEvent", true);
        }
        mContext.startActivity(intent);
    }

    /**
     * Starts the intent for users to change their push notifications settings.
     */
    private void startUserSettings() {
        Intent intent = new Intent(mContext, UserSettingsActivity.class);
        mContext.startActivity(intent);
    }

    /**
     * Checks if user is logged in and displays the corresponding authentication option in menu
     * - User is logged in -> display "Log Out" and "Edit/Delete an Event"
     * - User is not logged in -> display "Log In or Sign Up"
     */
    public void setAuthenticationMenuOptions() {
        boolean isLoggedIn = prefs.getBoolean("isLoggedIn", false);
        final Menu menu = mNavigationView.getMenu();
        menu.findItem(R.id.login_signup).setVisible(!isLoggedIn);
        menu.findItem(R.id.user_settings).setVisible(isLoggedIn);
        menu.findItem(R.id.logout).setVisible(isLoggedIn);
    }

    /**
     * Saves the result of logging out in preferences since we log out
     * without starting LoginActivity.
     */
    @SuppressLint("ApplySharedPref")
    private void setLogOutPrefs() {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean("isLoggedIn", false);
        editor.putString("userId", "");
        editor.commit();
    }
}
