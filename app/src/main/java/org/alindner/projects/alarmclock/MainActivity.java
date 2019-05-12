package org.alindner.projects.alarmclock;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;

import org.alindner.projects.alarmclock.fragments.Overview;
import org.alindner.projects.alarmclock.fragments.Settings;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_main);

        final TabLayout tabs = this.findViewById(R.id.tabs);
        tabs.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(final TabLayout.Tab tab) {
                final Fragment newFragment;

                final String s = String.valueOf(tab.getText());
                if (MainActivity.this.getString(R.string.settings).equals(s)) {
                    newFragment = new Settings();
                } else {
                    newFragment = new Overview();
                }
                MainActivity.this.changeTabFragment(newFragment);
            }

            @Override
            public void onTabUnselected(final TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(final TabLayout.Tab tab) {

            }
        });
        final FloatingActionButton button = this.findViewById(R.id.add_button);
        button.setOnClickListener(v -> {
            final Intent intent = new Intent(this, AddActivity.class);
            this.startActivity(intent);
        });
        this.changeTabFragment(new Overview());
    }

    private void changeTabFragment(final Fragment fragment) {
        final FragmentTransaction transaction = this.getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    @Override
    public void onActivityResult(final int requestCode, final int resultCode, @Nullable final Intent data) {
        this.changeTabFragment(new Overview());
    }
}