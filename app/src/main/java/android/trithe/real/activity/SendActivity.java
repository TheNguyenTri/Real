package android.trithe.real.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.trithe.real.R;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ServerValue;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class SendActivity extends AppCompatActivity {
    private EditText edSend;
    private ImageView btnSend;
    private String id, name;
    private String mCurrentId;
    private FirebaseFirestore firebaseFirestore;

    private TextView nameUserChat;
    private TextView timeOnlineChat;
    private CircleImageView imageUserChat;
    private RecyclerView recyclerViewChat;
    private ImageView btnAddImage;

    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send);
        initView();
        firebaseFirestore = FirebaseFirestore.getInstance();
        id = getIntent().getStringExtra("user_id");
        name = getIntent().getStringExtra("user_name");
        mCurrentId = FirebaseAuth.getInstance().getUid();
        nameUserChat.setText(name);
        Glide.with(SendActivity.this).load(getIntent().getStringExtra("user_image")).into(imageUserChat);

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message=edSend.getText().toString();
                if(!message.equals("")){
                    final Map<String,Object> notificationMessage =new HashMap<>();
                    notificationMessage.put("message",message);
                    notificationMessage.put("from",mCurrentId);

                    firebaseFirestore.collection("Users/"+id+"/Notification").add(notificationMessage).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            Toast.makeText(getApplicationContext(),"Ok",Toast.LENGTH_SHORT).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                        }
                    });
                }
            }
        });
    }

    private void initView() {
        edSend = (EditText) findViewById(R.id.edSend);
        btnSend =  findViewById(R.id.btnSend);
        nameUserChat = (TextView) findViewById(R.id.name_user_chat);
        timeOnlineChat = (TextView) findViewById(R.id.time_online_chat);
        imageUserChat = (CircleImageView) findViewById(R.id.image_user_chat);
        recyclerViewChat = (RecyclerView) findViewById(R.id.recycler_view_chat);
        btnAddImage = (ImageView) findViewById(R.id.btnAddImage);
    }

    public void out(View view) {
        onBackPressed();
    }
}
