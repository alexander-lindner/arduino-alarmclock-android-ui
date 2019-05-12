package org.alindner.projects.alarmclock.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.URLUtil;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import org.alindner.projects.alarmclock.R;

import java.util.Objects;

public class Settings extends Fragment {

    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.tab_settings, container, false);
        final SharedPreferences sharedPref = Objects.requireNonNull(this.getActivity()).getPreferences(Context.MODE_PRIVATE);
        final com.google.android.material.textfield.TextInputEditText url = view.findViewById(R.id.settingsUrl);
        final SharedPreferences.Editor editor = sharedPref.edit();
        url.setTextColor(Color.BLACK);
        url.setText(Objects.requireNonNull(sharedPref.getString(String.valueOf(R.string.serverUrl), "http://192.168.178.43")));
        url.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(final CharSequence c, final int start, final int before, final int count) {
                if (URLUtil.isHttpUrl(c.toString()) || URLUtil.isHttpsUrl(c.toString())) {
                    editor.putString(String.valueOf(R.string.serverUrl), c.toString());
                    editor.apply();
                }
            }

            @Override
            public void beforeTextChanged(final CharSequence c, final int start, final int count, final int after) {
            }

            @Override
            public void afterTextChanged(final Editable c) {
            }
        });

        editor.putString(this.getString(R.string.serverUrl), "");
        editor.apply();

        return view;
    }
}
