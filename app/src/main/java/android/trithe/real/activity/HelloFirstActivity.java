package android.trithe.real.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.trithe.real.R;

import java.util.Timer;
import java.util.TimerTask;

public class HelloFirstActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hello);
        Timer timer = new Timer();

        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                finish();
                startActivity(new Intent(HelloFirstActivity.this, LoginActivity.class));
            }
        }, 1000);
    }

}
