package android.trithe.real.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.trithe.real.R;
import android.trithe.real.adapter.SlidePaperAdapter;
import android.trithe.real.model.BlogPost;
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
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class PostImageActivity extends AppCompatActivity implements View.OnClickListener {
    private ViewPager backdrop;
    private List<BlogPost> image_post = new ArrayList<>();
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
    private String mCurrentId;
    private ImageView imgBack;
    private static final int GALLERY_PICK = 1;
    private LinearLayout ll;
    DateFormat dateFormat = SimpleDateFormat.getDateInstance(DateFormat.MEDIUM, Locale.US);

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tin);
        initFireBase();
        initView();
        getDataPost();
        imgBack.setOnClickListener(this);
    }

    private void getDataPost() {
        image_post.clear();
        Query firstQuery = firebaseFirestore.collection("Posts").orderBy("timestamp", Query.Direction.DESCENDING);
        firstQuery.addSnapshotListener(PostImageActivity.this, (queryDocumentSnapshots, e) -> {
            if (queryDocumentSnapshots != null) {
                for (DocumentChange doc : queryDocumentSnapshots.getDocumentChanges()) {
                    if (doc.getType() == DocumentChange.Type.ADDED) {
                        String blogPostId = doc.getDocument().getId();
                        BlogPost blogPost = doc.getDocument().toObject(BlogPost.class).withId(blogPostId);
                        image_post.add(blogPost);
                        SlidePaperAdapter paperAdapter = new SlidePaperAdapter(getApplicationContext(), image_post);
                        backdrop.setAdapter(paperAdapter);
                        backdrop.setCurrentItem(getIntent().getIntExtra("position", 0));
                    }
                }
                backdrop.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                    @Override
                    public void onPageScrolled(int i, float v, int i1) {
                        checkCurrent();
                    }

                    @Override
                    public void onPageSelected(int i) {

                    }

                    @Override
                    public void onPageScrollStateChanged(int i) {

                    }
                });
            }
        });
    }

    private void checkCurrent() {
        firebaseFirestore.collection("Users").document(image_post.get(backdrop.getCurrentItem()).getUser_id()).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                final String userName = task.getResult().getString("name");
                final String userImage = task.getResult().getString("image");
                Glide.with(PostImageActivity.this).load(userImage).into(imageProfile);
                blogUserName.setText(userName);
            }
        });
        String creationDate = dateFormat.format(image_post.get(backdrop.getCurrentItem()).timestamp.getTime());
        blogDate.setText(creationDate);
        if (image_post.get(backdrop.getCurrentItem()).getUser_id().equals(mCurrentId)) {
            ll.setVisibility(View.GONE);
        } else {
            ll.setVisibility(View.VISIBLE);
            btnAddImage.setOnClickListener(this);
            btnSend.setOnClickListener(this);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void initFireBase() {
        firebaseFirestore = FirebaseFirestore.getInstance();
        mRootRef = FirebaseDatabase.getInstance().getReference();
        mImageStorage = FirebaseStorage.getInstance().getReference();
        mCurrentId = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        mUserRef = FirebaseDatabase.getInstance().getReference().child("TimeOnline").child(mCurrentId);
        mDisCovDatabase = FirebaseDatabase.getInstance().getReference().child("Chats");
    }

    private void chatSeen() {
        mRootRef.child("Chats").child(mCurrentId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (!dataSnapshot.hasChild(image_post.get(backdrop.getCurrentItem()).getUser_id())) {
                    Map<String, Object> chatAddMap = new HashMap<>();
                    chatAddMap.put("seen", false);
                    chatAddMap.put("timestamp", ServerValue.TIMESTAMP);
                    Map<String, Object> chatUserMap = new HashMap<>();
                    chatUserMap.put("Chats/" + mCurrentId + "/" + image_post.get(backdrop.getCurrentItem()).getUser_id(), chatAddMap);
                    chatUserMap.put("Chats/" + image_post.get(backdrop.getCurrentItem()).getUser_id() + "/" + mCurrentId, chatAddMap);
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
            String current_user_ref = "Messages/" + mCurrentId + "/" + image_post.get(backdrop.getCurrentItem()).getUser_id();
            String chat_user_ref = "Messages/" + image_post.get(backdrop.getCurrentItem()).getUser_id() + "/" + mCurrentId;
            DatabaseReference user_message_push = mRootRef.child("Messages").child(mCurrentId).child(image_post.get(backdrop.getCurrentItem()).getUser_id()).push();
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
                mDisCovDatabase.child(image_post.get(backdrop.getCurrentItem()).getUser_id()).child(mCurrentId).child("seen").setValue(false);
                Toast.makeText(PostImageActivity.this, "Gửi thành công", Toast.LENGTH_SHORT).show();
            });
        }
    }

    private void initView() {
        backdrop = findViewById(R.id.backdrop);
        imageProfile = findViewById(R.id.imageprofile);
        blogUserName = findViewById(R.id.blog_user_name);
        blogDate = findViewById(R.id.blog_date);
        btnAddImage = findViewById(R.id.btnAddImage);
        edSend = findViewById(R.id.edSend);
        btnSend = findViewById(R.id.btnSend);
        ll = findViewById(R.id.ll);
        imgBack = findViewById(R.id.imgBack);
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
        final String current_user_ref = "Messages/" + mCurrentId + "/" + image_post.get(backdrop.getCurrentItem()).getUser_id();
        final String chat_user_ref = "Messages/" + image_post.get(backdrop.getCurrentItem()).getUser_id() + "/" + mCurrentId;
        DatabaseReference user_message_push = mRootRef.child("Messages").child(mCurrentId).child(image_post.get(backdrop.getCurrentItem()).getUser_id()).push();
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
                    mDisCovDatabase.child(image_post.get(backdrop.getCurrentItem()).getUser_id()).child(mCurrentId).child("seen").setValue(false);
                    Toast.makeText(PostImageActivity.this, "Gửi thành công", Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnAddImage:
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "SELECT IMAGE"), GALLERY_PICK);
                break;
            case R.id.btnSend:
                sendMessage();
                edSend.setText("");
                chatSeen();
                break;
            case R.id.imgBack:
                finish();
                break;
        }
    }
}
