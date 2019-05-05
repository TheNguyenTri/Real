package android.trithe.real.activity;

import android.content.Intent;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.trithe.real.R;
import android.trithe.real.adapter.BlogAdapter;
import android.trithe.real.model.BlogPost;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class InfoUserActivity extends AppCompatActivity {
    private RecyclerView listInfo;
    private CircleImageView imgInfo;
    private TextView infoName;
    private TextView infoStatus;
    private Button btnSend, btnMessage;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth mAuth;
    private FirebaseUser mCurrent_user;
    private DatabaseReference mRootRef;
    private String user_id, userImage, username;
    private List<BlogPost> info_list = new ArrayList<>();
    private BlogAdapter infoPostUserAdapter;
    private LinearLayout ll;


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_user);
        initFirebase();
        initView();
        getInfo();
        getPosts();
        getFollow();
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                xulysend();
            }
        });
        btnMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(InfoUserActivity.this, SendActivity.class);
                intent.putExtra("user_id", user_id);
                intent.putExtra("user_name", username);
                intent.putExtra("user_image", userImage);
                startActivity(intent);
            }
        });


    }

    private void initFirebase() {
        firebaseFirestore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        mCurrent_user = FirebaseAuth.getInstance().getCurrentUser();
        mRootRef = FirebaseDatabase.getInstance().getReference();
        user_id = getIntent().getStringExtra("user_id");
    }

    private void xulysend() {
        final String currentDate = DateFormat.getDateTimeInstance().format(new Date());
        Map<String, Object> followMap = new HashMap<>();
        followMap.put("Follows/" + user_id + "/" + mCurrent_user.getUid() + "/date", currentDate);
        mRootRef.updateChildren(followMap, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                if (databaseError == null) {
                    if (databaseReference != null) {
                        btnSend.setText(R.string.unfollow);
                    }
                } else {
                    Toast.makeText(getApplicationContext(), databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void getPosts() {
        info_list.clear();
        Query firstQuery = firebaseFirestore.collection("Posts").whereEqualTo("user_id", user_id)
                .orderBy("timestamp", Query.Direction.DESCENDING);
        firstQuery.addSnapshotListener(this, new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot queryDocumentSnapshots, FirebaseFirestoreException e) {
                if (queryDocumentSnapshots != null) {
                    for (DocumentChange doc : queryDocumentSnapshots.getDocumentChanges()) {
                        if (doc.getType() == DocumentChange.Type.ADDED) {
                            String infoPostUserId = doc.getDocument().getId();
                            BlogPost blogPost = doc.getDocument().toObject(BlogPost.class).withId(infoPostUserId);
                            info_list.add(blogPost);
                            infoPostUserAdapter.notifyDataSetChanged();
                        }
                    }

                }
            }
        });
        RecyclerView.LayoutManager manager = new LinearLayoutManager(this);
        listInfo.setLayoutManager(manager);
        listInfo.setAdapter(infoPostUserAdapter);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void getInfo() {
        firebaseFirestore.collection("Users").document(user_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    username = task.getResult().getString("name");
                    userImage = task.getResult().getString("image");
                    String userStatus = task.getResult().getString("status");
                    infoName.setText(username);
                    infoStatus.setText(userStatus);
                    Glide.with(getApplicationContext()).load(userImage).into(imgInfo);
                }
            }
        });
        if (user_id.equals(Objects.requireNonNull(mAuth.getCurrentUser()).getUid())) {
            ll.setVisibility(View.GONE);
        }
    }

    private void getFollow() {
        mRootRef.child("Follows/" + user_id + "/" + mCurrent_user.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    btnSend.setText(R.string.unfollow);
                    btnSend.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Map<String, Object> unfollowMap = new HashMap<>();
                            unfollowMap.put("Follows/" + user_id + "/" + mCurrent_user.getUid(), null);
                            mRootRef.updateChildren(unfollowMap, new DatabaseReference.CompletionListener() {
                                @Override
                                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                    if (databaseError == null) {
                                        btnSend.setText(R.string.follow);
                                        btnSend.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                final String currentDate = DateFormat.getDateTimeInstance().format(new Date());
                                                Map<String, Object> followMap = new HashMap<>();
                                                followMap.put("Follows/" + user_id + "/" + mCurrent_user.getUid() + "/date", currentDate);
                                                mRootRef.updateChildren(followMap, new DatabaseReference.CompletionListener() {
                                                    @Override
                                                    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                                        if (databaseError == null) {
                                                            btnSend.setText(R.string.unfollow);
                                                        } else {
                                                            Toast.makeText(getApplicationContext(), databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                                                        }
                                                    }
                                                });
                                            }
                                        });
                                    } else {
                                        Toast.makeText(getApplicationContext(), databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                    btnSend.setEnabled(true);
                                }
                            });
                        }
                    });
                } else {
                    btnSend.setText(R.string.follow);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void initView() {
        ll = findViewById(R.id.ll);
        imgInfo = findViewById(R.id.imgInfo);
        infoName = findViewById(R.id.infoName);
        infoStatus = findViewById(R.id.infoStatus);
        btnSend = findViewById(R.id.btnSend);
        listInfo = findViewById(R.id.listInfo);
        btnMessage = findViewById(R.id.btnMessage);
        infoPostUserAdapter = new BlogAdapter(info_list, this);
    }

}
