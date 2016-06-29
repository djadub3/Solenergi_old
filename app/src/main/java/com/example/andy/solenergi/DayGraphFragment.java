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

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;

public class DayGraphFragment extends Fragment {

    private View mView;
    // basic variables
    private double voltage;
    private double batVoltage;
    private double current;
    private double power;
    private String time;
    ArrayList<Entry> powerVals;
    ArrayList<Entry> batteryVals;
    ArrayList<String> xLabels;
    int count;
    LineChart lineChart;


    public DayGraphFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        powerVals = new ArrayList<Entry>();
        batteryVals = new ArrayList<Entry>();
        xLabels = new ArrayList<String>();
        count=0;
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
        lineChart = (LineChart) view.findViewById(R.id.chart);

    }

    public void updateGraph(JSONObject inputJson)
    {
        try{
            if(inputJson.getInt("end")==0){
                float power =(float) inputJson.getDouble("power");
                float batteryVoltage = (float) inputJson.getDouble("batVoltage");
                powerVals.add(new Entry(power,count));
                batteryVals.add(new Entry(batteryVoltage,count));
                count++;

                time = inputJson.getString("time");
                int year = Integer.parseInt(time.substring(16,20));
                int month = Integer.parseInt(time.substring(13,15));
                int day = Integer.parseInt(time.substring(10,12));
                int hour = Integer.parseInt(time.substring(0,2));
                int min = Integer.parseInt(time.substring(2,4));
                int sec = Integer.parseInt(time.substring(4,6));
                Calendar cal=new GregorianCalendar(year,month,day,hour,min,sec);
                cal.setTimeZone(TimeZone.getTimeZone("Etc/GMT"));
                SimpleDateFormat sdf = new SimpleDateFormat("h:mm a", Locale.ENGLISH);
                sdf.setTimeZone(TimeZone.getDefault());
                String formattedDate = sdf.format(cal.getTime());
                Log.v("graph",formattedDate);
                xLabels.add(formattedDate);


            }
            else{

                LineDataSet setPower = new LineDataSet(powerVals, "Power");
                setPower.setAxisDependency(YAxis.AxisDependency.LEFT);
                setPower.setColor(Color.RED);
                setPower.setDrawCubic(true);
                setPower.setDrawCircles(false);


                LineDataSet setBattery = new LineDataSet(batteryVals, "Battery Voltage");
                setBattery.setAxisDependency(YAxis.AxisDependency.RIGHT);
                setBattery.setDrawCubic(true);
                setBattery.setDrawCircles(false);

                ArrayList<ILineDataSet> dataSets = new ArrayList<ILineDataSet>();
                dataSets.add(setPower);
                dataSets.add(setBattery);

                LineData data = new LineData(xLabels,dataSets);
                lineChart.setData(data);
                XAxis xAxis = lineChart.getXAxis();
                xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
                lineChart.invalidate();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_day_graph,menu);
        super.onCreateOptionsMenu(menu, inflater);
    }



}
