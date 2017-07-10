package cn.lixiang.customviewsummary;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import cn.lixiang.customviewsummary.activity.PieGraphActivity;
import cn.lixiang.customviewsummary.activity.SlideSwitchActivity;
import cn.lixiang.customviewsummary.activity.TestActivity;
import cn.lixiang.customviewsummary.activity.TrendChartActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void cake_btn(View view) {
        Intent intent = new Intent(this, TestActivity.class);
        startActivity(intent);

    }

    public void cake_zhifubbtn(View view) {
        Intent intent = new Intent(this, PieGraphActivity.class);
        startActivity(intent);
    }

    public void SlideSwitch(View view) {
        Intent intent = new Intent(this, SlideSwitchActivity.class);
        startActivity(intent);
    }

    public void trendchart(View view) {
        Intent intent = new Intent(this, TrendChartActivity.class);
        startActivity(intent);
    }
}
