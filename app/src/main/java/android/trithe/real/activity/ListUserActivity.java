package android.trithe.real.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.trithe.real.R;
import android.trithe.real.adapter.UserAdapter;
import android.trithe.real.database.UserDAO;
import android.trithe.real.inter.OnClick;
import android.trithe.real.model.User;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class ListUserActivity extends AppCompatActivity {
    private UserDAO userDAO;
    private ListView listView;
    private UserAdapter adapter;
    private List<User> list=new ArrayList<>();
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_user);
        initsView();
        setSupportActionBar(toolbar);
        userDAO = new UserDAO(this);
        list = userDAO.getAllUser();
        adapter = new UserAdapter(this, list, new OnClick() {
            @Override
            public void onItemClickClicked(int position) {
                userDAO.deleteUserByID(list.get(position).getUserName());
                list.remove(position);
                list.clear();
                list=userDAO.getAllUser();
                adapter.changeDataset(list);
            }
        });
        listView.setAdapter(adapter);

    }

    private void initsView() {
        listView = findViewById(R.id.lv);
        toolbar = findViewById(R.id.toolbaruser);

    }


    public void backuser(View view) {
        onBackPressed();
    }

    @Override
    protected void onResume() {
        super.onResume();
        list.clear();
        list = userDAO.getAllUser();
        adapter.changeDataset(list);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        startActivity(new Intent(getApplicationContext(), AddUserActivity.class));
        return super.onOptionsItemSelected(item);
    }

}
