package cn.lixiang.customviewsummary.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import cn.lixiang.customviewsummary.R;
import cn.lixiang.customviewtools.views.SlideSwitch;

public class SlideSwitchActivity extends AppCompatActivity implements SlideSwitch.OnSwitchOpendState {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_slide_switch);
        SlideSwitch aSwitch = (SlideSwitch) findViewById(R.id.activity_slide_switch);
        aSwitch.setSlideBgRes(R.drawable.switch_background);
        aSwitch.setSlideBtnRes(R.drawable.slide_button_background);
        aSwitch.setOnSwitchOpendState(this);
    }

    @Override
    public void getSwitchSate(boolean isOpen) {
        Log.d("BBBBB",isOpen+"");
    }
}
