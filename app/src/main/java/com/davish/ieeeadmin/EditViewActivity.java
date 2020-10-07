package com.davish.ieeeadmin;

import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.annotation.NonNull;
import android.view.MenuItem;

public class EditViewActivity extends AppCompatActivity {
    private ActionBar toolbar;


    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Fragment fragment;
            switch (item.getItemId()) {
                case R.id.navigation_event:
                    toolbar.setTitle("Events");
                    fragment = new FEditEventView();
                    loadFragment(fragment);
                    return true;
                case R.id.navigation_news:
                    toolbar.setTitle("News");
                    fragment = new FEditNewsView();
                    loadFragment(fragment);
                    return true;
                case R.id.navigation_notifications:
                    toolbar.setTitle("Notifications");
                    fragment = new FEditNotView();
                    loadFragment(fragment);
                    return true;
                case R.id.navigation_excom:
                    toolbar.setTitle("Excom");
                    fragment = new FEditExcomView();
                    loadFragment(fragment);
                    return true;
                case R.id.navigation_society:
                    toolbar.setTitle("Society");
                    fragment = new FEditSocietyView();
                    loadFragment(fragment);
                    return true;
            }
            return false;
        }
    };
    private void loadFragment(Fragment fragment) {
        // load fragment
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_container, fragment);
        //transaction.addToBackStack(null);
        transaction.commit();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_view);
        toolbar = getSupportActionBar();
        toolbar.setTitle("Events");
        loadFragment(new FEditEventView());

        BottomNavigationView navigation = findViewById(R.id.nav_view);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

    }

}
