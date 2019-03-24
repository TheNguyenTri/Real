package android.trithe.real.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.trithe.real.R;
import android.trithe.real.adapter.AboutAdapter;
import android.trithe.real.model.About1;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class AboutActivity extends AppCompatActivity {
    private final List<About1> list = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        ListView listView1 = findViewById(R.id.lvbou1);
        ListView listView2 = findViewById(R.id.lvbou2);
        ListView listView3 = findViewById(R.id.lvbou3);
        list.add(new About1("Nguyen Tri The", "thentph06484@fpt.edu.vn"));
        list.add(new About1("FPT Polytechnic", "fpteducation@fe.edu.vn"));
        List<String> list2 = new ArrayList<>();
        List<String> list3 = new ArrayList<>();

        list2.add("Share app with friends");
        list2.add("Rate on Google Play");
        list3.add("Privacy policy");
        list3.add("Open source licenses");
        ArrayAdapter<String> adapter1 = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, list2);
        ArrayAdapter<String> adapter2 = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, list3);
        listView2.setAdapter(adapter1);
        listView3.setAdapter(adapter2);
        AboutAdapter adapter = new AboutAdapter(list, this);
        listView1.setAdapter(adapter);
    }

    public void back(View view) {
        onBackPressed();
    }
}
