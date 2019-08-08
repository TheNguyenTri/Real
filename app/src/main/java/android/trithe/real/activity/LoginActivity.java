package android.trithe.real.activity;

import android.app.ActivityOptions;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.trithe.real.R;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class LoginActivity extends AppCompatActivity {
    FloatingActionButton fab;
    private GoogleSignInClient mGoogleSignInClient;
    int RC_SIGN_IN = 1;
    private EditText editTextUsername, editTextPassword;
    private FirebaseAuth mAuth;
    private Button btnFacebook;
    private CallbackManager callbackManager;
    private Button btnGoogle;
    private ProgressDialog pDialog;
    private FirebaseFirestore firebaseFirestore;
    private String user_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initView();
        FirebaseApp.initializeApp(LoginActivity.this);
        mAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        register();
        btnGoogle.setOnClickListener(v -> signIn());
        btnFacebook.setOnClickListener(v -> Facebook());
    }

    private void register() {
        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(LoginActivity.this);
        callbackManager = CallbackManager.Factory.create();
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
    }

    private void Facebook() {
        LoginManager.getInstance().logInWithReadPermissions(LoginActivity.this, Arrays.asList("email", "public_profile"));
        LoginManager.getInstance().registerCallback(
                callbackManager, new FacebookCallback<LoginResult>() {
                    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        handleFacebookAccessToken(loginResult.getAccessToken());
                    }

                    @Override
                    public void onCancel() {

                    }

                    @Override
                    public void onError(FacebookException error) {

                    }
                }
        );
    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private void initView() {
        fab = findViewById(R.id.fab);
        editTextUsername = findViewById(R.id.et_username);
        editTextPassword = findViewById(R.id.et_password);
        btnFacebook = findViewById(R.id.btnFacebook);
        btnGoogle = findViewById(R.id.btnGoogle);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void clickRegisterLayout(View view) {
        getWindow().setExitTransition(null);
        getWindow().setEnterTransition(null);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            ActivityOptions options =
                    ActivityOptions.makeSceneTransitionAnimation(LoginActivity.this, fab, fab.getTransitionName());
            startActivityForResult(new Intent(this, RegisterActivity.class), 2000, options.toBundle());
        } else {
            startActivityForResult(new Intent(this, RegisterActivity.class), 2000);
        }
    }

    private void showpDialog() {
        pDialog = new ProgressDialog(this);
        pDialog.setMessage("Please wait...");
        pDialog.setCancelable(false);
        pDialog.show();
    }

    private void showDispDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }

    public void clickLogin(View view) {
        String emails = editTextUsername.getText().toString();
        String pass = editTextPassword.getText().toString();
        if (emails.equals("")) {
            editTextUsername.setError("Email must not empty");
        } else if (pass.equals("")) {
            editTextPassword.setError("Password must not empty");
        } else {
            showpDialog();
            mAuth.signInWithEmailAndPassword(emails, pass).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Objects.requireNonNull(mAuth.getCurrentUser()).getIdToken(true).addOnSuccessListener(getTokenResult -> {
                        String token_id = getTokenResult.getToken();
                        String current_id = mAuth.getCurrentUser().getUid();
                        Map<String, Object> tokenMap = new HashMap<>();
                        tokenMap.put("token_id", Objects.requireNonNull(token_id));
                        firebaseFirestore.collection("Users").document(current_id).update(tokenMap)
                                .addOnSuccessListener(aVoid -> {
                                    sendtoMain();
                                    if (pDialog.isShowing())
                                        pDialog.dismiss();
                                });
                    });
                } else {
                    String errorMessage = Objects.requireNonNull(task.getException()).getMessage();
                    Toast.makeText(getApplicationContext(), errorMessage, Toast.LENGTH_SHORT).show();
                }
                showDispDialog();
            });
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            sendtoMain();
        }
    }

    private void sendtoMain() {
        startActivity(new Intent(getApplicationContext(), MainActivity.class));
        finish();
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately;
                // ...
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, tasks -> {
                    final String name = tasks.getResult().getUser().getDisplayName();
                    if (tasks.isSuccessful()) {
                        user_id = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
                        firebaseFirestore.collection("Users").document(user_id).get().addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                if (task.getResult().exists()) {
                                    String token_id = FirebaseInstanceId.getInstance().getToken();
                                    String current_id = mAuth.getCurrentUser().getUid();
                                    Map<String, Object> tokenMap = new HashMap<>();
                                    tokenMap.put("token_id", Objects.requireNonNull(token_id));

                                    firebaseFirestore.collection("Users").document(current_id).update(tokenMap)
                                            .addOnSuccessListener(aVoid -> sendtoMain());

                                } else {
                                    GoogleAndFaceBookLogin(name);
                                }
                            }
                        });
                    }
                });
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void GoogleAndFaceBookLogin(String name) {
        // nếu đã được dùng up token k được dùng tạo luôn token
        String token_id = FirebaseInstanceId.getInstance().getToken();
        Map<String, String> userMap = new HashMap<>();
        userMap.put("name", name);
        userMap.put("image", String.valueOf(Objects.requireNonNull(mAuth.getCurrentUser()).getPhotoUrl()));
        userMap.put("token_id", Objects.requireNonNull(token_id));
        firebaseFirestore.collection("Users").document(user_id).set(userMap).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(getApplicationContext(), "The user are updated", Toast.LENGTH_SHORT).show();
                sendtoMain();
            } else {
                Toast.makeText(getApplicationContext(), Objects.requireNonNull(task.getException()).getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
        Toast.makeText(getApplicationContext(), "Đã Upload", Toast.LENGTH_SHORT).show();
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void handleFacebookAccessToken(AccessToken token) {
        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, tasks -> {
                    final String name = tasks.getResult().getUser().getDisplayName();
                    if (tasks.isSuccessful()) {
                        user_id = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
                        firebaseFirestore.collection("Users").document(user_id).get().addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                if (task.getResult().exists()) {
                                    String token_id = FirebaseInstanceId.getInstance().getToken();
                                    String current_id = mAuth.getCurrentUser().getUid();
                                    Map<String, Object> tokenMap = new HashMap<>();
                                    tokenMap.put("token_id", Objects.requireNonNull(token_id));

                                    firebaseFirestore.collection("Users").document(current_id).update(tokenMap)
                                            .addOnSuccessListener(aVoid -> sendtoMain());

                                } else {
                                    GoogleAndFaceBookLogin(name);
                                }
                            }
                        });
                    }
                });
    }

}

