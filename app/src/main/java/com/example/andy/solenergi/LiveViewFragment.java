package com.example.andy.solenergi;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.YAxisValueFormatter;

import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;

/**
 * A placeholder fragment containing a simple view.
 */
public class LiveViewFragment extends Fragment {
    //some changes

    private static final String TAG = "LiveViewFragment";



    // Layout Views
    private TextView voltageView;
    private TextView batVoltageView;
    private TextView currentView;
    private TextView powerView;

    // variables
    private float voltage;
    private float batVoltage;
    private float current;
    private float power;

    // graphing variables
    LineChart lineChart;
    LineDataSet powerSet;
    LineDataSet batterySet;
    LineDataSet currentSet;
    LineData data;
    ArrayList<String> xLabels;
    int count;

    public LiveViewFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_live_view, container, false);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        voltageView = (TextView) view.findViewById(R.id.voltage_text);
        batVoltageView = (TextView) view.findViewById(R.id.bat_voltage_text);
        currentView = (TextView) view.findViewById(R.id.current_text);
        powerView = (TextView) view.findViewById(R.id.power_text);
        count=0;

        lineChart = (LineChart) view.findViewById(R.id.chart);
        lineChart.setDescription("");

        data = new LineData();

        XAxis xAxis = lineChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);

        YAxis leftYAxis = lineChart.getAxisLeft();
        leftYAxis.setValueFormatter(new WattageYAxisValueFormatter());
        leftYAxis.setLabelCount(6, true);
        leftYAxis.setAxisMinValue(0);
        leftYAxis.setAxisMaxValue(5);

        YAxis rightYAxis = lineChart.getAxisRight();
        rightYAxis.setValueFormatter(new VoltageAmperageYAxisValueFormatter());
        rightYAxis.setLabelCount(6, true);
        rightYAxis.setAxisMinValue(0);
        rightYAxis.setAxisMaxValue(5);


        powerSet = new LineDataSet(null, "Power");
        powerSet.setAxisDependency(YAxis.AxisDependency.LEFT);
        powerSet.setColor(Color.RED);
        powerSet.setDrawCubic(true);
        powerSet.setDrawCircles(false);
        powerSet.setLineWidth(3);

        batterySet = new LineDataSet(null, "Battery");
        batterySet.setAxisDependency(YAxis.AxisDependency.RIGHT);
        batterySet.setColor(Color.GREEN);
        batterySet.setDrawCubic(true);
        batterySet.setDrawCircles(false);
        batterySet.setLineWidth(3);

        currentSet = new LineDataSet(null, "Current");
        currentSet.setAxisDependency(YAxis.AxisDependency.RIGHT);
        currentSet.setColor(Color.BLUE);
        currentSet.setDrawCubic(true);
        currentSet.setDrawCircles(false);
        currentSet.setLineWidth(3);

        data.addDataSet(powerSet);
        data.addDataSet(batterySet);
        data.addDataSet(currentSet);
        lineChart.setData(data);

    }

    public void updateGraph(JSONObject inputJson)
    {
        try {
            voltage = Float.parseFloat(inputJson.getString("voltage"));
            voltageView.setText(String.format("%.2f", voltage) + "V");    // update text views

            batVoltage = Float.parseFloat(inputJson.getString("batVoltage"));
            batVoltageView.setText(String.format("%.2f", batVoltage) + "V");

            current = Math.abs(Float.parseFloat(inputJson.getString("current")));
            currentView.setText((String.format("%.2f", current) + "A"));

            power = voltage*current;
            powerView.setText((String.format("%.2f", power) + "W"));

            data.addXValue(count+"");
            powerSet.addEntry(new Entry(power, count));
            batterySet.addEntry(new Entry(batVoltage, count));
            currentSet.addEntry(new Entry(current, count));
            count++;


            data.notifyDataChanged(); // let the data know a dataSet changed
            lineChart.notifyDataSetChanged(); // let the chart know it's data changed
            lineChart.invalidate(); // refresh



        }catch(Exception e){}
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

    public class VoltageAmperageYAxisValueFormatter implements YAxisValueFormatter {

        private DecimalFormat mFormat;

        public VoltageAmperageYAxisValueFormatter() {
            mFormat = new DecimalFormat("###,###,##0.0"); // use one decimal
        }

        @Override
        public String getFormattedValue(float value, YAxis yAxis) {
            // write your logic here
            // access the YAxis object to get more information
            return mFormat.format(value) + " V,A"; // e.g. append a dollar-sign
        }
    }

}
