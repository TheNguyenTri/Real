package android.trithe.real.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.trithe.real.R;
import android.trithe.real.adapter.DogAdapter;
import android.trithe.real.database.DogDAO;
import android.trithe.real.model.Dog;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;

import java.io.ByteArrayOutputStream;
import java.util.List;

public class HomeActivity extends AppCompatActivity {
    private GridView listView;
    private DogDAO dogDao;
    private DogAdapter adapter;
    private List<Dog> list;
    private FloatingActionButton fab;
    Toolbar toolbar;

    @SuppressLint({"RestrictedApi"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        listView = findViewById(R.id.lvcho);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        dogDao = new DogDAO(HomeActivity.this);
        list = dogDao.getAllDog();
        adapter = new DogAdapter(this, list);
        listView.setAdapter(adapter);
//        fab=findViewById(R.id.fab);
//        SharedPreferences pref = getSharedPreferences("USERFILE",MODE_PRIVATE);
//        String strUserName = pref.getString("username", "");
//        if (strUserName.equals("admin")) {
//            fab.setVisibility(View.VISIBLE);
//        } else {
//            fab.setVisibility(View.GONE);
//        }
        SharedPreferences pref = getSharedPreferences("USERFILE", MODE_PRIVATE);
        String strUserName = pref.getString("username", "");
        if (strUserName.equals("admin")) {
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    finish();
                    Intent intent = new Intent(HomeActivity.this, EditDogActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("ID", list.get(position).getDogid());
                    bundle.putString("NAME", list.get(position).getName());
                    bundle.putString("AGE", String.valueOf(list.get(position).getAge()));
                    bundle.putString("PRICE", String.valueOf(list.get(position).getPrice()));
                    bundle.putByteArray("IMAGE", list.get(position).getImage());
                    intent.putExtras(bundle);
                    startActivity(intent);
                }
            });
        }
    }

    public byte[] ImageViewChange(ImageView imageView) {
        BitmapDrawable drawable = (BitmapDrawable) imageView.getDrawable();
        Bitmap bitmap = drawable.getBitmap();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteArray = stream.toByteArray();
        return byteArray;
    }

    public void addd(View view) {
        startActivity(new Intent(getApplicationContext(), AddDogActivity.class));
    }
}
