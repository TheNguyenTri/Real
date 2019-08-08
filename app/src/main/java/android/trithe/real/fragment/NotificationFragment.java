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
import android.trithe.real.adapter.NotificationAdapter;
import android.trithe.real.model.NotificationsModel;
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
import java.util.Objects;

public class NotificationFragment extends Fragment {
    private RecyclerView recyclerViewass;
    private DatabaseReference mRootRef;
    private String mCurrent_id;
    private List<NotificationsModel> list = new ArrayList<>();
    private NotificationAdapter adapter;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_notification, container, false);
        setHasOptionsMenu(true);
        initView(view);
        getNotification();
        return view;
    }

    private void getNotification() {
        list.clear();
        Query converStationQuery = mRootRef.child("Nofications").child(mCurrent_id).orderByChild("timestamp");
        converStationQuery.addChildEventListener(new ChildEventListener() {
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

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void initView(View view){
        mRootRef = FirebaseDatabase.getInstance().getReference();
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        mCurrent_id = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
        recyclerViewass = view.findViewById(R.id.recycler_viewass);
    }
}
