package com.anurag.therabeat;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.anurag.therabeat.Database.AnxietyUsage;
import com.anurag.therabeat.Database.AppDatabase;
import com.anurag.therabeat.Database.AttentionUsage;
import com.anurag.therabeat.Database.MemoryUsage;
import com.anurag.therabeat.Database.TotalUsage;
import com.anurag.therabeat.Database.TotalUsageDao;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
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
public class AppUsageFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener, AdapterView.OnItemSelectedListener {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    TotalUsageDao appUsageDao;
    List<String> axisLabel;
    float barSpace;
    Calendar c;
    LineChart chart;
    String date;
    TextView dateTextView;
    float groupSpace;
    SwipeRefreshLayout mSwipeRefreshLayout;
    SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
    TextView textView;
    Spinner spinner;
    List<Entry> values;
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
        spinner = (Spinner) inflatedView.findViewById(R.id.mode_selector);
// Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(),
                R.array.modes_array, R.layout.spinner_item);
// Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
// Apply the adapter to the spinner
        spinner.setAdapter(adapter);
        chart = inflatedView.findViewById(R.id.chart);
        spinner.setOnItemSelectedListener(this);
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
                refreshGraph(spinner.getSelectedItem().toString());
            }
        });
        return inflatedView;
    }

    private void refreshGraph(String selectedOption) {
        if (selectedOption.equals("Total")) {
            Total();
        } else if (selectedOption.equals("Attention")) {
            Attention();
        } else if (selectedOption.equals("Anxiety")) {
            Anxiety();
        } else {
            Memory();
        }
        buildChart();
        this.mSwipeRefreshLayout.setRefreshing(false);
    }


    @Override
    public void onRefresh() {
        refreshGraph(spinner.getSelectedItem().toString());
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        String selectedOption = adapterView.getItemAtPosition(i).toString();
        if (selectedOption.equals("Total")) {
            Total();
        } else if (selectedOption.equals("Attention")) {
            Attention();
        } else if (selectedOption.equals("Anxiety")) {
            Anxiety();
        } else {
            Memory();
        }
        buildChart();
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    private void Total() {
        this.axisLabel = new ArrayList();
        this.values = new ArrayList();
        Thread thread = new Thread(new Runnable() {

            public void run() {
                List<TotalUsage> list = db.totalUsageDao().getTotalUsage();
                if (list != null) {
                    int n = list.size() - 1;
                    int n2 = 0;
                    while (n >= 0) {
                        axisLabel.add(list.get(n).getDate());
                        int usage = list.get(n).getTimeUsed();
                        Log.d("usage", String.valueOf(usage));
//                        if (usage >= 3600) {
                        usage *= 0.000277778;
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
        } catch (InterruptedException interruptedException) {
            interruptedException.printStackTrace();
        }
    }

    private void Attention() {
        this.axisLabel = new ArrayList();
        this.values = new ArrayList();
        Thread thread = new Thread(new Runnable() {

            public void run() {
                List<AttentionUsage> list = db.attentionUsageDao().getAttentionUsage();
                if (list != null) {
                    int n = list.size() - 1;
                    int n2 = 0;
                    while (n >= 0) {
                        axisLabel.add(list.get(n).getDate());
                        int usage = list.get(n).getTimeUsed().intValue();
                        Log.d("usage", String.valueOf(usage));
//                        if (usage >= 3600) {
                        usage *= 0.000277778;
                        usage = (int) usage;
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
        } catch (InterruptedException interruptedException) {
            interruptedException.printStackTrace();
        }
    }

    private void Anxiety() {
        this.axisLabel = new ArrayList();
        this.values = new ArrayList();
        Thread thread = new Thread(new Runnable() {

            public void run() {
                List<AnxietyUsage> list = db.anxietyUsageDao().getAnxietyUsage();
                if (list != null) {
                    int n = list.size() - 1;
                    int n2 = 0;
                    while (n >= 0) {
                        axisLabel.add(list.get(n).getDate());
                        int usage = list.get(n).getTimeUsed().intValue();
                        Log.d("usage", String.valueOf(usage));
//                        if (usage >= 3600) {
                        usage *= 0.000277778;
                        usage = (int) usage;
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
        } catch (InterruptedException interruptedException) {
            interruptedException.printStackTrace();
        }
    }

    public void Memory() {
        axisLabel = new ArrayList();
        values = new ArrayList();
        Thread thread = new Thread(() -> {
            List<MemoryUsage> list = db.memoryUsageDao().getMemoryUsage();
            if (list != null) {
                int n = list.size() - 1;
                int n2 = 0;
                while (n >= 0) {
                    axisLabel.add(list.get(n).getDate());
                    int usage = list.get(n).getTimeUsed();
                    Log.d("usage", String.valueOf(usage));
//                        if (usage >= 3600) {
                    usage *= 0.000277778;
//                        } else if (usage >= 60 && usage < 3600) {
//                            usage *= (long) 0.0166667;
//                        }
                    AppUsageFragment.this.values.add(new BarEntry(n2, usage));
                    n--;
                    n2++;
                }
            }
        });
        thread.start();
        try {
            thread.join();
        } catch (InterruptedException interruptedException) {
            interruptedException.printStackTrace();
        }
    }

    public void buildChart() {
        LineDataSet barDataSet = new LineDataSet(values, "Time Used");
        barDataSet.setValueTextSize(20.0f);
        barDataSet.setValueFormatter((value, entry, dataSetIndex, viewPortHandler) -> String.valueOf(Math.round(value)));
        barDataSet.setValueTextColor(ContextCompat.getColor(getActivity(), R.color.white));
        barDataSet.setColor(Color.rgb((int) 255, (int) 255, (int) 255));
        LineData barData = new LineData(barDataSet);
        chart.setData(barData);
        Description desc = new Description();
        desc.setText("");
        chart.setScaleEnabled(false);
        chart.setDescription(desc);
        chart.animateXY(2000, 2000);
        chart.getAxisLeft().setDrawGridLines(false);
        chart.getAxisRight().setDrawGridLines(false);
        chart.getAxisLeft().setTextSize(15);
        chart.getAxisRight().setEnabled(false);
        chart.getAxisLeft().setEnabled(false);
        chart.getAxisLeft().setGranularity(1.0f);
        chart.getAxisLeft().setGranularityEnabled(true);

        chart.getAxisLeft().setTextColor(ContextCompat.getColor(getActivity(), R.color.white));
        XAxis xAxis = chart.getXAxis();
        xAxis.setTextSize(15);
        xAxis.setTextColor(ContextCompat.getColor(getActivity(), R.color.white));
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setDrawLabels(true);
        xAxis.setValueFormatter(new IndexAxisValueFormatter((Collection<String>) axisLabel));
        xAxis.setGranularity(1.0f);
        xAxis.setGranularityEnabled(true);
        chart.invalidate();
    }
}