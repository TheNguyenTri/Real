package android.trithe.real.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.trithe.real.R;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class PostImageActivity extends AppCompatActivity {
    private ImageView imgTin;
    private CircleImageView imageProfile;
    private TextView blogUserName;
    private TextView blogDate;
    private ImageView btnAddImage;
    private DatabaseReference mRootRef;
    private DatabaseReference mDisCovDatabase;
    private StorageReference mImageStorage;
    private DatabaseReference mUserRef;
    private EditText edSend;
    private ImageView btnSend;
    private FirebaseFirestore firebaseFirestore;
    private String user_id;
    private String mCurrentId;
    private String post_time;
    private String image_Post;
    private static final int GALLERY_PICK = 1;
    private LinearLayout ll;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tin);
        initFireBase();
        initView();
        getInfoUser();
        if (user_id.equals(mCurrentId)) {
            ll.setVisibility(View.GONE);
        } else {
            btnAddImage.setOnClickListener(v -> {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "SELECT IMAGE"), GALLERY_PICK);
            });
            btnSend.setOnClickListener(v -> {
                sendMessage();
                edSend.setText("");
                chatseen();
            });
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void initFireBase() {
        firebaseFirestore = FirebaseFirestore.getInstance();
        user_id = getIntent().getStringExtra("user_id");
        post_time = getIntent().getStringExtra("post_time");
        image_Post = getIntent().getStringExtra("image");
        mRootRef = FirebaseDatabase.getInstance().getReference();
        mImageStorage = FirebaseStorage.getInstance().getReference();
        mCurrentId = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        mUserRef = FirebaseDatabase.getInstance().getReference().child("TimeOnline").child(mCurrentId);
        mDisCovDatabase = FirebaseDatabase.getInstance().getReference().child("Chats").child(user_id);
    }

    private void chatseen() {
        mRootRef.child("Chats").child(mCurrentId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (!dataSnapshot.hasChild(user_id)) {
                    Map<String, Object> chatAddMap = new HashMap<>();
                    chatAddMap.put("seen", false);
                    chatAddMap.put("timestamp", ServerValue.TIMESTAMP);
                    Map<String, Object> chatUserMap = new HashMap<>();
                    chatUserMap.put("Chats/" + mCurrentId + "/" + user_id, chatAddMap);
                    chatUserMap.put("Chats/" + user_id + "/" + mCurrentId, chatAddMap);
                    mRootRef.updateChildren(chatUserMap, (databaseError, databaseReference) -> {
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }


    private void sendMessage() {
        String message = edSend.getText().toString();
        if (!message.equals("")) {
            String current_user_ref = "Messages/" + mCurrentId + "/" + user_id;
            String chat_user_ref = "Messages/" + user_id + "/" + mCurrentId;

            DatabaseReference user_message_push = mRootRef.child("Messages").child(mCurrentId).child(user_id).push();
            String push_id = user_message_push.getKey();

            Map<String, Object> messageMap = new HashMap<>();
            messageMap.put("message", message);
            messageMap.put("seen", false);
            messageMap.put("type", "text");
            messageMap.put("time", ServerValue.TIMESTAMP);
            messageMap.put("from", mCurrentId);

            Map<String, Object> messageUserMap = new HashMap<>();
            messageUserMap.put(current_user_ref + "/" + push_id, messageMap);
            messageUserMap.put(chat_user_ref + "/" + push_id, messageMap);

            mRootRef.updateChildren(messageUserMap, (databaseError, databaseReference) -> {
                mDisCovDatabase.child(mCurrentId).child("seen").setValue(false);
                Toast.makeText(PostImageActivity.this, "Gửi thành công", Toast.LENGTH_SHORT).show();
            });
        }
    }

    private void getInfoUser() {
        firebaseFirestore.collection("Users").document(user_id).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                final String userName = task.getResult().getString("name");
                final String userImage = task.getResult().getString("image");
                Glide.with(PostImageActivity.this).load(userImage).into(imageProfile);
                blogUserName.setText(userName);
            }
        });
        blogDate.setText(post_time);
        Glide.with(this).load(image_Post).into(imgTin);
    }

    private void initView() {
        imgTin = findViewById(R.id.imgTin);
        imageProfile = findViewById(R.id.imageprofile);
        blogUserName = findViewById(R.id.blog_user_name);
        blogDate = findViewById(R.id.blog_date);
        btnAddImage = findViewById(R.id.btnAddImage);
        edSend = findViewById(R.id.edSend);
        btnSend = findViewById(R.id.btnSend);
        ll = findViewById(R.id.ll);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mCurrentId != null) {
            firebaseFirestore.collection("Users").document(mCurrentId).update("online", true);
            mUserRef.child("online").setValue(true);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mCurrentId != null) {
            firebaseFirestore.collection("Users").document(mCurrentId).update("online", false);
            mUserRef.child("online").setValue(ServerValue.TIMESTAMP);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        onStop();
        startActivity(new Intent(PostImageActivity.this, MainActivity.class));
        finish();
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GALLERY_PICK && resultCode == RESULT_OK) {
            Uri imageUri = Objects.requireNonNull(data).getData();
            uploadImage(imageUri);
        }
    }

    private void uploadImage(Uri imageUri) {
        final String current_user_ref = "Messages/" + mCurrentId + "/" + user_id;
        final String chat_user_ref = "Messages/" + user_id + "/" + mCurrentId;
        DatabaseReference user_message_push = mRootRef.child("Messages").child(mCurrentId).child(user_id).push();
        final String push_id = user_message_push.getKey();
        StorageReference filepath = mImageStorage.child("message_images").child(push_id + ".jpg");
        filepath.putFile(imageUri).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                String download_uri = Objects.requireNonNull(task.getResult().getDownloadUrl()).toString();
                Map<String, Object> messageMap = new HashMap<>();
                messageMap.put("message", download_uri);
                messageMap.put("seen", false);
                messageMap.put("type", "image");
                messageMap.put("time", ServerValue.TIMESTAMP);
                messageMap.put("from", mCurrentId);
                Map<String, Object> messageUserMap = new HashMap<>();
                messageUserMap.put(current_user_ref + "/" + push_id, messageMap);
                messageUserMap.put(chat_user_ref + "/" + push_id, messageMap);
                mRootRef.updateChildren(messageUserMap, (databaseError, databaseReference) -> {
                    mDisCovDatabase.child(mCurrentId).child("seen").setValue(false);
                    Toast.makeText(PostImageActivity.this, "Gửi thành công", Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    public void backMain(View view) {
        finish();
    }
}
