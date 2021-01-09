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
import android.widget.SeekBar;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.material.textfield.TextInputEditText;

import org.alindner.projects.alarmclock.R;
import org.alindner.projects.alarmclock.RestInterface;
import org.alindner.projects.alarmclock.models.SettingsModel;

import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Settings extends Fragment {

    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.tab_settings, container, false);
        final SharedPreferences sharedPref = Objects.requireNonNull(this.getActivity()).getPreferences(Context.MODE_PRIVATE);
        final com.google.android.material.textfield.TextInputEditText url = view.findViewById(R.id.settingsUrl);
        final SharedPreferences.Editor editor = sharedPref.edit();
        url.setTextColor(Color.BLACK);
        url.setText(Objects.requireNonNull(sharedPref.getString(String.valueOf(R.string.serverUrl), "http://192.168.0.18")));
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

        final RestInterface retrofit = new Retrofit.Builder()
                .baseUrl(Objects.requireNonNull(sharedPref.getString(String.valueOf(R.string.serverUrl), "http://192.168.178.43")))
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(RestInterface.class);

        final SeekBar intensity = view.findViewById(R.id.intensity);
        final TextInputEditText streamUrl = view.findViewById(R.id.settingsStreamUrl);
        streamUrl.setTextColor(Color.BLACK);

        retrofit.getSettings().enqueue(new Callback<SettingsModel>() {
            @Override
            public void onResponse(final Call<SettingsModel> call, final Response<SettingsModel> response) {
                intensity.setProgress(Objects.requireNonNull(response.body()).getIntensity());
                streamUrl.setText(Objects.requireNonNull(response.body()).getStreamurl());
            }

            @Override
            public void onFailure(final Call<SettingsModel> call, final Throwable t) {
                System.out.println("ERROR");
                System.out.println(call.request());
                System.out.println(t.toString());
            }
        });

        intensity.setMin(0);
        intensity.setMax(10);
        intensity.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(final SeekBar seekBar, final int progress, final boolean fromUser) {
                retrofit.setIntensity(progress).enqueue(new Callback<Object>() {
                    @Override
                    public void onResponse(final Call<Object> call, final Response<Object> response) {

                    }

                    @Override
                    public void onFailure(final Call<Object> call, final Throwable t) {
                        System.out.println("ERROR");
                        System.out.println(call.request());
                    }
                });
            }

            @Override
            public void onStartTrackingTouch(final SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(final SeekBar seekBar) {

            }
        });

        streamUrl.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(final CharSequence s, final int start, final int count, final int after) {

            }

            @Override
            public void onTextChanged(final CharSequence s, final int start, final int before, final int count) {
                retrofit.setUrl(Objects.requireNonNull(streamUrl.getText()).toString()).enqueue(new Callback<Object>() {
                    @Override
                    public void onResponse(final Call<Object> call, final Response<Object> response) {

                    }

                    @Override
                    public void onFailure(final Call<Object> call, final Throwable t) {
                        System.out.println("ERROR");
                        System.out.println(call.request());
                    }
                });
            }

            @Override
            public void afterTextChanged(final Editable s) {

            }
        });
        return view;
    }
}
