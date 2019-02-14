package stanford.cs194.stanfood;

import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.MenuItem;
import android.view.View;


public class NavigationDrawer {

    private DrawerLayout mDrawerLayout;


    public NavigationDrawer(DrawerLayout drawerLayout) {
        mDrawerLayout = drawerLayout;
    }

    void openDrawer() {
        mDrawerLayout.openDrawer(GravityCompat.START);
    }

    void closeDrawer() {
        mDrawerLayout.closeDrawers();
    }

    /**
     * Add listener for events that occur when menu icon is interacted with
     */
    void addMenuIconListener() {
        mDrawerLayout.addDrawerListener(
                new DrawerLayout.DrawerListener() {
                    @Override
                    public void onDrawerSlide(View drawerView, float slideOffset) {
                        // Respond when the drawer's position changes
                    }

                    @Override
                    public void onDrawerOpened(View drawerView) {
                        // Respond when the drawer is opened
                        // e.g. make map background darker
                    }

                    @Override
                    public void onDrawerClosed(View drawerView) {
                        final NavigationView navigationView = drawerView.findViewById(R.id.nav_view);
                        navigationView.setCheckedItem(R.id.blank_option);
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
    void addNavigationListener(final Runnable loginSignup, final Runnable logOut, final Runnable createEvent, final NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        // close drawer when item is tapped
                        closeDrawer();

                        int itemId = menuItem.getItemId();
                        switch (itemId) {
                            case R.id.login_signup:
                                loginSignup.run();
                                break;
                            case R.id.logout:
                                logOut.run();
                                break;
                            case R.id.create_event:
                                createEvent.run();
                                break;
                        }

                        return true;
                    }
                });
    }
}
