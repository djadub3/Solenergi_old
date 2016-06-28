package com.example.andy.solenergi;

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
                cal.setTimeZone(TimeZone.getTimeZone("Etc/GMT"));
                Log.v("Time", String.valueOf(cal.getTimeZone()));
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
                            sdf.setTimeZone(TimeZone.getDefault());
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

}
