package cn.lixiang.customviewsummary.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import java.util.HashMap;

import cn.lixiang.customviewsummary.R;
import cn.lixiang.customviewtools.views.MyViewLineChart;

public class TrendChartActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trend_chart);
        MyViewLineChart chart = (MyViewLineChart) findViewById(R.id.chart);
        String[] yItem = {"3%", "6%", "9%", "12%", "15%"};
        String[] xItem = {"19", "20", "21", "22", "23", "24","25"};
        int[] data = {3,5,6,11,9,12,15};
        HashMap<Integer,Integer> mData = new HashMap<>();
        for (int i = 0; i < xItem.length; i++) {
            mData.put(i,data[i]);
        }
        chart.setXItem(xItem);
        chart.setYItem(yItem);
        chart.setData(mData);

    }
}
