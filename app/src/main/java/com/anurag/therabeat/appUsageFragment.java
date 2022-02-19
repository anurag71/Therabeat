package com.anurag.therabeat;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link appUsageFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class appUsageFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
    Calendar c = Calendar.getInstance();
    String date = sdf.format(c.getTime());
    SwipeRefreshLayout mSwipeRefreshLayout;
    TextView textView;
    TextView dateTextView;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;


    public appUsageFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment appUsageFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static appUsageFragment newInstance(String param1, String param2) {
        appUsageFragment fragment = new appUsageFragment();
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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View inflatedView = inflater.inflate(R.layout.fragment_app_usage, container, false);
        textView = inflatedView.findViewById(R.id.appUsage);
        dateTextView = inflatedView.findViewById(R.id.date);
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
        dateTextView.setText(date);
        textView.setText(Long.toString(getActivity().getSharedPreferences("Therabeat", 0).getLong(date, (long) 0.0)));
        mSwipeRefreshLayout.setRefreshing(false);
    }


    @Override
    public void onRefresh() {
        updateTextViews();
    }
}