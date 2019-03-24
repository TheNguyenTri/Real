package android.trithe.real.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.trithe.real.R;
import android.trithe.real.adapter.BlogAdapter;
import android.trithe.real.adapter.UsersAdapter;
import android.trithe.real.database.PetDAO;
import android.trithe.real.database.TypeDAO;
import android.trithe.real.model.BlogPost;
import android.trithe.real.model.Pet;
import android.trithe.real.model.TypePet;
import android.trithe.real.model.Users;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class PetFragment extends Fragment {
    DatabaseReference mData;
    private FirebaseAuth mAuth;
    private RecyclerView recyclerViewshop;
    private RecyclerView recyclerView;
    private List<BlogPost> blog_list = new ArrayList<>();
    private FirebaseFirestore firebaseFirestore;
    private BlogAdapter blogAdapter;
    private DocumentSnapshot lastVisible;
    private Boolean isFirstPageFirstLoad = true;
    private List<Users> list = new ArrayList<>();
    private UsersAdapter adapter;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.pet_fragment, container, false);
        recyclerView = view.findViewById(R.id.recycler_view);
        mAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        recyclerViewshop = view.findViewById(R.id.shop);
        mData = FirebaseDatabase.getInstance().getReference();


        blogAdapter = new BlogAdapter(blog_list, getActivity());


//        recyclerViewshop.addOnScrollListener(new RecyclerView.OnScrollListener() {
//            @Override
//            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
//                super.onScrolled(recyclerView, dx, dy);
//                Boolean reachedBottom = !recyclerView.canScrollVertically(-1);
//
//                if (reachedBottom) {
//                    loadMorePost();
//                }
//            }
//        });


        blog_list.clear();
        Query firstQuery = firebaseFirestore.collection("Posts").orderBy("timestamp", Query.Direction.DESCENDING);
        firstQuery.addSnapshotListener(getActivity(), new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot queryDocumentSnapshots, FirebaseFirestoreException e) {
                if (queryDocumentSnapshots != null) {
                    lastVisible = queryDocumentSnapshots.getDocuments().get(queryDocumentSnapshots.size() - 1);
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
        list.clear();
        adapter = new UsersAdapter(getContext(), list);
        firebaseFirestore.collection("Users").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot queryDocumentSnapshots, FirebaseFirestoreException e) {
                FirebaseUser currentUser = mAuth.getCurrentUser();
                if (currentUser != null) {
                    for (DocumentChange doc : queryDocumentSnapshots.getDocumentChanges()) {
                        if (doc.getType() == DocumentChange.Type.ADDED) {
                            String id = doc.getDocument().getId();
                            Users users = doc.getDocument().toObject(Users.class).withId(id);
                            list.add(users);
                            adapter.notifyDataSetChanged();
                            recyclerView.setHasFixedSize(true);
                            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
                            recyclerView.setAdapter(adapter);
                        }
                    }
                }
            }
        });
        return view;
    }

//    @Override
//    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
//        getActivity().getMenuInflater().inflate(R.menu.menu_main, menu);
//        super.onCreateOptionsMenu(menu, inflater);
////        getActivity().invalidateOptionsMenu();
//        SearchManager searchManager = (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);
//        SearchView searchView = (SearchView) menu.findItem(R.id.action_search)
//                .getActionView();
//        searchView.setSearchableInfo(searchManager
//                .getSearchableInfo(getActivity().getComponentName()));
//        searchView.setMaxWidth(Integer.MAX_VALUE);
//
//        // listening to search query text change
//        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
//            @Override
//            public boolean onQueryTextSubmit(String query) {
//                // filter recycler view when query submitted
//                petAdapter.getFilter().filter(query);
//                return false;
//            }
//
//            @Override
//            public boolean onQueryTextChange(String query) {
//                // filter recycler view when text is changed
//                petAdapter.getFilter().filter(query);
//                return false;
//            }
//        });
//    }


    public void loadMorePost() {
        if (mAuth.getCurrentUser() != null) {
            Query nextQuery = firebaseFirestore.collection("Posts")
                    .orderBy("timestamp", Query.Direction.DESCENDING)
                    .startAfter(lastVisible);
            nextQuery.addSnapshotListener(getActivity(), new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(QuerySnapshot queryDocumentSnapshots, FirebaseFirestoreException e) {
                    if (isFirstPageFirstLoad) {
                        lastVisible = queryDocumentSnapshots.getDocuments().get(queryDocumentSnapshots.size()-1);
                        for (DocumentChange doc : queryDocumentSnapshots.getDocumentChanges()) {
                            if (doc.getType() == DocumentChange.Type.ADDED) {
                                String blogPostId = doc.getDocument().getId();
                                BlogPost blogPost = doc.getDocument().toObject(BlogPost.class).withId(blogPostId);
                                if (isFirstPageFirstLoad) {
                                    blog_list.add(blogPost);
                                    Toast.makeText(getContext(), "Pk", Toast.LENGTH_SHORT).show();
                                } else {
                                    blog_list.add(0, blogPost);
                                }
                                blogAdapter.notifyDataSetChanged();
                            }

                        }
                        isFirstPageFirstLoad = false;
                    }
                }
            });
        }
    }

}