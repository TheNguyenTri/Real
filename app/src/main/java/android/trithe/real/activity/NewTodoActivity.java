package android.trithe.real.activity;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.trithe.real.R;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;

public class NewTodoActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {
    private EditText titleDoes;
    private EditText desTodo;
    private static EditText timeTodo;
    private Button btnCreate;
    private Button btnCancel;

    private DatabaseReference mRootRef;
    private FirebaseAuth mAuth;
    private String mCurrentId;
    private static final SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_todo);
        initView();
        mAuth = FirebaseAuth.getInstance();
        mCurrentId = mAuth.getCurrentUser().getUid();
        mRootRef = FirebaseDatabase.getInstance().getReference();
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                startActivity(new Intent(getApplicationContext(),MainActivity.class));
            }
        });
        timeTodo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerFragment fragment = new DatePickerFragment();
                fragment.show(getSupportFragmentManager(), "date");
            }
        });
btnCreate.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        addTodos();
    }
});
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        startActivity(new Intent(getApplicationContext(),MainActivity.class));
    }

    private void addTodos() {
        if (titleDoes.getText().toString().equals("")) {
            titleDoes.setError("Title rỗng");
        } else if (desTodo.getText().toString().equals("")) {
            desTodo.setError("Description rỗng");
        } else if (timeTodo.getText().toString().equals("")) {
            timeTodo.setError("Timeline rỗng");
        } else {
            DatabaseReference user_todos_push = mRootRef.child("Todos").child(mCurrentId).push();

            Map messageMap = new HashMap();
            messageMap.put("title", titleDoes.getText().toString());
            messageMap.put("description", desTodo.getText().toString());
            messageMap.put("times", timeTodo.getText().toString());

            user_todos_push.updateChildren(messageMap, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                    Toast.makeText(getApplicationContext(), "Add successfully", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int day) {
    }

    public void back(View view) {
        onBackPressed();
    }


    public static class DatePickerFragment extends android.support.v4.app.DialogFragment {
        @NonNull
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);
            return new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                    Calendar cal = new GregorianCalendar(year, month, dayOfMonth);
                    timeTodo.setText(sdf.format(cal.getTime()));
                }
            }, year, month, day);
        }

    }

    private void initView() {
        titleDoes = (EditText) findViewById(R.id.titleDoes);
        desTodo = (EditText) findViewById(R.id.desTodo);
        timeTodo = (EditText) findViewById(R.id.timeTodo);
        btnCreate = (Button) findViewById(R.id.btnCreate);
        btnCancel = (Button) findViewById(R.id.btnCancel);
    }
}
