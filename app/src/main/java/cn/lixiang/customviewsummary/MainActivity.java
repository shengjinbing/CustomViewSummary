package cn.lixiang.customviewsummary;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import cn.lixiang.customviewsummary.activity.CakeActivity;
import cn.lixiang.customviewsummary.activity.PieGraphActivity;
import cn.lixiang.customviewsummary.activity.SlideSwitchActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void cake_btn(View view) {
        Intent intent = new Intent(this, CakeActivity.class);
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
}
