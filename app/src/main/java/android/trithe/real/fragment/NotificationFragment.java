package android.trithe.real.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.trithe.real.R;
import android.trithe.real.adapter.NotificationAdapter;
import android.trithe.real.model.NotificationsModel;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.ArrayList;
import java.util.List;

public class NotificationFragment extends Fragment {
    private RecyclerView recyclerViewass;
    private DatabaseReference mRootRef;
    private FirebaseAuth mAuth;
    private String mCurrent_id;
    private List<NotificationsModel> list = new ArrayList<>();
    private NotificationAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.notification_fragment, container, false);
        setHasOptionsMenu(true);
        initView(view);
        getNotifi();
        return view;
    }

    private void getNotifi() {
        list.clear();
        Query converstationQuery = mRootRef.child("Nofications").child(mCurrent_id).orderByChild("timestamp");
        converstationQuery.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if (dataSnapshot.exists()) {
                    NotificationsModel messages = dataSnapshot.getValue(NotificationsModel.class);
                    list.add(messages);
                    adapter = new NotificationAdapter(getActivity(), list);
                    recyclerViewass.setAdapter(adapter);
                    recyclerViewass.setHasFixedSize(true);
                    recyclerViewass.setLayoutManager(new LinearLayoutManager(getActivity()));
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


    private void initView(View view){
        mRootRef = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        mCurrent_id = mAuth.getCurrentUser().getUid();
        recyclerViewass = view.findViewById(R.id.recycler_viewass);
    }
}
