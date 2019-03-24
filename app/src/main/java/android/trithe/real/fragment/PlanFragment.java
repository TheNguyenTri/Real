package android.trithe.real.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.trithe.real.R;
import android.trithe.real.adapter.TodosAdapter;
import android.trithe.real.model.Todos;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;


public class PlanFragment extends Fragment {
    private ConstraintLayout constraintLayout;
    private List<Todos> listplanss = new ArrayList<>();
    private CoordinatorLayout coordinatorLayout;
    private TodosAdapter adapter;
    private RecyclerView recyclerView;
    private FirebaseAuth mAuth;
    private DatabaseReference mTodoDatabase;
    private String mCurrent_user_id;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.plan_fragment, container, false);
        coordinatorLayout = view.findViewById(R.id.coor);
        recyclerView = view.findViewById(R.id.recycler_viewass);
        constraintLayout = view.findViewById(R.id.ll);
        mAuth = FirebaseAuth.getInstance();
        mCurrent_user_id = mAuth.getCurrentUser().getUid();
        adapter = new TodosAdapter(getActivity(), listplanss);
        mTodoDatabase = FirebaseDatabase.getInstance().getReference().child("Todos").child(mCurrent_user_id);
        listplanss.clear();
        mTodoDatabase.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if (dataSnapshot.exists()) {
                    constraintLayout.setVisibility(View.GONE);
                    Todos todos = dataSnapshot.getValue(Todos.class);
                    listplanss.add(todos);
                    adapter.changeDataset(listplanss);
                    recyclerView.setAdapter(adapter);
                    recyclerView.setHasFixedSize(true);
                    recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
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
        return view;
    }
}
