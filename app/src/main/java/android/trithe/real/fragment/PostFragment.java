package android.trithe.real.fragment;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.trithe.real.R;
import android.trithe.real.adapter.BlogAdapter;
import android.trithe.real.adapter.ImageAdapter;
import android.trithe.real.model.BlogPost;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class PostFragment extends Fragment {
    DatabaseReference mData;
    private RecyclerView recyclerViewshop;
    private RecyclerView recyclerView;
    private List<BlogPost> blog_list = new ArrayList<>();
    private List<BlogPost> image_post = new ArrayList<>();
    private FirebaseFirestore firebaseFirestore;
    private BlogAdapter blogAdapter;
    private ImageAdapter imageAdapter;


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_post, container, false);
        initView(view);
        getImagePost();
        getDataPost();
        return view;
    }

    private void initView(View view) {
        firebaseFirestore = FirebaseFirestore.getInstance();
        mData = FirebaseDatabase.getInstance().getReference();
        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerViewshop = view.findViewById(R.id.shop);
        blogAdapter = new BlogAdapter(blog_list, getActivity());
        imageAdapter = new ImageAdapter(image_post, getActivity());
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void getDataPost() {
        blog_list.clear();
        Query firstQuery = firebaseFirestore.collection("Posts").orderBy("timestamp", Query.Direction.DESCENDING);
        firstQuery.addSnapshotListener(Objects.requireNonNull(getActivity()), new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot queryDocumentSnapshots, FirebaseFirestoreException e) {
                if (queryDocumentSnapshots != null) {
                    for (DocumentChange doc : queryDocumentSnapshots.getDocumentChanges()) {
                        if (doc.getType() == DocumentChange.Type.ADDED) {
                            String blogPostId = doc.getDocument().getId();
                            BlogPost blogPost = doc.getDocument().toObject(BlogPost.class).withId(blogPostId);
                            blog_list.add(blogPost);
                            blogAdapter.notifyDataSetChanged();
                        }
                    }
                }
            }

        });
        RecyclerView.LayoutManager manager = new LinearLayoutManager(getActivity());
        recyclerViewshop.setLayoutManager(manager);
        recyclerViewshop.setAdapter(blogAdapter);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void getImagePost() {
        image_post.clear();
        Query firstQuery = firebaseFirestore.collection("Posts").orderBy("timestamp", Query.Direction.DESCENDING);
        firstQuery.addSnapshotListener(Objects.requireNonNull(getActivity()), new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot queryDocumentSnapshots, FirebaseFirestoreException e) {
                if (queryDocumentSnapshots != null) {
                    for (DocumentChange doc : queryDocumentSnapshots.getDocumentChanges()) {
                        if (doc.getType() == DocumentChange.Type.ADDED) {
                            String blogPostId = doc.getDocument().getId();
                            BlogPost blogPost = doc.getDocument().toObject(BlogPost.class).withId(blogPostId);
                            image_post.add(blogPost);
                            imageAdapter.notifyDataSetChanged();
                        }
                    }

                }
            }

        });
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        recyclerView.setAdapter(imageAdapter);
    }
}