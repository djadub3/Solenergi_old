package com.example.andy.solenergi;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jjoe64.graphview.DefaultLabelFormatter;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link DayGraphFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link DayGraphFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DayGraphFragment extends Fragment {


    View mView;
    // variables
    private double voltage;
    private double batVoltage;
    private double current;
    private double power;
    private String time;

    private ArrayList<DataPoint> dataPointArrayList;

    // graphing variables
    private double graphLastXValue = 0d;
    private LineGraphSeries<DataPoint> series;
    private double graph2LastXValue = 0d;
    private double graph3LastXValue = 0d;
    private LineGraphSeries<DataPoint> series2;
    private LineGraphSeries<DataPoint> series3;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment DayGraphFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static DayGraphFragment newInstance(String param1, String param2) {
        DayGraphFragment fragment = new DayGraphFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public DayGraphFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dataPointArrayList = new ArrayList<DataPoint>();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        return inflater.inflate(R.layout.fragment_day_graph, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

        super.onViewCreated(view, savedInstanceState);
        mView =view;
        /*
        GraphView graph = (GraphView) view.findViewById(R.id.day_graph);
        series = new LineGraphSeries<DataPoint>();
        graph.addSeries(series);
        graph.getGridLabelRenderer().setGridColor(Color.GRAY);
        series2 = new LineGraphSeries<DataPoint>();
        graph.addSeries(series2);
        series3 = new LineGraphSeries<DataPoint>();
        graph.getSecondScale().addSeries(series3);

        graph.getGridLabelRenderer().setVerticalLabelsSecondScaleColor(Color.GRAY);
        graph.getGridLabelRenderer().setVerticalLabelsColor(Color.GRAY);

        graph.getSecondScale().setMinY(0);
        graph.getSecondScale().setMaxY(30);

        series2.setColor(Color.YELLOW);
        series.setColor(Color.MAGENTA);
        series3.setColor(Color.CYAN);

        //graph.getViewport().setXAxisBoundsManual(true);
        graph.getViewport().setYAxisBoundsManual(true);
        //graph.getViewport().setMinX(0);
        //graph.getViewport().setMaxX(86400);
        graph.getViewport().setMinY(0);
        graph.getViewport().setMaxY(15);
        //graph.getViewport().setScrollable(true);
        graph.getGridLabelRenderer().setPadding(30);
        graph.getGridLabelRenderer().setHorizontalLabelsVisible(false);
        graph.getGridLabelRenderer().setNumVerticalLabels(16);
        graph.getGridLabelRenderer().setVerticalAxisTitle("Volts/Amps");
        //graph.getGridLabelRenderer().set
        graph.getGridLabelRenderer().setVerticalAxisTitleColor(Color.GRAY);
        graph.getGridLabelRenderer().setVerticalLabelsSecondScaleColor(Color.GRAY);
        graph.getGridLabelRenderer().reloadStyles();*/
    }

    public void updateGraph(JSONObject inputJson)
    {
        //dataPointArrayList = new ArrayList<DataPoint>();
        try {
            if(inputJson.getInt("end")==0) {
                //Log.v("load graph","data read");
                time = inputJson.getString("time");
                voltage = Double.parseDouble(inputJson.getString("involtage"));
                batVoltage = Double.parseDouble(inputJson.getString("batVoltage"));
                current = Math.abs(Double.parseDouble(inputJson.getString("current")));
                int year = Integer.parseInt(time.substring(16,20));
                int month = Integer.parseInt(time.substring(13,15));
                int day = Integer.parseInt(time.substring(10,12));
                int hour = Integer.parseInt(time.substring(0,2));
                int min = Integer.parseInt(time.substring(2,4));
                int sec = Integer.parseInt(time.substring(4,6));

                Calendar cal=new GregorianCalendar(year,month,day,hour,min,sec);
                dataPointArrayList.add(new DataPoint(cal.getTime(), voltage));


            }

            if(inputJson.getInt("end")==1){

                //Log.v("load graph","end of data read");
                GraphView graph = (GraphView) mView.findViewById(R.id.day_graph);
                DataPoint[] dataPointArray = dataPointArrayList.toArray(new DataPoint[dataPointArrayList.size()]);

                graph.addSeries(series = new LineGraphSeries<DataPoint>(dataPointArray));
                graph.getGridLabelRenderer().setLabelFormatter(new DefaultLabelFormatter() {
                    @Override
                    public String formatLabel(double value, boolean isValueX) {
                        // TODO Auto-generated method stub
                        if (isValueX) {
                            SimpleDateFormat sdf = new SimpleDateFormat("h:mm a", Locale.ENGLISH);
                            sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
                            String formattedDate = sdf.format(value);
                            return formattedDate;
                        }
                        return ""+(int) value;
                    }
                });
                dataPointArrayList = new ArrayList<DataPoint>();


            }

        }catch(Exception e){
            Log.v("DAY_VIEW","graph not updated");
        }
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }

}
