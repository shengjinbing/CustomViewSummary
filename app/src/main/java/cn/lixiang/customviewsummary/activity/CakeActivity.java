package cn.lixiang.customviewsummary.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import cn.lixiang.customviewsummary.R;
import cn.lixiang.customviewsummary.bean.CakeData;
import cn.lixiang.customviewsummary.views.CakePicture;

public class CakeActivity extends AppCompatActivity {

    private CakePicture mCakePicture;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cake);
        initView();
        initData();
        initListener();
    }

    private void initView() {
        mCakePicture = (CakePicture) findViewById(R.id.activity_cake_picture);
    }

    private void initData() {
        List<CakeData> cakeDatas = new ArrayList<>();
        Random random = new Random();
        for (int i = 0; i < 5; i++) {
            CakeData cakeData = new CakeData();
            cakeData.setName("小米"+i);
            cakeData.setValue(random.nextInt(100));
            cakeDatas.add(cakeData);
        }

        //设置结果
        mCakePicture.setData(cakeDatas);

    }

    private void initListener() {

    }
}
