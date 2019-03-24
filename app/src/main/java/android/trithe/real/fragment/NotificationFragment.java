package android.trithe.real.fragment;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.trithe.real.R;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class NotificationFragment extends Fragment {
    private RecyclerView recyclerView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.notification_fragment, container, false);
        String dataMessage =getActivity().getIntent().getStringExtra("dataMessage");
        String dataFrom =getActivity().getIntent().getStringExtra("dataFrom");
        recyclerView = (RecyclerView) view.findViewById(R.id.notification_id);

        return view;
    }
}
