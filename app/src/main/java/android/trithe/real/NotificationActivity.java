package android.trithe.real;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class NotificationActivity extends AppCompatActivity {
    private TextView text;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);
        String dataMessage =getIntent().getStringExtra("dataMessage");
        String dataFrom =getIntent().getStringExtra("dataFrom");
        text = (TextView) findViewById(R.id.text);
        text.setText("From : "+dataFrom+" | Message : "+dataMessage);
    }
}
