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
import com.github.mikephil.charting.formatter.YAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
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
        lineChart.setDescription("");

        XAxis xAxis = lineChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);

        YAxis leftYAxis = lineChart.getAxisLeft();
        leftYAxis.setValueFormatter(new WattageYAxisValueFormatter());
        leftYAxis.setLabelCount(6, true);
        leftYAxis.setAxisMinValue(0);
        leftYAxis.setAxisMaxValue(5);

        YAxis rightYAxis = lineChart.getAxisRight();
        rightYAxis.setValueFormatter(new VoltageYAxisValueFormatter());
        rightYAxis.setLabelCount(6,true);
        rightYAxis.setAxisMinValue(0);
        rightYAxis.setAxisMaxValue(5);

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

                LineDataSet powerSet = new LineDataSet(powerVals, "Power");
                powerSet.setAxisDependency(YAxis.AxisDependency.LEFT);
                powerSet.setColor(Color.RED);
                powerSet.setDrawCubic(true);
                powerSet.setDrawCircles(false);
                powerSet.setLineWidth(3);


                LineDataSet batterySet = new LineDataSet(batteryVals, "Battery Voltage");
                batterySet.setAxisDependency(YAxis.AxisDependency.RIGHT);
                batterySet.setDrawCubic(true);
                batterySet.setDrawCircles(false);
                batterySet.setLineWidth(3);
                batterySet.setDrawFilled(true);
                batterySet.setFillColor(Color.GREEN);
                batterySet.setColor(Color.GREEN);

                ArrayList<ILineDataSet> dataSets = new ArrayList<ILineDataSet>();
                dataSets.add(powerSet);
                dataSets.add(batterySet);

                LineData data = new LineData(xLabels,dataSets);
                data.setDrawValues(false);
                lineChart.setData(data);

                lineChart.animateY(2500);
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

    public class WattageYAxisValueFormatter implements YAxisValueFormatter {

        private DecimalFormat mFormat;

        public WattageYAxisValueFormatter () {
            mFormat = new DecimalFormat("###,###,##0.0"); // use one decimal
        }

        @Override
        public String getFormattedValue(float value, YAxis yAxis) {
            // write your logic here
            // access the YAxis object to get more information
            return mFormat.format(value) + " W"; // e.g. append a dollar-sign
        }
    }

    public class VoltageYAxisValueFormatter implements YAxisValueFormatter {

        private DecimalFormat mFormat;

        public VoltageYAxisValueFormatter () {
            mFormat = new DecimalFormat("###,###,##0.0"); // use one decimal
        }

        @Override
        public String getFormattedValue(float value, YAxis yAxis) {
            // write your logic here
            // access the YAxis object to get more information
            return mFormat.format(value) + " V"; // e.g. append a dollar-sign
        }
    }


}
