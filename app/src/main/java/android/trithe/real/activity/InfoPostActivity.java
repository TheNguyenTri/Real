package android.trithe.real.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.trithe.real.R;
import android.trithe.real.adapter.CommentsAdapter;
import android.trithe.real.model.Comments;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class InfoPostActivity extends AppCompatActivity {
    private ImageView blogImage;
    private CircleImageView imageprofile;
    private TextView blogUserName;
    private TextView blogDate;
    private RecyclerView recyclerViewComment;
    private ImageView deleteicon;
    private ImageView blogLike;
    private TextView blogDesc;
    private TextView blogTextLike;
    private TextView blogCountComment;
    private EditText edSend;
    private ImageView btnSend;
    private String blog_id, user_id, current_user_id;
    private CommentsAdapter commentsAdapter;
    private List<Comments> commentsList;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference mRootRef;
    private CircleImageView imageusersend;
    private ConstraintLayout lls;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_post);
        initFirebase();
        initView();
        setDataPost();
        getDataComment();
        getCountComment();
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendComment();
            }
        });
        getImageLike();
        getTextLike();
        blogLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getCountLike();
            }
        });
        getImagemCurrent();
    }

    private void getImagemCurrent() {
        firebaseFirestore.collection("Users").document(current_user_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    String userImage = task.getResult().getString("image");
                    Glide.with(InfoPostActivity.this).load(userImage).into(imageusersend);

                }
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void initFirebase() {
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        mRootRef = FirebaseDatabase.getInstance().getReference();
        current_user_id = Objects.requireNonNull(firebaseAuth.getCurrentUser()).getUid();
    }


    private void pushNotification(String id) {
        String current_user_ref = "Nofications/" + id;
        DatabaseReference user_notifi_push = mRootRef.child("Nofications").child(id).push();
        String push_id = user_notifi_push.getKey();

        Map<String, Object> notiMap = new HashMap<>();
        notiMap.put("body", " đã bình luận bài viết của bạn");
        notiMap.put("blog_id", blog_id);
        notiMap.put("timestamp", ServerValue.TIMESTAMP);
        notiMap.put("from", current_user_id);
        Map<String, Object> messageUserMap = new HashMap<>();
        messageUserMap.put(current_user_ref + "/" + push_id, notiMap);
        mRootRef.updateChildren(messageUserMap, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
            }
        });
    }

    private void setDataPost() {
        //fake lấy blog_id
        blog_id = getIntent().getStringExtra("user_id");
        //
        firebaseFirestore.collection("Posts").document(blog_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    Glide.with(InfoPostActivity.this).load(task.getResult().getString("image_url")).into(blogImage);
                    DateFormat dateFormat = SimpleDateFormat.getDateInstance(DateFormat.MEDIUM, Locale.US);
                    String creationDate = dateFormat.format(task.getResult().getDate("timestamp").getTime());
                    blogDate.setText(creationDate);
                    blogDesc.setText(task.getResult().getString("desc"));

                    user_id = task.getResult().getString("user_id");
                    deleteMyPosts();
                    firebaseFirestore.collection("Users").document(user_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                String username = task.getResult().getString("name");
                                String userImage = task.getResult().getString("image");
                                blogUserName.setText(username);
                                Glide.with(InfoPostActivity.this).load(userImage).into(imageprofile);

                            }
                        }
                    });
                }
            }
        });

    }

    ////LIKE
    private void getTextLike() {
        firebaseFirestore.collection("Posts/" + blog_id + "/Likes").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot queryDocumentSnapshots, FirebaseFirestoreException e) {
                if (queryDocumentSnapshots != null) {
                    int count = queryDocumentSnapshots.size();
                    if (count == 1) {
                        blogTextLike.setText(count + " Likes");
                        for (DocumentChange doc : queryDocumentSnapshots.getDocumentChanges()) {

                            if (doc.getType() == DocumentChange.Type.ADDED) {

                                String likeId = doc.getDocument().getId();
                                firebaseFirestore.collection("Users").document(likeId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                        if (task.isSuccessful()) {
                                            String username = task.getResult().getString("name");
                                            blogTextLike.setText(username);
                                        }
                                    }
                                });
                            }
                        }
                        logtoInfoLike(blog_id);
                    } else {
                        blogTextLike.setText(count + " Likes");
                    }
                }
            }
        });
    }


    private void logtoInfoLike(final String blogId) {
        blogTextLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent likeIntent = new Intent(InfoPostActivity.this, InfoPostActivity.class);
                likeIntent.putExtra("blog_post_id", blogId);
                startActivity(likeIntent);
            }
        });
    }

    private void getImageLike() {
        firebaseFirestore.collection("Posts/" + blog_id + "/Likes").document(current_user_id).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(DocumentSnapshot documentSnapshot, FirebaseFirestoreException e) {
                if (documentSnapshot != null) {
                    try {
                        if (documentSnapshot.exists()) {
                            Glide.with(InfoPostActivity.this).load(R.drawable.love).into(blogLike);
                        } else {
                            Glide.with(InfoPostActivity.this).load(R.drawable.chua_like).into(blogLike);
                        }
                    } catch (Exception ignored) {
                    }
                }
            }
        });
    }

    private void getCountLike() {
        firebaseFirestore.collection("Posts/" + blog_id + "/Likes").document(current_user_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (!task.getResult().exists()) {
                    Map<String, Object> likesMap = new HashMap<>();
                    likesMap.put("timestamp", FieldValue.serverTimestamp());
                    firebaseFirestore.collection("Posts/" + blog_id + "/Likes").document(current_user_id).set(likesMap);
                    if (!current_user_id.equals(user_id)) {
                        String current_user_ref = "Nofications/" + user_id;
                        DatabaseReference user_notifi_push = mRootRef.child("Nofications").child(user_id).push();
                        String push_id = user_notifi_push.getKey();
                        Map<String, Object> notiMap = new HashMap<>();
                        notiMap.put("body", "đã thích ảnh của bạn");
                        notiMap.put("blog_id", blog_id);
                        notiMap.put("timestamp", ServerValue.TIMESTAMP);
                        notiMap.put("from", current_user_id);
                        Map<String, Object> messageUserMap = new HashMap<>();
                        messageUserMap.put(current_user_ref + "/" + push_id, notiMap);
                        mRootRef.updateChildren(messageUserMap, new DatabaseReference.CompletionListener() {
                            @Override
                            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                            }
                        });
                    }
                } else {
                    firebaseFirestore.collection("Posts/" + blog_id + "/Likes").document(current_user_id).delete();
                    getTextLike();
                    getImageLike();
                }
            }
        });
    }


    private void getCountComment() {
        firebaseFirestore.collection("Posts/" + blog_id + "/Comments").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                if (firebaseAuth.getCurrentUser() != null) {
                    if (documentSnapshots != null) {
                        int count = documentSnapshots.size();
                        if (count == 0) {
                            blogCountComment.setText("Comment");
                        } else {
                            blogCountComment.setText(count + " Comments");
                        }
                    }
                }
            }
        });
    }

    private void deleteMyPosts() {
        if (user_id.equals(current_user_id)) {
            deleteicon.setVisibility(View.VISIBLE);
            deleteicon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final AlertDialog.Builder builder = new AlertDialog.Builder(InfoPostActivity.this);
                    builder.setTitle("Message");
                    builder.setMessage("Do you want delete your post ?");
                    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            firebaseFirestore.collection("Posts").document(blog_id).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(InfoPostActivity.this, "Đã xóa bài viết", Toast.LENGTH_SHORT).show();
                                    finish();
                                }
                            });
                        }
                    });
                    builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
                    builder.show();
                }
            });
        } else {
            deleteicon.setVisibility(View.GONE);
        }
    }

    private void getDataComment() {
        commentsList = new ArrayList<>();
        commentsAdapter = new CommentsAdapter(commentsList);
        recyclerViewComment.setHasFixedSize(true);
        recyclerViewComment.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewComment.setAdapter(commentsAdapter);


        firebaseFirestore.collection("Posts/" + blog_id + "/Comments").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                try {
                    for (DocumentChange doc : documentSnapshots.getDocumentChanges()) {
                        if (doc.getType() == DocumentChange.Type.ADDED) {
                            Comments comments = doc.getDocument().toObject(Comments.class);
                            commentsList.add(comments);
                            commentsAdapter.notifyDataSetChanged();
                        }
                        if (commentsList.size() == 0) {
                            lls.setVisibility(View.VISIBLE);
                        } else {
                            lls.setVisibility(View.GONE);
                        }

                    }
                } catch (Exception e1) {
                    Log.d("abc", e1.toString());
                }

            }
        });


    }

    private void sendComment() {
        String comment_message = edSend.getText().toString();
        Map<String, Object> commentsMap = new HashMap<>();
        commentsMap.put("message", comment_message);
        commentsMap.put("user_id", current_user_id);
        commentsMap.put("timestamp", FieldValue.serverTimestamp());
        firebaseFirestore.collection("Posts/" + blog_id + "/Comments").add(commentsMap).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onComplete(@NonNull Task<DocumentReference> task) {
                if (!task.isSuccessful()) {
                    Toast.makeText(InfoPostActivity.this, "Error Posting Comment : " + Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
                } else {
                    edSend.setText("");
                    lls.setVisibility(View.GONE);
                    if (!current_user_id.equals(user_id)) {
                        pushNotification(user_id);
                    }
                }

            }
        });
    }


    private void initView() {
        recyclerViewComment = findViewById(R.id.recycler_view_comment);
        blogImage = findViewById(R.id.blog_image);
        imageprofile = findViewById(R.id.imageprofile);
        blogUserName = findViewById(R.id.blog_user_name);
        blogDate = findViewById(R.id.blog_date);
        deleteicon = findViewById(R.id.deleteicon);
        blogLike = findViewById(R.id.blog_like);
        blogDesc = findViewById(R.id.blog_desc);
        blogTextLike = findViewById(R.id.blog_text_like);
        blogCountComment = findViewById(R.id.blog_count_comment);
        edSend = findViewById(R.id.edSend);
        btnSend = findViewById(R.id.btnSend);
        imageusersend = findViewById(R.id.imageusersend);
        lls = findViewById(R.id.lls);
    }
}
