package com.anurag.therabeat;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.anurag.therabeat.Database.AnxietyUsage;
import com.anurag.therabeat.Database.AppDatabase;
import com.anurag.therabeat.Database.AttentionUsage;
import com.anurag.therabeat.Database.MemoryUsage;
import com.anurag.therabeat.Database.TotalUsage;
import com.anurag.therabeat.Database.TotalUsageDao;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.google.android.material.button.MaterialButton;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.SelectableChannel;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.List;

import uk.co.deanwild.materialshowcaseview.MaterialShowcaseSequence;
import uk.co.deanwild.materialshowcaseview.MaterialShowcaseView;
import uk.co.deanwild.materialshowcaseview.ShowcaseConfig;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AppUsageFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AppUsageFragment extends DialogFragment implements SwipeRefreshLayout.OnRefreshListener, AdapterView.OnItemSelectedListener {

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
    MaterialButton ShareButton;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    boolean FirstTimeAnalytics;

    private static final String SHOWCASE_ID = "FirstTimeAnalytics";


    private SharedPreferences msharedPreferences;


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
        msharedPreferences = SingletonInstances.getInstance(getActivity().getApplicationContext()).getSharedPreferencesInstance();
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

//        appUsageDao = SingletonInstances.getInstance(getActivity().getApplicationContext()).getDbInstance().appUsageDao();
    }

//    @Override
//    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
//        super.onViewCreated(view, savedInstanceState);
//        FirstTimeAnalytics = msharedPreferences.getBoolean("firstTimeAnalytics", true);
//        if(FirstTimeAnalytics){
//            presentShowcaseSequence();
//        }
//    }

    @Override
    public void onResume() {
        super.onResume();
        Window window = getDialog().getWindow();
        if(window == null) return;
        WindowManager.LayoutParams params = window.getAttributes();
        params.width = WindowManager.LayoutParams.WRAP_CONTENT;
        params.height = 1000;
        window.setAttributes(params);
        window.setBackgroundDrawableResource(android.R.color.transparent);
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
        ShareButton = inflatedView.findViewById(R.id.sharebutton);
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

        ShareButton.setOnClickListener(view -> {
            shareImageUri(saveImage(chart.getChartBitmap()));
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
        buildChart(selectedOption);
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
        buildChart(selectedOption);
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

    public void buildChart(String SelectedOption) {
        LineDataSet barDataSet = new LineDataSet(values, "Time Used - "+ SelectedOption);
//        barDataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        barDataSet.setValueTextSize(20.0f);
        barDataSet.setValueFormatter((value, entry, dataSetIndex, viewPortHandler) -> String.valueOf(Math.round(value)));
        barDataSet.setValueTextColor(ContextCompat.getColor(getActivity(), R.color.black));
        barDataSet.setColor(Color.rgb((int) 0, (int) 0, (int) 0));
        LineData barData = new LineData(barDataSet);
        chart.setData(barData);
        Description desc = new Description();
        desc.setText("");
        chart.animateY(3000, Easing.EasingOption.EaseOutBack);
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

        chart.getAxisLeft().setTextColor(ContextCompat.getColor(getActivity(), R.color.black));
        XAxis xAxis = chart.getXAxis();
        xAxis.setTextSize(11);
        xAxis.setTextColor(ContextCompat.getColor(getActivity(), R.color.black));
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setDrawLabels(true);
        xAxis.setValueFormatter(new IndexAxisValueFormatter((Collection<String>) axisLabel));
        xAxis.setGranularity(1.0f);
        xAxis.setGranularityEnabled(true);
        chart.invalidate();
    }

    private void shareImageUri(Uri uri){
        Intent intent = new Intent(android.content.Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_STREAM, uri);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.setType("image/png");
        startActivity(intent);
    }


    private Uri saveImage(Bitmap image) {
        File imagesFolder = new File(getContext().getCacheDir(), "images");
        Uri uri = null;
        try {
            imagesFolder.mkdirs();
            File file = new File(imagesFolder, "shared_image.png");

            FileOutputStream stream = new FileOutputStream(file);
            image.compress(Bitmap.CompressFormat.PNG, 90, stream);
            stream.flush();
            stream.close();
            uri = FileProvider.getUriForFile(getActivity(), "com.anurag.fileprovider", file);

        } catch (IOException e) {
            Log.d("Share error", "IOException while trying to write file for sharing: " + e.getMessage());
        }
        return uri;
    }

//    private void presentShowcaseSequence() {
//
//        ShowcaseConfig config = new ShowcaseConfig();
//        config.setDelay(500); // half second between each showcase view
//
//        MaterialShowcaseSequence sequence = new MaterialShowcaseSequence(getActivity(), SHOWCASE_ID);
//
//        sequence.setConfig(config);
//
//        sequence.addSequenceItem(
//                new MaterialShowcaseView.Builder(getActivity())
//                        .setTarget(spinner)
//                        .setDismissText("GOT IT")
//                        .setContentText("Switch between different cognitive modes and change Binaural Frequency. The default mode is Memory.")
//                        .withOvalShape()
//                        .build()
//        );
//
//        sequence.addSequenceItem(ShareButton, "Search for your favorite songs and add them to your playlist for quicker access.", "GOT IT");
//        sequence.start();
//        msharedPreferences.edit().putBoolean("firstTimeAnalytics",false);
//        msharedPreferences.edit().apply();
//
//    }
}