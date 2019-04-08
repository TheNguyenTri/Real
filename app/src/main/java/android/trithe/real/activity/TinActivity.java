package android.trithe.real.activity;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class TinActivity extends AppCompatActivity {
    private ImageView imgTin;
    private CircleImageView imageprofile;
    private TextView blogUserName;
    private TextView blogDate;
    private ImageView btnAddImage;
    private DatabaseReference mRootRef;
    private DatabaseReference mCovDatabase;
    private DatabaseReference mDisCovDatabase;
    private StorageReference mImageStorage;
    private DatabaseReference mUserRef;
    private EditText edSend;
    private ImageView btnSend;
    private FirebaseFirestore firebaseFirestore;
    private String user_id, mCurrentId, post_time, image_Post;
    private static final int GALLERY_PICK = 1;
    private LinearLayout ll;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tin);
        initFirebase();
        initView();
        getInfoUser();
        if (user_id.equals(mCurrentId)) {
            ll.setVisibility(View.GONE);
        } else {
            btnAddImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent();
                    intent.setType("image/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(Intent.createChooser(intent, "SELECT IMAGE"), GALLERY_PICK);
                }
            });
            btnSend.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    sendMessage();
                    edSend.setText("");
                    chatseen();
                }
            });
        }
    }

    private void initFirebase() {
        firebaseFirestore = FirebaseFirestore.getInstance();
        user_id = getIntent().getStringExtra("user_id");
        post_time = getIntent().getStringExtra("post_time");
        image_Post = getIntent().getStringExtra("image");
        mRootRef = FirebaseDatabase.getInstance().getReference();
        mImageStorage = FirebaseStorage.getInstance().getReference();
        mCurrentId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        mUserRef = FirebaseDatabase.getInstance().getReference().child("TimeOnline").child(mCurrentId);
        mCovDatabase = FirebaseDatabase.getInstance().getReference().child("Chats").child(mCurrentId);
        mDisCovDatabase = FirebaseDatabase.getInstance().getReference().child("Chats").child(user_id);
    }

    private void chatseen() {
        mRootRef.child("Chats").child(mCurrentId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (!dataSnapshot.hasChild(user_id)) {
                    Map chatAddMap = new HashMap();
                    chatAddMap.put("seen", false);
                    chatAddMap.put("timestamp", ServerValue.TIMESTAMP);
                    Map chatUserMap = new HashMap();
                    chatUserMap.put("Chats/" + mCurrentId + "/" + user_id, chatAddMap);
                    chatUserMap.put("Chats/" + user_id + "/" + mCurrentId, chatAddMap);
                    mRootRef.updateChildren(chatUserMap, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                        }
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

            Map messageMap = new HashMap();
            messageMap.put("message", message);
            messageMap.put("seen", false);
            messageMap.put("type", "text");
            messageMap.put("time", ServerValue.TIMESTAMP);
            messageMap.put("from", mCurrentId);

            Map messageUserMap = new HashMap();
            messageUserMap.put(current_user_ref + "/" + push_id, messageMap);
            messageUserMap.put(chat_user_ref + "/" + push_id, messageMap);

            mRootRef.updateChildren(messageUserMap, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                    mDisCovDatabase.child(mCurrentId).child("seen").setValue(false);
                    Toast.makeText(TinActivity.this, "Gửi thành công", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void getInfoUser() {
        firebaseFirestore.collection("Users").document(user_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    final String userName = task.getResult().getString("name");
                    final String userImage = task.getResult().getString("image");
                    Glide.with(TinActivity.this).load(userImage).into(imageprofile);
                    blogUserName.setText(userName);
                }
            }
        });
        blogDate.setText(post_time);
        RequestOptions requestOptions = new RequestOptions();
        requestOptions.placeholder(R.drawable.default_image);
        Glide.with(this).applyDefaultRequestOptions(requestOptions).load(image_Post).into(imgTin);
    }

    private void initView() {
        imgTin = (ImageView) findViewById(R.id.imgTin);
        imageprofile = (CircleImageView) findViewById(R.id.imageprofile);
        blogUserName = (TextView) findViewById(R.id.blog_user_name);
        blogDate = (TextView) findViewById(R.id.blog_date);
        btnAddImage = (ImageView) findViewById(R.id.btnAddImage);
        edSend = (EditText) findViewById(R.id.edSend);
        btnSend = (ImageView) findViewById(R.id.btnSend);
        ll = (LinearLayout) findViewById(R.id.ll);
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
        startActivity(new Intent(TinActivity.this, MainActivity.class));
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GALLERY_PICK && resultCode == RESULT_OK) {
            Uri imageUri = data.getData();
            uploadImage(imageUri);
        }
    }

    private void uploadImage(Uri imageUri) {
        final String current_user_ref = "Messages/" + mCurrentId + "/" + user_id;
        final String chat_user_ref = "Messages/" + user_id + "/" + mCurrentId;
        DatabaseReference user_message_push = mRootRef.child("Messages").child(mCurrentId).child(user_id).push();
        final String push_id = user_message_push.getKey();
        StorageReference filepath = mImageStorage.child("message_images").child(push_id + ".jpg");
        filepath.putFile(imageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if (task.isSuccessful()) {
                    String download_uri = task.getResult().getDownloadUrl().toString();
                    Map messageMap = new HashMap();
                    messageMap.put("message", download_uri);
                    messageMap.put("seen", false);
                    messageMap.put("type", "image");
                    messageMap.put("time", ServerValue.TIMESTAMP);
                    messageMap.put("from", mCurrentId);
                    Map messageUserMap = new HashMap();
                    messageUserMap.put(current_user_ref + "/" + push_id, messageMap);
                    messageUserMap.put(chat_user_ref + "/" + push_id, messageMap);
                    mRootRef.updateChildren(messageUserMap, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                            mDisCovDatabase.child(mCurrentId).child("seen").setValue(false);
                            Toast.makeText(TinActivity.this, "Gửi thành công", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }

    public void backMain(View view) {
        finish();
    }
}
