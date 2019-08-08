package android.trithe.real.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.trithe.real.R;
import android.trithe.real.adapter.MessagesAdapter;
import android.trithe.real.helper.GetTimeAgo;
import android.trithe.real.model.Messages;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class SendActivity extends AppCompatActivity implements View.OnClickListener {
    private EditText edSend;
    private ImageView btnSend;
    private String id;
    private String mCurrentId;
    private FirebaseFirestore firebaseFirestore;
    private DatabaseReference mRootRef;
    private DatabaseReference mUserRef;
    private DatabaseReference mCovDatabase;
    private DatabaseReference mDisCovDatabase;
    private StorageReference mImageStorage;
    private TextView nameUserChat;
    private TextView timeOnlineChat;
    private CircleImageView imageUserChat;
    private RecyclerView recyclerViewChat;
    private List<Messages> list = new ArrayList<>();
    private ImageView btnAddImage;
    private static final int GALLERY_PICK = 1;
    private int mCurrentPage = 1;
    private static final int TOTAL_ITEMS_TO_LOAD = 8;
    private MessagesAdapter adapter;
    private SwipeRefreshLayout swipeRefresh;
    private ImageView imgBack;
    private boolean lastMessage = true;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send);
        initView();
        initFireBase();
        setUpAdapter();
        getInfo();
        getTimeUser();
        loadMessages();
        swipeRefresh.setOnRefreshListener(() -> {
            mCurrentPage++;
            lastMessage = false;
            list.clear();
            loadMessages();
        });
        listener();
    }

    private void setUpAdapter() {
        adapter = new MessagesAdapter(getApplicationContext(), list);
        recyclerViewChat.setAdapter(adapter);
        recyclerViewChat.setHasFixedSize(true);
        recyclerViewChat.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
    }

    private void listener() {
        btnSend.setOnClickListener(this);
        btnAddImage.setOnClickListener(this);
        imgBack.setOnClickListener(this);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void initFireBase() {
        firebaseFirestore = FirebaseFirestore.getInstance();
        mRootRef = FirebaseDatabase.getInstance().getReference();
        mImageStorage = FirebaseStorage.getInstance().getReference();
        mCurrentId = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        mUserRef = FirebaseDatabase.getInstance().getReference().child("TimeOnline").child(mCurrentId);
        mCovDatabase = FirebaseDatabase.getInstance().getReference().child("Chats").child(mCurrentId);
        mDisCovDatabase = FirebaseDatabase.getInstance().getReference().child("Chats").child(id);
    }

    private void getInfo() {
        firebaseFirestore.collection("Users").document(id).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Glide.with(SendActivity.this).load(task.getResult().getString("image")).into(imageUserChat);
                nameUserChat.setText(task.getResult().getString("name"));
            }
        });
    }

    private void seenMessage() {
        mRootRef.child("Chats").child(mCurrentId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (!dataSnapshot.hasChild(id)) {
                    Map<String, Object> chatAddMap = new HashMap<>();
                    chatAddMap.put("seen", false);
                    chatAddMap.put("timestamp", ServerValue.TIMESTAMP);

                    Map<String, Object> chatUserMap = new HashMap<>();
                    chatUserMap.put("Chats/" + mCurrentId + "/" + id, chatAddMap);
                    chatUserMap.put("Chats/" + id + "/" + mCurrentId, chatAddMap);

                    mRootRef.updateChildren(chatUserMap, (databaseError, databaseReference) -> {
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void loadMessages() {
        DatabaseReference messageRef = mRootRef.child("Messages").child(mCurrentId).child(id);
        Query messageQuery = messageRef.limitToLast(mCurrentPage * TOTAL_ITEMS_TO_LOAD);
        messageQuery.addChildEventListener(new ChildEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                String id = dataSnapshot.getKey();
                Messages messages = Objects.requireNonNull(dataSnapshot.getValue(Messages.class)).withId(id);
                list.add(messages);
                adapter.notifyDataSetChanged();
                swipeRefresh.setRefreshing(false);
                if (lastMessage) {
                    recyclerViewChat.scrollToPosition(list.size() - 1);
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void sendMessage() {
        String message = edSend.getText().toString();
        if (!message.equals("")) {
            String current_user_ref = "Messages/" + mCurrentId + "/" + id;
            String chat_user_ref = "Messages/" + id + "/" + mCurrentId;
            DatabaseReference user_message_push = mRootRef.child("Messages").child(mCurrentId).child(id).push();
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
                Toast.makeText(SendActivity.this, "Gửi thành công", Toast.LENGTH_SHORT).show();
            });
        }
    }

    private void getTimeUser() {
        mRootRef.child("TimeOnline").child(id).addValueEventListener(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String online = Objects.requireNonNull(dataSnapshot.child("online").getValue()).toString();
                    if (online.equals("true")) {
                        timeOnlineChat.setText(R.string.online);
                    } else {
                        long lastTime = Long.parseLong(online);
                        String lastSeenTime = GetTimeAgo.getTimeAgo(lastTime, getApplicationContext());
                        timeOnlineChat.setText(lastSeenTime);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        checkOnline();
    }

    @Override
    protected void onStop() {
        super.onStop();
        checkOnline();
    }

    private void checkOnline() {
        if (mCurrentId != null) {
            firebaseFirestore.collection("Users").document(mCurrentId).update("online", true);
            mUserRef.child("online").setValue(true);
        }
    }

    private void initView() {
        edSend = findViewById(R.id.edSend);
        btnSend = findViewById(R.id.btnSend);
        nameUserChat = findViewById(R.id.name_user_chat);
        timeOnlineChat = findViewById(R.id.time_online_chat);
        imageUserChat = findViewById(R.id.image_user_chat);
        recyclerViewChat = findViewById(R.id.recycler_view_chat);
        btnAddImage = findViewById(R.id.btnAddImage);
        swipeRefresh = findViewById(R.id.swipe_refresh);
        imgBack = findViewById(R.id.imgBack);

        id = getIntent().getStringExtra("user_id");
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(SendActivity.this, MainActivity.class));
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GALLERY_PICK && resultCode == RESULT_OK) {
            setUploadImage(data);
        }
    }

    private void setUploadImage(Intent data) {
        Uri imageUri = Objects.requireNonNull(data).getData();
        final String current_user_ref = "Messages/" + mCurrentId + "/" + id;
        final String chat_user_ref = "Messages/" + id + "/" + mCurrentId;
        DatabaseReference user_message_push = mRootRef.child("Messages").child(mCurrentId).child(id).push();
        final String push_id = user_message_push.getKey();
        StorageReference filepath = mImageStorage.child("message_images").child(push_id + ".jpg");
        filepath.putFile(Objects.requireNonNull(imageUri)).addOnCompleteListener(task -> {
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
                    Toast.makeText(SendActivity.this, "Gửi thành công", Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnSend:
                mCovDatabase.child(id).child("seen").setValue(true);
                sendMessage();
                edSend.setText("");
                adapter = new MessagesAdapter(getApplicationContext(), list);
                adapter.changeDataset(list);
                seenMessage();
                break;
            case R.id.btnAddImage:
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "SELECT IMAGE"), GALLERY_PICK);
                break;
            case R.id.imgBack:
                onBackPressed();
                break;
        }
    }
}