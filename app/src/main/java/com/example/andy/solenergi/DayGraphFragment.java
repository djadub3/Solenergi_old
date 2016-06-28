package com.example.andy.solenergi;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
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

    private View mView;
    // variables
    private double voltage;
    private double batVoltage;
    private double current;
    private double power;
    private String time;

    private ArrayList<DataPoint> powerDataPointArrayList;
    private ArrayList<DataPoint> batteryDataPointArrayList;

    // graphing variables

    private LineGraphSeries<DataPoint> series;



    public DayGraphFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        powerDataPointArrayList = new ArrayList<DataPoint>();
        batteryDataPointArrayList = new ArrayList<DataPoint>();
        setHasOptionsMenu(true);

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
        try {
            if(inputJson.getInt("end")==0) {
                //Log.v("load graph","data read");
                time = inputJson.getString("time");
                power = inputJson.getDouble("power");
                batVoltage = inputJson.getDouble("batVoltage");


                int year = Integer.parseInt(time.substring(16,20));
                int month = Integer.parseInt(time.substring(13,15));
                int day = Integer.parseInt(time.substring(10,12));
                int hour = Integer.parseInt(time.substring(0,2));
                int min = Integer.parseInt(time.substring(2,4));
                int sec = Integer.parseInt(time.substring(4,6));

                Calendar cal=new GregorianCalendar(year,month,day,hour,min,sec);
                cal.setTimeZone(TimeZone.getTimeZone("Etc/GMT"));

                powerDataPointArrayList.add(new DataPoint(cal.getTime(), power));
                batteryDataPointArrayList.add(new DataPoint(cal.getTime(), batVoltage));

            }

            if(inputJson.getInt("end")==1){

                //Log.v("load graph","end of data read");
                GraphView graph = (GraphView) mView.findViewById(R.id.day_graph);
                DataPoint[] powerDataPointArray = powerDataPointArrayList.toArray(new DataPoint[powerDataPointArrayList.size()]);
                DataPoint[] batteryDataPointArray = batteryDataPointArrayList.toArray(new DataPoint[batteryDataPointArrayList.size()]);

                LineGraphSeries<DataPoint> powerSeries = new LineGraphSeries<DataPoint>(powerDataPointArray);
                LineGraphSeries<DataPoint> batterySeries = new LineGraphSeries<DataPoint>(batteryDataPointArray);

                graph.addSeries(powerSeries);
                graph.addSeries(batterySeries);

                powerSeries.setColor(Color.argb(100, 100, 0, 0));
                batterySeries.setColor(Color.argb(100,0,100,0));


                powerSeries.setBackgroundColor(Color.argb(50, 100, 0, 0));
                batterySeries.setBackgroundColor(Color.argb(50, 0, 100, 0));
                powerSeries.setDrawBackground(true);
                batterySeries.setDrawBackground(true);
                graph.getGridLabelRenderer().setPadding(30);
                graph.getViewport().setScalable(true);
                graph.getGridLabelRenderer().reloadStyles();



                graph.getGridLabelRenderer().setLabelFormatter(new DefaultLabelFormatter() {
                    @Override
                    public String formatLabel(double value, boolean isValueX) {
                        if (isValueX) {
                            SimpleDateFormat sdf = new SimpleDateFormat("h:mm a", Locale.ENGLISH);
                            sdf.setTimeZone(TimeZone.getDefault());
                            String formattedDate = sdf.format(value);
                            return formattedDate;
                        }
                        return ""+(int) value;
                    }
                });
                powerDataPointArrayList = new ArrayList<DataPoint>();
            }

        }catch(Exception e){
            Log.v("DAY_VIEW","graph not updated");
        }
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_day_graph,menu);
        super.onCreateOptionsMenu(menu, inflater);
    }



}
