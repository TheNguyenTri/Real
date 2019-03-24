package android.trithe.real;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.trithe.real.adapter.CommentsRecyclerAdapter;
import android.trithe.real.adapter.LikeAdapter;
import android.trithe.real.model.Comments;
import android.trithe.real.model.Likes;
import android.trithe.real.model.Users;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class LikeActivity extends AppCompatActivity {
    private TextView titlelike;
    private RecyclerView likerecylever;
    private String blog_post_id;
    private List<Likes> likesList = new ArrayList<>();
    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;
    private LikeAdapter likeAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_like);
        initView();
        blog_post_id = getIntent().getStringExtra("blog_post_id");
        firebaseFirestore.collection("Posts/" + blog_post_id + "/Likes").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                if (firebaseAuth.getCurrentUser() != null) {
                    if (!documentSnapshots.isEmpty()) {
                        int count = documentSnapshots.size();
                        if(count == 1){
                            titlelike.setText("Like");
                        }else {
                            titlelike.setText(count + " Likes");
                        }
//
                        for (DocumentChange doc : documentSnapshots.getDocumentChanges()) {
                            if (doc.getType() == DocumentChange.Type.ADDED) {
                                String id = doc.getDocument().getId();
                                Likes likes = doc.getDocument().toObject(Likes.class).withId(id);
                                likesList.add(likes);
                                likeAdapter.notifyDataSetChanged();
                                likerecylever.setHasFixedSize(true);
                                likerecylever.setLayoutManager(new LinearLayoutManager(LikeActivity.this));
                                likerecylever.setAdapter(likeAdapter);
                            }
                        }
                    }
                }
            }
        });
//        firebaseFirestore.collection("Posts/" + blog_post_id + "/Likes")
//                .addSnapshotListener(LikeActivity.this, new EventListener<QuerySnapshot>() {
//                    @Override
//                    public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
//                        if (!documentSnapshots.isEmpty()) {
//                                }
//                    }
//                });
    }

    private void initView() {
        titlelike = (TextView) findViewById(R.id.titlelike);
        likerecylever = (RecyclerView) findViewById(R.id.like_list);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        likeAdapter = new LikeAdapter(likesList);
    }


    public void backlike(View view) {
        finish();
    }
}
