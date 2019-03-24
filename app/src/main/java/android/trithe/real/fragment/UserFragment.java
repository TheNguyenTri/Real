package android.trithe.real.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.trithe.real.adapter.UsersAdapter;
import android.trithe.real.model.Users;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.trithe.real.R;

import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class UserFragment extends Fragment {
    private RecyclerView reycler;
    private List<Users> list = new ArrayList<>();
    private UsersAdapter adapter;
    private FirebaseFirestore firebaseFirestore;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.user_fragment, container, false);
        reycler = (RecyclerView) view.findViewById(R.id.reycler);
        firebaseFirestore=FirebaseFirestore.getInstance();
        adapter = new UsersAdapter(getContext(), list);
        list.clear();
        FirebaseFirestore();
        return view;
    }
    private void FirebaseFirestore(){
        firebaseFirestore.collection("Users").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot queryDocumentSnapshots, FirebaseFirestoreException e) {
                for(DocumentChange doc: queryDocumentSnapshots.getDocumentChanges()){
                    if(doc.getType() == DocumentChange.Type.ADDED){
                        String id =doc.getDocument().getId();
                        Users users=doc.getDocument().toObject(Users.class).withId(id);
                        list.add(users);
                        adapter.notifyDataSetChanged();
                        reycler.setHasFixedSize(true);
                        reycler.setLayoutManager(new LinearLayoutManager(getContext()));
                        reycler.setAdapter(adapter);
                    }
                }
            }
        });
    }

}
