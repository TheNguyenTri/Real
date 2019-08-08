package android.trithe.real.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.trithe.real.R;
import android.trithe.real.adapter.LikeAdapter;
import android.trithe.real.model.Likes;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class LikeActivity extends AppCompatActivity {
    private ImageView imgBack;
    private TextView titleLike;
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
        getLike();
        imgBack.setOnClickListener(v -> finish());
    }

    private void getLike() {
        firebaseFirestore.collection("Posts/" + blog_post_id + "/Likes").addSnapshotListener((documentSnapshots, e) -> {
            if (firebaseAuth.getCurrentUser() != null) {
                if (!documentSnapshots.isEmpty()) {
                    int count = documentSnapshots.size();
                    if (count == 1) {
                        titleLike.setText("Like");
                    } else {
                        titleLike.setText(count + " Likes");
                    }
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
        });
    }

    private void initView() {
        imgBack = findViewById(R.id.imgBack);
        titleLike = findViewById(R.id.titleLike);
        likerecylever = findViewById(R.id.like_list);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        likeAdapter = new LikeAdapter(likesList);
    }
}
