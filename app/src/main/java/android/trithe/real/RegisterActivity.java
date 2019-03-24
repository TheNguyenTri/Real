package android.trithe.real;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.text.TextUtils;
import android.trithe.real.activity.SetUpActivity;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.animation.AccelerateInterpolator;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class RegisterActivity extends AppCompatActivity {
    FloatingActionButton fab;
    CardView cvAdd;
    private FirebaseAuth mAuth;
    private EditText editTextUsername, editTextPassword, editTextRepeatPassword;
    private ProgressDialog pDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        initView();
        mAuth = FirebaseAuth.getInstance();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            ShowEnterAnimation();
        }
        fab.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View v) {
                animateRevealClose();
            }
        });
    }

    private void initView() {
        fab = (FloatingActionButton) findViewById(R.id.fab);
        cvAdd = (CardView) findViewById(R.id.cv_add);
        editTextUsername = (EditText) findViewById(R.id.et_username);
        editTextPassword = (EditText) findViewById(R.id.et_password);
        editTextRepeatPassword = (EditText) findViewById(R.id.et_repeatpassword);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void ShowEnterAnimation() {
        android.transition.Transition transition = android.transition.TransitionInflater.from(this).inflateTransition(R.transition.fabtransition);
        getWindow().setSharedElementEnterTransition(transition);

        transition.addListener(new android.transition.Transition.TransitionListener() {
            @Override
            public void onTransitionStart(android.transition.Transition transition) {
                cvAdd.setVisibility(View.GONE);
            }

            @Override
            public void onTransitionEnd(android.transition.Transition transition) {
                transition.removeListener(this);
                animateRevealShow();
            }

            @Override
            public void onTransitionCancel(android.transition.Transition transition) {

            }

            @Override
            public void onTransitionPause(android.transition.Transition transition) {

            }

            @Override
            public void onTransitionResume(android.transition.Transition transition) {

            }


        });
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void animateRevealShow() {
        Animator mAnimator = ViewAnimationUtils.createCircularReveal(cvAdd, cvAdd.getWidth() / 2, 0, fab.getWidth() / 2, cvAdd.getHeight());
        mAnimator.setDuration(500);
        mAnimator.setInterpolator(new AccelerateInterpolator());
        mAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
            }

            @Override
            public void onAnimationStart(Animator animation) {
                cvAdd.setVisibility(View.VISIBLE);
                super.onAnimationStart(animation);
            }
        });
        mAnimator.start();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onBackPressed() {
        animateRevealClose();
    }


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void animateRevealClose() {
        Animator mAnimator = ViewAnimationUtils.createCircularReveal(cvAdd, cvAdd.getWidth() / 2, 0, cvAdd.getHeight(), fab.getWidth() / 2);
        mAnimator.setDuration(500);
        mAnimator.setInterpolator(new AccelerateInterpolator());
        mAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                cvAdd.setVisibility(View.INVISIBLE);
                super.onAnimationEnd(animation);
                fab.setImageResource(R.drawable.ic_signup);
                RegisterActivity.super.onBackPressed();
            }

            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
            }
        });
        mAnimator.start();
    }


    public void clickRegister(View view) {
        String email = editTextUsername.getText().toString();
        String pass = editTextPassword.getText().toString();
        String cfpass = editTextRepeatPassword.getText().toString();
            if(email.isEmpty()){
                editTextUsername.setError("Email must not empty");
            }else if(pass.isEmpty()){
                editTextPassword.setError("Password must not empty");
            }else if (! pass.equals(cfpass)) {
                editTextRepeatPassword.setError("Confirm password must like as Password");
            } else {
                pDialog = new ProgressDialog(RegisterActivity.this);
                pDialog.setMessage("Please wait...");
                pDialog.setCancelable(false);
                pDialog.show();
                mAuth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            startActivity(new Intent(getApplicationContext(),SetUpActivity.class));
                        } else {
                            Toast.makeText(getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                        if (pDialog.isShowing())
                            pDialog.dismiss();
                    }
                });
            }
    }




}
