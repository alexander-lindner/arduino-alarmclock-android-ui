package org.alindner.projects.alarmclock;

import android.annotation.SuppressLint;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Objects;
import java.util.stream.IntStream;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class AddActivity extends AppCompatActivity {
    @SuppressLint("DefaultLocale")
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_add);
        final SharedPreferences sharedPref = this.getPreferences(Context.MODE_PRIVATE);

        final Button timePicker = this.findViewById(R.id.timePicker);
        final Calendar mCurrentTime = Calendar.getInstance();
        final int currentHour = mCurrentTime.get(Calendar.HOUR_OF_DAY);
        final int currentMinute = mCurrentTime.get(Calendar.MINUTE);

        timePicker.setText(String.format("%d:%d", currentHour, currentMinute));
        timePicker.setOnClickListener(v -> {
            final TimePickerDialog mTimePicker = new TimePickerDialog(
                    AddActivity.this,
                    (timePicker1, selectedHour, selectedMinute) -> timePicker.setText(String.format("%d:%d", selectedHour, selectedMinute)),
                    currentHour,
                    currentMinute,
                    true
            );
            mTimePicker.setTitle("Select Time");
            mTimePicker.show();
        });
        final String url = Objects.requireNonNull(sharedPref.getString(String.valueOf(R.string.serverUrl), "http://192.168.0.18"));
        final RestInterface retrofit = new Retrofit.Builder()
                .baseUrl(url)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(RestInterface.class);

        final MaterialButton sendButton = this.findViewById(R.id.send);
        sendButton.setOnClickListener(v -> {
            final TextInputEditText name = this.findViewById(R.id.name);
            final String[] time = String.valueOf(timePicker.getText()).split(":");
            final int hour = Integer.parseInt(time[0]);
            final int minute = Integer.parseInt(time[1]);
            final LocalDateTime localDateTime = LocalDateTime.now();

            final HashMap<DayOfWeek, Boolean> queue = new HashMap<>();
            queue.put(DayOfWeek.MONDAY, this.getCheckBox(R.id.monday));
            queue.put(DayOfWeek.TUESDAY, this.getCheckBox(R.id.tuesday));
            queue.put(DayOfWeek.WEDNESDAY, this.getCheckBox(R.id.wednesday));
            queue.put(DayOfWeek.THURSDAY, this.getCheckBox(R.id.thursday));
            queue.put(DayOfWeek.FRIDAY, this.getCheckBox(R.id.friday));
            queue.put(DayOfWeek.SATURDAY, this.getCheckBox(R.id.saturday));
            queue.put(DayOfWeek.SUNDAY, this.getCheckBox(R.id.sunday));

            final DayOfWeek dayOfWeek = localDateTime.getDayOfWeek();
            final DayOfWeek[] current = new DayOfWeek[1];
            IntStream.range(0, 7).forEach(i -> {
                if (queue.get(dayOfWeek.plus(i))) {
                    current[0] = dayOfWeek.plus(i);
                }
            });

            retrofit.add(
                    String.valueOf(name.getText()),
                    localDateTime.withHour(hour).withMinute(minute).with(TemporalAdjusters.nextOrSame(current[0])).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                    this.getCheckBox(R.id.monday),
                    this.getCheckBox(R.id.tuesday),
                    this.getCheckBox(R.id.wednesday),
                    this.getCheckBox(R.id.thursday),
                    this.getCheckBox(R.id.friday),
                    this.getCheckBox(R.id.saturday),
                    this.getCheckBox(R.id.sunday),
                    this.getCheckBox(R.id.specialDays)
            ).enqueue(new Callback<Object>() {
                @Override
                public void onResponse(@NonNull final Call<Object> call, @NonNull final Response<Object> response) {
                    final Intent intent = new Intent(AddActivity.this, MainActivity.class);
                    AddActivity.this.startActivity(intent);
                }

                @Override
                public void onFailure(@NonNull final Call<Object> call, @NonNull final Throwable t) {
                    System.out.println("ERROR");
                    System.out.println(call.request());
                }
            });
        });
    }

    private boolean getCheckBox(final int id) {
        final CheckBox checkBox = this.findViewById(id);
        return checkBox.isChecked();
    }
}
