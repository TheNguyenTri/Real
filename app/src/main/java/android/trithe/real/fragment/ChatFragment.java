package android.trithe.real.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.trithe.real.R;
import android.trithe.real.adapter.ChatAdapter;
import android.trithe.real.adapter.UsersAdapter;
import android.trithe.real.inter.OnClick;
import android.trithe.real.inter.OnClick1;
import android.trithe.real.model.Conv;
import android.trithe.real.model.Users;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;


public class ChatFragment extends Fragment {
    private ConstraintLayout constraintLayout;
    private List<Conv> list = new ArrayList<>();
    private List<Users> listUser = new ArrayList<>();
    private ChatAdapter adapter;
    private RecyclerView recyclerView;
    private FirebaseAuth mAuth;
    private DatabaseReference mCovDatabase;
    private String mCurrent_user_id;
    private SwipeRefreshLayout swipeRefresh;
    private int mCurrentPage = 1;
    private RecyclerView recyclerViewUser;
    private UsersAdapter useradapter;
    private FirebaseFirestore firebaseFirestore;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.chat_fragment, container, false);
        initFirebase();
        initView(view);
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mCurrentPage++;
                xulyAdapter();
            }
        });
        getUser();
        xulyAdapter();
        return view;
    }

    private void initFirebase() {
        mAuth = FirebaseAuth.getInstance();
        mCurrent_user_id = mAuth.getCurrentUser().getUid();
        firebaseFirestore = FirebaseFirestore.getInstance();
        mCovDatabase = FirebaseDatabase.getInstance().getReference().child("Chats").child(mCurrent_user_id);
        mCovDatabase.keepSynced(true);
    }


    private void getUser() {
        listUser.clear();
        useradapter = new UsersAdapter(getContext(), listUser);
        firebaseFirestore.collection("Users").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot queryDocumentSnapshots, FirebaseFirestoreException e) {
                FirebaseUser currentUser = mAuth.getCurrentUser();
                if (currentUser != null) {
                    for (DocumentChange doc : queryDocumentSnapshots.getDocumentChanges()) {
                        if (doc.getType() == DocumentChange.Type.ADDED) {
                            String id = doc.getDocument().getId();
                            Users users = doc.getDocument().toObject(Users.class).withId(id);
                            listUser.add(users);
                            useradapter.notifyDataSetChanged();
                            recyclerViewUser.setHasFixedSize(true);
                            recyclerViewUser.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
                            recyclerViewUser.setAdapter(useradapter);
                        }
                    }
                }
            }
        });
    }

    private void initView(View view) {
        recyclerViewUser = view.findViewById(R.id.recycler_view);
        recyclerView = view.findViewById(R.id.recycler_viewass);
        constraintLayout = view.findViewById(R.id.ll);
        swipeRefresh = view.findViewById(R.id.swipe_refresh);
    }

    private void xulyAdapter() {
        list.clear();
        Query converstationQuery = mCovDatabase.orderByChild("timestamp");
        converstationQuery.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if (dataSnapshot.exists()) {
                    constraintLayout.setVisibility(View.GONE);
                    String id = dataSnapshot.getKey();
                    Conv messages = dataSnapshot.getValue(Conv.class).withId(id);
                    list.add(messages);
                    adapter = new ChatAdapter(getContext(), list, new OnClick() {
                        @Override
                        public void onItemClickClicked(int position) {
                            getActivity().finish();
                        }
                    }, new OnClick1() {
                        @Override
                        public void onItemClickClicked(int position) {
                            mCurrentPage++;
                            list.clear();
                            xulyAdapter();
                        }
                    });
                    recyclerView.setAdapter(adapter);
                    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
                    linearLayoutManager.setReverseLayout(true);
                    linearLayoutManager.setStackFromEnd(true);
                    recyclerView.setHasFixedSize(true);
                    recyclerView.setLayoutManager(linearLayoutManager);
                    swipeRefresh.setRefreshing(false);
                } else {
                    constraintLayout.setVisibility(View.VISIBLE);
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
}
