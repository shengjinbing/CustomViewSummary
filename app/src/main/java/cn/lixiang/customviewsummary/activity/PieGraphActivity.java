package cn.lixiang.customviewsummary.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import cn.lixiang.customviewsummary.R;
import cn.lixiang.customviewtools.bean.PieItem;
import cn.lixiang.customviewtools.bean.PieItemGroup;
import cn.lixiang.customviewtools.views.PieGraphView;

public class PieGraphActivity extends AppCompatActivity {
    private PieGraphView pie1;
    private int[] colors = new int[]{0xfff9bdbb, 0xfff36c60, 0xffce93d8, 0xffafbfff, 0xffb2dfdb, 0xff00acc1, 0xffcddc39, 0xff259b24};
    private int colorIndex;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pie_graph);

        pie1 = (PieGraphView) findViewById(R.id.pie1);
        pie1.setRingWidthFactor(0.37f);

        // 造例子数据
        PieItemGroup[] groups = new PieItemGroup[3];

        for (int i = 0; i < groups.length; i++) {
            PieItemGroup itemGroup = new PieItemGroup();
            groups[i] = itemGroup;
            itemGroup.id = "zu@" + i;
            PieItem[] items = new PieItem[4];
            itemGroup.items = items;

            for (int j = 0; j < items.length; j++) {
                PieItem item = new PieItem();
                item.id = "it@" + j;
                item.value = j * 24 + 24;
                item.color = colors[colorIndex++ % colors.length];
                items[j] = item;
            }
        }

        pie1.setData(groups);
        pie1.setItemChangeListener(new PieGraphView.ItemChangeListener() {
            @Override
            public void onItemSelected(PieItemGroup group, PieItem item) {
                String msg = "group = " + group.id + ", item = " + item.id;
                Log.d(PieGraphActivity.class.getSimpleName(), msg);
            }
        });
    }
}
