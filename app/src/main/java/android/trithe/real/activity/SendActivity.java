package android.trithe.real.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class SendActivity extends AppCompatActivity {
    private EditText edSend;
    private ImageView btnSend;
    private String id, name;
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
    private static final int TOTAL_ITEMS_TO_LOAD = 10;
    private MessagesAdapter adapter;
    private SwipeRefreshLayout swipeRefresh;


    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send);
        initView();
        id = getIntent().getStringExtra("user_id");
        name = getIntent().getStringExtra("user_name");
        initFirebase();
        getInfo();
        getTimeUser();
        loadMessages();
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mCurrentPage++;
                list.clear();
                loadMessages();
            }
        });
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCovDatabase.child(id).child("seen").setValue(true);
                sendMessage();
                edSend.setText("");
                adapter = new MessagesAdapter(getApplicationContext(), list);
                adapter.changeDataset(list);
                chatseen();
            }
        });
        btnAddImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "SELECT IMAGE"), GALLERY_PICK);
            }
        });
    }

    private void initFirebase() {
        firebaseFirestore = FirebaseFirestore.getInstance();
        mRootRef = FirebaseDatabase.getInstance().getReference();
        mImageStorage = FirebaseStorage.getInstance().getReference();
        mCurrentId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        mUserRef = FirebaseDatabase.getInstance().getReference().child("TimeOnline").child(mCurrentId);
        mCovDatabase = FirebaseDatabase.getInstance().getReference().child("Chats").child(mCurrentId);
        mDisCovDatabase = FirebaseDatabase.getInstance().getReference().child("Chats").child(id);

    }

    private void getInfo() {
        nameUserChat.setText(name);
        Glide.with(SendActivity.this).load(getIntent().getStringExtra("user_image")).into(imageUserChat);
    }


    private void chatseen() {
        mRootRef.child("Chats").child(mCurrentId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (!dataSnapshot.hasChild(id)) {
                    Map chatAddMap = new HashMap();
                    chatAddMap.put("seen", false);
                    chatAddMap.put("timestamp", ServerValue.TIMESTAMP);

                    Map chatUserMap = new HashMap();
                    chatUserMap.put("Chats/" + mCurrentId + "/" + id, chatAddMap);
                    chatUserMap.put("Chats/" + id + "/" + mCurrentId, chatAddMap);

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

    private void loadMessages() {
        DatabaseReference messageRef = mRootRef.child("Messages").child(mCurrentId).child(id);

        Query messageQuery = messageRef.limitToLast(mCurrentPage * TOTAL_ITEMS_TO_LOAD);

        messageQuery.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                String id = dataSnapshot.getKey();
                Messages messages = dataSnapshot.getValue(Messages.class).withId(id);
                list.add(messages);
                adapter = new MessagesAdapter(getApplicationContext(), list);
                recyclerViewChat.setAdapter(adapter);
                recyclerViewChat.setHasFixedSize(true);
                recyclerViewChat.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                swipeRefresh.setRefreshing(false);
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
                    Toast.makeText(SendActivity.this, "Gửi thành công", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void getTimeUser() {
        mRootRef.child("TimeOnline").child(id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String online = dataSnapshot.child("online").getValue().toString();
                    if (online.equals("true")) {
                        timeOnlineChat.setText("Online");
                    } else {
                        GetTimeAgo getTimeAgo = new GetTimeAgo();
                        long lasttime = Long.parseLong(online);
                        String lastSeentime = getTimeAgo.getTimeAgo(lasttime, getApplicationContext());
                        timeOnlineChat.setText(lastSeentime);
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

    private void initView() {
        edSend = findViewById(R.id.edSend);
        btnSend = findViewById(R.id.btnSend);
        nameUserChat =  findViewById(R.id.name_user_chat);
        timeOnlineChat =  findViewById(R.id.time_online_chat);
        imageUserChat =  findViewById(R.id.image_user_chat);
        recyclerViewChat =  findViewById(R.id.recycler_view_chat);
        btnAddImage =findViewById(R.id.btnAddImage);
        swipeRefresh =  findViewById(R.id.swipe_refresh);
    }

    public void out(View view) {
        onBackPressed();
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
            Uri imageUri = data.getData();
            final String current_user_ref = "Messages/" + mCurrentId + "/" + id;
            final String chat_user_ref = "Messages/" + id + "/" + mCurrentId;

            DatabaseReference user_message_push = mRootRef.child("Messages").child(mCurrentId).child(id).push();
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
                                Toast.makeText(SendActivity.this, "Gửi thành công", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
            });
        }
    }
}