package android.trithe.real;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.trithe.real.activity.HomeActivity;
import android.trithe.real.activity.SignupActivity;
import android.trithe.real.database.UserDAO;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

public class LoginActivity extends AppCompatActivity {
    private EditText username;
    private EditText password;
    private CheckBox chkrememberme;
    private UserDAO userDAO;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initView();
        userDAO = new UserDAO(this);
        restore();

    }

    public void login(View view) {
        String user = username.getText().toString();
        String pass = password.getText().toString();
        boolean chk = chkrememberme.isChecked();
        if (user.equals("")) {
            username.setError(getString(R.string.error_empty));
        } else if (pass.equals("")) {
            password.setError(getString(R.string.error_emptyps));
        } else {
            if (user.equals("admin") && pass.equals("admin")) {
                remember(user, pass, chk);
                startActivity(new Intent(getApplicationContext(), HomeActivity.class));
            } else if (pass.equals(userDAO.login(user))) {
                remember(user, pass, chk);
                finish();
                Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                startActivity(intent);
            } else if ((!user.equals("admin") && !pass.equals("admin")) || (!pass.equals(userDAO.login(user)))) {
                Toast.makeText(getApplicationContext(), getString(R.string.wrong_user_pass), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void remember(String u, String p, boolean check) {
        SharedPreferences sharedPreferences = getSharedPreferences("USERFILE", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        if (!check) {
            editor.clear();
        } else {
            editor.putString("username", u);
            editor.putString("password", p);
            editor.putBoolean("cbo", true);
        }
        editor.apply();
    }

    private void restore() {
        SharedPreferences pref = getSharedPreferences("USERFILE", MODE_PRIVATE);
        boolean check = pref.getBoolean("cbo", false);
        if (check) {
            String user = pref.getString("username", "");
            String pass = pref.getString("password", "");
            username.setText(user);
            password.setText(pass);
        }
        chkrememberme.setChecked(check);
    }

    private void initView() {
        username = (EditText) findViewById(R.id.username);
        password = (EditText) findViewById(R.id.password);
        chkrememberme = (CheckBox) findViewById(R.id.chkrememberme);
    }

    public void signup(View view) {
        finish();
        startActivity(new Intent(getApplicationContext(), SignupActivity.class));
    }

}
