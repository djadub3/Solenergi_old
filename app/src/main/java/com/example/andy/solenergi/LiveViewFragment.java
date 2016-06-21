package com.example.andy.solenergi;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import org.json.JSONObject;

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
    private double voltage;
    private double batVoltage;
    private double current;
    private double power;

    // graphing variables
    private double graphLastXValue = 0d;
    private LineGraphSeries<DataPoint> series;
    private double graph2LastXValue = 0d;
    private double graph3LastXValue = 0d;
    private LineGraphSeries<DataPoint> series2;
    private LineGraphSeries<DataPoint> series3;


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


        GraphView graph = (GraphView) view.findViewById(R.id.graph);
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

        graph.getViewport().setXAxisBoundsManual(true);
        graph.getViewport().setYAxisBoundsManual(true);
        graph.getViewport().setMinX(0);
        graph.getViewport().setMaxX(10);
        graph.getViewport().setMinY(0);
        graph.getViewport().setMaxY(15);
        graph.getGridLabelRenderer().setPadding(30);
        graph.getGridLabelRenderer().setHorizontalLabelsVisible(false);
        graph.getGridLabelRenderer().setNumVerticalLabels(16);
        graph.getGridLabelRenderer().setVerticalAxisTitle("Volts/Amps");
        //graph.getGridLabelRenderer().set
        graph.getGridLabelRenderer().setVerticalAxisTitleColor(Color.GRAY);
        graph.getGridLabelRenderer().setVerticalLabelsSecondScaleColor(Color.GRAY);
        graph.getGridLabelRenderer().reloadStyles();
    }

    public void updateGraph(JSONObject inputJson)
    {
        try {
            voltage = Double.parseDouble(inputJson.getString("voltage"));
            voltageView.setText(String.format("%.2f", voltage) + "V");    // update text views

            batVoltage = Double.parseDouble(inputJson.getString("batVoltage"));
            batVoltageView.setText(String.format("%.2f", batVoltage) + "V");

            current = Math.abs(Double.parseDouble(inputJson.getString("current")));
            currentView.setText((String.format("%.2f", current) + "A"));

            series.appendData(new DataPoint(graphLastXValue, voltage), true, 40);
            graphLastXValue += 1d;

            series2.appendData(new DataPoint(graph2LastXValue, current), true, 40);
            graph2LastXValue += 1d;

            power = current * voltage;
            series3.appendData(new DataPoint(graph3LastXValue, power), true, 40);
            graph3LastXValue += 1d;

            powerView.setText((String.format("%.2f", power) + "W"));
        }catch(Exception e){}
    }

}
