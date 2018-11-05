package prima.optimasi.indonesia.payroll.main_owner;

import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.AppCompatActivity;
import android.widget.RelativeLayout;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.List;

import prima.optimasi.indonesia.payroll.R;

public class Activity_Chart extends AppCompatActivity {

    public BarChart chart;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chart);

        chart = (BarChart) findViewById(R.id.chart);
        /*
        List<BarEntry> entries = new ArrayList<>();


        entries.add(new BarEntry(0, 40f));

        entries.add(new BarEntry(1, 80f));
        entries.add(new BarEntry(2, 60f));
        entries.add(new BarEntry(3, 50f));
        // gap of 2f

        entries.add(new BarEntry(5, 70f));

        entries.add(new BarEntry(6, 62.5f));
        entries.add(new BarEntry(8, 30f));

        BarDataSet set = new BarDataSet(entries, "Total Gaji Bersih");
        set.setStackLabels(new String[]{"CU1", "CU2"});

        BarData data = new BarData(set);

        data.setBarWidth(0.9f); // set custom bar width
        chart.setData(data);
        chart.setFitBars(true); // make the x-axis fit exactly all bars
        chart.invalidate(); // refresh

        BarEntry stackedEntry = new BarEntry(0f, new float[] { 10, 20, 30 });

        final ArrayList<String> xLabels = new ArrayList<>();
        xLabels.add("Jan");
        xLabels.add("Feb");
        xLabels.add("Mar");
        xLabels.add("Apr");
        xLabels.add("May");
        xLabels.add("Jun");

        xLabels.add("Jul");
        xLabels.add("Aug");
        xLabels.add("Sep");
        xLabels.add("Oct");
        xLabels.add("Nov");
        xLabels.add("Dec");

        XAxis xAxis = chart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setTextSize(10f);
        xAxis.setTextColor(Color.RED);
        xAxis.setDrawAxisLine(true);
        xAxis.setDrawGridLines(true);
        //xAxis.setLabelCount(10);
        //xAxis.setAxisMaximum(12f);
        xAxis.setDrawLabels(true);
        xAxis.setValueFormatter(new IndexAxisValueFormatter(xLabels));
        xAxis.setGranularity(1f);

        xAxis.setGranularityEnabled(true);*/







        Legend l = chart.getLegend();
        l.setFormSize(10f); // set the size of the legend forms/shapes
        l.setForm(Legend.LegendForm.CIRCLE); // set what type of form/shape should be used
        l.setPosition(Legend.LegendPosition.BELOW_CHART_LEFT);
        l.setTextSize(12f);
        l.setTextColor(Color.BLACK);
        l.setXEntrySpace(5f); // set the space between the legend entries on the x-axis
        l.setYEntrySpace(5f); // set the space between the legend entries on the y-axis

        /*
        set.setHighlightEnabled(true); // allow highlighting for DataSet

        // set this to false to disable the drawing of highlight indicator (lines)
        set.setDrawValues(true);
        set.setHighLightColor(Color.BLACK); // color for highlight indicator*/
        // set a custom value formatter
        chart.setDrawBarShadow(true);


        chart.animateX(3000); // animate horizontal 3000 milliseconds
// or:
        chart.animateY(3000, Easing.EasingOption.EaseOutBack);
// or:
        chart.animateXY(3000, 3000); // animate horizontal and vertical 3000 milliseconds









        YAxis yAxis = chart.getAxis(YAxis.AxisDependency.LEFT);
        chart.getAxisRight().setEnabled(false); // no right axis
        // Minimum section is 1.0f, could be 0.25f for float values
        float labelInterval = 1.0f / 2f;

        int sections=10; //No of sections in Y-Axis, Means your Highest Y-value.

        yAxis.setAxisMinimum(0f);
        yAxis.setAxisMaximum(100f);
        yAxis.setLabelCount(sections);
        setData(10);

    }
    public void setData(int count){
        ArrayList<BarEntry> yValues=new ArrayList<>();
        for(int i=0;i<count;i++){
            float val1=(float)(Math.random()*count)+20;
            float val2=(float)(Math.random()*count)+20;
            yValues.add(new BarEntry(i,new float[]{val1,val2}));
        }
        BarDataSet set1;
        set1=new BarDataSet(yValues,"Total Gaji");
        set1.setDrawIcons(false);
        set1.setStackLabels(new String[]{"Total gaji bersih", "Total potongan"});
        set1.setColors(ColorTemplate.COLORFUL_COLORS);
        BarData data= new BarData(set1);
        chart.setData(data);
        chart.setFitBars(true);
        chart.invalidate();
        chart.getDescription().setEnabled(false);
    }

}
/*
        String[] values = new String[] {"1","2","3","4","5","6","7","8","9","10","11","12"};
        xAxis.setValueFormatter(new MyXAxisValueFormatter(values));*/
        /*
        xAxis.setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {

                return xLabels.get((int) value);
            }

        });*/
/*
        YAxis left = chart.getAxisLeft();
        left.setDrawLabels(false); // no axis labels
        left.setDrawAxisLine(false); // no axis line
        left.setDrawGridLines(false); // no grid lines
        left.setDrawZeroLine(true); // draw a zero line


        YAxis yAxis = chart.getAxisLeft();
        yAxis.setTextSize(12f); // set the text size
        yAxis.setAxisMinimum(0f); // start at zero
        yAxis.setAxisMaximum(100f); // the axis maximum is 100
        yAxis.setTextColor(Color.BLACK);
        yAxis.setGranularity(1f); // interval 1
        yAxis.setLabelCount(6, true); // force 6 labels
        */
/*
        YAxis leftAxis = chart.getAxisLeft();
        leftAxis.setTextColor(Color.RED);
        LimitLine ll = new LimitLine(140f, "Critical Blood Pressure");
        ll.setLineColor(Color.RED);
        ll.setLineWidth(4f);
        ll.setTextColor(Color.BLACK);
        ll.setTextSize(12f);
        // .. and more styling options
        leftAxis.addLimitLine(ll);
        leftAxis.setValueFormatter(new IndexAxisValueFormatter(xLabels));*/
/*
        chart.getAxisLeft().setLabelCount( 10+ 2, true);
        chart.getAxisLeft().setAxisMinValue(0f);
        chart.getAxisLeft().setAxisMaxValue(10 + 1);
        YAxisValueFormatter customYaxisFormatter = new YAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, YAxis yAxis) {
                return String.valueOf((int)value);
            }
        };
        chart.getAxisLeft().setValueFormatter(customYaxisFormatter);*/

        /*
        set.addEntry(new BarEntry(7f, 31.25f));
        data.notifyDataChanged(); // let the data know a dataSet changed
        chart.notifyDataSetChanged(); // let the chart know it's data changed
        chart.invalidate(); // refresh*/
