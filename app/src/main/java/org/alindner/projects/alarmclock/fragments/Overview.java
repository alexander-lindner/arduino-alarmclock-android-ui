package org.alindner.projects.alarmclock.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;

import org.alindner.projects.alarmclock.R;
import org.alindner.projects.alarmclock.RestInterface;
import org.alindner.projects.alarmclock.models.ResultModel;
import org.alindner.projects.alarmclock.models.TimeModel;
import org.alindner.projects.alarmclock.models.Weekdays;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

interface onAfterDelete {
    void callback(Long id);
}

public class Overview extends Fragment {
    private final List<TimeModel> myDataset = new ArrayList<>();
    private RestInterface retrofit;
    private ListAdapter mAdapter;

    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.tab_overview, container, false);
        final SharedPreferences sharedPref = Objects.requireNonNull(this.getActivity()).getPreferences(Context.MODE_PRIVATE);

        final RecyclerView recyclerView = view.findViewById(R.id.my_recycler_view);
        recyclerView.setHasFixedSize(true);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(this.getActivity());
        recyclerView.setLayoutManager(layoutManager);

        final String url = Objects.requireNonNull(sharedPref.getString(String.valueOf(R.string.serverUrl), "http://192.168.178.43"));
        this.retrofit = new Retrofit.Builder()
                .baseUrl(url)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(RestInterface.class);
        this.load();

        this.mAdapter = new ListAdapter(this.myDataset, (id) -> this.retrofit.delete(id).enqueue(new Callback<Object>() {
            @Override
            public void onResponse(@NonNull final Call<Object> call, @NonNull final Response<Object> response) {
                Overview.this.load();
            }

            @Override
            public void onFailure(@NonNull final Call<Object> call, @NonNull final Throwable t) {
                Overview.this.load();
            }
        }), this.getActivity().getApplicationContext());
        recyclerView.setAdapter(this.mAdapter);
        recyclerView.setNestedScrollingEnabled(false);
        return view;
    }

    private void load() {
        this.retrofit.getList().enqueue(new Callback<ResultModel>() {
            @Override
            public void onResponse(@NonNull final Call<ResultModel> call, @NonNull final Response<ResultModel> response) {
                if (response.body() != null) {
                    final int size = Overview.this.myDataset.size();
                    Overview.this.myDataset.clear();
                    Overview.this.mAdapter.notifyItemRangeRemoved(0, size);
                    Overview.this.myDataset.addAll(response.body().getTimes());
                    Overview.this.mAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(@NonNull final Call<ResultModel> call, @NonNull final Throwable t) {
                System.out.println(Arrays.toString(t.getStackTrace()));
            }
        });
    }

    @Override
    public void onActivityResult(final int requestCode, final int resultCode, @Nullable final Intent data) {
        System.out.println("Reload");
        this.load();
    }
}

class ListAdapter extends RecyclerView.Adapter<ListAdapter.InlineItem> {
    private final onAfterDelete deleteCallback;
    private final Context context;
    private final List<TimeModel> mDataset;
    private LinearLayout view;

    ListAdapter(final List<TimeModel> list, final onAfterDelete deleteCallback, final Context context) {
        this.mDataset = list;
        this.deleteCallback = deleteCallback;
        this.context = context;
    }

    @NonNull
    @Override
    public InlineItem onCreateViewHolder(@NonNull final ViewGroup parent, final int viewType) {
        final LinearLayout v = (LinearLayout) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_view_item, parent, false);
        final float h = 240;
        final float s = (float) (Math.random() * 100) / 100;
        final float l = (float) (Math.random() * 100) / 100;
        final float[] colors = {h, s, l};
        this.view = v;
        v.setBackgroundColor(Color.HSVToColor(colors));
        return new InlineItem(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final InlineItem holder, final int position) {
        final TextView textView = this.view.findViewById(R.id.list_view_item_name);
        textView.setText(this.mDataset.get(position).getName());
        final TextView datum = this.view.findViewById(R.id.list_view_item_datum);
        datum.setText(DateUtils.formatDateTime(
                this.context,
                this.mDataset.get(position).getTime() * 1000, DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE
        ));

        final TextView days = this.view.findViewById(R.id.list_view_item_days);
        final Weekdays weekdays = this.mDataset.get(position).getWeekdays();
        final String daysText;
        if (weekdays.isMonday() && weekdays.isTuesday() && weekdays.isWednesday() && weekdays.isThursday() && weekdays.isFriday() && weekdays.isSaturday() && weekdays.isSunday()) {
            daysText = this.context.getString(R.string.each_day);
        } else if (weekdays.isMonday() && weekdays.isTuesday() && weekdays.isWednesday() && weekdays.isThursday() && weekdays.isFriday() && !weekdays.isSaturday() && !weekdays.isSunday()) {
            daysText = this.context.getString(R.string.during_week);
        } else if (!weekdays.isMonday() && !weekdays.isTuesday() && !weekdays.isWednesday() && !weekdays.isThursday() && !weekdays.isFriday() && weekdays.isSaturday() && weekdays.isSunday()) {
            daysText = this.context.getString(R.string.weekend);
        } else {
            final List<String> dayList = new ArrayList<>();
            if (weekdays.isMonday()) {
                dayList.add("Montags");
            }
            if (weekdays.isTuesday()) {
                dayList.add("Dienstags");
            }
            if (weekdays.isWednesday()) {
                dayList.add("Mittwochs");
            }
            if (weekdays.isThursday()) {
                dayList.add("Donnerstags");
            }
            if (weekdays.isFriday()) {
                dayList.add("Freitags");
            }
            if (weekdays.isSaturday()) {
                dayList.add("Samstags");
            }
            if (weekdays.isSunday()) {
                dayList.add("Sonntags");
            }
            daysText = String.join(", ", dayList);

        }
        days.setText(daysText);

        final MaterialButton button = this.view.findViewById(R.id.list_view_item_button);
        button.setOnClickListener(v -> this.deleteCallback.callback(this.mDataset.get(position).getId()));
    }

    @Override
    public int getItemCount() {
        return this.mDataset.size();
    }

    static class InlineItem extends RecyclerView.ViewHolder {
        LinearLayout layout;

        InlineItem(final LinearLayout v) {
            super(v);
            this.layout = v;
        }
    }
}