package com.anurag.therabeat;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.anurag.therabeat.Database.AppDatabase;
import com.anurag.therabeat.Database.Person;
import com.anurag.therabeat.Database.PersonDao;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AppUsageFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AppUsageFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    PersonDao appUsageDao;
    List<String> axisLabel;
    float barSpace;
    Calendar c;
    BarChart chart;
    String date;
    TextView dateTextView;
    float groupSpace;
    SwipeRefreshLayout mSwipeRefreshLayout;
    SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
    TextView textView;
    List<BarEntry> values;
    AppDatabase db;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;


    public AppUsageFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AppUsageFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AppUsageFragment newInstance(String param1, String param2) {
        AppUsageFragment fragment = new AppUsageFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
//        appUsageDao = SingletonInstances.getInstance(getActivity().getApplicationContext()).getDbInstance().appUsageDao();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        db = AppDatabase.getInstance(this.getActivity().getApplicationContext());
        View inflatedView = inflater.inflate(R.layout.fragment_app_usage, container, false);
        chart = inflatedView.findViewById(R.id.chart);
        mSwipeRefreshLayout = (SwipeRefreshLayout) inflatedView.findViewById(R.id.appUsageSwipeContainer);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.design_default_color_primary,
                android.R.color.holo_green_dark,
                android.R.color.holo_orange_dark,
                android.R.color.holo_blue_dark);
        mSwipeRefreshLayout.post(new Runnable() {

            @Override
            public void run() {

                mSwipeRefreshLayout.setRefreshing(true);

                // Fetching data from server
                updateTextViews();
            }
        });
        return inflatedView;
    }

    private void updateTextViews() {
        this.axisLabel = new ArrayList();
        this.values = new ArrayList();
        Thread thread = new Thread(new Runnable() {

            public void run() {
                List<Person> list = db.personDao().loadAllPersons();
                if (list != null) {
                    int n = list.size() - 1;
                    int n2 = 0;
                    while (n >= 0) {
                        if (n == 6) {
                            return;
                        }
                        Log.d((String) "error", (String) String.valueOf((Object) ((Person) list.get(n)).getTimeUsed()));
                        AppUsageFragment.this.axisLabel.add(list.get(n).getDate());
                        long usage = list.get(n).getTimeUsed().intValue();
//                        if (usage >= 3600) {
                        usage *= (long) 0.000277778;
//                        } else if (usage >= 60 && usage < 3600) {
//                            usage *= (long) 0.0166667;
//                        }
                        AppUsageFragment.this.values.add(new BarEntry(n2, usage));
                        n--;
                        n2++;
                    }
                }
            }
        });
        thread.start();
        try {
            thread.join();
            BarDataSet barDataSet = new BarDataSet(this.values, "Time Used");
            barDataSet.setValueTextSize(20.0f);
            barDataSet.setValueTextColor(ContextCompat.getColor(getActivity(), R.color.white));
            barDataSet.setColor(Color.rgb((int) 104, (int) 241, (int) 175));
            BarData barData = new BarData(barDataSet);
            this.chart.setData(barData);

            this.chart.animateXY(2000, 2000);
            this.chart.getAxisLeft().setDrawGridLines(false);
            this.chart.getAxisRight().setDrawGridLines(false);
            this.chart.getAxisLeft().setTextSize(15);
            this.chart.getAxisRight().setEnabled(false);
            this.chart.getAxisLeft().setGranularity(1.0f);
            this.chart.getAxisLeft().setGranularityEnabled(true);

            this.chart.getAxisLeft().setTextColor(ContextCompat.getColor(getActivity(), R.color.white));
            XAxis xAxis = this.chart.getXAxis();
            xAxis.setTextSize(20.0f);
            xAxis.setTextColor(ContextCompat.getColor(getActivity(), R.color.white));
            xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
            xAxis.setDrawGridLines(false);
            xAxis.setDrawLabels(true);
            xAxis.setValueFormatter(new IndexAxisValueFormatter((Collection<String>) this.axisLabel));
            xAxis.setGranularity(1.0f);
            xAxis.setGranularityEnabled(true);
            this.chart.invalidate();
        } catch (InterruptedException interruptedException) {
            interruptedException.printStackTrace();
        }
        this.mSwipeRefreshLayout.setRefreshing(false);
        Log.d((String) "check", (String) "check");
    }


    @Override
    public void onRefresh() {
        updateTextViews();
    }
}